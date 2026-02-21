package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context
import android.graphics.PointF
import java.util.ArrayList
import java.util.Locale
import kotlin.math.hypot
import kotlin.math.sqrt
import kotlin.math.max
import kotlin.math.min
import kotlin.math.ln
import kotlin.math.abs

// =================================================================================
// DATA CLASS: TimedPoint
// SUMMARY: A point with timestamp for dwell time analysis in swipe gestures.
//          Allows the prediction engine to detect when users linger on keys
//          to disambiguate similar words like "for" vs "four".
// =================================================================================
data class TimedPoint(
    val x: Float,
    val y: Float,
    val timestamp: Long  // System.currentTimeMillis() when this point was captured
) {
    fun toPointF(): android.graphics.PointF = android.graphics.PointF(x, y)
}
// =================================================================================
// END BLOCK: TimedPoint data class
// =================================================================================

// =================================================================================
// ENUM: PredictionSource
// SUMMARY: Identifies which algorithm produced a prediction result.
//          PRECISE = Current high-accuracy algorithm (good for slow swipes)
//          SHAPE_CONTEXT = Gboard-style normalized shape + language model (fast swipes)
// =================================================================================
enum class PredictionSource {
    PRECISE,
    SHAPE_CONTEXT
}
// =================================================================================
// END BLOCK: PredictionSource enum
// =================================================================================

// =================================================================================
// DATA CLASS: SwipeResult
// SUMMARY: Packages a prediction word with metadata about how it was generated.
//          - word: The predicted word
//          - source: Which algorithm produced this (PRECISE or SHAPE_CONTEXT)
//          - confidence: Score from 0.0 to 1.0 (higher = more confident)
//          - rawScore: The actual algorithm score (lower = better for our scoring)
// =================================================================================
data class SwipeResult(
    val word: String,
    val source: PredictionSource,
    val confidence: Float = 0.0f,
    val rawScore: Float = Float.MAX_VALUE
)
// =================================================================================
// END BLOCK: SwipeResult data class
// =================================================================================

/**
 * =================================================================================
 * CLASS: PredictionEngine
 * SUMMARY: Handles predictive text suggestions and swipe-to-type gesture decoding.
 *          Implements a SHARK2-inspired dual-channel algorithm with:
 *          1. Uniform path sampling (N points)
 *          2. Template generation for dictionary words
 *          3. Shape channel (scale-normalized pattern matching)
 *          4. Location channel (absolute position matching)
 *          5. Integration scoring with word frequency weighting
 *          
 * Based on: SHARK2 algorithm by Kristensson & Zhai (2004)
 * Reference: http://pokristensson.com/pubs/KristenssonZhaiUIST2004.pdf
 * =================================================================================
 */
class PredictionEngine {

    companion object {
        val instance = PredictionEngine()

        // Tuning Parameters
        private const val SAMPLE_POINTS = 64
        private const val NORMALIZATION_SIZE = 100f
        private const val SEARCH_RADIUS = 70f
        
        // Reference keyboard dimensions for scaling pixel values
        private const val REFERENCE_KB_WIDTH = 1080f
        private const val REFERENCE_KB_HEIGHT = 400f
        
        // Helper to calculate scale factor from keyMap (uses geometric mean of X and Y scales)
        fun getKeyboardScale(keyMap: Map<String, PointF>): Float {
            if (keyMap.isEmpty()) return 1f
            val minX = keyMap.values.minOfOrNull { it.x } ?: 0f
            val maxX = keyMap.values.maxOfOrNull { it.x } ?: REFERENCE_KB_WIDTH
            val minY = keyMap.values.minOfOrNull { it.y } ?: 0f
            val maxY = keyMap.values.maxOfOrNull { it.y } ?: REFERENCE_KB_HEIGHT
            val kbWidth = maxX - minX
            val kbHeight = maxY - minY
            val scaleX = kbWidth / REFERENCE_KB_WIDTH
            val scaleY = kbHeight / REFERENCE_KB_HEIGHT
            // Use geometric mean to handle aspect ratio changes fairly
            val scale = kotlin.math.sqrt(scaleX * scaleY).coerceIn(0.3f, 2.0f)
            return scale
        }
        
        // Weights: 
        // Shape: 0.25 -> kept low to allow messy sizing
        // Location: 0.85 -> high trust in key hits
        // Direction: 0.5 -> kept low
        // Turn: 1.5 -> SIGNIFICANTLY INCREASED (was 0.9). Sharp corners are now king.
        // Dwell: 0.6 -> Weight for keys where user lingered longer
        private const val SHAPE_WEIGHT = 0.25f
        private const val DWELL_TIME_WEIGHT = 0.6f  // NEW: Weight for time-based key scoring
        private const val DWELL_THRESHOLD_MS = 80L  // Minimum ms to count as "lingering" on a key
        private const val LOCATION_WEIGHT = 0.85f
        private const val DIRECTION_WEIGHT = 0.5f   
        private const val TURN_WEIGHT = 1.5f        
        
        // Files
        private const val USER_STATS_FILE = "user_stats.json"
        private const val BLOCKED_DICT_FILE = "blocked_words.txt"
        private const val USER_DICT_FILE = "user_words.txt"
        private const val MIN_WORD_LENGTH = 2
        
        // (Moved to instance variable to allow Settings adjustment)

        // =================================================================================
        // SHAPE/CONTEXT ALGORITHM WEIGHTS (Gboard-style - inverted from Precise)
        // =================================================================================
        private const val SHAPE_CONTEXT_SHAPE_WEIGHT = 1.5f      // REDUCED: Allow more fuzziness (was 1.8)
        private const val SHAPE_CONTEXT_LOCATION_WEIGHT = 0.4f   // LOW: Position tolerance
        private const val SHAPE_CONTEXT_DIRECTION_WEIGHT = 0.6f  // Medium: Flow matters
        private const val SHAPE_CONTEXT_TURN_WEIGHT = 1.5f       // HIGH: Corners reliable
        private const val SHAPE_CONTEXT_START_PENALTY = 1.0f     // REDUCED: Forgive sloppy starts
        private const val SHAPE_CONTEXT_END_PENALTY = 0.8f       // REDUCED: Forgive overshoot (e.g. t->y)

    }

    // =================================================================================
    // END BLOCK: TUNING PARAMETERS
    // =================================================================================






    // ... (TrieNode class remains the same) ...

// HELPER: Consonant Anchoring
    private fun isVowel(c: Char): Boolean = "aeiouy".contains(c.lowercaseChar())

    // UPDATE: Add directionVectors and WEIGHTS
    data class WordTemplate(
        val word: String,
        val rank: Int,
        val rawPoints: List<PointF>,
        val rawWeights: List<Float>, // NEW: Consonant/Vowel weights
        var sampledPoints: List<PointF>? = null,
        var sampledWeights: List<Float>? = null, // NEW: Resampled weights
        var normalizedPoints: List<PointF>? = null,
        var directionVectors: List<PointF>? = null
    )




    // =================================================================================
    // DATA STRUCTURES
    // =================================================================================
    
    class TrieNode {
        val children = HashMap<Char, TrieNode>()
        var isEndOfWord = false
        var word: String? = null
        var rank: Int = Int.MAX_VALUE // 0 = Most Frequent
    }

    private val root = TrieNode()
    private val wordList = ArrayList<String>()

    // =================================================================================
    // SETTINGS: PREDICTION AGGRESSION (Legacy)
    // Kept for backwards compatibility with settings serialization.
    // No longer affects prediction behavior — preview cache is used instead.
    // =================================================================================
    var speedThreshold: Float = 0.8f


    private var lastKeyMap: Map<String, PointF>? = null
    // --- USER STATS ---
    private val USER_STATS_FILE = "user_stats.json"
    private val KEY_OFFSETS_FILE = "key_offsets.json" // NEW
    private val userFrequencyMap = HashMap<String, Int>()
    
    // SPATIAL LEARNING: Stores user's average offset (dx, dy) for each key
    private val keyOffsets = HashMap<String, PointF>()
    
        // BACKSPACE LEARNING: Map of <Word, ExpirationTimestamp>
    private val temporaryPenalties = java.util.concurrent.ConcurrentHashMap<String, Long>()
    private val PENALTY_DURATION_MS = 15_000L // Reduced to 15s safety net
    
    fun clearTemporaryPenalties() {
        if (temporaryPenalties.isNotEmpty()) {
            temporaryPenalties.clear()

        }
    }

    fun penalizeWord(word: String) {
        val clean = word.trim().lowercase(java.util.Locale.ROOT)
        // Set expiration time
        temporaryPenalties[clean] = System.currentTimeMillis() + PENALTY_DURATION_MS

    }
    private var lastSwipePath: List<TimedPoint>? = null // Cache last path to learn from
    
    // Cache last preview result — preview is more accurate than full decode for short words
    @Volatile private var lastPreviewWords: List<String> = emptyList()
    @Volatile private var lastPreviewTimestamp: Long = 0L
    @Volatile private var lastPreviewPathLength: Float = 0f



    
    // =================================================================================
    // OPTIMIZATION: Pre-indexed word lookup by first and last letter
    // SUMMARY: Instead of filtering all 10k words, we lookup by first letter then
    //          filter by last letter. This reduces candidate pool by ~96% immediately.
    // =================================================================================
    private val wordsByFirstLetter = HashMap<Char, ArrayList<String>>()
    private val wordsByFirstLastLetter = HashMap<String, ArrayList<String>>()
    // =================================================================================
    // END BLOCK: Pre-indexed word lookup
    // =================================================================================
    
    // Template cache - maps word to its template (lazy-computed per keyboard layout)
    private val templateCache = HashMap<String, WordTemplate>()
    private var lastKeyMapHash = 0  // Track if keyboard layout changed

    // --- CUSTOM DICTIONARY STORAGE ---
    private val blockedWords = java.util.HashSet<String>()
    private val customWords = java.util.HashSet<String>()
    // =================================================================================
    // CUSTOM WORD DISPLAY FORMS
    // SUMMARY: Maps lowercase lookup key to the display form with proper capitalization.
    //          E.g., "droidos" -> "DroidOS", "iphone" -> "iPhone"
    //          This allows swiping "droidos" to output "DroidOS".
    // =================================================================================
    private val customWordDisplayForms = HashMap<String, String>()
    // =================================================================================
    // END BLOCK: Custom word display forms
    // =================================================================================

    // =================================================================================
    // CONTEXT MODEL DATA STRUCTURES
    // SUMMARY: N-gram frequency maps for language model scoring.
    // =================================================================================
    private val bigramCounts = HashMap<String, HashMap<String, Int>>()
    private val unigramCounts = HashMap<String, Int>()
    private var lastContextWord: String? = null
    
    // =================================================================================
    // GRAMMAR ENGINE (Heuristic POS Tagger)
    // SUMMARY: Lightweight tagging for top ~120 structural words to apply grammar rules.
    // =================================================================================
enum class POSTag { NOUN, VERB, ADJECTIVE, PRONOUN, DETERMINER, PREPOSITION, CONJUNCTION, ADVERB, QUESTION, UNKNOWN }

    private val commonPosTags = hashMapOf(
        // DETERMINERS
        "the" to POSTag.DETERMINER, "a" to POSTag.DETERMINER, "an" to POSTag.DETERMINER, 
        "this" to POSTag.DETERMINER, "that" to POSTag.DETERMINER, "these" to POSTag.DETERMINER, 
        "those" to POSTag.DETERMINER, "my" to POSTag.DETERMINER, "your" to POSTag.DETERMINER, 
        "his" to POSTag.DETERMINER, "her" to POSTag.DETERMINER, "its" to POSTag.DETERMINER, 
        "our" to POSTag.DETERMINER, "their" to POSTag.DETERMINER, "all" to POSTag.DETERMINER,
        "some" to POSTag.DETERMINER, "any" to POSTag.DETERMINER, "no" to POSTag.DETERMINER,
        
        // PRONOUNS
        "i" to POSTag.PRONOUN, "you" to POSTag.PRONOUN, "he" to POSTag.PRONOUN, 
        "she" to POSTag.PRONOUN, "it" to POSTag.PRONOUN, "we" to POSTag.PRONOUN, 
        "they" to POSTag.PRONOUN, "me" to POSTag.PRONOUN, "him" to POSTag.PRONOUN,
        "her" to POSTag.PRONOUN, "us" to POSTag.PRONOUN, "them" to POSTag.PRONOUN,
        
        // VERBS (Auxiliary & Common Action)
        "is" to POSTag.VERB, "am" to POSTag.VERB, "are" to POSTag.VERB, 
        "was" to POSTag.VERB, "were" to POSTag.VERB, "be" to POSTag.VERB,
        "been" to POSTag.VERB, "being" to POSTag.VERB,
        "have" to POSTag.VERB, "has" to POSTag.VERB, "had" to POSTag.VERB,
        "do" to POSTag.VERB, "does" to POSTag.VERB, "did" to POSTag.VERB,
        "go" to POSTag.VERB, "going" to POSTag.VERB, "went" to POSTag.VERB, 
        "gone" to POSTag.VERB, "get" to POSTag.VERB, "got" to POSTag.VERB,
        "make" to POSTag.VERB, "know" to POSTag.VERB, "think" to POSTag.VERB, 
        "take" to POSTag.VERB, "see" to POSTag.VERB, "come" to POSTag.VERB, 
        "want" to POSTag.VERB, "look" to POSTag.VERB, "use" to POSTag.VERB,
        "find" to POSTag.VERB, "give" to POSTag.VERB, "tell" to POSTag.VERB,
        "say" to POSTag.VERB, "said" to POSTag.VERB,
        "run" to POSTag.VERB, "play" to POSTag.VERB, "walk" to POSTag.VERB, 
        "rub" to POSTag.VERB, "rib" to POSTag.VERB, // ADDED
        "shady" to POSTag.ADJECTIVE, // ADDED
        "call" to POSTag.VERB, "try" to POSTag.VERB, "need" to POSTag.VERB,
        "can" to POSTag.VERB, "could" to POSTag.VERB, "will" to POSTag.VERB,
        "would" to POSTag.VERB, "should" to POSTag.VERB, "may" to POSTag.VERB,
        "might" to POSTag.VERB, "must" to POSTag.VERB,
        
        // PREPOSITIONS
        "to" to POSTag.PREPOSITION, "of" to POSTag.PREPOSITION, "in" to POSTag.PREPOSITION,
        "for" to POSTag.PREPOSITION, "on" to POSTag.PREPOSITION, "with" to POSTag.PREPOSITION,
        "at" to POSTag.PREPOSITION, "from" to POSTag.PREPOSITION, "by" to POSTag.PREPOSITION,
        "about" to POSTag.PREPOSITION, "as" to POSTag.PREPOSITION, "into" to POSTag.PREPOSITION,
        "like" to POSTag.PREPOSITION, "through" to POSTag.PREPOSITION, "after" to POSTag.PREPOSITION,
        "over" to POSTag.PREPOSITION, "between" to POSTag.PREPOSITION, "out" to POSTag.PREPOSITION,
        "against" to POSTag.PREPOSITION, "during" to POSTag.PREPOSITION, "without" to POSTag.PREPOSITION,
        "before" to POSTag.PREPOSITION, "under" to POSTag.PREPOSITION, "around" to POSTag.PREPOSITION,
        "among" to POSTag.PREPOSITION,
        
        // CONJUNCTIONS
        "and" to POSTag.CONJUNCTION, "but" to POSTag.CONJUNCTION, "or" to POSTag.CONJUNCTION,
        "if" to POSTag.CONJUNCTION, "because" to POSTag.CONJUNCTION, "when" to POSTag.CONJUNCTION,
        "than" to POSTag.CONJUNCTION, "so" to POSTag.CONJUNCTION, "while" to POSTag.CONJUNCTION,
        "since" to POSTag.CONJUNCTION, "although" to POSTag.CONJUNCTION, "though" to POSTag.CONJUNCTION,
        
        // ADVERBS (High Frequency)
        "not" to POSTag.ADVERB, "now" to POSTag.ADVERB, "then" to POSTag.ADVERB,
        "just" to POSTag.ADVERB, "only" to POSTag.ADVERB, "also" to POSTag.ADVERB,
        "very" to POSTag.ADVERB, "here" to POSTag.ADVERB, "there" to POSTag.ADVERB,
        "well" to POSTag.ADVERB, "even" to POSTag.ADVERB, "still" to POSTag.ADVERB,
        "never" to POSTag.ADVERB, "always" to POSTag.ADVERB, "back" to POSTag.ADVERB,
        
        // QUESTION WORDS
        "what" to POSTag.QUESTION, "who" to POSTag.QUESTION, "where" to POSTag.QUESTION,
        "why" to POSTag.QUESTION, "how" to POSTag.QUESTION, "which" to POSTag.QUESTION
    )
    // =================================================================================
    // END BLOCK: Context model data structures
    // =================================================================================
    // Cache for the top 1000 words to make "Hail Mary" pass instant
    private val commonWordsCache = ArrayList<String>()
    // Throttle template failure logging
    private var lastTemplateMissLog = 0L
    
