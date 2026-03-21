
import sys
import re
import os
import datetime
import subprocess
import shutil

# CONFIG: Set your project root
PROJECT_ROOT = os.path.expanduser("~/projects/DroidOS")
HISTORY_FILE = os.path.join(PROJECT_ROOT, "planhistory.md")

LAUNCHER_BINARY_OPTIONS = {
    "0": {
        "label": "Internal Testing (default)",
        "task": "assembleInternalDebug",
        "apk": "app/build/outputs/apk/internal/debug/app-internal-debug.apk",
    },
    "1": {
        "label": "GitHub",
        "task": "assembleGithubDebug",
        "apk": "app/build/outputs/apk/github/debug/app-github-debug.apk",
    },
    "2": {
        "label": "Google Play",
        "task": "assemblePlayDebug",
        "apk": "app/build/outputs/apk/play/debug/app-play-debug.apk",
    },
    "3": {
        "label": "Samsung",
        "task": "assembleSamsungDebug",
        "apk": "app/build/outputs/apk/samsung/debug/app-samsung-debug.apk",
    },
    "4": {
        "label": "Legacy Aggregate Debug",
        "task": "assembleDebug",
        "apk": "app/build/outputs/apk/debug/app-debug.apk",
    },
}

def find_file(filename):
    if os.path.exists(filename): return filename, None
    target_name = os.path.basename(filename)
    matches = []
    for root, dirs, files in os.walk(PROJECT_ROOT):
        if target_name in files: matches.append(os.path.join(root, target_name))
    if len(matches) == 0: return None, f"❌ Not found: '{target_name}'"
    if len(matches) > 1: return None, f"⚠️  Ambiguous: found {len(matches)} files named '{target_name}'"
    return matches[0], None

def resolve_new_path(filename):
    if os.path.isabs(filename): return filename
    return os.path.join(PROJECT_ROOT, filename)

def sanitize_filename(filename):
    """Removes Markdown artifacts like **bold** or `code` from filenames."""
    clean = filename.replace('*', '').replace('`', '')
    return clean.strip()

def sanitize_text(text):
    """Cleans markdown noise and non-breaking spaces from AI output."""
    text = text.replace('\xa0', ' ')
    garbage = {"code kotlin", "code java", "code xml", "downloadcontent_copy", "expand_less", "expand_more"}
    lines = text.split('\n')
    clean_lines = []
    for line in lines:
        stripped = line.strip().lower()
        if stripped in garbage: continue
        if re.match(r"^`{3,}\w*$", stripped): continue
        if re.match(r"^[-=_]{3,}$", stripped): continue
        if "end_of_update_block" in stripped: continue
        clean_lines.append(line)
    return "\n".join(clean_lines)

def log_history(action, filename, reason, content_payload):
    """Logs every change to a persistent planhistory.md for audit."""
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    try:
        with open(HISTORY_FILE, 'a', encoding='utf-8') as f:
            f.write(f"\n## [{timestamp}] {action}: {filename}\n")
            f.write(f"**Reason:** {reason}\n\n")
            if action == "CREATE":
                f.write(f"```\n{content_payload.strip()}\n```\n")
            elif action == "UPDATE":
                search, replace = content_payload
                f.write(f"### Search:\n```\n{search.strip()}\n```\n")
                f.write(f"### Replace:\n```\n{replace.strip()}\n```\n")
            f.write("\n---\n")
    except Exception as e:
        print(f"⚠️  History Log Failed: {e}")

def copy_to_clipboard(text):
    """Fallback clipboard support for Termux."""
    try:
        subprocess.run(["termux-clipboard-set"], input=text.encode('utf-8'), check=True)
    except Exception:
        pass

def get_project_prefix(path):
    """Determines project tag for structured commit messages."""
    if "DroidOSLauncher" in path: return "Launcher: "
    if "DroidOSKeyboardTrackpad" in path: return "Trackpad: "
    return ""

def get_adb_command():
    """Detects nadb or adb for device installation."""
    if shutil.which("nadb"): return "nadb"
    if shutil.which("adb"): return "adb"
    return None

def has_internal_launcher_flavor():
    """Detects whether an internal Launcher flavor is implemented yet."""
    build_file = os.path.join(PROJECT_ROOT, "DroidOSLauncher", "app", "build.gradle.kts")
    try:
        with open(build_file, "r", encoding="utf-8") as f:
            text = f.read()
        return 'create("internal")' in text
    except Exception:
        return False

