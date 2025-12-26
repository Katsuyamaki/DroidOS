
package com.example.coverscreentester

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
        
        // SHARK2 Algorithm Parameters
        private const val SAMPLE_POINTS = 64           // Number of uniform sample points (64 is good balance)
        private const val NORMALIZATION_SIZE = 100f    // Size L for shape normalization
        private const val PRUNING_THRESHOLD = 80f      // Max start/end key distance for pruning (pixels)
        private const val SHAPE_WEIGHT = 0.4f          // Weight for shape channel (α)
        private const val LOCATION_WEIGHT = 0.6f       // Weight for location channel (β) - α + β = 1
        private const val TOP_N_CANDIDATES = 10        // Number of candidates for final ranking
        private const val MIN_WORD_LENGTH = 2          // Minimum word length to consider
    }

    // =================================================================================
    // DATA STRUCTURES
    // =================================================================================
    
    class TrieNode {
        val children = HashMap<Char, TrieNode>()
        var isEndOfWord = false
        var word: String? = null
        var rank: Int = Int.MAX_VALUE // 0 = Most Frequent
    }

    /**
     * Pre-computed template for a word containing sampled key positions
     */
    data class WordTemplate(
        val word: String,
        val rank: Int,
        val rawPoints: List<PointF>,        // Key centers for each letter
        var sampledPoints: List<PointF>? = null,  // Will be computed when keyboard layout is known
        var normalizedPoints: List<PointF>? = null // Scale-normalized version
    )

    private val root = TrieNode()
    private val wordList = ArrayList<String>()
    
    // Template cache - maps word to its template (lazy-computed per keyboard layout)
    private val templateCache = HashMap<String, WordTemplate>()
    private var lastKeyMapHash = 0  // Track if keyboard layout changed

    // --- CUSTOM DICTIONARY STORAGE ---
    private val blockedWords = java.util.HashSet<String>()
    private val customWords = java.util.HashSet<String>()
    // Cache for the top 1000 words to make "Hail Mary" pass instant
    private val commonWordsCache = ArrayList<String>()
    // Fix: Removed 'const' (not allowed in class body)
    private val USER_DICT_FILE = "user_words.txt"
    private val BLOCKED_DICT_FILE = "blocked_words.txt"
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
            "play", "small", "end", "put", "while", "next", "sound", "below",
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
    // FUNCTION: loadDictionary
    // SUMMARY: Loads the weighted dictionary from assets asynchronously with enhanced
    //          logging for debugging. The file should be at assets/dictionary.txt and
    //          sorted by frequency (most common words first). Falls back to defaults
    //          if loading fails.
    // =================================================================================
    fun loadDictionary(context: Context) {
        Thread {
            try {
                val start = System.currentTimeMillis()
                val newRoot = TrieNode()
                val newWordList = ArrayList<String>()
                val newBlocked = java.util.HashSet<String>()
                val newCustom = java.util.HashSet<String>()
                var lineCount = 0

                // =================================================================================
                // LOAD CUSTOM LISTS (User & Blocked)
                // SUMMARY: Loads user's custom words and blocked words from persistent storage.
                //          These files are in the app's private filesDir.
                // =================================================================================
                try {
                    val blockFile = java.io.File(context.filesDir, BLOCKED_DICT_FILE)
                    if (blockFile.exists()) {
                        val blockedLines = blockFile.readLines().map { it.trim().lowercase() }.filter { it.isNotEmpty() }
                        newBlocked.addAll(blockedLines)
                        android.util.Log.d("DroidOS_Prediction", "LOAD: Blocked words file found, ${blockedLines.size} words: $blockedLines")
                    } else {
                        android.util.Log.d("DroidOS_Prediction", "LOAD: No blocked words file exists yet")
                    }

                    val userFile = java.io.File(context.filesDir, USER_DICT_FILE)
                    if (userFile.exists()) {
                        val userLines = userFile.readLines().map { it.trim().lowercase() }.filter { it.isNotEmpty() }
                        newCustom.addAll(userLines)
                        android.util.Log.d("DroidOS_Prediction", "LOAD: User words file found, ${userLines.size} words")
                    } else {
                        android.util.Log.d("DroidOS_Prediction", "LOAD: No user words file exists yet")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DroidOS_Prediction", "Failed to load user lists", e)
                }
                // =================================================================================
                // END BLOCK: LOAD CUSTOM LISTS
                // =================================================================================

                // 2. Load Main Dictionary (Assets) - Filtering Blocked words
                try {
                    context.assets.open("dictionary.txt").bufferedReader().useLines { lines ->
                        lines.forEachIndexed { index, line ->
                            val word = line.trim().lowercase(Locale.ROOT)
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
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DroidOS_Prediction", "Dictionary asset load failed: ${e.message}")
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
                        current.rank = 0 // High priority
                    }
                }

                // 4. Merge Hardcoded Defaults
                val existingDefaults = synchronized(this) { ArrayList(wordList) }
                for (defaultWord in existingDefaults) {
                    val lower = defaultWord.lowercase(Locale.ROOT)
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

                    blockedWords.clear()
                    blockedWords.addAll(newBlocked)
                    customWords.clear()
                    customWords.addAll(newCustom)

                    templateCache.clear()

                    // Populate Common Words Cache (Top 1000)
                    commonWordsCache.clear()
                    commonWordsCache.addAll(
                        wordList.sortedBy { getWordRank(it) }.take(1000)
                    )
                }
                android.util.Log.d("DroidOS_Prediction", "Dictionary Loaded: $lineCount asset + ${newCustom.size} user words + ${newBlocked.size} blocked. Common Cache: ${commonWordsCache.size}")
                if (newBlocked.isNotEmpty()) {
                    android.util.Log.d("DroidOS_Prediction", "Blocked words loaded: $newBlocked")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: loadDictionary
    // =================================================================================

    /**
     * Learns a new word: Adds to memory and saves to user_words.txt
     */
    fun learnWord(context: Context, word: String) {
        val cleanWord = word.trim().lowercase(Locale.ROOT)
        if (cleanWord.length < MIN_WORD_LENGTH) return
        if (hasWord(cleanWord)) return // Already known

        Thread {
            try {
                // 1. Update Memory
                synchronized(this) {
                    customWords.add(cleanWord)
                    blockedWords.remove(cleanWord) // Unblock if previously blocked
                    insert(cleanWord, 0) // Rank 0 = High Priority
                }

                // 2. Append to File
                val file = java.io.File(context.filesDir, USER_DICT_FILE)
                file.appendText("$cleanWord\n")

                // 3. Ensure it's removed from blocked file if needed
                saveSetToFile(context, BLOCKED_DICT_FILE, blockedWords)

                android.util.Log.d("DroidOS_Prediction", "Learned word: $cleanWord")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    /**
     * Blocks a word: Removes from memory and saves to blocked_words.txt
     */
    // =================================================================================
    // FUNCTION: blockWord
    // SUMMARY: Blocks a word from appearing in suggestions. Saves to persistent storage.
    //          The word will remain blocked until user clears app data or unblocks it.
    // =================================================================================
    fun blockWord(context: Context, word: String) {
        val cleanWord = word.trim().lowercase(Locale.ROOT)
        if (cleanWord.isEmpty()) return

        Thread {
            try {
                // 1. Update Memory
                synchronized(this) {
                    blockedWords.add(cleanWord)
                    customWords.remove(cleanWord)

                    // Remove from active lists
                    wordList.remove(cleanWord)
                    templateCache.remove(cleanWord)
                }

                // 2. Save to Files
                saveSetToFile(context, BLOCKED_DICT_FILE, blockedWords)
                saveSetToFile(context, USER_DICT_FILE, customWords)

                // 3. Verify save
                val blockFile = java.io.File(context.filesDir, BLOCKED_DICT_FILE)
                val savedContent = if (blockFile.exists()) blockFile.readText() else "(file not found)"
                android.util.Log.d("DroidOS_Prediction", "SAVE: Blocked '$cleanWord'. File now contains: $savedContent")
                android.util.Log.d("DroidOS_Prediction", "SAVE: blockedWords set now has ${blockedWords.size} words: $blockedWords")
            } catch (e: Exception) {
                android.util.Log.e("DroidOS_Prediction", "SAVE FAILED: ${e.message}", e)
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: blockWord
    // =================================================================================

    // =================================================================================
    // FUNCTION: saveSetToFile
    // SUMMARY: Saves a set of words to a file in the app's private storage.
    // =================================================================================
    private fun saveSetToFile(context: Context, filename: String, data: Set<String>) {
        try {
            val file = java.io.File(context.filesDir, filename)
            val content = data.filter { it.isNotEmpty() }.joinToString("\n")
            file.writeText(content)
            android.util.Log.d("DroidOS_Prediction", "SAVEFILE: Wrote ${data.size} items to $filename")
        } catch (e: Exception) {
            android.util.Log.e("DroidOS_Prediction", "SAVEFILE FAILED: $filename - ${e.message}", e)
        }
    }
    // =================================================================================
    // END BLOCK: saveSetToFile
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

    fun insert(word: String, rank: Int = Int.MAX_VALUE) {
        val lower = word.lowercase(Locale.ROOT)
        synchronized(this) {
            if (!wordList.contains(lower)) wordList.add(lower)
            
            var current = root
            for (char in lower) {
                current = current.children.computeIfAbsent(char) { TrieNode() }
            }
            current.isEndOfWord = true
            current.word = lower
            if (rank < current.rank) current.rank = rank
        }
    }

    /**
     * Returns a list of suggested words for the given prefix, sorted by popularity.
     */
    // =================================================================================
    // FUNCTION: getSuggestions
    // SUMMARY: Returns suggested words for a given prefix, sorted by popularity.
    //          Filters out blocked words to prevent them from appearing in suggestions.
    // =================================================================================
    fun getSuggestions(prefix: String, maxResults: Int = 3): List<String> {
        if (prefix.isEmpty()) return emptyList()
        val cleanPrefix = prefix.lowercase(Locale.ROOT)

        var current = root
        for (char in cleanPrefix) {
            current = current.children[char] ?: return emptyList()
        }

        val candidates = ArrayList<Pair<String, Int>>()
        collectCandidates(current, candidates)
        candidates.sortWith(compareBy<Pair<String, Int>> { it.second }.thenBy { it.first.length })

        // Filter out blocked words before returning
        return candidates
            .filter { !blockedWords.contains(it.first) }
            .take(maxResults)
            .map { it.first }
    }
    // =================================================================================
    // END BLOCK: getSuggestions
    // =================================================================================

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

    fun decodeSwipe(swipePath: List<PointF>, keyMap: Map<String, PointF>): List<String> {
        val startT = System.currentTimeMillis()

        // ENTRY LOG: Proves decodeSwipe was called
        android.util.Log.d("DroidOS_Swipe", "=== decodeSwipe ENTRY: ${swipePath.size} path points, ${keyMap.size} keys ===")

        // --- DIAGNOSTIC: COORDINATE CHECK ---
        if (swipePath.isNotEmpty()) {
            val firstPt = swipePath[0]
            val keyE = keyMap["e"] ?: keyMap["E"]
            if (keyE != null) {
                // Log touch vs key to ensure they are in the same coordinate system
                android.util.Log.d("DroidOS_Swipe", "Coords -> Touch: (${firstPt.x.toInt()}, ${firstPt.y.toInt()}) vs Key 'e': (${keyE.x.toInt()}, ${keyE.y.toInt()})")
            } else {
                android.util.Log.e("DroidOS_Swipe", "CRITICAL: Key 'e' not found in KeyMap!")
            }
        }

        // --- DIAGNOSTIC: WORD CHECK ---
        val debugWord = "example"
        if (!wordList.contains(debugWord)) {
            android.util.Log.e("DroidOS_Swipe", "CRITICAL: '$debugWord' is MISSING from dictionary!")
        }

        // AUTO-RECOVERY: If dictionary is gone, reload it immediately
        if (wordList.isEmpty()) {
            android.util.Log.e("DroidOS_Swipe", "EXIT: Word list empty. Reloading...")
            loadDefaults()
            return emptyList()
        }

        // EXIT 1: Path too short
        if (swipePath.size < 3) {
            android.util.Log.d("DroidOS_Swipe", "EXIT: Path too short (${swipePath.size} < 3)")
            return emptyList()
        }

        val keyMapHash = keyMap.hashCode()
        if (keyMapHash != lastKeyMapHash) {
            templateCache.clear()
            lastKeyMapHash = keyMapHash
            android.util.Log.d("DroidOS_Prediction", "Keymap changed, clearing cache.")
        }

        // 1. Processing Input
        val sampledInput = samplePath(swipePath, SAMPLE_POINTS)
        val normalizedInput = normalizePath(sampledInput)
        val startKeyDists = findKeysWithDist(sampledInput.first(), keyMap, 5000f)
        val endKeyDists = findKeysWithDist(sampledInput.last(), keyMap, 5000f)

        // Log the start/end keys to see if we are detecting them correctly
        val startKeys = startKeyDists.filter { it.value < 150 }.keys.joinToString()
        val endKeys = endKeyDists.filter { it.value < 150 }.keys.joinToString()
        android.util.Log.d("DroidOS_Swipe", "Input Start Near: [$startKeys] | End Near: [$endKeys]")

        // 2. Candidate Filtering Helper
        fun getCandidates(commonRadius: Float, rareRadius: Float, commonRankLimit: Int): List<String> {
            return wordList.filter { word ->
                if (word.length < MIN_WORD_LENGTH) return@filter false

                val first = word.first().toString().lowercase()
                val last = word.last().toString().lowercase()

                val dStart = startKeyDists[first] ?: return@filter false
                val dEnd = endKeyDists[last] ?: return@filter false

                val rank = getWordRank(word)
                val maxDist = if (rank < commonRankLimit) commonRadius else rareRadius
                dStart <= maxDist && dEnd <= maxDist
            }
        }

        // PASS 1: Strict (Accurate Swiping)
        var candidates = getCandidates(400f, 150f, 1000)
        var currentPass = 1
        var shapeWeight = 0.4f
        var locationWeight = 0.6f

        android.util.Log.d("DroidOS_Swipe", "Pass 1 Candidates: ${candidates.size}")

        // PASS 2: Loose (Sloppy Swiping)
        if (candidates.size < 3) {
            val pass2 = getCandidates(3000f, 350f, 5000)
            candidates = (candidates + pass2).distinct()
            currentPass = 2
            shapeWeight = 0.8f
            locationWeight = 0.2f
            android.util.Log.d("DroidOS_Swipe", "Pass 2 Triggered. Total Candidates: ${candidates.size}")
        }

        // =================================================================================
        // PASS 3: HAIL MARY FALLBACK
        // SUMMARY: When Pass 1 and 2 find zero candidates, fall back to the pre-cached
        //          common words list. Logs cache status to diagnose empty cache scenarios.
        // =================================================================================
        if (candidates.isEmpty()) {
            android.util.Log.w("DroidOS_Swipe", "Pass 2 failed (0 candidates). Triggering Hail Mary. Cache size: ${commonWordsCache.size}")

            if (commonWordsCache.isEmpty()) {
                android.util.Log.e("DroidOS_Swipe", "CRITICAL: commonWordsCache is EMPTY! Dictionary may not have loaded.")
            }

            candidates = ArrayList(commonWordsCache)
            currentPass = 3
            shapeWeight = 1.0f
            locationWeight = 0.0f
        }
        // =================================================================================
        // END BLOCK: PASS 3 HAIL MARY FALLBACK
        // =================================================================================

        // =================================================================================
        // SCORING BLOCK WITH ENHANCED FAILURE LOGGING
        // SUMMARY: Scores all candidates using shape and location channels. Logs all failure
        //          scenarios to diagnose silent prediction failures. Tracks template creation
        //          failures, sample point mismatches, and empty result scenarios.
        // =================================================================================
        try {
            // Track failure reasons for debugging
            var templateFailures = 0
            var samplePointFailures = 0
            var scoredCount = 0

            val scored = candidates.mapNotNull { word ->
                val template = getOrCreateTemplate(word, keyMap)
                if (template == null) {
                    templateFailures++
                    return@mapNotNull null
                }

                if (template.sampledPoints == null) {
                    template.sampledPoints = samplePath(template.rawPoints, SAMPLE_POINTS)
                    template.normalizedPoints = normalizePath(template.sampledPoints!!)
                }

                if (template.sampledPoints?.size != SAMPLE_POINTS) {
                    samplePointFailures++
                    return@mapNotNull null
                }

                val shapeScore = calculateShapeScore(normalizedInput, template.normalizedPoints!!)
                val locationScore = if (locationWeight > 0) calculateLocationScore(sampledInput, template.sampledPoints!!) else 0f

                val integrationScore = shapeWeight * shapeScore + locationWeight * locationScore

                val rank = template.rank
                val freqBonusMultiplier = if (currentPass >= 2) 0.8f else 0.5f
                val frequencyBonus = 1.0f / (1.0f + 0.3f * ln((rank + 1).toFloat()))

                val finalScore = integrationScore * (1.0f - freqBonusMultiplier * frequencyBonus)

                scoredCount++
                Triple(word, finalScore, rank)
            }

            // LOG: Scoring statistics to identify filtering issues
            if (templateFailures > 0 || samplePointFailures > 0) {
                android.util.Log.w("DroidOS_Swipe", "SCORING FILTER: $templateFailures template failures, $samplePointFailures sample point failures out of ${candidates.size} candidates")
            }

            val sorted = scored.sortedBy { it.second }
            val results = sorted.take(3).map { it.first }

            // LOG: Empty results with detailed diagnostics
            if (results.isEmpty()) {
                android.util.Log.e("DroidOS_Swipe", "FAIL: 0 results! Pass=$currentPass, Candidates=${candidates.size}, Scored=$scoredCount, TemplateErr=$templateFailures, SampleErr=$samplePointFailures")
                android.util.Log.e("DroidOS_Swipe", "FAIL: KeyMap has ${keyMap.size} keys. CommonCache has ${commonWordsCache.size} words.")

                // Fallback: Return most common words as last resort
                if (commonWordsCache.isNotEmpty()) {
                    val fallback = commonWordsCache.take(3)
                    android.util.Log.w("DroidOS_Swipe", "FALLBACK: Returning top common words: $fallback")
                    return fallback
                }
            }

            // Log if our debug word made it
            if (results.contains(debugWord)) {
                android.util.Log.d("DroidOS_Swipe", "SUCCESS: '$debugWord' found!")
            }

            android.util.Log.d("DroidOS_Swipe", "FINAL (Pass $currentPass, ${System.currentTimeMillis() - startT}ms): $results")
            return results

        } catch (e: Exception) {
            android.util.Log.e("DroidOS_Swipe", "CRASH during Scoring: ${e.message}", e)
            e.printStackTrace()
            return emptyList()
        }
        // =================================================================================
        // END BLOCK: SCORING WITH ENHANCED FAILURE LOGGING
        // =================================================================================
    }

    
    // =================================================================================
    // FUNCTION: getOrCreateTemplate
    // SUMMARY: Gets or creates a word template with key positions. Returns null if any
    //          character in the word is missing from the keyMap. Logs first failure per
    //          batch to avoid log spam while still providing diagnostic info.
    // =================================================================================
    private fun getOrCreateTemplate(word: String, keyMap: Map<String, PointF>): WordTemplate? {
        templateCache[word]?.let { return it }

        // Build raw points from key centers
        val rawPoints = ArrayList<PointF>()
        for (char in word) {
            val keyPos = keyMap[char.toString().uppercase()]
                ?: keyMap[char.toString().lowercase()]

            if (keyPos == null) {
                // Log missing key (throttled to once per second to avoid spam)
                val now = System.currentTimeMillis()
                if (now - lastTemplateMissLog > 1000) {
                    android.util.Log.w("DroidOS_Swipe", "Template fail for '$word': key '$char' not in keyMap (${keyMap.size} keys)")
                    lastTemplateMissLog = now
                }
                return null
            }
            rawPoints.add(PointF(keyPos.x, keyPos.y))
        }

        if (rawPoints.size < MIN_WORD_LENGTH) return null

        val rank = getWordRank(word)
        val template = WordTemplate(word, rank, rawPoints)
        templateCache[word] = template
        return template
    }
    // =================================================================================
    // END BLOCK: getOrCreateTemplate
    // =================================================================================

    /**
     * Uniformly sample N points along a path.
     * This makes paths of different lengths comparable.
     */
    private fun samplePath(path: List<PointF>, numSamples: Int): List<PointF> {
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
    private fun calculateLocationScore(input: List<PointF>, template: List<PointF>): Float {
        if (input.size != template.size) return Float.MAX_VALUE
        
        var totalDist = 0f
        var totalWeight = 0f
        
        for (i in input.indices) {
            val dx = input[i].x - template[i].x
            val dy = input[i].y - template[i].y
            val dist = sqrt(dx * dx + dy * dy)
            
            // Weight endpoints more heavily (helps distinguish similar words)
            val position = i.toFloat() / (input.size - 1)
            val weight = if (position < 0.15f || position > 0.85f) 2.0f else 1.0f
            
            totalDist += dist * weight
            totalWeight += weight
        }
        
        return totalDist / totalWeight
    }
    
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
}
