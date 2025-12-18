--------- beginning of main
12-18 20:47:19.139  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=127041347119000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:19.140  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x4, f=0x0, d=0, 'c422cb7', t=1 
12-18 20:47:19.141  1401  1700 I InputDispatcher: Delivering touch to (2045): action: 0x0, f=0x0, d=0, '253deac', t=1 +(0,-2445)
--------- beginning of system
12-18 20:47:19.188  1401  1542 D WindowManager: requestTransientBars: swipeTarget=Window{253deac u0 NavigationBar0}, controlTarget=PermanentControlTarget, canShowTransient=false, restorePositionTypes=0x3, from=com.android.server.wm.DisplayPolicy$1.onSwipeFromBottom:21 com.android.server.wm.SystemGesturesPointerEventListener.onPointerEvent:545 
12-18 20:47:19.191  1401  2773 I InputDispatcher: Channel [Gesture Monitor] swipe-up is stealing input gesture for device 3 from [253deac NavigationBar0, [Gesture Monitor] clo, [Gesture Monitor] secinputdev, [Gesture Monitor] PalmMotion, [Gesture Monitor] edge-swipe]
12-18 20:47:19.192  1401  2773 D InputDispatcher: Cancel for '253deac': input channel stole pointer stream, m=1
12-18 20:47:19.192  1401  2773 I InputDispatcher: Delivering touch to (2045): action: 0x3, f=0x20, d=0, '253deac', t=1 +(0,-2445)
12-18 20:47:19.204  1401  2151 I WindowManager: Reparenting to leash, surface=Surface(name=RemoteWallpaperAnim:1:1)/@0xd604b25, leashParent=Surface(name=OneHanded:0:14)/@0xc8374fa
12-18 20:47:19.206  1401  2151 D WindowManager: makeSurface duration=2 leash=Surface(name=Surface(name=RemoteWallpaperAnim:1:1)/@0xd604b25 - animation-leash of remote_wallpaper)/@0x39f8956
12-18 20:47:19.206  1401  2151 D WindowManager: startAnimation for remote wallpaper, leash=Surface(name=Surface(name=RemoteWallpaperAnim:1:1)/@0xd604b25 - animation-leash of remote_wallpaper)/@0x39f8956
12-18 20:47:19.207  1401  2151 D WindowManager: startRemoteWallpaperAnimation, d=0, leash=Surface(name=Surface(name=RemoteWallpaperAnim:1:1)/@0xd604b25 - animation-leash of remote_wallpaper)/@0x39f8956
12-18 20:47:19.207  1401  2591 D WindowManager: Transition is created, t=TransitionRecord{d5526c4 id=-1 type=TO_FRONT flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.WindowOrganizerController.startTransition:18 com.android.server.wm.WindowOrganizerController.startNewTransition:2 android.window.IWindowOrganizerController$Stub.onTransact:247 com.android.server.wm.WindowOrganizerController.onTransact:1 
12-18 20:47:19.209  1401  1544 I ActivityManager: Changes in 10155 5 to 2, 184 to 255
12-18 20:47:19.210  1401  2591 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{225d09b u0 RecentsTransitionOverlay}
12-18 20:47:19.211  1401  1551 W ActivityManager: Rescheduling restart of crashed service app.revanced.android.gms/org.microg.gms.gcm.McsService in 7029509ms for mem-pressure-event
12-18 20:47:19.213  1401  2591 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}
12-18 20:47:19.213  1401  2591 D WindowManager: rotationForOrientation, orientationSource=ActivityRecord{26066939 u0 com.sec.android.app.launcher/.activities.LauncherActivity t17646}
12-18 20:47:19.214  1401  2591 I WindowManager: Reparenting to leash, surface=Surface(name=1aa7434 FreeformContainer)/@0x4f2b598, leashParent=Surface(name=WindowToken{b8c572b type=2604 android.os.BinderProxy@f0783a5})/@0xa3bd1f1
12-18 20:47:19.215  1401  2591 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=1aa7434 FreeformContainer)/@0x4f2b598 - animation-leash of window_animation)/@0x7dfeb30
12-18 20:47:19.224  1401  3074 I ActivityManager: Changes in 10395 19 to 3, 0 to 144
12-18 20:47:19.226  1401  3545 I WindowManager: Relayout Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}: viewVisibility=0 req=1080x2520 ty=1 d0
12-18 20:47:19.227  1401  3545 D WindowManager: makeSurface duration=0 name=com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity$_2738
12-18 20:47:19.229  1401  3545 I WindowManager: Relayout hash=3dc2dd0, pid=2738, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=nothing forwardNavigation} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT wanim=0x1030306
12-18 20:47:19.229  1401  3545 I WindowManager:   fl=81910100
12-18 20:47:19.229  1401  3545 I WindowManager:   pfl=10008840
12-18 20:47:19.229  1401  3545 I WindowManager:   bhv=2
12-18 20:47:19.229  1401  3545 I WindowManager:   fitSides=0
12-18 20:47:19.229  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:19.229  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=200000 naviIconColor=0}
12-18 20:47:19.232  1401  1544 D WindowManager: mRunner#onAnimationStart for remote wallpaper=[android.view.RemoteAnimationTarget@5108a5c], transaction=startRemoteWallpaperAnimation_88726999
12-18 20:47:19.238  1401  2724 D WindowManager: finishDrawingWindow: Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:19.240  1401  1545 D WindowManager: setActiveRecents, recents=Task{e6ea632 #1 type=home}, task=Task{57ae0b9 #17882 type=standard A=10342:com.termux}, caller=com.android.server.wm.InputMonitor.setActiveRecents:33 com.android.server.wm.Transition.onTransactionReady:1748 com.android.server.wm.BLASTSyncEngine$SyncGroup.finishNow:240 
12-18 20:47:19.248  1401  2724 D InputDispatcher: Focused application(0): ActivityRecord{26066939 u0 com.sec.android.app.launcher/.activities.LauncherActivity t17646}
12-18 20:47:19.248  1401  2724 D WindowManager: Changing focus from Window{1230479 u0 com.termux/com.termux.app.TermuxActivity} to Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} displayId=0 Callers=com.android.server.wm.ActivityTaskManagerService.setLastResumedActivityUncheckLocked:264 com.android.server.wm.ActivityTaskManagerService.setFocusedTask:20 com.android.server.wm.ActivityTaskManagerService.focusTopTask:53 android.app.IActivityTaskManager$Stub.onTransact:2027 
12-18 20:47:19.248  1401  2724 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x7f6b943
12-18 20:47:19.248  1401  2724 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-18 20:47:19.249  1401  2724 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-18 20:47:19.249  1401  2724 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x37dfb78
12-18 20:47:19.249  1401  2724 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, navColorWin=Window{897ad1d u0 InputMethod}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.ActivityTaskManagerService.setLastResumedActivityUncheckLocked:264 com.android.server.wm.ActivityTaskManagerService.setFocusedTask:20 
12-18 20:47:19.249  1401  2724 D WindowManager: updateSystemBarAttributes, bhv=2, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
12-18 20:47:19.251  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:19.253  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:19.254  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=127041463757000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:19.263  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:19.269  1401  2724 D InputDispatcher: Once focus requested (0): recents_animation_input_consumer
12-18 20:47:19.269  1401  2724 D InputDispatcher: Focus left window (0): 1230479 com.termux/com.termux.app.TermuxActivity
12-18 20:47:19.269  1401  2724 D InputDispatcher: Focus entered window (0): recents_animation_input_consumer
12-18 20:47:19.416  1401  1545 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{b8c572b type=2604 android.os.BinderProxy@f0783a5})/@0xa3bd1f1, destroy=true, surface=Surface(name=1aa7434 FreeformContainer)/@0x4f2b598
12-18 20:47:19.426  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:19.486  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:19.818  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:20.053  1401  2724 D WindowManager: setActiveRecents, recents=null, task=null, caller=com.android.server.wm.InputMonitor.setActiveRecents:33 com.android.server.wm.TransitionController.finishTransition:1670 com.android.server.wm.WindowOrganizerController.finishTransitionInner:31 
12-18 20:47:20.054  1401  2589 I WindowManager: Relayout Window{225d09b u0 RecentsTransitionOverlay}: viewVisibility=0 req=1x1 ty=2632 d0
12-18 20:47:20.055  1401  3074 D InputDispatcher: Focus left window (0): recents_animation_input_consumer
12-18 20:47:20.056  1401  2589 I WindowManager: Relayout hash=225d09b, pid=0, syncId=-1: mAttrs={(0,0)(1x1) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=TRANSIENT_LAUNCH_OVERLAY fmt=TRANSLUCENT if=INPUT_FEATURE_NO_INPUT_CHANNEL
12-18 20:47:20.056  1401  2589 I WindowManager:   fl=118
12-18 20:47:20.056  1401  2589 I WindowManager:   pfl=10000040
12-18 20:47:20.056  1401  2589 I WindowManager:   bhv=1
12-18 20:47:20.056  1401  2589 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:20.056  1401  2589 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-18 20:47:20.056  1401  2589 I WindowManager:   sfl=4020000}
12-18 20:47:20.058  1401  1544 I ActivityManager: Changes in 10342 2 to 4, 255 to 254
12-18 20:47:20.065  1401  2773 I WindowManager: Relayout Window{1230479 u0 com.termux/com.termux.app.TermuxActivity}: viewVisibility=8 req=1080x2520 ty=1 d0
12-18 20:47:20.067  1401  2773 I WindowManager: Relayout hash=1230479, pid=13489, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} ty=BASE_APPLICATION wanim=0x1030317
12-18 20:47:20.067  1401  2773 I WindowManager:   fl=8d810100
12-18 20:47:20.067  1401  2773 I WindowManager:   pfl=10008040
12-18 20:47:20.067  1401  2773 I WindowManager:   vsysui=700
12-18 20:47:20.067  1401  2773 I WindowManager:   bhv=1
12-18 20:47:20.067  1401  2773 I WindowManager:   fitSides=0
12-18 20:47:20.067  1401  2773 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:20.067  1401  2773 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:20.068  1401  3545 D InputDispatcher: Once focus requested (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:20.068  1401  3545 D InputDispatcher: Focus entered window (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:20.071  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:20.071  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-18 20:47:20.071  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-18 20:47:20.073  1401  2151 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x2fbca97
12-18 20:47:20.073  1401  2151 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960, destroy=false, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963
12-18 20:47:20.073  1401  2151 I WindowManager: Reparenting to leash, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963, leashParent=Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960
12-18 20:47:20.073  1401  2151 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x9cfaec0
12-18 20:47:20.075  1401  1545 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x3fc1e52
12-18 20:47:20.075  1401  1545 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-18 20:47:20.075  1401  1545 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-18 20:47:20.075  1401  1545 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xeefb59f
12-18 20:47:20.081  1401  1544 I ActivityManager: Changes in 10332 5 to 7, 255 to 128
12-18 20:47:20.083  1401  2773 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-18 20:47:20.084  1401  2773 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-18 20:47:20.084  1401  2773 I WindowManager:   fl=14318
12-18 20:47:20.084  1401  2773 I WindowManager:   pfl=14
12-18 20:47:20.084  1401  2773 I WindowManager:   bhv=1
12-18 20:47:20.084  1401  2773 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:20.084  1401  2773 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-18 20:47:20.084  1401  2773 I WindowManager:   sfl=8}
12-18 20:47:20.085  1401  2151 E WindowManager: win=Window{1230479 u0 com.termux/com.termux.app.TermuxActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:192 com.android.server.wm.ActivityClientController.activityStopped:97 android.app.IActivityClientController$Stub.onTransact:726 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1462 android.os.Binder.execTransact:1401 
12-18 20:47:20.087  1401  3074 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:20.093  1401  3545 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:20.093  1401  3545 I InputMethodManagerService: setImeWindowStatus: vis=0, backDisposition=0
12-18 20:47:20.099  1401  1645 I WindowManager: Relayout Window{897ad1d u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-18 20:47:20.100  1401  1645 I WindowManager: Relayout hash=897ad1d, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-18 20:47:20.100  1401  1645 I WindowManager:   fl=81800108
12-18 20:47:20.100  1401  1645 I WindowManager:   pfl=14000000
12-18 20:47:20.100  1401  1645 I WindowManager:   bhv=1
12-18 20:47:20.100  1401  1645 I WindowManager:   fitTypes=3
12-18 20:47:20.100  1401  1645 I WindowManager:   fitSides=7
12-18 20:47:20.100  1401  1645 I WindowManager:   fitIgnoreVis
12-18 20:47:20.100  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true
12-18 20:47:20.100  1401  1645 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-18 20:47:20.101  1401  1645 D WindowManager: setInsetsWindow Window{897ad1d u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-18 20:47:20.466  1401  3703 I WindowManager: Cancelling animation restarting=false, leash=Surface(name=Surface(name=RemoteWallpaperAnim:1:1)/@0xd604b25 - animation-leash of remote_wallpaper)/@0x39f8956
12-18 20:47:20.466  1401  3703 I WindowManager: Reparenting to original parent: Surface(name=OneHanded:0:14)/@0xc8374fa, destroy=false, surface=Surface(name=RemoteWallpaperAnim:1:1)/@0xd604b25
12-18 20:47:20.468  1401  3703 D WindowManager: mRunner#onAnimationCancelled, leash=Surface(name=Surface(name=RemoteWallpaperAnim:1:1)/@0xd604b25 - animation-leash of remote_wallpaper)/@0x39f8956, caller=com.android.server.wm.SurfaceAnimator.cancelAnimation:14 com.android.server.wm.SurfaceAnimator.cancelAnimation:1 com.android.server.wm.WindowContainer.cancelAnimation:3 com.android.server.wm.WindowManagerService.finishRemoteWallpaperAnimation:81 android.view.IWindowManager$Stub.onTransact:4321 
12-18 20:47:20.468  1401  3703 D WindowManager: finishRemoteWallpaperAnimation, success=true
12-18 20:47:20.554  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=127042762635000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:20.554  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x4, f=0x0, d=0, 'c422cb7', t=1 
12-18 20:47:20.554  1401  1700 I InputDispatcher: Delivering touch to (1570): action: 0x0, f=0x0, d=0, 'ab1260a', t=1 +(-90,-1169)
12-18 20:47:20.601  1401  2589 I WindowManager: Relayout Window{897ad1d u0 InputMethod}: viewVisibility=8 req=1080x2404 ty=2011 d0
12-18 20:47:20.601  1401  2589 E WindowManager: win=Window{897ad1d u0 InputMethod} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1462 android.os.Binder.execTransact:1401 
12-18 20:47:20.602  1401  2589 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x9cfaec0
12-18 20:47:20.602  1401  2589 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960, destroy=false, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963
12-18 20:47:20.602  1401  2589 I WindowManager: Reparenting to leash, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963, leashParent=Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960
12-18 20:47:20.602  1401  2589 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x5f98d20
12-18 20:47:20.604  1401  3703 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-18 20:47:20.605  1401  2589 I WindowManager: Relayout hash=897ad1d, pid=19314, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-18 20:47:20.605  1401  2589 I WindowManager:   fl=81800108
12-18 20:47:20.605  1401  2589 I WindowManager:   pfl=14000000
12-18 20:47:20.605  1401  2589 I WindowManager:   bhv=1
12-18 20:47:20.605  1401  2589 I WindowManager:   fitTypes=3
12-18 20:47:20.605  1401  2589 I WindowManager:   fitSides=7
12-18 20:47:20.605  1401  2589 I WindowManager:   fitIgnoreVis
12-18 20:47:20.605  1401  2589 I WindowManager:   dvrrWindowFrameRateHint=true
12-18 20:47:20.605  1401  2589 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-18 20:47:20.606  1401  3703 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-18 20:47:20.606  1401  3703 I WindowManager:   fl=14318
12-18 20:47:20.606  1401  3703 I WindowManager:   pfl=14
12-18 20:47:20.606  1401  3703 I WindowManager:   bhv=1
12-18 20:47:20.606  1401  3703 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:20.606  1401  3703 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-18 20:47:20.606  1401  3703 I WindowManager:   sfl=8}
12-18 20:47:20.606  1401  2589 D WindowManager: setInsetsWindow Window{897ad1d u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion(), touchableInsets 3 -> 3
12-18 20:47:20.609  1401  3703 I WindowManager: Relayout Window{5b7421b u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-18 20:47:20.610  1401  3703 I WindowManager: Relayout hash=5b7421b, pid=2045, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-18 20:47:20.610  1401  3703 I WindowManager:   fl=14318
12-18 20:47:20.610  1401  3703 I WindowManager:   pfl=14
12-18 20:47:20.610  1401  3703 I WindowManager:   bhv=1
12-18 20:47:20.610  1401  3703 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:20.610  1401  3703 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-18 20:47:20.610  1401  3703 I WindowManager:   sfl=8}
12-18 20:47:20.619  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=127042829075000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:20.619  1401  1700 I InputDispatcher: Delivering touch to (1570): action: 0x1, f=0x0, d=0, 'ab1260a', t=1 +(-90,-1169)
12-18 20:47:20.642  1570  1570 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{19a679b V.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.KeyboardOverlay.createKeyboardWindow:434 com.example.coverscreentester.KeyboardOverlay.show:319 
12-18 20:47:20.642  1570  1570 D ViewRootImpl: desktopMode is false
12-18 20:47:20.642  1570  1570 I ViewRootImpl: dVRR is disabled
12-18 20:47:20.646  1570  1570 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{c284cd6 VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1539 
12-18 20:47:20.647  1570  1570 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{c284cd6 VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1539 com.example.coverscreentester.OverlayService.toggleCustomKeyboard:1244 
12-18 20:47:20.650  1570  1570 D ViewRootImpl: desktopMode is false
12-18 20:47:20.650  1570  1570 I ViewRootImpl: dVRR is disabled
12-18 20:47:20.654  1570  1570 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{4346062 V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1540 
12-18 20:47:20.656  1570  1570 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{4346062 V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1540 com.example.coverscreentester.OverlayService.toggleCustomKeyboard:1244 
12-18 20:47:20.658  1570  1570 D ViewRootImpl: desktopMode is false
12-18 20:47:20.658  1570  1570 I ViewRootImpl: dVRR is disabled
12-18 20:47:20.662  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:20.662  1401  1645 D WindowManager: makeSurface duration=0 name=$_1570
12-18 20:47:20.663  1401  1645 I WindowManager: Relayout hash=75c9765, pid=1570, syncId=-1: mAttrs={(405,1662)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:20.663  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:20.663  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:20.663  1401  1645 I WindowManager:   bhv=1
12-18 20:47:20.663  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:20.663  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:20.663  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:20.668  1401  1645 D WindowManager: finishDrawingWindow: Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:20.676  1401  2589 I WindowManager: Relayout Window{d8bb811 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=972x564 ty=2032 d0
12-18 20:47:20.676  1401  2589 D WindowManager: makeSurface duration=0 name=$_1570
12-18 20:47:20.677  1401  2589 I WindowManager: Relayout hash=d8bb811, pid=1570, syncId=-1: mAttrs={(37,1930)(972xwrap) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:20.677  1401  2589 I WindowManager:   fl=1000208
12-18 20:47:20.677  1401  2589 I WindowManager:   pfl=40000000
12-18 20:47:20.677  1401  2589 I WindowManager:   bhv=1
12-18 20:47:20.677  1401  2589 I WindowManager:   fitTypes=207
12-18 20:47:20.677  1401  2589 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:20.677  1401  2589 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:20.693  1401  2589 D WindowManager: finishDrawingWindow: Window{d8bb811 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:20.694  1401  3703 I WindowManager: Relayout Window{312f7c u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=93x93 ty=2032 d0
12-18 20:47:20.695  1401  3703 D WindowManager: makeSurface duration=1 name=$_1570
12-18 20:47:20.696  1401  3703 I WindowManager: Relayout hash=312f7c, pid=1570, syncId=-1: mAttrs={(90,1169)(93x93) gr=TOP START CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:20.696  1401  3703 I WindowManager:   fl=1000308
12-18 20:47:20.696  1401  3703 I WindowManager:   bhv=1
12-18 20:47:20.696  1401  3703 I WindowManager:   fitTypes=206
12-18 20:47:20.696  1401  3703 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:20.696  1401  3703 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:20.700  1401  2589 D WindowManager: finishDrawingWindow: Window{312f7c u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:20.701  1401  1645 I WindowManager: Relayout Window{67b0003 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=50x50 ty=2032 d0
12-18 20:47:20.702  1401  1645 D WindowManager: makeSurface duration=0 name=$_1570
12-18 20:47:20.703  1401  1645 I WindowManager: Relayout hash=67b0003, pid=1570, syncId=-1: mAttrs={(540,1260)(wrapxwrap) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:20.703  1401  1645 I WindowManager:   fl=1000318
12-18 20:47:20.703  1401  1645 I WindowManager:   bhv=1
12-18 20:47:20.703  1401  1645 I WindowManager:   fitTypes=206
12-18 20:47:20.703  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:20.703  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:20.707  1401  1645 D WindowManager: finishDrawingWindow: Window{67b0003 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:20.707  1570  1570 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{c284cd6 VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1539 
12-18 20:47:20.708  1570  1570 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{c284cd6 VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1539 com.example.coverscreentester.OverlayService.setupBubble$lambda$25$lambda$24:848 
12-18 20:47:20.711  1570  1570 D ViewRootImpl: desktopMode is false
12-18 20:47:20.711  1570  1570 I ViewRootImpl: dVRR is disabled
12-18 20:47:20.714  1570  1570 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{4346062 V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1540 
12-18 20:47:20.715  1570  1570 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{4346062 V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1540 com.example.coverscreentester.OverlayService.setupBubble$lambda$25$lambda$24:848 
12-18 20:47:20.717  1570  1570 D ViewRootImpl: desktopMode is false
12-18 20:47:20.717  1570  1570 I ViewRootImpl: dVRR is disabled
12-18 20:47:20.724  1401  3703 D WindowManager: finishDrawingWindow: Window{d8bb811 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=HAS_DRAWN seqId=0
12-18 20:47:20.725  1401  2589 I WindowManager: Relayout Window{d1c6d2d u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=93x93 ty=2032 d0
12-18 20:47:20.726  1401  2589 D WindowManager: makeSurface duration=1 name=$_1570
12-18 20:47:20.727  1401  2589 I WindowManager: Relayout hash=d1c6d2d, pid=1570, syncId=-1: mAttrs={(90,1169)(93x93) gr=TOP START CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:20.727  1401  2589 I WindowManager:   fl=1000308
12-18 20:47:20.727  1401  2589 I WindowManager:   bhv=1
12-18 20:47:20.727  1401  2589 I WindowManager:   fitTypes=206
12-18 20:47:20.727  1401  2589 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:20.727  1401  2589 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:20.734  1401  2589 D WindowManager: finishDrawingWindow: Window{d1c6d2d u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:20.736  1401  1645 I WindowManager: Relayout Window{17319c8 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=50x50 ty=2032 d0
12-18 20:47:20.736  1401  1645 D WindowManager: makeSurface duration=0 name=$_1570
12-18 20:47:20.738  1401  1645 I WindowManager: Relayout hash=17319c8, pid=1570, syncId=-1: mAttrs={(540,1260)(wrapxwrap) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:20.738  1401  1645 I WindowManager:   fl=1000318
12-18 20:47:20.738  1401  1645 I WindowManager:   bhv=1
12-18 20:47:20.738  1401  1645 I WindowManager:   fitTypes=206
12-18 20:47:20.738  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:20.738  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:20.744  1401  1645 D WindowManager: finishDrawingWindow: Window{17319c8 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:20.749  1401  1645 D WindowManager: finishDrawingWindow: Window{17319c8 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=HAS_DRAWN seqId=0
12-18 20:47:21.427  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=127043631069000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:21.429  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x0, f=0x0, d=0, 'c422cb7', t=1 +(-50,-200)
12-18 20:47:21.486  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=127043695242000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:21.487  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x1, f=0x0, d=0, 'c422cb7', t=1 +(-50,-200)
12-18 20:47:21.493 15402 15402 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{2e961fc VFE...C.. .F...... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerImpl.addView:158 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:511 com.example.quadrantlauncher.FloatingLauncherService.access$toggleDrawer:54 
12-18 20:47:21.495 15402 15402 D ViewRootImpl: desktopMode is false
12-18 20:47:21.495 15402 15402 I ViewRootImpl: dVRR is disabled
12-18 20:47:21.504  1401  3703 D WindowManager: Changing focus from Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} to Window{862340c u0 com.katsuyamaki.DroidOSLauncher} displayId=0 Callers=com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 com.android.server.wm.Session.addToDisplayAsUser:24 android.view.IWindowSession$Stub.onTransact:757 
12-18 20:47:21.504  1401  3703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xeefb59f
12-18 20:47:21.504  1401  3703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-18 20:47:21.504  1401  3703 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-18 20:47:21.505  1401  3703 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xf78f8a4
12-18 20:47:21.505  1401  3703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x37dfb78
12-18 20:47:21.505  1401  3703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-18 20:47:21.506  1401  3703 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-18 20:47:21.506  1401  3703 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x1d4b9c2
12-18 20:47:21.507  1401  3703 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{862340c u0 com.katsuyamaki.DroidOSLauncher}, navColorWin=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 
12-18 20:47:21.508  1401  3703 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
12-18 20:47:21.548  1401  2773 I WindowManager: Relayout Window{862340c u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=1080x2404 ty=2032 d0
12-18 20:47:21.548  1401  2773 D WindowManager: makeSurface duration=0 name=$_15402
12-18 20:47:21.550  1401  2773 I WindowManager: Relayout hash=862340c, pid=15402, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP START CENTER sim={adjust=nothing} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:21.550  1401  2773 I WindowManager:   fl=1000300
12-18 20:47:21.550  1401  2773 I WindowManager:   bhv=1
12-18 20:47:21.550  1401  2773 I WindowManager:   fitTypes=206
12-18 20:47:21.550  1401  2773 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:21.550  1401  2773 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:21.566  1401  2773 D InputDispatcher: Once focus requested (0): 862340c com.katsuyamaki.DroidOSLauncher
12-18 20:47:21.566  1401  2773 D InputDispatcher: Focus request (0): 862340c com.katsuyamaki.DroidOSLauncher but waiting because NO_WINDOW
12-18 20:47:21.566  1401  2773 D InputDispatcher: Focus left window (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:21.707  1401  3703 D WindowManager: finishDrawingWindow: Window{862340c u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:21.708  1401  2591 I WindowManager: Relayout Window{c422cb7 u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=8 req=63x63 ty=2032 d0
12-18 20:47:21.709  1401  2591 E WindowManager: win=Window{c422cb7 u0 com.katsuyamaki.DroidOSLauncher} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-18 20:47:21.720  1401  2591 I WindowManager: Relayout hash=c422cb7, pid=15402, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:21.720  1401  2591 I WindowManager:   fl=1040308
12-18 20:47:21.720  1401  2591 I WindowManager:   bhv=1
12-18 20:47:21.720  1401  2591 I WindowManager:   fitTypes=206
12-18 20:47:21.720  1401  2591 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:21.720  1401  2591 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:21.737  1401  2773 D InputDispatcher: Focus entered window (0): 862340c com.katsuyamaki.DroidOSLauncher
12-18 20:47:21.745  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:21.745  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-18 20:47:21.746  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-18 20:47:21.802  1401  1645 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x5f98d20
12-18 20:47:21.802  1401  1645 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960, destroy=false, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963
12-18 20:47:21.803  1401  1645 I WindowManager: Reparenting to leash, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963, leashParent=Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960
12-18 20:47:21.804  1401  1645 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x1468c3b
12-18 20:47:22.808  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=127045011222000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:22.810  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x0, f=0x0, d=0, '862340c', t=1 +(0,-116)
12-18 20:47:23.318  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=127045526489000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:23.320  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x1, f=0x0, d=0, '862340c', t=1 +(0,-116)
12-18 20:47:23.325 15402 15402 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{2e961fc VFE...C.. .F.P.... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:502 
12-18 20:47:23.347  1401  3703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x1468c3b
12-18 20:47:23.347  1401  3703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960, destroy=false, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963
12-18 20:47:23.347  1401  3703 I WindowManager: Reparenting to leash, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963, leashParent=Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960
12-18 20:47:23.348  1401  3703 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x8c54170
12-18 20:47:23.351  1401  3703 D WindowManager: Changing focus from Window{862340c u0 com.katsuyamaki.DroidOSLauncher} to Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} displayId=0 Callers=com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-18 20:47:23.351  1401  3703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xf78f8a4
12-18 20:47:23.351  1401  3703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-18 20:47:23.351  1401  3703 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-18 20:47:23.352  1401  3703 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xa39ac0f
12-18 20:47:23.352  1401  3703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x1d4b9c2
12-18 20:47:23.352  1401  3703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-18 20:47:23.352  1401  3703 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-18 20:47:23.353  1401  3703 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xf95049c
12-18 20:47:23.353  1401  3703 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, navColorWin=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 
12-18 20:47:23.354  1401  3703 D WindowManager: updateSystemBarAttributes, bhv=2, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
12-18 20:47:23.360  1401  3545 D InputDispatcher: Focus left window (0): 862340c com.katsuyamaki.DroidOSLauncher
12-18 20:47:23.360  1401  1645 I WindowManager: Relayout Window{c422cb7 u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=63x63 ty=2032 d0
12-18 20:47:23.361  1401  1645 D WindowManager: makeSurface duration=1 name=$_15402
12-18 20:47:23.364  1401  1645 I WindowManager: Relayout hash=c422cb7, pid=15402, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:23.364  1401  1645 I WindowManager:   fl=1040308
12-18 20:47:23.364  1401  1645 I WindowManager:   bhv=1
12-18 20:47:23.364  1401  1645 I WindowManager:   fitTypes=206
12-18 20:47:23.364  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:23.364  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:23.375  1401  2773 D WindowManager: finishDrawingWindow: Window{c422cb7 u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:23.377  1401  3703 D InputDispatcher: Once focus requested (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:23.377  1401  3703 D InputDispatcher: Focus entered window (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:23.388  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:23.388  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-18 20:47:23.389  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-18 20:47:23.391  1401  3545 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x8c54170
12-18 20:47:23.391  1401  3545 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960, destroy=false, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963
12-18 20:47:23.391  1401  3545 I WindowManager: Reparenting to leash, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963, leashParent=Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960
12-18 20:47:23.392  1401  3545 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x8b19c46
12-18 20:47:23.408  1401  1551 I ActivityManager: Changes in 10053 6 to 15, 128 to 0
12-18 20:47:24.794  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=127046992838000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:24.795  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x4, f=0x0, d=0, 'c422cb7', t=1 
12-18 20:47:24.796  1401  1700 I InputDispatcher: Delivering touch to (1570): action: 0x0, f=0x0, d=0, '75c9765', t=1 +(-405,-1662)
12-18 20:47:24.808  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.813  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(405,1662)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.813  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.813  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.813  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.813  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.813  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.813  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.815  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.823  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(405,1662)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.823  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.823  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.823  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.823  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.823  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.823  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.829  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.836  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(405,1662)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.836  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.836  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.836  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.836  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.836  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.836  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.841  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.844  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(405,1662)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.844  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.844  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.844  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.844  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.844  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.844  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.844  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.847  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(405,1662)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.847  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.847  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.847  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.847  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.847  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.847  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.848  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.852  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(402,1647)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.852  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.852  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.852  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.852  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.852  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.852  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.854  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.858  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(402,1632)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.858  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.858  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.858  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.858  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.858  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.858  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.863  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.867  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(404,1614)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.867  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.867  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.867  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.867  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.867  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.867  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.871  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.875  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(407,1593)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.875  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.875  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.875  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.875  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.875  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.875  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.880  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.883  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(411,1572)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.883  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.883  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.883  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.883  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.883  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.883  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.887  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.890  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(415,1551)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.890  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.890  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.890  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.890  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.890  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.890  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.897  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.899  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(418,1531)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.899  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.899  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.899  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.899  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.899  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.899  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.904  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.906  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(420,1512)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.906  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.906  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.906  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.906  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.906  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.906  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.913  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.919  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(422,1494)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.919  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:24.919  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:24.919  1401  1645 I WindowManager:   bhv=1
12-18 20:47:24.919  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:24.919  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.919  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.921  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.924  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(423,1476)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.924  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.924  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.924  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.924  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.924  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.924  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.930  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.933  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1461)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.933  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:24.933  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:24.933  1401  1645 I WindowManager:   bhv=1
12-18 20:47:24.933  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:24.933  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.933  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.937  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.940  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1447)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.940  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:24.940  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:24.940  1401  1645 I WindowManager:   bhv=1
12-18 20:47:24.940  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:24.940  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.940  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.946  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.950  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1433)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.950  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:24.950  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:24.950  1401  1645 I WindowManager:   bhv=1
12-18 20:47:24.950  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:24.950  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.950  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.954  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.956  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1421)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.956  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.956  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.956  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.956  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.956  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.956  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.963  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.966  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1410)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.966  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.966  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.966  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.966  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.966  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.966  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.971  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.973  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1402)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.973  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:24.973  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:24.973  1401  1645 I WindowManager:   bhv=1
12-18 20:47:24.973  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:24.973  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.973  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.979  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.982  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1394)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.982  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.982  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.982  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.982  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.982  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.982  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.987  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.989  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1389)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.989  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:24.989  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:24.989  1401  3545 I WindowManager:   bhv=1
12-18 20:47:24.989  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:24.989  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.989  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:24.996  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:24.998  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1385)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:24.998  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:24.998  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:24.998  1401  1645 I WindowManager:   bhv=1
12-18 20:47:24.998  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:24.998  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:24.998  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.004  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.006  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1381)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.006  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.006  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.006  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.006  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.006  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.006  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.013  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.015  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1378)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.015  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.015  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.015  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.015  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.015  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.015  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.020  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.023  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1376)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.023  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.023  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.023  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.023  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.023  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.023  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.029  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.031  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1374)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.031  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.031  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.031  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.031  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.031  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.031  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.038  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.040  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1373)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.040  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.040  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.040  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.040  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.040  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.040  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.046  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.048  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.048  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.048  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.048  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.048  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.048  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.048  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.054  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.056  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.056  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.056  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.056  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.056  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.056  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.056  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.062  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.064  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.064  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.064  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.064  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.064  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.064  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.064  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.070  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.072  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.072  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.072  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.072  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.072  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.072  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.072  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.080  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.082  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.082  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.082  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.082  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.082  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.082  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.082  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.088  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.091  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.091  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.091  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.091  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.091  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.091  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.091  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.096  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.098  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.098  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.098  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.098  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.098  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.098  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.098  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.104  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.106  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.106  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.106  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.106  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.106  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.106  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.106  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.112  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.114  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.114  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.114  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.114  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.114  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.114  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.114  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.122  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.124  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.124  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.124  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.124  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.124  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.124  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.124  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.129  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.130  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.130  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.130  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.130  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.130  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.130  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.130  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.137  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.140  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.140  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.140  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.140  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.140  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.140  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.140  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.146  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.148  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.148  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.148  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.148  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.148  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.148  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.148  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.153  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.156  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.156  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.156  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.156  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.156  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.156  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.156  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.162  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.164  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.164  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.164  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.164  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.164  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.164  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.164  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.171  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.173  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.173  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.173  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.173  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.173  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.173  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.173  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.179  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.181  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.181  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.181  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.181  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.181  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.181  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.181  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.187  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.190  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1372)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.190  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.190  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.190  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.190  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.190  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.190  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.196  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.202  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1371)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.202  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.202  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.202  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.202  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.202  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.202  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.205  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.207  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1371)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.207  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.207  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.207  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.207  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.207  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.207  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.215  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.220  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1370)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.220  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.220  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.220  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.220  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.220  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.220  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.221  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.223  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1370)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.223  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.223  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.223  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.223  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.223  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.223  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.229  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.231  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1370)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.231  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.231  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.231  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.231  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.231  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.231  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.237  1401  1551 I ActivityManager: Changes in 10395 3 to 15, 144 to 0
12-18 20:47:25.238  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.241  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1370)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.241  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.241  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.241  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.241  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.241  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.241  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.246  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.248  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(424,1370)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.248  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.248  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.248  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.248  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.248  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.248  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.253  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.256  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(423,1369)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.256  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.256  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.256  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.256  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.256  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.256  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.262  1401  1645 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.264  1401  1645 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(420,1368)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.264  1401  1645 I WindowManager:   fl=1040228
12-18 20:47:25.264  1401  1645 I WindowManager:   pfl=40000000
12-18 20:47:25.264  1401  1645 I WindowManager:   bhv=1
12-18 20:47:25.264  1401  1645 I WindowManager:   fitTypes=207
12-18 20:47:25.264  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.264  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.266  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=127047475098000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:25.266  1401  1700 I InputDispatcher: Delivering touch to (1570): action: 0x1, f=0x0, d=0, '75c9765', t=1 +(-424,-1370)
12-18 20:47:25.270  1401  3545 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=527x444 ty=2032 d0
12-18 20:47:25.272  1401  3545 I WindowManager: Relayout hash=75c9765, pid=0, syncId=-1: mAttrs={(420,1368)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.272  1401  3545 I WindowManager:   fl=1040228
12-18 20:47:25.272  1401  3545 I WindowManager:   pfl=40000000
12-18 20:47:25.272  1401  3545 I WindowManager:   bhv=1
12-18 20:47:25.272  1401  3545 I WindowManager:   fitTypes=207
12-18 20:47:25.272  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.272  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.835  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=127048041341000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:25.837  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x0, f=0x0, d=0, 'c422cb7', t=1 +(-50,-200)
12-18 20:47:25.926  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=127048132789000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:25.927  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x1, f=0x0, d=0, 'c422cb7', t=1 +(-50,-200)
12-18 20:47:25.931 15402 15402 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{2e961fc VFE...C.. .F...... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerImpl.addView:158 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:511 com.example.quadrantlauncher.FloatingLauncherService.access$toggleDrawer:54 
12-18 20:47:25.933 15402 15402 D ViewRootImpl: desktopMode is false
12-18 20:47:25.934 15402 15402 I ViewRootImpl: dVRR is disabled
12-18 20:47:25.944  1401  3545 D WindowManager: Changing focus from Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} to Window{386f7 u0 com.katsuyamaki.DroidOSLauncher} displayId=0 Callers=com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 com.android.server.wm.Session.addToDisplayAsUser:24 android.view.IWindowSession$Stub.onTransact:757 
12-18 20:47:25.944  1401  3545 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xa39ac0f
12-18 20:47:25.944  1401  3545 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-18 20:47:25.945  1401  3545 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-18 20:47:25.945  1401  3545 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xc5e5def
12-18 20:47:25.945  1401  3545 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xf95049c
12-18 20:47:25.945  1401  3545 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-18 20:47:25.946  1401  3545 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-18 20:47:25.946  1401  3545 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xa506385
12-18 20:47:25.947  1401  3545 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{386f7 u0 com.katsuyamaki.DroidOSLauncher}, navColorWin=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 
12-18 20:47:25.947  1401  3545 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
12-18 20:47:25.975  1401  2773 I WindowManager: Relayout Window{386f7 u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=1080x2404 ty=2032 d0
12-18 20:47:25.976  1401  2773 D WindowManager: makeSurface duration=1 name=$_15402
12-18 20:47:25.978  1401  2773 I WindowManager: Relayout hash=386f7, pid=15402, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP START CENTER sim={adjust=nothing} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:25.978  1401  2773 I WindowManager:   fl=1000300
12-18 20:47:25.978  1401  2773 I WindowManager:   bhv=1
12-18 20:47:25.978  1401  2773 I WindowManager:   fitTypes=206
12-18 20:47:25.978  1401  2773 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:25.978  1401  2773 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:25.996  1401  1645 D InputDispatcher: Once focus requested (0): 386f7 com.katsuyamaki.DroidOSLauncher
12-18 20:47:25.996  1401  1645 D InputDispatcher: Focus request (0): 386f7 com.katsuyamaki.DroidOSLauncher but waiting because NO_WINDOW
12-18 20:47:25.996  1401  1645 D InputDispatcher: Focus left window (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:26.154  1401  2151 D WindowManager: finishDrawingWindow: Window{386f7 u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:26.154  1401  2589 I WindowManager: Relayout Window{c422cb7 u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=8 req=63x63 ty=2032 d0
12-18 20:47:26.154  1401  2589 E WindowManager: win=Window{c422cb7 u0 com.katsuyamaki.DroidOSLauncher} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-18 20:47:26.158  1401  2589 I WindowManager: Relayout hash=c422cb7, pid=15402, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:26.158  1401  2589 I WindowManager:   fl=1040308
12-18 20:47:26.158  1401  2589 I WindowManager:   bhv=1
12-18 20:47:26.158  1401  2589 I WindowManager:   fitTypes=206
12-18 20:47:26.158  1401  2589 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:26.158  1401  2589 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:26.228  1401  2591 D InputDispatcher: Focus entered window (0): 386f7 com.katsuyamaki.DroidOSLauncher
12-18 20:47:26.236  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:26.236  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-18 20:47:26.237  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-18 20:47:26.238  1401  3545 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x8b19c46
12-18 20:47:26.238  1401  3545 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960, destroy=false, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963
12-18 20:47:26.238  1401  3545 I WindowManager: Reparenting to leash, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963, leashParent=Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960
12-18 20:47:26.239  1401  3545 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0xe24a98a
12-18 20:47:26.916  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=127049117111000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:26.918  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x0, f=0x0, d=0, '386f7 c', t=1 +(0,-116)
12-18 20:47:26.982  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=127049190391000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:26.982  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x1, f=0x0, d=0, '386f7 c', t=1 +(0,-116)
12-18 20:47:26.985 15402 15402 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{2e961fc VFE...C.. .F.P.... 0,0-1080,2404 aid=1}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.quadrantlauncher.FloatingLauncherService.toggleDrawer:502 
12-18 20:47:27.001  1401  1645 I WindowManager: Relayout Window{c422cb7 u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=63x63 ty=2032 d0
12-18 20:47:27.002  1401  1645 D WindowManager: makeSurface duration=1 name=$_15402
12-18 20:47:27.008  1401  1645 I WindowManager: Relayout hash=c422cb7, pid=15402, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:27.008  1401  1645 I WindowManager:   fl=1040308
12-18 20:47:27.008  1401  1645 I WindowManager:   bhv=1
12-18 20:47:27.008  1401  1645 I WindowManager:   fitTypes=206
12-18 20:47:27.008  1401  1645 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:27.008  1401  1645 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:27.023  1401  3545 D WindowManager: finishDrawingWindow: Window{c422cb7 u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:27.032  1401  2591 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0xe24a98a
12-18 20:47:27.032  1401  2591 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960, destroy=false, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963
12-18 20:47:27.032  1401  2591 I WindowManager: Reparenting to leash, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963, leashParent=Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960
12-18 20:47:27.033  1401  2591 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x75a0865
12-18 20:47:27.037  1401  2591 D WindowManager: Changing focus from Window{386f7 u0 com.katsuyamaki.DroidOSLauncher} to Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} displayId=0 Callers=com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-18 20:47:27.037  1401  2591 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xc5e5def
12-18 20:47:27.037  1401  2591 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-18 20:47:27.037  1401  2591 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-18 20:47:27.038  1401  2591 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x1274348
12-18 20:47:27.038  1401  2591 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xa506385
12-18 20:47:27.038  1401  2591 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-18 20:47:27.038  1401  2591 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-18 20:47:27.039  1401  2591 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x5e7c5e1
12-18 20:47:27.040  1401  2591 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, navColorWin=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 
12-18 20:47:27.040  1401  2591 D WindowManager: updateSystemBarAttributes, bhv=2, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
12-18 20:47:27.050  1401  2591 D InputDispatcher: Focus left window (0): 386f7 com.katsuyamaki.DroidOSLauncher
12-18 20:47:27.069  1401  2589 D InputDispatcher: Once focus requested (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:27.069  1401  2589 D InputDispatcher: Focus entered window (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:27.077  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:27.077  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-18 20:47:27.078  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-18 20:47:27.080  1401  1645 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x75a0865
12-18 20:47:27.080  1401  1645 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960, destroy=false, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963
12-18 20:47:27.081  1401  1645 I WindowManager: Reparenting to leash, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963, leashParent=Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960
12-18 20:47:27.081  1401  1645 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x3f13360
12-18 20:47:27.577  1401  1645 I ActivityManager: Changes in 10053 15 to 6, 0 to 128
12-18 20:47:30.255  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=127052454978000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:30.257  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x4, f=0x0, d=0, 'c422cb7', t=1 
12-18 20:47:30.258  1401  1700 I InputDispatcher: Delivering touch to (1570): action: 0x0, f=0x0, d=0, 'd1c6d2d', t=1 +(-90,-1169)
12-18 20:47:30.329  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=127052537611000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:30.329  1401  1700 I InputDispatcher: Delivering touch to (1570): action: 0x1, f=0x0, d=0, 'd1c6d2d', t=1 +(-90,-1169)
12-18 20:47:30.332  1570  1570 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{19a679b V.E...... ........ 0,0-972,564}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.KeyboardOverlay.hide:328 
12-18 20:47:30.338  1570  1570 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{c284cd6 VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1539 
12-18 20:47:30.342  1570  1570 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{c284cd6 VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1539 com.example.coverscreentester.OverlayService.toggleCustomKeyboard:1244 
12-18 20:47:30.353  1570  1570 D ViewRootImpl: desktopMode is false
12-18 20:47:30.354  1570  1570 I ViewRootImpl: dVRR is disabled
12-18 20:47:30.369  1570  1570 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{4346062 V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1540 
12-18 20:47:30.372  1570  1570 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{4346062 V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1540 com.example.coverscreentester.OverlayService.toggleCustomKeyboard:1244 
12-18 20:47:30.378  1570  1570 D ViewRootImpl: desktopMode is false
12-18 20:47:30.378  1570  1570 I ViewRootImpl: dVRR is disabled
12-18 20:47:30.384  1401  1552 I ActivityManager: Changes in 10100 19 to 11, 0 to 128
12-18 20:47:30.395  1401  2724 I ActivityManager: Changes in 10100 11 to 19, 128 to 0
12-18 20:47:30.400  1401  2724 I WindowManager: Relayout Window{814a85f u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=93x93 ty=2032 d0
12-18 20:47:30.401  1401  2724 D WindowManager: makeSurface duration=0 name=$_1570
12-18 20:47:30.403  1401  2724 I WindowManager: Relayout hash=814a85f, pid=1570, syncId=-1: mAttrs={(90,1169)(93x93) gr=TOP START CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:30.403  1401  2724 I WindowManager:   fl=1000308
12-18 20:47:30.403  1401  2724 I WindowManager:   bhv=1
12-18 20:47:30.403  1401  2724 I WindowManager:   fitTypes=206
12-18 20:47:30.403  1401  2724 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:30.403  1401  2724 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:30.415  1401  3545 I WindowManager: Relayout Window{b985d62 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=50x50 ty=2032 d0
12-18 20:47:30.416  1401  3545 D WindowManager: makeSurface duration=0 name=$_1570
12-18 20:47:30.417  1401  2773 D WindowManager: finishDrawingWindow: Window{814a85f u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:30.418  1401  3545 I WindowManager: Relayout hash=b985d62, pid=1570, syncId=-1: mAttrs={(540,1260)(wrapxwrap) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:30.418  1401  3545 I WindowManager:   fl=1000318
12-18 20:47:30.418  1401  3545 I WindowManager:   bhv=1
12-18 20:47:30.418  1401  3545 I WindowManager:   fitTypes=206
12-18 20:47:30.418  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:30.418  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:30.429  1401  3545 D WindowManager: finishDrawingWindow: Window{b985d62 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:30.432  1401  2589 I WindowManager: Relayout Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=8 req=527x444 ty=2032 d0
12-18 20:47:30.432  1401  2589 E WindowManager: win=Window{75c9765 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-18 20:47:30.435  1401  2589 I WindowManager: Relayout hash=75c9765, pid=1570, syncId=-1: mAttrs={(420,1368)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:30.435  1401  2589 I WindowManager:   fl=1040228
12-18 20:47:30.435  1401  2589 I WindowManager:   pfl=40000000
12-18 20:47:30.435  1401  2589 I WindowManager:   bhv=1
12-18 20:47:30.435  1401  2589 I WindowManager:   fitTypes=207
12-18 20:47:30.435  1401  2589 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:30.435  1401  2589 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:30.443  1570  1570 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{c284cd6 VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1539 
12-18 20:47:30.448  1570  1570 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{c284cd6 VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1539 com.example.coverscreentester.OverlayService.setupBubble$lambda$25$lambda$24:848 
12-18 20:47:30.453  1570  1570 D ViewRootImpl: desktopMode is false
12-18 20:47:30.453  1570  1570 I ViewRootImpl: dVRR is disabled
12-18 20:47:30.463  1570  1570 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{4346062 V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1540 
12-18 20:47:30.468  1570  1570 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{4346062 V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1540 com.example.coverscreentester.OverlayService.setupBubble$lambda$25$lambda$24:848 
12-18 20:47:30.472  1570  1570 D ViewRootImpl: desktopMode is false
12-18 20:47:30.473  1570  1570 I ViewRootImpl: dVRR is disabled
12-18 20:47:30.482  1401  2589 I WindowManager: Relayout Window{5d98d74 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=93x93 ty=2032 d0
12-18 20:47:30.483  1401  2589 D WindowManager: makeSurface duration=0 name=$_1570
12-18 20:47:30.485  1401  2589 I WindowManager: Relayout hash=5d98d74, pid=1570, syncId=-1: mAttrs={(90,1169)(93x93) gr=TOP START CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:30.485  1401  2589 I WindowManager:   fl=1000308
12-18 20:47:30.485  1401  2589 I WindowManager:   bhv=1
12-18 20:47:30.485  1401  2589 I WindowManager:   fitTypes=206
12-18 20:47:30.485  1401  2589 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:30.485  1401  2589 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:30.498  1401  1645 D WindowManager: finishDrawingWindow: Window{5d98d74 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:30.498  1401  2724 I WindowManager: Relayout Window{fe6435b u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=50x50 ty=2032 d0
12-18 20:47:30.499  1401  2724 D WindowManager: makeSurface duration=1 name=$_1570
12-18 20:47:30.501  1401  2724 I WindowManager: Relayout hash=fe6435b, pid=1570, syncId=-1: mAttrs={(540,1260)(wrapxwrap) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-18 20:47:30.501  1401  2724 I WindowManager:   fl=1000318
12-18 20:47:30.501  1401  2724 I WindowManager:   bhv=1
12-18 20:47:30.501  1401  2724 I WindowManager:   fitTypes=206
12-18 20:47:30.501  1401  2724 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:30.501  1401  2724 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:30.510  1401  2724 D WindowManager: finishDrawingWindow: Window{fe6435b u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:30.516  1401  3545 D WindowManager: finishDrawingWindow: Window{fe6435b u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=HAS_DRAWN seqId=0
12-18 20:47:30.988  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=127053190942000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:30.990  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x4, f=0x0, d=0, 'c422cb7', t=1 
12-18 20:47:30.990  1401  1700 I InputDispatcher: Delivering touch to (2045): action: 0x4, f=0x0, d=0, '253deac', t=1 
12-18 20:47:30.990  1401  1700 I InputDispatcher: Delivering touch to (2738): action: 0x0, f=0x0, d=0, '3dc2dd0', t=1 
12-18 20:47:31.015  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-18 20:47:31.042  1401  2247 I InputDispatcher: Channel [Gesture Monitor] swipe-up is stealing input gesture for device 3 from [3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity, 5b7421b com.android.systemui.wallpapers.ImageWallpaper, [Gesture Monitor] clo, [Gesture Monitor] secinputdev, [Gesture Monitor] PalmMotion, [Gesture Monitor] edge-swipe]
12-18 20:47:31.042  1401  2247 D InputDispatcher: Cancel for '3dc2dd0': input channel stole pointer stream, m=1
12-18 20:47:31.042  1401  2247 I InputDispatcher: Delivering touch to (2738): action: 0x3, f=0x20, d=0, '3dc2dd0', t=1 
12-18 20:47:31.042  1401  2247 D InputDispatcher: Cancel for '5b7421b': input channel stole pointer stream, m=1
12-18 20:47:31.046  1401  1542 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-18 20:47:31.060  1401  3074 I WindowManager: Reparenting to leash, surface=Surface(name=RemoteWallpaperAnim:1:1)/@0xd604b25, leashParent=Surface(name=OneHanded:0:14)/@0xc8374fa
12-18 20:47:31.061  1401  3074 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=RemoteWallpaperAnim:1:1)/@0xd604b25 - animation-leash of remote_wallpaper)/@0xd9adb0e
12-18 20:47:31.061  1401  3074 D WindowManager: startAnimation for remote wallpaper, leash=Surface(name=Surface(name=RemoteWallpaperAnim:1:1)/@0xd604b25 - animation-leash of remote_wallpaper)/@0xd9adb0e
12-18 20:47:31.061  1401  3074 D WindowManager: startRemoteWallpaperAnimation, d=0, leash=Surface(name=Surface(name=RemoteWallpaperAnim:1:1)/@0xd604b25 - animation-leash of remote_wallpaper)/@0xd9adb0e
12-18 20:47:31.062  1401  3074 D WindowManager: Transition is created, t=TransitionRecord{b025b3c id=-1 type=TO_FRONT flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.WindowOrganizerController.startTransition:18 com.android.server.wm.WindowOrganizerController.startNewTransition:2 android.window.IWindowOrganizerController$Stub.onTransact:247 com.android.server.wm.WindowOrganizerController.onTransact:1 
12-18 20:47:31.063  1401  3074 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}
12-18 20:47:31.064  1401  3074 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{225d09b u0 RecentsTransitionOverlay}
12-18 20:47:31.065  1401  3074 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{16d9d50 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity}
12-18 20:47:31.065  1401  3074 D WindowManager: rotationForOrientation, orientationSource=ActivityRecord{226040612 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity t17650}
12-18 20:47:31.067  1401  3074 I WindowManager: Reparenting to leash, surface=Surface(name=1aa7434 FreeformContainer)/@0x4f2b598, leashParent=Surface(name=WindowToken{b8c572b type=2604 android.os.BinderProxy@f0783a5})/@0xa3bd1f1
12-18 20:47:31.067  1401  3074 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=1aa7434 FreeformContainer)/@0x4f2b598 - animation-leash of window_animation)/@0x43550c5
12-18 20:47:31.073  1401  3074 I WindowManager: Relayout Window{225d09b u0 RecentsTransitionOverlay}: viewVisibility=0 req=1x1 ty=2632 d0
12-18 20:47:31.075  1401  3074 I WindowManager: Relayout hash=225d09b, pid=2738, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=TRANSIENT_LAUNCH_OVERLAY fmt=TRANSLUCENT if=INPUT_FEATURE_NO_INPUT_CHANNEL
12-18 20:47:31.075  1401  3074 I WindowManager:   fl=118
12-18 20:47:31.075  1401  3074 I WindowManager:   pfl=10000040
12-18 20:47:31.075  1401  3074 I WindowManager:   bhv=1
12-18 20:47:31.075  1401  3074 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:31.075  1401  3074 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-18 20:47:31.075  1401  3074 I WindowManager:   sfl=4020000}
12-18 20:47:31.076  1401  1544 D WindowManager: mRunner#onAnimationStart for remote wallpaper=[android.view.RemoteAnimationTarget@6ad9a1a], transaction=startRemoteWallpaperAnimation_75610415
12-18 20:47:31.098  1401  3074 I WindowManager: Relayout Window{16d9d50 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity}: viewVisibility=8 req=1080x2520 ty=1 d0
12-18 20:47:31.100  1401  3074 I WindowManager: Relayout hash=16d9d50, pid=2738, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=pan} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT wanim=0x1030306
12-18 20:47:31.100  1401  3074 I WindowManager:   fl=81910100
12-18 20:47:31.100  1401  3074 I WindowManager:   pfl=10008840
12-18 20:47:31.100  1401  3074 I WindowManager:   bhv=2
12-18 20:47:31.100  1401  3074 I WindowManager:   fitSides=0
12-18 20:47:31.100  1401  3074 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:31.100  1401  3074 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-18 20:47:31.100  1401  3074 I WindowManager:   sfl=4000000}
12-18 20:47:31.119  1401  2323 I WindowManager: Relayout Window{16d9d50 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity}: viewVisibility=0 req=1080x2520 ty=1 d0
12-18 20:47:31.120  1401  2323 D WindowManager: makeSurface duration=1 name=com.sec.android.app.launcher/com.android.quickstep.RecentsActivity$_2738
12-18 20:47:31.122  1401  2323 I WindowManager: Relayout hash=16d9d50, pid=2738, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT wanim=0x1030306
12-18 20:47:31.122  1401  2323 I WindowManager:   fl=81910100
12-18 20:47:31.122  1401  2323 I WindowManager:   pfl=10008840
12-18 20:47:31.122  1401  2323 I WindowManager:   bhv=2
12-18 20:47:31.122  1401  2323 I WindowManager:   fitSides=0
12-18 20:47:31.122  1401  2323 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:31.122  1401  2323 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-18 20:47:31.122  1401  2323 I WindowManager:   sfl=4000000}
12-18 20:47:31.144  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=127053353378000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:31.153  1401  3074 D WindowManager: finishDrawingWindow: Window{16d9d50 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:31.184  1401  2323 D InputDispatcher: Focused application(0): ActivityRecord{226040612 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity t17650}
12-18 20:47:31.185  1401  2323 D WindowManager: Changing focus from Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} to Window{16d9d50 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity} displayId=0 Callers=com.android.server.wm.ActivityTaskManagerService.setLastResumedActivityUncheckLocked:264 com.android.server.wm.ActivityTaskManagerService.setFocusedTask:20 com.android.server.wm.ActivityTaskManagerService.focusTopTask:53 android.app.IActivityTaskManager$Stub.onTransact:2027 
12-18 20:47:31.185  1401  2323 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x1274348
12-18 20:47:31.185  1401  2323 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-18 20:47:31.185  1401  2323 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-18 20:47:31.186  1401  2323 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xe310b6c
12-18 20:47:31.186  1401  2323 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x5e7c5e1
12-18 20:47:31.186  1401  2323 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-18 20:47:31.186  1401  2323 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-18 20:47:31.187  1401  2323 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x930bdca
12-18 20:47:31.208  1401  2247 D InputDispatcher: Once focus requested (0): 16d9d50 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity
12-18 20:47:31.208  1401  2247 D InputDispatcher: Focus request (0): 16d9d50 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity but waiting because NOT_VISIBLE
12-18 20:47:31.208  1401  2247 D InputDispatcher: Focus left window (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:31.272  1401  1545 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{b8c572b type=2604 android.os.BinderProxy@f0783a5})/@0xa3bd1f1, destroy=true, surface=Surface(name=1aa7434 FreeformContainer)/@0x4f2b598
12-18 20:47:31.843  1401  2247 D InputDispatcher: Focused application(0): ActivityRecord{26066939 u0 com.sec.android.app.launcher/.activities.LauncherActivity t17646}
12-18 20:47:31.843  1401  2247 D WindowManager: Changing focus from Window{16d9d50 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity} to Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} displayId=0 Callers=com.android.server.wm.ActivityTaskManagerService.setLastResumedActivityUncheckLocked:264 com.android.server.wm.ActivityTaskSupervisor.updateTopResumedActivityIfNeeded:49 com.android.server.wm.TaskDisplayArea.positionChildAt:252 com.android.server.wm.Task.moveToFront:40 
12-18 20:47:31.843  1401  2247 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xe310b6c
12-18 20:47:31.843  1401  2247 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-18 20:47:31.843  1401  2247 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-18 20:47:31.844  1401  2247 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xf84ec96
12-18 20:47:31.844  1401  2247 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x930bdca
12-18 20:47:31.844  1401  2247 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-18 20:47:31.844  1401  2247 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-18 20:47:31.845  1401  2247 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xa9f5c04
12-18 20:47:31.846  1401  2247 D WindowManager: Transition is created, t=TransitionRecord{2fb95ed id=-1 type=TO_FRONT flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createTransition:17 com.android.server.wm.ActivityTaskSupervisor.findTaskToMoveToFront:96 com.android.server.wm.ActivityTaskManagerService.moveTaskToFrontLocked:200 com.android.server.wm.ActivityTaskSupervisor.startActivityFromRecents:727 
12-18 20:47:31.846  1401  2247 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{1230479 u0 com.termux/com.termux.app.TermuxActivity}
12-18 20:47:31.855  1401  2323 D InputDispatcher: Focused application(0): ActivityRecord{99426944 u0 com.termux/.app.TermuxActivity t17882}
12-18 20:47:31.856  1401  2323 D WindowManager: Changing focus from Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} to null displayId=0 Callers=com.android.server.wm.ActivityTaskManagerService.setLastResumedActivityUncheckLocked:264 com.android.server.wm.ActivityTaskSupervisor.updateTopResumedActivityIfNeeded:49 com.android.server.wm.TaskFragment.setResumedActivity:30 com.android.server.wm.ActivityRecord.setState:100 
12-18 20:47:31.858  1401  2323 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}
12-18 20:47:31.858  1401  2323 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{16d9d50 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity}
12-18 20:47:31.859  1401  2247 D InputDispatcher: Once focus requested (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:31.859  1401  2247 D InputDispatcher: Focus entered window (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:31.859  1401  2323 D WindowManager: rotationForOrientation, orientationSource=null
12-18 20:47:31.863 13489 13489 D ViewRootImpl: Skipping stats log for color mode
12-18 20:47:31.867  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:31.867  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-18 20:47:31.867  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-18 20:47:31.871  1401  1868 I WindowManager: Relayout Window{1230479 u0 com.termux/com.termux.app.TermuxActivity}: viewVisibility=0 req=1080x2520 ty=1 d0
12-18 20:47:31.871  1401  1868 D WindowManager: makeSurface duration=0 name=com.termux/com.termux.app.TermuxActivity$_13489
12-18 20:47:31.873  1401  1868 D WindowManager: Changing focus from null to Window{1230479 u0 com.termux/com.termux.app.TermuxActivity} displayId=0 Callers=com.android.server.wm.WindowManagerService.relayoutWindow:1359 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 
12-18 20:47:31.873  1401  1868 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xf84ec96
12-18 20:47:31.873  1401  1868 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-18 20:47:31.873  1401  1868 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-18 20:47:31.874  1401  1868 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xe27e721
12-18 20:47:31.874  1401  1868 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0xa9f5c04
12-18 20:47:31.874  1401  1868 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a, destroy=false, surface=Surface(name=7a1affc StatusBar)/@0x9878745
12-18 20:47:31.874  1401  1868 I WindowManager: Reparenting to leash, surface=Surface(name=7a1affc StatusBar)/@0x9878745, leashParent=Surface(name=WindowToken{ea68ef type=2000 android.os.BinderProxy@acba6c9})/@0xe49ce9a
12-18 20:47:31.874  1401  1868 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=7a1affc StatusBar)/@0x9878745 - animation-leash of insets_animation)/@0x825bd07
12-18 20:47:31.875  1401  1868 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{1230479 u0 com.termux/com.termux.app.TermuxActivity}, navColorWin=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowManagerService.relayoutWindow:1359 com.android.server.wm.Session.relayout:27 
12-18 20:47:31.875  1401  1868 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-1
12-18 20:47:31.875  1401  3074 D InputDispatcher: Once focus requested (0): <null>
12-18 20:47:31.875  1401  3074 D InputDispatcher: Focus request (0): <null> but waiting because NO_WINDOW
12-18 20:47:31.875  1401  3074 D InputDispatcher: Focus left window (0): 3dc2dd0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity
12-18 20:47:31.876  1401  1868 I WindowManager: Relayout hash=1230479, pid=13489, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} ty=BASE_APPLICATION wanim=0x1030317
12-18 20:47:31.876  1401  1868 I WindowManager:   fl=8d810100
12-18 20:47:31.876  1401  1868 I WindowManager:   pfl=10008040
12-18 20:47:31.876  1401  1868 I WindowManager:   vsysui=700
12-18 20:47:31.876  1401  1868 I WindowManager:   bhv=1
12-18 20:47:31.876  1401  1868 I WindowManager:   fitSides=0
12-18 20:47:31.876  1401  1868 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:31.876  1401  1868 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:31.879  1401  1544 I ActivityManager: Changes in 10342 4 to 2, 254 to 255
12-18 20:47:31.892  1401  3074 D InputDispatcher: Once focus requested (0): 1230479 com.termux/com.termux.app.TermuxActivity
12-18 20:47:31.892  1401  3074 D InputDispatcher: Focus request (0): 1230479 com.termux/com.termux.app.TermuxActivity but waiting because NO_WINDOW
12-18 20:47:31.916  1401  3074 D WindowManager: finishDrawingWindow: Window{1230479 u0 com.termux/com.termux.app.TermuxActivity} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:31.934  1401  2151 D WindowManager: setActiveRecents, recents=null, task=null, caller=com.android.server.wm.InputMonitor.setActiveRecents:33 com.android.server.wm.TransitionController.finishTransition:1670 com.android.server.wm.WindowOrganizerController.finishTransitionInner:31 
12-18 20:47:31.934  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:31.942  1401  3074 I WindowManager: Relayout Window{225d09b u0 RecentsTransitionOverlay}: viewVisibility=0 req=1x1 ty=2632 d0
12-18 20:47:31.944  1401  3074 I WindowManager: Relayout hash=225d09b, pid=2738, syncId=-1: mAttrs={(0,0)(1x1) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=TRANSIENT_LAUNCH_OVERLAY fmt=TRANSLUCENT if=INPUT_FEATURE_NO_INPUT_CHANNEL
12-18 20:47:31.944  1401  3074 I WindowManager:   fl=118
12-18 20:47:31.944  1401  3074 I WindowManager:   pfl=10000040
12-18 20:47:31.944  1401  3074 I WindowManager:   bhv=1
12-18 20:47:31.944  1401  3074 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:31.944  1401  3074 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-18 20:47:31.944  1401  3074 I WindowManager:   sfl=4020000}
12-18 20:47:31.945  1401  3074 D InputDispatcher: Focus entered window (0): 1230479 com.termux/com.termux.app.TermuxActivity
12-18 20:47:31.953  1401  1763 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:31.953  1401  1763 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-18 20:47:31.953  1401  1763 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-18 20:47:31.953  1401  1763 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-18 20:47:31.957  1401  1763 I ActivityManager: Changes in 10332 7 to 5, 128 to 255
12-18 20:47:31.957  1401  2151 D WindowManager: finishDrawingWindow: Window{225d09b u0 RecentsTransitionOverlay} mDrawState=HAS_DRAWN seqId=1478
12-18 20:47:31.958  1401  1763 I InputMethodManagerService: attachNewInputLocked: showCurrentInputInternal, softInputModeState=STATE_UNSPECIFIED|ADJUST_RESIZE|IS_FORWARD_NAVIGATION
12-18 20:47:31.959  1401  3703 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x3f13360
12-18 20:47:31.959  1401  3703 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960, destroy=false, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963
12-18 20:47:31.959  1401  3703 I WindowManager: Reparenting to leash, surface=Surface(name=897ad1d InputMethod)/@0xe8f8963, leashParent=Surface(name=WindowToken{ab9e97e type=2011 android.os.Binder@1356f39})/@0x48bd960
12-18 20:47:31.960  1401  3703 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=897ad1d InputMethod)/@0xe8f8963 - animation-leash of insets_animation)/@0x98bca85
12-18 20:47:31.962  1401  1544 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-18 20:47:31.962  1401  3545 I InputMethodManagerService: isAccessoryKeyboard 0
12-18 20:47:31.963  1401  2247 I InputMethodManagerService: isAccessoryKeyboard 0
12-18 20:47:31.973  1401  1544 I ActivityManager: Changes in 10155 2 to 5, 255 to 184
12-18 20:47:31.979  1401  2151 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:31.981  1401  2151 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-18 20:47:31.981  1401  2151 I InputMethodManagerService: setImeWindowStatus: vis=3, backDisposition=0
12-18 20:47:31.982  1401  2247 I InputMethodManagerService: isAccessoryKeyboard 0
12-18 20:47:31.982  1401  3703 I InputMethodManagerService: isAccessoryKeyboard 0
12-18 20:47:31.988  1401  3545 I WindowManager: Relayout Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}: viewVisibility=8 req=1080x2520 ty=1 d0
12-18 20:47:31.989  1401  2247 I WindowManager: Relayout Window{897ad1d u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-18 20:47:31.989  1401  3545 I WindowManager: Relayout hash=3dc2dd0, pid=2738, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=nothing forwardNavigation} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT wanim=0x1030306
12-18 20:47:31.989  1401  3545 I WindowManager:   fl=81910100
12-18 20:47:31.989  1401  3545 I WindowManager:   pfl=10008840
12-18 20:47:31.989  1401  3545 I WindowManager:   bhv=2
12-18 20:47:31.989  1401  3545 I WindowManager:   fitSides=0
12-18 20:47:31.989  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:31.989  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=200000 naviIconColor=0}
12-18 20:47:31.990  1401  2247 D WindowManager: makeSurface duration=0 name=InputMethod$_19314
12-18 20:47:31.990  1401  2247 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0xe27e721
12-18 20:47:31.990  1401  2247 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf, destroy=false, surface=Surface(name=253deac NavigationBar0)/@0x344c78e
12-18 20:47:31.990  1401  2247 I WindowManager: Reparenting to leash, surface=Surface(name=253deac NavigationBar0)/@0x344c78e, leashParent=Surface(name=WindowToken{742645f type=2019 android.os.BinderProxy@3f7f8b9})/@0xeea6baf
12-18 20:47:31.991  1401  2247 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=253deac NavigationBar0)/@0x344c78e - animation-leash of insets_animation)/@0x6d8be7e
12-18 20:47:31.992  1401  2247 I WindowManager: Relayout hash=897ad1d, pid=19314, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-18 20:47:31.992  1401  2247 I WindowManager:   fl=81800108
12-18 20:47:31.992  1401  2247 I WindowManager:   pfl=14000000
12-18 20:47:31.992  1401  2247 I WindowManager:   bhv=1
12-18 20:47:31.992  1401  2247 I WindowManager:   fitTypes=3
12-18 20:47:31.992  1401  2247 I WindowManager:   fitSides=7
12-18 20:47:31.992  1401  2247 I WindowManager:   fitIgnoreVis
12-18 20:47:31.992  1401  2247 I WindowManager:   dvrrWindowFrameRateHint=true
12-18 20:47:31.992  1401  2247 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-18 20:47:31.996  1401  3703 E WindowManager: win=Window{3dc2dd0 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:192 com.android.server.wm.ActivityClientController.activityStopped:97 android.app.IActivityClientController$Stub.onTransact:726 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1462 android.os.Binder.execTransact:1401 
12-18 20:47:32.003  1401  3545 D WindowManager: setInsetsWindow Window{897ad1d u0 InputMethod}, contentInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion() -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-18 20:47:32.005  1401  3545 D WindowManager: finishDrawingWindow: Window{897ad1d u0 InputMethod} mDrawState=DRAW_PENDING seqId=0
12-18 20:47:32.005  1401  3074 I WindowManager: Relayout Window{16d9d50 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity}: viewVisibility=4 req=1080x2520 ty=1 d0
12-18 20:47:32.008  1401  3074 I WindowManager: Relayout hash=16d9d50, pid=2738, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=pan forwardNavigation} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT wanim=0x1030306
12-18 20:47:32.008  1401  3074 I WindowManager:   fl=81910100
12-18 20:47:32.008  1401  3074 I WindowManager:   pfl=10008840
12-18 20:47:32.008  1401  3074 I WindowManager:   bhv=2
12-18 20:47:32.008  1401  3074 I WindowManager:   fitSides=0
12-18 20:47:32.008  1401  3074 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:32.008  1401  3074 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-18 20:47:32.008  1401  3074 I WindowManager:   sfl=4000000}
12-18 20:47:32.012  1401  2773 E WindowManager: win=Window{16d9d50 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=4 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:192 com.android.server.wm.ActivityClientController.activityStopped:97 android.app.IActivityClientController$Stub.onTransact:726 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1462 android.os.Binder.execTransact:1401 
12-18 20:47:32.015  1401  3545 I WindowManager: Relayout Window{16d9d50 u0 com.sec.android.app.launcher/com.android.quickstep.RecentsActivity}: viewVisibility=8 req=1080x2520 ty=1 d0
12-18 20:47:32.016  1401  3545 I WindowManager: Relayout hash=16d9d50, pid=2738, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=pan forwardNavigation} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT wanim=0x1030306
12-18 20:47:32.016  1401  3545 I WindowManager:   fl=81910100
12-18 20:47:32.016  1401  3545 I WindowManager:   pfl=10008840
12-18 20:47:32.016  1401  3545 I WindowManager:   bhv=2
12-18 20:47:32.016  1401  3545 I WindowManager:   fitSides=0
12-18 20:47:32.016  1401  3545 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:32.016  1401  3545 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-18 20:47:32.016  1401  3545 I WindowManager:   sfl=4000000}
12-18 20:47:32.018  1401  2589 D WindowManager: finishDrawingWindow: Window{897ad1d u0 InputMethod} mDrawState=HAS_DRAWN seqId=0
12-18 20:47:32.025  1401  2589 I WindowManager: Relayout Window{1230479 u0 com.termux/com.termux.app.TermuxActivity}: viewVisibility=0 req=1080x2520 ty=1 d0
12-18 20:47:32.026  1401  2589 I WindowManager: Relayout hash=1230479, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} ty=BASE_APPLICATION wanim=0x1030317
12-18 20:47:32.026  1401  2589 I WindowManager:   fl=8d810100
12-18 20:47:32.026  1401  2589 I WindowManager:   pfl=10008040
12-18 20:47:32.026  1401  2589 I WindowManager:   vsysui=700
12-18 20:47:32.026  1401  2589 I WindowManager:   bhv=1
12-18 20:47:32.026  1401  2589 I WindowManager:   fitSides=0
12-18 20:47:32.026  1401  2589 I WindowManager:   frameRateBoostOnTouch=true
12-18 20:47:32.026  1401  2589 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-18 20:47:32.032  1401  3074 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:32.047  1401  2589 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-18 20:47:32.061  1401  2589 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-18 20:47:32.952  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=127055160579000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:32.952  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x4, f=0x0, d=0, 'c422cb7', t=1 
12-18 20:47:32.952  1401  1700 I InputDispatcher: Delivering touch to (2045): action: 0x4, f=0x0, d=0, '253deac', t=1 
12-18 20:47:32.952  1401  1700 I InputDispatcher: Delivering touch to (13489): action: 0x0, f=0x0, d=0, '1230479', t=1 
12-18 20:47:33.018  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x1, time=127055226873000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:33.018  1401  1700 I InputDispatcher: Delivering touch to (13489): action: 0x1, f=0x0, d=0, '1230479', t=1 
12-18 20:47:33.643  1401  1551 I ActivityManager: Changes in 10053 6 to 15, 128 to 0
12-18 20:47:33.703  1401  1401 D InputDispatcher: Inject motion (0/0): action=0x0, time=127055903509000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-18 20:47:33.704  1401  1700 I InputDispatcher: Delivering touch to (15402): action: 0x4, f=0x0, d=0, 'c422cb7', t=1 
12-18 20:47:33.704  1401  1700 I InputDispatcher: Delivering touch to (2045): action: 0x4, f=0x0, d=0, '253deac', t=1 
12-18 20:47:33.704  1401  1700 I InputDispatcher: Delivering touch to (19314): action: 0x0, f=0x0, d=0, '897ad1d', t=1 +(0,-116)
