-keep class com.katsuyamaki.DroidOSLauncher.ShellUserService { *; }
-keep class com.katsuyamaki.DroidOSLauncher.IShellService { *; }
-keep interface com.katsuyamaki.DroidOSLauncher.IShellService { *; }

-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}

-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
    public void printStackTrace(java.io.PrintStream);
    public void printStackTrace(java.io.PrintWriter);
}
