# Gradle Build Setup Notes

## Do I need to edit `~/.gradle` on a new workstation?
No.  
You can build without changing `~/.gradle` as long as the machine has:

- Java 17
- Android SDK / build-tools
- `adb` (or `nadb`) if you want install automation

## Optional Gradle properties (for convenience)
These are optional defaults so you do not need to type `-P...` each build.

You can set them in either:

- User-level: `~/.gradle/gradle.properties` (local to your machine)
- Project-level: `DroidOSLauncher/gradle.properties` (shared in repo, if desired)

```properties
GITHUB_AUTH_EXCHANGE_URL=https://your-auth-backend.example.com
GITHUB_ALLOW_MANUAL_TOKEN_IMPORT=false
```

If `GITHUB_AUTH_EXCHANGE_URL` is not set:

- Internal / Play / Samsung builds still build normally.
- GitHub build still builds, but claim-code exchange remains unavailable.

## Launcher build variants (current)

```bash
cd /data/data/com.termux/files/home/projects/DroidOS/DroidOSLauncher

./gradlew assembleInternalDebug
./gradlew assembleGithubDebug
./gradlew assemblePlayDebug
./gradlew assembleSamsungDebug
./gradlew assembleDebug   # legacy aggregate debug
```

### APK output paths

- Internal: `app/build/outputs/apk/internal/debug/app-internal-debug.apk`
- GitHub: `app/build/outputs/apk/github/debug/app-github-debug.apk`
- Play: `app/build/outputs/apk/play/debug/app-play-debug.apk`
- Samsung: `app/build/outputs/apk/samsung/debug/app-samsung-debug.apk`
- Legacy debug: `app/build/outputs/apk/debug/app-debug.apk`

## `builder.py` / export menu mapping

- `0` = Internal
- `1` = GitHub
- `2` = Play
- `3` = Samsung
- `4` = Legacy debug
