package com.katsuyamaki.DroidOSTrackpadKeyboard;

import android.content.ComponentName;
import android.content.ServiceConnection;
import rikka.shizuku.Shizuku;

public class ShizukuBinder {
    
    public static void bind(ComponentName component, ServiceConnection connection, boolean debug, int version) {
        Shizuku.UserServiceArgs args = new Shizuku.UserServiceArgs(component)
                .processNameSuffix("shell")
                .daemon(false)
                .debuggable(debug)
                .version(version);
        
        Shizuku.bindUserService(args, connection);
    }

    public static void unbind(ComponentName component, ServiceConnection connection) {
        Shizuku.UserServiceArgs args = new Shizuku.UserServiceArgs(component)
                .processNameSuffix("shell")
                .daemon(false);
        
        Shizuku.unbindUserService(args, connection, true); 
    }
}
