# DroidOS Workflow Memory

Keep this file focused on durable process rules and validation commands.

## Patch Workflow
- Normal implementation uses `plan.md` first.
- Apply using `python3 builder.py plan.md`.
- Do not directly edit DroidOS `.kt` files unless the user explicitly directs otherwise.
- Prefer new focused `.kt` classes for new functionality instead of adding more logic to existing god classes; keep god-class changes to minimal delegation/wiring when possible.
- Have a reviewer agent review `plan.md` unless the user specifically says not to.
- Iterate on `plan.md` until reviewer passes. Reviewer scope includes goal fit, feature completeness, bug/security risk, anchor/build correctness, conflicts with other app behavior, and general software engineering quality.
- Use `subagentnudge.sh` with `inject-strong` for preparer/reviewer messages. Do not tail the reviewer pane by default; let the reviewer nudge back when complete.
- After reviewer pass, summarize the plan for user approval before build/install. Run `builder.py` and `adb install -r` only after approval or explicit instruction.

## Reporting
- Final task reports should include changed files, build/install result, and any runtime validation or remaining on-device validation gaps.

## Search
- Prefer `rg -n` for code and planhistory searches.
- Search `planhistory.md` by `REASON` when prior patch rationale or exact old blocks matter.

