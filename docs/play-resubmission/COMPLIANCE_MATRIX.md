# DroidOS Launcher Play compliance and feature-disposition matrix

Snapshot 2026-11-05. Status terms: RETAIN = API/feature remains in scope; MODIFY = code must change or is partially patched; INVESTIGATE = not cleared without evidence; EXCLUDE = keep out of the first Play binary. This is a source audit, not an approval determination.

| Feature | Disposition | Android API / permission / backend | Policy and implementation finding / gate |
| --- | --- | --- | --- |
| Floating launcher/drawer | RETAIN | AccessibilityService, WindowManager TYPE_ACCESSIBILITY_OVERLAY; sometimes SYSTEM_ALERT_WINDOW | User-selected overlays, no spoofed system UI; verify overlay-denied behavior. |
| Accessibility window events | MODIFY | BIND_ACCESSIBILITY_SERVICE, canRetrieveWindowContent, flagRetrieveInteractiveWindows | Current typeAllMask is broad; scope event types and minimize content reads. User-visible disclosure and affirmative consent required; isAccessibilityTool=false is correct unless proven otherwise. |
| Key handling and gestures | RETAIN conditional | AccessibilityService.onKeyEvent / dispatchGesture / global actions | Only deterministic user-directed actions; never automate Android permission/security consent or sensitive data access. |
| IME/trackpad companion | INVESTIGATE | InputMethodService in sibling app, cross-app explicit intent/broadcast, SYSTEM_ALERT_WINDOW | Separate app review; verify signature trust and cross-app foreground starts on API36. |
| Semantic broadcast automation | MODIFY | Dynamically exported BroadcastReceiver, sender UID/signature validation, user opt-in | WINDOW_MANAGER and REMOTE_KEY actions can perform privileged operations; current trust gate is insufficient, especially if sender identity unknown. |
| Launcher app discovery | RETAIN | PackageManager queryIntentActivities ACTION_MAIN/CATEGORY_LAUNCHER, manifest queries | Current scoped launcher queries are preferable to QUERY_ALL_PACKAGES; audit linked apps. |
| Standard app launch | MODIFY | PackageManager.getLaunchIntentForPackage, ActivityOptions, Context.startActivity | Public gateway added; no silent shell fallback in Play. Background-activity-start limitations still apply. |
| Launch on Display 0/1/secondary | MODIFY | DisplayManager.getDisplay, FEATURE_ACTIVITIES_ON_SECONDARY_DISPLAYS, ActivityManager.isActivityStartAllowedOnDisplay | Target eligibility check added. Samsung private/cover display can reject; fail closed without privileged retry. |
| Launch bounds/freeform | RETAIN conditional | ActivityOptions.setLaunchBounds | Advisory only; accepted only if OS/OEM freeform supports it. |
| Move existing task between displays | EXCLUDE from normal Play flow | Hidden IActivityTaskManager.moveRootTaskToDisplay, am task move-task-to-display | Replace with target-display session request; hidden API remains a separate high-risk feature to isolate. |
| New per-display workspace | MODIFY | Normal app launch + observed task state + saved queue profile | Queue/profile restore currently not guaranteed on all displays; no promise of native task movement. |
| Same-display minimize/focus/restore | INVESTIGATE | am task, hidden TaskManager, Shizuku | Analyze task ownership, stale IDs, user interaction and background-start equivalence; avoid reactivation bypass. |
| Privileged task resize/freeform mode | INVESTIGATE | am task resize/set-windowing-mode, hidden Binder | May preserve for legitimate tasks with user consent; test OS/API/OEM permissions. |
| Resolution and DPI | INVESTIGATE | wm size/density -d, SurfaceControl/DisplayManager private APIs | Shizuku/shell backend; require type-specific permission and display-target validation. |
| Orientation and refresh | INVESTIGATE | settings put system, cmd display, privileged per-display controls | User-directed changes only; restore device defaults on failure. |
| Cover UI | RETAIN conditional | Accessibility overlay, display-context WindowManager | Must work within Samsung/Android display policy; no cover allowlist bypass. |
| External/virtual display | INVESTIGATE | DisplayManager/createVirtualDisplay, shell display commands | Private or virtual display launch permission is different from mere display existence. |
| Input injection and cursor | INVESTIGATE | Accessibility.dispatchGesture vs shell input injection/Binder | Map actual input events, per-display routing and security restrictions. |
| Shizuku authorization | RETAIN conditional | ShizukuProvider / user-bound user service | Explicit user grant; recover from absence, death and revocation. Optional for basic Play onboarding. |
| Generic privileged runCommand | MODIFY BLOCKER | IShellService.runCommand + Runtime.exec | First patch blocks known start/accessibility patterns; generic shell API remains Play-reachable. Replace with narrow typed, validated commands and Play source-graph isolation. |
| Programmatic Accessibility enabling | EXCLUDE | settings put secure enabled_accessibility_services / accessibility_enabled | Play path now opens Settings; no silent enable or toggling companion Accessibility. Must check all other entry points. |
| Foreground service specialUse | MODIFY | FOREGROUND_SERVICE_SPECIAL_USE, PROPERTY_SPECIAL_USE_FGS_SUBTYPE | Current AccessibilityService did not call its foreground helper; Play manifest overlay removes specialUse declaration. Validate merged AAB. If any real FGS is introduced, separate service with declared type and Play justification. |
| Purchase and restore | MODIFY | com.android.vending.BILLING, BillingClient | Migrated to supported Billing 8.3; untested until signed Play track/test account. Validate receipt verification/entitlement security. |
| GitHub/Paddle commerce | EXCLUDE in Play where rules require | BuildConfig USE_GITHUB_AUTH=false; purchase UI/navigation | Audit every deep link, external website and resource entry point, not just visible buttons. Regional programs require actual enrollment. |
| App package/update distribution | INVESTIGATE | INTERNET, intents, download code | Ensure no executable DEX/JAR/SO/APK self-update distribution in the Play binary; current source does not prove final binary clean. |
| Clipboard/screenshots/logs | INVESTIGATE | ClipboardManager, ImageReader, accessibility window visibility, network | Collect minimum required; confirm telemetry/transmission and Data Safety truthfulness. |
| AIUI autonomous actions | EXCLUDE | AI-driven Accessibility automation | Outside initial Play scope, irrespective of private product progress. |
| Package/signing/version | BLOCKER | com.katsuyamaki.DroidOSLauncher, versionCode 4 | Original app is suspended. Check appeal permission and signing history before making a numbered upload. |

## Critical known deficiencies after the first patches

1. **Structural source separation is incomplete.** Shared main-source launch and generic shell code can still be compiled in the Play variant. BuildConfig guards plus a string denylist are interim containment, not proof that prohibited paths are absent from the artifact.
2. **Receiver/IPC security remains open.** Dynamic exported command receiver includes window-control commands and accommodates external integrations. Need explicit sender trust and semantic per-action permissions, not senderPackages-is-empty trust.
3. **Post-launch observation is incomplete.** Public launch only requests a display and bounds; it must verify where the new task actually landed and adopt only legitimate results.
4. **Automated queue restoration and stale task recovery are not independently proven.** Recreating new sessions may be constrained by background start limits and OEM cover policy.
5. **Accessibility scope, data disclosure, privacy network review and billing validation are pending.**
6. **No signed Play AAB or Samsung hardware test has established release readiness.**

Review against current policy, not anecdotes:
- https://support.google.com/googleplay/android-developer/answer/10964491
- https://support.google.com/googleplay/android-developer/answer/16559646
- https://support.google.com/googleplay/android-developer/answer/16558241
- https://support.google.com/googleplay/android-developer/answer/10281818
- https://developer.android.com/guide/components/activities/background-starts
