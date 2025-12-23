package com.example.coverscreentester

import android.graphics.PointF
import java.util.*
import kotlin.math.hypot

class PredictionEngine {

    class TrieNode {
        val children = HashMap<Char, TrieNode>()
        var isEndOfWord = false
        var word: String? = null
    }

    private val root = TrieNode()
    // Keep a flat list for faster swipe filtering
    private val wordList = ArrayList<String>()

    init {
        // Expanded dictionary for better testing
        val commonWords = listOf(
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
            "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
            "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
            "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
            "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
            "hello", "droid", "trackpad", "keyboard", "launcher", "google", "android",
            "thanks", "please", "yes", "no", "good", "bad", "maybe", "tomorrow",
            "today", "yesterday", "time", "work", "home", "call", "text", "love",
            "great", "swipe", "gesture", "typing", "works", "cool", "fast", "slow",
            "screen", "cover", "display", "mouse", "click", "double", "right", "left"
        )

        for (word in commonWords) {
            insert(word)
        }
        android.util.Log.d("DroidOS_Swipe", "Dictionary loaded: ${wordList.size} words")
    }

    fun insert(word: String) {
        val lower = word.lowercase(Locale.ROOT)
        if (!wordList.contains(lower)) wordList.add(lower)

        var current = root
        for (char in lower) {
            current = current.children.computeIfAbsent(char) { TrieNode() }
        }
        current.isEndOfWord = true
        current.word = lower
    }

    fun getSuggestions(prefix: String, maxResults: Int = 3): List<String> {
        if (prefix.isEmpty()) return emptyList()

        val cleanPrefix = prefix.lowercase(Locale.ROOT)
        var current = root
        for (char in cleanPrefix) {
            current = current.children[char] ?: return emptyList()
        }

        val results = ArrayList<String>()
        searchRecursive(current, results, maxResults)
        return results.sortedBy { it.length }
    }

    private fun searchRecursive(node: TrieNode, results: MutableList<String>, max: Int) {
        if (results.size >= max) return
        if (node.isEndOfWord) node.word?.let { results.add(it) }
        for (child in node.children.values) searchRecursive(child, results, max)
    }

    // --- SWIPE DECODER LOGIC ---

    fun decodeSwipe(swipePath: List<PointF>, keyMap: Map<String, PointF>): List<String> {
        if (swipePath.size < 5) {
            android.util.Log.d("DroidOS_Swipe", "Swipe too short: ${swipePath.size} points")
            return emptyList()
        }

        // 1. Identify Start and End Regions
        val startPt = swipePath.first()
        val endPt = swipePath.last()

        val startKey = findClosestKey(startPt, keyMap)
        val endKey = findClosestKey(endPt, keyMap)

        android.util.Log.d("DroidOS_Swipe", "Swipe: ${swipePath.size} pts. Start: $startKey (${startPt.x.toInt()},${startPt.y.toInt()}), End: $endKey (${endPt.x.toInt()},${endPt.y.toInt()})")

        if (startKey == null || endKey == null) return emptyList()

        // 2. Filter Candidates
        val candidates = wordList.filter {
            it.startsWith(startKey, ignoreCase = true) &&
            it.endsWith(endKey, ignoreCase = true)
        }

        android.util.Log.d("DroidOS_Swipe", "Candidates starting with $startKey, ending with $endKey: ${candidates.size}")

        if (candidates.isEmpty()) return emptyList()

        // 3. Score Candidates
        val scored = candidates.map { word ->
            val score = calculatePathScore(word, swipePath, keyMap)
            android.util.Log.v("DroidOS_Swipe", "Scoring '$word': $score")
            Pair(word, score)
        }

        // 4. Return top matches
        val results = scored.sortedBy { it.second }.take(3).map { it.first }
        android.util.Log.d("DroidOS_Swipe", "Result: $results")
        return results
    }

    private fun findClosestKey(pt: PointF, keyMap: Map<String, PointF>): String? {
        var closestKey: String? = null
        var minDist = Float.MAX_VALUE

        for ((key, pos) in keyMap) {
            val dist = hypot(pt.x - pos.x, pt.y - pos.y)
            if (dist < minDist) {
                minDist = dist
                closestKey = key
            }
        }
        // Only return if reasonably close (e.g. within 150px) to avoid matching across screen
        return if (minDist < 200) closestKey else null
    }

    private fun calculatePathScore(word: String, path: List<PointF>, keyMap: Map<String, PointF>): Float {
        var totalDist = 0f

        // For each letter in the word, find the closest point on the swipe path
        // The points on path must appear in chronological order roughly
        var pathIndex = 0

        for (char in word) {
            val keyPos = keyMap[char.toString().uppercase()] ?: keyMap[char.toString()] ?: continue

            var localMin = Float.MAX_VALUE
            var bestIndex = pathIndex

            // Search ahead in the path
            for (i in pathIndex until path.size) {
                val dist = hypot(path[i].x - keyPos.x, path[i].y - keyPos.y)
                if (dist < localMin) {
                    localMin = dist
                    bestIndex = i
                }
            }

            totalDist += localMin
            // Advance our search window so we don't go backwards (enforce letter order)
            pathIndex = bestIndex
        }

        // Normalize by word length so long words aren't penalized
        return totalDist / word.length
    }
}
