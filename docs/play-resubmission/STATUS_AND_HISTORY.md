# DroidOS Launcher — Play resubmission status and historical assessment

Snapshot: 2026-11-05. Release track: NOT APPROVED. Development branch: feature/play-resubmission-compat-api36.
This dossier covers the public DroidOSLauncher app. It does not assert that private DroidOS Pro features, AIUI, or newer tiling logic were in the historical Play package.

## Historical enforcement: confirmed from the developer's original notice

- Package: com.katsuyamaki.DroidOSLauncher.
- Enforcement issued: March 5, 2026 (received March 6 UTC).
- Affected version code: 4. The March 2 public source had versionName 2.2 and versionCode 4, but is not proven byte-identical to the uploaded package.
- Action: SUSPENDED; app removed from Play. This was not merely a rejected update.
- Policy categories: Elevated Privilege Abuse and Device and Network Abuse.
- Google's stated concerns: code attempting to exploit a known vulnerability and a bypass of Android background-activity-start restrictions. No specific CVE or source line was named.
- The notice did not announce developer-account termination. No successful reinstatement or appeal decision was established in the reviewed email history.

Source evidence: original Google Play enforcement email retained privately by the developer; avoid publishing case identifiers, email content, signing material, or account metadata in this public repository.
A later internal September 2026 retrospective diagnosed privileged cover-display activity starts as the leading hypothesis; it is retrospective analysis, not contemporaneous proof of the uploaded binary.

## Historical source reconstruction

The latest public commit before the notice was dfd11205a65887addbd47e59fe174df57c399f29 (2026-03-02).
At that commit, Gradle declared compile/target 35, package com.katsuyamaki.DroidOSLauncher, versionCode 4 / versionName 2.2, and the existing internal/play/samsung/github flavors. The historical source included Shizuku AIDL/user-service, a shell activity-start path targeting particular displays, a foreground-service specialUse declaration, and a broad Accessibility service.
The March 2026 source contains more advanced task/window work than the earliest original launcher. Code chronology is not proof that a given capability was in the Play-uploaded artifact.

UNRESOLVED: exact uploaded AAB/APK hash, signature certificate, versionName shown in Console, upload timestamp, exact source commit, permission set of the published artifact, and any reviewer attachments not supplied by email. Obtain these from the original Play Console and, where available, archived source/build artifacts. Do not invent them.

## Current source vs historical enforcement

The current public branch uses shared Android source plus BuildConfig store flags. It includes privileged am start, task movement, a generic shell command method, Accessibility toggles, billing, and overlay functionality. These facts do not prove which specific implementation triggered Google; the explicit Google objection to background starts is more precise than the generic assumption that all Shizuku/Accessibility use is prohibited.

The feature branch currently includes: public display-start validation, Play-only command containment and Accessibility-Settings recovery, Play targetSdk 36, Billing 8.3 migration, optional Shizuku onboarding, specialUse manifest removal, focused unit tests, and non-publishing CI. The command containment is NOT sufficient structural separation to clear the release. See COMPLIANCE_MATRIX.md.

## Enforcement decision and sequence

1. Enter the ORIGINAL Play Console application > Policy status. Record full finding, appeal availability, account standing, policy clarification and any previous appeals. Verify the original listing and signing/identity records. Do not infer reinstatement from missing email.
2. If Appeal remains available, submit an accurate account of the corrected activity-start architecture with evidence; do not claim the old bypass was allowed or that Dextop endorses DroidOS. If not available or previously denied, use official Play Developer Support for permitted resolution. Do not create substitute accounts/listings as an evasion.
3. Independently review source-graph exclusion, privileged IPC, Accessibility, foreground-service lifecycle, package visibility, payment, data handling, and Play-signed AAB.
4. Only after official enforcement clarity and successful Samsung hardware verification decide whether to pursue reinstatement/appeal or any other explicitly permitted Console path. No upload or publication is authorized by this dossier.

Official references:
- https://support.google.com/googleplay/android-developer/answer/9842754
- https://support.google.com/googleplay/android-developer/answer/9899234
- https://support.google.com/googleplay/android-developer/answer/16559646
- https://support.google.com/googleplay/android-developer/answer/11926878

Note: Google's 180-day appeal deadline published for **ACCOUNT TERMINATIONS** must not be misapplied to this app suspension. Check the actual Play Console action and case instructions.
