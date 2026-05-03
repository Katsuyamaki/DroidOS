# DroidOS Project Memory

Purpose: small, always-read project brief for fresh agent sessions. Keep this file under roughly 100 lines. Do not turn it into a task log.

## Load Rules
- Read this file first on fresh DroidOS sessions.
- Load `memory/*.md` files only when relevant to the current task.
- Use TerminalAssistant SQLite/event search for raw history, old completions, nudges, and task logs.
- Put proposed durable memory updates in `memory/inbox.md`; do not auto-edit curated memory files unless explicitly asked.

## Project Shape
- `DroidOSLauncher/`: launcher/windowing side of DroidOS.
- `DroidOSKeyboardTrackpad/`: keyboard/trackpad and input behavior.
- `Cover-Screen-Launcher/`: cover-screen launcher variant.
- `Cover-Screen-Trackpad/`: cover-screen trackpad variant.
- `builder.py`: project-approved patch application and build/install flow from `plan.md`.
- `plan.md`: current patch plan source for normal implementation work.
- `planhistory.md`: historical applied plans; useful for rationale and prior exact code blocks.
- `TerminalAgentChat.md`: human-readable subagent handoff and request log.

## Operating Rules
- Normal code changes must go through `plan.md` and `builder.py`.
- Direct `.kt` edits are denied for DroidOS unless the user explicitly directs otherwise.
- Plan workflow is required: prepare/edit `plan.md`, have a reviewer agent review it, iterate until reviewer passes, summarize for user review, then run approved build/install such as `adb install -r`.
- Reviewer should check goal fit, feature completeness, bugs, security risks, anchor/build errors, conflicts with existing app behavior, and standard software development practices.
- Existing large Kotlin files are god classes and should be refactored over time; new functionality should be added in new focused `.kt` classes when practical, with existing god classes only delegating/wiring to them.
- TP0/TD0 style preparer and reviewer coordination should use `subagentnudge.sh` with `inject-strong`; no need to tail the other pane while waiting for review.

## Memory Files
- `memory/architecture.md`: module/service map and durable architecture notes.
- `memory/important_files.md`: high-signal file map by subsystem.
- `memory/workflows.md`: build/install/test/review workflow details.
- `memory/decisions.md`: durable decisions and rationale.
- `memory/known_issues.md`: active or recurring issues only.
- `memory/inbox.md`: staging area for proposed memory updates.

## Promotion Rule
Promote an item into curated memory only if it would likely prevent a future agent from wasting significant time, repeating a mistake, or misunderstanding a durable design choice.

