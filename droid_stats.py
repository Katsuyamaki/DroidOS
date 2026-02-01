#!/usr/bin/env python
import subprocess
import json
import os
import pandas as pd
from datetime import datetime, timedelta

CSV_FILE = "DroidOSDownloads.csv"
COLUMNS = ['timestamp', 'version', 'asset', 'total_count', 'hourly_delta', 'delta_24h']

def clean_csv_schema():
    """
    Self-healing function:
    Reads the raw CSV file. If it finds mixed row lengths (old 5-col vs new 6-col),
    it pads the old rows with ',0' to match the new schema.
    """
    if not os.path.exists(CSV_FILE):
        return

    try:
        # Try reading with mixed format support immediately to catch schema issues vs date issues
        pd.read_csv(CSV_FILE)
    except pd.errors.ParserError:
        print("\033[1;33m[!] Detecting schema mismatch. Migrating legacy data...\033[0m")
        
        with open(CSV_FILE, 'r') as f:
            lines = f.readlines()
        
        expected_commas = len(COLUMNS) - 1
        new_lines = []
        for i, line in enumerate(lines):
            line = line.strip()
            commas = line.count(',')
            
            if i == 0 and commas < expected_commas:
                new_lines.append(",".join(COLUMNS) + "\n")
                continue
            
            if commas < expected_commas:
                missing = expected_commas - commas
                line += ",0" * missing
            
            new_lines.append(line + "\n")
            
        with open(CSV_FILE, 'w') as f:
            f.writelines(new_lines)
        print("\033[1;32m[+] CSV Schema patched successfully.\033[0m")

def get_gh_data():
    cmd = "gh release list --json tagName --jq '.[].tagName' | xargs -I {} gh release view {} --json assets,tagName --jq '{version: .tagName, assets: [.assets[] | {name, downloadCount}]}'"
    result = subprocess.check_output(cmd, shell=True, text=True)
    raw_data = [json.loads(s) for s in result.strip().split('\n')]
    
    rows = []
    # FIX: Force clean string format without seconds/microseconds
    current_time = datetime.now().strftime("%Y-%m-%d %H:%M")
    
    for release in raw_data:
        for asset in release['assets']:
            rows.append({
                'timestamp': current_time,
                'version': release['version'],
                'asset': asset['name'],
                'total_count': asset['downloadCount']
            })
    return pd.DataFrame(rows)

def process_stats():
    clean_csv_schema()
    new_df = get_gh_data()
    now = datetime.now()

    if os.path.exists(CSV_FILE):
        full_df = pd.read_csv(CSV_FILE)
        
        # FIX: Added format='mixed' to handle both old (short) and new (long) timestamps safely
        full_df['timestamp'] = pd.to_datetime(full_df['timestamp'], format='mixed')
        
        # 1. Calculate Hourly Delta
        last_ts = full_df['timestamp'].max()
        prev_snap = full_df[full_df['timestamp'] == last_ts]
        merged = pd.merge(new_df, prev_snap[['asset', 'total_count']], on='asset', how='left', suffixes=('', '_prev'))
        merged['hourly_delta'] = merged['total_count'] - merged['total_count_prev'].fillna(0)

        # 2. Calculate 24h Delta
        target_24h = now - timedelta(hours=24)
        times = full_df['timestamp'].unique()
        
        if len(times) > 0:
            past_24h_ts = min(times, key=lambda x: abs(pd.Timestamp(x) - target_24h))
            snap_24h = full_df[full_df['timestamp'] == past_24h_ts]
            merged = pd.merge(merged, snap_24h[['asset', 'total_count']], on='asset', how='left', suffixes=('', '_24h'))
            merged['delta_24h'] = merged['total_count'] - merged['total_count_24h'].fillna(0)
        else:
            merged['delta_24h'] = 0
    else:
        merged = new_df.copy()
        merged['hourly_delta'] = 0
        merged['delta_24h'] = 0

    # Save to CSV
    merged[COLUMNS].to_csv(CSV_FILE, mode='a', index=False, header=not os.path.exists(CSV_FILE))

    # --- Visual Feedback ---
    grand_total_itd = merged['total_count'].sum()
    grand_total_24h = merged['delta_24h'].sum()

    print(f"\n\033[1;32mDroidOS Analytics - {now.strftime('%Y-%m-%d %H:%M')}\033[0m")
    print(f"{'Asset':<40} | {'Total':<6} | {'+1h':<5} | {'+24h':<5}")
    print("-" * 68)
    
    for _, row in merged.iterrows():
        h_color = "\033[1;34m" if row['hourly_delta'] > 0 else ""
        d_color = "\033[1;35m" if row.get('delta_24h', 0) > 0 else ""
        reset = "\033[0m"
        print(f"{row['asset'][:39]:<40} | "
              f"{int(row['total_count']):<6} | "
              f"{h_color}{int(row['hourly_delta']):>+5}{reset} | "
              f"{d_color}{int(row.get('delta_24h', 0)):>+5}{reset}")

    print("-" * 68)
    print(f"\033[1mCumulative Inception to Date:\033[0m \033[1;32m{int(grand_total_itd)}\033[0m downloads")
    print(f"\033[1mTotal Growth Last 24h:\033[0m        \033[1;35m+{int(grand_total_24h)}\033[0m")

if __name__ == "__main__":
    process_stats()
