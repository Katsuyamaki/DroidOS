# DroidOS Play Console package — REVIEW DRAFT, NOT AUTHORIZED FOR SUBMISSION

Owner approval is required before any Play Console upload or publication. This document is a working request for a future human policy/engineering review. All untested claims are marked conditional.

## Suggested Play listing (accuracy must be rechecked on final AAB)

App name: DroidOS Launcher — Android Desktop
Short description: Floating app launcher, workspace queues and display controls for Android.
Long description:
DroidOS Launcher provides a floating workspace panel for browsing and opening apps, arranging saved app queues and requesting multi-window layouts where supported by the device. Supported phones, foldables and external displays can use display-aware launch controls. Some advanced display, window and input operations require optional user-activated Shizuku/ADB privileges; without them, public Android launcher functions remain available. Android and device manufacturers determine whether an app may launch on a specific display and whether freeform bounds are applied. DroidOS does not override a denied display launch in the Google Play edition. Companion input features may require the separately installed DroidOS Trackpad Keyboard app.
Do not advertise arbitrary cover-app launching, guaranteed cross-display task movement, or virtual-display functionality until each has Play hardware evidence.

## Draft reviewer note

DroidOS Launcher is a user-directed Android desktop/window utility. Users manually select apps, display targets, workspace queue profiles, shortcuts and window actions. The Google Play variant uses documented Android display checks and standard activity-start APIs for new application launches. If Android or Samsung does not permit a launch onto the chosen display, the request fails without a privileged background activity-start fallback.

The launcher offers a user-enabled AccessibilityService for visible window/focus information, keyboard shortcuts, overlays and user-requested gesture/global actions. It is not designated a disability-specific Accessibility tool. An in-app disclosure precedes the Android Accessibility Settings screen. Users must activate/revoke the service themselves in system Settings. No Accessibility auto-enablement or security-consent bypass should be present in the final Play build.

The optional Shizuku backend is activated through user-approved Android/ADB setup and is intended for narrowly documented window, display and input controls. Basic launcher features should remain usable without Shizuku; do not imply that privileged functionality is silently available. The first Play variant has no valid specialUse foreground-service justification unless a separate continuous user-visible FGS is implemented and declared.

An existing Play-distributed desktop utility, Dextop by NarYuki, publicly advertises some comparable features and documents a user-initiated privileged runtime. This is comparison evidence, not a claim that Google approved DroidOS's implementation or that Dextop's current source is byte-identical to its Play AAB.

Reviewer setup: install the exact signed Play candidate and, if companion functions are evaluated, its separately declared input app. Open DroidOS, complete the on-screen disclosure and explicitly enable Accessibility in Android Settings. Allow overlay access if requested. Open the floating panel, launch a regular app on the permitted main display, select a saved queue workspace, and test an unsupported target: the attempt should be denied or gracefully redirected without privileged shell escalation. To test optional Shizuku features, launch Shizuku using its own documented setup, authorize DroidOS explicitly, and run only the documented control actions. Revoke Accessibility and Shizuku to verify safe degraded behavior.

This note must be updated with exact final manifest permissions, demo links, test account details, product IDs, signed AAB hash, supported devices and any reviewer-specific prerequisites.

## Permission declarations and disclosures to prepare

- Accessibility: describe exact events and window content accessed, when and why, what is acted on, and data-sharing practices. Show a prominent in-app dialog with affirmative consent BEFORE opening Settings. The present typeAllMask can be broader than the task and must be audited.
- Overlay: explain floating launcher/trackpad UI and permission denial behavior. Never misrepresent overlays as Android system UI.
- Foreground service: no specialUse claim in the current Play manifest overlay. Do not file a specialUse declaration for a service that is not actually running in foreground. If a future dedicated service exists, supply its precise continuous activity, notification and stop action.
- Shizuku/ADB: explicit user setup and permission grant; clarify absence/death, persistence, and what privileged commands actually perform.
- Package visibility: only Launcher-intent and declared package queries where necessary.
- Billing: Play Billing 8.3 one-time purchases, restore with queryPurchasesAsync, acknowledge only purchased entitlements, verify purchase tokens/ownership before unlock, no alternative payment links unless valid regional program is configured.
- Data Safety: UNKNOWN UNTIL AUDITED. Inspect INTERNET endpoints, crash/diagnostic transmission, app/window metadata, clipboard, screenshots, logcat and any companion app boundaries. Do not assert 'no data collected/shared' without binary/runtime evidence.
- Executable code: inspect final Play package and runtime for executable downloads/self-updates, including GitHub link handling.
- Exported IPC: list dynamic/static receivers and ShizukuProvider permissions, validate trusted senders and opt-in external automation.

