
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


// =================================================================================
    // BLOCK: TUNING PARAMETERS
    // SUMMARY: Core algorithm weights and thresholds.
    // =================================================================================

    companion object {
        val instance = PredictionEngine()

        // Tuning Parameters
        private const val SAMPLE_POINTS = 64
        private const val NORMALIZATION_SIZE = 100f
        private const val SEARCH_RADIUS = 70f
        
        // Weights: 
        // Shape: 0.25 -> kept low to allow messy sizing
        // Location: 0.85 -> high trust in key hits
        // Direction: 0.5 -> kept low
        // Turn: 1.5 -> SIGNIFICANTLY INCREASED (was 0.9). Sharp corners are now king.
        private const val SHAPE_WEIGHT = 0.25f
        private const val LOCATION_WEIGHT = 0.85f
        private const val DIRECTION_WEIGHT = 0.5f   
        private const val TURN_WEIGHT = 1.5f        
        
        // Files
        private const val USER_STATS_FILE = "user_stats.json"
        private const val BLOCKED_DICT_FILE = "blocked_words.txt"
        private const val USER_DICT_FILE = "user_words.txt"
        private const val MIN_WORD_LENGTH = 2
    }

    // =================================================================================
    // END BLOCK: TUNING PARAMETERS
    // =================================================================================






    // ... (TrieNode class remains the same) ...

    // UPDATE: Add directionVectors to cache the flow of the word
    data class WordTemplate(
        val word: String,
        val rank: Int,
        val rawPoints: List<PointF>,
        var sampledPoints: List<PointF>? = null,
        var normalizedPoints: List<PointF>? = null,
        var directionVectors: List<PointF>? = null // NEW FIELD
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



// --- USER STATS ---
    private val USER_STATS_FILE = "user_stats.json"
    private val userFrequencyMap = HashMap<String, Int>()
    
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
                android.util.Log.d("DroidOS_Prediction", "Loaded stats for ${userFrequencyMap.size} words")
            }
        } catch (e: Exception) {
            android.util.Log.e("DroidOS_Prediction", "Failed to load user stats", e)
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
                e.printStackTrace()
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
        
        // FIX: Removed auto-learning. Typos won't be added automatically.
        // The UI must call learnWord() explicitly when the user selects a "New Word".
        // if (!hasWord(clean)) {
        //    learnWord(context, clean)
        // }
        
        saveUserStats(context)
    }


    fun loadDictionary(context: Context) {
        Thread {
            try {
                loadUserStats(context)
                val start = System.currentTimeMillis()
                val newRoot = TrieNode()
                val newWordList = ArrayList<String>()
                val newBlocked = java.util.HashSet<String>()
                val newCustom = java.util.HashSet<String>()
                var lineCount = 0

                val newWordsByFirstLetter = HashMap<Char, ArrayList<String>>()
                val newWordsByFirstLastLetter = HashMap<String, ArrayList<String>>()

                // =================================================================================
                // LOAD CUSTOM LISTS (User & Blocked)
                // SUMMARY: Loads user's custom words and blocked words from persistent storage.
                // =================================================================================
                try {
                    val blockFile = java.io.File(context.filesDir, BLOCKED_DICT_FILE)
                    if (blockFile.exists()) {
                        val blockedLines = blockFile.readLines().map { it.trim().lowercase(java.util.Locale.ROOT) }.filter { it.isNotEmpty() }
                        newBlocked.addAll(blockedLines)
                        android.util.Log.d("DroidOS_Prediction", "LOAD: Blocked words file found, ${blockedLines.size} words")
                    } else {
                        android.util.Log.d("DroidOS_Prediction", "LOAD: No blocked words file exists yet")
                    }

                    val userFile = java.io.File(context.filesDir, USER_DICT_FILE)
                    if (userFile.exists()) {
                        val userLines = userFile.readLines().map { it.trim().lowercase(java.util.Locale.ROOT) }.filter { it.isNotEmpty() }
                        
                        // FILTER: Check each user word against garbage filter on load
                        for (w in userLines) {
                            if (!looksLikeGarbage(w)) {
                                newCustom.add(w)
                            } else {
                                android.util.Log.d("DroidOS_Prediction", "Pruned garbage from user dict: $w")
                            }
                        }
                        android.util.Log.d("DroidOS_Prediction", "LOAD: User words file found, ${newCustom.size} valid words")
                    } else {
                        android.util.Log.d("DroidOS_Prediction", "LOAD: No user words file exists yet")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DroidOS_Prediction", "Failed to load user lists", e)
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
                android.util.Log.d("DroidOS_Prediction", "Dictionary Loaded: $lineCount asset + ${newCustom.size} user words + ${newBlocked.size} blocked. Common Cache: ${commonWordsCache.size}")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }




    /**
     * Learns a new word: Adds to memory and saves to user_words.txt
     */
    fun learnWord(context: Context, word: String) {
        if (word.length < 2) return

        // 1. Standardize the word immediately
        val cleanWord = word.trim().lowercase(java.util.Locale.ROOT)
        
        // FILTER: Don't learn garbage (random letters/typos)
        if (looksLikeGarbage(cleanWord)) {
            android.util.Log.d("DroidOS_Prediction", "Ignored garbage input: $cleanWord")
            return
        }

        // FILTER: Don't learn blocked words
        if (isWordBlocked(cleanWord)) return

        // OPTIMIZATION: Don't relearn if we already know it
        if (hasWord(cleanWord)) return

        Thread {
            try {
                // 2. Update Memory
                synchronized(this) {
                    customWords.add(cleanWord)
                    blockedWords.remove(cleanWord) // Unblock if previously blocked
                    insert(cleanWord, 0) // Rank 0 = High Priority
                }

                // 3. Append to File
                val file = java.io.File(context.filesDir, USER_DICT_FILE)
                file.appendText("$cleanWord\n")

                // 4. Ensure it's removed from blocked file if needed
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
    // FUNCTION: blockWord (Complete Cleanup)
    // =================================================================================
    fun blockWord(context: Context, word: String) {
        val cleanWord = word.trim().lowercase(Locale.ROOT)
        if (cleanWord.isEmpty()) return

        Thread {
            try {
                synchronized(this) {
                    // 1. Add to Block List
                    blockedWords.add(cleanWord)
                    
                    // 2. Remove from ALL active lists
                    customWords.remove(cleanWord)
                    wordList.remove(cleanWord)
                    
                    // 3. Remove from Indices (Crucial for immediate disappearance)
                    if (cleanWord.isNotEmpty()) {
                        wordsByFirstLetter[cleanWord.first()]?.remove(cleanWord)
                        if (cleanWord.length >= 2) {
                            val key = "${cleanWord.first()}${cleanWord.last()}"
                            wordsByFirstLastLetter[key]?.remove(cleanWord)
                        }
                    }
                    
                    // 4. Remove from User Stats (CRITICAL FIX: Stops "Zombie Words")
                    // If we don't remove it here, the "User Rescue" in decodeSwipe will bring it back.
                    synchronized(userFrequencyMap) {
                        userFrequencyMap.remove(cleanWord)
                    }
                    
                    templateCache.remove(cleanWord)
                }

                // 5. Persist Changes
                saveSetToFile(context, BLOCKED_DICT_FILE, blockedWords)
                saveSetToFile(context, USER_DICT_FILE, customWords)
                saveUserStats(context) // Save the removal from stats so it doesn't come back on reboot

                android.util.Log.d("DroidOS_Prediction", "BLOCKED: '$cleanWord' removed from all lists and stats.")
            } catch (e: Exception) {
                android.util.Log.e("DroidOS_Prediction", "Block failed", e)
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

    // =================================================================================
    // FILTER: GARBAGE DETECTION
    // Rule: Must have at least one vowel/y OR be in the whitelist.
    // =================================================================================
    private val VALID_VOWELLESS = setOf(
        "hmm", "shh", "psst", "brr", "pfft", "nth", "src", "jpg", "png", "gif",
        "txt", "xml", "pdf", "css", "html", "tv", "pc", "ok", "id", "cv", "ad", "ex", "vs", "mr", "dr", "ms"
    )

    private fun looksLikeGarbage(word: String): Boolean {
        if (word.length > 1) {
            val hasVowel = word.any { "aeiouyAEIOUY".contains(it) }
            if (!hasVowel) {
                if (VALID_VOWELLESS.contains(word.lowercase(java.util.Locale.ROOT))) return false
                return true // No vowel and not whitelisted -> Garbage
            }
        }
        return false
    }

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
        val cleanPrefix = prefix.lowercase(Locale.ROOT)

        var current = root
        for (char in cleanPrefix) {
            current = current.children[char] ?: return emptyList()
        }

        val candidates = ArrayList<Pair<String, Int>>()
        collectCandidates(current, candidates)
        
        // SORTING LOGIC:
        // 1. User Frequency (Highest First)
        // 2. Dictionary Rank (Lowest First)
        // 3. Length (Shortest First)
        val sortedCandidates = candidates.sortedWith(Comparator { a, b ->
            val wordA = a.first
            val wordB = b.first
            
            val countA = userFrequencyMap[wordA] ?: 0
            val countB = userFrequencyMap[wordB] ?: 0
            
            if (countA != countB) {
                return@Comparator countB - countA // Higher user count wins
            }
            
            val rankA = a.second
            val rankB = b.second
            if (rankA != rankB) {
                return@Comparator rankA - rankB // Lower dictionary rank wins
            }
            
            wordA.length - wordB.length
        })

        return sortedCandidates
            .filter { !blockedWords.contains(it.first) }
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
    // FUNCTION: decodeSwipe (v8 - Partial Match Override)
    // SUMMARY: Integrates "Prefix Bonus" and "Partial Match Override".
    //          If the user swipes "A-W-A" (Prefix of "Awake"), the path keys match perfectly.
    //          Normally, the Shape Score penalizes "Awake" because the template "A-W-A-K-E"
    //          is much longer/different shape than "A-W-A".
    //          The Override detects this condition and ignores Shape/Location scores,
    //          trusting the Key Match + Direction Score instead.
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

        // 1. Prepare Input
        val sampledInput = samplePath(swipePath, SAMPLE_POINTS)
        val normalizedInput = normalizePath(sampledInput)
        val inputDirections = calculateDirectionVectors(sampledInput)
        val inputTurns = detectTurns(inputDirections)

        // 2. Extract Path Keys (The "Skeleton" of the swipe)
        // Note: Thresholds inside extractPathKeys are optimized for sensitivity
        val pathKeys = extractPathKeys(sampledInput, keyMap, 8)
        
        val startKey = findClosestKey(sampledInput.first(), keyMap)
        val endKey = findClosestKey(sampledInput.last(), keyMap)
        
        // 3. Candidate Collection
        val candidates = HashSet<String>()
        val nearbyStart = findNearbyKeys(sampledInput.first(), keyMap, 80f)
        val nearbyEnd = findNearbyKeys(sampledInput.last(), keyMap, 80f)
        
        for (s in nearbyStart) {
            for (e in nearbyEnd) {
                wordsByFirstLastLetter["${s}${e}"]?.let { candidates.addAll(it) }
            }
        }
        
        // Prefix injection: Crucial for "Awake" if we stop at 'A' (Awa)
        if (startKey != null) {
            wordsByFirstLetter[startKey.first()]?.let { words ->
                // Increased candidate pool to ensure Awake is found even if low freq
                candidates.addAll(words.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(60))
            }
        }
        
        // 4. Scoring
        val scored = candidates
            .filter { !isWordBlocked(it) && it.length >= MIN_WORD_LENGTH }
            .sortedWith(compareByDescending<String> { userFrequencyMap[it] ?: 0 }.thenBy { getWordRank(it) })
            .take(200)
            .mapNotNull { word ->
                val template = getOrCreateTemplate(word, keyMap) ?: return@mapNotNull null
                
                val tLen = getPathLength(template.rawPoints)
                val ratio = tLen / inputLength
                
                // RATIO CHECK:
                // Lowered from 0.4f to 0.2f. 
                // Awa (short) vs Awake (long) -> ratio ~3.0 (OK)
                // If swipe is super short, we still want to match if keys align.
                if (ratio < 0.2f) return@mapNotNull null
                
                if (template.sampledPoints == null) {
                    template.sampledPoints = samplePath(template.rawPoints, SAMPLE_POINTS)
                    template.normalizedPoints = normalizePath(template.sampledPoints!!)
                    template.directionVectors = calculateDirectionVectors(template.sampledPoints!!)
                }
                
                // A. Core Geometric Scores
                val shapeScore = calculateShapeScore(normalizedInput, template.normalizedPoints!!)
                val locScore = calculateLocationScore(sampledInput, template.sampledPoints!!)
                val dirScore = calculateDirectionScore(inputDirections, template.directionVectors!!)
                
                // B. Turn & Key Scores
                val templateTurns = detectTurns(template.directionVectors!!)
                val turnScore = calculateTurnScore(inputTurns, templateTurns)
                
                // This returns 0.0 if "Awake" matches A-W-A. 
                // Returns HIGH penalty (3.0 per miss) if "Awesome" matches A-W-A (Missing second A).
                val pathKeyPenalty = calculatePathKeyScore(pathKeys, word)

                // C. Integration
                var integrationScore = (shapeScore * SHAPE_WEIGHT) +
                                       (locScore * LOCATION_WEIGHT) +
                                       (dirScore * DIRECTION_WEIGHT) +
                                       (turnScore * TURN_WEIGHT) +
                                       (pathKeyPenalty * 2.5f)

                // --- PARTIAL MATCH OVERRIDE ---
                // If the user's path keys match the word perfectly (penalty 0),
                // AND the word is significantly longer than the input (ratio > 1.2),
                // it's likely a prefix match (e.g. "Awa" for "Awake").
                // We IGNORE shape/location scores because the tail is missing.
                if (pathKeyPenalty <= 0.1f && ratio > 1.2f) {
                    // Trust the Key Match + Direction. Discard Shape/Location mismatch.
                    integrationScore = (dirScore * DIRECTION_WEIGHT) + (turnScore * TURN_WEIGHT) + 0.1f
                }

                // D. Boosts
                val rank = template.rank
                val freqBonus = 1.0f / (1.0f + 0.15f * ln((rank + 1).toFloat()))
                var userBoost = if ((userFrequencyMap[word] ?: 0) > 0) 1.2f else 1.0f

                // Key Match Boosts
                if (startKey != null && word.startsWith(startKey, ignoreCase = true)) userBoost *= 1.15f
                if (endKey != null && word.endsWith(endKey, ignoreCase = true)) userBoost *= 1.15f
                
                // Path Sequence Boost (if keys matched perfectly)
                if (pathKeyPenalty == 0f) {
                    if (word.length > pathKeys.size) {
                        // "Awake" vs path "A-W-A" -> Bonus for being a predictive match
                        userBoost *= 1.5f 
                    } else {
                        // Exact length match
                        userBoost *= 1.2f
                    }
                }

                val finalScore = (integrationScore * (1.0f - 0.5f * freqBonus)) / userBoost
                Pair(word, finalScore)
            }
        
        return scored.sortedBy { it.second }.distinctBy { it.first }.take(3).map { it.first }
    }

    // =================================================================================
    // END BLOCK: decodeSwipe
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

        // FAST CANDIDATE COLLECTION - fewer candidates for speed
        val candidates = HashSet<String>()

        // 1. Neighbor Search (smaller radius for speed)
        val nearbyStart = findNearbyKeys(startPoint, keyMap, 60f)
        val nearbyEnd = findNearbyKeys(endPoint, keyMap, 60f)

        for (s in nearbyStart) {
            for (e in nearbyEnd) {
                wordsByFirstLastLetter["${s}${e}"]?.let { candidates.addAll(it) }
            }
        }

        // 2. PREFIX INJECTION (fewer candidates)
        if (startKey != null) {
            wordsByFirstLetter[startKey.first()]?.let { words ->
                candidates.addAll(words.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(15))
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

                if (template.sampledPoints == null) {
                    template.sampledPoints = samplePath(template.rawPoints, SAMPLE_POINTS)
                    template.normalizedPoints = normalizePath(template.sampledPoints!!)
                    template.directionVectors = calculateDirectionVectors(template.sampledPoints!!)
                }

                // SIMPLIFIED SCORING - location, direction, and path key matching for speed
                val locScore = calculateLocationScore(sampledInput, template.sampledPoints!!)
                val dirScore = calculateDirectionScore(inputDirections, template.directionVectors!!)

                // Add path key score for better intermediate key matching
                val pathKeyScore = calculatePathKeyScore(pathKeys, word)

                val integrationScore = locScore * 0.4f + dirScore * 0.2f + pathKeyScore * 0.6f  // Path keys most important for preview

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

        return scored.sortedBy { it.second }.distinctBy { it.first }.take(3).map { it.first }
    }
    // =================================================================================
    // END BLOCK: decodeSwipePreview
    // =================================================================================

    // =================================================================================
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
    // FUNCTION: extractPathKeys (v3 - High Sensitivity)
    // SUMMARY: Extracts key characters with lowered thresholds to catch fast/small corners.
    //          Crucial for "Awake" where the 'W' turn might be tight.
    // =================================================================================
    private fun extractPathKeys(path: List<PointF>, keyMap: Map<String, PointF>, maxKeys: Int): List<String> {
        if (path.size < 3) return emptyList()
        val keys = ArrayList<String>()

        // 1. Always add Start Key
        val startKey = findClosestKey(path.first(), keyMap)
        if (startKey != null) keys.add(startKey.lowercase())

        // 2. Detect Turns (Corner Keys)
        var lastAddedIdx = 0
        
        // Use a smaller buffer (1) to catch turns immediately after start
        for (i in 2 until path.size - 2) {
            val p1 = path[i - 2]
            val p2 = path[i]
            val p3 = path[i + 2]

            val v1x = p2.x - p1.x
            val v1y = p2.y - p1.y
            val v2x = p3.x - p2.x
            val v2y = p3.y - p2.y

            val len1 = kotlin.math.hypot(v1x, v1y)
            val len2 = kotlin.math.hypot(v2x, v2y)

            // REDUCED THRESHOLD: 10f -> 5f
            // Catches tighter corners in small keyboards or fast swipes
            if (len1 > 5f && len2 > 5f) {
                val dot = (v1x * v2x + v1y * v2y) / (len1 * len2)
                
                // Dot < 0.75 is ~41 degrees. This is generous enough.
                if (dot < 0.75) { 
                    // REDUCED BUFFER: 4 -> 2
                    // Allows capturing 'W' even if it's very close to 'A' in time/points
                    if (i - lastAddedIdx > 2) {
                        val key = findClosestKey(p2, keyMap)
                        if (key != null && (keys.isEmpty() || keys.last() != key.lowercase())) {
                            keys.add(key.lowercase())
                            lastAddedIdx = i
                        }
                    }
                }
            }
        }

        // 3. Always add End Key
        val endKey = findClosestKey(path.last(), keyMap)
        if (endKey != null && (keys.isEmpty() || keys.last() != endKey.lowercase())) {
            keys.add(endKey.lowercase())
        }

        return keys.take(maxKeys)
    }

    // =================================================================================
    // FUNCTION: calculatePathKeyScore (v4 - Aggressive Penalty)
    // SUMMARY: Penalizes words that do not contain the keys found at path corners.
    //          Uses "Subsequence Matching": keys must appear in the word in correct order.
    //          The penalty is boosted to 3.0f per miss to aggressively disqualify "Area".
    // =================================================================================
    private fun calculatePathKeyScore(pathKeys: List<String>, word: String): Float {
        if (pathKeys.isEmpty()) return 0f
        
        val wordChars = word.lowercase()
        var pathIdx = 0
        var wordIdx = 0
        var matchedKeys = 0
        
        // Iterate through path keys and try to find them in the word (in order)
        while (pathIdx < pathKeys.size && wordIdx < wordChars.length) {
            val pKey = pathKeys[pathIdx]
            val wChar = wordChars[wordIdx].toString()
            
            if (pKey == wChar) {
                // Match found! Advance both.
                matchedKeys++
                pathIdx++
                wordIdx++ 
            } else {
                // No match at current char. 
                // Advance word index to search later in the word.
                // (e.g. searching for 'K' in "Awake" -> skip 'w', 'a'...)
                wordIdx++
            }
        }
        
        // Calculate Unmatched Keys (Keys we swiped but didn't find in order)
        // e.g. Swiping 'W' in "Area". 'W' is not in "Area". Unmatched = 1.
        val unmatchedKeys = pathKeys.size - matchedKeys
        
        // PENALTY CALCULATION:
        // Base penalty: 3.0f per missing key.
        // This is massive. Standard weights are 0.8f, so 3.0 * 0.8 = 2.4 total score impact.
        // Good matches usually have a total score < 0.5. This ensures "Area" is rejected.
        var penalty = unmatchedKeys * 3.0f 
        
        // Length Penalty:
        // If word is MUCH longer than the path (e.g. path 'a' vs 'android')
        // We add a small penalty to prefer shorter matches for short swipes.
        // "Awake" (5) vs Path "A-W-K-E" (4) -> Ratio 1.25 (OK)
        // "Awesome" (7) vs Path "A-W-E" (3) -> Ratio 2.3 (Penalty)
        if (word.length > pathKeys.size * 2.5) {
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
        var lastKeyPos: PointF? = null
        
        for (char in word) {
            val keyPos = keyMap[char.toString().uppercase()] ?: keyMap[char.toString().lowercase()] ?: return null
            

            // DOUBLE LETTER LOGIC:
            if (lastKeyPos != null && keyPos.x == lastKeyPos.x && keyPos.y == lastKeyPos.y) {
                // INCREASED: 10f -> 15f. 
                // Makes the "circle" gesture easier to register.
                rawPoints.add(PointF(keyPos.x + 15f, keyPos.y + 15f))
            }
            
            rawPoints.add(PointF(keyPos.x, keyPos.y))
            lastKeyPos = keyPos
        }

        if (rawPoints.size < 2) return null

        val t = WordTemplate(word, getWordRank(word), rawPoints)
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

    // =================================================================================
    // FUNCTION: calculateLocationScore (v2 - Prefix Friendly)
    // SUMMARY: Reduced the massive penalty for endpoint mismatch.
    //          Allows "Awake" to survive even if the swipe stops at "A".
    // =================================================================================
    private fun calculateLocationScore(input: List<PointF>, template: List<PointF>): Float {
        var totalDist = 0f
        var totalWeight = 0f
        val size = input.size
        
        for (i in input.indices) {
            val dist = hypot(input[i].x - template[i].x, input[i].y - template[i].y)
            
            // --- ENDPOINT WEIGHTING ---
            // Start: 3.0x (Anchor is important)
            // End:   2.5x (Reduced from 5.0x)
            //        We want to punish sloppy endings, but NOT kill prefix matches.
            val w = when {
                i < size * 0.15 -> 3.0f
                i > size * 0.85 -> 2.5f 
                else -> 1.0f
            }
            
            totalDist += dist * w
            totalWeight += w
        }
        return totalDist / totalWeight
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
}
