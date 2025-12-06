package com.example.coverscreentester;

interface IShellService {
    void injectMouse(int action, float x, float y, int displayId, int source, int buttonState, long downTime);
    void injectScroll(float x, float y, float vDistance, float hDistance, int displayId);
    void execClick(float x, float y, int displayId);
    void execRightClick(float x, float y, int displayId);
    
    // Updated: Now accepts metaState (Ctrl/Alt) and displayId
    void injectKey(int keyCode, int action, int metaState, int displayId);
    
    void setWindowingMode(int taskId, int mode);
    void resizeTask(int taskId, int left, int top, int right, int bottom);
    String runCommand(String cmd);
}
