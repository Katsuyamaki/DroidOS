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

def resolve_new_path(filename):
    if os.path.isabs(filename): return filename
    return os.path.join(PROJECT_ROOT, filename)

def sanitize_text(text):
    """Cleans Markdown artifacts and normalizes spaces."""
    # 1. Replace non-breaking spaces with regular spaces
    text = text.replace('\xa0', ' ')
    
    # 2. Strip UI garbage
    garbage = {"code kotlin", "code java", "code xml", "downloadcontent_copy", "expand_less", "expand_more"}
    lines = text.split('\n')
    clean_lines = []
    for line in lines:
        stripped = line.strip().lower()
        if stripped in garbage: continue
        if re.match(r"^`{3,}\w*$", stripped): continue
        clean_lines.append(line)
    return "\n".join(clean_lines)

def process_plan(plan_file):
    if not os.path.exists(plan_file): return print(f"❌ Plan file not found.")
    
    with open(plan_file, 'r', encoding='utf-8') as f: content = f.read()
    print(f"🚀 Processing Plan...")

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
            full_path = resolve_new_path(raw_filename)
            clean_content = sanitize_text(content_block)
            
            os.makedirs(os.path.dirname(full_path), exist_ok=True)
            with open(full_path, 'w', encoding='utf-8') as f: f.write(clean_content)
            print(f"✨ Created: {raw_filename}")

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
            full_path, error = find_file(raw_filename)
            
            if error:
                print(f"❌ Skipped: {raw_filename} ({error})")
                continue

            clean_search = sanitize_text(search_block)
            clean_replace = sanitize_text(replace_block)
            
            with open(full_path, 'r', encoding='utf-8') as f: file_content = f.read()

            # [FIX] NORMALIZE FILE CONTENT IN MEMORY (Handle NBSP in existing files)
            # This allows matching even if the file on disk has weird spaces
            normalized_content = file_content.replace('\xa0', ' ')

            if clean_search in normalized_content:
                # We matched on normalized content, so we apply the replace there
                new_content = normalized_content.replace(clean_search, clean_replace)
                with open(full_path, 'w', encoding='utf-8') as f: f.write(new_content)
                print(f"✅ Updated: {os.path.basename(full_path)}")
                
            elif clean_search.strip() in normalized_content:
                new_content = normalized_content.replace(clean_search.strip(), clean_replace)
                with open(full_path, 'w', encoding='utf-8') as f: f.write(new_content)
                print(f"✅ Updated (Fuzzy): {os.path.basename(full_path)}")
                
            else:
                # Debugging Help: Print what the script saw vs what it wanted
                print(f"❌ Anchor Mismatch: {os.path.basename(full_path)}")
                # print(f"   Wanted:\n{clean_search[:100]}...") # Uncomment for debug

if __name__ == "__main__":
    if len(sys.argv) < 2: print("Usage: python builder.py <plan.md>")
    else: process_plan(sys.argv[1])
