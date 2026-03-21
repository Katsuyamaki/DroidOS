#!/data/data/com.termux/files/usr/bin/bash

# 1. Set the strict environment (Crucial for Android)
export PATH=/data/data/com.termux/files/usr/bin:$PATH
export HOME=/data/data/com.termux/files/home

# 2. Go to the correct folder
cd "$HOME/projects/DroidOS"

# 3. Log the date for debugging
echo "--- Run at $(date) ---" >>debug_cron.log

# 4. Run the python script and capture ALL errors to the log
python droid_stats.py >>debug_cron.log 2>&1