def choose_launcher_binary():
    """Prompts for Launcher binary target using numeric options only."""
    print("\nSelect Launcher binary to build/install:")
    for key, option in LAUNCHER_BINARY_OPTIONS.items():
        print(f"  {key}) {option['label']}")

    while True:
        choice = input("Enter choice [0]: ").strip() or "0"
        if choice in LAUNCHER_BINARY_OPTIONS:
            break
        print("❌ Invalid choice. Please enter one of: " + ", ".join(LAUNCHER_BINARY_OPTIONS.keys()))

    if choice == "0" and not has_internal_launcher_flavor():
        print("ℹ️  Internal flavor not implemented yet. Using GitHub binary (1) for now.")
        choice = "1"

    selected = LAUNCHER_BINARY_OPTIONS[choice]
    print(f"➡️  Selected Launcher binary: {choice} ({selected['label']})")
    return selected["task"], selected["apk"], selected["label"]

def process_plan(plan_file):
    if not os.path.exists(plan_file): return print(f"❌ Plan file not found.")
    
    with open(plan_file, 'r', encoding='utf-8') as f: content = f.read()
    print(f"🚀 Processing Plan...")

    actions_log, failures_log = [], []
    
    # --- PASS 1: FILE CREATION ---
    create_pattern = re.compile(
        r"FILE_CREATE:\s*(.+?)\n.*?REASON:\s*(.+?)\n.*?CONTENT_BLOCK:\s*(.*?)\s*(?:END_OF_UPDATE_BLOCK|(?=\n(?:FILE_UPDATE|FILE_CREATE):|$))",
        re.DOTALL
    )
    for raw_filename, reason, content_block in create_pattern.findall(content):
        raw_filename = sanitize_filename(raw_filename)
        full_path = resolve_new_path(raw_filename)
        clean_content = sanitize_text(content_block)
        try:
            os.makedirs(os.path.dirname(full_path), exist_ok=True)
            with open(full_path, 'w', encoding='utf-8') as f: f.write(clean_content)
            print(f"✨ Created: {raw_filename}")
            actions_log.append((raw_filename, reason.strip(), "Create"))
            log_history("CREATE", raw_filename, reason.strip(), clean_content)
        except Exception as e:
            failures_log.append((raw_filename, reason.strip(), str(e)))

