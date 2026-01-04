
import os
import re

# ==========================================
# CONFIGURATION
# ==========================================
INPUT_FILE = "dictionary.txt"
OUTPUT_FILE = "dictionary.txt" # Overwrites original

# 1. STRICT ALLOWLISTS (Short words are the noisiest in swipe)
# Only these 1-letter words are allowed
VALID_1_LETTER = {"a", "i"}

# Only these 2-letter words are allowed
VALID_2_LETTER = {
    "am", "an", "as", "at", "be", "by", "do", "go", "ha", "he", "hi", 
    "if", "in", "is", "it", "me", "my", "no", "of", "oh", "ok", "on", 
    "or", "ox", "so", "to", "up", "us", "we", "ye", "yo"
}

# Only these 3-letter words are allowed (Common English + standard abbreviations)
VALID_3_LETTER = {
    "act", "add", "ado", "age", "ago", "aid", "aim", "air", "ale", "all", "and", "ant", "any", "ape", "apt", "arc", "are", "ark", "arm", "art", "ash", "ask", "ate", "awe", "axe", "aye",
    "bad", "bag", "ban", "bar", "bat", "bay", "bed", "bee", "beg", "bet", "bib", "bid", "big", "bin", "bit", "bob", "bog", "boo", "bow", "box", "boy", "bra", "bud", "bug", "bum", "bun", "bus", "but", "buy", "bye",
    "cab", "cad", "cam", "can", "cap", "car", "cat", "cod", "cog", "con", "coo", "cop", "cot", "cow", "coy", "cry", "cub", "cue", "cup", "cut",
    "dab", "dad", "dam", "day", "den", "dew", "did", "die", "dig", "dim", "din", "dip", "doc", "doe", "dog", "don", "dot", "dry", "dub", "dud", "due", "dug", "duo", "dye",
    "ear", "eat", "ebb", "eel", "egg", "ego", "eke", "elf", "elk", "elm", "end", "era", "err", "eve", "ewe", "eye",
    "fad", "fan", "far", "fat", "fax", "fed", "fee", "few", "fib", "fig", "fin", "fit", "fix", "flu", "fly", "foe", "fog", "for", "fox", "fry", "fun", "fur",
    "gab", "gag", "gal", "gap", "gas", "gay", "gel", "gem", "get", "gig", "gin", "god", "got", "gum", "gun", "gut", "guy", "gym",
    "had", "hag", "ham", "has", "hat", "hay", "hem", "hen", "her", "hey", "hid", "him", "hip", "hit", "hoe", "hog", "hop", "hot", "how", "hub", "hue", "hug", "hum", "hut",
    "ice", "icy", "ill", "imp", "ink", "inn", "ion", "ire", "irk", "its", "ivy",
    "jab", "jam", "jar", "jaw", "jay", "jet", "jig", "job", "jog", "joy", "jug",
    "kea", "keg", "key", "kid", "kin", "kit",
    "lab", "lad", "lag", "lap", "law", "lay", "led", "lee", "leg", "let", "lid", "lie", "lip", "lit", "lob", "log", "loo", "lot", "low", "lug",
    "mad", "man", "map", "mat", "may", "men", "met", "mid", "mix", "mob", "mom", "mop", "mud", "mug", "mum",
    "nab", "nag", "nap", "nay", "net", "new", "nil", "nip", "nod", "nor", "not", "now", "nun", "nut",
    "oak", "oar", "oat", "odd", "off", "oft", "oil", "old", "one", "opt", "orb", "ore", "our", "out", "owl", "own",
    "pad", "pal", "pan", "par", "pat", "paw", "pay", "pea", "peg", "pen", "pet", "pew", "pie", "pig", "pin", "pit", "ply", "pod", "pop", "pot", "pro", "pry", "pub", "pun", "pup", "put",
    "rag", "ram", "ran", "rap", "rat", "raw", "ray", "red", "rib", "rid", "rig", "rim", "rip", "rob", "rod", "rot", "row", "rub", "rue", "rug", "rum", "run", "rut", "rye",
    "sad", "sag", "sap", "sat", "saw", "sax", "say", "sea", "see", "set", "sew", "sex", "she", "shy", "sin", "sip", "sir", "sit", "six", "ski", "sky", "sly", "sob", "sod", "son", "sop", "sow", "soy", "spa", "spy", "sub", "sue", "sum", "sun",
    "tab", "tag", "tan", "tap", "tar", "tat", "tax", "tea", "tee", "ten", "the", "thy", "tic", "tie", "tin", "tip", " toe", "tog", "ton", "too", "top", "tow", "toy", "try", "tub", "tug", "two",
    "urn", "use",
    "van", "vat", "vet", "via", "vow",
    "wad", "wag", "war", "was", "wax", "way", "web", "wed", "wee", "wet", "who", "why", "wig", "win", "wit", "woe", "won", "woo", "wow", "wry",
    "yak", "yam", "yap", "yes", "yet", "yew", "you",
    "zap", "zip", "zoo"
}

# 2. BLACKLIST
# Remove specific junk words or patterns
BLOCKED_PATTERNS = [
    r".*sex$",      # Ends in sex (animalsex, worldsex), unless it is 'sex' (handled by length check)
    r"^[^aeiouy]+$" # Words with NO vowels (e.g. 'tgp', 'mnt') - usually abbreviations
]
# Exceptions to the "ends with sex" rule (valid words)
SEX_EXCEPTIONS = {"sex", "unisex", "middlesex", "essex"}

def clean_dictionary():
    print(f"Reading {INPUT_FILE}...")
    
    if not os.path.exists(INPUT_FILE):
        print(f"Error: {INPUT_FILE} not found.")
        return

    with open(INPUT_FILE, 'r', encoding='utf-8') as f:
        raw_words = f.read().splitlines()

    cleaned_words = []
    removed_count = 0

    seen = set()

    for w in raw_words:
        w = w.strip().lower()
        
        # Filter 1: Basic Validity
        if not w or not w.isalpha():
            continue

        # Filter 2: Length-based strict allowlists
        if len(w) == 1:
            if w not in VALID_1_LETTER:
                removed_count += 1
                continue
        elif len(w) == 2:
            if w not in VALID_2_LETTER:
                # print(f"Removing 2-letter junk: {w}")
                removed_count += 1
                continue
        elif len(w) == 3:
            if w not in VALID_3_LETTER:
                # print(f"Removing 3-letter junk: {w}")
                removed_count += 1
                continue

        # Filter 3: Pattern Blocking
        is_blocked = False
        
        # Check "No Vowels" (junk abbreviations)
        if re.match(r"^[^aeiouy]+$", w):
            removed_count += 1
            continue

        # Check "sex" suffix spam
        if w.endswith("sex") and w not in SEX_EXCEPTIONS:
            print(f"Removing spam: {w}")
            removed_count += 1
            continue

        # Deduplicate
        if w in seen:
            continue
            
        seen.add(w)
        cleaned_words.append(w)

    # Sort alphabetically
    cleaned_words.sort()

    print(f"Original count: {len(raw_words)}")
    print(f"Removed: {removed_count}")
    print(f"New count: {len(cleaned_words)}")

    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write("\n".join(cleaned_words))
    
    print(f"Successfully cleaned dictionary saved to {OUTPUT_FILE}")

if __name__ == "__main__":
    clean_dictionary()
