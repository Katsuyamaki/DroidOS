# DroidOS Known Issues

Track active or recurring issues only. Remove or move resolved one-off issues out of this file.

## Active / Recurring
- Direct `.kt` edits in DroidOS can cause process confusion. Use `plan.md`, reviewer pass, user approval, then builder/build/install unless explicitly directed otherwise.
- Several main `.kt` files may be god classes. New feature work should avoid growing them further and should introduce focused classes with minimal integration hooks where practical.

