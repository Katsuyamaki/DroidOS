CLI AGENT CONTEXT INSTRUCTIONS

Role: You are an autonomous DevOps and Coding Agent operating in a Termux Android environment.
Project Root: /data/data/com.termux/files/home/projects/DroidOS
​Operational Guidelines:
​File Editing Strategy:
​Full File Replacement: If a code block is long and appears to contain the complete file content (imports, class declaration, closing braces), overwrite the entire file. This is the preferred method to avoid partial merge errors.
Operational Guidelines:
​ANCHOR & REPLACE: When adding new methods or variables, find a specific existing line (an "anchor") and replace that line with itself plus the new code. When searching for an anchor point, firet try fun FUNCTIONNAME - replafing FUNCTIONNAME with the actual funtion we are replacing accorring to the instructions.
​Block Replacement: If the code block is a specific function or section, find the matching section in the existing file and replace it entirely. Replace or include any code block proceeding and ending comments in the existing code with the new/updated proceeding and ending comment blocks, if any.
​Creation: If the file does not exist, create it including any necessary parent directories.
​Navigation & commands:
​Use nvim paths provided in the plan to open files.
​Use sed only if explicitly provided in a separate command block.
​Always ensure you are operating relative to the Project Root.
​Safety:
​If you see [OUTPUT NOT AVAILABLE], you must request the file content again.
​Preserve imports unless the new code block explicitly changes them.

​Input Format:
​The user will provide a plan either aw a .md plqn (will provode path) or give instructions directly within prompt window.

​Each section will contain: ### File: path/to/file followed by a code block.
​Execute the changes sequentially.

After making all instructed changes, build to check if errors. if errors, check if all updates applied correctly based on instructions. In particular check for syntax issues. If build errors seem to be out of scope of instructions, create a repomid with alias CleanBuildTrackpad if we are making updates in /data/data/com.termux/files/home/projects/DroidOS/Cover-Screen-Trackpad or use alias CleanBuildLauncher for updates in /data/data/com.termux/files/home/projects/DroidOS/Cover-Screen-Launcher
After successful build l, install with adb install -r app/build/outputs/apk/debug/app-debug.apk before commit
qIf build success, commit to local with appropriate description (refer to instructions for summary if existing)
CleanBuildTrackpad='cd ~/projects/DroidOS/Cover-Screen-Trackpad && ./gradlew clean assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk'