# --- PASS 2: FILE UPDATES ---
    update_pattern = re.compile(
        r"FILE_UPDATE:\s*(.+?)\n.*?REASON:\s*(.+?)\n.*?SEARCH_BLOCK:\s*(.*?)\n\s*REPLACE_BLOCK:\s*(.*?)\s*(?:END_OF_UPDATE_BLOCK|(?=\n(?:FILE_UPDATE|FILE_CREATE):|$))",
        re.DOTALL
    )
    
    updates = update_pattern.findall(content)
    if updates:
        print(f"\n🔧 Applying {len(updates)} updates...")
        for raw_filename, reason, search_block, replace_block in updates:
            raw_filename = sanitize_filename(raw_filename)
            full_path, error = find_file(raw_filename)
            
            if error:
                # This restores the immediate "Red X" for missing files
                print(f"❌ Skipped: {raw_filename} ({error})")
                failures_log.append((raw_filename, reason.strip(), error))
                continue

            clean_search = sanitize_text(search_block)
            clean_replace = sanitize_text(replace_block)
            
            with open(full_path, 'r', encoding='utf-8') as f: 
                file_content = f.read()
            normalized_content = file_content.replace('\xa0', ' ')

            if clean_search in normalized_content:
                new_content = normalized_content.replace(clean_search, clean_replace)
                with open(full_path, 'w', encoding='utf-8') as f: f.write(new_content)
                print(f"✅ Updated: {os.path.basename(full_path)}")
                actions_log.append((raw_filename, reason.strip(), "Update"))
                log_history("UPDATE", os.path.basename(full_path), reason.strip(), (clean_search, clean_replace))
            elif clean_search.strip() in normalized_content:
                # Adding fuzzy match feedback back too
                new_content = normalized_content.replace(clean_search.strip(), clean_replace)
                with open(full_path, 'w', encoding='utf-8') as f: f.write(new_content)
                print(f"✅ Updated (Fuzzy): {os.path.basename(full_path)}")
                actions_log.append((raw_filename, reason.strip(), "Update"))
                log_history("UPDATE", os.path.basename(full_path), reason.strip(), (clean_search.strip(), clean_replace))
            else:
                # This restores the immediate "Red X" for anchor mismatches
                print(f"❌ Anchor Mismatch: {os.path.basename(full_path)}")
                failures_log.append((os.path.basename(full_path), reason.strip(), "Anchor text not found"))

    # --- LOG SUCCESSES TO BUILDERCOMMITS.MD ---
    if actions_log:
        print("\n" + "="*50 + "\n📝 LOGGING COMMIT MESSAGES\n" + "="*50)
        commit_file_path = os.path.join(PROJECT_ROOT, "buildercommits.md")
        try:
            with open(commit_file_path, 'a', encoding='utf-8') as cf:
                if os.path.exists(commit_file_path) and os.path.getsize(commit_file_path) > 0:
                    cf.write("\n")
                for fname, reason, action in actions_log:
                    line = f"* {get_project_prefix(fname)}{'(New) ' if action == 'Create' else ''}{os.path.basename(fname)}: {reason}"
                    print(line)
                    cf.write(line + "\n")
        except Exception as e: print(f"❌ Log Failed: {e}")

    # --- FAILURES SUMMARY ---
    if failures_log:
        print("\n" + "!"*50 + "\n⚠️  FAILURES SUMMARY\n" + "!"*50)
        for fname, reason, err in failures_log: print(f"* {get_project_prefix(fname)}{fname}: {reason} (FAILED: {err})")

    # --- FULL AUTO-BUILD LOGIC ---
    if actions_log:
        launcher_changed = any("DroidOSLauncher" in item[0] for item in actions_log)
        trackpad_changed = any("DroidOSKeyboardTrackpad" in item[0] for item in actions_log)
        if (launcher_changed or trackpad_changed) and input("\nBuild and install APK(s)? (Y/n): ").strip().lower() in ["", "y", "yes"]:
            adb_cmd = get_adb_command()
            try:
                if launcher_changed:
                    launcher_dir = os.path.join(PROJECT_ROOT, "DroidOSLauncher")
                    launcher_task, launcher_apk_path, launcher_label = choose_launcher_binary()
                    print(f"\n🔨 Building Launcher ({launcher_label})...")
                    subprocess.run(["./gradlew", launcher_task], cwd=launcher_dir, check=True)
                    if adb_cmd:
                        full_apk = os.path.join(launcher_dir, launcher_apk_path)
                        if not os.path.exists(full_apk):
                            raise FileNotFoundError(f"Expected APK not found: {launcher_apk_path}")
                        print(f"📲 Installing Launcher ({launcher_label})...")
                        subprocess.run([adb_cmd, "install", "-r", launcher_apk_path], cwd=launcher_dir, check=True)
                    else:
                        print("⚠️  adb/nadb not found; build completed but install was skipped.")
                if trackpad_changed:
                    trackpad_dir = os.path.join(PROJECT_ROOT, "DroidOSKeyboardTrackpad")
                    trackpad_apk_path = "app/build/outputs/apk/debug/app-debug.apk"
                    print("\n🔨 Building Trackpad...")
                    subprocess.run(["./gradlew", "assembleDebug"], cwd=trackpad_dir, check=True)
                    if adb_cmd:
                        full_apk = os.path.join(trackpad_dir, trackpad_apk_path)
                        if not os.path.exists(full_apk):
                            raise FileNotFoundError(f"Expected APK not found: {trackpad_apk_path}")
                        print("📲 Installing Trackpad...")
                        subprocess.run([adb_cmd, "install", "-r", trackpad_apk_path], cwd=trackpad_dir, check=True)
                    else:
                        print("⚠️  adb/nadb not found; build completed but install was skipped.")
                print("\n✨ All operations complete!")
            except (subprocess.CalledProcessError, FileNotFoundError) as e: print(f"\n❌ Operation Failed: {e}")

if __name__ == "__main__":
    if len(sys.argv) < 2: print("Usage: python builder.py <plan.md>")
    else: process_plan(sys.argv[1])
