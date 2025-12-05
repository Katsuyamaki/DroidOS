package com.example.quadrantlauncher;

interface IShellService {
    void forceStop(String packageName);
    void runCommand(String command);
    void setScreenOff(int displayIndex, boolean turnOff);
    void setBrightness(int displayIndex, int brightness);
    void repositionTask(String packageName, int left, int top, int right, int bottom);
    List<String> getVisiblePackages(int displayId);
    List<String> getWindowLayouts(int displayId);
    List<String> getAllRunningPackages();
    
    // --- RESTORED V1.0 METHODS ---
    int getTaskId(String packageName);
    void moveTaskToBack(int taskId);
}