    // =================================================================================
    // END DATA STRUCTURES
    // =================================================================================



    init {
        loadDefaults()
    }



    private fun loadDefaults() {
        val commonWords = listOf(
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
            "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
            "this", "but", "his", "by", "from", "they", "we", "say", "her",
            "she", "or", "an", "will", "my", "one", "all", "would", "there",
            "their", "what", "so", "up", "out", "if", "about", "who", "get",
            "which", "go", "me", "when", "make", "can", "like", "time", "no",
            "just", "him", "know", "take", "people", "into", "year", "your",
            "good", "some", "could", "them", "see", "other", "than", "then",
            "now", "look", "only", "come", "its", "over", "think", "also",
            "back", "after", "use", "two", "how", "our", "work", "first",
            "well", "way", "even", "new", "want", "because", "any", "these",
            "give", "day", "most", "us", "is", "was", "are", "been", "has",
            "more", "or", "had", "did", "said", "each", "she", "may", "find",
            "long", "down", "did", "get", "made", "live", "back", "little",
            "only", "round", "man", "year", "came", "show", "every", "good",
            "great", "help", "through", "much", "before", "line", "right", 
            "too", "old", "mean", "same", "tell", "boy", "follow", "very",
            "just", "why", "ask", "went", "men", "read", "need", "land",
            "here", "home", "big", "high", "such", "again", "turn", "hand",
            "run", "store", "what", "where", "how", "off", "play", "end", // Added missing words            "play", "small", "end", "put", "while", "next", "sound", "below",
            // Common mobile/tech words
            "swipe", "keyboard", "trackpad", "android", "phone", "text", "type",
            "hello", "yes", "no", "ok", "okay", "thanks", "please", "sorry",
            "love", "like", "cool", "nice", "awesome", "great", "good", "bad"
        )
        for ((index, word) in commonWords.withIndex()) {
            insert(word, index)
        }
    }


// =================================================================================
    // USER STATS & PRIORITY LOGIC
    // =================================================================================
    
    private fun loadUserStats(context: Context) {
        try {
            val file = java.io.File(context.filesDir, USER_STATS_FILE)
            if (file.exists()) {
                val content = file.readText()
                // Simple parsing: "word":count
                content.replace("{", "").replace("}", "").split(",").forEach {
                    val parts = it.split(":")
                    if (parts.size == 2) {
                        val w = parts[0].trim().replace("\"", "")
                        val c = parts[1].trim().toIntOrNull() ?: 0
                        userFrequencyMap[w] = c
                    }
                }

            }
        } catch (e: Exception) {
        }
    }

    private fun saveUserStats(context: Context) {
        Thread {
            try {
                val sb = StringBuilder("{")
                synchronized(userFrequencyMap) {
                    var first = true
                    for ((k, v) in userFrequencyMap) {
                        if (!first) sb.append(",")
                        sb.append("\"$k\":$v")
                        first = false
                    }
                }
                sb.append("}")
                val file = java.io.File(context.filesDir, USER_STATS_FILE)
                file.writeText(sb.toString())
            } catch (e: Exception) {

            }
        }.start()
    }

    /**
     * Call this when the user clicks a word in the suggestion bar.
     * Boosts the word's priority for future predictions.
     */

/**
     * Call this when the user clicks a word in the suggestion bar.
     * Boosts the word's priority for future predictions.
     * NOTE: This no longer auto-learns new words. Use learnWord() for that.
     */


    fun recordSelection(context: Context, word: String) {
        if (word.isBlank()) return
        val clean = word.lowercase(Locale.ROOT)
        
        synchronized(userFrequencyMap) {
            val count = userFrequencyMap.getOrDefault(clean, 0)
            userFrequencyMap[clean] = count + 1
        }

        // NEW: Spatial Learning
        // If we have the swipe path that generated this word, learn the offsets.
        if (lastSwipePath != null) {
             learnKeyOffsets(context, clean, lastSwipePath!!)
             lastSwipePath = null // Consumed
        } else {
        }
        
        saveUserStats(context)
    }



