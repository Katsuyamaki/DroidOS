# Dextop precedent: verified architecture, unknowns, and non-copying approach

Snapshot: 2026-11-05. NarYuki/Dextop: https://github.com/NarYuki/Dextop
Play listing: https://play.google.com/store/apps/details?id=moe.n4tsu.gpdextop

Public distribution is real. The store advertises virtual desktop displays, freeform windows, DPI/resolution/orientation controls, input routing, Wireless Debugging setup, and workspace restore. A store listing proves distribution of an application, not Google's endorsement of every internal API choice or identity of the latest source and reviewed APK.

## Verified public source

| Question | Source-backed finding | Remaining uncertainty |
| --- | --- | --- |
| Public APIs | Context.startActivity with ActivityOptions.setLaunchDisplayId for own cover-display activities; Android NSD discovery and local wireless-debug pairing; normal services/providers, InputMethodService and AccessibilityService. | Display-start permission and OEM availability are device-specific. |
| Privileged tasks | TaskBridge uses hidden IActivityTaskManager getTasks, resizeTask, moveRootTaskToDisplay, setFocusedTask and IWindowOrganizerController / WindowContainerTransaction; requires shell-level Binder access. | Play artifact's exact executable class set not independently extracted. |
| Display geometry | DisplayTopologyController uses shell-backed wm density/rotation and privileged display service Binder. | Firmware and granted privileges vary. |
| Pairing | DistributionPrivilegeBootstrap uses a user-provided Wireless Debugging pairing code, discovers Android pairing/connect services, and establishes a local ADB shell session. | Play reviewer setup requirements and device-specific pairing failure handling. |
| Privileged process | EmbeddedServerPayload starts Android's app_process from code bundled within its installed APK and extracts a bundled JNI library for the shell process. This is not evidence of downloading new executable code. | Native-library delivery provenance should still be verified in published AAB. |
| Binder IPC | EmbeddedPrivilegeServer publishes a Binder to EmbeddedPrivilegeProvider. Provider checks the caller UID is SHELL_UID and rejects stale APK/duplicate binders. | Other exposed IPC surfaces must be audited separately. |
| Runtime recovery | DistributionPrivilegeRuntime monitors Binder death. EmbeddedPrivilegeKeeper handles reconnection after app-process death using existing credentials. External Shizuku remains a fallback in PrivilegedAccess. | Recovery after force-stop, reboot and revoked ADB authorization is conditional, not guaranteed. |
| Input | PrivilegedInputService uses native injection in a shell-owned service. MirrorService is a separate AccessibilityService. | Accessibility vs privileged injection usage per feature must be verified. |
| Flavors | GitHub ID moe.n4tsu.dextop; Play ID moe.n4tsu.gpdextop. Both source flavors set EMBEDDED_STELLAR true. Play manifest removes REQUEST_INSTALL_PACKAGES and REQUEST_DELETE_PACKAGES. | Play Store artifact equality with this source revision cannot be assumed without binary inspection. |

Source file anchors:
- https://github.com/NarYuki/Dextop/blob/main/android/app/build.gradle.kts
- https://github.com/NarYuki/Dextop/blob/main/android/app/src/play/AndroidManifest.xml
- https://github.com/NarYuki/Dextop/blob/main/android/app/src/main/kotlin/moe/n4tsu/dextop/privilege/DistributionPrivilegeBootstrap.kt
- https://github.com/NarYuki/Dextop/blob/main/android/app/src/main/kotlin/moe/n4tsu/dextop/privilege/EmbeddedServerPayload.kt
- https://github.com/NarYuki/Dextop/blob/main/android/app/src/main/kotlin/moe/n4tsu/dextop/privilege/EmbeddedPrivilegeServer.kt
- https://github.com/NarYuki/Dextop/blob/main/android/app/src/main/kotlin/moe/n4tsu/dextop/privilege/EmbeddedPrivilegeProvider.kt
- https://github.com/NarYuki/Dextop/blob/main/android/app/src/main/kotlin/moe/n4tsu/dextop/privilege/EmbeddedPrivilegeKeeper.kt
- https://github.com/NarYuki/Dextop/blob/main/android/app/src/main/kotlin/moe/n4tsu/dextop/plasma/TaskBridge.kt
- https://github.com/NarYuki/Dextop/blob/main/android/app/src/main/kotlin/moe/n4tsu/dextop/input/PrivilegedInputService.kt
- https://github.com/NarYuki/Dextop/blob/main/android/app/src/main/res/xml/mirror_accessibility_service.xml

## Important limits on reuse

Dextop uses GPLv3. Post-foss-final DroidOS main uses FSL-1.1-Apache-2.0. Study the architectural separation, pairing UX, shell caller validation, stale-instance rejection, and lifecycle tests; do not transplant source or JNI payload.
Dextop's privileged binder proxy and shell commands are not public Android SDK APIs merely because the app is on Play. Its recovery and system-settings operations deserve independent policy and compatibility review; do not copy them as assumed-safe shortcuts.
Dextop's Play package ID is a distinct listing; it does not establish a route to bypass DroidOS's own suspended listing.

## DroidOS Keyboard Picker v2 comparison

The developer reports Play approval of the separate Keyboard Picker v2 despite Shizuku-assisted bootstrap and persistence after Shizuku exit. The sibling project's architectural documents describe a separate foreground/session runtime, lifecycle checks and stale-session rejection. This supports testing a purpose-limited bootstrap mechanism, not automatically approving broad arbitrary task starts in DroidOS. APK contents, signed manifest, review outcome and exact backend lifecycle must be verified directly before stronger claims.

## Architectural decision for public DroidOS

Prefer a normal platform-approved app launch first; inspect actual returned task/display and adopt it into a queue workspace. Preserve user-authorized, narrowly scoped window/input/display control if independently reviewed, but do not escalate an Android-denied cover/background launch into a privileged shell start.
Dextop demonstrates feasibility of a separate process in a distinct Play product; implementing similar architecture for DroidOS requires independent design, security review and explicit user consent, not code copying.
