package com.katsuyamaki.DroidOSLauncher;

interface IShellService {
    void injectKey(int keyCode, int action, int flags, int displayId, int metaState);
    void forceStop(String packageName);
    void runCommand(String command);
    void setScreenOff(int displayIndex, boolean turnOff);
    void repositionTask(String packageName, String className, int left, int top, int right, int bottom);
    List<String> getVisiblePackages(int displayId);
    List<String> getVisibleComponents(int displayId);
    List<String> getVisibleTaskComponents(int displayId);
    List<String> getWindowLayouts(int displayId);
    List<String> getAllRunningPackages();
    int getTaskId(String packageName, String className);
    void moveTaskToBack(int taskId);
    void moveTaskToFront(int taskId, int displayId);
    void batchResize(in List<String> packages, in int[] bounds);
    void batchResizeComponents(in List<String> packages, in List<String> classes, in int[] bounds);
    String getTaskDebugSnapshot(String packageName);
    boolean isTaskFreeform(String packageName);



    // Brightness Control
    void setSystemBrightness(int brightness);
    int getSystemBrightness();
    float getSystemBrightnessFloat();
    void setAutoBrightness(boolean enabled);
    boolean isAutoBrightness();
    
    // Legacy / Direct Hardware Control
    boolean setBrightnessViaDisplayManager(int displayId, float brightness);

    // NEW: Alternate Display Off Logic (Targeted)
    void setBrightness(int displayId, int value);
}