    fun loadDictionary(context: Context) {
        Thread {
            try {
                loadUserStats(context)
                loadKeyOffsets(context)
                val start = System.currentTimeMillis()
                val newRoot = TrieNode()
                val newWordList = ArrayList<String>()
                val newBlocked = java.util.HashSet<String>()
                val newCustom = java.util.HashSet<String>()
                var lineCount = 0


                val newWordsByFirstLetter = HashMap<Char, ArrayList<String>>()
                val newWordsByFirstLastLetter = HashMap<String, ArrayList<String>>()
                // FIX: Declare here so it is accessible in the commit block
                val newDisplayForms = HashMap<String, String>()


                // =================================================================================
                // LOAD CUSTOM LISTS (User & Blocked)
                // SUMMARY: Loads user's custom words and blocked words from persistent storage.
                // =================================================================================
                try {
                    val blockFile = java.io.File(context.filesDir, BLOCKED_DICT_FILE)
                    if (blockFile.exists()) {
                        val blockedLines = blockFile.readLines().map { it.trim().lowercase(java.util.Locale.ROOT) }.filter { it.isNotEmpty() }
                        newBlocked.addAll(blockedLines)

                    } else {

                    }

                    val userFile = java.io.File(context.filesDir, USER_DICT_FILE)
                    if (userFile.exists()) {
                        val userLines = userFile.readLines().map { it.trim() }.filter { it.isNotEmpty() }
                        
                        // FILTER: Check each user word against garbage filter on load
                        // Also build display forms map
                        for (originalForm in userLines) {
                            val lookupForm = originalForm.lowercase(java.util.Locale.ROOT)
                            val trieKey = lookupForm.replace("'", "")
                            
                            if (!looksLikeGarbage(trieKey)) {
                                newCustom.add(lookupForm)
                                // Store display form mapping
                                newDisplayForms[lookupForm] = originalForm
                                newDisplayForms[trieKey] = originalForm
                            } else {

                            }
                        }

                    } else {

                    }
                } catch (e: Exception) {
                }

                // 2. Load Main Dictionary (Assets) - Filtering Blocked words
                try {
                    context.assets.open("dictionary.txt").bufferedReader().useLines { lines ->
                        lines.forEachIndexed { index, line ->
                            val word = line.trim().lowercase(java.util.Locale.ROOT)
                            // SKIP if blocked
                            if (!newBlocked.contains(word) && word.isNotEmpty() && word.all { it.isLetter() } && word.length >= MIN_WORD_LENGTH) {
                                newWordList.add(word)
                                lineCount++

                                var current = newRoot
                                for (char in word) {
                                    current = current.children.computeIfAbsent(char) { TrieNode() }
                                }
                                current.isEndOfWord = true
                                current.word = word
                                if (index < current.rank) current.rank = index

                                if (word.length >= 2) {
                                    val firstChar = word.first()
                                    newWordsByFirstLetter.getOrPut(firstChar) { ArrayList() }.add(word)
                                    val key = "${word.first()}${word.last()}"
                                    newWordsByFirstLastLetter.getOrPut(key) { ArrayList() }.add(word)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                }

                // 3. Merge Custom Words
                for (word in newCustom) {
                    if (!newWordList.contains(word)) {
                        newWordList.add(word)
                        var current = newRoot
                        for (char in word) {
                            current = current.children.computeIfAbsent(char) { TrieNode() }
                        }
                        current.isEndOfWord = true
                        current.word = word
                        current.rank = 50 // Moderate priority — compete on geometry, not just frequency

                        if (word.length >= 2) {
                            val firstChar = word.first()
                            newWordsByFirstLetter.getOrPut(firstChar) { ArrayList() }.add(word)
                            val key = "${word.first()}${word.last()}"
                            newWordsByFirstLastLetter.getOrPut(key) { ArrayList() }.add(word)
                        }
                    }
                }

                // 4. Merge Hardcoded Defaults
                val existingDefaults = synchronized(this) { ArrayList(wordList) }
                for (defaultWord in existingDefaults) {
                    val lower = defaultWord.lowercase(java.util.Locale.ROOT)
                    if (!newBlocked.contains(lower) && !newWordList.contains(lower)) {
                        newWordList.add(lower)
                        var current = newRoot
                        for (char in lower) {
                            current = current.children.computeIfAbsent(char) { TrieNode() }
                        }
                        current.isEndOfWord = true
                        current.word = lower
                        if (current.rank > 100) current.rank = 50
                    }
                }

                // 5. Commit Changes
                synchronized(this) {
                    wordList.clear()
                    wordList.addAll(newWordList)
                    root.children.clear()
                    root.children.putAll(newRoot.children)

                    wordsByFirstLetter.clear()
                    wordsByFirstLetter.putAll(newWordsByFirstLetter)
                    wordsByFirstLastLetter.clear()
                    wordsByFirstLastLetter.putAll(newWordsByFirstLastLetter)

                    blockedWords.clear()
                    blockedWords.addAll(newBlocked)
                    customWords.clear()
                    customWords.addAll(newCustom)
                    
                    // Load display forms
                    customWordDisplayForms.clear()
                    customWordDisplayForms.putAll(newDisplayForms)
                    customWords.addAll(newCustom)

                    templateCache.clear()

                    // Populate Common Words Cache (Top 1000)
                    commonWordsCache.clear()
                    commonWordsCache.addAll(
                        wordList.sortedBy { getWordRank(it) }.take(1000)
                    )
                }
                // =================================================================================
                // OPTIMIZATION: Pre-warm template cache for common words
                // SUMMARY: Pre-compute templates for top 500 words to eliminate lag on first swipe.
                //          This runs in background so doesn't block UI.
                // =================================================================================
                // Note: Templates require keyMap which we don't have here.
                // Templates will be created on first use, but the word indexes are ready.
                // =================================================================================


            } catch (e: Exception) {

            }
        }.start()
    }




// =================================================================================
    // FUNCTION: learnWord
    // SUMMARY: Learns a new word and saves to user_words.txt
    //          
    // SMART CAPITALIZATION:
    //   - If at sentence start: strip first-letter cap, keep internal caps
    //     E.g., "DroidOS" at start → stored as "droidOS" 
    //   - If mid-sentence: keep all caps as typed
    //     E.g., "DroidOS" mid-sentence → stored as "DroidOS"
    //   - When suggesting, always use the stored display form
    //
    // @param isSentenceStart: true if word was typed at beginning of sentence
    // =================================================================================
    fun learnWord(context: Context, word: String, isSentenceStart: Boolean = false) {
        if (word.length < 2) return

        val originalWord = word.trim()
        
        // =======================================================================
        // SMART CAPITALIZATION
        // If at sentence start, the first letter being capital is automatic,
        // so we should store without that automatic cap to get the "true" form.
        // E.g., User types "DroidOS" at start → they want "droidOS" normally
        // But if typed mid-sentence as "DroidOS" → they want "DroidOS" always
        // =======================================================================
        val displayForm = if (isSentenceStart && originalWord.isNotEmpty() && originalWord[0].isUpperCase()) {
            // Strip the automatic sentence-start capitalization
            originalWord[0].lowercaseChar() + originalWord.substring(1)
        } else {
            // Keep as-is (mid-sentence or already lowercase)
            originalWord
        }
        // =======================================================================
        // END BLOCK: Smart capitalization
        // =======================================================================
        
        val lookupWord = displayForm.lowercase(java.util.Locale.ROOT)
        val trieKey = lookupWord.replace("'", "")  // For trie lookup

        // Don't learn garbage
        if (looksLikeGarbage(trieKey)) {

             return
        }

        // Don't relearn if already known (but allow updating display form)
        val alreadyKnown = hasWord(trieKey) || hasWord(lookupWord)
        
        Thread {
            try {
                synchronized(this) {
                    customWords.add(lookupWord)
                    blockedWords.remove(lookupWord)
                    blockedWords.remove(trieKey)
                    
                    // Store the display form for this word
                    customWordDisplayForms[lookupWord] = displayForm
                    customWordDisplayForms[trieKey] = displayForm
                    
                    // Insert base form (without apostrophe) for swipe matching
                    if (!alreadyKnown) {
                        insert(trieKey, 0)
                        
                        // Also insert with apostrophe if present
                        if (lookupWord.contains("'")) {
                            insert(lookupWord, 0)
                        }
                    }

                    // FIX: File I/O moved INSIDE synchronized block to prevent race conditions
                    val file = java.io.File(context.filesDir, USER_DICT_FILE)
                    
                    // If already known, we need to update the file (remove old, add new)
                    if (alreadyKnown) {
                        val existingLines = if (file.exists()) file.readLines() else emptyList()
                        val updatedLines = existingLines.filter { 
                            it.trim().lowercase(java.util.Locale.ROOT).replace("'", "") != trieKey 
                        } + displayForm
                        file.writeText(updatedLines.joinToString("\n") + "\n")

                    } else {
                        file.appendText("$displayForm\n")

                    }

                    // Safe to call here (it uses synchronized data)
                    saveSetToFile(context, BLOCKED_DICT_FILE, blockedWords)
                }

            } catch (e: Exception) {

            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: learnWord with smart capitalization
    // =================================================================================

// =================================================================================
    // FUNCTION: getDisplayForm
    // SUMMARY: Returns the proper display form for a custom word.
    //          E.g., "droidos" -> "DroidOS", "iphone" -> "iPhone"
    //          Returns the input unchanged if not a custom word.
    // =================================================================================
    fun getDisplayForm(word: String): String {
        val lookup = word.lowercase(java.util.Locale.ROOT)
        val lookupNoApostrophe = lookup.replace("'", "")
        
        return customWordDisplayForms[lookup] 
            ?: customWordDisplayForms[lookupNoApostrophe]
            ?: word
    }
    // =================================================================================
    // END BLOCK: getDisplayForm
    // =================================================================================

    // =================================================================================
    // FUNCTION: saveSetToFile
    // SUMMARY: Saves a set of words to a file in the app's private storage.
    // =================================================================================
    private fun saveSetToFile(context: Context, filename: String, data: Set<String>) {
        try {
            val file = java.io.File(context.filesDir, filename)
            // FIX: Append newline to prevent next write from merging with last word (e.g. can'twon't)
            val content = data.filter { it.isNotEmpty() }.joinToString("\n") + "\n"
            file.writeText(content)

        } catch (e: Exception) {
        }
    }
    // =================================================================================
    // END BLOCK: saveSetToFile
    // =================================================================================

    // =================================================================================
    // FILTER: GARBAGE DETECTION
    // Rule: Must have at least one vowel/y OR be in the whitelist.
    // =================================================================================
    private val VALID_VOWELLESS = setOf(
        "hmm", "shh", "psst", "brr", "pfft", "nth", "src", "jpg", "png", "gif",
        "txt", "xml", "pdf", "css", "html", "tv", "pc", "ok", "id", "cv", "ad", "ex", "vs", "mr", "dr", "ms"
    )

    // =================================================================================
    // FUNCTION: looksLikeGarbage
    // SUMMARY: Filters random letter combinations. Must have vowel or be whitelisted.
    // UPDATED: Strips apostrophes before checking (don't -> dont has vowel)
    // =================================================================================
    private fun looksLikeGarbage(word: String): Boolean {
        val checkWord = word.replace("'", "")
        if (checkWord.length > 1) {
            val hasVowel = checkWord.any { "aeiouyAEIOUY".contains(it) }
            if (!hasVowel) {
                if (VALID_VOWELLESS.contains(checkWord.lowercase(java.util.Locale.ROOT))) return false
                return true
            }
        }
        return false
    }
    // =================================================================================
    // END BLOCK: looksLikeGarbage
    // =================================================================================

    fun hasWord(word: String): Boolean {
        return wordList.contains(word.lowercase(Locale.ROOT))
    }

    // =================================================================================
    // FUNCTION: isWordBlocked
    // SUMMARY: Checks if a word is in the blocked list.
    // =================================================================================
    fun isWordBlocked(word: String): Boolean {
        return blockedWords.contains(word.lowercase(Locale.ROOT))
    }
// =================================================================================
    // END BLOCK: isWordBlocked
    // =================================================================================

    // =================================================================================
    // FUNCTION: isCustomWord
    // SUMMARY: Checks if a word is in the user's custom dictionary.
    //          Used to style user-added words differently (italic) in prediction bar.
    // =================================================================================
    fun isCustomWord(word: String): Boolean {
        val lower = word.lowercase(Locale.ROOT)
        val withoutApostrophe = lower.replace("'", "")
        return customWords.contains(lower) || customWords.contains(withoutApostrophe)
    }
    // =================================================================================
    // END BLOCK: isCustomWord
    // =================================================================================    // =================================================================================
    // FUNCTION: insert
    // SUMMARY: Inserts a word into the Trie and the first/last letter index.
    // =================================================================================
    fun insert(word: String, rank: Int) {
        val lower = word.lowercase(Locale.ROOT)
        if (lower.length < 2) return  // Skip single-letter words
        
        var node = root
        for (c in lower) {
            node = node.children.getOrPut(c) { TrieNode() }
        }
        node.isEndOfWord = true
        node.word = lower
        node.rank = rank
        
        // OPTIMIZATION: Add to first-letter index
        val firstChar = lower.first()
        wordsByFirstLetter.getOrPut(firstChar) { ArrayList() }.add(lower)
        
        // OPTIMIZATION: Add to first+last letter index for fast lookup
        val key = "${lower.first()}${lower.last()}"
        wordsByFirstLastLetter.getOrPut(key) { ArrayList() }.add(lower)
    }
    // =================================================================================
    // END BLOCK: insert
    // =================================================================================

    /**
     * Returns a list of suggested words for the given prefix, sorted by popularity.
     */
    // =================================================================================
    // FUNCTION: getSuggestions
    // SUMMARY: Returns suggested words for a given prefix, sorted by popularity.
    //          Filters out blocked words to prevent them from appearing in suggestions.

// =================================================================================
    // FUNCTION: getSuggestions (Updated for Priority Sort)
    // =================================================================================


    fun getSuggestions(prefix: String, maxResults: Int = 3): List<String> {
        if (prefix.isEmpty()) return emptyList()
        val cleanPrefix = prefix.lowercase(java.util.Locale.ROOT)

        var current = root
        for (char in cleanPrefix) {
            current = current.children[char] ?: return emptyList()
        }

        val candidates = ArrayList<Pair<String, Int>>()
        collectCandidates(current, candidates)
        
        val sortedCandidates = candidates.sortedWith(Comparator { a, b ->
            val wordA = a.first
            val wordB = b.first
            
            val countA = userFrequencyMap[wordA] ?: 0
            val countB = userFrequencyMap[wordB] ?: 0
            
            if (countA != countB) return@Comparator countB - countA
            val rankA = a.second
            val rankB = b.second
            if (rankA != rankB) return@Comparator rankA - rankB
            wordA.length - wordB.length
        })

        return sortedCandidates
            .filter { !blockedWords.contains(it.first.lowercase(java.util.Locale.ROOT)) }
            .distinctBy { it.first }
            .take(maxResults)
            .map { it.first }
    }



    // =================================================================================
    // FUNCTION: collectCandidates
    // SUMMARY: Recursively collects word candidates from trie nodes.
    //          Skips blocked words during traversal for efficiency.
    // =================================================================================
    private fun collectCandidates(node: TrieNode, results: MutableList<Pair<String, Int>>) {
        if (node.isEndOfWord) {
            node.word?.let { word ->
                // Skip blocked words
                if (!blockedWords.contains(word)) {
                    results.add(word to node.rank)
                }
            }
        }
        for (child in node.children.values) {
            collectCandidates(child, results)
        }
    }
    // =================================================================================
    // END BLOCK: collectCandidates
    // =================================================================================

    // =================================================================================
    // SHARK2-INSPIRED SWIPE DECODER LOGIC
    // =================================================================================
    
    /**
     * Main entry point for decoding a swipe gesture into word candidates.
     * Implements SHARK2-style dual-channel matching with:
     * 1. Path sampling to uniform N points
     * 2. Candidate pruning by start/end keys
     * 3. Shape channel scoring (normalized patterns)
     * 4. Location channel scoring (absolute positions)  
     * 5. Integration with frequency weighting
     */
    // =================================================================================
    // FUNCTION: decodeSwipe (Adaptive Length + Robust Neighbors)
    // =================================================================================
// =================================================================================
    // FUNCTION: decodeSwipe (v6 - Turn-Aware Scoring)
    // SUMMARY: Based on original working version with:
    //          - NEW turn detection for shortcut, android, circle
    //          - Conservative dwell for as/ass, to/too
    //          - Original candidate collection and length filtering
    // =================================================================================
    fun decodeSwipe(swipePath: List<PointF>, keyMap: Map<String, PointF>): List<String> {
        if (swipePath.size < 3 || keyMap.isEmpty()) return emptyList()
        if (wordList.isEmpty()) {
            loadDefaults()
            return emptyList()
        }

        val keyMapHash = keyMap.hashCode()
        if (keyMapHash != lastKeyMapHash) {
            templateCache.clear()
            lastKeyMapHash = keyMapHash
        }

        val inputLength = getPathLength(swipePath)
        if (inputLength < 10f) return emptyList()

        // =======================================================================
        // DWELL DETECTION (Conservative - for to/too, as/ass)
        // =======================================================================
        var dwellScore = 0f
        if (swipePath.size > 12) {
            val tailSize = maxOf(12, swipePath.size / 4)
            val tailStart = maxOf(0, swipePath.size - tailSize)
            val tail = swipePath.subList(tailStart, swipePath.size)
            
            var tailLength = 0f
            for (i in 1 until tail.size) {
                tailLength += hypot(tail[i].x - tail[i-1].x, tail[i].y - tail[i-1].y)
            }
            
            val avgMovementPerPoint = tailLength / tail.size
            dwellScore = when {
                avgMovementPerPoint < 2f -> 1.0f
                avgMovementPerPoint < 4f -> 0.6f
                avgMovementPerPoint < 7f -> 0.2f
                else -> 0f
            }
        }
        val isDwellingAtEnd = dwellScore >= 0.6f
        // =======================================================================
        // END DWELL DETECTION
        // =======================================================================

        val sampledInput = samplePath(swipePath, SAMPLE_POINTS)
        val normalizedInput = normalizePath(sampledInput)
        val inputDirections = calculateDirectionVectors(sampledInput)

        // NEW: Calculate turn points for input
        val inputTurns = detectTurns(inputDirections)

        // NEW: Extract sequence of keys the path passes through
        // This is CRITICAL for distinguishing "awake" vs "awesome"

        val pathKeys = extractPathKeys(sampledInput, keyMap, 8)

        val startPoint = sampledInput.first()
        val endPoint = sampledInput.last()

        // FIX: Define startKey and endKey (Missing in previous build)
        val startKey = findClosestKey(startPoint, keyMap)
        val endKey = findClosestKey(endPoint, keyMap)
        
// =======================================================================
        // CANDIDATE COLLECTION (Enhanced with Path Key Matching)
        // SUMMARY: Collects candidates using:
        //          1. Start/End neighbor search (original)
        //          2. PREFIX INJECTION (original)  
        //          3. PATH KEY INJECTION - NEW: Add words containing detected path keys
        //             This ensures "awake" is included when path shows a→w→...
        //          4. User History (original)
        // =======================================================================
        val candidates = HashSet<String>()
        
        // 1. Neighbor Search (scaled for keyboard size)
        val scale = getKeyboardScale(keyMap)
        val nearbyStart = findNearbyKeys(startPoint, keyMap, 80f * scale)
        val nearbyEnd = findNearbyKeys(endPoint, keyMap, 80f * scale)
        
        for (s in nearbyStart) {
            for (e in nearbyEnd) {
                wordsByFirstLastLetter["${s}${e}"]?.let { candidates.addAll(it) }
            }
        }
        
        // 2. PREFIX INJECTION (original)
        if (startKey != null) {
            wordsByFirstLetter[startKey.first()]?.let { words ->
                candidates.addAll(words.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(25))
            }
        }

                // 3. PATH KEY INJECTION (Enhanced)

                // If path shows specific intermediate keys, add words that contain those keys.

                // We increased limits (30->150) to ensure "awake" isn't pushed out by common words.

                if (pathKeys.size >= 2) {

                    val secondKey = pathKeys.getOrNull(1)?.firstOrNull()?.lowercaseChar()

                    if (secondKey != null && startKey != null) {

                        wordsByFirstLetter[startKey.first()]?.let { words ->

                            val matchingWords = words.filter { word ->

                                word.length >= 2 && word.drop(1).contains(secondKey)

                            }.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(150) // Increased from 30

                            candidates.addAll(matchingWords)

                        }

                    }

        

                    // Also check third key if present

                    val thirdKey = pathKeys.getOrNull(2)?.firstOrNull()?.lowercaseChar()

                    if (thirdKey != null && startKey != null && thirdKey != secondKey) {

                        wordsByFirstLetter[startKey.first()]?.let { words ->

                            val matchingWords = words.filter { word ->

                                word.length >= 3 && word.drop(1).contains(thirdKey)

                            }.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(150) // Increased from 20

                            candidates.addAll(matchingWords)

                        }

                    }

                    

                    // 3.5 STRICT SEQUENCE MATCH (New)

                    // If we have a complex path (e.g. a->w->a->e), specifically look for words

                    // that contain ALL these keys in relative order.

                    if (pathKeys.size >= 3 && startKey != null) {

                        wordsByFirstLetter[startKey.first()]?.let { words ->

                            // Get all intermediate keys (excluding start)

                            val requiredKeys = pathKeys.drop(1).map { it.firstOrNull()?.lowercaseChar() }.filterNotNull()

                            

                            val strictMatches = words.filter { word ->

                                var lastIdx = 0

                                var matches = true

                                for (rk in requiredKeys) {

                                    val idx = word.indexOf(rk, lastIdx)

                                    if (idx == -1) { 

                                        matches = false; break 

                                    }

                                    lastIdx = idx + 1

                                }

                                matches

                            }.take(50) // Force include these specific matches

                            

                            if (strictMatches.isNotEmpty()) {

                                candidates.addAll(strictMatches)



                            }

                        }

                    }

                }

        

                // 4. User History (original)

                synchronized(userFrequencyMap) {

                    candidates.addAll(userFrequencyMap.entries

                        .sortedByDescending { it.value }

                        .take(30) // Increased from 15

                        .map { it.key })

                }

        

                // Debug: Log total candidates



        

                // =======================================================================

                // END CANDIDATE COLLECTION

                // =======================================================================

        

                // --- SCORING ---

                val scored = candidates

                    .filter { !isWordBlocked(it) && it.length >= MIN_WORD_LENGTH }

                    .sortedWith(compareByDescending<String> { userFrequencyMap[it] ?: 0 }.thenBy { getWordRank(it) })

                    .take(400) // Increased from 150 to prevent dropping valid low-frequency words

                    .mapNotNull { word ->
                val template = getOrCreateTemplate(word, keyMap) ?: return@mapNotNull null
                
                // --- ADAPTIVE LENGTH FILTER (Original) ---
                val tLen = getPathLength(template.rawPoints)
                val ratio = tLen / inputLength
                
                val maxRatio = if (inputLength < 150f) 1.5f else 5.0f
                if (ratio > maxRatio || ratio < 0.4f) return@mapNotNull null

                
                if (template.sampledPoints == null || template.normalizedPoints == null || template.directionVectors == null) {
                    template.sampledPoints = samplePath(template.rawPoints, SAMPLE_POINTS)
                    template.normalizedPoints = normalizePath(template.sampledPoints!!)
                    template.directionVectors = calculateDirectionVectors(template.sampledPoints!!)
                }
                
                val shapeScore = calculateShapeScore(normalizedInput, template.normalizedPoints!!)
                val locScore = calculateLocationScore(sampledInput, template.sampledPoints!!)
                val dirScore = calculateDirectionScore(inputDirections, template.directionVectors!!)

                // NEW: Turn matching score
                val templateTurns = detectTurns(template.directionVectors!!)
                val turnScore = calculateTurnScore(inputTurns, templateTurns)

                // NEW: Path key matching score - penalizes words where path doesn't match
                // This distinguishes "awake" (path goes through w) from "awesome" (path would need s)
                val pathKeyScore = calculatePathKeyScore(pathKeys, word)

                val integrationScore = (shapeScore * SHAPE_WEIGHT) +
                                       (locScore * LOCATION_WEIGHT) +
                                       (dirScore * DIRECTION_WEIGHT) +
                                       (turnScore * TURN_WEIGHT) +
                                       (pathKeyScore * 1.5f)  // INCREASED (was 0.8): Match preview's trust in path keys
                

                // --- BOOSTS (Original) ---
                val rank = template.rank
                val freqBonus = 1.0f / (1.0f + 0.3f * ln((rank + 1).toFloat()))
                
                // CHANGED: Reset user history boost to neutral (1.0f). 
                // This stops user-added words (like "texting") from overriding better geometric matches (like "testing").
                var userBoost = 1.0f
                
                // DOUBLE LETTER BOOST (Conservative - only 3+ letter words)
                val hasEndDouble = word.length >= 3 && 
                    word.last().lowercaseChar() == word[word.length - 2].lowercaseChar()
                
                if (hasEndDouble && isDwellingAtEnd) {
                    userBoost *= (1.10f + dwellScore * 0.15f)
                }
                
                // EXACT KEY MATCH BONUS
                if (startKey != null && word.startsWith(startKey, ignoreCase = true)) userBoost *= 1.15f
                if (endKey != null && word.endsWith(endKey, ignoreCase = true)) {
                    userBoost *= 1.3f
                } else if (endKey != null) {
                    val endChar = endKey.first().lowercaseChar()
                    val wordEnd = word.last().lowercaseChar()
                    if (!areKeysAdjacent(endChar, wordEnd)) {
                        userBoost *= 0.7f
                    }
                }
                
                // LONG WORD BONUS
                if (word.length >= 6) userBoost *= 1.15f



                val finalScore = (integrationScore * (1.0f - 0.5f * freqBonus)) / userBoost
                Pair(word, finalScore)
            }
        
        // FIX: Apply getDisplayForm to ensure capitalized words (e.g. Katsuya) are returned correctly
        val results = scored.sortedBy { it.second }.distinctBy { it.first }.take(3).map { it.first }
        return results.map { getDisplayForm(it) }
    }

    // =================================================================================
    // END BLOCK: Boost Calculation (and decodeSwipe)
    // =================================================================================

    // =================================================================================
    // FUNCTION: decodeSwipeTimed (Time-Weighted Swipe Decoding)
    // SUMMARY: Enhanced version of decodeSwipe that uses timestamp data to detect
    //          when users linger on specific keys. This allows disambiguation of
    //          similar words like "for" vs "four" - lingering on "u" boosts "four".
    //          
    // HOW IT WORKS:
    //   1. Converts TimedPoints to regular path for geometric analysis
    //   2. Calculates dwell time on each key the path crosses
    //   3. Keys with longer dwell times get boosted in scoring
    //   4. Final score combines geometric + frequency + dwell time weights
    // =================================================================================

    // =================================================================================
    // FUNCTION: decodeSwipeTimed (PHASE 2 - Returns Dual Results)
    // SUMMARY: Now returns SwipeResult list from the dual algorithm.
    //          The winning algorithm is selected based on swipe speed.
    //          Source info is used for color-coding in the UI.
    // =================================================================================
    fun decodeSwipeTimed(timedPath: List<TimedPoint>, keyMap: Map<String, PointF>): List<SwipeResult> {
        if (timedPath.size < 3 || keyMap.isEmpty()) return emptyList()
        
        // Use the dual decoder which runs both algorithms and picks winner
        return decodeSwipeDual(timedPath, keyMap)
    }
    // =================================================================================
    // END BLOCK: decodeSwipeTimed
    // =================================================================================

    // =================================================================================
    // FUNCTION: calculateKeyDwellTimes
    // SUMMARY: Analyzes a timed swipe path to calculate how long the user spent
    //          near each key. Returns a map of key -> total milliseconds.
    //          Used to boost keys where user intentionally lingered.
    // =================================================================================
    private fun calculateKeyDwellTimes(
        timedPath: List<TimedPoint>, 
        keyMap: Map<String, PointF>
    ): Map<String, Long> {
        val dwellTimes = HashMap<String, Long>()
        if (timedPath.size < 2) return dwellTimes
        
        // Scale radius based on keyboard size
        val scale = getKeyboardScale(keyMap)
        val KEY_PROXIMITY_RADIUS = 50f * scale  // Pixels - scaled for keyboard size
        
        for (i in 1 until timedPath.size) {
            val prev = timedPath[i - 1]
            val curr = timedPath[i]
            val timeDelta = curr.timestamp - prev.timestamp
            
            // Skip if timestamp jump is too large (likely a pause/resume)
            if (timeDelta > 500) continue
            
            // Find which key this point is closest to
            val point = PointF(curr.x, curr.y)
            var closestKey: String? = null
            var closestDist = Float.MAX_VALUE
            
            for ((key, center) in keyMap) {
                val dist = hypot(point.x - center.x, point.y - center.y)
                if (dist < closestDist && dist < KEY_PROXIMITY_RADIUS) {
                    closestDist = dist
                    closestKey = key
                }
            }
            
            // Add time spent near this key
            if (closestKey != null) {
                dwellTimes[closestKey] = (dwellTimes[closestKey] ?: 0L) + timeDelta
            }
        }
        
        return dwellTimes
    }
    // =================================================================================
    // END BLOCK: calculateKeyDwellTimes
    // =================================================================================

    // =================================================================================
    // FUNCTION: decodeSwipeWithDwell
    // SUMMARY: Modified version of decodeSwipe that incorporates dwell time scoring.
    //          Words containing keys with high dwell times get boosted.
    //          This is the core logic that makes "lingering on U" select "four" over "for".
    // =================================================================================
    private fun decodeSwipeWithDwell(
        swipePath: List<PointF>, 
        keyMap: Map<String, PointF>,
        keyDwellTimes: Map<String, Long>
    ): List<String> {
        if (swipePath.size < 3 || keyMap.isEmpty()) return emptyList()
        if (wordList.isEmpty()) {
            loadDefaults()
            return emptyList()
        }

        val keyMapHash = keyMap.hashCode()
        if (keyMapHash != lastKeyMapHash) {
            templateCache.clear()
            lastKeyMapHash = keyMapHash
        }

        val inputLength = getPathLength(swipePath)
        if (inputLength < 10f) return emptyList()

        // =======================================================================
        // DWELL DETECTION (Conservative - for to/too, as/ass)
        // =======================================================================
        var dwellScore = 0f
        if (swipePath.size > 12) {
            val tailSize = maxOf(12, swipePath.size / 4)
            val tailStart = maxOf(0, swipePath.size - tailSize)
            val tail = swipePath.subList(tailStart, swipePath.size)
            
            var tailLength = 0f
            for (i in 1 until tail.size) {
                tailLength += hypot(tail[i].x - tail[i-1].x, tail[i].y - tail[i-1].y)
            }
            
            val avgMovementPerPoint = tailLength / tail.size
            dwellScore = when {
                avgMovementPerPoint < 2f -> 1.0f
                avgMovementPerPoint < 4f -> 0.6f
                avgMovementPerPoint < 7f -> 0.2f
                else -> 0f
            }
        }
        val isDwellingAtEnd = dwellScore >= 0.6f
        // =======================================================================
        // END DWELL DETECTION
        // =======================================================================

        val sampledInput = samplePath(swipePath, SAMPLE_POINTS)
        if (sampledInput.isEmpty()) return emptyList()
        
        val normalizedInput = normalizePath(sampledInput)
        if (normalizedInput.isEmpty()) return emptyList()
        
        val inputDirections = calculateDirectionVectors(sampledInput)
        if (inputDirections.isEmpty()) return emptyList()

        val inputTurns = detectTurns(inputDirections)
        val pathKeys = extractPathKeys(sampledInput, keyMap, 8)

        val startPoint = sampledInput.first()
        val endPoint = sampledInput.last()
        val startKey = findClosestKey(startPoint, keyMap)
        val endKey = findClosestKey(endPoint, keyMap)
        
        // =======================================================================
        // CANDIDATE COLLECTION (same as original)
        // =======================================================================
        val candidates = HashSet<String>()
        
        val scale = getKeyboardScale(keyMap)
        val nearbyStart = findNearbyKeys(startPoint, keyMap, 80f * scale)
        val nearbyEnd = findNearbyKeys(endPoint, keyMap, 120f * scale)
        
        for (s in nearbyStart) {
            for (e in nearbyEnd) {
                wordsByFirstLastLetter["${s}${e}"]?.let { candidates.addAll(it) }
            }
        }
        
        if (startKey != null) {
            wordsByFirstLetter[startKey.first()]?.let { words ->
                candidates.addAll(words.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(25))
            }
        }

        if (pathKeys.size >= 2) {
            val secondKey = pathKeys.getOrNull(1)?.firstOrNull()?.lowercaseChar()
            if (secondKey != null && startKey != null) {
                wordsByFirstLetter[startKey.first()]?.let { words ->
                    val matchingWords = words.filter { word ->
                        word.length >= 2 && word.drop(1).contains(secondKey)
                    }.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(150)
                    candidates.addAll(matchingWords)
                }
            }
            
            val thirdKey = pathKeys.getOrNull(2)?.firstOrNull()?.lowercaseChar()
            if (thirdKey != null && startKey != null && thirdKey != secondKey) {
                wordsByFirstLetter[startKey.first()]?.let { words ->
                    val matchingWords = words.filter { word ->
                        word.length >= 3 && word.drop(1).contains(thirdKey)
                    }.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(150)
                    candidates.addAll(matchingWords)
                }
            }
            
            if (pathKeys.size >= 3 && startKey != null) {
                wordsByFirstLetter[startKey.first()]?.let { words ->
                    val requiredKeys = pathKeys.drop(1).map { it.firstOrNull()?.lowercaseChar() }.filterNotNull()
                    val strictMatches = words.filter { word ->
                        var lastIdx = 0
                        var matches = true
                        for (rk in requiredKeys) {
                            val idx = word.indexOf(rk, lastIdx)
                            if (idx == -1) { 
                                matches = false; break 
                            }
                            lastIdx = idx + 1
                        }
                        matches
                    }.take(50)
                    
                    if (strictMatches.isNotEmpty()) {
                        candidates.addAll(strictMatches)
                    }
                }
            }
        }

        synchronized(userFrequencyMap) {
            val nearbyStartChars = nearbyStart.mapNotNull { it.firstOrNull()?.lowercaseChar() }.toSet()
            candidates.addAll(userFrequencyMap.entries
                .filter { entry -> 
                    val firstChar = entry.key.firstOrNull()?.lowercaseChar()
                    firstChar != null && firstChar in nearbyStartChars
                }
                .sortedByDescending { it.value }
                .take(30)
                .map { it.key })
        }
        // =======================================================================
        // END CANDIDATE COLLECTION
        // =======================================================================

        // =======================================================================
        // SCORING WITH DWELL TIME BOOST
        // =======================================================================
        val scored = candidates
            .filter { !isWordBlocked(it) && it.length >= MIN_WORD_LENGTH }
            .sortedWith(compareByDescending<String> { userFrequencyMap[it] ?: 0 }.thenBy { getWordRank(it) })
            .take(400)
            .mapNotNull { word ->
                val template = getOrCreateTemplate(word, keyMap) ?: return@mapNotNull null
                
                val tLen = getPathLength(template.rawPoints)
                val ratio = tLen / inputLength
                
                val maxRatio = if (inputLength < 150f) 1.5f else 5.0f
                if (ratio > maxRatio || ratio < 0.4f) return@mapNotNull null

                // FIX: Check ALL cached properties, not just sampledPoints
                // Old cache entries may have sampledPoints but not directionVectors
                if (template.sampledPoints == null || template.normalizedPoints == null || template.directionVectors == null || template.sampledWeights == null) {
                    // Use new sampler that handles weights
                    val (pts, wts) = sampleTemplate(template.rawPoints, template.rawWeights, SAMPLE_POINTS)
                    template.sampledPoints = pts
                    template.sampledWeights = wts
                    
                    template.normalizedPoints = normalizePath(template.sampledPoints!!)
                    template.directionVectors = calculateDirectionVectors(template.sampledPoints!!)
                }
                
                // These are now guaranteed to be non-null
                val shapeScore = calculateShapeScore(normalizedInput, template.normalizedPoints!!)
                // Pass weights to location scorer
                val locScore = calculateLocationScore(sampledInput, template.sampledPoints!!, template.sampledWeights)
                val dirScore = calculateDirectionScore(inputDirections, template.directionVectors!!)


                val templateTurns = detectTurns(template.directionVectors!!)
                val turnScore = calculateTurnScore(inputTurns, templateTurns)

                // NEW: Path key matching score - penalizes words where path doesn't match
                // This distinguishes "awake" (path goes through w) from "awesome" (path would need s)
                val pathKeyScore = calculatePathKeyScore(pathKeys, word)

                // =======================================================================
                // NEW: DWELL TIME SCORING
                // Calculate how much the user's dwell times match this word's letters.
                // If user lingered on "U", words containing "U" get boosted.
                // =======================================================================
                val dwellBoost = calculateDwellBoost(word, keyDwellTimes)
                
                // =======================================================================
                // NEW: CONTEXT & GRAMMAR BOOST (Ported from Shape Algorithm)
                // Applies n-gram and POS tagging logic to the precise algorithm
                // =======================================================================
                val nGramBoost = getContextBoost(word)
                val grammarBoost = getGrammarBoost(word)
                val totalContextBoost = (nGramBoost * grammarBoost).coerceAtMost(2.5f)
                
                // =======================================================================
                // END DWELL & CONTEXT
                // =======================================================================

                // LENGTH MISMATCH PENALTY
                val turnBasedLen = (inputTurns.size + 2).coerceAtLeast(2)
                val expectedLen = minOf(pathKeys.size, turnBasedLen).coerceAtLeast(2)
                val lengthPenalty = (word.length - expectedLen).coerceAtLeast(0) * 0.5f

                // SHORT WORD SCORING: For words ≤5 letters, use pathKey-dominant
                // formula. Short swipes lack geometry for shape/turn to be reliable.
                // pathKeyScore at 1.0 makes intermediate key mismatches (past vs part,
                // 2.0 penalty for s≠r) strongly decisive over location/direction noise.
                val integrationScore = if (word.length <= 5) {
                    (locScore * 0.3f) +
                    (dirScore * 0.15f) +
                    (pathKeyScore * 1.0f) +
                    lengthPenalty
                } else {
                    (shapeScore * SHAPE_WEIGHT) +
                    (locScore * LOCATION_WEIGHT) +
                    (dirScore * DIRECTION_WEIGHT) +
                    (turnScore * TURN_WEIGHT) +
                    (pathKeyScore * 0.8f) +
                    lengthPenalty
                }
                
                val rank = template.rank
                val freqBonus = 1.0f / (1.0f + 0.3f * ln((rank + 1).toFloat()))
                
                var userBoost = 1.0f
                
                val hasEndDouble = word.length >= 3 && 
                    word.last().lowercaseChar() == word[word.length - 2].lowercaseChar()
                
                if (hasEndDouble && isDwellingAtEnd) {
                    userBoost *= (1.10f + dwellScore * 0.15f)
                }
                
                if (startKey != null && word.startsWith(startKey, ignoreCase = true)) userBoost *= 1.15f
                if (endKey != null && word.endsWith(endKey, ignoreCase = true)) {
                    userBoost *= 1.3f
                } else if (endKey != null) {
                    val endChar = endKey.first().lowercaseChar()
                    val wordEnd = word.last().lowercaseChar()
                    if (!areKeysAdjacent(endChar, wordEnd)) {
                        userBoost *= 0.7f
                    }
                }
                if (word.length >= 6) userBoost *= 1.15f

                // =======================================================================
                // APOSTROPHE VARIANT BOOST
                // If user has a custom word with apostrophe (don't) and we're matching
                // the base form (dont), boost the apostrophe version significantly.
                // This makes swiping "dont" return "don't" if learned.
                // =======================================================================
                val wordWithApostrophe = findApostropheVariant(word)
                if (wordWithApostrophe != null) {
                    userBoost *= 1.5f  // Strong boost for apostrophe variants

                }
                // =======================================================================
                // END BLOCK: APOSTROPHE VARIANT BOOST
                // =======================================================================

                // Apply Context Boost
                userBoost *= totalContextBoost

                // Apply dwell boost - words matching user's lingered keys score better
                userBoost *= dwellBoost

                // CAP: Prevent wild boost swings (0.28 to 2.35 observed) that make
                // the winner random when geometry is near-identical (what vs wheat).
                userBoost = userBoost.coerceIn(0.5f, 2.0f)

                var finalScore = (integrationScore * (1.0f - 0.5f * freqBonus)) / userBoost

                // APPLY PENALTY
                val penaltyEnd = temporaryPenalties[word] ?: 0L
                if (System.currentTimeMillis() < penaltyEnd) {
                    finalScore *= 10.0f // Massive penalty to push it to bottom
                }

                Pair(word, finalScore)
            }
        
        // =======================================================================
        // COMMON WORD PROMOTION: If a top-200 word appears anywhere in the top 5
        // candidates and the current winner is much rarer (rank 10x+ worse),
        // promote the common word to #1. This handles noisy pathKeyScore giving
        // "wheat" (rank 5953) a big geometry advantage over "what" (rank 56)
        // despite identical swipe paths.
        // =======================================================================
        // =======================================================================
        // VALIDATED PREVIEW CACHE: Use the cached preview result ONLY if its top
        // word also appears in the full decode's top-5 candidates. This catches
        // bad previews from incomplete paths (e.g. fast "past" → "power") while
        // keeping the preview's superior accuracy for words it gets right.
        // =======================================================================
        val previewAge = System.currentTimeMillis() - lastPreviewTimestamp
        val pathCoverage = if (inputLength > 0f) lastPreviewPathLength / inputLength else 0f
        if (lastPreviewWords.isNotEmpty() && previewAge < 500L && pathCoverage >= 0.90f && inputLength > 200f) {

            return lastPreviewWords.map { word ->
                val apostropheVariant = findApostropheVariant(word)
                val base = apostropheVariant ?: word
                getDisplayForm(base)
            }
        } else if (lastPreviewWords.isNotEmpty() && previewAge < 500L) {

        }

        val allSorted = scored.sortedBy { it.second }.distinctBy { it.first }
        
        // COMMON WORD RESCUE: Find the most common word (by rank) in ALL scored
        // candidates, not just top 5. If it's top-200 and the winner is 10x rarer,
        // inject it into the results.
        val topFive = allSorted.take(5).toMutableList()
        val winner = topFive.firstOrNull()
        val winnerRank = if (winner != null) getWordRank(winner.first) else 0
        
        // Search full list for ultra-common words not already in top 5,
        // but only if they're similar length to the winner (within 1 letter)
        val winnerLen = winner?.first?.length ?: 0
        val commonRescue = allSorted.firstOrNull { candidate ->
            val r = getWordRank(candidate.first)
            val lenOk = kotlin.math.abs(candidate.first.length - winnerLen) <= 1
            r < 200 && lenOk && !topFive.any { it.first == candidate.first }
        }
        if (commonRescue != null) {
            topFive.add(commonRescue)
        }
        
        // Now promote: if a top-200 word exists and winner is much rarer, swap
        val tiebroken = if (topFive.size >= 2) {
            val top = topFive.first()
            val topRank2 = getWordRank(top.first)
            val commonCandidate = topFive.minByOrNull { getWordRank(it.first) }
            val commonRank = if (commonCandidate != null) getWordRank(commonCandidate.first) else Int.MAX_VALUE
            
            val lenDiff = kotlin.math.abs(commonCandidate?.first?.length?.minus(top.first.length) ?: 99)
            if (commonCandidate != null && commonRank < 200 && topRank2 > commonRank * 10 && commonCandidate.first != top.first && lenDiff <= 1) {

                val mutable = topFive.toMutableList()
                mutable.remove(commonCandidate)
                mutable.add(0, commonCandidate)
                mutable
            } else topFive
        } else topFive

        // =======================================================================
        // POST-PROCESS: Apply display forms and apostrophe variants
        // =======================================================================
        val results = tiebroken.take(3).map { it.first }
        
        return results.map { word ->
            val apostropheVariant = findApostropheVariant(word)
            val base = apostropheVariant ?: word
            // FIX: Apply display form to ensure proper capitalization (Katsuya, iPhone, etc.)
            getDisplayForm(base)
        }
    }

    // =================================================================================
    // END BLOCK: decodeSwipeWithDwell
    // =================================================================================

    // =================================================================================
    // FUNCTION: calculateDwellBoost
    // SUMMARY: Calculates a boost factor for a word based on how well it matches
    //          the user's key dwell times. Words containing keys the user lingered
    //          on get a higher boost (lower score = better in this system, so we
    //          return values > 1.0 to boost, < 1.0 to penalize).
    //          
    // EXAMPLE: User types "for" path but lingers on "U" for 150ms
    //          - "for" has no U -> dwellBoost = 1.0 (neutral)
    //          - "four" has U -> dwellBoost = 1.2 (boosted, wins!)
    // =================================================================================
    private fun calculateDwellBoost(word: String, keyDwellTimes: Map<String, Long>): Float {
        if (keyDwellTimes.isEmpty()) return 1.0f
        
        var boost = 1.0f
        val wordLower = word.lowercase()
        
        // Find keys with significant dwell time
        val significantDwells = keyDwellTimes.filter { it.value > DWELL_THRESHOLD_MS }
        
        for ((key, dwellMs) in significantDwells) {
            val keyChar = key.lowercase().firstOrNull() ?: continue
            
            // Calculate boost based on dwell time
            // 100ms = small boost (1.05), 200ms = medium (1.15), 300ms+ = strong (1.25)
            val dwellFactor = when {
                dwellMs > 300 -> 1.25f
                dwellMs > 200 -> 1.15f
                dwellMs > 150 -> 1.10f
                dwellMs > 100 -> 1.05f
                else -> 1.02f
            }
            
            if (wordLower.contains(keyChar)) {
                // Word contains this lingered-on key - BOOST it
                boost *= dwellFactor

            } else {
                // Word does NOT contain this lingered-on key - slight penalty
                // This helps "four" beat "for" when user lingered on "u"
                val penalty = 1.0f / (dwellFactor * 0.5f + 0.5f)  // Gentler penalty
                boost *= penalty

            }
        }
        
        return boost.coerceIn(0.5f, 2.0f)  // Clamp to reasonable range
    }
    // =================================================================================
    // END BLOCK: calculateDwellBoost
    // =================================================================================

    // =================================================================================
    // FUNCTION: findApostropheVariant
    // SUMMARY: Checks if a user-learned word with apostrophe exists for this base word.
    //          E.g., "dont" -> "don't", "wont" -> "won't", "im" -> "i'm"
    //          Returns the apostrophe variant if found in customWords, null otherwise.
    // =================================================================================
    private fun findApostropheVariant(baseWord: String): String? {
        val lower = baseWord.lowercase(java.util.Locale.ROOT)
        
        // Common contraction patterns
        val patterns = listOf(
            // n't contractions
            Pair("nt$", "n't"),      // dont -> don't, wont -> won't, cant -> can't
            // 'm contractions  
            Pair("^im$", "i'm"),     // im -> i'm
            // 'll contractions
            Pair("ll$", "'ll"),      // well could match, so be careful
            // 're contractions
            Pair("re$", "'re"),      // youre -> you're, were -> we're
            // 've contractions
            Pair("ve$", "'ve"),      // wouldve -> would've
            // 's contractions
            Pair("s$", "'s"),        // its -> it's, thats -> that's
        )
        
        // Check common contractions first
        for ((pattern, replacement) in patterns) {
            val regex = Regex(pattern)
            if (regex.containsMatchIn(lower)) {
                val variant = lower.replace(regex, replacement)
                if (customWords.contains(variant)) {
                    return variant
                }
            }
        }
        
        // Also check if any custom word without apostrophe matches this word
        for (customWord in customWords) {
            if (customWord.contains("'")) {
                val withoutApostrophe = customWord.replace("'", "")
                if (withoutApostrophe == lower) {
                    return customWord
                }
            }
        }
        
        return null
    }
    // =================================================================================
    // END BLOCK: calculateDwellBoost
    // =================================================================================

    // =================================================================================
    // FUNCTION: decodeSwipePreview (FAST - for live updates)
    // SUMMARY: Lightweight version of decodeSwipe for real-time preview during swiping.
    //          Uses fewer candidates and simpler scoring for speed.
    //          Returns top 3 predictions based on current path (may be incomplete).
    // =================================================================================
    fun decodeSwipePreview(swipePath: List<PointF>, keyMap: Map<String, PointF>): List<String> {
        if (swipePath.size < 5 || keyMap.isEmpty()) return emptyList()
        if (wordList.isEmpty()) return emptyList()

        val keyMapHash = keyMap.hashCode()
        if (keyMapHash != lastKeyMapHash) {
            templateCache.clear()
            lastKeyMapHash = keyMapHash
        }

        val inputLength = getPathLength(swipePath)
        if (inputLength < 20f) return emptyList()

        val sampledInput = samplePath(swipePath, SAMPLE_POINTS)
        val inputDirections = calculateDirectionVectors(sampledInput)

        val startPoint = sampledInput.first()
        val endPoint = sampledInput.last()

        val startKey = findClosestKey(startPoint, keyMap)
        val endKey = findClosestKey(endPoint, keyMap)

        // Extract path keys for intermediate matching (fewer samples for speed)
        val pathKeys = extractPathKeys(sampledInput, keyMap, 6)

        // DEBUG: Log path keys for live preview
        if (pathKeys.isNotEmpty()) {

        }

        // FAST CANDIDATE COLLECTION - fewer candidates for speed
        val candidates = HashSet<String>()

        // 1. Neighbor Search (scaled for keyboard size)
        val scale = getKeyboardScale(keyMap)
        val nearbyStart = findNearbyKeys(startPoint, keyMap, 80f * scale)
        val nearbyEnd = findNearbyKeys(endPoint, keyMap, 80f * scale)

        for (s in nearbyStart) {
            for (e in nearbyEnd) {
                wordsByFirstLastLetter["${s}${e}"]?.let { candidates.addAll(it) }
            }
        }

        // 2. PREFIX INJECTION (widened to include all nearby start keys)
        for (sk in nearbyStart) {
            val c = sk.firstOrNull() ?: continue
            wordsByFirstLetter[c]?.let { words ->
                candidates.addAll(words.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(15))
            }
        }
        // 3. COMMON WORDS INJECTION: Always include top common words matching nearby keys
        //    This ensures "what", "when", "where" etc. aren't missed by start key drift
        for (sk in nearbyStart) {
            candidates.addAll(commonWordsCache.filter { it.startsWith(sk, ignoreCase = true) }.take(10))
        }
        if (pathKeys.size >= 2) {
            val secondKey = pathKeys.getOrNull(1)?.firstOrNull()?.lowercaseChar()
            if (secondKey != null && startKey != null) {
                wordsByFirstLetter[startKey.first()]?.let { words ->
                    val matchingWords = words.filter { word ->
                        word.length >= 2 && word.drop(1).contains(secondKey)
                    }.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(20)
                    candidates.addAll(matchingWords)
                }
            }
        }
        // FAST SCORING - simplified for speed
        val scored = candidates
            .filter { !isWordBlocked(it) && it.length >= MIN_WORD_LENGTH }
            .sortedWith(compareByDescending<String> { userFrequencyMap[it] ?: 0 }.thenBy { getWordRank(it) })
            .take(50)  // Fewer candidates for speed
            .mapNotNull { word ->
                val template = getOrCreateTemplate(word, keyMap) ?: return@mapNotNull null

                // Quick length filter
                val tLen = getPathLength(template.rawPoints)
                val ratio = tLen / inputLength
                if (ratio > 3.0f || ratio < 0.3f) return@mapNotNull null


                if (template.sampledPoints == null || template.normalizedPoints == null || template.directionVectors == null) {

                    template.sampledPoints = samplePath(template.rawPoints, SAMPLE_POINTS)
                    template.normalizedPoints = normalizePath(template.sampledPoints!!)
                    template.directionVectors = calculateDirectionVectors(template.sampledPoints!!)
                }

                // SIMPLIFIED SCORING - location, direction, and path key matching for speed
                val locScore = calculateLocationScore(sampledInput, template.sampledPoints!!)
                val dirScore = calculateDirectionScore(inputDirections, template.directionVectors!!)

                // Add path key score for better intermediate key matching
                val pathKeyScore = calculatePathKeyScore(pathKeys, word)

                val integrationScore = locScore * 0.4f + dirScore * 0.2f + pathKeyScore * 0.6f

                // Basic boosts
                val rank = template.rank
                val freqBonus = 1.0f / (1.0f + 0.1f * ln((rank + 1).toFloat()))

                var boost = 1.0f
                if (startKey != null && word.startsWith(startKey, ignoreCase = true)) boost *= 1.2f
                if (endKey != null && word.endsWith(endKey, ignoreCase = true)) boost *= 1.2f
                if ((userFrequencyMap[word] ?: 0) > 0) boost *= 1.3f

                val finalScore = (integrationScore * (1.0f - 0.3f * freqBonus)) / boost
                Pair(word, finalScore)
            }

// =======================================================================
        // POST-PROCESS: Apply display forms and apostrophe variants
        // 1. First check for apostrophe variant (cant -> can't)
        // 2. Then apply display form for proper capitalization (droidos -> DroidOS)
        // =======================================================================
        val results = scored.sortedBy { it.second }.distinctBy { it.first }.take(3).map { it.first }
        
        val finalResults = results.map { word ->
            val withApostrophe = findApostropheVariant(word)
            val baseWord = withApostrophe ?: word
            getDisplayForm(baseWord)
        }
        
        // Cache preview result for use by final decode
        lastPreviewWords = finalResults
        lastPreviewTimestamp = System.currentTimeMillis()
        lastPreviewPathLength = inputLength
        
        return finalResults
        // =======================================================================
        // END BLOCK: Display form application
        // =======================================================================
    }
    // =================================================================================
    // END BLOCK: decodeSwipeWithDwellPreview
    // =================================================================================    // =================================================================================
    // FUNCTION: findClosestKey
    // SUMMARY: Finds the single closest key to a point. Fast O(n) where n = key count.
    // =================================================================================
    private fun findClosestKey(point: PointF, keyMap: Map<String, PointF>): String? {
        var closestKey: String? = null
        var closestDist = Float.MAX_VALUE
        
        for ((key, pos) in keyMap) {
            if (key.length != 1 || !Character.isLetter(key[0])) continue
            val dist = hypot(point.x - pos.x, point.y - pos.y)
            if (dist < closestDist) {
                closestDist = dist
                closestKey = key
            }
        }
        return closestKey
    }
    // =================================================================================
    // END BLOCK: findClosestKey
    // =================================================================================

    // =================================================================================
    // FUNCTION: findNearbyKeys
    // SUMMARY: Finds all letter keys within a radius of a point.
    // =================================================================================
    private fun findNearbyKeys(point: PointF, keyMap: Map<String, PointF>, radius: Float): List<String> {
        return keyMap.entries
            .filter { (key, pos) -> 
                key.length == 1 && Character.isLetter(key[0]) &&
                hypot(point.x - pos.x, point.y - pos.y) <= radius
            }
            .map { it.key }
    }
    // =================================================================================
    // END BLOCK: findNearbyKeys
    // =================================================================================



    // =================================================================================
    // FUNCTION: extractPathKeys (v4 - Sharp Turns Only, No Sampling)
    // SUMMARY: Extracts ONLY the key waypoints: Start, Sharp Turns, and End.
    //          Does NOT sample intermediate points (which creates noise).
    //          A sharp turn is where direction changes significantly (dot < 0.4).
    //          This prevents picking up 's' when swiping diagonally from 'a' to 'k'.
    //
    //          Example: "awake" swipe path has sharp turns at w, a, k
    //          Result: a→w→a→k→e (not a→w→a→s→k→e)
    // =================================================================================
    private fun extractPathKeys(path: List<PointF>, keyMap: Map<String, PointF>, maxKeys: Int): List<String> {
        if (path.size < 3) return emptyList()

        val keys = ArrayList<String>()

        // 1. Always add Start Key
        val startKey = findClosestKey(path.first(), keyMap)
        if (startKey != null) {
            keys.add(startKey.lowercase())
        }

        // 2. Find ONLY sharp turns (significant direction changes)
        // Use a larger window (5 points) to avoid noise from jitter
        val windowSize = 5
        var lastTurnIdx = 0

        for (i in windowSize until path.size - windowSize) {
            // Vector from 5 points ago to current point
            val p1 = path[i - windowSize]
            val p2 = path[i]
            val p3 = path[i + windowSize]

            val v1x = p2.x - p1.x
            val v1y = p2.y - p1.y
            val v2x = p3.x - p2.x
            val v2y = p3.y - p2.y

            val len1 = kotlin.math.hypot(v1x, v1y)
            val len2 = kotlin.math.hypot(v2x, v2y)

            // Need significant movement to count as a direction
            if (len1 > 15f && len2 > 15f) {
                val dot = (v1x * v2x + v1y * v2y) / (len1 * len2)

                                    // SHARP turn only: dot < 0.6 means angle > ~53 degrees
                                    // Relaxed from 0.4 to catch 'k' in 'awake' and 'x' in 'expect'
                                    if (dot < 0.6f) {
                                        // Minimum distance from last turn to avoid duplicates
                                        if (i - lastTurnIdx > windowSize * 2) {                        val key = findClosestKey(p2, keyMap)?.lowercase()
                        if (key != null && (keys.isEmpty() || keys.last() != key)) {
                            keys.add(key)
                            lastTurnIdx = i
                        }
                    }
                }
            }
        }

        // 2b. MIDPOINT FALLBACK: If turn detection found no intermediate keys
        //     (only start key so far), sample evenly-spaced points along the path.
        //     This catches short words like "past" where a→s is too small for turn detection.
        if (keys.size <= 1 && path.size > 10) {
            val numMidpoints = minOf(3, path.size / 4)
            for (i in 1..numMidpoints) {
                val idx = (path.size * i) / (numMidpoints + 1)
                val midKey = findClosestKey(path[idx], keyMap)?.lowercase()
                if (midKey != null && (keys.isEmpty() || keys.last() != midKey)) {
                    keys.add(midKey)
                }
            }
        }

        // 3. Always add End Key
        val endKey = findClosestKey(path.last(), keyMap)?.lowercase()
        if (endKey != null && (keys.isEmpty() || keys.last() != endKey)) {
            keys.add(endKey)
        }

        // DEBUG: Log extracted keys


        return keys.take(maxKeys)
    }
    // =================================================================================
    // END BLOCK: extractPathKeys
    // =================================================================================


    // =================================================================================
    // FUNCTION: calculatePathKeyScore (v6 - Fuzzy Neighbor Matching)
    // SUMMARY: Penalizes words that don't match path keys.
    //          UPDATED: Now uses areKeysAdjacent() to forgive "fat finger" errors.
    //          e.g. Path has 'g', Word has 'h' -> Match (because g-h are neighbors)
    // =================================================================================
    private fun calculatePathKeyScore(pathKeys: List<String>, word: String): Float {
        if (pathKeys.isEmpty()) return 0f
        
        val wordChars = word.lowercase()
        var penalty = 0f
        
        // CRITICAL: Check if the FIRST path keys match the FIRST word letters
        val firstPathKey = pathKeys.firstOrNull()?.firstOrNull()
        val firstWordChar = wordChars.firstOrNull()
        
        if (firstPathKey != null && firstWordChar != null && firstPathKey != firstWordChar) {
            // First key mismatch - Check adjacency (Fuzzy Match)
            if (areKeysAdjacent(firstPathKey, firstWordChar)) {
                 penalty += 1.0f // Mild penalty for neighbor start
            } else {
                 penalty += 5.0f // Big penalty for wrong start
            }
        }
        
        // Check SECOND path key
        if (pathKeys.size >= 2 && wordChars.length >= 2) {
            val secondPathKey = pathKeys[1].firstOrNull()
            val secondWordChar = wordChars[1]
            
            if (secondPathKey != null && secondPathKey != secondWordChar) {
                // Fuzzy check for second key
                if (areKeysAdjacent(secondPathKey, secondWordChar)) {
                    penalty += 0.5f 
                } else {
                    penalty += 4.0f
                }
            }
        }
        
        // Check THIRD path key
        if (pathKeys.size >= 3 && wordChars.length >= 3) {
            val thirdPathKey = pathKeys[2].firstOrNull()
            val thirdWordChar = wordChars[2]
            
            if (thirdPathKey != null && thirdWordChar != null && thirdPathKey != thirdWordChar) {
                if (areKeysAdjacent(thirdPathKey, thirdWordChar)) {
                    penalty += 0.2f
                } else {
                    penalty += 2.0f
                }
            }
        }
        
        // Subsequence matching (rest of the path)
        var pathIdx = 0
        var wordIdx = 0
        var matchedKeys = 0
        
        while (pathIdx < pathKeys.size && wordIdx < wordChars.length) {
            val pKey = pathKeys[pathIdx].firstOrNull() ?: continue
            val wChar = wordChars[wordIdx]
            
            if (pKey == wChar) {
                matchedKeys++
                pathIdx++
                wordIdx++
            } else {
                wordIdx++
            }
        }
        
        // Penalty for unmatched path keys
        val unmatchedKeys = pathKeys.size - matchedKeys
        penalty += unmatchedKeys * 2.0f 
        
        // Length penalty
        if (wordChars.length > pathKeys.size * 2.5) {
            penalty += 0.5f
        }
        
        return penalty
    }

    // =================================================================================
    // FUNCTION: areKeysAdjacent
    // SUMMARY: Checks if two keys are adjacent on a QWERTY keyboard.
    //          Used for typo tolerance - adjacent mismatches are less severe.
    // =================================================================================
    private fun areKeysAdjacent(key1: Char, key2: Char): Boolean {
        val adjacencyMap = mapOf(
            'q' to setOf('w', 'a'),
            'w' to setOf('q', 'e', 'a', 's'),
            'e' to setOf('w', 'r', 's', 'd'),
            'r' to setOf('e', 't', 'd', 'f'),
            't' to setOf('r', 'y', 'f', 'g'),
            'y' to setOf('t', 'u', 'g', 'h'),
            'u' to setOf('y', 'i', 'h', 'j'),
            'i' to setOf('u', 'o', 'j', 'k'),
            'o' to setOf('i', 'p', 'k', 'l'),
            'p' to setOf('o', 'l'),
            'a' to setOf('q', 'w', 's', 'z'),
            's' to setOf('a', 'w', 'e', 'd', 'z', 'x'),
            'd' to setOf('s', 'e', 'r', 'f', 'x', 'c'),
            'f' to setOf('d', 'r', 't', 'g', 'c', 'v'),
            'g' to setOf('f', 't', 'y', 'h', 'v', 'b'),
            'h' to setOf('g', 'y', 'u', 'j', 'b', 'n'),
            'j' to setOf('h', 'u', 'i', 'k', 'n', 'm'),
            'k' to setOf('j', 'i', 'o', 'l', 'm'),
            'l' to setOf('k', 'o', 'p'),
            'z' to setOf('a', 's', 'x'),
            'x' to setOf('z', 's', 'd', 'c'),
            'c' to setOf('x', 'd', 'f', 'v'),
            'v' to setOf('c', 'f', 'g', 'b'),
            'b' to setOf('v', 'g', 'h', 'n'),
            'n' to setOf('b', 'h', 'j', 'm'),
            'm' to setOf('n', 'j', 'k')
        )
        
        val adjacent1 = adjacencyMap[key1.lowercaseChar()] ?: emptySet()
        return key2.lowercaseChar() in adjacent1
    }
    // =================================================================================
    // END BLOCK: areKeysAdjacent
    // =================================================================================
    // =================================================================================
    // END BLOCK: calculatePathKeyScore
    // =================================================================================

    // =================================================================================
    // FUNCTION: getOrCreateTemplate
    // SUMMARY: Gets or creates a word template with key positions. Returns null if any
    //          character in the word is missing from the keyMap. Logs first failure per
    //          batch to avoid log spam while still providing diagnostic info.
    // =================================================================================

    // =================================================================================
    // FUNCTION: getOrCreateTemplate (With Micro-Loops for Double Letters)
    // =================================================================================
    private fun getOrCreateTemplate(word: String, keyMap: Map<String, PointF>): WordTemplate? {
        templateCache[word]?.let { return it }

        val rawPoints = ArrayList<PointF>()
        val rawWeights = ArrayList<Float>()
        var lastKeyPos: PointF? = null
        
        for (char in word) {
            val keyPos = keyMap[char.toString().uppercase()] ?: keyMap[char.toString().lowercase()] ?: return null
            
            // Consonant Anchoring: Vowels = 0.6, Consonants = 1.0
            val weight = if (isVowel(char)) 0.6f else 1.0f
            
            // NEW: Apply learned user offset (Fat Finger Correction)
            val offset = keyOffsets[char.toString().lowercase()] ?: PointF(0f, 0f)
            val adjustedX = keyPos.x + offset.x
            val adjustedY = keyPos.y + offset.y

            // DOUBLE LETTER LOGIC:
            if (lastKeyPos != null && keyPos.x == lastKeyPos.x && keyPos.y == lastKeyPos.y) {
                rawPoints.add(PointF(adjustedX + 15f, adjustedY + 15f))
                rawWeights.add(weight)
            }
            
            rawPoints.add(PointF(adjustedX, adjustedY))
            rawWeights.add(weight)
            
            lastKeyPos = keyPos

            if (lastKeyPos != null && keyPos.x == lastKeyPos.x && keyPos.y == lastKeyPos.y) {
                rawPoints.add(PointF(keyPos.x + 15f, keyPos.y + 15f))
                rawWeights.add(weight)
            }
            
            rawPoints.add(PointF(keyPos.x, keyPos.y))
            rawWeights.add(weight)
            
            lastKeyPos = keyPos
        }

        if (rawPoints.size < 2) return null

        // (Debug log removed to prevent spam)

        val t = WordTemplate(word, getWordRank(word), rawPoints, rawWeights)
        templateCache[word] = t
        return t
    }

    // =================================================================================
    // END BLOCK: getOrCreateTemplate
    // =================================================================================

/**
     * Calculates the total absolute length of a path in pixels.
     */
    private fun getPathLength(points: List<PointF>): Float {
        if (points.size < 2) return 0f
        var length = 0f
        for (i in 0 until points.size - 1) {
            length += hypot(points[i+1].x - points[i].x, points[i+1].y - points[i].y)
        }
        return length
    }
    /**
     * Uniformly sample N points along a path.
     * This makes paths of different lengths comparable.
     */
    private fun samplePath(path: List<PointF>, numSamples: Int): List<PointF> {
        // [Existing samplePath code...]
        if (path.size < 2) return path
        if (path.size == numSamples) return path
        
        // Calculate total path length
        var totalLength = 0f
        for (i in 1 until path.size) {
            totalLength += hypot(path[i].x - path[i-1].x, path[i].y - path[i-1].y)
        }
        
        if (totalLength < 0.001f) {
            // Path is essentially a point - return copies of first point
            return List(numSamples) { PointF(path[0].x, path[0].y) }
        }
        
        val segmentLength = totalLength / (numSamples - 1)
        val sampled = ArrayList<PointF>(numSamples)
        sampled.add(PointF(path[0].x, path[0].y))
        
        var currentDist = 0f
        var pathIndex = 0
        var targetDist = segmentLength
        
        while (sampled.size < numSamples - 1 && pathIndex < path.size - 1) {
            val p1 = path[pathIndex]
            val p2 = path[pathIndex + 1]
            val segLen = hypot(p2.x - p1.x, p2.y - p1.y)
            
            while (currentDist + segLen >= targetDist && sampled.size < numSamples - 1) {
                val ratio = (targetDist - currentDist) / segLen
                val x = p1.x + ratio * (p2.x - p1.x)
                val y = p1.y + ratio * (p2.y - p1.y)
                sampled.add(PointF(x, y))
                targetDist += segmentLength
            }
            
            currentDist += segLen
            pathIndex++
        }
        
        // Ensure we have exactly numSamples by adding the last point
        while (sampled.size < numSamples) {
            sampled.add(PointF(path.last().x, path.last().y))
        }
        
        return sampled
    }

    /**
     * Samples both Points AND Weights uniformly.
     */
    private fun sampleTemplate(path: List<PointF>, weights: List<Float>, numSamples: Int): Pair<List<PointF>, List<Float>> {
        if (path.size < 2 || weights.size != path.size) return Pair(path, weights)
        
        var totalLength = 0f
        for (i in 1 until path.size) {
            totalLength += hypot(path[i].x - path[i-1].x, path[i].y - path[i-1].y)
        }
        
        if (totalLength < 0.001f) {
            return Pair(
                List(numSamples) { PointF(path[0].x, path[0].y) },
                List(numSamples) { weights[0] }
            )
        }
        
        val segmentLength = totalLength / (numSamples - 1)
        val sampledPoints = ArrayList<PointF>(numSamples)
        val sampledWeights = ArrayList<Float>(numSamples)
        
        sampledPoints.add(PointF(path[0].x, path[0].y))
        sampledWeights.add(weights[0])
        
        var currentDist = 0f
        var pathIndex = 0
        var targetDist = segmentLength
        
        while (sampledPoints.size < numSamples - 1 && pathIndex < path.size - 1) {
            val p1 = path[pathIndex]
            val p2 = path[pathIndex + 1]
            val w1 = weights[pathIndex]
            val w2 = weights[pathIndex + 1]
            val segLen = hypot(p2.x - p1.x, p2.y - p1.y)
            
            while (currentDist + segLen >= targetDist && sampledPoints.size < numSamples - 1) {
                val ratio = (targetDist - currentDist) / segLen
                
                // Interpolate Point
                val x = p1.x + ratio * (p2.x - p1.x)
                val y = p1.y + ratio * (p2.y - p1.y)
                sampledPoints.add(PointF(x, y))
                
                // Interpolate Weight
                val w = w1 + ratio * (w2 - w1)
                sampledWeights.add(w)
                
                targetDist += segmentLength
            }
            
            currentDist += segLen
            pathIndex++
        }
        
        while (sampledPoints.size < numSamples) {
            sampledPoints.add(PointF(path.last().x, path.last().y))
            sampledWeights.add(weights.last())
        }
        
        return Pair(sampledPoints, sampledWeights)
    }
    
    /**
     * Normalize a path to fit within a square of size NORMALIZATION_SIZE.
     * This removes scale and translation differences for shape comparison.
     */
    private fun normalizePath(path: List<PointF>): List<PointF> {
        if (path.isEmpty()) return path
        
        // Find bounding box
        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE
        
        for (pt in path) {
            minX = min(minX, pt.x)
            maxX = max(maxX, pt.x)
            minY = min(minY, pt.y)
            maxY = max(maxY, pt.y)
        }
        
        val width = maxX - minX
        val height = maxY - minY
        val maxDim = max(width, height)
        
        if (maxDim < 0.001f) {
            // All points are the same - return centered points
            return path.map { PointF(NORMALIZATION_SIZE / 2, NORMALIZATION_SIZE / 2) }
        }
        
        // Scale to NORMALIZATION_SIZE and center at origin
        val scale = NORMALIZATION_SIZE / maxDim
        val centerX = (minX + maxX) / 2
        val centerY = (minY + maxY) / 2
        
        return path.map { pt ->
            PointF(
                (pt.x - centerX) * scale + NORMALIZATION_SIZE / 2,
                (pt.y - centerY) * scale + NORMALIZATION_SIZE / 2
            )
        }
    }
    
    /**
     * Calculate shape score between two normalized paths.
     * Uses average point-to-point distance.
     * Lower score = better match.
     */
    private fun calculateShapeScore(input: List<PointF>, template: List<PointF>): Float {
        if (input.size != template.size) return Float.MAX_VALUE
        
        var totalDist = 0f
        for (i in input.indices) {
            val dx = input[i].x - template[i].x
            val dy = input[i].y - template[i].y
            totalDist += sqrt(dx * dx + dy * dy)
        }
        
        return totalDist / input.size
    }
    
    /**
     * Calculate location score between two paths (absolute positions).
     * Uses average point-to-point distance with position weighting.
     * Points at the beginning and end are weighted more heavily.
     * Lower score = better match.
     */

    /**
     * Calculate location score with Consonant Anchoring support.
     * @param templateWeights List of weights (1.0 for consonants, 0.6 for vowels). Can be null for backwards compat.
     */
    private fun calculateLocationScore(input: List<PointF>, template: List<PointF>, templateWeights: List<Float>? = null): Float {
        var totalDist = 0f
        var totalWeight = 0f
        val size = input.size
        
        for (i in input.indices) {
            val dist = hypot(input[i].x - template[i].x, input[i].y - template[i].y)
            
            // --- ENDPOINT WEIGHTING (Strict) ---
            val posWeight = when {
                i < size * 0.15 -> 3.0f
                i > size * 0.85 -> 5.0f 
                else -> 1.0f
            }
            
            // --- CONSONANT ANCHORING ---
            // Consonants = 1.0, Vowels = 0.6
            // Multiply position weight by character weight
            val charWeight = templateWeights?.getOrElse(i) { 1.0f } ?: 1.0f
            
            val combinedWeight = posWeight * charWeight
            
            totalDist += dist * combinedWeight
            totalWeight += combinedWeight
        }
        return if (totalWeight > 0) totalDist / totalWeight else totalDist
    }



    // =================================================================================
    // NEW: DIRECTION SCORING HELPERS
    // =================================================================================
    

    private fun calculateDirectionVectors(path: List<PointF>): List<PointF> {
        val vectors = ArrayList<PointF>()
        for (i in 0 until path.size - 1) {
            val dx = path[i+1].x - path[i].x
            val dy = path[i+1].y - path[i].y
            val len = hypot(dx, dy)
            if (len > 0.001f) {
                vectors.add(PointF(dx/len, dy/len))
            } else {
                // Return 0,0 for stationary segments (handled by Score function now)
                vectors.add(PointF(0f, 0f))
            }
        }
        return vectors
    }

    // =================================================================================
    // FUNCTION: calculateDirectionScore (Fixed for Double Letters & Pauses)
    // =================================================================================

    // =================================================================================
    // FUNCTION: calculateDirectionScore (Linear + Stationary Skip)
    // =================================================================================
    private fun calculateDirectionScore(input: List<PointF>, template: List<PointF>): Float {
        var totalScore = 0f
        var validPoints = 0
        
        val count = min(input.size, template.size)
        if (count == 0) return 0f

        for (i in 0 until count) {
            val v1 = input[i]
            val v2 = template[i]
            
            // Skip stationary segments (Pause/Tap)
            if ((v2.x == 0f && v2.y == 0f) || (v1.x == 0f && v1.y == 0f)) {
                continue
            }

            // Dot Product: 1.0 = aligned, -1.0 = opposite
            val dot = v1.x * v2.x + v1.y * v2.y
            
            // Penalty: Linear (Robust for complex words like "Either")
            // Reverted from Squared to forgive messy scribbles.
            totalScore += (1.0f - dot)
            validPoints++
        }
        
        return if (validPoints > 0) totalScore / validPoints else 0f
    }

// =================================================================================
    // FUNCTION: detectTurns
    // SUMMARY: Detects significant direction changes (turns/corners) in a path.
    //          Returns a list of (position, angle) pairs where position is 0.0-1.0
    //          along the path and angle is the turn magnitude in radians.
    //          A turn is detected when consecutive direction vectors differ significantly.
    // =================================================================================
// =================================================================================
    // FUNCTION: detectTurns (v2 - Sharp Corner Detection)
    // SUMMARY: Detects significant direction changes with emphasis on SHARP corners.
    //          Sharp corners = abrupt changes over 1-2 points (more intentional)
    //          Rounded corners = gradual changes over many points (less distinctive)
    //          Returns list of (position, sharpness) where sharpness indicates
    //          how abrupt the turn was (higher = sharper).
    // =================================================================================

    // =================================================================================
    // FUNCTION: detectTurns (v3 - Ultra-Sharp Emphasis)
    // =================================================================================
    private fun detectTurns(directions: List<PointF>): List<Pair<Float, Float>> {
        if (directions.size < 3) return emptyList()
        
        val turns = ArrayList<Pair<Float, Float>>()
        
        for (i in 0 until directions.size - 1) {
            val curr = directions[i]
            val next = directions[i + 1]
            
            if ((curr.x == 0f && curr.y == 0f) || (next.x == 0f && next.y == 0f)) continue
            
            // Dot product: 1.0 = straight, 0 = 90deg, -1 = U-turn
            val dot = curr.x * next.x + curr.y * next.y
            
            // ULTRA SHARP CORNER (e.g. "W", "Z", "M")
            // dot < 0.2 means angle > 78 degrees
            if (dot < 0.2f) {
                val position = i.toFloat() / directions.size.toFloat()
                // AMPLIFIED: Scale sharpness by 3.0x (was 1.5x)
                // This makes a sharp corner worth 3 "normal" curves
                val sharpness = (1.0f - dot) * 3.0f  
                turns.add(Pair(position, sharpness))
            }
            // MEDIUM CORNER (Standard curve)
            // dot < 0.6 means angle > 53 degrees
            else if (dot < 0.6f) {
                val position = i.toFloat() / directions.size.toFloat()
                val sharpness = (1.0f - dot) * 0.8f // Reduce weight of soft curves
                turns.add(Pair(position, sharpness))
            }
        }
        
        // Secondary pass for "spread out" turns (U-turns that take 3 points)
        for (i in 0 until directions.size - 3) {
            val curr = directions[i]
            val later = directions[i + 3]
            
            if ((curr.x == 0f && curr.y == 0f) || (later.x == 0f && later.y == 0f)) continue
            
            val dot = curr.x * later.x + curr.y * later.y
            
            if (dot < 0.1f) {  
                val position = (i + 1.5f) / directions.size.toFloat()
                
                val nearbyTurn = turns.any { abs(it.first - position) < 0.08f }
                if (!nearbyTurn) {
                    val sharpness = (1.0f - dot) * 2.0f // Boost U-turns too
                    turns.add(Pair(position, sharpness))
                }
            }
        }
        
        return turns.sortedBy { it.first }
    }

    // =================================================================================
    // END BLOCK: detectTurns
    // =================================================================================
    // =================================================================================
    // END BLOCK: detectTurns
    // =================================================================================

    // =================================================================================
    // FUNCTION: calculateTurnScore
    // SUMMARY: Compares turn patterns between input and template.
    //          Rewards matching turn counts and positions, penalizes mismatches.
    //          Lower score = better match.
    // =================================================================================
// =================================================================================
    // FUNCTION: calculateTurnScore (v2 - Sharp Corner Emphasis)
    // SUMMARY: Compares turn patterns with HEAVY emphasis on matching sharp corners.
    //          - Matching sharp corners = big reward (low score)
    //          - Missing sharp corners = big penalty (high score)
    //          - Turn count matters less than turn positions and sharpness
    // =================================================================================

    // =================================================================================
    // FUNCTION: calculateTurnScore (v3 - Sharp Corner Enforcement)
    // =================================================================================
    private fun calculateTurnScore(inputTurns: List<Pair<Float, Float>>, templateTurns: List<Pair<Float, Float>>): Float {
        if (inputTurns.isEmpty() && templateTurns.isEmpty()) return 0f
        
        var score = 0f
        
        // Filter for "Significant" turns (Sharpness > 1.5, which corresponds to our new 3.0x scale)
        val templateSharpTurns = templateTurns.filter { it.second > 1.5f }
        val inputSharpTurns = inputTurns.filter { it.second > 1.5f }
        
        // HEAVY PENALTY for mismatching sharp corners
        if (templateSharpTurns.isNotEmpty() && inputSharpTurns.isEmpty()) {
            // Template has a sharp corner (like "Android" 'N') but user missed it -> Fatal
            score += 1.5f 
        } else if (templateSharpTurns.isEmpty() && inputSharpTurns.isNotEmpty()) {
            // User made a sharp jerk where none belongs -> Fatal
            score += 1.0f
        }
        
        val usedTemplate = BooleanArray(templateTurns.size)
        var matchedTurns = 0
        var totalSharpnessMatch = 0f
        
        for (inputTurn in inputTurns) {
            var bestMatch = -1
            var bestScore = Float.MAX_VALUE
            
            for (j in templateTurns.indices) {
                if (usedTemplate[j]) continue
                
                val posDist = abs(inputTurn.first - templateTurns[j].first)
                val sharpnessDiff = abs(inputTurn.second - templateTurns[j].second)
                
                // Position tolerance: 20%
                if (posDist < 0.20f) {
                    val matchScore = posDist * 2f + sharpnessDiff * 0.5f
                    if (matchScore < bestScore) {
                        bestScore = matchScore
                        bestMatch = j
                    }
                }
            }
            
            if (bestMatch >= 0) {
                usedTemplate[bestMatch] = true
                matchedTurns++
                totalSharpnessMatch += bestScore
                
                // HUGE REWARD: If both are sharp and matched
                if (inputTurn.second > 1.5f && templateTurns[bestMatch].second > 1.5f) {
                    score -= 0.5f  // Massive bonus for hitting the corner
                }
            } else {
                // Penalty for extra turns in input
                score += 0.2f + inputTurn.second * 0.2f
            }
        }
        
        // Penalty for missing turns in template
        for (j in templateTurns.indices) {
            if (!usedTemplate[j]) {
                score += 0.2f + templateTurns[j].second * 0.2f
            }
        }
        
        if (matchedTurns > 0) {
            score += totalSharpnessMatch / matchedTurns * 0.3f
        }
        
        return max(0f, score)
    }

    // =================================================================================
    // END BLOCK: calculateTurnScore
    // =================================================================================
    // =================================================================================
    // END BLOCK: calculateTurnScore
    // =================================================================================



    /**
     * Find all keys within threshold distance of a point.
     * Returns a Map of Key -> Distance for O(1) lookups.
     */
    private fun findKeysWithDist(pt: PointF, keyMap: Map<String, PointF>, threshold: Float): Map<String, Float> {
        val keys = HashMap<String, Float>()
        for ((key, pos) in keyMap) {
            if (key.length != 1) continue
            val dist = hypot(pt.x - pos.x, pt.y - pos.y)
            if (dist < threshold) {
                // If key exists (e.g. from another case), keep smallest dist
                val existing = keys[key.lowercase()]
                if (existing == null || dist < existing) {
                    keys[key.lowercase()] = dist
                }
            }
        }
        return keys
    }
    
    private fun getWordRank(word: String): Int {
        var current = root
        for (char in word) {
            current = current.children[char] ?: return Int.MAX_VALUE
        }
        return if (current.isEndOfWord) current.rank else Int.MAX_VALUE
    }
    
    // =================================================================================
    // END BLOCK: SHARK2-INSPIRED SWIPE DECODER LOGIC
    // =================================================================================


    /**
     * Blocks a word permanently:
     * 1. Adds to memory.
     * 2. Removes from active lists.
     * 3. Removes from user stats. 
     * 4. Saves to file.
     */

    fun blockWord(context: Context, word: String) {
        val cleanWord = word.trim().lowercase(java.util.Locale.ROOT)
        if (cleanWord.isEmpty()) return

        Thread {
            try {
                synchronized(this) {
                    blockedWords.add(cleanWord)
                    customWords.remove(cleanWord)
                    wordList.remove(cleanWord)
                    
                    if (cleanWord.isNotEmpty()) {
                        wordsByFirstLetter[cleanWord.first()]?.remove(cleanWord)
                        if (cleanWord.length >= 2) {
                            wordsByFirstLastLetter["${cleanWord.first()}${cleanWord.last()}"]?.remove(cleanWord)
                        }
                    }
                    
                    synchronized(userFrequencyMap) {
                        userFrequencyMap.remove(cleanWord)
                    }
                    templateCache.remove(cleanWord)
                    
                    // FIX: Save files inside synchronized block to prevent ConcurrentModification and race conditions
                    saveSetToFile(context, BLOCKED_DICT_FILE, blockedWords)
                    saveSetToFile(context, USER_DICT_FILE, customWords)
                }
                
                saveUserStats(context)


            } catch (e: Exception) {
            }
        }.start()
    }


    // =================================================================================
    // FUNCTION: calculateSwipeSpeed
    // SUMMARY: Calculates average swipe speed in pixels per millisecond.
    // =================================================================================
    fun calculateSwipeSpeed(timedPath: List<TimedPoint>): Float {
        if (timedPath.size < 2) return 0f
        
        var totalLength = 0f
        for (i in 1 until timedPath.size) {
            val prev = timedPath[i - 1]
            val curr = timedPath[i]
            totalLength += hypot(curr.x - prev.x, curr.y - prev.y)
        }
        
        val startTime = timedPath.first().timestamp
        val endTime = timedPath.last().timestamp
        val duration = (endTime - startTime).toFloat()
        
        if (duration <= 0f) return 0f
        
        return totalLength / duration
    }
    // =================================================================================
    // END BLOCK: calculateSwipeSpeed
    // =================================================================================

    // =================================================================================
    // FUNCTION: updateContextModel
    // SUMMARY: Updates the n-gram language model with a newly committed word.
    // =================================================================================
    fun updateContextModel(word: String) {
        val normalizedWord = word.lowercase(Locale.ROOT).trim()
        if (normalizedWord.isEmpty() || normalizedWord.length < 2) return
        
        unigramCounts[normalizedWord] = (unigramCounts[normalizedWord] ?: 0) + 1
        
        lastContextWord?.let { prev ->
            val prevNorm = prev.lowercase(Locale.ROOT)
            val bigramMap = bigramCounts.getOrPut(prevNorm) { HashMap() }
            bigramMap[normalizedWord] = (bigramMap[normalizedWord] ?: 0) + 1
        }
        
        lastContextWord = normalizedWord
    }
    // =================================================================================
    // END BLOCK: updateContextModel
    // =================================================================================

    // =================================================================================
    // FUNCTION: clearContext
    // SUMMARY: Resets the context model's current state at sentence boundaries.
    // =================================================================================
    fun clearContext() {
        lastContextWord = null
    }
    // =================================================================================
    // END BLOCK: clearContext
    // =================================================================================

    // =================================================================================
    // FUNCTION: getContextBoost (private)
    // SUMMARY: Returns a boost factor for a word based on context (previous word).
    // =================================================================================
    private fun getContextBoost(word: String): Float {
        val normalizedWord = word.lowercase(Locale.ROOT)
        if (lastContextWord == null) return 1.0f
        
        val prevWord = lastContextWord!!.lowercase(Locale.ROOT)
        val bigramMap = bigramCounts[prevWord] ?: return 1.0f
        
        val bigramCount = bigramMap[normalizedWord] ?: 0
        if (bigramCount == 0) return 1.0f
        
        val prevTotalCount = bigramMap.values.sum().toFloat().coerceAtLeast(1f)
        val bigramProb = bigramCount / prevTotalCount
        
        return 1.0f + (bigramProb * 0.5f).coerceAtMost(0.5f)
    }
    // =================================================================================
    // END BLOCK: getContextBoost
    // =================================================================================

    // =================================================================================


    // =================================================================================
    // FUNCTION:     // FUNCTION: getGrammarBoost
    // SUMMARY: Returns a multiplier based on grammar rules between previous and current word.
    //          e.g. "I" (Pronoun) -> "run" (Verb) = BOOST
    //          e.g. "The" (Determiner) -> "run" (Verb) = PENALTY
    // =================================================================================
    private fun getGrammarBoost(word: String): Float {
        if (lastContextWord == null) return 1.0f
        
        val prevWord = lastContextWord!!.lowercase(Locale.ROOT)
        val currWord = word.lowercase(Locale.ROOT)
        
        // 1. REPEAT WORD PENALTY (Avoid "the the", "is is")
        if (prevWord == currWord) {
            // Exceptions: "that that", "had had"
            if (prevWord != "that" && prevWord != "had") {
                return 0.2f // Strong Penalty
            }
        }
        
        val prevTag = commonPosTags[prevWord] ?: POSTag.UNKNOWN
        val currTag = commonPosTags[currWord] ?: POSTag.UNKNOWN
        
        // If we don't know the tags, assume neutral
        if (prevTag == POSTag.UNKNOWN && currTag == POSTag.UNKNOWN) return 1.0f

        return when (prevTag) {
            POSTag.PRONOUN -> {
                // NEGATIVE CONTEXT: "He the", "I a" -> IMPOSSIBLE
                if (currTag == POSTag.DETERMINER) 0.1f
                // "I run", "He is" -> High Boost
                else if (currTag == POSTag.VERB) 1.3f 
                // "I very" -> Moderate
                else if (currTag == POSTag.ADVERB) 1.1f
                else 1.0f
            }
            POSTag.DETERMINER -> {
                // NEGATIVE CONTEXT: "The the", "My a" -> IMPOSSIBLE
                if (currTag == POSTag.DETERMINER) 0.1f 
                // NEGATIVE CONTEXT: "The he" -> HIGHLY UNLIKELY
                else if (currTag == POSTag.PRONOUN) 0.2f
                // "The run" (Bad) vs "The car" (Good - assuming Unknowns are often Nouns/Adj)
                else if (currTag == POSTag.VERB) 0.6f // Penalty
                else if (currTag == POSTag.UNKNOWN || currTag == POSTag.ADJECTIVE || currTag == POSTag.NOUN) 1.15f 
                else 1.0f
            }
            POSTag.VERB -> {
                // "Go to", "Look at"
                if (currTag == POSTag.PREPOSITION || currTag == POSTag.DETERMINER) 1.2f 
                // "Is not"
                else if (currTag == POSTag.ADVERB) 1.2f
                else 1.0f
            }
            POSTag.PREPOSITION -> {
                // "To the", "For my"
                if (currTag == POSTag.DETERMINER || currTag == POSTag.PRONOUN) 1.25f 
                else 1.0f
            }
            POSTag.ADVERB -> {
                // "Very good" (Adj), "Not go" (Verb)
                if (currTag == POSTag.ADJECTIVE || currTag == POSTag.VERB) 1.2f 
                else 1.0f
            }
            POSTag.QUESTION -> {
                // "Who is", "What do"
                if (currTag == POSTag.VERB) 1.3f 
                else 1.0f
            }
            else -> 1.0f
        }
    }




    // =================================================================================
    // FUNCTION: decodeSwipeShapeContext (Debug + Grammar + Adjacency Fix)
    // SUMMARY: Gboard-style decoder with Grammar Context, Debug Logging, and Smart End-Keys.
    // =================================================================================
    fun decodeSwipeShapeContext(
        timedPath: List<TimedPoint>, 
        keyMap: Map<String, PointF>
    ): List<SwipeResult> {
        val swipePath = timedPath.map { it.toPointF() }
        
        if (swipePath.size < 3 || keyMap.isEmpty()) return emptyList()
        if (wordList.isEmpty()) {
            loadDefaults()
            return emptyList()
        }

        val keyMapHash = keyMap.hashCode()
        if (keyMapHash != lastKeyMapHash) {
            templateCache.clear()
            lastKeyMapHash = keyMapHash
        }

        val inputLength = getPathLength(swipePath)
        if (inputLength < 10f) return emptyList()

        val sampledInput = samplePath(swipePath, SAMPLE_POINTS)
        if (sampledInput.isEmpty()) return emptyList()
        
        val normalizedInput = normalizePath(sampledInput)
        val inputDirections = calculateDirectionVectors(sampledInput)
        val inputTurns = detectTurns(inputDirections)

        val startPoint = sampledInput.first()
        val endPoint = sampledInput.last()
        val startKey = findClosestKey(startPoint, keyMap)
        val endKey = findClosestKey(endPoint, keyMap)
        
        // DEBUG: Calculate "Letters Swiped" for logcat
        val pathKeys = extractPathKeys(sampledInput, keyMap, 8)
        val pathKeyString = pathKeys.joinToString("-")


        
        // Candidate collection
        val candidates = HashSet<String>()
        val scale = getKeyboardScale(keyMap)
        val nearbyStart = findNearbyKeys(startPoint, keyMap, 100f * scale)
        val nearbyEnd = findNearbyKeys(endPoint, keyMap, 100f * scale)
        
        for (s in nearbyStart) {
            for (e in nearbyEnd) {
                wordsByFirstLastLetter["${s}${e}"]?.let { candidates.addAll(it) }
            }
        }
        
        if (startKey != null && startKey.isNotEmpty()) {
            wordsByFirstLetter[startKey.first()]?.let { words ->
                candidates.addAll(words.take(50))
            }
        }
        
        lastContextWord?.let { prev ->
            val prevNorm = prev.lowercase(Locale.ROOT)
            bigramCounts[prevNorm]?.let { followingWords ->
                candidates.addAll(followingWords.keys.take(20))
            }
        }

        synchronized(userFrequencyMap) {
            val nearbyStartChars = nearbyStart.mapNotNull { it.firstOrNull()?.lowercaseChar() }.toSet()
            candidates.addAll(userFrequencyMap.entries
                .filter { entry -> 
                    val firstChar = entry.key.firstOrNull()?.lowercaseChar()
                    firstChar != null && firstChar in nearbyStartChars
                }
                .sortedByDescending { it.value }
                .take(30)
                .map { it.key })
        }

        // SCORING
        val scored = candidates
            .filter { !isWordBlocked(it) && it.length >= MIN_WORD_LENGTH }
            .take(400)
            .mapNotNull { word ->
                val template = getOrCreateTemplate(word, keyMap) ?: return@mapNotNull null
                
                val tLen = getPathLength(template.rawPoints)
                val ratio = tLen / inputLength
                
                // More lenient ratio
                if (ratio > 6.0f || ratio < 0.3f) return@mapNotNull null

                if (template.sampledPoints == null || template.normalizedPoints == null || template.directionVectors == null || template.sampledWeights == null) {
                    val (pts, wts) = sampleTemplate(template.rawPoints, template.rawWeights, SAMPLE_POINTS)
                    template.sampledPoints = pts
                    template.sampledWeights = wts
                    template.normalizedPoints = normalizePath(template.sampledPoints!!)
                    template.directionVectors = calculateDirectionVectors(template.sampledPoints!!)
                }
                
                val tSampled = template.sampledPoints ?: return@mapNotNull null
                val tWeights = template.sampledWeights ?: return@mapNotNull null
                val tNormalized = template.normalizedPoints ?: return@mapNotNull null
                val tDirections = template.directionVectors ?: return@mapNotNull null
                if (tSampled.isEmpty() || tNormalized.isEmpty() || tDirections.isEmpty()) return@mapNotNull null
                
                // Calculate scores
                val shapeScore = calculateShapeScore(normalizedInput, tNormalized)
                // Pass weights
                val locScore = calculateLocationScore(sampledInput, tSampled, tWeights)
                val dirScore = calculateDirectionScore(inputDirections, tDirections)
                val templateTurns = detectTurns(tDirections)
                val turnScore = calculateTurnScore(inputTurns, templateTurns)
                val pathKeyScore = calculatePathKeyScore(pathKeys, word) // Added Path Key Score

                // LENGTH MISMATCH PENALTY (same as Precise algorithm)
                val turnBasedLen = (inputTurns.size + 2).coerceAtLeast(2)
                val expectedLen = minOf(pathKeys.size, turnBasedLen).coerceAtLeast(2)
                val lengthPenalty = (word.length - expectedLen).coerceAtLeast(0) * 0.5f

                // Integration
                val geometryScore = (shapeScore * SHAPE_CONTEXT_SHAPE_WEIGHT) +
                                    (locScore * SHAPE_CONTEXT_LOCATION_WEIGHT) +
                                    (dirScore * SHAPE_CONTEXT_DIRECTION_WEIGHT) +
                                    (turnScore * SHAPE_CONTEXT_TURN_WEIGHT) +
                                    (pathKeyScore * 1.2f) +
                                    lengthPenalty
                
                // Smart Penalty
                var penalty = 0f
                if (startKey != null && !word.startsWith(startKey, ignoreCase = true)) {
                    penalty += SHAPE_CONTEXT_START_PENALTY
                }
                if (endKey != null && !word.endsWith(endKey, ignoreCase = true)) {
                     val lastChar = word.last()
                     val endChar = endKey.first()
                     // FIX: If keys are adjacent (e.g. B and N), penalize less
                     if (areKeysAdjacent(lastChar, endChar)) {
                         penalty += 0.2f 
                     } else {
                         penalty += SHAPE_CONTEXT_END_PENALTY
                     }
                }
                
                // Context & Grammar
                val nGramBoost = getContextBoost(word)
                val grammarBoost = getGrammarBoost(word)
                val totalContextBoost = (nGramBoost * grammarBoost).coerceAtMost(2.5f)
                
                // Frequency
                val rank = template.rank
                val freqBonus = 1.0f / (1.0f + 0.3f * ln((rank + 1).toFloat()))
                
                var userBoost = 1.0f
                synchronized(userFrequencyMap) {
                    val userCount = userFrequencyMap[word] ?: 0
                    if (userCount > 0) {
                        userBoost *= (1.0f + 0.05f * min(userCount, 10))
                    }
                }
                // Cap usage boost before applying context so it can't dominate geometry
                userBoost = userBoost.coerceAtMost(1.5f)
                userBoost *= totalContextBoost
                
                                // FINAL CALCULATION
                                // FREQUENCY WEIGHT BALANCED (0.7 -> 0.45) so geometry can disambiguate custom words
                                var finalScore = (geometryScore + penalty) * (1.0f - 0.45f * freqBonus) / userBoost
                                
                                // APPLY PENALTY
                                val penaltyEnd = temporaryPenalties[word] ?: 0L
                                if (System.currentTimeMillis() < penaltyEnd) {
                                    finalScore *= 10.0f // Massive penalty
                                }
                                


                val confidence = (1.0f / (1.0f + finalScore)).coerceIn(0f, 1f)
                SwipeResult(word, PredictionSource.SHAPE_CONTEXT, confidence, finalScore)
            }
        
        return scored.sortedBy { it.rawScore }.distinctBy { it.word }.take(5)
    }
    // =================================================================================
    // FUNCTION: decodeSwipeDual
    // SUMMARY: Main entry point for dual-algorithm prediction.
    //          Runs BOTH Precise and Shape/Context algorithms, then picks winner
    //          based on swipe speed. Returns SwipeResult list with source info.
    //          
    // SPEED-BASED SELECTION:
    //   - speed < 0.8 px/ms: PRECISE wins (slow, careful swipes)
    //   - speed >= 0.8 px/ms: SHAPE_CONTEXT wins (fast, sloppy swipes)
    //          
    // RETURNS: List<SwipeResult> with first item being the "winner"
    // =================================================================================


    fun decodeSwipeDual(
        timedPath: List<TimedPoint>, 
        keyMap: Map<String, PointF>
    ): List<SwipeResult> {
        if (timedPath.size < 3 || keyMap.isEmpty()) return emptyList()
        
        // NEW: Cache this path. If the user selects a word from this swipe,
        // we will use this path to learn their "fat finger" offsets.
        

        // NEW: Cache this path. If the user selects a word from this swipe,
        // we will use this path to learn their "fat finger" offsets.
        
        lastSwipePath = ArrayList(timedPath)
        lastKeyMap = keyMap // Cache reference


        // Ensure dictionary is loaded
        if (wordList.isEmpty()) {

            loadDefaults()
            if (wordList.isEmpty()) {
                return emptyList()
            }
        }
        
        val swipePath = timedPath.map { it.toPointF() }
        val keyDwellTimes = calculateKeyDwellTimes(timedPath, keyMap)
        

        
        // Run BOTH algorithms
        val preciseResults = mutableListOf<SwipeResult>()
        val shapeResults = mutableListOf<SwipeResult>()
        
        // Precise algorithm
        try {
            val preciseWords = decodeSwipeWithDwell(swipePath, keyMap, keyDwellTimes)

            preciseResults.addAll(preciseWords.mapIndexed { idx, word ->
                SwipeResult(
                    word = word,
                    source = PredictionSource.PRECISE,
                    confidence = (1.0f - idx * 0.15f).coerceIn(0.3f, 1.0f),
                    rawScore = idx.toFloat()
                )
            })

        } catch (e: Exception) {
              // Print full stack trace to logcat
        }
        
        // Shape/Context algorithm
        try {
            val shapeRaw = decodeSwipeShapeContext(timedPath, keyMap)

            shapeResults.addAll(shapeRaw)

        } catch (e: Exception) {
              // Print full stack trace to logcat
        }

        
        // =======================================================================
        // SMART MERGE: Precise overrides Shape when it found a shorter/equal,
        // higher-rank word. This prevents "wheat" from beating "what" just
        // because the swipe was fast. Otherwise use speed-based winner.
        // =======================================================================
        val preciseTop = preciseResults.firstOrNull()
        val shapeTop = shapeResults.firstOrNull()
        
        // Precise is always primary (slot #1). Shape fills remaining slots.
        val finalWinner = preciseResults
        val finalLoser = shapeResults

        val merged = mutableListOf<SwipeResult>()
        val maxLen = maxOf(finalWinner.size, finalLoser.size)
        for (i in 0 until maxLen) {
            if (i < finalWinner.size) merged.add(finalWinner[i])
            if (i < finalLoser.size) merged.add(finalLoser[i])
        }

        // Deduplicate by word (keep first occurrence to preserve priority)
        val seen = HashSet<String>()
        val deduplicated = merged.filter { result ->
            val normalized = result.word.lowercase(Locale.ROOT)
            if (seen.contains(normalized)) false
            else { seen.add(normalized); true }
        }.take(3)
        



        // DEBUG: Log the weights of the WINNER to verify Consonant Anchoring
        // This confirms if vowels are getting 0.6 and consonants 1.0
        if (deduplicated.isNotEmpty()) {
            val winner = deduplicated[0]
            val t = templateCache[winner.word]
            if (t != null) {

            }
        }
        
        // FALLBACK: If both algorithms failed, try basic prefix matching
        if (deduplicated.isEmpty()) {

            val startPoint = swipePath.first()
            val startKey = findClosestKey(startPoint, keyMap)
            if (startKey != null) {
                val fallbackWords = getSuggestions(startKey, 3)
                if (fallbackWords.isNotEmpty()) {

                    return fallbackWords.map { word ->
                        SwipeResult(word, PredictionSource.PRECISE, 0.5f, 100f)
                    }
                }
            }
        }
        
        return deduplicated
    }
    // =================================================================================
    // END BLOCK: decodeSwipeDual
    // =================================================================================

    // =================================================================================
    // FUNCTION: decodeSwipeDualPreview
    // SUMMARY: Lightweight dual-algorithm preview for mid-swipe updates.
    //          Returns top result from BOTH algorithms simultaneously so UI can
    //          display them side-by-side during swiping.
    //          
    // DISPLAY LAYOUT:
    //   - Left slot (cand1): Precise top result (GREEN)
    //   - Middle slot (cand2): Shape/Context top result (BLUE)
    //   - Right slot (cand3): Empty during preview
    //          
    // RETURNS: Pair<SwipeResult?, SwipeResult?> = (preciseTop, shapeTop)
    // =================================================================================
    fun decodeSwipeDualPreview(
        swipePath: List<PointF>, 
        keyMap: Map<String, PointF>
    ): Pair<SwipeResult?, SwipeResult?> {
        if (swipePath.size < 5 || keyMap.isEmpty()) return Pair(null, null)
        
        var preciseResult: SwipeResult? = null
        var shapeResult: SwipeResult? = null
        
        // Get quick PRECISE prediction (using existing preview function)
        try {
            val preciseWords = decodeSwipePreview(swipePath, keyMap)

            if (preciseWords.isNotEmpty()) {
                preciseResult = SwipeResult(
                    word = preciseWords.first(),
                    source = PredictionSource.PRECISE,
                    confidence = 0.8f
                )
            }
        } catch (e: Exception) { 
            // Silent fail for preview
        }
        
        // Get quick SHAPE prediction (simplified for speed)
        try {
            shapeResult = decodeSwipeShapePreview(swipePath, keyMap)

        } catch (e: Exception) { 
            // Silent fail for preview
        }
        
        return Pair(preciseResult, shapeResult)
    }
    // =================================================================================
    // END BLOCK: decodeSwipeDualPreview
    // =================================================================================

    // =================================================================================
    // FUNCTION: decodeSwipeShapePreview
    // SUMMARY: Lightweight Shape/Context preview for mid-swipe updates.
    //          Uses simplified scoring with shape-first weights for speed.
    // =================================================================================
    private fun decodeSwipeShapePreview(
        swipePath: List<PointF>, 
        keyMap: Map<String, PointF>
    ): SwipeResult? {
        if (swipePath.size < 5 || keyMap.isEmpty()) return null
        if (wordList.isEmpty()) return null

        val inputLength = getPathLength(swipePath)
        if (inputLength < 20f) return null

        val sampledInput = samplePath(swipePath, SAMPLE_POINTS)
        val normalizedInput = normalizePath(sampledInput)

        val startPoint = sampledInput.first()
        val endPoint = sampledInput.last()
        val startKey = findClosestKey(startPoint, keyMap)

        // FAST CANDIDATE COLLECTION - wider radius for shape tolerance
        val candidates = HashSet<String>()

        val scale = getKeyboardScale(keyMap)
        val nearbyStart = findNearbyKeys(startPoint, keyMap, 80f * scale)  // Wider than precise
        val nearbyEnd = findNearbyKeys(endPoint, keyMap, 80f * scale)

        for (s in nearbyStart) {
            for (e in nearbyEnd) {
                wordsByFirstLastLetter["${s}${e}"]?.let { candidates.addAll(it) }
            }
        }

        if (startKey != null) {
            wordsByFirstLetter[startKey.first()]?.let { words ->
                candidates.addAll(words.take(30))
            }
        }

        // Add context-boosted candidates
        lastContextWord?.let { prev ->
            val prevNorm = prev.lowercase(Locale.ROOT)
            bigramCounts[prevNorm]?.let { followingWords ->
                candidates.addAll(followingWords.keys.take(10))
            }
        }

        // FAST SCORING with shape-first weights
        val scored = candidates
            .filter { !isWordBlocked(it) && it.length >= MIN_WORD_LENGTH }
            .take(40)
            .mapNotNull { word ->
                val template = getOrCreateTemplate(word, keyMap) ?: return@mapNotNull null

                val tLen = getPathLength(template.rawPoints)
                val ratio = tLen / inputLength
                if (ratio > 4.0f || ratio < 0.25f) return@mapNotNull null

                
                if (template.sampledPoints == null || template.normalizedPoints == null || template.directionVectors == null || template.sampledWeights == null) {

                
                                    val (pts, wts) = sampleTemplate(template.rawPoints, template.rawWeights, SAMPLE_POINTS)

                
                                    template.sampledPoints = pts

                
                                    template.sampledWeights = wts

                
                                    template.normalizedPoints = normalizePath(template.sampledPoints!!)

                
                                    template.directionVectors = calculateDirectionVectors(template.sampledPoints!!)

                
                                }

                
                

                
                                // SHAPE-FIRST scoring (simplified)

                
                                val shapeScore = calculateShapeScore(normalizedInput, template.normalizedPoints!!)

                
                                val locScore = calculateLocationScore(sampledInput, template.sampledPoints!!, template.sampledWeights)

                // Shape weight HIGH, location LOW (Gboard style)
                val integrationScore = (shapeScore * 1.5f) + (locScore * 0.3f)

                val rank = template.rank
                val freqBonus = 1.0f / (1.0f + 0.1f * ln((rank + 1).toFloat()))
                val contextBoost = getContextBoost(word)

                val finalScore = integrationScore * (1.0f - 0.3f * freqBonus) / contextBoost
                Pair(word, finalScore)
            }

        val topWord = scored.minByOrNull { it.second }?.first ?: return null
        
        return SwipeResult(
            word = topWord,
            source = PredictionSource.SHAPE_CONTEXT,
            confidence = 0.7f
        )
    }
    // =================================================================================
    // END BLOCK: decodeSwipeShapePreview
    // =================================================================================

    // =================================================================================
    // FUNCTION: learnKeyOffsets (Spatial Heatmap)
    // SUMMARY: Compares the actual swipe path to the ideal keys of the selected word.
    //          Calculates the offset (error) and updates the rolling average for those keys.
    //          Only learns from Start, End, and Sharp Turns to avoid noise.
    // =================================================================================

    private fun learnKeyOffsets(context: Context, word: String, path: List<TimedPoint>) {
        val keys = lastKeyMap ?: return
        val rawPath = path.map { it.toPointF() }
        
        // 1. Identify Key Points in Path (Start, End, Sharp Turns)
        // reusing extractPathKeys logic but getting POINTS, not letters
        val startPoint = rawPath.first()
        val endPoint = rawPath.last()
        
        // Simple Learning: Start -> First Letter, End -> Last Letter
        // We only learn if the user was "close enough" to be ambiguous (e.g. < 80px)
        
        val firstChar = word.first().toString().lowercase()
        val lastChar = word.last().toString().lowercase()
        
        val idealStart = keys[firstChar] ?: keys[firstChar.uppercase()]
        val idealEnd = keys[lastChar] ?: keys[lastChar.uppercase()]
        
        synchronized(keyOffsets) {
            var modified = false
            

            // Learn Start Offset
            if (idealStart != null) {
                val dx = startPoint.x - idealStart.x
                val dy = startPoint.y - idealStart.y
                val dist = hypot(dx, dy)
                


                // INCREASED LIMIT: 80f -> 150f for easier testing
                if (dist < 150f) {
                    val current = keyOffsets[firstChar] ?: PointF(0f, 0f)
                    // Learning Rate 0.1 (Slow adaptation)
                    current.x = current.x * 0.9f + dx * 0.1f
                    current.y = current.y * 0.9f + dy * 0.1f
                    keyOffsets[firstChar] = current
                    modified = true
                }
            }

            
            // Learn End Offset
            if (idealEnd != null) {
                val dx = endPoint.x - idealEnd.x
                val dy = endPoint.y - idealEnd.y
                if (hypot(dx, dy) < 80f) {
                    val current = keyOffsets[lastChar] ?: PointF(0f, 0f)
                    current.x = current.x * 0.9f + dx * 0.1f
                    current.y = current.y * 0.9f + dy * 0.1f
                    keyOffsets[lastChar] = current
                    modified = true
                }
            }
            
            if (modified) {
                saveKeyOffsets(context)
                templateCache.clear() // Clear cache so new offsets apply

            }
        }
    }

    
    // Helper to load/save offsets
    private fun loadKeyOffsets(context: Context) {
        try {
            val file = java.io.File(context.filesDir, KEY_OFFSETS_FILE)
            if (file.exists()) {
                val json = org.json.JSONObject(file.readText())
                val keys = json.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val pos = json.getJSONObject(key)
                    keyOffsets[key] = PointF(pos.getDouble("x").toFloat(), pos.getDouble("y").toFloat())
                }

            }
        } catch (e: Exception) {
        }
    }

    private fun saveKeyOffsets(context: Context) {
        try {
            val json = org.json.JSONObject()
            synchronized(keyOffsets) {
                for ((key, offset) in keyOffsets) {
                    val pos = org.json.JSONObject()
                    pos.put("x", offset.x)
                    pos.put("y", offset.y)
                    json.put(key, pos)
                }
            }
            java.io.File(context.filesDir, KEY_OFFSETS_FILE).writeText(json.toString())
        } catch (e: Exception) {
            
        }
    }
}
