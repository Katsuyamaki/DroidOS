DroidOS 📱🚀
DroidOS is a suite of advanced system tools designed to give "superpowers" to the standard Android experience.
It functions as a universal Samsung DeX replacement, a tiling window manager, and an unrestricted app launcher that works on any Android device. Whether you are using a Foldable, a Flip phone, AR Glasses, or a secondary monitor, DroidOS unlocks the full potential of your hardware.
📂 Project Structure (Monorepo)
This repository is a Monorepo containing two distinct but complementary Android applications.
> ⚠️ Developer Note: Do not open this root folder directly in Android Studio. You must open each project folder individually.
> 
| Project | Description | Path |
|---|---|---|
| DroidOS Launcher | An advanced tiling window manager and app launcher. Bypasses cover screen restrictions and manages multi-window layouts. | /Cover-Screen-Launcher |
| DroidOS Trackpad Keyboard | A virtual mouse trackpad and custom keyboard overlay. Turns your phone into a precision input device for external displays. | /Cover-Screen-Trackpad |

✨ Core Features
🖥️ Universal Desktop Mode (DeX Replacement)
Unlike proprietary solutions locked to specific brands, DroidOS provides a desktop-class experience on any Android phone:
 * Window Management: Force apps into specific tiling layouts (Split-screen, Quadrants, Tri-split) on external displays.
 * Input Control: Turn your phone screen into a fully functional trackpad and keyboard while viewing content on a larger screen.
🕶️ AR Glasses & Virtual Screens
Optimized for users of XREAL, Rokid, Viture, and other AR glasses:
 * "Headless" Mode: Turn off your phone's physical screen to save battery and reduce heat while the system continues running on the glasses.
 * Blind Navigation: The Trackpad module allows you to control the AR interface without looking at your phone.
📱 Foldable & Cover Screen Enhancements
Unleash the full power of your Galaxy Z Flip, Fold, or other foldable devices:
 * Unrestricted Launching: Launch any app on the cover screen, bypassing system "Good Lock" allowlists.
 * Orientation Control: Force landscape or portrait orientations on screens that don't natively support them.
🛠️ How It Works
DroidOS utilizes Shizuku to access elevated system APIs without requiring root access. This allows it to:
 * Inject raw input events (Mouse/Keyboard) directly into the system input stream.
 * Manage window sizes, positions, and display power states via hidden Android APIs (Reflection).
 * Launch activities on specific display IDs (Cover screens, Virtual displays).
🚀 Getting Started
Prerequisites
 * Shizuku: Must be installed and running on your device.
 * Developer Options: "Force activities to be resizable" and "Enable freeform windows" must be enabled.
Installation
You can download the latest APKs for both modules from the Releases page.
 * Install DroidOS Launcher to manage your apps and windows.
 * Install DroidOS Trackpad Keyboard to control the cursor.
 * DroidOS Launcher can be exited by swiping tbr bubble icon away.
 * Grant Shizuku permissions when prompted in each app.
 * I reccomend this fork of shizuku https://github.com/thedjchi/Shizuku once you set it up. It has a watchdog feature to autorestart whenever it gets turned off. Once you turn it on, even if you lose wireless adb you can still turn shizuku back on without it. Also has an auto start on boot feature. Does not require root.
 * Grant accessibility permissions to the trackpad when prompted.
🤝 Contributing
We welcome contributions! Please note that this is a Monorepo.
 * If you are fixing a bug in the Launcher, make your Pull Request against the Cover-Screen-Launcher directory.
 * If you are improving the Trackpad, work within the Cover-Screen-Trackpad directory.
📄 License
This project is licensed under the GNU General Public License v3.0 (GPLv3).
You are free to use, modify, and distribute this software, but all modifications must remain open source. See the LICENSE file for details.

