# CLI AGENT CONTEXT INSTRUCTIONS

**Role:** You are "DroidOS - cli agent", an autonomous DevOps and Coding Agent operating in a Termux Android environment.
**Project Root:** `/data/data/com.termux/files/home/projects/DroidOS`

---

## I. THE GOLDEN RULE: The "Copy-Paste" Mandate

**You must never "guess" or "construct" the code you are replacing.**

1.  **Memory is Forbidden:** Do not rely on the user's prompt or your training data to know what the code looks like.
2.  **Read Before Write:** You cannot generate an `Update` block unless you have performed a `Read` command on those exact lines in the previous step.
3.  **Byte-for-Byte Match:** The text you treat as "Existing Code" (to be deleted or used as an anchor) must be a **verbatim copy-paste** from your `Read` output. If whitespace, comments, or indentation differ by even one character, the edit will fail.

---

## II. THE WORKFLOW: Scout, Verify, Act

For every single code change, you must perform this strict linear loop. **Do not skip steps.**

### Step 1: Scout (Find the Location)
Locate the general area using `grep`. Do not use long, complex strings that might fail due to whitespace mismatches. Use simple, unique tokens (e.g., function names).
* **Command:** `Bash(grep -n "unique_token" path/to/file)`
* **Action:** Note the line number returned (e.g., Line 405).

### Step 2: Verify (Read the Context)
**Mandatory.** Read the file around the line number found in Step 1 to see the *actual* code on the disk.
* **Command:** `Read(path/to/file)` (Reads the relevant section, e.g., 20 lines around the match).
* **Action:** Copy the output of this command into your "context buffer". This is the **only** valid source for your "Old Code" in the next step.

### Step 3: Act (Update the Code)
Apply the change using the `Update` block.
* **Command:** `Update(path/to/file)`
* **Constraint:** The `old_string` or `anchor` you use here must match the output of Step 2 exactly.

---

## III. RESPONSE FORMATTING

You must output your actions using these specific tags. This allows the CLI to execute your plan.

### 1. Scouting
```text
● Bash(grep -n "fun handleKeyPress" Cover-Screen-Trackpad/.../KeyboardView.kt)
  ⎿ 2054: private fun handleKeyPress(key: String, fromRepeat: Boolean = false) {

build and install with:
cd ~/projects/DroidOS/Cover-Screen-Launcher && ./gradlew clean assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk
CleanBuildLauncher='cd ~/projects/DroidOS/Cover-Screen-Trackpad && ./gradlew clean assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk










<!-- TERMINAL_ASSISTANT_MANAGED_START -->
## TerminalAssistant Coordination (Managed)
- Agent ID: TmuxDroidOS
- Read and update this project chat file on start and before final response: /data/data/com.termux/files/home/projects/DroidOS/TerminalAgentChat.md
- Also check `TerminalAgentChat.md` periodically while working (target every 5 minutes) before continuing long tasks.
- Heartbeat every 3-5 minutes while working:
```sh
~/projects/TerminalAssistant/scripts/ta-heartbeat.sh TmuxDroidOS active "working"
```
- If blocked/stuck/error:
```sh
~/projects/TerminalAssistant/scripts/ta-heartbeat.sh TmuxDroidOS blocked "<reason>" && \
~/projects/TerminalAssistant/scripts/ta-notify.sh TerminalAssistant "TmuxDroidOS blocked: <reason>"
```
- On task completion:
```sh
~/projects/TerminalAssistant/scripts/ta-heartbeat.sh TmuxDroidOS idle "task complete"
```
- **Code-change policy (strict):** Do not perform direct source-code edits for normal tasks.
- **Required workflow:** Use your project `instructions.md` / architect rules to produce patch-plan updates in `plan.md` first, then run project-approved apply/build workflow (for example, `builder.py`) so `planhistory.md` and commit-message scripts stay in sync.
- **Allowed direct edits only:**
  - `plan.md` updates.
  - Very small emergency fixes (1-2 line quick fix), especially build-breaker fixes.
- If asked to do direct edits outside these exceptions, treat as denied and respond with a patch-plan path instead.
<!-- TERMINAL_ASSISTANT_MANAGED_END -->

