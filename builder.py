import sys
import re
import os

# CONFIG: Set your project root
PROJECT_ROOT = os.path.expanduser("~/projects/DroidOS")

def find_file(filename):
    if os.path.exists(filename): return filename, None
    target_name = os.path.basename(filename)
    matches = []
    for root, dirs, files in os.walk(PROJECT_ROOT):
        if target_name in files: matches.append(os.path.join(root, target_name))
    if len(matches) == 0: return None, f"❌ Not found: '{target_name}'"
    if len(matches) > 1: return None, f"⚠️  Ambiguous: found {len(matches)} files named '{target_name}'"
    return matches[0], None

def sanitize_text(text):
    """
    AGGRESSIVE CLEANING:
    1. Removes all Markdown code fences (```kotlin, ```, etc).
    2. Strips non-breaking spaces.
    3. Removes AI Studio UI artifacts.
    """
    # 1. Fix weird spaces
    text = text.replace('\xa0', ' ')
    
    # 2. Garbage Collection
    garbage_lines = {
        "code kotlin", "code java", "code python", 
        "downloadcontent_copy", "expand_less", "expand_more"
    }
    
    lines = text.split('\n')
    clean_lines = []
    
    for line in lines:
        stripped = line.strip().lower()
        
        # SKIP: Garbage UI lines
        if stripped in garbage_lines: 
            continue
            
        # SKIP: Markdown Fences (matches ```, ```kotlin, etc.)
        if re.match(r"^`{3,}\w*$", stripped):
            continue
            
        clean_lines.append(line)
        
    return "\n".join(clean_lines)

def apply_changes(plan_file):
    if not os.path.exists(plan_file): 
        print(f"❌ Plan file not found.")
        return
    
    with open(plan_file, 'r', encoding='utf-8') as f: content = f.read()

    pattern = re.compile(
        r"FILE_UPDATE:\s*(.+?)\n"
        r".*?REASON:\s*(.+?)\n"
        r".*?SEARCH_BLOCK:\s*(.*?)\n\s*"
        r"REPLACE_BLOCK:\s*(.*?)(?=\nFILE_UPDATE:|$)",
        re.DOTALL
    )

    matches = pattern.findall(content)
    
    if not matches: 
        print("⚠️  No updates found. Check syntax.")
        return

    print(f"🔎 Scanning {len(matches)} updates...\n")
    
    applied_updates = []
    failed_updates = []

    for raw_filename, reason, search_block, replace_block in matches:
        raw_filename = raw_filename.strip()
        reason = reason.strip()
        
        full_path, error = find_file(raw_filename)
        
        if error:
            print(error)
            failed_updates.append((raw_filename, reason, "File not found"))
            continue

        # --- SANITIZATION & FEEDBACK LOGIC ---
        clean_search = sanitize_text(search_block)
        clean_replace = sanitize_text(replace_block)
        
        # Check if "sed" did any work
        was_cleaned = (clean_search != search_block) or (clean_replace != replace_block)
        status_icon = "🧹" if was_cleaned else "  " # Broom if cleaned, empty if clean

        with open(full_path, 'r', encoding='utf-8') as f: file_content = f.read()

        # Try Exact Match
        if clean_search in file_content:
            new_content = file_content.replace(clean_search, clean_replace)
            with open(full_path, 'w', encoding='utf-8') as f: f.write(new_content)
            
            msg = f"✅ Applied: {os.path.basename(full_path)}"
            if was_cleaned: msg += " (Artifacts Removed 🧹)"
            print(msg)
            
            applied_updates.append((os.path.basename(full_path), reason, was_cleaned))
        
        # Try Fuzzy Match
        elif clean_search.strip() in file_content:
            new_content = file_content.replace(clean_search.strip(), clean_replace)
            with open(full_path, 'w', encoding='utf-8') as f: f.write(new_content)
            
            msg = f"✅ Applied (Fuzzy): {os.path.basename(full_path)}"
            if was_cleaned: msg += " (Artifacts Removed 🧹)"
            print(msg)
            
            applied_updates.append((os.path.basename(full_path), reason, was_cleaned))
            
        else:
            print(f"❌ FAILED: {os.path.basename(full_path)}")
            if was_cleaned: print("   (Note: Script tried to clean markdown artifacts but still failed)")
            failed_updates.append((os.path.basename(full_path), reason, "Anchor text not found"))

    # --- SUMMARY REPORT ---
    print("\n" + "="*60)
    print("📝 GIT COMMIT SUMMARY")
    print("="*60)
    if applied_updates:
        for fname, reason, cleaned in applied_updates:
            # Mark the commit message if it required cleaning (optional, mostly for your info)
            marker = " [Cleaned]" if cleaned else ""
            print(f"* {fname}: {reason}")
    else:
        print("(No changes applied)")

    if failed_updates:
        print("\n" + "!"*60)
        print("⚠️  MISSED UPDATES")
        print("!"*60)
        for fname, reason, error in failed_updates:
            print(f"❌ {fname}")
            print(f"   Reason: {reason}")
            print(f"   Error:  {error}")

if __name__ == "__main__":
    if len(sys.argv) < 2: print("Usage: python builder.py <plan.md>")
    else: apply_changes(sys.argv[1])

