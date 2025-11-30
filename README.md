DroidOS 📱🚀
DroidOS is a suite of advanced system tools designed to give "superpowers" to the standard Android experience.
It functions as a universal Samsung DeX replacement, a tiling window manager, and an unrestricted app launcher that works on any Android device. Whether you are using a Foldable, a Flip phone, AR Glasses, or a secondary monitor, DroidOS unlocks the full potential of your hardware.
📂 Project Structure (Monorepo)
This repository is a Monorepo containing two distinct but complementary Android applications.
> ⚠️ Developer Note: Do not open this root folder directly in Android Studio. You must open each project folder individually.
> 
| Project | Description | Path |
|---|---|---|
| CoverScreen Launcher | An advanced tiling window manager and app launcher. Bypasses cover screen restrictions and manages multi-window layouts. | /Cover-Screen-Launcher |
| CoverScreen Trackpad | A virtual mouse trackpad and custom keyboard overlay. Turns your phone into a precision input device for external displays. | /Cover-Screen-Trackpad |
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
 * Install CoverScreen Launcher to manage your apps and windows.
 * Install CoverScreen Trackpad to control the cursor.
 * Grant Shizuku permissions when prompted in each app.
🤝 Contributing
We welcome contributions! Please note that this is a Monorepo.
 * If you are fixing a bug in the Launcher, make your Pull Request against the Cover-Screen-Launcher directory.
 * If you are improving the Trackpad, work within the Cover-Screen-Trackpad directory.
📄 License
This project is licensed under the GNU General Public License v3.0 (GPLv3).
You are free to use, modify, and distribute this software, but all modifications must remain open source. See the LICENSE file for details.

