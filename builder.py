import sys
import re
import os
import datetime
import subprocess

# CONFIG: Set your project root
PROJECT_ROOT = os.path.expanduser("~/projects/DroidOS")
HISTORY_FILE = os.path.join(PROJECT_ROOT, "planhistory.md")

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

def sanitize_text(text):
    text = text.replace('\xa0', ' ')
    garbage = {"code kotlin", "code java", "code xml", "downloadcontent_copy", "expand_less", "expand_more"}
    lines = text.split('\n')
    clean_lines = []
    for line in lines:
        stripped = line.strip().lower()
        if stripped in garbage: continue
        if re.match(r"^`{3,}\w*$", stripped): continue
        if re.match(r"^[-=_]{3,}$", stripped): continue
        clean_lines.append(line)
    return "\n".join(clean_lines)

def log_history(action, filename, reason, content_payload):
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
    """Uses Termux native clipboard API"""
    try:
        # Check if termux-clipboard-set exists
        subprocess.run(["termux-clipboard-set"], input=text.encode('utf-8'), check=True)
        print("📋 Copied to clipboard!")
    except FileNotFoundError:
        print("⚠️  'termux-clipboard-set' not found. Install Termux:API if needed.")
    except Exception as e:
        print(f"⚠️  Clipboard copy failed: {e}")

def get_project_prefix(path):
    """Returns 'Launcher:' or 'Trackpad:' based on path"""
    if "Cover-Screen-Launcher" in path: return "Launcher: "
    if "Cover-Screen-Trackpad" in path: return "Trackpad: "
    return ""

def process_plan(plan_file):
    if not os.path.exists(plan_file): return print(f"❌ Plan file not found.")
    
    with open(plan_file, 'r', encoding='utf-8') as f: content = f.read()
    print(f"🚀 Processing Plan...")

    actions_log = [] 
    failures_log = []

    # --- PASS 1: FILE CREATION ---
    create_pattern = re.compile(
        r"FILE_CREATE:\s*(.+?)\n.*?REASON:\s*(.+?)\n.*?CONTENT_BLOCK:\s*(.*?)(?=\n(?:FILE_UPDATE|FILE_CREATE):|$)",
        re.DOTALL
    )
    
    creates = create_pattern.findall(content)
    if creates:
        print(f"\n🔨 Creating {len(creates)} new files...")
        for raw_filename, reason, content_block in creates:
            raw_filename = raw_filename.strip()
            reason = reason.strip()
            full_path = resolve_new_path(raw_filename)
            clean_content = sanitize_text(content_block)
            
            try:
                os.makedirs(os.path.dirname(full_path), exist_ok=True)
                with open(full_path, 'w', encoding='utf-8') as f: f.write(clean_content)
                
                print(f"✨ Created: {raw_filename}")
                # Store full raw_filename to detect prefix later
                actions_log.append((raw_filename, reason, "Create"))
                log_history("CREATE", raw_filename, reason, clean_content)
                
            except Exception as e:
                print(f"❌ Failed to create {raw_filename}: {e}")

    # --- PASS 2: FILE UPDATES ---
    update_pattern = re.compile(
        r"FILE_UPDATE:\s*(.+?)\n.*?REASON:\s*(.+?)\n.*?SEARCH_BLOCK:\s*(.*?)\n\s*REPLACE_BLOCK:\s*(.*?)(?=\n(?:FILE_UPDATE|FILE_CREATE):|$)",
        re.DOTALL
    )

    updates = update_pattern.findall(content)
    if updates:
        print(f"\n🔧 Applying {len(updates)} updates...")
        for raw_filename, reason, search_block, replace_block in updates:
            raw_filename = raw_filename.strip()
            reason = reason.strip()
            full_path, error = find_file(raw_filename)
            
            if error:
                print(f"❌ Skipped: {raw_filename} ({error})")
                failures_log.append((raw_filename, reason, error))
                continue

            clean_search = sanitize_text(search_block)
            clean_replace = sanitize_text(replace_block)
            
            with open(full_path, 'r', encoding='utf-8') as f: file_content = f.read()
            normalized_content = file_content.replace('\xa0', ' ')

            success = False
            if clean_search in normalized_content:
                new_content = normalized_content.replace(clean_search, clean_replace)
                with open(full_path, 'w', encoding='utf-8') as f: f.write(new_content)
                print(f"✅ Updated: {os.path.basename(full_path)}")
                success = True
                
            elif clean_search.strip() in normalized_content:
                new_content = normalized_content.replace(clean_search.strip(), clean_replace)
                with open(full_path, 'w', encoding='utf-8') as f: f.write(new_content)
                print(f"✅ Updated (Fuzzy): {os.path.basename(full_path)}")
                success = True
            
            if success:
                # Store raw_filename (relative path) to detect prefix, NOT just basename
                actions_log.append((raw_filename, reason, "Update"))
                log_history("UPDATE", os.path.basename(full_path), reason, (clean_search, clean_replace))
            else:
                print(f"❌ Anchor Mismatch: {os.path.basename(full_path)}")
                failures_log.append((os.path.basename(full_path), reason, "Anchor text not found"))

    # --- GIT SUMMARY & CLIPBOARD ---
    if actions_log:
        print("\n" + "="*50)
        print("📝 GIT COMMIT SUMMARY")
        print("="*50)
        
        # Build the clean list for clipboard
        clipboard_lines = []
        
        for fname, reason, action in actions_log:
            prefix_tag = get_project_prefix(fname)
            base_name = os.path.basename(fname)
            new_tag = "(New) " if action == "Create" else ""
            
            # Format: * Launcher: MainActivity.kt: Reason
            line = f"* {prefix_tag}{new_tag}{base_name}: {reason}"
            print(line)
            clipboard_lines.append(line)
            
        # Interactive Copy
        print("="*50)
        user_input = input("\nCopy commit message to clipboard? (Y/n): ").strip().lower()
        if user_input in ["", "y", "yes"]:
            copy_to_clipboard("\n".join(clipboard_lines))
    else:
        print("\n(No changes applied)")

    if failures_log:
        print("\n" + "!"*50)
        print("⚠️  FAILURES")
        print("!"*50)
        for fname, reason, err in failures_log:
            print(f"❌ {fname}: {err}")

if __name__ == "__main__":
    if len(sys.argv) < 2: print("Usage: python builder.py <plan.md>")
    else: process_plan(sys.argv[1])
