--------- beginning of system
12-19 23:42:22.131  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
--------- beginning of main
12-19 23:42:22.827  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184110668547000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:22.829  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x4, f=0x0, d=0, '3f8bbcd', t=1 
12-19 23:42:22.829  1401  1700 I InputDispatcher: Delivering touch to (2045): action: 0x4, f=0x0, d=0, '253deac', t=1 
12-19 23:42:22.830  1401  1700 I InputDispatcher: Delivering touch to (13489): action: 0x0, f=0x0, d=0, 'dea7911', t=1 +(0,-116)
12-19 23:42:22.855  1401  3546 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:22.861  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:22.916  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184110759722000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:22.917  1401  1700 I InputDispatcher: Delivering touch to (13489): action: 0x1, f=0x0, d=0, 'dea7911', t=1 +(0,-116)
12-19 23:42:23.626  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184111468289000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:23.627  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, '3f8bbcd', t=1 +(-50,-200)
12-19 23:42:23.733  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184111576248000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:23.733  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, '3f8bbcd', t=1 +(-50,-200)
12-19 23:42:23.734 28359 28359 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{ffc88d0 VFE...C.. .F...... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerImpl.addView:158 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:546 com.example.quadrantlauncher.FloatingLauncherService.access$toggleDrawer:54 
12-19 23:42:23.735 28359 28359 D ViewRootImpl: desktopMode is false
12-19 23:42:23.735 28359 28359 I ViewRootImpl: dVRR is disabled
12-19 23:42:23.741  1401  2168 D WindowManager: Changing focus from Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity} to Window{b361125 u0 com.katsuyamaki.DroidOSLauncher} displayId=0 Callers=com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 com.android.server.wm.Session.addToDisplayAsUser:24 android.view.IWindowSession$Stub.onTransact:757 
12-19 23:42:23.741  1401  2168 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x788e27c
12-19 23:42:23.741  1401  2168 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-19 23:42:23.741  1401  2168 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-19 23:42:23.741  1401  2168 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x7e67308
12-19 23:42:23.742  1401  2168 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{b361125 u0 com.katsuyamaki.DroidOSLauncher}, navColorWin=Window{b217ba3 u0 InputMethod}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 
12-19 23:42:23.742  1401  2168 D WindowManager: updateSystemBarAttributes, bhv=1, apr=1048576, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
12-19 23:42:23.765  1401  2168 I WindowManager: Relayout Window{b361125 u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=1080x2404 ty=2032 d0
12-19 23:42:23.767  1401  2168 D WindowManager: makeSurface duration=0 name=$_28359
12-19 23:42:23.769  1401  2168 I WindowManager: Relayout hash=b361125, pid=28359, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP START CENTER sim={adjust=nothing} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:42:23.769  1401  2168 I WindowManager:   fl=1000300
12-19 23:42:23.769  1401  2168 I WindowManager:   bhv=1
12-19 23:42:23.769  1401  2168 I WindowManager:   fitTypes=206
12-19 23:42:23.769  1401  2168 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:23.769  1401  2168 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:23.780  1401  2247 D InputDispatcher: Once focus requested (0): b361125 com.katsuyamaki.DroidOSLauncher
12-19 23:42:23.780  1401  2247 D InputDispatcher: Focus request (0): b361125 com.katsuyamaki.DroidOSLauncher but waiting because NO_WINDOW
12-19 23:42:23.780  1401  2247 D InputDispatcher: Focus left window (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:42:23.917  1401  3546 D WindowManager: finishDrawingWindow: Window{b361125 u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-19 23:42:23.918  1401  3549 I WindowManager: Relayout Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=8 req=63x63 ty=2032 d0
12-19 23:42:23.918  1401  3549 E WindowManager: win=Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-19 23:42:23.919  1401  2168 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:23.920  1401  3549 I WindowManager: Relayout hash=3f8bbcd, pid=28359, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:42:23.920  1401  3549 I WindowManager:   fl=1040308
12-19 23:42:23.920  1401  3549 I WindowManager:   bhv=1
12-19 23:42:23.920  1401  3549 I WindowManager:   fitTypes=206
12-19 23:42:23.920  1401  3549 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:23.920  1401  3549 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:23.942  1401  2237 D InputDispatcher: Focus entered window (0): b361125 com.katsuyamaki.DroidOSLauncher
12-19 23:42:23.986  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:23.988  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:42:23.989  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-19 23:42:23.992  1401  2247 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x9901f49
12-19 23:42:23.992  1401  2247 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:42:23.993  1401  2247 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:42:23.993  1401  2247 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xbef0c50
12-19 23:42:23.995  1401  1545 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xfcd56f
12-19 23:42:23.995  1401  1545 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-19 23:42:23.995  1401  1545 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-19 23:42:23.996  1401  1545 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x7229c6f
12-19 23:42:24.007  1401  3549 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:24.013  1401  3549 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:24.013  1401  3549 I WindowManager:   fl=14318
12-19 23:42:24.013  1401  3549 I WindowManager:   pfl=14
12-19 23:42:24.013  1401  3549 I WindowManager:   bhv=1
12-19 23:42:24.013  1401  3549 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:24.013  1401  3549 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:24.013  1401  3549 I WindowManager:   sfl=8}
12-19 23:42:24.014  1401  1544 I ActivityManager: Changes in 10332 5 to 7, 255 to 128
12-19 23:42:24.027  1401  1646 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:24.034  1401  2773 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:24.034  1401  2773 I InputMethodManagerService: setImeWindowStatus: vis=0, backDisposition=0
12-19 23:42:24.038  1401  1646 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-19 23:42:24.039  1401  1646 I WindowManager: Relayout hash=b217ba3, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:42:24.039  1401  1646 I WindowManager:   fl=81800108
12-19 23:42:24.039  1401  1646 I WindowManager:   pfl=14000000
12-19 23:42:24.039  1401  1646 I WindowManager:   bhv=1
12-19 23:42:24.039  1401  1646 I WindowManager:   fitTypes=3
12-19 23:42:24.039  1401  1646 I WindowManager:   fitSides=7
12-19 23:42:24.039  1401  1646 I WindowManager:   fitIgnoreVis
12-19 23:42:24.039  1401  1646 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:42:24.039  1401  1646 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:42:24.040  1401  1646 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-19 23:42:24.550  1401  1646 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=8 req=1080x2404 ty=2011 d0
12-19 23:42:24.550  1401  1646 E WindowManager: win=Window{b217ba3 u0 InputMethod} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-19 23:42:24.552  1401  1646 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xbef0c50
12-19 23:42:24.552  1401  1646 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:42:24.552  1401  1646 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:42:24.552  1401  1646 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x4f4f0bd
12-19 23:42:24.556  1401  1646 I WindowManager: Relayout hash=b217ba3, pid=15168, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:42:24.556  1401  1646 I WindowManager:   fl=81800108
12-19 23:42:24.556  1401  1646 I WindowManager:   pfl=14000000
12-19 23:42:24.556  1401  1646 I WindowManager:   bhv=1
12-19 23:42:24.556  1401  1646 I WindowManager:   fitTypes=3
12-19 23:42:24.556  1401  1646 I WindowManager:   fitSides=7
12-19 23:42:24.556  1401  1646 I WindowManager:   fitIgnoreVis
12-19 23:42:24.556  1401  1646 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:42:24.556  1401  1646 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:42:24.560  1401  1646 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion(), touchableInsets 3 -> 3
12-19 23:42:24.571  1401  2773 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:24.574  1401  2773 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:24.574  1401  2773 I WindowManager:   fl=14318
12-19 23:42:24.574  1401  2773 I WindowManager:   pfl=14
12-19 23:42:24.574  1401  2773 I WindowManager:   bhv=1
12-19 23:42:24.574  1401  2773 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:24.574  1401  2773 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:24.574  1401  2773 I WindowManager:   sfl=8}
12-19 23:42:24.578  1401  2773 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:24.583  1401  2773 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:24.583  1401  2773 I WindowManager:   fl=14318
12-19 23:42:24.583  1401  2773 I WindowManager:   pfl=14
12-19 23:42:24.583  1401  2773 I WindowManager:   bhv=1
12-19 23:42:24.583  1401  2773 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:24.583  1401  2773 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:24.583  1401  2773 I WindowManager:   sfl=8}
12-19 23:42:25.936  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:26.222  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184114060503000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:26.223  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, 'b361125', t=1 +(0,-116)
12-19 23:42:26.252  1401  3546 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:26.253  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:26.347  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184114190213000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:26.347  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, 'b361125', t=1 +(0,-116)
12-19 23:42:26.405  1401  2773 I WindowManager: Using new display size: 1080x2520
12-19 23:42:27.383  1401  2773 D WindowManager: Transition is created, t=TransitionRecord{862b644 id=-1 type=TO_BACK flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.Task.moveTaskToBack:19 com.android.server.wm.Task.moveTaskToBack:1 com.android.server.wm.ActivityTaskManagerService$LocalService.moveTaskToBack:50 com.android.server.am.ActivityManagerService.moveTaskToBackWithBundle:16 
12-19 23:42:27.385  1401  2773 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{2b95cdf u0 com.google.android.googlequicksearchbox/com.google.android.apps.search.assistant.surfaces.voice.robin.main.MainActivity}
12-19 23:42:27.390  1401  2773 D InputDispatcher: Focused application(0): ActivityRecord{145063843 u0 com.google.android.googlequicksearchbox/com.google.android.apps.search.assistant.surfaces.voice.robin.main.MainActivity t18307}
12-19 23:42:27.392  1401  2773 D InputDispatcher: Focused application(0): ActivityRecord{147709724 u0 com.termux/.app.TermuxActivity t18195}
12-19 23:42:27.416  1401  2245 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:27.506  1401  3546 D WindowManager: Transition is created, t=TransitionRecord{49f306b id=-1 type=OPEN flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createAndStartCollecting:106 com.android.server.wm.ActivityStarter.executeRequest:3537 com.android.server.wm.ActivityStarter.execute:1873 com.android.server.wm.ActivityTaskManagerService.startActivityAsUser:87 
12-19 23:42:27.511  1401  3546 D WindowManager: Aborting Transition: 10220 in state 0 called from com.android.server.wm.ActivityStarter.handleStartResult:802
12-19 23:42:27.523  1401  3546 I WindowManager: Relayout Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}: viewVisibility=0 req=1080x1260 ty=1 d0
12-19 23:42:27.525  1401  3546 I WindowManager: Relayout hash=dea7911, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} ty=BASE_APPLICATION wanim=0x1030317
12-19 23:42:27.525  1401  3546 I WindowManager:   fl=8d810100
12-19 23:42:27.525  1401  3546 I WindowManager:   pfl=10008040
12-19 23:42:27.525  1401  3546 I WindowManager:   vsysui=700
12-19 23:42:27.525  1401  3546 I WindowManager:   bhv=1
12-19 23:42:27.525  1401  3546 I WindowManager:   fitSides=0
12-19 23:42:27.525  1401  3546 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:27.525  1401  3546 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:27.591  1401  3546 D WindowManager: Transition is created, t=TransitionRecord{cc467e0 id=-1 type=OPEN flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createAndStartCollecting:106 com.android.server.wm.ActivityStarter.executeRequest:3537 com.android.server.wm.ActivityStarter.execute:1873 com.android.server.wm.ActivityTaskManagerService.startActivityAsUser:87 
12-19 23:42:27.596  1401  3546 D WindowManager: Aborting Transition: 10221 in state 0 called from com.android.server.wm.ActivityStarter.handleStartResult:802
12-19 23:42:27.599  1401  3549 I WindowManager: Relayout Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}: viewVisibility=0 req=1080x1260 ty=1 d0
12-19 23:42:27.600  1401  3549 I WindowManager: Relayout hash=dea7911, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} ty=BASE_APPLICATION wanim=0x1030317
12-19 23:42:27.600  1401  3549 I WindowManager:   fl=8d810100
12-19 23:42:27.600  1401  3549 I WindowManager:   pfl=10008040
12-19 23:42:27.600  1401  3549 I WindowManager:   vsysui=700
12-19 23:42:27.600  1401  3549 I WindowManager:   bhv=1
12-19 23:42:27.600  1401  3549 I WindowManager:   fitSides=0
12-19 23:42:27.600  1401  3549 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:27.600  1401  3549 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:27.727  1401  2237 W WindowManager: Cannot find window which accessibility connection is added to
12-19 23:42:27.732  1401  3549 W WindowManager: Failed looking up window session=Session{c966652 2045:u0a10055} callers=com.android.server.wm.Session.remove:10 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-19 23:42:27.749  1401  3549 I WindowManager: Relayout Window{2b95cdf u0 com.google.android.googlequicksearchbox/com.google.android.apps.search.assistant.surfaces.voice.robin.main.MainActivity}: viewVisibility=4 req=1080x1260 ty=1 d0
12-19 23:42:27.750  1401  3549 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{b361125 u0 com.katsuyamaki.DroidOSLauncher}, navColorWin=Window{8d8ec89 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.DisplayPolicy.finishPostLayoutPolicyLw:17 com.android.server.wm.RootWindowContainer.applySurfaceChangesTransaction$1:195 com.android.server.wm.RootWindowContainer.performSurfacePlacementNoTrace:86 
12-19 23:42:27.750  1401  3549 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
12-19 23:42:27.751  1401  3549 I WindowManager: Relayout hash=2b95cdf, pid=19274, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION wanim=0x1030317
12-19 23:42:27.751  1401  3549 I WindowManager:   fl=80810100
12-19 23:42:27.751  1401  3549 I WindowManager:   pfl=10008840
12-19 23:42:27.751  1401  3549 I WindowManager:   bhv=1
12-19 23:42:27.751  1401  3549 I WindowManager:   fitSides=0
12-19 23:42:27.751  1401  3549 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:27.751  1401  3549 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:27.770  1401  2237 E WindowManager: win=Window{2b95cdf u0 com.google.android.googlequicksearchbox/com.google.android.apps.search.assistant.surfaces.voice.robin.main.MainActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=4 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:192 com.android.server.wm.ActivityClientController.activityStopped:97 android.app.IActivityClientController$Stub.onTransact:726 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1462 android.os.Binder.execTransact:1401 
12-19 23:42:27.780  1401  3549 D WindowManager: Transition is created, t=TransitionRecord{6239f22 id=-1 type=CHANGE flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.ActivityTaskManagerService.resizeTask:129 com.android.server.am.ActivityManagerShellCommand.runTask:220 com.android.server.am.ActivityManagerShellCommand.onCommand:1690 com.android.modules.utils.BasicShellCommandHandler.exec:97 
12-19 23:42:27.781  1401  3549 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}
12-19 23:42:27.862  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184115705581000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:27.862  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, 'b361125', t=1 +(0,-116)
12-19 23:42:28.019  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184115863792000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:28.020  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, 'b361125', t=1 +(0,-116)
12-19 23:42:28.174  1401  2237 I WindowManager: Using new display size: 1080x2520
12-19 23:42:28.177  1401  2245 I ActivityManager: Force stopping com.google.android.googlequicksearchbox appid=10270 user=0: from pid 32626
12-19 23:42:28.178  1401  2245 I ActivityManager: Killing 19274:com.google.android.googlequicksearchbox:search/u0a270 (adj 199): stop com.google.android.googlequicksearchbox due to from pid 32626
12-19 23:42:28.183  1401  2245 I ActivityManager: Killing 29094:com.google.android.googlequicksearchbox:googleapp/u0a270 (adj 915): stop com.google.android.googlequicksearchbox due to from pid 32626
12-19 23:42:28.185  1401  2245 I ActivityManager: Killing 29576:com.google.android.webview:sandboxed_process0:org.chromium.content.app.SandboxedProcessService0:0/u0a270i299 (adj 0): isolated not needed
12-19 23:42:28.185  1401  2773 W WindowManager: Failed looking up window session=Session{c966652 2045:u0a10055} callers=com.android.server.wm.Session.remove:10 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-19 23:42:28.186  1401  2773 W WindowManager: Failed looking up window session=Session{c966652 2045:u0a10055} callers=com.android.server.wm.Session.remove:10 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-19 23:42:28.186  1401  2245 I ActivityManager: Killing 19262:com.google.android.googlequicksearchbox:interactor/u0a270 (adj 100): stop com.google.android.googlequicksearchbox due to from pid 32626
12-19 23:42:28.188  1401  2773 I WindowManager: Using new display size: 1080x2520
12-19 23:42:28.188  1401  2245 I ActivityManager: Killing 29626:com.google.android.webview:sandboxed_process0:org.chromium.content.app.SandboxedProcessService0:1/u0a270i300 (adj 0): isolated not needed
12-19 23:42:28.190  1401  2245 I ActivityManager:   Force stopping service ServiceRecord{ba992b4 u0 com.google.android.googlequicksearchbox/com.google.android.apps.gsa.hotword.hotworddetectionservice.GsaHotwordDetectionService:hotword_detector_0 c:android}
12-19 23:42:28.193  1401  2237 I ActivityManager: Killing 19462:com.google.android.googlequicksearchbox:trusted_disable_art_image_:com.google.android.apps.gsa.hotword.hotworddetectionservice.GsaHotwordDetectionService:hotword_detector_0/u0a270i297 (adj 100): isolated not needed
12-19 23:42:28.193  1401  3549 W ActivityManager: Service done with onDestroy, but not inDestroying: ServiceRecord{cace62 u0 com.google.android.googlequicksearchbox/com.google.frameworks.client.data.android.server.tiktok.assistant.InteractorProcessEndpointService c:com.google.android.googlequicksearchbox}, app=ProcessRecord{faf0ecd 0:com.google.android.googlequicksearchbox:interactor/u0a270}
12-19 23:42:28.203  1401  1544 I ActivityManager: Changes in 99297 5 to 19, 184 to 0
12-19 23:42:28.237  1401  1470 W WindowManager: Cannot find window which accessibility connection is added to
12-19 23:42:28.238  1401  1553 I ActivityManager: Start proc 32648:com.samsung.euicc/1000 for service {com.samsung.euicc/com.samsung.euicc.service.EuiccServiceImpl}
12-19 23:42:28.238  1401  1553 I ActivityManager: ProcessObserver broadcast disabled
12-19 23:42:28.238  1401  2221 D InputMethodManagerService: onClientRemovedInternalLocked
12-19 23:42:28.238  1401  1544 W ActivityManager: setHasOverlayUi called on unknown pid: 19274
12-19 23:42:28.246  1401  1552 W ActivityManager: pid 1401 system sent binder code 9 with flags 1 to frozen apps and got error -32
12-19 23:42:28.251  1401  1552 W ActivityManager: pid 1401 system sent binder code 1 with flags 1 to frozen apps and got error -32
12-19 23:42:28.251  1401  1552 W ActivityManager: pid 1401 system sent binder code 1 with flags 1 to frozen apps and got error -32
12-19 23:42:28.258  1401  1765 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:28.258  1401  1765 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:42:28.261  1401  1553 I ActivityManager: Start proc 32658:com.google.android.googlequicksearchbox:interactor/u0a270 for service {com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService}
12-19 23:42:28.261  1401  1553 I ActivityManager: ProcessObserver broadcast disabled
12-19 23:42:28.263  1401  1765 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:28.263  1401  1765 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:42:28.287  1401  1553 I ActivityManager: Start proc 32678:com.google.android.googlequicksearchbox:search/u0a270 for broadcast {com.google.android.googlequicksearchbox/com.google.android.libraries.assistant.contexttrigger.impl.ContextTriggerBroadcastReceiver_Receiver}
12-19 23:42:28.287  1401  1553 I ActivityManager: ProcessObserver broadcast disabled
12-19 23:42:28.344  1401  1868 I ActivityManager: Changes in 10270 19 to 5, 0 to 255
12-19 23:42:28.404  1401  3547 D WindowManager: Transition is created, t=TransitionRecord{c9c6667 id=-1 type=CHANGE flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.ActivityTaskManagerService.resizeTask:129 com.android.server.am.ActivityManagerShellCommand.runTask:220 com.android.server.am.ActivityManagerShellCommand.onCommand:1690 com.android.modules.utils.BasicShellCommandHandler.exec:97 
12-19 23:42:28.405  1401  3547 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}
12-19 23:42:28.417  1401  2245 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:28.507  1401  1553 I ActivityManager: Start proc 541:com.google.android.googlequicksearchbox:googleapp/u0a270 for broadcast {com.google.android.googlequicksearchbox/com.google.android.libraries.notifications.platform.entrypoints.restart.RestartReceiver}
12-19 23:42:28.507  1401  1553 I ActivityManager: ProcessObserver broadcast disabled
12-19 23:42:28.773  1401  1553 I ActivityManager: Start proc 1048:com.google.android.googlequicksearchbox:trusted_disable_art_image_:com.google.android.apps.gsa.hotword.hotworddetectionservice.GsaHotwordDetectionService:hotword_detector_0/u0i301 for service {com.google.android.googlequicksearchbox/com.google.android.apps.gsa.hotword.hotworddetectionservice.GsaHotwordDetectionService:hotword_detector_0}
12-19 23:42:28.773  1401  1553 I ActivityManager: ProcessObserver broadcast disabled
12-19 23:42:28.782  1401  1553 I ActivityManager: Start proc 1053:com.android.hotwordenrollment.xgoogle/u0a262 for broadcast {com.android.hotwordenrollment.xgoogle/com.android.hotwordenrollment.xgoogle.EnrollmentDelegateReceiver}
12-19 23:42:28.783  1401  1553 I ActivityManager: ProcessObserver broadcast disabled
12-19 23:42:28.813  1401  3507 I ActivityManager: Changes in 99301 19 to 5, 0 to 184
12-19 23:42:28.828  1401  3507 I ActivityManager: Changes in 10262 19 to 11, 0 to 128
12-19 23:42:28.844  1401  3547 I ActivityManager: Changes in 10262 11 to 19, 128 to 0
12-19 23:42:29.112  1401  3547 D WindowManager: Transition is created, t=TransitionRecord{998bb88 id=-1 type=OPEN flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createAndStartCollecting:106 com.android.server.wm.ActivityStarter.executeRequest:3537 com.android.server.wm.ActivityStarter.execute:1873 com.android.server.wm.ActivityTaskManagerService.startActivityAsUser:87 
12-19 23:42:29.118  1401  3547 D WindowManager: Aborting Transition: 10224 in state 0 called from com.android.server.wm.ActivityStarter.handleStartResult:802
12-19 23:42:29.123  1401  3547 I WindowManager: Relayout Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}: viewVisibility=0 req=1080x1260 ty=1 d0
12-19 23:42:29.128  1401  3507 D WindowManager: Transition is created, t=TransitionRecord{3c20834 id=-1 type=OPEN flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createAndStartCollecting:106 com.android.server.wm.ActivityStarter.executeRequest:3537 com.android.server.wm.ActivityStarter.execute:1873 com.android.server.wm.ActivityTaskManagerService.startActivityAsUser:87 
12-19 23:42:29.130  1401  3507 D WindowManager: Aborting Transition: 10225 in state 0 called from com.android.server.wm.ActivityStarter.handleStartResult:802
12-19 23:42:29.130  1401  3547 I WindowManager: Relayout hash=dea7911, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} ty=BASE_APPLICATION wanim=0x1030317
12-19 23:42:29.130  1401  3547 I WindowManager:   fl=8d810100
12-19 23:42:29.130  1401  3547 I WindowManager:   pfl=10008040
12-19 23:42:29.130  1401  3547 I WindowManager:   vsysui=700
12-19 23:42:29.130  1401  3547 I WindowManager:   bhv=1
12-19 23:42:29.130  1401  3547 I WindowManager:   fitSides=0
12-19 23:42:29.130  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:29.130  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:29.139  1401  3509 I WindowManager: Relayout Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}: viewVisibility=0 req=1080x1260 ty=1 d0
12-19 23:42:29.144  1401  3509 I WindowManager: Relayout hash=dea7911, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} ty=BASE_APPLICATION wanim=0x1030317
12-19 23:42:29.144  1401  3509 I WindowManager:   fl=8d810100
12-19 23:42:29.144  1401  3509 I WindowManager:   pfl=10008040
12-19 23:42:29.144  1401  3509 I WindowManager:   vsysui=700
12-19 23:42:29.144  1401  3509 I WindowManager:   bhv=1
12-19 23:42:29.144  1401  3509 I WindowManager:   fitSides=0
12-19 23:42:29.144  1401  3509 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:29.144  1401  3509 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:29.177  1401  3547 D WindowManager: Transition is created, t=TransitionRecord{8e467ff id=-1 type=OPEN flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createAndStartCollecting:106 com.android.server.wm.ActivityStarter.executeRequest:3537 com.android.server.wm.ActivityStarter.execute:1873 com.android.server.wm.ActivityTaskManagerService.startActivityAsUser:87 
12-19 23:42:29.177  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184117021157000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:29.177  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, 'b361125', t=1 +(0,-116)
12-19 23:42:29.178  1401  3547 D WindowManager: Aborting Transition: 10226 in state 0 called from com.android.server.wm.ActivityStarter.handleStartResult:802
12-19 23:42:29.180  1401  3547 I WindowManager: Relayout Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}: viewVisibility=0 req=1080x1260 ty=1 d0
12-19 23:42:29.181  1401  3547 I WindowManager: Relayout hash=dea7911, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} ty=BASE_APPLICATION wanim=0x1030317
12-19 23:42:29.181  1401  3547 I WindowManager:   fl=8d810100
12-19 23:42:29.181  1401  3547 I WindowManager:   pfl=10008040
12-19 23:42:29.181  1401  3547 I WindowManager:   vsysui=700
12-19 23:42:29.181  1401  3547 I WindowManager:   bhv=1
12-19 23:42:29.181  1401  3547 I WindowManager:   fitSides=0
12-19 23:42:29.181  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:29.181  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:29.211  1401  3509 D WindowManager: Transition is created, t=TransitionRecord{511e664 id=-1 type=OPEN flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createAndStartCollecting:106 com.android.server.wm.ActivityStarter.executeRequest:3537 com.android.server.wm.ActivityStarter.execute:1873 com.android.server.wm.ActivityTaskManagerService.startActivityAsUser:87 
12-19 23:42:29.213  1401  3509 D WindowManager: Aborting Transition: 10227 in state 0 called from com.android.server.wm.ActivityStarter.handleStartResult:802
12-19 23:42:29.222  1401  3547 I WindowManager: Relayout Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}: viewVisibility=0 req=1080x1260 ty=1 d0
12-19 23:42:29.223  1401  3547 I WindowManager: Relayout hash=dea7911, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} ty=BASE_APPLICATION wanim=0x1030317
12-19 23:42:29.223  1401  3547 I WindowManager:   fl=8d810100
12-19 23:42:29.223  1401  3547 I WindowManager:   pfl=10008040
12-19 23:42:29.223  1401  3547 I WindowManager:   vsysui=700
12-19 23:42:29.223  1401  3547 I WindowManager:   bhv=1
12-19 23:42:29.223  1401  3547 I WindowManager:   fitSides=0
12-19 23:42:29.223  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:29.223  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:29.277  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184117121076000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:29.277  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, 'b361125', t=1 +(0,-116)
12-19 23:42:29.379  1401  3509 D WindowManager: Transition is created, t=TransitionRecord{ac7ad2e id=-1 type=CHANGE flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.ActivityTaskManagerService.resizeTask:129 com.android.server.am.ActivityManagerShellCommand.runTask:220 com.android.server.am.ActivityManagerShellCommand.onCommand:1690 com.android.modules.utils.BasicShellCommandHandler.exec:97 
12-19 23:42:29.380  1401  3509 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}
12-19 23:42:29.415  1401  3509 D WindowManager: Transition is created, t=TransitionRecord{fa7581d id=-1 type=CHANGE flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.ActivityTaskManagerService.resizeTask:129 com.android.server.am.ActivityManagerShellCommand.runTask:220 com.android.server.am.ActivityManagerShellCommand.onCommand:1690 com.android.modules.utils.BasicShellCommandHandler.exec:97 
12-19 23:42:29.416  1401  3509 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}
12-19 23:42:29.424  1401  3509 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:30.087  1401  3507 D WindowManager: Transition is created, t=TransitionRecord{89e83a2 id=-1 type=CHANGE flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.ActivityTaskManagerService.resizeTask:129 com.android.server.am.ActivityManagerShellCommand.runTask:220 com.android.server.am.ActivityManagerShellCommand.onCommand:1690 com.android.modules.utils.BasicShellCommandHandler.exec:97 
12-19 23:42:30.088  1401  3507 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}
12-19 23:42:30.089  1401  3507 D WindowManager: Transition is created, t=TransitionRecord{f57848f id=-1 type=CHANGE flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.ActivityTaskManagerService.resizeTask:129 com.android.server.am.ActivityManagerShellCommand.runTask:220 com.android.server.am.ActivityManagerShellCommand.onCommand:1690 com.android.modules.utils.BasicShellCommandHandler.exec:97 
12-19 23:42:30.104  1401  1544 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}
12-19 23:42:32.300  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:32.893  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:33.217  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184121055036000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:33.220  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, 'b361125', t=1 +(0,-116)
12-19 23:42:33.267  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:33.270  1401  2495 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:33.310  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184121151870000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:33.310  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, 'b361125', t=1 +(0,-116)
12-19 23:42:33.311 28359 28359 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{ffc88d0 VFE...C.. .F.P.... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:537 
12-19 23:42:33.319  1401  2251 I WindowManager: Relayout Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=63x63 ty=2032 d0
12-19 23:42:33.320  1401  2251 D WindowManager: makeSurface duration=0 name=$_28359
12-19 23:42:33.322  1401  2251 I WindowManager: Relayout hash=3f8bbcd, pid=28359, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:42:33.322  1401  2251 I WindowManager:   fl=1040308
12-19 23:42:33.322  1401  2251 I WindowManager:   bhv=1
12-19 23:42:33.322  1401  2251 I WindowManager:   fitTypes=206
12-19 23:42:33.322  1401  2251 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:33.322  1401  2251 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:33.334  1401  3509 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x4f4f0bd
12-19 23:42:33.334  1401  3509 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:42:33.334  1401  3509 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:42:33.335  1401  3509 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xd68af7f
12-19 23:42:33.336  1401  3509 D WindowManager: Changing focus from Window{b361125 u0 com.katsuyamaki.DroidOSLauncher} to Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity} displayId=0 Callers=com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-19 23:42:33.337  1401  3509 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x7229c6f
12-19 23:42:33.337  1401  3509 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-19 23:42:33.337  1401  3509 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-19 23:42:33.337  1401  3509 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x862c49b
12-19 23:42:33.337  1401  3509 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x7e67308
12-19 23:42:33.337  1401  3509 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-19 23:42:33.337  1401  3509 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-19 23:42:33.337  1401  3509 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x4d72038
12-19 23:42:33.338  1401  3509 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}, navColorWin=Window{8d8ec89 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 
12-19 23:42:33.338  1401  3509 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-1
12-19 23:42:33.339  1401  2251 D InputDispatcher: Focus left window (0): b361125 com.katsuyamaki.DroidOSLauncher
12-19 23:42:33.342  1401  2495 D WindowManager: finishDrawingWindow: Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-19 23:42:33.343  1401  2495 W WindowManager: Failed looking up window session=Session{dbe50cc 28359:u0a10516} callers=com.android.server.wm.WindowManagerService.windowForClientLocked:1 com.android.server.wm.Session.setOnBackInvokedCallbackInfo:15 android.view.IWindowSession$Stub.onTransact:1390 
12-19 23:42:33.343  1401  2495 I WindowManager: setOnBackInvokedCallback(): No window state for package:com.katsuyamaki.DroidOSLauncher
12-19 23:42:33.349  1401  2495 D InputDispatcher: Once focus requested (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:42:33.349  1401  2495 D InputDispatcher: Focus entered window (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:42:33.354  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:33.354  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:42:33.354  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-19 23:42:33.356  1401  1763 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-19 23:42:33.363  1401  1763 I ActivityManager: Changes in 10332 7 to 5, 128 to 255
12-19 23:42:33.365  1401  3547 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:42:33.366  1401  1703 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:42:33.367  1401  1763 I InputMethodManagerService: attachNewInputLocked: showCurrentInputInternal, softInputModeState=STATE_UNSPECIFIED|ADJUST_RESIZE|IS_FORWARD_NAVIGATION
12-19 23:42:33.374  1401  1544 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-19 23:42:33.377  1401  3509 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:33.380  1401  3509 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:33.380  1401  3509 I WindowManager:   fl=14318
12-19 23:42:33.380  1401  3509 I WindowManager:   pfl=14
12-19 23:42:33.380  1401  3509 I WindowManager:   bhv=1
12-19 23:42:33.380  1401  3509 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:33.380  1401  3509 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:33.380  1401  3509 I WindowManager:   sfl=8}
12-19 23:42:33.380  1401  3549 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:33.380  1401  3549 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:42:33.381  1401  3549 I InputMethodManagerService: setImeWindowStatus: vis=3, backDisposition=0
12-19 23:42:33.390  1401  3545 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-19 23:42:33.390  1401  3545 D WindowManager: makeSurface duration=1 name=InputMethod$_15168
12-19 23:42:33.392  1401  3545 I WindowManager: Relayout hash=b217ba3, pid=15168, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:42:33.392  1401  3545 I WindowManager:   fl=81800108
12-19 23:42:33.392  1401  3545 I WindowManager:   pfl=14000000
12-19 23:42:33.392  1401  3545 I WindowManager:   bhv=1
12-19 23:42:33.392  1401  3545 I WindowManager:   fitTypes=3
12-19 23:42:33.392  1401  3545 I WindowManager:   fitSides=7
12-19 23:42:33.392  1401  3545 I WindowManager:   fitIgnoreVis
12-19 23:42:33.392  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:42:33.392  1401  3545 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:42:33.397  1401  1646 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion() -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-19 23:42:33.404  1401  3545 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:33.406  1401  3545 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:33.406  1401  3545 I WindowManager:   fl=14318
12-19 23:42:33.406  1401  3545 I WindowManager:   pfl=14
12-19 23:42:33.406  1401  3545 I WindowManager:   bhv=1
12-19 23:42:33.406  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:33.406  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:33.406  1401  3545 I WindowManager:   sfl=8}
12-19 23:42:33.407  1401  1646 D WindowManager: finishDrawingWindow: Window{b217ba3 u0 InputMethod} mDrawState=DRAW_PENDING seqId=0
12-19 23:42:33.408  1401  2251 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:42:33.409  1401  1703 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:42:33.414  1401  3547 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:33.416  1401  3547 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:33.416  1401  3547 I WindowManager:   fl=14318
12-19 23:42:33.416  1401  3547 I WindowManager:   pfl=14
12-19 23:42:33.416  1401  3547 I WindowManager:   bhv=1
12-19 23:42:33.416  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:33.416  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:33.416  1401  3547 I WindowManager:   sfl=8}
12-19 23:42:33.422  1401  3545 D WindowManager: finishDrawingWindow: Window{b217ba3 u0 InputMethod} mDrawState=HAS_DRAWN seqId=0
12-19 23:42:34.682  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:34.769  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184122609742000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:34.770  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, '3f8bbcd', t=1 +(-50,-200)
12-19 23:42:34.775  1401  3509 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:34.802  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:34.865  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184122709245000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:34.865  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, '3f8bbcd', t=1 +(-50,-200)
12-19 23:42:34.867 28359 28359 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{ffc88d0 VFE...C.. .F...... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerImpl.addView:158 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:546 com.example.quadrantlauncher.FloatingLauncherService.access$toggleDrawer:54 
12-19 23:42:34.867 28359 28359 D ViewRootImpl: desktopMode is false
12-19 23:42:34.867 28359 28359 I ViewRootImpl: dVRR is disabled
12-19 23:42:34.877  1401  3509 D WindowManager: Changing focus from Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity} to Window{908afac u0 com.katsuyamaki.DroidOSLauncher} displayId=0 Callers=com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 com.android.server.wm.Session.addToDisplayAsUser:24 android.view.IWindowSession$Stub.onTransact:757 
12-19 23:42:34.877  1401  3509 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x4d72038
12-19 23:42:34.878  1401  3509 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-19 23:42:34.878  1401  3509 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-19 23:42:34.878  1401  3509 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x3bf4844
12-19 23:42:34.878  1401  3509 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{908afac u0 com.katsuyamaki.DroidOSLauncher}, navColorWin=Window{b217ba3 u0 InputMethod}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 
12-19 23:42:34.879  1401  3509 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
12-19 23:42:34.899  1401  1703 I WindowManager: Relayout Window{908afac u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=1080x2404 ty=2032 d0
12-19 23:42:34.899  1401  1703 D WindowManager: makeSurface duration=1 name=$_28359
12-19 23:42:34.901  1401  1703 I WindowManager: Relayout hash=908afac, pid=28359, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP START CENTER sim={adjust=nothing} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:42:34.901  1401  1703 I WindowManager:   fl=1000300
12-19 23:42:34.901  1401  1703 I WindowManager:   bhv=1
12-19 23:42:34.901  1401  1703 I WindowManager:   fitTypes=206
12-19 23:42:34.901  1401  1703 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:34.901  1401  1703 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:34.913  1401  3509 D InputDispatcher: Once focus requested (0): 908afac com.katsuyamaki.DroidOSLauncher
12-19 23:42:34.913  1401  3509 D InputDispatcher: Focus request (0): 908afac com.katsuyamaki.DroidOSLauncher but waiting because NO_WINDOW
12-19 23:42:34.914  1401  3509 D InputDispatcher: Focus left window (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:42:34.994  1401  3547 I WindowManager: Relayout Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=8 req=63x63 ty=2032 d0
12-19 23:42:34.995  1401  3547 E WindowManager: win=Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-19 23:42:34.997  1401  3547 I WindowManager: Relayout hash=3f8bbcd, pid=28359, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:42:34.997  1401  3547 I WindowManager:   fl=1040308
12-19 23:42:34.997  1401  3547 I WindowManager:   bhv=1
12-19 23:42:34.997  1401  3547 I WindowManager:   fitTypes=206
12-19 23:42:34.997  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:34.997  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:34.998  1401  2251 D WindowManager: finishDrawingWindow: Window{908afac u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-19 23:42:35.014  1401  3545 D InputDispatcher: Focus entered window (0): 908afac com.katsuyamaki.DroidOSLauncher
12-19 23:42:35.024  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:35.075  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:42:35.075  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-19 23:42:35.077  1401  1703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xd68af7f
12-19 23:42:35.077  1401  1703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:42:35.077  1401  1703 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:42:35.078  1401  1703 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x53f35e
12-19 23:42:35.083  1401  1545 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x862c49b
12-19 23:42:35.083  1401  1545 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-19 23:42:35.083  1401  1545 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-19 23:42:35.084  1401  1545 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xdfe4155
12-19 23:42:35.090  1401  1544 I ActivityManager: Changes in 10332 5 to 7, 255 to 128
12-19 23:42:35.093  1401  3549 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:35.104  1401  3549 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:35.104  1401  3549 I WindowManager:   fl=14318
12-19 23:42:35.104  1401  3549 I WindowManager:   pfl=14
12-19 23:42:35.104  1401  3549 I WindowManager:   bhv=1
12-19 23:42:35.104  1401  3549 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:35.104  1401  3549 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:35.104  1401  3549 I WindowManager:   sfl=8}
12-19 23:42:35.107  1401  3545 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:35.107  1401  3545 I InputMethodManagerService: setImeWindowStatus: vis=0, backDisposition=0
12-19 23:42:35.110  1401  3509 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-19 23:42:35.111  1401  3509 I WindowManager: Relayout hash=b217ba3, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:42:35.111  1401  3509 I WindowManager:   fl=81800108
12-19 23:42:35.111  1401  3509 I WindowManager:   pfl=14000000
12-19 23:42:35.111  1401  3509 I WindowManager:   bhv=1
12-19 23:42:35.111  1401  3509 I WindowManager:   fitTypes=3
12-19 23:42:35.111  1401  3509 I WindowManager:   fitSides=7
12-19 23:42:35.111  1401  3509 I WindowManager:   fitIgnoreVis
12-19 23:42:35.111  1401  3509 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:42:35.111  1401  3509 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:42:35.114  1401  3509 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-19 23:42:35.133  1401  1703 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:35.624  1401  1703 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=8 req=1080x2404 ty=2011 d0
12-19 23:42:35.624  1401  1703 E WindowManager: win=Window{b217ba3 u0 InputMethod} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-19 23:42:35.625  1401  1703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x53f35e
12-19 23:42:35.625  1401  1703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:42:35.626  1401  1703 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:42:35.626  1401  1703 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xe80ff1a
12-19 23:42:35.630  1401  1703 I WindowManager: Relayout hash=b217ba3, pid=15168, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:42:35.630  1401  1703 I WindowManager:   fl=81800108
12-19 23:42:35.630  1401  1703 I WindowManager:   pfl=14000000
12-19 23:42:35.630  1401  1703 I WindowManager:   bhv=1
12-19 23:42:35.630  1401  1703 I WindowManager:   fitTypes=3
12-19 23:42:35.630  1401  1703 I WindowManager:   fitSides=7
12-19 23:42:35.630  1401  1703 I WindowManager:   fitIgnoreVis
12-19 23:42:35.630  1401  1703 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:42:35.630  1401  1703 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:42:35.634  1401  1703 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion(), touchableInsets 3 -> 3
12-19 23:42:35.637  1401  1703 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:35.638  1401  1703 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:35.638  1401  1703 I WindowManager:   fl=14318
12-19 23:42:35.638  1401  1703 I WindowManager:   pfl=14
12-19 23:42:35.638  1401  1703 I WindowManager:   bhv=1
12-19 23:42:35.638  1401  1703 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:35.638  1401  1703 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:35.638  1401  1703 I WindowManager:   sfl=8}
12-19 23:42:35.640  1401  1703 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:35.641  1401  1703 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:35.641  1401  1703 I WindowManager:   fl=14318
12-19 23:42:35.641  1401  1703 I WindowManager:   pfl=14
12-19 23:42:35.641  1401  1703 I WindowManager:   bhv=1
12-19 23:42:35.641  1401  1703 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:35.641  1401  1703 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:35.641  1401  1703 I WindowManager:   sfl=8}
12-19 23:42:36.173  1401  1553 I ActivityManager: Start proc 1762:com.google.android.apps.turbo/u0a266 for broadcast {com.google.android.apps.turbo/com.google.android.apps.turbo.nudges.broadcasts.BatteryStatusChangedReceiver}
12-19 23:42:36.173  1401  1553 I ActivityManager: ProcessObserver broadcast disabled
12-19 23:42:36.258  1401  2245 I ActivityManager: Changes in 10266 19 to 11, 0 to 128
12-19 23:42:36.267  1401  3509 I ActivityManager: Changes in 10266 11 to 19, 128 to 0
12-19 23:42:36.284  1401  1553 I ActivityManager: Start proc 1796:com.android.settings.intelligence/u0a136 for broadcast {com.android.settings.intelligence/com.samsung.android.settings.intelligence.widget.receiver.BatteryWidgetReceiver}
12-19 23:42:36.284  1401  1553 I ActivityManager: ProcessObserver broadcast disabled
12-19 23:42:36.400  1401  1703 I ActivityManager: Changes in 10136 19 to 11, 0 to 128
12-19 23:42:36.466  1401  2245 I ActivityManager: Changes in 10136 11 to 19, 128 to 0
12-19 23:42:36.480  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:36.493  1401  1703 I ActivityManager: Changes in 10136 19 to 8, 0 to 128
12-19 23:42:36.617  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184124458677000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:36.618  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, '908afac', t=1 +(0,-116)
12-19 23:42:36.647  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:36.653  1401  3509 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:36.697  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184124541339000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:36.698  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, '908afac', t=1 +(0,-116)
12-19 23:42:39.720  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:41.669  1401  1703 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:46.810  1401  1703 I ActivityManager: Changes in 10136 8 to 10, 128 to 0
12-19 23:42:48.799  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184136630528000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:48.802  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, '908afac', t=1 +(0,-116)
12-19 23:42:48.842  1401  3547 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:48.842  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:48.869  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184136712786000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:48.869  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, '908afac', t=1 +(0,-116)
12-19 23:42:48.870 28359 28359 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{ffc88d0 VFE...C.. .F.P.... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:537 
12-19 23:42:48.881  1401  3547 I WindowManager: Relayout Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=63x63 ty=2032 d0
12-19 23:42:48.882  1401  3547 D WindowManager: makeSurface duration=0 name=$_28359
12-19 23:42:48.884  1401  3547 I WindowManager: Relayout hash=3f8bbcd, pid=28359, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:42:48.884  1401  3547 I WindowManager:   fl=1040308
12-19 23:42:48.884  1401  3547 I WindowManager:   bhv=1
12-19 23:42:48.884  1401  3547 I WindowManager:   fitTypes=206
12-19 23:42:48.884  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:48.884  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:48.893  1401  3547 D WindowManager: finishDrawingWindow: Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-19 23:42:48.896  1401  1703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xe80ff1a
12-19 23:42:48.896  1401  1703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:42:48.897  1401  1703 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:42:48.897  1401  1703 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xbb9a869
12-19 23:42:48.901  1401  3547 D InputDispatcher: Focus left window (0): 908afac com.katsuyamaki.DroidOSLauncher
12-19 23:42:48.901  1401  1703 D WindowManager: Changing focus from Window{908afac u0 com.katsuyamaki.DroidOSLauncher} to Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity} displayId=0 Callers=com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-19 23:42:48.901  1401  1703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xdfe4155
12-19 23:42:48.901  1401  1703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-19 23:42:48.901  1401  1703 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-19 23:42:48.902  1401  1703 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x8bb6225
12-19 23:42:48.902  1401  1703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x3bf4844
12-19 23:42:48.902  1401  1703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-19 23:42:48.902  1401  1703 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-19 23:42:48.902  1401  1703 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xd4e3ffa
12-19 23:42:48.903  1401  1703 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}, navColorWin=Window{8d8ec89 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 
12-19 23:42:48.903  1401  1703 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-1
12-19 23:42:48.918  1401  3547 D InputDispatcher: Once focus requested (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:42:48.918  1401  3547 D InputDispatcher: Focus entered window (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:42:48.927  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:48.927  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:42:48.927  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-19 23:42:48.928  1401  1763 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-19 23:42:48.933  1401  1763 I ActivityManager: Changes in 10332 7 to 5, 128 to 255
12-19 23:42:48.935  1401  3509 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:42:48.936  1401  1703 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:42:48.937  1401  1763 I InputMethodManagerService: attachNewInputLocked: showCurrentInputInternal, softInputModeState=STATE_UNSPECIFIED|ADJUST_RESIZE
12-19 23:42:48.943  1401  1544 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-19 23:42:48.948  1401  2245 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:48.948  1401  2245 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:42:48.948  1401  3547 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:48.949  1401  2245 I InputMethodManagerService: setImeWindowStatus: vis=3, backDisposition=0
12-19 23:42:48.951  1401  3547 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:48.951  1401  3547 I WindowManager:   fl=14318
12-19 23:42:48.951  1401  3547 I WindowManager:   pfl=14
12-19 23:42:48.951  1401  3547 I WindowManager:   bhv=1
12-19 23:42:48.951  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:48.951  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:48.951  1401  3547 I WindowManager:   sfl=8}
12-19 23:42:48.953  1401  3549 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-19 23:42:48.953  1401  3549 D WindowManager: makeSurface duration=1 name=InputMethod$_15168
12-19 23:42:48.955  1401  3549 I WindowManager: Relayout hash=b217ba3, pid=15168, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:42:48.955  1401  3549 I WindowManager:   fl=81800108
12-19 23:42:48.955  1401  3549 I WindowManager:   pfl=14000000
12-19 23:42:48.955  1401  3549 I WindowManager:   bhv=1
12-19 23:42:48.955  1401  3549 I WindowManager:   fitTypes=3
12-19 23:42:48.955  1401  3549 I WindowManager:   fitSides=7
12-19 23:42:48.955  1401  3549 I WindowManager:   fitIgnoreVis
12-19 23:42:48.955  1401  3549 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:42:48.955  1401  3549 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:42:48.961  1401  3507 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion() -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-19 23:42:48.970  1401  1469 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:48.972  1401  3545 D WindowManager: finishDrawingWindow: Window{b217ba3 u0 InputMethod} mDrawState=DRAW_PENDING seqId=0
12-19 23:42:48.972  1401  1469 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:48.972  1401  1469 I WindowManager:   fl=14318
12-19 23:42:48.972  1401  1469 I WindowManager:   pfl=14
12-19 23:42:48.972  1401  1469 I WindowManager:   bhv=1
12-19 23:42:48.972  1401  1469 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:48.972  1401  1469 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:48.972  1401  1469 I WindowManager:   sfl=8}
12-19 23:42:48.972  1401  1868 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:42:48.973  1401  1868 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:42:48.983  1401  2245 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:48.985  1401  2245 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:48.985  1401  2245 I WindowManager:   fl=14318
12-19 23:42:48.985  1401  2245 I WindowManager:   pfl=14
12-19 23:42:48.985  1401  2245 I WindowManager:   bhv=1
12-19 23:42:48.985  1401  2245 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:48.985  1401  2245 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:48.985  1401  2245 I WindowManager:   sfl=8}
12-19 23:42:48.999  1401  1868 D WindowManager: finishDrawingWindow: Window{b217ba3 u0 InputMethod} mDrawState=HAS_DRAWN seqId=0
12-19 23:42:49.864  1401  3549 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:50.325  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:53.583  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184141419647000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:53.591  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, '3f8bbcd', t=1 +(-50,-200)
12-19 23:42:53.622  1401  3549 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:53.628  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:53.689  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184141532953000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:53.690  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, '3f8bbcd', t=1 +(-50,-200)
12-19 23:42:53.691 28359 28359 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{ffc88d0 VFE...C.. .F...... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerImpl.addView:158 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:546 com.example.quadrantlauncher.FloatingLauncherService.access$toggleDrawer:54 
12-19 23:42:53.692 28359 28359 D ViewRootImpl: desktopMode is false
12-19 23:42:53.692 28359 28359 I ViewRootImpl: dVRR is disabled
12-19 23:42:53.697  1401  2245 D WindowManager: Changing focus from Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity} to Window{7d1c56f u0 com.katsuyamaki.DroidOSLauncher} displayId=0 Callers=com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 com.android.server.wm.Session.addToDisplayAsUser:24 android.view.IWindowSession$Stub.onTransact:757 
12-19 23:42:53.697  1401  2245 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xd4e3ffa
12-19 23:42:53.697  1401  2245 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-19 23:42:53.697  1401  2245 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-19 23:42:53.698  1401  2245 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x285435a
12-19 23:42:53.698  1401  2245 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{7d1c56f u0 com.katsuyamaki.DroidOSLauncher}, navColorWin=Window{b217ba3 u0 InputMethod}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 
12-19 23:42:53.698  1401  2245 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
12-19 23:42:53.724  1401  2245 I WindowManager: Relayout Window{7d1c56f u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=1080x2404 ty=2032 d0
12-19 23:42:53.724  1401  2245 D WindowManager: makeSurface duration=1 name=$_28359
12-19 23:42:53.726  1401  2245 I WindowManager: Relayout hash=7d1c56f, pid=28359, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP START CENTER sim={adjust=nothing} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:42:53.726  1401  2245 I WindowManager:   fl=1000300
12-19 23:42:53.726  1401  2245 I WindowManager:   bhv=1
12-19 23:42:53.726  1401  2245 I WindowManager:   fitTypes=206
12-19 23:42:53.726  1401  2245 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:53.726  1401  2245 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:53.737  1401  3549 D InputDispatcher: Once focus requested (0): 7d1c56f com.katsuyamaki.DroidOSLauncher
12-19 23:42:53.737  1401  3549 D InputDispatcher: Focus request (0): 7d1c56f com.katsuyamaki.DroidOSLauncher but waiting because NO_WINDOW
12-19 23:42:53.737  1401  3549 D InputDispatcher: Focus left window (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:42:53.816  1401  3507 D WindowManager: finishDrawingWindow: Window{7d1c56f u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-19 23:42:53.816  1401  3547 I WindowManager: Relayout Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=8 req=63x63 ty=2032 d0
12-19 23:42:53.816  1401  3547 E WindowManager: win=Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-19 23:42:53.818  1401  3547 I WindowManager: Relayout hash=3f8bbcd, pid=28359, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:42:53.818  1401  3547 I WindowManager:   fl=1040308
12-19 23:42:53.818  1401  3547 I WindowManager:   bhv=1
12-19 23:42:53.818  1401  3547 I WindowManager:   fitTypes=206
12-19 23:42:53.818  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:53.818  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:42:53.838  1401  2168 D InputDispatcher: Focus entered window (0): 7d1c56f com.katsuyamaki.DroidOSLauncher
12-19 23:42:53.871  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:53.871  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:42:53.871  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-19 23:42:53.873  1401  3547 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xbb9a869
12-19 23:42:53.873  1401  3547 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:42:53.873  1401  3547 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:42:53.876  1401  3547 D WindowManager: makeSurface duration=4 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x223147
12-19 23:42:53.878  1401  1545 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x8bb6225
12-19 23:42:53.878  1401  1545 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-19 23:42:53.878  1401  1545 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-19 23:42:53.878  1401  1545 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xb0c2312
12-19 23:42:53.884  1401  1544 I ActivityManager: Changes in 10332 5 to 7, 255 to 128
12-19 23:42:53.888  1401  3547 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:53.896  1401  3547 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:53.896  1401  3547 I WindowManager:   fl=14318
12-19 23:42:53.896  1401  3547 I WindowManager:   pfl=14
12-19 23:42:53.896  1401  3547 I WindowManager:   bhv=1
12-19 23:42:53.896  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:53.896  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:53.896  1401  3547 I WindowManager:   sfl=8}
12-19 23:42:53.926  1401  3074 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:53.939  1401  3074 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:42:53.939  1401  3074 I InputMethodManagerService: setImeWindowStatus: vis=0, backDisposition=0
12-19 23:42:53.950  1401  1703 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-19 23:42:53.953  1401  1703 I WindowManager: Relayout hash=b217ba3, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:42:53.953  1401  1703 I WindowManager:   fl=81800108
12-19 23:42:53.953  1401  1703 I WindowManager:   pfl=14000000
12-19 23:42:53.953  1401  1703 I WindowManager:   bhv=1
12-19 23:42:53.953  1401  1703 I WindowManager:   fitTypes=3
12-19 23:42:53.953  1401  1703 I WindowManager:   fitSides=7
12-19 23:42:53.953  1401  1703 I WindowManager:   fitIgnoreVis
12-19 23:42:53.953  1401  1703 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:42:53.953  1401  1703 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:42:53.954  1401  2245 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-19 23:42:54.457  1401  2168 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=8 req=1080x2404 ty=2011 d0
12-19 23:42:54.457  1401  2168 E WindowManager: win=Window{b217ba3 u0 InputMethod} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-19 23:42:54.459  1401  2168 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x223147
12-19 23:42:54.459  1401  2168 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:42:54.459  1401  2168 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:42:54.459  1401  2168 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xc5026a
12-19 23:42:54.463  1401  2168 I WindowManager: Relayout hash=b217ba3, pid=15168, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:42:54.463  1401  2168 I WindowManager:   fl=81800108
12-19 23:42:54.463  1401  2168 I WindowManager:   pfl=14000000
12-19 23:42:54.463  1401  2168 I WindowManager:   bhv=1
12-19 23:42:54.463  1401  2168 I WindowManager:   fitTypes=3
12-19 23:42:54.463  1401  2168 I WindowManager:   fitSides=7
12-19 23:42:54.463  1401  2168 I WindowManager:   fitIgnoreVis
12-19 23:42:54.463  1401  2168 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:42:54.463  1401  2168 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:42:54.469  1401  2168 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion(), touchableInsets 3 -> 3
12-19 23:42:54.476  1401  2168 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:42:54.477  1401  2168 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:42:54.477  1401  2168 I WindowManager:   fl=14318
12-19 23:42:54.477  1401  2168 I WindowManager:   pfl=14
12-19 23:42:54.477  1401  2168 I WindowManager:   bhv=1
12-19 23:42:54.477  1401  2168 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:42:54.477  1401  2168 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:42:54.477  1401  2168 I WindowManager:   sfl=8}
12-19 23:42:55.153  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:55.410  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184143249539000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:55.412  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, '7d1c56f', t=1 +(0,-116)
12-19 23:42:55.435  1401  3507 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:42:55.436  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:42:55.488  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184143331811000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:42:55.489  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, '7d1c56f', t=1 +(0,-116)
12-19 23:42:58.518  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:43:00.204  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184148044715000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:43:00.206  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, '7d1c56f', t=1 +(0,-116)
12-19 23:43:00.240  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:43:00.247  1401  3507 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:43:00.324  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184148168206000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:43:00.324  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, '7d1c56f', t=1 +(0,-116)
12-19 23:43:00.326 28359 28359 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{ffc88d0 VFE...C.. .F.P.... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:537 
12-19 23:43:00.338  1401  3547 I WindowManager: Relayout Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=63x63 ty=2032 d0
12-19 23:43:00.338  1401  3547 D WindowManager: makeSurface duration=1 name=$_28359
12-19 23:43:00.341  1401  3547 I WindowManager: Relayout hash=3f8bbcd, pid=28359, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:43:00.341  1401  3547 I WindowManager:   fl=1040308
12-19 23:43:00.341  1401  3547 I WindowManager:   bhv=1
12-19 23:43:00.341  1401  3547 I WindowManager:   fitTypes=206
12-19 23:43:00.341  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:00.341  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:43:00.352  1401  3547 D WindowManager: finishDrawingWindow: Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-19 23:43:00.357  1401  2245 D InputDispatcher: Focus left window (0): 7d1c56f com.katsuyamaki.DroidOSLauncher
12-19 23:43:00.357  1401  1469 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xc5026a
12-19 23:43:00.357  1401  1469 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:43:00.358  1401  1469 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:43:00.358  1401  1469 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x59c4b6c
12-19 23:43:00.360  1401  1469 D WindowManager: Changing focus from Window{7d1c56f u0 com.katsuyamaki.DroidOSLauncher} to Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity} displayId=0 Callers=com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-19 23:43:00.360  1401  1469 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xb0c2312
12-19 23:43:00.360  1401  1469 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-19 23:43:00.360  1401  1469 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-19 23:43:00.360  1401  1469 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x9a4cb3b
12-19 23:43:00.360  1401  1469 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x285435a
12-19 23:43:00.360  1401  1469 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-19 23:43:00.360  1401  1469 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-19 23:43:00.361  1401  1469 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xbb8b258
12-19 23:43:00.361  1401  1469 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}, navColorWin=Window{8d8ec89 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 
12-19 23:43:00.361  1401  1469 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-1
12-19 23:43:00.374  1401  1469 D InputDispatcher: Once focus requested (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:43:00.374  1401  1469 D InputDispatcher: Focus entered window (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:43:00.380  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:43:00.380  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:43:00.381  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-19 23:43:00.382  1401  1763 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-19 23:43:00.388  1401  1763 I ActivityManager: Changes in 10332 7 to 5, 128 to 255
12-19 23:43:00.393  1401  3547 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:43:00.394  1401  2168 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:43:00.395  1401  1763 I InputMethodManagerService: attachNewInputLocked: showCurrentInputInternal, softInputModeState=STATE_UNSPECIFIED|ADJUST_RESIZE
12-19 23:43:00.405  1401  1469 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:43:00.405  1401  1469 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:43:00.405  1401  1469 I InputMethodManagerService: setImeWindowStatus: vis=3, backDisposition=0
12-19 23:43:00.410  1401  1544 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-19 23:43:00.411  1401  1703 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-19 23:43:00.411  1401  1703 D WindowManager: makeSurface duration=1 name=InputMethod$_15168
12-19 23:43:00.412  1401  1703 I WindowManager: Relayout hash=b217ba3, pid=15168, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:43:00.412  1401  1703 I WindowManager:   fl=81800108
12-19 23:43:00.412  1401  1703 I WindowManager:   pfl=14000000
12-19 23:43:00.412  1401  1703 I WindowManager:   bhv=1
12-19 23:43:00.412  1401  1703 I WindowManager:   fitTypes=3
12-19 23:43:00.412  1401  1703 I WindowManager:   fitSides=7
12-19 23:43:00.412  1401  1703 I WindowManager:   fitIgnoreVis
12-19 23:43:00.412  1401  1703 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:43:00.412  1401  1703 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:43:00.418  1401  1469 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion() -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-19 23:43:00.422  1401  3547 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:43:00.424  1401  3547 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:43:00.424  1401  3547 I WindowManager:   fl=14318
12-19 23:43:00.424  1401  3547 I WindowManager:   pfl=14
12-19 23:43:00.424  1401  3547 I WindowManager:   bhv=1
12-19 23:43:00.424  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:00.424  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:43:00.424  1401  3547 I WindowManager:   sfl=8}
12-19 23:43:00.426  1401  2168 D WindowManager: finishDrawingWindow: Window{b217ba3 u0 InputMethod} mDrawState=DRAW_PENDING seqId=0
12-19 23:43:00.426  1401  1646 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:43:00.427  1401  3074 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:43:00.431  1401  3547 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:43:00.434  1401  3547 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:43:00.434  1401  3547 I WindowManager:   fl=14318
12-19 23:43:00.434  1401  3547 I WindowManager:   pfl=14
12-19 23:43:00.434  1401  3547 I WindowManager:   bhv=1
12-19 23:43:00.434  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:00.434  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:43:00.434  1401  3547 I WindowManager:   sfl=8}
12-19 23:43:00.438  1401  1703 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:43:00.440  1401  1703 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:43:00.440  1401  1703 I WindowManager:   fl=14318
12-19 23:43:00.440  1401  1703 I WindowManager:   pfl=14
12-19 23:43:00.440  1401  1703 I WindowManager:   bhv=1
12-19 23:43:00.440  1401  1703 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:00.440  1401  1703 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:43:00.440  1401  1703 I WindowManager:   sfl=8}
12-19 23:43:00.446  1401  1703 D WindowManager: finishDrawingWindow: Window{b217ba3 u0 InputMethod} mDrawState=HAS_DRAWN seqId=0
12-19 23:43:01.764  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:43:01.853  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184149692848000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:43:01.855  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, '3f8bbcd', t=1 +(-50,-200)
12-19 23:43:01.879  1401  3547 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:43:01.880  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:43:01.965  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184149808394000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:43:01.965  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, '3f8bbcd', t=1 +(-50,-200)
12-19 23:43:01.967 28359 28359 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{ffc88d0 VFE...C.. .F...... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerImpl.addView:158 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:546 com.example.quadrantlauncher.FloatingLauncherService.access$toggleDrawer:54 
12-19 23:43:01.967 28359 28359 D ViewRootImpl: desktopMode is false
12-19 23:43:01.968 28359 28359 I ViewRootImpl: dVRR is disabled
12-19 23:43:01.975  1401  3074 D WindowManager: Changing focus from Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity} to Window{fdf85ff u0 com.katsuyamaki.DroidOSLauncher} displayId=0 Callers=com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 com.android.server.wm.Session.addToDisplayAsUser:24 android.view.IWindowSession$Stub.onTransact:757 
12-19 23:43:01.975  1401  3074 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xbb8b258
12-19 23:43:01.975  1401  3074 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-19 23:43:01.975  1401  3074 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-19 23:43:01.975  1401  3074 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xcca35f7
12-19 23:43:01.976  1401  3074 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{fdf85ff u0 com.katsuyamaki.DroidOSLauncher}, navColorWin=Window{b217ba3 u0 InputMethod}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 
12-19 23:43:01.976  1401  3074 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
12-19 23:43:01.998  1401  3547 I WindowManager: Relayout Window{fdf85ff u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=1080x2404 ty=2032 d0
12-19 23:43:01.999  1401  3547 D WindowManager: makeSurface duration=0 name=$_28359
12-19 23:43:02.001  1401  3547 I WindowManager: Relayout hash=fdf85ff, pid=28359, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP START CENTER sim={adjust=nothing} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:43:02.001  1401  3547 I WindowManager:   fl=1000300
12-19 23:43:02.001  1401  3547 I WindowManager:   bhv=1
12-19 23:43:02.001  1401  3547 I WindowManager:   fitTypes=206
12-19 23:43:02.001  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:02.001  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:43:02.012  1401  3547 D InputDispatcher: Once focus requested (0): fdf85ff com.katsuyamaki.DroidOSLauncher
12-19 23:43:02.012  1401  3547 D InputDispatcher: Focus request (0): fdf85ff com.katsuyamaki.DroidOSLauncher but waiting because NO_WINDOW
12-19 23:43:02.012  1401  3547 D InputDispatcher: Focus left window (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:43:02.081  1401  1469 D WindowManager: finishDrawingWindow: Window{fdf85ff u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-19 23:43:02.081  1401  3547 I WindowManager: Relayout Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=8 req=63x63 ty=2032 d0
12-19 23:43:02.082  1401  3547 E WindowManager: win=Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-19 23:43:02.084  1401  3547 I WindowManager: Relayout hash=3f8bbcd, pid=28359, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:43:02.084  1401  3547 I WindowManager:   fl=1040308
12-19 23:43:02.084  1401  3547 I WindowManager:   bhv=1
12-19 23:43:02.084  1401  3547 I WindowManager:   fitTypes=206
12-19 23:43:02.084  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:02.084  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:43:02.104  1401  1703 D InputDispatcher: Focus entered window (0): fdf85ff com.katsuyamaki.DroidOSLauncher
12-19 23:43:02.108  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:43:02.108  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:43:02.108  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-19 23:43:02.109  1401  1703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x59c4b6c
12-19 23:43:02.109  1401  1703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:43:02.110  1401  1703 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:43:02.110  1401  1703 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x5c0053d
12-19 23:43:02.111  1401  1545 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x9a4cb3b
12-19 23:43:02.111  1401  1545 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-19 23:43:02.111  1401  1545 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-19 23:43:02.112  1401  1545 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xa69a800
12-19 23:43:02.116  1401  2245 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:43:02.117  1401  1544 I ActivityManager: Changes in 10332 5 to 7, 255 to 128
12-19 23:43:02.118  1401  2245 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:43:02.118  1401  2245 I WindowManager:   fl=14318
12-19 23:43:02.118  1401  2245 I WindowManager:   pfl=14
12-19 23:43:02.118  1401  2245 I WindowManager:   bhv=1
12-19 23:43:02.118  1401  2245 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:02.118  1401  2245 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:43:02.118  1401  2245 I WindowManager:   sfl=8}
12-19 23:43:02.138  1401  1469 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:43:02.138  1401  1469 I InputMethodManagerService: setImeWindowStatus: vis=0, backDisposition=0
12-19 23:43:02.150  1401  2168 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-19 23:43:02.153  1401  2168 I WindowManager: Relayout hash=b217ba3, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:43:02.153  1401  2168 I WindowManager:   fl=81800108
12-19 23:43:02.153  1401  2168 I WindowManager:   pfl=14000000
12-19 23:43:02.153  1401  2168 I WindowManager:   bhv=1
12-19 23:43:02.153  1401  2168 I WindowManager:   fitTypes=3
12-19 23:43:02.153  1401  2168 I WindowManager:   fitSides=7
12-19 23:43:02.153  1401  2168 I WindowManager:   fitIgnoreVis
12-19 23:43:02.153  1401  2168 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:43:02.153  1401  2168 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:43:02.154  1401  2168 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-19 23:43:02.178  1401  3549 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:43:02.659  1401  3549 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=8 req=1080x2404 ty=2011 d0
12-19 23:43:02.660  1401  3549 E WindowManager: win=Window{b217ba3 u0 InputMethod} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-19 23:43:02.661  1401  3549 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x5c0053d
12-19 23:43:02.661  1401  3549 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:43:02.661  1401  3549 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:43:02.662  1401  3549 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xf155f56
12-19 23:43:02.665  1401  3549 I WindowManager: Relayout hash=b217ba3, pid=15168, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:43:02.665  1401  3549 I WindowManager:   fl=81800108
12-19 23:43:02.665  1401  3549 I WindowManager:   pfl=14000000
12-19 23:43:02.665  1401  3549 I WindowManager:   bhv=1
12-19 23:43:02.665  1401  3549 I WindowManager:   fitTypes=3
12-19 23:43:02.665  1401  3549 I WindowManager:   fitSides=7
12-19 23:43:02.665  1401  3549 I WindowManager:   fitIgnoreVis
12-19 23:43:02.665  1401  3549 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:43:02.665  1401  3549 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:43:02.666  1401  3549 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:43:02.668  1401  3549 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:43:02.668  1401  3549 I WindowManager:   fl=14318
12-19 23:43:02.668  1401  3549 I WindowManager:   pfl=14
12-19 23:43:02.668  1401  3549 I WindowManager:   bhv=1
12-19 23:43:02.668  1401  3549 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:02.668  1401  3549 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:43:02.668  1401  3549 I WindowManager:   sfl=8}
12-19 23:43:02.672  1401  2495 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion(), touchableInsets 3 -> 3
12-19 23:43:02.685  1401  2168 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:43:02.686  1401  2168 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:43:02.686  1401  2168 I WindowManager:   fl=14318
12-19 23:43:02.686  1401  2168 I WindowManager:   pfl=14
12-19 23:43:02.686  1401  2168 I WindowManager:   bhv=1
12-19 23:43:02.686  1401  2168 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:02.686  1401  2168 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:43:02.686  1401  2168 I WindowManager:   sfl=8}
12-19 23:43:03.562  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:43:03.810  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184151651102000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:43:03.811  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, 'fdf85ff', t=1 +(0,-116)
12-19 23:43:03.843  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:43:03.850  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184151690056000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:43:03.850  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, 'fdf85ff', t=1 +(0,-116)
12-19 23:43:03.853  1401  2168 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:43:05.776  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184153615571000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:43:05.777  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x0, f=0x0, d=0, 'fdf85ff', t=1 +(0,-116)
12-19 23:43:05.790  1401  3507 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:43:05.837  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184153680126000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:43:05.838  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x1, f=0x0, d=0, 'fdf85ff', t=1 +(0,-116)
12-19 23:43:05.841 28359 28359 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{ffc88d0 VFE...C.. .F.P.... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:537 
12-19 23:43:05.850  1401  3507 I WindowManager: Relayout Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=63x63 ty=2032 d0
12-19 23:43:05.851  1401  3507 D WindowManager: makeSurface duration=0 name=$_28359
12-19 23:43:05.853  1401  3507 I WindowManager: Relayout hash=3f8bbcd, pid=28359, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-19 23:43:05.853  1401  3507 I WindowManager:   fl=1040308
12-19 23:43:05.853  1401  3507 I WindowManager:   bhv=1
12-19 23:43:05.853  1401  3507 I WindowManager:   fitTypes=206
12-19 23:43:05.853  1401  3507 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:05.853  1401  3507 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-19 23:43:05.862  1401  3509 D WindowManager: finishDrawingWindow: Window{3f8bbcd u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-19 23:43:05.869  1401  2168 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0xf155f56
12-19 23:43:05.869  1401  2168 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8, destroy=false, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b
12-19 23:43:05.869  1401  2168 I WindowManager: Reparenting to leash, surface=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b, leashParent=Surface(name=WindowToken{c321922 type=2011 android.os.Binder@edfc6ed})/@0x7a81b8
12-19 23:43:05.869  1401  2168 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=b217ba3 InputMethod)/@0xfd8bc1b - animation-leash of insets_animation)/@0x9f293de
12-19 23:43:05.874  1401  2168 D WindowManager: Changing focus from Window{fdf85ff u0 com.katsuyamaki.DroidOSLauncher} to Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity} displayId=0 Callers=com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-19 23:43:05.874  1401  2168 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xa69a800
12-19 23:43:05.874  1401  2168 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-19 23:43:05.874  1401  2168 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-19 23:43:05.874  1401  2168 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x2100fea
12-19 23:43:05.875  1401  2168 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xcca35f7
12-19 23:43:05.875  1401  2168 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-19 23:43:05.875  1401  2168 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-19 23:43:05.875  1401  2168 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xeac42db
12-19 23:43:05.875  1401  2168 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{dea7911 u0 com.termux/com.termux.app.TermuxActivity}, navColorWin=Window{8d8ec89 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 
12-19 23:43:05.876  1401  2168 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-1
12-19 23:43:05.878  1401  3507 W WindowManager: Failed looking up window session=Session{dbe50cc 28359:u0a10516} callers=com.android.server.wm.WindowManagerService.windowForClientLocked:1 com.android.server.wm.Session.setOnBackInvokedCallbackInfo:15 android.view.IWindowSession$Stub.onTransact:1390 
12-19 23:43:05.878  1401  3507 I WindowManager: setOnBackInvokedCallback(): No window state for package:com.katsuyamaki.DroidOSLauncher
12-19 23:43:05.880  1401  3549 D InputDispatcher: Focus left window (0): fdf85ff com.katsuyamaki.DroidOSLauncher
12-19 23:43:05.895  1401  3507 D InputDispatcher: Once focus requested (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:43:05.895  1401  3507 D InputDispatcher: Focus entered window (0): dea7911 com.termux/com.termux.app.TermuxActivity
12-19 23:43:05.902  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:43:05.902  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:43:05.903  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-19 23:43:05.903  1401  1763 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-19 23:43:05.909  1401  1763 I ActivityManager: Changes in 10332 7 to 5, 128 to 255
12-19 23:43:05.911  1401  1703 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:43:05.912  1401  2495 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:43:05.916  1401  1763 I InputMethodManagerService: attachNewInputLocked: showCurrentInputInternal, softInputModeState=STATE_UNSPECIFIED|ADJUST_RESIZE
12-19 23:43:05.924  1401  2168 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-19 23:43:05.928  1401  2168 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-19 23:43:05.928  1401  2168 I InputMethodManagerService: setImeWindowStatus: vis=3, backDisposition=0
12-19 23:43:05.931  1401  2495 I WindowManager: Relayout Window{b217ba3 u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-19 23:43:05.932  1401  2495 D WindowManager: makeSurface duration=0 name=InputMethod$_15168
12-19 23:43:05.933  1401  3509 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:43:05.933  1401  2495 I WindowManager: Relayout hash=b217ba3, pid=15168, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-19 23:43:05.933  1401  2495 I WindowManager:   fl=81800108
12-19 23:43:05.933  1401  2495 I WindowManager:   pfl=14000000
12-19 23:43:05.933  1401  2495 I WindowManager:   bhv=1
12-19 23:43:05.933  1401  2495 I WindowManager:   fitTypes=3
12-19 23:43:05.933  1401  2495 I WindowManager:   fitSides=7
12-19 23:43:05.933  1401  2495 I WindowManager:   fitIgnoreVis
12-19 23:43:05.933  1401  2495 I WindowManager:   dvrrWindowFrameRateHint=true
12-19 23:43:05.933  1401  2495 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-19 23:43:05.934  1401  3509 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:43:05.934  1401  3509 I WindowManager:   fl=14318
12-19 23:43:05.934  1401  3509 I WindowManager:   pfl=14
12-19 23:43:05.934  1401  3509 I WindowManager:   bhv=1
12-19 23:43:05.934  1401  3509 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:05.934  1401  3509 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:43:05.934  1401  3509 I WindowManager:   sfl=8}
12-19 23:43:05.936  1401  1544 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-19 23:43:05.938  1401  2495 D WindowManager: setInsetsWindow Window{b217ba3 u0 InputMethod}, contentInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion() -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-19 23:43:05.948  1401  2245 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:43:05.948  1401  3507 D WindowManager: finishDrawingWindow: Window{b217ba3 u0 InputMethod} mDrawState=DRAW_PENDING seqId=0
12-19 23:43:05.949  1401  2168 I InputMethodManagerService: isAccessoryKeyboard 0
12-19 23:43:05.960  1401  3547 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:43:05.963  1401  3547 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:43:05.963  1401  3547 I WindowManager:   fl=14318
12-19 23:43:05.963  1401  3547 I WindowManager:   pfl=14
12-19 23:43:05.963  1401  3547 I WindowManager:   bhv=1
12-19 23:43:05.963  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:05.963  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:43:05.963  1401  3547 I WindowManager:   sfl=8}
12-19 23:43:05.966  1401  3547 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-19 23:43:05.968  1401  3547 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-19 23:43:05.968  1401  3547 I WindowManager:   fl=14318
12-19 23:43:05.968  1401  3547 I WindowManager:   pfl=14
12-19 23:43:05.968  1401  3547 I WindowManager:   bhv=1
12-19 23:43:05.968  1401  3547 I WindowManager:   frameRateBoostOnTouch=true
12-19 23:43:05.968  1401  3547 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-19 23:43:05.968  1401  3547 I WindowManager:   sfl=8}
12-19 23:43:05.974  1401  3547 D WindowManager: finishDrawingWindow: Window{b217ba3 u0 InputMethod} mDrawState=HAS_DRAWN seqId=0
12-19 23:43:07.167  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:43:08.197  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184156037598000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:43:08.198  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x4, f=0x0, d=0, '3f8bbcd', t=1 
12-19 23:43:08.198  1401  1700 I InputDispatcher: Delivering touch to (2045): action: 0x4, f=0x0, d=0, '253deac', t=1 
12-19 23:43:08.199  1401  1700 I InputDispatcher: Delivering touch to (13489): action: 0x0, f=0x0, d=0, 'dea7911', t=1 +(0,-116)
12-19 23:43:08.232  1401  3507 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=18195)/@0xa075cc1} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-19 23:43:08.232  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-19 23:43:08.276  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=184156119064000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:43:08.276  1401  1700 I InputDispatcher: Delivering touch to (13489): action: 0x1, f=0x0, d=0, 'dea7911', t=1 +(0,-116)
12-19 23:43:09.002  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=184156843530000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-19 23:43:09.004  1401  1700 I InputDispatcher: Delivering touch to (28359): action: 0x4, f=0x0, d=0, '3f8bbcd', t=1 
12-19 23:43:09.004  1401  1700 I InputDispatcher: Delivering touch to (2045): action: 0x4, f=0x0, d=0, '253deac', t=1 
12-19 23:43:09.004  1401  1700 I InputDispatcher: Delivering touch to (15168): action: 0x0, f=0x0, d=0, 'b217ba3', t=1 +(0,-116)