## Reviewer demo-video storyboard (not yet recorded)

00:00 — Show app name/version and installed package on clean target device.
00:20 — Open onboarding and show Accessibility purpose/disclosure; decline then affirmatively accept.
00:45 — Manually enable the service in Android Settings; return and open floating launcher.
01:10 — Select main Display 0, launch another ordinary app using public Android API; show real task/display.
01:35 — Save and restore a queue profile; demonstrate freeform bounds only where OS supports them.
02:05 — Select an unavailable/private display or unsupported app target; show truthful denial and no shell escalation.
02:30 — Optional Shizuku activation with user approval; show relevant display/window control and loss-of-service behavior.
03:05 — Revoke Accessibility and deny overlay; show no auto re-enable and clear recovery instructions.
03:35 — Show Play Billing product page, test purchase and restore (sandbox tester only).
04:00 — End with package/version/hash, device/OS/One UI and explicit tested capability summary.
Actual video timings are illustrative; never show simulated capabilities as real device test results.

## Required manual test matrix — all NOT RUN until hardware verification

| Scenario | Expected result | Status |
| --- | --- | --- |
| Galaxy Z Flip SM-F766U1 main Display 0 | Public launcher and selected display start | NOT RUN |
| Cover Display 1 | Allowed target only; denied requests fail closed | NOT RUN |
| Display 1 to Display 0 and reverse | Existing task not silently migrated; user can recreate workspace | NOT RUN |
| Saved queue profile restoration | Recreates permitted sessions; respects background-start restrictions | NOT RUN |
| Unsupported/invalid/private display | No privileged start fallback, useful error | NOT RUN |
| Shizuku present, absent, killed and permission revoked | Basic launcher works and advanced controls degrade/recover | NOT RUN |
| Privileged service death and stale AIDL/task identity | No crash or stale task action; user-initiated recovery | NOT RUN |
| Accessibility off/on/revoked | App prompts for manual Settings; no auto-activation | NOT RUN |
| Overlay permission denied | No crash, clear recovery | NOT RUN |
| Force stop and reboot | No unsupported auto-start or silent privilege escalation | NOT RUN |
| Android 16 target API 36 | Background starts/FGS/edge-to-edge behavior verified | NOT RUN |
| Play Billing test purchase, pending, acknowledgment and restore | Correct entitlements and purchase security | NOT RUN |
| App bundle, merged release manifest, signing/version and package | Match final Play candidate | CI PENDING |

## Automated build / test evidence

CI workflow: .github/workflows/droidos-play-compatibility.yml. It executes Play debug unit tests and bundlePlayRelease, validates the actual AAB with bundletool, dumps the AAB base manifest, checks targetSdk 36, billing and absence of package-install permissions, and stores build manifest/checksum as evidence. It does not publish or upload the AAB to Play Console.
Do not mark a test PASS merely because a workflow was triggered. Review workflow run conclusion, exact SHA, logs and artifact integrity. Do not conflate compilation with Samsung hardware verification.
The initial CI attempts encountered Android runner environment problems (obsolete SDK tools package, missing PATH entry, then a checked-in Termux-specific AAPT2 override). The workflow contains Linux-only corrections; the current run outcome must still be recorded.

## Final promotion gates

1. Clarify the original app suspension and permissible appeal/reinstatement route in Play Console.
2. Remove or isolate all Play-reachable arbitrary privileged activity-start paths from the source graph; replace the generic shell command surface with narrow typed operations.
3. Complete permission/IPC/accessibility/billing/data-safety review; verify final merged manifest, dependencies, exact signed AAB and version code.
4. Execute manual Samsung Z Flip / Android 16 test matrix and retain reproducible logs/videos with private data redacted.
5. Obtain explicit developer authorization for any release upload or publication. Keep all current production and non-Play distributions untouched until that decision.
