package com.katsuyamaki.DroidOSLauncher

/**
 * Central registry for app-specific compatibility behavior.
 *
 * To add more problematic apps later, add a new Rule entry in [rules].
 */
object AppCompatibilityRegistry {

    data class Rule(
        val equivalentPackages: Set<String> = emptySet(),
        val preferPackageTaskMatch: Boolean = false,
        val forcePackageLaunch: Boolean = false,
        val mergeTaskIdentity: Boolean = false
    )

    private val rules: Map<String, Rule> = mapOf(
        // Gemini often trampolines into Google Quick Search Box activities.
        "com.google.android.apps.bard" to Rule(
            equivalentPackages = setOf("com.google.android.googlequicksearchbox"),
            preferPackageTaskMatch = true,
            forcePackageLaunch = true,
            mergeTaskIdentity = true
        ),
        "com.google.android.googlequicksearchbox" to Rule(
            equivalentPackages = setOf("com.google.android.apps.bard"),
            preferPackageTaskMatch = true,
            forcePackageLaunch = false,
            mergeTaskIdentity = true
        ),

        // Docs/Sheets are related but should remain independent queue/task identities.
        "com.google.android.apps.docs" to Rule(
            equivalentPackages = setOf(
                "com.google.android.apps.docs.editors.docs",
                "com.google.android.apps.docs.editors.sheets"
            ),
            preferPackageTaskMatch = false,
            forcePackageLaunch = false,
            mergeTaskIdentity = false
        ),
        "com.google.android.apps.docs.editors.docs" to Rule(
            equivalentPackages = setOf(
                "com.google.android.apps.docs",
                "com.google.android.apps.docs.editors.sheets"
            ),
            preferPackageTaskMatch = false,
            forcePackageLaunch = false,
            mergeTaskIdentity = false
        ),
        "com.google.android.apps.docs.editors.sheets" to Rule(
            equivalentPackages = setOf(
                "com.google.android.apps.docs",
                "com.google.android.apps.docs.editors.docs"
            ),
            preferPackageTaskMatch = false,
            forcePackageLaunch = false,
            mergeTaskIdentity = false
        )
    )

    fun normalizePackage(rawPackage: String?): String {
        if (rawPackage.isNullOrBlank()) return ""
        return rawPackage.substringBefore(':').substringBefore('/')
    }

    fun equivalentPackagesFor(rawPackage: String?): Set<String> {
        val normalized = normalizePackage(rawPackage)
        if (normalized.isEmpty()) return emptySet()

        val out = linkedSetOf(normalized)
        rules[normalized]?.equivalentPackages?.forEach { out.add(normalizePackage(it)) }

        // Reverse lookup so we don't need to duplicate every alias rule forever.
        for ((key, rule) in rules) {
            if (rule.equivalentPackages.any { normalizePackage(it) == normalized }) {
                out.add(normalizePackage(key))
            }
        }

        return out
    }

    fun packagesEquivalent(lhs: String?, rhs: String?): Boolean {
        val left = normalizePackage(lhs)
        val right = normalizePackage(rhs)
        if (left.isEmpty() || right.isEmpty()) return false
        if (left == right) return true

        val leftSet = equivalentPackagesFor(left)
        val rightSet = equivalentPackagesFor(right)
        return leftSet.contains(right) || rightSet.contains(left) || leftSet.intersect(rightSet).isNotEmpty()
    }

    fun shouldMergeTaskIdentity(rawPackage: String?): Boolean {
        return equivalentPackagesFor(rawPackage).any { rules[it]?.mergeTaskIdentity == true }
    }

    fun taskMatchPackagesFor(rawPackage: String?): Set<String> {
        val normalized = normalizePackage(rawPackage)
        if (normalized.isEmpty()) return emptySet()

        return if (shouldMergeTaskIdentity(normalized)) {
            equivalentPackagesFor(normalized)
        } else {
            setOf(normalized)
        }
    }

    fun packagesEquivalentForTaskIdentity(lhs: String?, rhs: String?): Boolean {
        val left = normalizePackage(lhs)
        val right = normalizePackage(rhs)
        if (left.isEmpty() || right.isEmpty()) return false
        if (left == right) return true

        if (!shouldMergeTaskIdentity(left) && !shouldMergeTaskIdentity(right)) return false
        return packagesEquivalent(left, right)
    }

    fun shouldPreferPackageTaskMatch(rawPackage: String?): Boolean {
        return equivalentPackagesFor(rawPackage).any { rules[it]?.preferPackageTaskMatch == true }
    }

    fun shouldForcePackageLaunch(rawPackage: String?): Boolean {
        return equivalentPackagesFor(rawPackage).any { rules[it]?.forcePackageLaunch == true }
    }

    fun resolveLaunchClass(rawPackage: String?, className: String?): String? {
        if (shouldForcePackageLaunch(rawPackage)) return null

        val trimmed = className?.trim()
        if (trimmed.isNullOrEmpty() || trimmed == "null" || trimmed == "default") return null
        return trimmed
    }
}