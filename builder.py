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
    """Converts non-breaking spaces (\xa0) to regular spaces and strips padding."""
    # Replace non-breaking spaces with regular spaces
    text = text.replace('\xa0', ' ')
    # Remove the generic code block markers if accidentally captured
    text = re.sub(r"^```\w*\n", "", text)
    text = text.rstrip("`").strip()
    return text

def apply_changes(plan_file):
    if not os.path.exists(plan_file): 
        print(f"❌ Plan file not found.")
        return
    
    with open(plan_file, 'r', encoding='utf-8') as f: content = f.read()

    # REGEX: Finds multiple FILE_UPDATE blocks in a row
    pattern = re.compile(
        r"FILE_UPDATE:\s*(.+?)\n.*?"                 
        r"SEARCH_BLOCK:\s*```\w*\n(.*?)\n\s*```\s*"  
        r"REPLACE_BLOCK:\s*```\w*\n(.*?)\n\s*```",   
        re.DOTALL
    )

    matches = pattern.findall(content)
    
    if not matches: 
        print("⚠️  No updates found. Check syntax.")
        return

    print(f"🔎 Found {len(matches)} updates...")

    for raw_filename, search_block, replace_block in matches:
        raw_filename = raw_filename.strip()
        full_path, error = find_file(raw_filename)
        
        if error:
            print(error)
            continue

        # SANITIZE: Fixes the copy-paste space issues
        search_block = sanitize_text(search_block)
        replace_block = sanitize_text(replace_block)

        with open(full_path, 'r', encoding='utf-8') as f: file_content = f.read()

        # 1. Exact Match
        if search_block in file_content:
            new_content = file_content.replace(search_block, replace_block)
            with open(full_path, 'w', encoding='utf-8') as f: f.write(new_content)
            print(f"✅ Updated: {os.path.basename(full_path)}")
        
        # 2. Fuzzy Match (Ignore leading/trailing whitespace difference)
        elif search_block.strip() in file_content:
            new_content = file_content.replace(search_block.strip(), replace_block)
            with open(full_path, 'w', encoding='utf-8') as f: f.write(new_content)
            print(f"✅ Updated (Fuzzy): {os.path.basename(full_path)}")
            
        else:
            print(f"❌ Anchor Mismatch: {os.path.basename(full_path)}")
            print(f"   -> Could not find the SEARCH_BLOCK.")
            print(f"   -> Start of block looked like: '{search_block[:50]}...'")

if __name__ == "__main__":
    if len(sys.argv) < 2: print("Usage: python builder.py <plan.md>")
    else: apply_changes(sys.argv[1])
