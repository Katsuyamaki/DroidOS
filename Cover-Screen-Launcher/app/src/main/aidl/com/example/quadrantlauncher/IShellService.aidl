package com.example.quadrantlauncher;

interface IShellService {
    void forceStop(String packageName);
    void runCommand(String command);
    void setScreenOff(int displayIndex, boolean turnOff);
    void repositionTask(String packageName, int left, int top, int right, int bottom);
    List<String> getVisiblePackages(int displayId);
    List<String> getWindowLayouts(int displayId);
    List<String> getAllRunningPackages();
    int getTaskId(String packageName);
    void moveTaskToBack(int taskId);
    void setSystemBrightness(int brightness);
    int getSystemBrightness();
    float getSystemBrightnessFloat();
    void setAutoBrightness(boolean enabled);
    boolean isAutoBrightness();
    boolean setBrightnessViaDisplayManager(int displayId, float brightness);
    void setBrightness(int value);
}
