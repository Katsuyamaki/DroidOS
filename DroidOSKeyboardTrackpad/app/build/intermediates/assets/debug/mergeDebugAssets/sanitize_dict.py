import os
import sys

# Configuration
INPUT_FILE = 'dictionary.txt'
OUTPUT_FILE = 'dictionary_sanitized.txt'

# Blocklist of unwanted words
BLACKLIST = {
    # German
    'der', 'die', 'und', 'des', 'von', 'im', 'da', 'den', 'ein', 'eine', 'zu', 'mit', 
    'ist', 'auf', 'aus', 'dem', 'fuer', 'fur', 'sich', 'nicht', 'dass', 'sie', 'wir', 'ich',
    # French
    'le', 'la', 'les', 'un', 'une', 'du', 'et', 'pour', 'que', 'qui', 'dans', 'sur', 
    'par', 'pas', 'aux', 'ne', 'ce', 'se', 'vous', 'nous', 'en', 'au', 'monde', 'mais',
    # Spanish
    'el', 'los', 'las', 'del', 'por', 'para', 'con', 'una', 'su', 'al', 'lo', 'como', 'mas', 'todo', 'sobre',
    # Garbage / File extensions / HTML / Protocols
    'http', 'https', 'www', 'com', 'org', 'net', 'html', 'php', 'jpg', 'jpeg', 'gif', 
    'png', 'pdf', 'rss', 'xml', 'css', 'js', 'exe', 'dll', 'txt', 'src', 'href', 'img',
    'gmt', 'pst', 'est', 'cst', 'mst', 'utc', 'vol', 'tel', 'fax', 'isbn', 'faq', 'nbsp',
    'aa', 'bb', 'cc', 'dd', 'ee', 'ff', 'gg', 'hh', 'jj', 'kk', 'll', 'mm', 'nn', 'oo', 'pp', 'qq', 'rr', 'ss', 'tt', 'uu', 'vv', 'ww', 'xx', 'yy', 'zz',
    'ii', 'iii', 'iv', 'vi', 'vii', 'viii', 'ix', 'xi', 'xii', 'xiii', 'xiv', 'xv', 'xvi',
    # Adult / Offensive (Remove to clean up suggestions)
    'porn', 'sex', 'xxx', 'fuck', 'shit', 'ass', 'dick', 'pussy', 'cock', 'bitch', 
    'anal', 'erotic', 'nude', 'naked', 'milf', 'cunt', 'whore', 'slut', 'rape', 
    'incest', 'beastiality', 'shemale', 'tranny', 'hentai', 'boobs', 'tits', 
    'blowjob', 'handjob', 'dildo', 'vibrator', 'camgirl', 'escort', 'lolita',
    'lesbian', 'gay', 'bisexual', 'transgender', 'queer'
}

# Allow list for single letters
KEEP_SINGLE = {'a', 'i'}

def sanitize():
    # Handle path resolution (check current or parent dir)
    target_file = INPUT_FILE
    if not os.path.exists(target_file):
        if os.path.exists(os.path.join('..', INPUT_FILE)):
            target_file = os.path.join('..', INPUT_FILE)
        else:
            print(f"Error: {INPUT_FILE} not found in current or parent directory.")
            return

    print(f"Reading {target_file}...")
    try:
        with open(target_file, 'r', encoding='utf-8', errors='ignore') as f:
            lines = f.readlines()
    except Exception as e:
        print(f"Failed to read file: {e}")
        return

    clean_lines = []
    seen = set()
    removed_count = 0

    for line in lines:
        word = line.strip().lower()
        
        # skip empty
        if not word:
            continue
            
        # skip numbers or mixed alphanumeric (e.g. mp3, 4th) unless desired, assuming pure dictionary here
        if not word.isalpha():
            # Special case: keep strictly alpha words. Remove things with numbers like 'mp3' or '3rd'
            removed_count += 1
            continue

        # skip single letters except 'a' and 'i'
        if len(word) == 1 and word not in KEEP_SINGLE:
            removed_count += 1
            continue

        # blacklist check
        if word in BLACKLIST:
            removed_count += 1
            continue

        # dedup
        if word in seen:
            continue
            
        seen.add(word)
        clean_lines.append(word)

    # Write output
    out_path = OUTPUT_FILE
    if os.path.dirname(target_file):
        out_path = os.path.join(os.path.dirname(target_file), OUTPUT_FILE)
        
    with open(out_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(clean_lines))
        f.write('\n')
        
    print(f"Sanitization complete.")
    print(f"Processed: {len(lines)} lines.")
    print(f"Removed: {removed_count} lines.")
    print(f"Cleaned file saved to: {out_path}")

if __name__ == "__main__":
    sanitize()