🚀 DroidOS Launcher Usage Guide
The DroidOS Launcher is designed to manage multi-window tiling and control display resolutions, primarily using Shizuku for elevated permissions.
1. The Two Operational Modes
The Launcher operates primarily using an app queue combined with your selected window layout. The core difference lies in how aggressively the launcher manages apps after initialization.
>
| Mode | Key Feature | Execution Action | Ideal For |
|---|---|---|---|
| Instant Mode | Live, dynamic window management. | Windows are launched/resized automatically every time you adjust the queue (add/remove/hide apps). The Green Play/Execute button is hidden. | Quick adjustments, experimental resizing, or when fine-tuning a small layout. |
| Launcher Mode | Traditional "batch" execution. | Changes to the queue or layout only take effect when you explicitly press the Green Play/Execute button. | Large, complex setups (3+ apps) where manual timing is better, or minimizing system resource drain. |
> Switch Mode: Go to the Settings tab (Gear Icon) and toggle "Instant Mode (Live Changes)".
> 
2. Managing the App Queue (The Dock)
The App Queue (the horizontal list of icons at the top of the main drawer) determines which apps are launched and where they are placed in your chosen layout.
>
| Action | How To | Result |
|---|---|---|
| Adding an App | 1. Navigate to the Search tab. 2. Tap an app listed in the main recycler view. | The app is added to the right end of the App Queue. If in Instant Mode, the layout is applied immediately. |
| Adding a Spacer | Tap "(Blank Space)" in the search list. | Inserts a blank placeholder into the queue. This ensures an empty tile space in your final layout (e.g., in a 4-Quadrant layout, you can use 2 apps and 2 blanks). |
| Reordering/Moving | Drag and drop an app icon horizontally within the queue. | Changes the app's position in the queue, which dictates its screen placement (Tiling order). |
| Toggling Hide/Minimize | Tap an app icon in the App Queue. | The app's icon turns slightly transparent (minimized). The app is moved to the background using its Task ID. The app is skipped during subsequent tiling calculations. |
| Closing/Killing App | Swipe the app icon up or down in the App Queue. | The app is removed from the queue and a force-stop shell command is executed to kill the app. |
| Favoriting (Global) | Long-press an app in the main search list or swipe the app left/right in the search list. | Toggles the star icon and adds/removes the app from your global favorites list. |
3. Tiling Position & Order
Tiling positions are determined strictly from left-to-right in the App Queue to top-to-bottom, left-to-right in the selected screen layout.
 * The leftmost app in your queue corresponds to the first defined window tile in your layout.
 * The second app corresponds to the second tile, and so on.
Example: 4-Quadrant Layout
 * Tile 1 (Top-Left): Corresponds to the 1st app in the queue.
 * Tile 2 (Top-Right): Corresponds to the 2nd app in the queue.
 * Tile 3 (Bottom-Left): Corresponds to the 3rd app in the queue.
 * Tile 4 (Bottom-Right): Corresponds to the 4th app in the queue.
You can ensure an app lands in a specific tile by dragging it to the corresponding position in the App Queue.

![Screenshot_20251130_125934_Reddit](https://github.com/user-attachments/assets/a4644964-8371-4f39-9a03-df88e4a8524a)
![Screenshot_20251130_130005_Reddit](https://github.com/user-attachments/assets/24ceaf2f-5212-4fe6-b027-e5941164ca93)
![Screenshot_20251130_125958_Reddit](https://github.com/user-attachments/assets/3809df01-7d7a-4383-b5f3-f9aa85998685)
![Screenshot_20251130_125944_Reddit](https://github.com/user-attachments/assets/98ab3819-6204-44b7-9c6e-92c961f3151e)
![Screenshot_20251130_125940_Reddit](https://github.com/user-attachments/assets/fd1ab37f-3158-4b01-a342-9c5f82423b89)
![Screenshot_20251130_125922_Reddit](https://github.com/user-attachments/assets/2af9a4f6-8dd2-48da-9551-943845a28613)
![Screenshot_20251130_125807_One UI Cover Home](https://github.com/user-attachments/assets/eb08e879-6c55-45f6-a175-a19791588337)
![Screenshot_20251130_130124_Photos](https://github.com/user-attachments/assets/e9449ba4-4ac9-401d-b8b4-74329f948ebf)

Want to donate to support the development of this project? https://ko-fi.com/katsuyamaki
