package com.example.coverscreentester

import java.util.*

class PredictionEngine {

    class TrieNode {
        val children = HashMap<Char, TrieNode>()
        var isEndOfWord = false
        var word: String? = null
    }

    private val root = TrieNode()

    init {
        // Initial "Baby Dictionary" for testing plumbing.
        // In the future, we will load a raw resource file here.
        val commonWords = listOf(
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
            "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
            "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
            "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
            "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
            "hello", "droid", "trackpad", "keyboard", "launcher", "google", "android",
            "thanks", "please", "yes", "no", "good", "bad", "maybe", "tomorrow",
            "today", "yesterday", "time", "work", "home", "call", "text", "love"
        )

        for (word in commonWords) {
            insert(word)
        }
    }

    fun insert(word: String) {
        var current = root
        for (char in word.lowercase(Locale.ROOT)) {
            current = current.children.computeIfAbsent(char) { TrieNode() }
        }
        current.isEndOfWord = true
        current.word = word
    }

    fun getSuggestions(prefix: String, maxResults: Int = 3): List<String> {
        if (prefix.isEmpty()) return emptyList()

        val cleanPrefix = prefix.lowercase(Locale.ROOT)
        var current = root

        // 1. Navigate to the node representing the prefix
        for (char in cleanPrefix) {
            current = current.children[char] ?: return emptyList()
        }

        // 2. Perform Depth-First Search to find all words below this node
        val results = ArrayList<String>()
        searchRecursive(current, results, maxResults)

        // 3. Sort by length (shorter words first usually better for prefix)
        return results.sortedBy { it.length }
    }

    private fun searchRecursive(node: TrieNode, results: MutableList<String>, max: Int) {
        if (results.size >= max) return

        if (node.isEndOfWord) {
            node.word?.let { results.add(it) }
        }

        for (child in node.children.values) {
            searchRecursive(child, results, max)
        }
    }
}
