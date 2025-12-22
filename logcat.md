--------- beginning of system
12-22 20:59:08.661  1423  1549 D WindowManager: onStateChanged, state=2
12-22 20:59:08.858  1423  1423 I WindowManager: getCoverScreenRotation 0, sensor= 0, isFlat=false, isDual=false
12-22 20:59:09.098  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-22 20:59:09.218  1423  1549 D WindowManager: onStateChanged, state=0
12-22 20:59:09.230  1423  1549 D WindowManager: Changing focus from Window{f704bfc u0 com.termux/com.termux.app.TermuxActivity} to null displayId=0 Callers=com.android.server.wm.RootWindowContainer.onChildPositionChanged:8 com.android.server.wm.WindowContainer.positionChildAt:69 com.android.server.wm.RootWindowContainer.positionChildAt:8 com.android.server.wm.WindowManagerServiceExt.moveDisplayToTop:37 
12-22 20:59:09.230  1423  1549 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{64185e1 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, navColorWin=Window{f748235 u0 InputMethod}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.RootWindowContainer.onChildPositionChanged:8 com.android.server.wm.WindowContainer.positionChildAt:69 
12-22 20:59:09.230  1423  1549 D WindowManager: updateSystemBarAttributes, bhv=2, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
--------- beginning of main
12-22 20:59:09.230  1423  1549 D InputDispatcher: Focused display: 0 -> 1
12-22 20:59:09.230  1423  1549 W InputDispatcher: Focused display #1 does not have a focused window.
12-22 20:59:09.230  1423  1549 E InputDispatcher: But another display has a focused window
12-22 20:59:09.230  1423  1549 E InputDispatcher:   FocusedWindows:
12-22 20:59:09.230  1423  1549 E InputDispatcher:     displayId=0, name='f704bfc com.termux/com.termux.app.TermuxActivity'
12-22 20:59:09.233  1423  2229 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=19260)/@0xcf8efbb} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-22 20:59:09.264  1423  1754 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-22 20:59:09.264  1423  2227 D InputDispatcher: Once focus requested (0): <null>
12-22 20:59:09.264  1423  2227 D InputDispatcher: Focus request (0): <null> but waiting because NO_WINDOW
12-22 20:59:09.264  1423  2227 D InputDispatcher: Focus left window (0): f704bfc com.termux/com.termux.app.TermuxActivity
12-22 20:59:09.276  1423  1604 D WindowManager: Create SleepToken: tag=Display-off, displayId=0
12-22 20:59:09.281  1423  1754 W InputMethodManagerService: callbackImeWindowStatus: skipped topFocusedDisplayId=1, curTokenDisplayId=0
12-22 20:59:09.282  1423  1754 I WindowManager: Attempted to take screenshot while display was off.
12-22 20:59:09.282  1423  2230 W WindowManager: Cannot find window which accessibility connection is added to
12-22 20:59:09.282  2022  2022 I WindowManager: WindowManagerGlobal#addView, ty=2024, view=com.android.systemui.navigationbar.gestural.SamsungBackPanel{3740d0b G.ED..... ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.android.systemui.navigationbar.gestural.BackPanelController.setLayoutParams:7 com.android.systemui.navigationbar.gestural.EdgeBackGestureHandler.setEdgeBackPlugin:20 
12-22 20:59:09.282  2022  2022 D ViewRootImpl: desktopMode is false
12-22 20:59:09.282  2022  2022 I ViewRootImpl: dVRR is disabled
12-22 20:59:09.287  1423  1423 I ActivityManager: Changes in 10516 5 to 6, 255 to 239
12-22 20:59:09.287  1423  1423 I ActivityManager: Changes in 10515 5 to 6, 255 to 239
12-22 20:59:09.287  1423  1423 I ActivityManager: Changes in 10375 2 to 12, 255 to 0
12-22 20:59:09.287  1423  1423 I ActivityManager: Changes in 10342 2 to 4, 255 to 254
12-22 20:59:09.287  1423  1423 I ActivityManager: Changes in 10155 2 to 5, 255 to 184
12-22 20:59:09.294  1423  2267 W WindowManager: Failed looking up window session=Session{eef2b83 2022:u0a10055} callers=com.android.server.wm.Session.remove:10 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-22 20:59:09.295  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(948 x 1048) immediately
12-22 20:59:09.303  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-22 20:59:09.304  2022  2022 I WindowManager: WindowManagerGlobal#addView, ty=2019, view=com.android.systemui.navigationbar.views.NavigationBarFrame{7824b18 V.E...... ......I. 0,0-0,0 #7f0a083a app:id/navigation_bar_frame}, caller=android.view.WindowManagerImpl.addView:158 com.android.systemui.navigationbar.views.NavigationBar.onInit:237 com.android.systemui.util.ViewController.init:6 
12-22 20:59:09.305  2022  2022 D ViewRootImpl: desktopMode is false
12-22 20:59:09.305  2022  2022 I ViewRootImpl: dVRR is disabled
12-22 20:59:09.319  1423  4812 I WindowManager: Relayout Window{f704bfc u0 com.termux/com.termux.app.TermuxActivity}: viewVisibility=8 req=1080x1260 ty=1 d0
12-22 20:59:09.321  1423  2886 I WindowManager: Relayout Window{64185e1 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}: viewVisibility=8 req=1080x2520 ty=1 d0
12-22 20:59:09.322  1423  4812 I WindowManager: Relayout hash=f704bfc, pid=22381, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} ty=BASE_APPLICATION wanim=0x1030317
12-22 20:59:09.322  1423  4812 I WindowManager:   fl=8d810100
12-22 20:59:09.322  1423  4812 I WindowManager:   pfl=10008040
12-22 20:59:09.322  1423  4812 I WindowManager:   vsysui=700
12-22 20:59:09.322  1423  4812 I WindowManager:   bhv=1
12-22 20:59:09.322  1423  4812 I WindowManager:   fitSides=0
12-22 20:59:09.322  1423  4812 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.322  1423  4812 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.323  1423  2886 I WindowManager: Relayout hash=64185e1, pid=2712, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=nothing} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT wanim=0x1030306
12-22 20:59:09.323  1423  2886 I WindowManager:   fl=81910100
12-22 20:59:09.323  1423  2886 I WindowManager:   pfl=10008840
12-22 20:59:09.323  1423  2886 I WindowManager:   bhv=2
12-22 20:59:09.323  1423  2886 I WindowManager:   fitSides=0
12-22 20:59:09.323  1423  2886 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.323  1423  2886 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=200000 naviIconColor=0}
12-22 20:59:09.346  1423  4808 E WindowManager: win=Window{64185e1 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:192 com.android.server.wm.ActivityClientController.activityStopped:97 android.app.IActivityClientController$Stub.onTransact:726 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1462 android.os.Binder.execTransact:1401 
12-22 20:59:09.349  1423  4808 E WindowManager: win=Window{f704bfc u0 com.termux/com.termux.app.TermuxActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:192 com.android.server.wm.ActivityClientController.activityStopped:97 android.app.IActivityClientController$Stub.onTransact:726 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1462 android.os.Binder.execTransact:1401 
12-22 20:59:09.407  1423  2265 I WindowManager: Relayout Window{6933967 u0 EdgeBackGestureHandler1}: viewVisibility=8 req=164x419 ty=2024 d1
12-22 20:59:09.407  1423  2265 I WindowManager: Relayout hash=6933967, pid=2022, syncId=-1: mAttrs={(0,0)(164x419) sim={adjust=pan} ty=NAVIGATION_BAR_PANEL fmt=TRANSLUCENT
12-22 20:59:09.407  1423  2265 I WindowManager:   fl=1000118
12-22 20:59:09.407  1423  2265 I WindowManager:   pfl=30200010
12-22 20:59:09.407  1423  2265 I WindowManager:   bhv=1
12-22 20:59:09.407  1423  2265 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.407  1423  2265 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.423  2022  2022 I WindowManager: WindowManagerGlobal#addView, ty=2024, view=com.android.systemui.navigationbar.gestural.QuickswitchOrientedNavHandle{2671829 V.E...... ......I. 0,0-0,0 #7f0a0b72 app:id/secondary_home_handle}, caller=android.view.WindowManagerImpl.addView:158 com.android.systemui.navigationbar.views.NavigationBar.initSecondaryHomeHandleForRotation:87 com.android.systemui.navigationbar.views.NavigationBar.onViewAttached:417 
12-22 20:59:09.423  2022  2022 D ViewRootImpl: desktopMode is false
12-22 20:59:09.423  2022  2022 I ViewRootImpl: dVRR is disabled
12-22 20:59:09.427  1423  4808 I WindowManager: Relayout Window{e983ff1 u0 NavigationBar1}: viewVisibility=0 req=1422x75 ty=2019 d1
12-22 20:59:09.428  1423  4808 D WindowManager: makeSurface duration=0 name=NavigationBar1$_2022
12-22 20:59:09.431  1423  4808 I WindowManager: Relayout hash=e983ff1, pid=2022, syncId=-1: mAttrs={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.431  1423  4808 I WindowManager:   fl=20040028
12-22 20:59:09.431  1423  4808 I WindowManager:   pfl=31000000
12-22 20:59:09.431  1423  4808 I WindowManager:   bhv=1
12-22 20:59:09.431  1423  4808 I WindowManager:   providedInsets:
12-22 20:59:09.431  1423  4808 I WindowManager:     InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.431  1423  4808 I WindowManager:     InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:     InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.431  1423  4808 I WindowManager:     InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:     InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.431  1423  4808 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:09.431  1423  4808 I WindowManager:   paramsForRotation:
12-22 20:59:09.431  1423  4808 I WindowManager:     ROTATION_0={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.431  1423  4808 I WindowManager:       fl=20040028
12-22 20:59:09.431  1423  4808 I WindowManager:       pfl=31000000
12-22 20:59:09.431  1423  4808 I WindowManager:       bhv=1
12-22 20:59:09.431  1423  4808 I WindowManager:       providedInsets:
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.431  1423  4808 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.431  1423  4808 I WindowManager:     ROTATION_90={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.431  1423  4808 I WindowManager:       fl=20040028
12-22 20:59:09.431  1423  4808 I WindowManager:       pfl=31000000
12-22 20:59:09.431  1423  4808 I WindowManager:       bhv=1
12-22 20:59:09.431  1423  4808 I WindowManager:       providedInsets:
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=23}}]}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.431  1423  4808 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.431  1423  4808 I WindowManager:     ROTATION_180={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.431  1423  4808 I WindowManager:       fl=20040028
12-22 20:59:09.431  1423  4808 I WindowManager:       pfl=31000000
12-22 20:59:09.431  1423  4808 I WindowManager:       bhv=1
12-22 20:59:09.431  1423  4808 I WindowManager:       providedInsets:
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.431  1423  4808 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.431  1423  4808 I WindowManager:     ROTATION_270={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.431  1423  4808 I WindowManager:       fl=20040028
12-22 20:59:09.431  1423  4808 I WindowManager:       pfl=31000000
12-22 20:59:09.431  1423  4808 I WindowManager:       bhv=1
12-22 20:59:09.431  1423  4808 I WindowManager:       providedInsets:
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=23}}]}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.431  1423  4808 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.431  1423  4808 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0} naviIconColor=0}
12-22 20:59:09.435  1423  1855 D WindowManager: finishDrawingWindow: Window{e983ff1 u0 NavigationBar1} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:09.540  1423  4794 I WindowManager: Relayout Window{e983ff1 u0 NavigationBar1}: viewVisibility=0 req=1422x66 ty=2019 d1
12-22 20:59:09.542  1423  4794 I WindowManager: Relayout hash=e983ff1, pid=2022, syncId=-1: mAttrs={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.542  1423  4794 I WindowManager:   fl=20040028
12-22 20:59:09.542  1423  4794 I WindowManager:   pfl=31000000
12-22 20:59:09.542  1423  4794 I WindowManager:   bhv=1
12-22 20:59:09.542  1423  4794 I WindowManager:   providedInsets:
12-22 20:59:09.542  1423  4794 I WindowManager:     InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=66}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.542  1423  4794 I WindowManager:     InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:     InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.542  1423  4794 I WindowManager:     InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:     InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.542  1423  4794 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:09.542  1423  4794 I WindowManager:   paramsForRotation:
12-22 20:59:09.542  1423  4794 I WindowManager:     ROTATION_0={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.542  1423  4794 I WindowManager:       fl=20040028
12-22 20:59:09.542  1423  4794 I WindowManager:       pfl=31000000
12-22 20:59:09.542  1423  4794 I WindowManager:       bhv=1
12-22 20:59:09.542  1423  4794 I WindowManager:       providedInsets:
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=66}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.542  1423  4794 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.542  1423  4794 I WindowManager:     ROTATION_90={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.542  1423  4794 I WindowManager:       fl=20040028
12-22 20:59:09.542  1423  4794 I WindowManager:       pfl=31000000
12-22 20:59:09.542  1423  4794 I WindowManager:       bhv=1
12-22 20:59:09.542  1423  4794 I WindowManager:       providedInsets:
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=38}}]}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.542  1423  4794 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.542  1423  4794 I WindowManager:     ROTATION_180={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.542  1423  4794 I WindowManager:       fl=20040028
12-22 20:59:09.542  1423  4794 I WindowManager:       pfl=31000000
12-22 20:59:09.542  1423  4794 I WindowManager:       bhv=1
12-22 20:59:09.542  1423  4794 I WindowManager:       providedInsets:
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.542  1423  4794 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.542  1423  4794 I WindowManager:     ROTATION_270={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.542  1423  4794 I WindowManager:       fl=20040028
12-22 20:59:09.542  1423  4794 I WindowManager:       pfl=31000000
12-22 20:59:09.542  1423  4794 I WindowManager:       bhv=1
12-22 20:59:09.542  1423  4794 I WindowManager:       providedInsets:
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=38}}]}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.542  1423  4794 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.542  1423  4794 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0} naviIconColor=0}
12-22 20:59:09.547  1423  1635 I WindowManager: Relayout Window{e733bca u0 SecondaryHomeHandle1}: viewVisibility=8 req=0x0 ty=2024 d1
12-22 20:59:09.547  1423  2933 D WindowManager: finishDrawingWindow: Window{e983ff1 u0 NavigationBar1} mDrawState=READY_TO_SHOW seqId=0
12-22 20:59:09.547  1423  1635 I WindowManager: Relayout hash=e733bca, pid=2022, syncId=-1: mAttrs={(0,0)(0x0) sim={adjust=pan} ty=NAVIGATION_BAR_PANEL fmt=TRANSLUCENT
12-22 20:59:09.547  1423  1635 I WindowManager:   fl=21000138
12-22 20:59:09.547  1423  1635 I WindowManager:   pfl=1040
12-22 20:59:09.547  1423  1635 I WindowManager:   bhv=1
12-22 20:59:09.547  1423  1635 I WindowManager:   fitTypes=206
12-22 20:59:09.547  1423  1635 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.547  1423  1635 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.587  1423  2886 I WindowManager: Relayout Window{41abd5c u0 com.sec.android.app.launcher/com.samsung.app.honeyspace.edge.edgepanel.app.CocktailBarService}: viewVisibility=8 req=0x0 ty=2226 d0
12-22 20:59:09.587  1423  2886 E WindowManager: win=Window{41abd5c u0 com.sec.android.app.launcher/com.samsung.app.honeyspace.edge.edgepanel.app.CocktailBarService} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-22 20:59:09.589  1423  2886 I WindowManager: Relayout hash=41abd5c, pid=2712, syncId=-1: mAttrs={(0,628)(0x0) gr=TOP RIGHT CENTER sim={adjust=nothing} layoutInDisplayCutoutMode=always ty=2226 fmt=TRANSPARENT
12-22 20:59:09.589  1423  2886 I WindowManager:   fl=81800108
12-22 20:59:09.589  1423  2886 I WindowManager:   pfl=10000070
12-22 20:59:09.589  1423  2886 I WindowManager:   apr=10
12-22 20:59:09.589  1423  2886 I WindowManager:   bhv=1
12-22 20:59:09.589  1423  2886 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.589  1423  2886 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:09.589  1423  2886 I WindowManager:  dimAmount=0.0 dimDuration=0 naviIconColor=0
12-22 20:59:09.589  1423  2886 I WindowManager:   sfl=420000}
12-22 20:59:09.591  1423  4807 D WindowManager: Transition is created, t=TransitionRecord{3c4fc43 id=-1 type=TO_FRONT flags=0x8000}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createTransition:17 com.android.server.wm.TransitionController.requestTransitionIfNeeded:48 com.android.server.wm.KeyguardController.setKeyguardShown:257 com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda13.accept:13 
12-22 20:59:09.593  1423  4807 D WindowManager: Create SleepToken: tag=keyguard, displayId=0
12-22 20:59:09.593  1423  1754 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-22 20:59:09.598  1423  1754 W InputMethodManagerService: callbackImeWindowStatus: skipped topFocusedDisplayId=1, curTokenDisplayId=0
12-22 20:59:09.598  1423  1754 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-22 20:59:09.601  1423  1754 W InputMethodManagerService: callbackImeWindowStatus: skipped topFocusedDisplayId=1, curTokenDisplayId=0
12-22 20:59:09.601  1423  1473 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=6146b28 NavigationBar0)/@0x1b76ee0 - animation-leash of insets_animation)/@0x7d1fda5
12-22 20:59:09.601  1423  1473 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{abeb61a type=2019 android.os.BinderProxy@2cf173c})/@0x22a599, destroy=false, surface=Surface(name=6146b28 NavigationBar0)/@0x1b76ee0
12-22 20:59:09.601  1423  1473 I WindowManager: Reparenting to leash, surface=Surface(name=6146b28 NavigationBar0)/@0x1b76ee0, leashParent=Surface(name=WindowToken{abeb61a type=2019 android.os.BinderProxy@2cf173c})/@0x22a599
12-22 20:59:09.601  1423  1473 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=6146b28 NavigationBar0)/@0x1b76ee0 - animation-leash of insets_animation)/@0x1073eb5
12-22 20:59:09.606  1423  1473 D WindowManager: Transition is created, t=TransitionRecord{3879bf0 id=-1 type=TO_FRONT flags=0x800}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createTransition:17 com.android.server.wm.TransitionController.requestTransitionIfNeeded:48 com.android.server.wm.KeyguardController.setKeyguardShown:238 com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda13.accept:13 
12-22 20:59:09.608  1423  1754 W InputMethodManagerService: callbackImeWindowStatus: skipped topFocusedDisplayId=1, curTokenDisplayId=0
12-22 20:59:09.609  1423  1754 W InputMethodManagerService: callbackImeWindowStatus: skipped topFocusedDisplayId=1, curTokenDisplayId=0
12-22 20:59:09.624  1423  1555 I ActivityManager: Changes in 10362 16 to 11, 0 to 128
12-22 20:59:09.633  1423  1474 I ActivityManager: Changes in 10362 11 to 16, 128 to 0
12-22 20:59:09.655  1423  2886 I WindowManager: Relayout Window{b5eb294 u0 NotificationShade}: viewVisibility=0 req=1080x2520 ty=2040 d0
12-22 20:59:09.655  1423  2886 D WindowManager: makeSurface duration=1 name=NotificationShade$_2022
12-22 20:59:09.657  1423  2886 I WindowManager: Relayout hash=b5eb294, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NOTIFICATION_SHADE fmt=TRANSLUCENT or=SCREEN_ORIENTATION_NOSENSOR if=INPUT_FEATURE_DISABLE_USER_ACTIVITY userActivityTimeout=5000
12-22 20:59:09.657  1423  2886 I WindowManager:   fl=81160040
12-22 20:59:09.657  1423  2886 I WindowManager:   pfl=10080200
12-22 20:59:09.657  1423  2886 I WindowManager:   bhv=2
12-22 20:59:09.657  1423  2886 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.657  1423  2886 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=0 naviIconColor=0
12-22 20:59:09.657  1423  2886 I WindowManager:   sfl=40000}
12-22 20:59:09.660  1423  2266 I WindowManager: Relayout Window{9f6c9d1 u0 ThumbsUpHandler_L}: viewVisibility=8 req=41x2520 ty=2601 d0
12-22 20:59:09.660  1423  2266 E WindowManager: win=Window{9f6c9d1 u0 ThumbsUpHandler_L} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-22 20:59:09.662  1423  2266 I WindowManager: Relayout hash=9f6c9d1, pid=6554, syncId=-1: mAttrs={(0,0)(41x2520) gr=TOP LEFT CENTER sim={state=hidden adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=2601 fmt=TRANSLUCENT alpha=0.0
12-22 20:59:09.662  1423  2266 I WindowManager:   fl=5800528
12-22 20:59:09.662  1423  2266 I WindowManager:   pfl=10000050
12-22 20:59:09.662  1423  2266 I WindowManager:   vsysui=500
12-22 20:59:09.662  1423  2266 I WindowManager:   bhv=2
12-22 20:59:09.662  1423  2266 I WindowManager:   fitTypes=207
12-22 20:59:09.662  1423  2266 I WindowManager:   fitSides=0
12-22 20:59:09.662  1423  2266 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.662  1423  2266 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:09.662  1423  2266 I WindowManager:   sfl=20000}
12-22 20:59:09.667  1423  1555 I ActivityManager: Changes in 10176 19 to 11, 0 to 128
12-22 20:59:09.672  1423  1474 I WindowManager: Relayout Window{7aff3c3 u0 ThumbsUpHandler_R}: viewVisibility=8 req=41x2520 ty=2601 d0
12-22 20:59:09.672  1423  1474 E WindowManager: win=Window{7aff3c3 u0 ThumbsUpHandler_R} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
12-22 20:59:09.673  1423  1474 I WindowManager: Relayout hash=7aff3c3, pid=6554, syncId=-1: mAttrs={(0,0)(41x2520) gr=TOP RIGHT CENTER sim={state=hidden adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=2601 fmt=TRANSLUCENT alpha=0.0
12-22 20:59:09.673  1423  1474 I WindowManager:   fl=5800528
12-22 20:59:09.673  1423  1474 I WindowManager:   pfl=10000050
12-22 20:59:09.673  1423  1474 I WindowManager:   vsysui=500
12-22 20:59:09.673  1423  1474 I WindowManager:   bhv=2
12-22 20:59:09.673  1423  1474 I WindowManager:   fitTypes=207
12-22 20:59:09.673  1423  1474 I WindowManager:   fitSides=0
12-22 20:59:09.673  1423  1474 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.673  1423  1474 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:09.673  1423  1474 I WindowManager:   sfl=20000}
12-22 20:59:09.681  1423  2229 D WindowManager: finishDrawingWindow: Window{b5eb294 u0 NotificationShade} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:09.684  1423  2267 I WindowManager: Relayout Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}: viewVisibility=8 req=1422x1500 ty=1 d1
12-22 20:59:09.684  1423  2267 I WindowManager: Relayout hash=9a3d1c6, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=pan} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY userActivityTimeout=30000
12-22 20:59:09.684  1423  2267 I WindowManager:   fl=81810100
12-22 20:59:09.684  1423  2267 I WindowManager:   pfl=10008850
12-22 20:59:09.684  1423  2267 I WindowManager:   vsysui=1600000
12-22 20:59:09.684  1423  2267 I WindowManager:   bhv=1
12-22 20:59:09.684  1423  2267 I WindowManager:   fitSides=0
12-22 20:59:09.684  1423  2267 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.684  1423  2267 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=0 naviIconColor=0}
12-22 20:59:09.685  1423  2934 I ActivityManager: Changes in 10176 11 to 19, 128 to 0
12-22 20:59:09.685  1423  2886 I WindowManager: Relayout Window{4ad78ec u0 Bouncer}: viewVisibility=4 req=1080x0 ty=2009 d0
12-22 20:59:09.685  1423  2886 I WindowManager: Relayout hash=4ad78ec, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP CENTER_VERTICAL sim={adjust=resize} layoutInDisplayCutoutMode=shortEdges ty=KEYGUARD_DIALOG fmt=TRANSLUCENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY userActivityTimeout=10000
12-22 20:59:09.685  1423  2886 I WindowManager:   fl=85000548
12-22 20:59:09.685  1423  2886 I WindowManager:   pfl=10000000
12-22 20:59:09.685  1423  2886 I WindowManager:   vsysui=500
12-22 20:59:09.685  1423  2886 I WindowManager:   bhv=2
12-22 20:59:09.685  1423  2886 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.685  1423  2886 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:09.685  1423  2886 I WindowManager:  dimAmount=0.0 screenDimDuration=0 naviIconColor=0
12-22 20:59:09.685  1423  2886 I WindowManager:   sfl=40000}
12-22 20:59:09.686  1423  2886 I InputMethodManagerService: isInputMethodShown: isShown=true
12-22 20:59:09.690  1423  2886 I InputMethodManagerService: isInputMethodShown: isShown=true
12-22 20:59:09.691  1423  4808 I WindowManager: Relayout Window{e983ff1 u0 NavigationBar1}: viewVisibility=0 req=1422x66 ty=2019 d1
12-22 20:59:09.692  1423  2934 I WindowManager: Relayout Window{6146b28 u0 NavigationBar0}: viewVisibility=0 req=1080x75 ty=2019 d0
12-22 20:59:09.692  1423  4808 I WindowManager: Relayout hash=e983ff1, pid=2022, syncId=-1: mAttrs={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.692  1423  4808 I WindowManager:   fl=20040028
12-22 20:59:09.692  1423  4808 I WindowManager:   pfl=31000000
12-22 20:59:09.692  1423  4808 I WindowManager:   bhv=1
12-22 20:59:09.692  1423  4808 I WindowManager:   providedInsets:
12-22 20:59:09.692  1423  4808 I WindowManager:     InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=66}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.692  1423  4808 I WindowManager:     InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:     InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.692  1423  4808 I WindowManager:     InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:     InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.692  1423  4808 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:09.692  1423  4808 I WindowManager:   paramsForRotation:
12-22 20:59:09.692  1423  4808 I WindowManager:     ROTATION_0={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.692  1423  4808 I WindowManager:       fl=20040028
12-22 20:59:09.692  1423  4808 I WindowManager:       pfl=31000000
12-22 20:59:09.692  1423  4808 I WindowManager:       bhv=1
12-22 20:59:09.692  1423  4808 I WindowManager:       providedInsets:
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=66}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.692  1423  4808 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.692  1423  4808 I WindowManager:     ROTATION_90={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.692  1423  4808 I WindowManager:       fl=20040028
12-22 20:59:09.692  1423  4808 I WindowManager:       pfl=31000000
12-22 20:59:09.692  1423  4808 I WindowManager:       bhv=1
12-22 20:59:09.692  1423  4808 I WindowManager:       providedInsets:
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=38}}]}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.692  1423  4808 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.692  1423  4808 I WindowManager:     ROTATION_180={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.692  1423  4808 I WindowManager:       fl=20040028
12-22 20:59:09.692  1423  4808 I WindowManager:       pfl=31000000
12-22 20:59:09.692  1423  4808 I WindowManager:       bhv=1
12-22 20:59:09.692  1423  4808 I WindowManager:       providedInsets:
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.692  1423  4808 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.692  1423  4808 I WindowManager:     ROTATION_270={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.692  1423  4808 I WindowManager:       fl=20040028
12-22 20:59:09.692  1423  4808 I WindowManager:       pfl=31000000
12-22 20:59:09.692  1423  4808 I WindowManager:       bhv=1
12-22 20:59:09.692  1423  4808 I WindowManager:       providedInsets:
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=38}}]}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.692  1423  4808 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.692  1423  4808 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0} naviIconColor=0}
12-22 20:59:09.698  1423  1604 D WindowManager: Remove SleepToken: tag=Display-off, displayId=1
12-22 20:59:09.699  1423  1604 D WindowManager: Transition is created, t=TransitionRecord{4df3957 id=-1 type=WAKE flags=0x1000}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createTransition:17 com.android.server.wm.RootWindowContainer.applySleepTokens:209 com.android.server.wm.ActivityTaskManagerService.updateSleepIfNeededLocked:90 com.android.server.wm.RootWindowContainer.removeSleepToken:131 
12-22 20:59:09.700  1423  1604 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}
12-22 20:59:09.701  1423  1604 I WindowManager: getCoverScreenRotation 0, sensor= 0, isFlat=false, isDual=false
12-22 20:59:09.702  1423  1604 I WindowManager: Waiting for transition
12-22 20:59:09.703  1423  1754 W InputMethodManagerService: callbackImeWindowStatus: skipped topFocusedDisplayId=1, curTokenDisplayId=0
12-22 20:59:09.704  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.705  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.706  1423  2934 I WindowManager: Relayout hash=6146b28, pid=0, syncId=-1: mAttrs={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.706  1423  2934 I WindowManager:   fl=20040028
12-22 20:59:09.706  1423  2934 I WindowManager:   pfl=31001000
12-22 20:59:09.706  1423  2934 I WindowManager:   bhv=1
12-22 20:59:09.706  1423  2934 I WindowManager:   providedInsets:
12-22 20:59:09.706  1423  2934 I WindowManager:     InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.706  1423  2934 I WindowManager:     InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:     InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.706  1423  2934 I WindowManager:     InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:     InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.706  1423  2934 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:09.706  1423  2934 I WindowManager:   paramsForRotation:
12-22 20:59:09.706  1423  2934 I WindowManager:     ROTATION_0={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.706  1423  2934 I WindowManager:       fl=20040028
12-22 20:59:09.706  1423  2934 I WindowManager:       pfl=31001000
12-22 20:59:09.706  1423  2934 I WindowManager:       bhv=1
12-22 20:59:09.706  1423  2934 I WindowManager:       providedInsets:
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.706  1423  2934 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.706  1423  2934 I WindowManager:     ROTATION_90={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.706  1423  2934 I WindowManager:       fl=20040028
12-22 20:59:09.706  1423  2934 I WindowManager:       pfl=31001000
12-22 20:59:09.706  1423  2934 I WindowManager:       bhv=1
12-22 20:59:09.706  1423  2934 I WindowManager:       providedInsets:
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=23}}]}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.706  1423  2934 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.706  1423  2934 I WindowManager:     ROTATION_180={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.706  1423  2934 I WindowManager:       fl=20040028
12-22 20:59:09.706  1423  2934 I WindowManager:       pfl=31001000
12-22 20:59:09.706  1423  2934 I WindowManager:       bhv=1
12-22 20:59:09.706  1423  2934 I WindowManager:       providedInsets:
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.706  1423  2934 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.706  1423  2934 I WindowManager:     ROTATION_270={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.706  1423  2934 I WindowManager:       fl=20040028
12-22 20:59:09.706  1423  2934 I WindowManager:       pfl=31001000
12-22 20:59:09.706  1423  2934 I WindowManager:       bhv=1
12-22 20:59:09.706  1423  2934 I WindowManager:       providedInsets:
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=23}}]}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.706  1423  2934 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.706  1423  2934 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0} naviIconColor=0}
12-22 20:59:09.706  1423  2934 D WindowManager: finishDrawingWindow: Window{e983ff1 u0 NavigationBar1} mDrawState=READY_TO_SHOW seqId=0
12-22 20:59:09.707  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.714  1423  1423 I ActivityManager: Changes in 10516 6 to 5, 239 to 255
12-22 20:59:09.714  1423  1423 I ActivityManager: Changes in 10515 6 to 5, 239 to 255
12-22 20:59:09.714  1423  1423 I ActivityManager: Changes in 10375 12 to 2, 0 to 255
12-22 20:59:09.714  1423  1423 D InputDispatcher: Inject key (0/0): action=0, f=0x8, d=-1, time=33709295782000
12-22 20:59:09.720  1423  2934 I WindowManager: Relayout Window{b5eb294 u0 NotificationShade}: viewVisibility=0 req=1080x2520 ty=2040 d0
12-22 20:59:09.721  1423  2934 I WindowManager: Waiting for transition
12-22 20:59:09.721  1423  2934 I WindowManager: Relayout hash=b5eb294, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NOTIFICATION_SHADE fmt=TRANSLUCENT or=SCREEN_ORIENTATION_NOSENSOR if=INPUT_FEATURE_DISABLE_USER_ACTIVITY userActivityTimeout=5000
12-22 20:59:09.721  1423  2934 I WindowManager:   fl=81060040
12-22 20:59:09.721  1423  2934 I WindowManager:   pfl=10080200
12-22 20:59:09.721  1423  2934 I WindowManager:   bhv=2
12-22 20:59:09.721  1423  2934 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.721  1423  2934 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=0 naviIconColor=0
12-22 20:59:09.721  1423  2934 I WindowManager:   sfl=40000}
12-22 20:59:09.728  1423  2229 I InputMethodManagerService: isInputMethodShown: isShown=true
12-22 20:59:09.728  1423  1636 I InputMethodManagerService: fhsi
12-22 20:59:09.730  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.732  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.733  1423  1555 I ActivityManager: Changes in 10329 8 to 8, 0 to 128
12-22 20:59:09.736  1423  1555 I ActivityManager: Changes in 5006 8 to 8, 0 to 128
12-22 20:59:09.738  1423  2267 I WindowManager: Waiting for transition
12-22 20:59:09.738  1423  2267 I WindowManager: Waiting for transition
12-22 20:59:09.739  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.740  1423  1549 I ActivityManager: Changes in 10332 5 to 7, 255 to 128
12-22 20:59:09.740  1423  1549 I ActivityManager: Changes in 10122 5 to 7, 184 to 128
12-22 20:59:09.741  1423  1754 W InputMethodManagerService: callbackImeWindowStatus: skipped topFocusedDisplayId=1, curTokenDisplayId=0
12-22 20:59:09.742  1423  2886 I ActivityManager: Changes in 5006 8 to 8, 128 to 0
12-22 20:59:09.743  1423  1754 W InputMethodManagerService: callbackImeWindowStatus: skipped topFocusedDisplayId=1, curTokenDisplayId=0
12-22 20:59:09.743  1423  2227 W InputMethodManagerService: callbackImeWindowStatus: skipped topFocusedDisplayId=1, curTokenDisplayId=0
12-22 20:59:09.743  1423  2227 I InputMethodManagerService: setImeWindowStatus: vis=0, backDisposition=0
12-22 20:59:09.751  1423  2934 I InputMethodManagerService: isInputMethodShown: isShown=false
12-22 20:59:09.755  1423  2934 I WindowManager: Relayout Window{f748235 u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-22 20:59:09.756  1423  2934 I WindowManager: Waiting for transition
12-22 20:59:09.756  1423  2934 I WindowManager: Relayout hash=f748235, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-22 20:59:09.756  1423  2934 I WindowManager:   fl=81800108
12-22 20:59:09.756  1423  2934 I WindowManager:   pfl=14000000
12-22 20:59:09.756  1423  2934 I WindowManager:   bhv=1
12-22 20:59:09.756  1423  2934 I WindowManager:   fitTypes=3
12-22 20:59:09.756  1423  2934 I WindowManager:   fitSides=7
12-22 20:59:09.756  1423  2934 I WindowManager:   fitIgnoreVis
12-22 20:59:09.756  1423  2934 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:09.756  1423  2934 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-22 20:59:09.756  1423  2934 D WindowManager: setInsetsWindow Window{f748235 u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-22 20:59:09.757  1423  2934 I WindowManager: Waiting for transition
12-22 20:59:09.768  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(948 x 1048) immediately
12-22 20:59:09.770  1423  1547 I WindowManager: Waiting for transition
12-22 20:59:09.773  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.773  1423  1555 I ActivityManager: Changes in 10373 16 to 11, 0 to 128
12-22 20:59:09.776  1423  2267 E ActivityManager: Sending non-protected broadcast com.samsung.android.app.aodservice.ACTION_COVER_HOME_FOCUS_CHANGED from system 2022:com.android.systemui/u0a55 pkg com.android.systemui
12-22 20:59:09.776  1423  2267 E ActivityManager: java.lang.Throwable
12-22 20:59:09.776  1423  2267 E ActivityManager: 	at com.android.server.am.BroadcastController.checkBroadcastFromSystem(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:252)
12-22 20:59:09.776  1423  2267 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLockedTraced(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:344)
12-22 20:59:09.776  1423  2267 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLocked(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:18)
12-22 20:59:09.776  1423  2267 E ActivityManager: 	at com.android.server.am.ActivityManagerService.broadcastIntentWithFeature(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:149)
12-22 20:59:09.776  1423  2267 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact$broadcastIntentWithFeature$(IActivityManager.java:12241)
12-22 20:59:09.776  1423  2267 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact(IActivityManager.java:3154)
12-22 20:59:09.776  1423  2267 E ActivityManager: 	at com.android.server.am.ActivityManagerService.onTransact(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:425)
12-22 20:59:09.776  1423  2267 E ActivityManager: 	at android.os.Binder.execTransactInternal(Binder.java:1462)
12-22 20:59:09.776  1423  2267 E ActivityManager: 	at android.os.Binder.execTransact(Binder.java:1401)
12-22 20:59:09.777  1423  2229 E ActivityManager: Sending non-protected broadcast com.samsung.android.app.aodservice.ACTION_FLOATING_SHORTCUT_AVAILABLE_CHANGED from system 2022:com.android.systemui/u0a55 pkg com.android.systemui
12-22 20:59:09.777  1423  2229 E ActivityManager: java.lang.Throwable
12-22 20:59:09.777  1423  2229 E ActivityManager: 	at com.android.server.am.BroadcastController.checkBroadcastFromSystem(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:252)
12-22 20:59:09.777  1423  2229 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLockedTraced(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:344)
12-22 20:59:09.777  1423  2229 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLocked(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:18)
12-22 20:59:09.777  1423  2229 E ActivityManager: 	at com.android.server.am.ActivityManagerService.broadcastIntentWithFeature(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:149)
12-22 20:59:09.777  1423  2229 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact$broadcastIntentWithFeature$(IActivityManager.java:12241)
12-22 20:59:09.777  1423  2229 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact(IActivityManager.java:3154)
12-22 20:59:09.777  1423  2229 E ActivityManager: 	at com.android.server.am.ActivityManagerService.onTransact(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:425)
12-22 20:59:09.777  1423  2229 E ActivityManager: 	at android.os.Binder.execTransactInternal(Binder.java:1462)
12-22 20:59:09.777  1423  2229 E ActivityManager: 	at android.os.Binder.execTransact(Binder.java:1401)
12-22 20:59:09.778  1423  2886 E ActivityManager: Sending non-protected broadcast com.samsung.android.app.aodservice.ACTION_COVER_HOME_QUICK_PANEL_TOUCH_AREA_CHANGED from system 2022:com.android.systemui/u0a55 pkg com.android.systemui
12-22 20:59:09.778  1423  2886 E ActivityManager: java.lang.Throwable
12-22 20:59:09.778  1423  2886 E ActivityManager: 	at com.android.server.am.BroadcastController.checkBroadcastFromSystem(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:252)
12-22 20:59:09.778  1423  2886 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLockedTraced(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:344)
12-22 20:59:09.778  1423  2886 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLocked(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:18)
12-22 20:59:09.778  1423  2886 E ActivityManager: 	at com.android.server.am.ActivityManagerService.broadcastIntentWithFeature(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:149)
12-22 20:59:09.778  1423  2886 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact$broadcastIntentWithFeature$(IActivityManager.java:12241)
12-22 20:59:09.778  1423  2886 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact(IActivityManager.java:3154)
12-22 20:59:09.778  1423  2886 E ActivityManager: 	at com.android.server.am.ActivityManagerService.onTransact(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:425)
12-22 20:59:09.778  1423  2886 E ActivityManager: 	at android.os.Binder.execTransactInternal(Binder.java:1462)
12-22 20:59:09.778  1423  2886 E ActivityManager: 	at android.os.Binder.execTransact(Binder.java:1401)
12-22 20:59:09.782  1423  2266 I ActivityManager: Changes in 10373 11 to 16, 128 to 0
12-22 20:59:09.796  1423  1555 I ActivityManager: Changes in 10113 10 to 10, 0 to 128
12-22 20:59:09.800  7845  7845 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{dfd1384 VFE...C.. ........ 0,0-63,63}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.quadrantlauncher.FloatingLauncherService.performDisplayChange:1054 
12-22 20:59:09.801  1423  2227 I ActivityManager: Changes in 10329 8 to 8, 128 to 0
12-22 20:59:09.805 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{c47052b VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.setupUI:790 
12-22 20:59:09.806  1423  1474 I WindowManager: Relayout Window{b5eb294 u0 NotificationShade}: viewVisibility=0 req=1080x2520 ty=2040 d0
12-22 20:59:09.806 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{d15ea34 V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.setupUI:791 
12-22 20:59:09.807  1423  1474 I WindowManager: Waiting for transition
12-22 20:59:09.807  1423  1474 I WindowManager: Relayout hash=b5eb294, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NOTIFICATION_SHADE fmt=TRANSLUCENT or=SCREEN_ORIENTATION_NOSENSOR if=INPUT_FEATURE_DISABLE_USER_ACTIVITY userActivityTimeout=5000
12-22 20:59:09.807  1423  1474 I WindowManager:   fl=81160040
12-22 20:59:09.807  1423  1474 I WindowManager:   pfl=10000200
12-22 20:59:09.807  1423  1474 I WindowManager:   bhv=2
12-22 20:59:09.807  1423  1474 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.807  1423  1474 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=0 naviIconColor=0
12-22 20:59:09.807  1423  1474 I WindowManager:   sfl=40000}
12-22 20:59:09.809 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{5dca76c G.E...... ......ID 0,0-527,444}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.setupUI:794 
12-22 20:59:09.814  7845  7845 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{d4e1542 VFE...C.. ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.quadrantlauncher.FloatingLauncherService.setupBubble:607 com.example.quadrantlauncher.FloatingLauncherService.performDisplayChange:1071 
12-22 20:59:09.814  7845  7845 D ViewRootImpl: desktopMode is false
12-22 20:59:09.815  7845  7845 I ViewRootImpl: dVRR is disabled
12-22 20:59:09.815  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.815  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.822 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{1b6ce99 G.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.setupTrackpad:938 com.example.coverscreentester.OverlayService.setupUI:822 
12-22 20:59:09.823 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:09.823 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:09.825  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.826  1423  1555 I ActivityManager: Changes in 10100 19 to 11, 0 to 128
12-22 20:59:09.830  1423  4808 D WindowManager: finishDrawingWindow: Window{b5eb294 u0 NotificationShade} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:09.830  1423  4812 I WindowManager: Relayout Window{e382cc2 u0 SubLauncherWindow}: viewVisibility=0 req=1422x1500 ty=1000 d1
12-22 20:59:09.830  1423  4812 D WindowManager: makeSurface duration=0 name=SubLauncherWindow$_2022
12-22 20:59:09.831  1423  4812 I WindowManager: Waiting for transition
12-22 20:59:09.831  1423  4812 I WindowManager: Relayout hash=e382cc2, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=pan} layoutInDisplayCutoutMode=always ty=APPLICATION_PANEL fmt=TRANSLUCENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY
12-22 20:59:09.831  1423  4812 I WindowManager:   fl=81080278
12-22 20:59:09.831  1423  4812 I WindowManager:   vsysui=1600400
12-22 20:59:09.831  1423  4812 I WindowManager:   bhv=1
12-22 20:59:09.831  1423  4812 I WindowManager:   fitTypes=206
12-22 20:59:09.831  1423  4812 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.831  1423  4812 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.832  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.834  1423  2230 I ActivityManager: Changes in 10100 11 to 19, 128 to 0
12-22 20:59:09.837  1423  2230 I WindowManager: Waiting for transition
12-22 20:59:09.839  1423  2267 I WindowManager: Relayout Window{2a89570 u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=63x63 ty=2032 d1
12-22 20:59:09.839  1423  2267 D WindowManager: makeSurface duration=0 name=$_7845
12-22 20:59:09.840  1423  2267 I WindowManager: Waiting for transition
12-22 20:59:09.841 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{dbb9009 VFE...C.. ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.setupBubble:903 com.example.coverscreentester.OverlayService.setupUI:825 
12-22 20:59:09.841 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:09.842 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:09.843  1423  2267 I WindowManager: Relayout hash=2a89570, pid=7845, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:09.843  1423  2267 I WindowManager:   fl=1040308
12-22 20:59:09.843  1423  2267 I WindowManager:   bhv=1
12-22 20:59:09.843  1423  2267 I WindowManager:   fitTypes=206
12-22 20:59:09.843  1423  2267 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.843  1423  2267 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.844  1423  4808 I WindowManager: Relayout Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}: viewVisibility=0 req=1422x1500 ty=1 d1
12-22 20:59:09.844  1423  4808 D WindowManager: makeSurface duration=1 name=com.android.systemui/com.android.systemui.subscreen.SubHomeActivity$_2022
12-22 20:59:09.845  1423  4808 I WindowManager: Waiting for transition
12-22 20:59:09.845  1423  4808 D WindowManager: Changing focus from null to Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity} displayId=1 Callers=com.android.server.wm.WindowManagerService.relayoutWindow:1359 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 
12-22 20:59:09.845  1423  4808 I WindowManager: Reparenting to leash, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc, leashParent=Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785
12-22 20:59:09.846  1423  4808 D WindowManager: makeSurface duration=2 leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0xa7dc9da
12-22 20:59:09.847  1423  4808 I WindowManager: Relayout hash=9a3d1c6, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY userActivityTimeout=30000
12-22 20:59:09.847  1423  4808 I WindowManager:   fl=81910100
12-22 20:59:09.847  1423  4808 I WindowManager:   pfl=10008850
12-22 20:59:09.847  1423  4808 I WindowManager:   vsysui=1600000
12-22 20:59:09.847  1423  4808 I WindowManager:   bhv=1
12-22 20:59:09.847  1423  4808 I WindowManager:   fitSides=0
12-22 20:59:09.847  1423  4808 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.847  1423  4808 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=0 naviIconColor=0}
12-22 20:59:09.847  1423  2230 D WindowManager: finishDrawingWindow: Window{e382cc2 u0 SubLauncherWindow} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:09.849 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{c87711a V.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.setupCursor:957 com.example.coverscreentester.OverlayService.setupUI:826 
12-22 20:59:09.849  1423  1550 W WindowManager: Don't apply multiCrop on extra display
12-22 20:59:09.849 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:09.850 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:09.850  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.850  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.854 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{dbb9009 VFE...C.. ......I. 0,0-0,0}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.setupUI:790 
12-22 20:59:09.854  1423  1474 I ActivityManager: Changes in 10113 10 to 10, 128 to 0
12-22 20:59:09.856 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{c87711a V.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.setupUI:791 
12-22 20:59:09.858 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{1b6ce99 G.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.setupUI:794 
12-22 20:59:09.863 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{f60067d G.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.setupTrackpad:938 com.example.coverscreentester.OverlayService.setupUI:822 
12-22 20:59:09.864 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:09.864 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:09.867  1423  4812 D InputDispatcher: Once focus requested (1): 9a3d1c6 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity
12-22 20:59:09.867  1423  4812 D InputDispatcher: Focus request (1): 9a3d1c6 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity but waiting because NO_WINDOW
12-22 20:59:09.869  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:09.869  1423  1636 D WindowManager: finishDrawingWindow: Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:09.878  1423  1550 D WindowManager: All windows drawn!
12-22 20:59:09.883  1423  2267 D WindowManager: finishDrawingWindow: Window{2a89570 u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:09.884 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{cfc4abe VFE...C.. ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.setupBubble:903 com.example.coverscreentester.OverlayService.setupUI:825 
12-22 20:59:09.885 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:09.885 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:09.897 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{ae54ca V.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.setupCursor:957 com.example.coverscreentester.OverlayService.setupUI:826 
12-22 20:59:09.897 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:09.898 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:09.899  1423  1423 D InputDispatcher: Inject key (0/0): action=1, f=0x8, d=-1, time=33709502637000
12-22 20:59:09.905  1423  2886 W WindowManager: Failed looking up window session=Session{9d5656e 28700:u0a10515} callers=com.android.server.wm.WindowManagerService.windowForClientLocked:1 com.android.server.wm.Session.setOnBackInvokedCallbackInfo:15 android.view.IWindowSession$Stub.onTransact:1390 
12-22 20:59:09.905  1423  2886 I WindowManager: setOnBackInvokedCallback(): No window state for package:com.katsuyamaki.DroidOSTrackpadKeyboard
12-22 20:59:09.910  1423  2886 D WindowManager: finishDrawingWindow: Window{2a89570 u0 com.katsuyamaki.DroidOSLauncher} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:09.911  1423  2267 W WindowManager: Failed looking up window session=Session{9d5656e 28700:u0a10515} callers=com.android.server.wm.WindowManagerService.windowForClientLocked:1 com.android.server.wm.Session.setOnBackInvokedCallbackInfo:15 android.view.IWindowSession$Stub.onTransact:1390 
12-22 20:59:09.911  1423  2267 I WindowManager: setOnBackInvokedCallback(): No window state for package:com.katsuyamaki.DroidOSTrackpadKeyboard
12-22 20:59:09.918  1423  1474 D InputDispatcher: Focus entered window (1): 9a3d1c6 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity
12-22 20:59:09.933  1423  1555 I ActivityManager: Changes in 10176 19 to 11, 0 to 128
12-22 20:59:09.934  1423  4808 I InputMethodManagerService: isInputMethodShown: isShown=false
12-22 20:59:09.936  1423  1474 I WindowManager: Relayout Window{e983ff1 u0 NavigationBar1}: viewVisibility=0 req=1422x66 ty=2019 d1
12-22 20:59:09.936  1423  2934 I WindowManager: Relayout Window{e30915d u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=8 req=681x551 ty=2032 d1
12-22 20:59:09.937  1423  1474 I WindowManager: Relayout hash=e983ff1, pid=2022, syncId=-1: mAttrs={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.937  1423  1474 I WindowManager:   fl=20040028
12-22 20:59:09.937  1423  1474 I WindowManager:   pfl=31000000
12-22 20:59:09.937  1423  1474 I WindowManager:   bhv=1
12-22 20:59:09.937  1423  1474 I WindowManager:   providedInsets:
12-22 20:59:09.937  1423  1474 I WindowManager:     InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=66}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.937  1423  1474 I WindowManager:     InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:     InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.937  1423  1474 I WindowManager:     InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:     InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.937  1423  1474 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:09.937  1423  1474 I WindowManager:   paramsForRotation:
12-22 20:59:09.937  1423  1474 I WindowManager:     ROTATION_0={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.937  1423  1474 I WindowManager:       fl=20040028
12-22 20:59:09.937  1423  1474 I WindowManager:       pfl=31000000
12-22 20:59:09.937  1423  1474 I WindowManager:       bhv=1
12-22 20:59:09.937  1423  1474 I WindowManager:       providedInsets:
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=66}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.937  1423  1474 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.937  1423  1474 I WindowManager:     ROTATION_90={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.937  1423  1474 I WindowManager:       fl=20040028
12-22 20:59:09.937  1423  1474 I WindowManager:       pfl=31000000
12-22 20:59:09.937  1423  1474 I WindowManager:       bhv=1
12-22 20:59:09.937  1423  1474 I WindowManager:       providedInsets:
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=38}}]}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.937  1423  1474 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.937  1423  1474 I WindowManager:     ROTATION_180={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.937  1423  1474 I WindowManager:       fl=20040028
12-22 20:59:09.937  1423  1474 I WindowManager:       pfl=31000000
12-22 20:59:09.937  1423  1474 I WindowManager:       bhv=1
12-22 20:59:09.937  1423  1474 I WindowManager:       providedInsets:
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.937  1423  1474 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.937  1423  1474 I WindowManager:     ROTATION_270={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:09.937  1423  1474 I WindowManager:       fl=20040028
12-22 20:59:09.937  1423  1474 I WindowManager:       pfl=31000000
12-22 20:59:09.937  1423  1474 I WindowManager:       bhv=1
12-22 20:59:09.937  1423  1474 I WindowManager:       providedInsets:
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=38}}]}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:09.937  1423  1474 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:09.937  1423  1474 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0} naviIconColor=0}
12-22 20:59:09.937  1423  2934 I WindowManager: Relayout hash=e30915d, pid=28700, syncId=-1: mAttrs={(683,640)(681x551) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:09.937  1423  2934 I WindowManager:   fl=1040228
12-22 20:59:09.937  1423  2934 I WindowManager:   pfl=40000000
12-22 20:59:09.937  1423  2934 I WindowManager:   bhv=1
12-22 20:59:09.937  1423  2934 I WindowManager:   fitTypes=207
12-22 20:59:09.937  1423  2934 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.937  1423  2934 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.938  1423  2230 I ActivityManager: Changes in 10176 11 to 19, 128 to 0
12-22 20:59:09.938  1423  2230 I WindowManager: Relayout Window{a5d8bcd u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=93x93 ty=2032 d1
12-22 20:59:09.938  1423  2230 D WindowManager: makeSurface duration=1 name=$_28700
12-22 20:59:09.939  1423  1555 I ActivityManager: Changes in 10329 8 to 8, 0 to 128
12-22 20:59:09.939  1423  2230 I WindowManager: Relayout hash=a5d8bcd, pid=28700, syncId=-1: mAttrs={(95,1058)(93x93) gr=TOP START CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:09.939  1423  2230 I WindowManager:   fl=1000308
12-22 20:59:09.939  1423  2230 I WindowManager:   bhv=1
12-22 20:59:09.939  1423  2230 I WindowManager:   fitTypes=206
12-22 20:59:09.939  1423  2230 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.939  1423  2230 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.940  1423  2934 D WindowManager: finishDrawingWindow: Window{e983ff1 u0 NavigationBar1} mDrawState=READY_TO_SHOW seqId=0
12-22 20:59:09.940  1423  1555 I ActivityManager: Changes in 5006 8 to 8, 0 to 128
12-22 20:59:09.944  1423  2886 I ActivityManager: Changes in 5006 8 to 8, 128 to 0
12-22 20:59:09.950  1423  1474 I WindowManager: Relayout Window{9f6c9d1 u0 ThumbsUpHandler_L}: viewVisibility=0 req=41x2520 ty=2601 d0
12-22 20:59:09.950  1423  1474 D WindowManager: makeSurface duration=1 name=ThumbsUpHandler_L$_6554
12-22 20:59:09.951  1423  1474 I WindowManager: Relayout hash=9f6c9d1, pid=6554, syncId=-1: mAttrs={(0,0)(41x2520) gr=TOP LEFT CENTER sim={state=hidden adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=2601 fmt=TRANSLUCENT alpha=0.0
12-22 20:59:09.951  1423  1474 I WindowManager:   fl=5800528
12-22 20:59:09.951  1423  1474 I WindowManager:   pfl=10000050
12-22 20:59:09.951  1423  1474 I WindowManager:   vsysui=500
12-22 20:59:09.951  1423  1474 I WindowManager:   bhv=2
12-22 20:59:09.951  1423  1474 I WindowManager:   fitTypes=207
12-22 20:59:09.951  1423  1474 I WindowManager:   fitSides=0
12-22 20:59:09.951  1423  1474 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.951  1423  1474 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:09.951  1423  1474 I WindowManager:   sfl=20000}
12-22 20:59:09.953  1423  2227 I ActivityManager: Changes in 5009 6 to 5, 128 to 144
12-22 20:59:09.955  1423  2266 I InputMethodManagerService: isInputMethodShown: isShown=false
12-22 20:59:09.956  1423  2230 D WindowManager: finishDrawingWindow: Window{a5d8bcd u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:09.957  1423  4812 I WindowManager: Relayout Window{e82ffe7 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=50x50 ty=2032 d1
12-22 20:59:09.957  1423  4812 D WindowManager: makeSurface duration=0 name=$_28700
12-22 20:59:09.958  1423  4812 I WindowManager: Relayout hash=e82ffe7, pid=28700, syncId=-1: mAttrs={(711,750)(wrapxwrap) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:09.958  1423  4812 I WindowManager:   fl=1000318
12-22 20:59:09.958  1423  4812 I WindowManager:   bhv=1
12-22 20:59:09.958  1423  4812 I WindowManager:   fitTypes=206
12-22 20:59:09.958  1423  4812 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.958  1423  4812 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.958  1423  2886 I WindowManager: Relayout Window{7aff3c3 u0 ThumbsUpHandler_R}: viewVisibility=0 req=41x2520 ty=2601 d0
12-22 20:59:09.958  1423  2886 D WindowManager: makeSurface duration=0 name=ThumbsUpHandler_R$_6554
12-22 20:59:09.959  1423  1555 I ActivityManager: Changes in 10373 16 to 11, 0 to 128
12-22 20:59:09.959  1423  2886 I WindowManager: Relayout hash=7aff3c3, pid=6554, syncId=-1: mAttrs={(0,0)(41x2520) gr=TOP RIGHT CENTER sim={state=hidden adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=2601 fmt=TRANSLUCENT alpha=0.0
12-22 20:59:09.959  1423  2886 I WindowManager:   fl=5800528
12-22 20:59:09.959  1423  2886 I WindowManager:   pfl=10000050
12-22 20:59:09.959  1423  2886 I WindowManager:   vsysui=500
12-22 20:59:09.959  1423  2886 I WindowManager:   bhv=2
12-22 20:59:09.959  1423  2886 I WindowManager:   fitTypes=207
12-22 20:59:09.959  1423  2886 I WindowManager:   fitSides=0
12-22 20:59:09.959  1423  2886 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.959  1423  2886 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:09.959  1423  2886 I WindowManager:   sfl=20000}
12-22 20:59:09.960  1423  1754 I InputMethodManagerService: startInputOrWindowGainedFocusInternalLocked: Rejecting unbind in case of no editor in cover screen.
12-22 20:59:09.961  1423  1754 I InputMethodManagerService: startInputUncheckedLocked: Rejecting unbind/bind in case of no editor in cover screen.
12-22 20:59:09.963  1423  4812 D WindowManager: finishDrawingWindow: Window{9f6c9d1 u0 ThumbsUpHandler_L} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:09.965  1423  2267 I WindowManager: Relayout Window{b6fe3cf u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=8 req=681x551 ty=2032 d1
12-22 20:59:09.967  1423  2265 D WindowManager: finishDrawingWindow: Window{e82ffe7 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:09.968  1423  2267 I WindowManager: Relayout hash=b6fe3cf, pid=28700, syncId=-1: mAttrs={(683,640)(681x551) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:09.968  1423  2267 I WindowManager:   fl=1040228
12-22 20:59:09.968  1423  2267 I WindowManager:   pfl=40000000
12-22 20:59:09.968  1423  2267 I WindowManager:   bhv=1
12-22 20:59:09.968  1423  2267 I WindowManager:   fitTypes=207
12-22 20:59:09.968  1423  2267 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.968  1423  2267 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.969  1423  4812 D WindowManager: finishDrawingWindow: Window{7aff3c3 u0 ThumbsUpHandler_R} mDrawState=DRAW_PENDING seqId=14
12-22 20:59:09.969  1423  1555 I ActivityManager: Changes in 10362 16 to 11, 0 to 128
12-22 20:59:09.969  1423  2230 I WindowManager: Relayout Window{5112d51 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=93x93 ty=2032 d1
12-22 20:59:09.970  1423  2230 D WindowManager: makeSurface duration=0 name=$_28700
12-22 20:59:09.970  1423  2230 I WindowManager: Relayout hash=5112d51, pid=28700, syncId=-1: mAttrs={(95,1058)(93x93) gr=TOP START CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:09.970  1423  2230 I WindowManager:   fl=1000308
12-22 20:59:09.970  1423  2230 I WindowManager:   bhv=1
12-22 20:59:09.970  1423  2230 I WindowManager:   fitTypes=206
12-22 20:59:09.970  1423  2230 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.970  1423  2230 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.973  1423  4808 I ActivityManager: Changes in 10373 11 to 16, 128 to 0
12-22 20:59:09.976  1423  4808 I ActivityManager: Changes in 10362 11 to 16, 128 to 0
12-22 20:59:09.981  1423  1555 I ActivityManager: Changes in 10113 10 to 10, 0 to 128
12-22 20:59:09.986  1423  1700 I ActivityManager: Changes in 10329 8 to 8, 128 to 0
12-22 20:59:09.986  1423  1700 D WindowManager: finishDrawingWindow: Window{5112d51 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:09.986  1423  2227 I WindowManager: Relayout Window{2fbb98e u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=50x50 ty=2032 d1
12-22 20:59:09.987  1423  1555 I ActivityManager: Changes in 10100 19 to 11, 0 to 128
12-22 20:59:09.987  1423  2227 D WindowManager: makeSurface duration=0 name=$_28700
12-22 20:59:09.988  1423  2227 I WindowManager: Relayout hash=2fbb98e, pid=28700, syncId=-1: mAttrs={(711,750)(wrapxwrap) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:09.988  1423  2227 I WindowManager:   fl=1000318
12-22 20:59:09.988  1423  2227 I WindowManager:   bhv=1
12-22 20:59:09.988  1423  2227 I WindowManager:   fitTypes=206
12-22 20:59:09.988  1423  2227 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:09.988  1423  2227 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:09.988  1423  4808 I ActivityManager: Changes in 10113 10 to 10, 128 to 0
12-22 20:59:09.993  1423  4811 D WindowManager: finishDrawingWindow: Window{2fbb98e u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:09.995  1423  4808 I ActivityManager: Changes in 10100 11 to 19, 128 to 0
12-22 20:59:10.001  1423  2886 W WindowManager: Failed looking up window session=Session{9d5656e 28700:u0a10515} callers=com.android.server.wm.WindowManagerService.windowForClientLocked:1 com.android.server.wm.Session.setOnBackInvokedCallbackInfo:15 android.view.IWindowSession$Stub.onTransact:1390 
12-22 20:59:10.001  1423  2886 I WindowManager: setOnBackInvokedCallback(): No window state for package:com.katsuyamaki.DroidOSTrackpadKeyboard
12-22 20:59:10.001  1423  2886 W WindowManager: Failed looking up window session=Session{9d5656e 28700:u0a10515} callers=com.android.server.wm.WindowManagerService.windowForClientLocked:1 com.android.server.wm.Session.setOnBackInvokedCallbackInfo:15 android.view.IWindowSession$Stub.onTransact:1390 
12-22 20:59:10.001  1423  2886 I WindowManager: setOnBackInvokedCallback(): No window state for package:com.katsuyamaki.DroidOSTrackpadKeyboard
12-22 20:59:10.002  1423  4807 I WindowManager: Relayout Window{41abd5c u0 com.sec.android.app.launcher/com.samsung.app.honeyspace.edge.edgepanel.app.CocktailBarService}: viewVisibility=0 req=67x342 ty=2226 d0
12-22 20:59:10.002  1423  4807 D WindowManager: makeSurface duration=0 name=com.sec.android.app.launcher/com.samsung.app.honeyspace.edge.edgepanel.app.CocktailBarService$_2712
12-22 20:59:10.003  1423  4807 I WindowManager: Relayout hash=41abd5c, pid=2712, syncId=-1: mAttrs={(0,628)(67x342) gr=TOP RIGHT CENTER sim={adjust=nothing} layoutInDisplayCutoutMode=always ty=2226 fmt=TRANSPARENT
12-22 20:59:10.003  1423  4807 I WindowManager:   fl=81800108
12-22 20:59:10.003  1423  4807 I WindowManager:   pfl=10000070
12-22 20:59:10.003  1423  4807 I WindowManager:   apr=10
12-22 20:59:10.003  1423  4807 I WindowManager:   bhv=1
12-22 20:59:10.003  1423  4807 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:10.003  1423  4807 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:10.003  1423  4807 I WindowManager:  dimAmount=0.0 dimDuration=0 naviIconColor=0
12-22 20:59:10.003  1423  4807 I WindowManager:   sfl=420000}
12-22 20:59:10.010  1423  2230 D WindowManager: finishDrawingWindow: Window{41abd5c u0 com.sec.android.app.launcher/com.samsung.app.honeyspace.edge.edgepanel.app.CocktailBarService} mDrawState=DRAW_PENDING seqId=57
12-22 20:59:10.019  1423  2230 D WindowManager: finishDrawingWindow: Window{5112d51 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:10.020  1423  4807 D WindowManager: finishDrawingWindow: Window{2fbb98e u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:10.262  1423  2230 I WindowManager: Relayout Window{f748235 u0 InputMethod}: viewVisibility=8 req=1080x2404 ty=2011 d0
12-22 20:59:10.263  1423  2230 E WindowManager: win=Window{f748235 u0 InputMethod} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1462 android.os.Binder.execTransact:1401 
12-22 20:59:10.279  1423  2230 I WindowManager: Relayout hash=f748235, pid=4255, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-22 20:59:10.279  1423  2230 I WindowManager:   fl=81800108
12-22 20:59:10.279  1423  2230 I WindowManager:   pfl=14000000
12-22 20:59:10.279  1423  2230 I WindowManager:   bhv=1
12-22 20:59:10.279  1423  2230 I WindowManager:   fitTypes=3
12-22 20:59:10.279  1423  2230 I WindowManager:   fitSides=7
12-22 20:59:10.279  1423  2230 I WindowManager:   fitIgnoreVis
12-22 20:59:10.279  1423  2230 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:10.279  1423  2230 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-22 20:59:10.288  1423  2227 D WindowManager: setInsetsWindow Window{f748235 u0 InputMethod}, contentInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), visibleInsets=Rect(0, 1904 - 0, 0) -> Rect(0, 2329 - 0, 0), touchableRegion=SkRegion((0,1904,1080,2404)) -> SkRegion(), touchableInsets 3 -> 3
12-22 20:59:10.292  1423  2227 I WindowManager: Relayout Window{5a7dcc6 u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-22 20:59:10.294  1423  2227 I WindowManager: Relayout hash=5a7dcc6, pid=2022, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-22 20:59:10.294  1423  2227 I WindowManager:   fl=14318
12-22 20:59:10.294  1423  2227 I WindowManager:   pfl=14
12-22 20:59:10.294  1423  2227 I WindowManager:   bhv=1
12-22 20:59:10.294  1423  2227 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:10.294  1423  2227 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:10.294  1423  2227 I WindowManager:   sfl=8}
12-22 20:59:10.396  1423  2230 D WindowManager: finishDrawingWindow: Window{b5eb294 u0 NotificationShade} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:11.025  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x0, time=33710627084000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:11.027  1423  1695 I InputDispatcher: Delivering touch to (28700): action: 0x0, f=0x0, d=1, '5112d51', t=1 +(-95,-1094)*(1.50)
12-22 20:59:11.076  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(948 x 1048) immediately
12-22 20:59:11.088  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x1, time=33710692369000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:11.088  1423  1695 I InputDispatcher: Delivering touch to (28700): action: 0x1, f=0x0, d=1, '5112d51', t=1 +(-95,-1094)*(1.50)
12-22 20:59:11.095 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{cfc4abe VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1588 
12-22 20:59:11.097 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{cfc4abe VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1588 com.example.coverscreentester.OverlayService.setupBubble$lambda$24$lambda$23:897 
12-22 20:59:11.100 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:11.101 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:11.107 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{ae54ca V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1589 
12-22 20:59:11.112 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{ae54ca V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1589 com.example.coverscreentester.OverlayService.setupBubble$lambda$24$lambda$23:897 
12-22 20:59:11.115 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:11.115 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:11.121  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(948 x 1048) immediately
12-22 20:59:11.122  1423  2230 I WindowManager: Relayout Window{7a9e9d4 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=93x93 ty=2032 d1
12-22 20:59:11.123  1423  2230 D WindowManager: makeSurface duration=0 name=$_28700
12-22 20:59:11.124  1423  2230 I WindowManager: Relayout hash=7a9e9d4, pid=28700, syncId=-1: mAttrs={(95,1058)(93x93) gr=TOP START CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:11.124  1423  2230 I WindowManager:   fl=1000308
12-22 20:59:11.124  1423  2230 I WindowManager:   bhv=1
12-22 20:59:11.124  1423  2230 I WindowManager:   fitTypes=206
12-22 20:59:11.124  1423  2230 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:11.124  1423  2230 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:11.131  1423  4807 D WindowManager: finishDrawingWindow: Window{7a9e9d4 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:11.131  1423  2227 I WindowManager: Relayout Window{a5c2f3b u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=50x50 ty=2032 d1
12-22 20:59:11.131  1423  2227 D WindowManager: makeSurface duration=1 name=$_28700
12-22 20:59:11.132  1423  2227 I WindowManager: Relayout hash=a5c2f3b, pid=28700, syncId=-1: mAttrs={(711,750)(wrapxwrap) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:11.132  1423  2227 I WindowManager:   fl=1000318
12-22 20:59:11.132  1423  2227 I WindowManager:   bhv=1
12-22 20:59:11.132  1423  2227 I WindowManager:   fitTypes=206
12-22 20:59:11.132  1423  2227 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:11.132  1423  2227 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:11.144  1423  1700 D WindowManager: finishDrawingWindow: Window{a5c2f3b u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:11.152  1423  1700 D WindowManager: finishDrawingWindow: Window{a5c2f3b u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:11.701  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x0, time=33711294047000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:11.703  1423  1695 I InputDispatcher: Delivering touch to (28700): action: 0x0, f=0x0, d=1, '7a9e9d4', t=1 +(-95,-1094)*(1.50)
12-22 20:59:11.741  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(948 x 1048) immediately
12-22 20:59:11.744  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x1, time=33711342421000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:11.744  1423  1695 I InputDispatcher: Delivering touch to (28700): action: 0x1, f=0x0, d=1, '7a9e9d4', t=1 +(-95,-1094)*(1.50)
12-22 20:59:11.754  1423  4807 I WindowManager: Relayout Window{b6fe3cf u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=681x551 ty=2032 d1
12-22 20:59:11.755  1423  4807 D WindowManager: makeSurface duration=1 name=$_28700
12-22 20:59:11.756  1423  4807 I WindowManager: Relayout hash=b6fe3cf, pid=28700, syncId=-1: mAttrs={(683,640)(681x551) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:11.756  1423  4807 I WindowManager:   fl=1040228
12-22 20:59:11.756  1423  4807 I WindowManager:   pfl=40000000
12-22 20:59:11.756  1423  4807 I WindowManager:   bhv=1
12-22 20:59:11.756  1423  4807 I WindowManager:   fitTypes=207
12-22 20:59:11.756  1423  4807 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:11.756  1423  4807 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:11.768 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{cfc4abe VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1588 
12-22 20:59:11.768  1423  4807 D WindowManager: finishDrawingWindow: Window{b6fe3cf u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:11.769 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{cfc4abe VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1588 com.example.coverscreentester.OverlayService.setupBubble$lambda$24$lambda$23:897 
12-22 20:59:11.774  1423  4807 W WindowManager: Failed looking up window session=Session{9d5656e 28700:u0a10515} callers=com.android.server.wm.WindowManagerService.windowForClientLocked:1 com.android.server.wm.Session.setOnBackInvokedCallbackInfo:15 android.view.IWindowSession$Stub.onTransact:1390 
12-22 20:59:11.774  1423  4807 I WindowManager: setOnBackInvokedCallback(): No window state for package:com.katsuyamaki.DroidOSTrackpadKeyboard
12-22 20:59:11.775 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:11.775 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:11.782 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{ae54ca V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1589 
12-22 20:59:11.783 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{ae54ca V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1589 com.example.coverscreentester.OverlayService.setupBubble$lambda$24$lambda$23:897 
12-22 20:59:11.788 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:11.788 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:11.798  1423  2189 I WindowManager: Relayout Window{b6fe3cf u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=681x551 ty=2032 d1
12-22 20:59:11.799  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(948 x 1048) immediately
12-22 20:59:11.799  1423  2189 I WindowManager: Relayout hash=b6fe3cf, pid=28700, syncId=-1: mAttrs={(683,640)(681x551) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:11.799  1423  2189 I WindowManager:   fl=1040228
12-22 20:59:11.799  1423  2189 I WindowManager:   pfl=40000000
12-22 20:59:11.799  1423  2189 I WindowManager:   bhv=1
12-22 20:59:11.799  1423  2189 I WindowManager:   fitTypes=207
12-22 20:59:11.799  1423  2189 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:11.799  1423  2189 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:11.801  1423  2886 I WindowManager: Relayout Window{f54e17a u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=93x93 ty=2032 d1
12-22 20:59:11.801  1423  2886 D WindowManager: makeSurface duration=0 name=$_28700
12-22 20:59:11.802  1423  2886 I WindowManager: Relayout hash=f54e17a, pid=28700, syncId=-1: mAttrs={(95,1058)(93x93) gr=TOP START CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:11.802  1423  2886 I WindowManager:   fl=1000308
12-22 20:59:11.802  1423  2886 I WindowManager:   bhv=1
12-22 20:59:11.802  1423  2886 I WindowManager:   fitTypes=206
12-22 20:59:11.802  1423  2886 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:11.802  1423  2886 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:11.807  1423  4811 D WindowManager: finishDrawingWindow: Window{f54e17a u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:11.807  1423  2189 I WindowManager: Relayout Window{411d59 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=50x50 ty=2032 d1
12-22 20:59:11.807  1423  2189 D WindowManager: makeSurface duration=1 name=$_28700
12-22 20:59:11.809  1423  2189 I WindowManager: Relayout hash=411d59, pid=28700, syncId=-1: mAttrs={(711,750)(wrapxwrap) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:11.809  1423  2189 I WindowManager:   fl=1000318
12-22 20:59:11.809  1423  2189 I WindowManager:   bhv=1
12-22 20:59:11.809  1423  2189 I WindowManager:   fitTypes=206
12-22 20:59:11.809  1423  2189 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:11.809  1423  2189 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:11.814  1423  2189 D WindowManager: finishDrawingWindow: Window{411d59 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:11.818  1423  4812 D WindowManager: finishDrawingWindow: Window{411d59 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:12.576  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x0, time=33712175402000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:12.577  1423  1695 I InputDispatcher: Delivering touch to (28700): action: 0x0, f=0x0, d=1, 'b6fe3cf', t=1 +(-682,-676)*(1.50)
12-22 20:59:12.629 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{4228daa V.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.TrackpadMenuManager.show:49 com.example.coverscreentester.TrackpadMenuManager.toggle:75 
12-22 20:59:12.629 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:12.629 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:12.633  1423  4811 D WindowManager: Changing focus from Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity} to Window{a0ecd0b u0 com.katsuyamaki.DroidOSTrackpadKeyboard} displayId=1 Callers=com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 com.android.server.wm.Session.addToDisplayAsUser:24 android.view.IWindowSession$Stub.onTransact:757 
12-22 20:59:12.633  1423  4811 D WindowManager: updateSystemBarAttributes: displayId=1, focusedCanBeNavColorWin=false, win=Window{a0ecd0b u0 com.katsuyamaki.DroidOSTrackpadKeyboard}, navColorWin=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 
12-22 20:59:12.633  1423  4811 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[], requestedVisibilities=-9
12-22 20:59:12.638 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{4228daa V.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.TrackpadMenuManager.bringToFront:82 
12-22 20:59:12.639 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{4228daa V.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.TrackpadMenuManager.bringToFront:83 com.example.coverscreentester.OverlayService.enforceZOrder:1587 
12-22 20:59:12.641  1423  2886 D WindowManager: Changing focus from Window{a0ecd0b u0 com.katsuyamaki.DroidOSTrackpadKeyboard} to Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity} displayId=1 Callers=com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-22 20:59:12.641  1423  2886 D WindowManager: updateSystemBarAttributes: displayId=1, focusedCanBeNavColorWin=false, win=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}, navColorWin=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.wm.Session.remove:16 
12-22 20:59:12.641  1423  2886 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[], requestedVisibilities=-11
12-22 20:59:12.642 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:12.642 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:12.647  1423  4807 D WindowManager: Changing focus from Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity} to Window{4443c39 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} displayId=1 Callers=com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 com.android.server.wm.Session.addToDisplayAsUser:24 android.view.IWindowSession$Stub.onTransact:757 
12-22 20:59:12.647  1423  4807 D WindowManager: updateSystemBarAttributes: displayId=1, focusedCanBeNavColorWin=false, win=Window{4443c39 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}, navColorWin=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowManagerService.addWindowInner:565 com.android.server.wm.WindowManagerService.addWindow:1842 
12-22 20:59:12.647  1423  4807 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[], requestedVisibilities=-9
12-22 20:59:12.649 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{cfc4abe VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1588 
12-22 20:59:12.651 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{cfc4abe VFE...C.. ........ 0,0-93,93}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1588 com.example.coverscreentester.TrackpadMenuManager.show:56 
12-22 20:59:12.655 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:12.655 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:12.660 28700 28700 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{ae54ca V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.coverscreentester.OverlayService.enforceZOrder:1589 
12-22 20:59:12.662 28700 28700 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{ae54ca V.E...... ........ 0,0-50,50}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.enforceZOrder:1589 com.example.coverscreentester.TrackpadMenuManager.show:56 
12-22 20:59:12.664  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x1, time=33712267612000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:12.665  1423  1695 I InputDispatcher: Delivering touch to (28700): action: 0x1, f=0x0, d=1, 'b6fe3cf', t=1 +(-682,-676)*(1.50)
12-22 20:59:12.666 28700 28700 D ViewRootImpl: desktopMode is false
12-22 20:59:12.666 28700 28700 I ViewRootImpl: dVRR is disabled
12-22 20:59:12.671  1423  2886 D InputDispatcher: Once focus requested (1): 9a3d1c6 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity
12-22 20:59:12.685  1423  2189 I WindowManager: Relayout Window{4443c39 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=500x625 ty=2032 d1
12-22 20:59:12.686  1423  2189 D WindowManager: makeSurface duration=1 name=$_28700
12-22 20:59:12.687  1423  2189 I WindowManager: Relayout hash=4443c39, pid=28700, syncId=-1: mAttrs={(430,437)(wrapxwrap) gr=TOP START CENTER sim={adjust=resize} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:12.687  1423  2189 I WindowManager:   fl=1000120
12-22 20:59:12.687  1423  2189 I WindowManager:   bhv=1
12-22 20:59:12.687  1423  2189 I WindowManager:   fitTypes=206
12-22 20:59:12.687  1423  2189 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:12.687  1423  2189 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:12.701  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(948 x 1048) immediately
12-22 20:59:12.711  1423  4807 D InputDispatcher: Once focus requested (1): 4443c39 com.katsuyamaki.DroidOSTrackpadKeyboard
12-22 20:59:12.711  1423  4807 D InputDispatcher: Focus request (1): 4443c39 com.katsuyamaki.DroidOSTrackpadKeyboard but waiting because NO_WINDOW
12-22 20:59:12.711  1423  4807 D InputDispatcher: Focus left window (1): 9a3d1c6 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity
12-22 20:59:12.806  1423  2189 D WindowManager: finishDrawingWindow: Window{4443c39 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:12.806  1423  4807 I WindowManager: Relayout Window{c2cc0c4 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=93x93 ty=2032 d1
12-22 20:59:12.807  1423  4807 D WindowManager: makeSurface duration=0 name=$_28700
12-22 20:59:12.809  1423  4807 I WindowManager: Relayout hash=c2cc0c4, pid=28700, syncId=-1: mAttrs={(95,1058)(93x93) gr=TOP START CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:12.809  1423  4807 I WindowManager:   fl=1000308
12-22 20:59:12.809  1423  4807 I WindowManager:   bhv=1
12-22 20:59:12.809  1423  4807 I WindowManager:   fitTypes=206
12-22 20:59:12.809  1423  4807 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:12.809  1423  4807 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:12.815  1423  1700 D WindowManager: finishDrawingWindow: Window{c2cc0c4 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:12.816  1423  2886 I WindowManager: Relayout Window{e9faceb u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=50x50 ty=2032 d1
12-22 20:59:12.816  1423  2886 D WindowManager: makeSurface duration=0 name=$_28700
12-22 20:59:12.817  1423  2886 I WindowManager: Relayout hash=e9faceb, pid=28700, syncId=-1: mAttrs={(711,750)(wrapxwrap) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:12.817  1423  2886 I WindowManager:   fl=1000318
12-22 20:59:12.817  1423  2886 I WindowManager:   bhv=1
12-22 20:59:12.817  1423  2886 I WindowManager:   fitTypes=206
12-22 20:59:12.817  1423  2886 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:12.817  1423  2886 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:12.822  1423  2886 D WindowManager: finishDrawingWindow: Window{e9faceb u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:12.826  1423  2886 D WindowManager: finishDrawingWindow: Window{4443c39 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:12.827  1423  2886 D WindowManager: finishDrawingWindow: Window{e9faceb u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:12.829  1423  4807 D InputDispatcher: Focus entered window (1): 4443c39 com.katsuyamaki.DroidOSTrackpadKeyboard
12-22 20:59:12.836  1423  1754 I InputMethodManagerService: startInputOrWindowGainedFocusInternalLocked: Rejecting unbind in case of no editor in cover screen.
12-22 20:59:12.836  1423  1754 I InputMethodManagerService: startInputUncheckedLocked: Rejecting unbind/bind in case of no editor in cover screen.
12-22 20:59:12.858  1423  2886 I ActivityManager: Changes in 10122 7 to 10, 128 to 0
12-22 20:59:13.452  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x0, time=33713051322000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:13.454  1423  1695 I InputDispatcher: Delivering touch to (28700): action: 0x0, f=0x0, d=1, '4443c39', t=1 +(-430,-472)*(1.50)
12-22 20:59:13.538  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x1, time=33713142593000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:13.539  1423  1695 I InputDispatcher: Delivering touch to (28700): action: 0x1, f=0x0, d=1, '4443c39', t=1 +(-430,-472)*(1.50)
12-22 20:59:13.608  1423  1695 W InputDispatcher: channel '4443c39 com.katsuyamaki.DroidOSTrackpadKeyboard' ~ Consumer closed input channel or an error occurred.  events=0x9
12-22 20:59:13.609  1423  1695 E InputDispatcher: channel '4443c39 com.katsuyamaki.DroidOSTrackpadKeyboard' ~ Channel is unrecoverably broken and will be disposed!
12-22 20:59:13.609  1423  1695 I WindowManager: WINDOW DIED Window{4443c39 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}
12-22 20:59:13.610  1423  1695 D WindowManager: Changing focus from Window{4443c39 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} to Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity} displayId=1 Callers=com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.input.InputManagerService.notifyInputChannelBroken:72 <bottom of call stack> <bottom of call stack> 
12-22 20:59:13.610  1423  1695 D WindowManager: updateSystemBarAttributes: displayId=1, focusedCanBeNavColorWin=false, win=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}, navColorWin=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowState.removeIfPossible:647 com.android.server.input.InputManagerService.notifyInputChannelBroken:72 
12-22 20:59:13.610  1423  1695 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[], requestedVisibilities=-11
12-22 20:59:13.611  1423  1695 W InputDispatcher: channel 'b6fe3cf com.katsuyamaki.DroidOSTrackpadKeyboard' ~ Consumer closed input channel or an error occurred.  events=0x9
12-22 20:59:13.611  1423  1695 E InputDispatcher: channel 'b6fe3cf com.katsuyamaki.DroidOSTrackpadKeyboard' ~ Channel is unrecoverably broken and will be disposed!
12-22 20:59:13.611  1423  1695 I WindowManager: WINDOW DIED Window{b6fe3cf u0 com.katsuyamaki.DroidOSTrackpadKeyboard}
12-22 20:59:13.612  1423  1695 W InputDispatcher: channel 'e9faceb com.katsuyamaki.DroidOSTrackpadKeyboard' ~ Consumer closed input channel or an error occurred.  events=0x9
12-22 20:59:13.612  1423  1695 E InputDispatcher: channel 'e9faceb com.katsuyamaki.DroidOSTrackpadKeyboard' ~ Channel is unrecoverably broken and will be disposed!
12-22 20:59:13.612  1423  1695 W InputDispatcher: channel 'c2cc0c4 com.katsuyamaki.DroidOSTrackpadKeyboard' ~ Consumer closed input channel or an error occurred.  events=0x9
12-22 20:59:13.612  1423  1695 E InputDispatcher: channel 'c2cc0c4 com.katsuyamaki.DroidOSTrackpadKeyboard' ~ Channel is unrecoverably broken and will be disposed!
12-22 20:59:13.612  1423  1695 I WindowManager: WINDOW DIED Window{e9faceb u0 com.katsuyamaki.DroidOSTrackpadKeyboard}
12-22 20:59:13.612  1423  1700 D InputDispatcher: Focus left window (1): 4443c39 com.katsuyamaki.DroidOSTrackpadKeyboard
12-22 20:59:13.613  1423  1695 I WindowManager: WINDOW DIED Window{c2cc0c4 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}
12-22 20:59:13.615  1423  2267 I ActivityManager: Process com.katsuyamaki.DroidOSTrackpadKeyboard (pid 28700) has died: vis BFGS(819,1740)
12-22 20:59:13.616  1423  2267 W ActivityManager: Scheduling restart of crashed service com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.OverlayService in 1000ms for connection
12-22 20:59:13.619  1423  2697 D InputMethodManagerService: onClientRemovedInternalLocked
12-22 20:59:13.621  1423  2227 D InputMethodManagerService: onClientRemovedInternalLocked
12-22 20:59:13.622  1423  2109 W ActivityManager: pid 1423 system sent binder code 1 with flags 1 to frozen apps and got error -32
12-22 20:59:13.626  1423  2270 W WindowManager: Cannot find window which accessibility connection is added to
12-22 20:59:13.626  1423  1636 W WindowManager: Cannot find window which accessibility connection is added to
12-22 20:59:13.627  1423  4812 W WindowManager: Cannot find window which accessibility connection is added to
12-22 20:59:13.628  1423  1549 W ActivityManager: setHasOverlayUi called on unknown pid: 28700
12-22 20:59:13.629  1423  4800 W WindowManager: Cannot find window which accessibility connection is added to
12-22 20:59:13.634  1423  2228 D InputDispatcher: Once focus requested (1): 9a3d1c6 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity
12-22 20:59:13.634  1423  2228 D InputDispatcher: Focus entered window (1): 9a3d1c6 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity
12-22 20:59:13.639  1423  1754 I InputMethodManagerService: startInputOrWindowGainedFocusInternalLocked: Rejecting unbind in case of no editor in cover screen.
12-22 20:59:13.639  1423  1754 I InputMethodManagerService: startInputUncheckedLocked: Rejecting unbind/bind in case of no editor in cover screen.
12-22 20:59:14.417  1423  1554 W ActivityManager: Rescheduling restart of crashed service com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.OverlayService in 29202ms for mem-pressure-event
12-22 20:59:14.657  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x0, time=33714242798000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:14.657  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(948 x 1048) immediately
12-22 20:59:14.658  1423  1695 I InputDispatcher: Delivering touch to (7845): action: 0x4, f=0x0, d=1, '2a89570', t=1 
12-22 20:59:14.658  1423  1695 I InputDispatcher: Delivering touch to (2022): action: 0x4, f=0x0, d=1, 'e983ff1', t=1 
12-22 20:59:14.658  1423  1695 I InputDispatcher: Delivering touch to (2022): action: 0x0, f=0x0, d=1, '9a3d1c6', t=1 +(0,-35)*(1.50)
12-22 20:59:14.689  1423  1700 I WindowManager: Relayout Window{e382cc2 u0 SubLauncherWindow}: viewVisibility=0 req=1422x1500 ty=1000 d1
12-22 20:59:14.690  1423  1700 I WindowManager: Relayout hash=e382cc2, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize} layoutInDisplayCutoutMode=always ty=APPLICATION_PANEL fmt=TRANSLUCENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY
12-22 20:59:14.690  1423  1700 I WindowManager:   fl=81080278
12-22 20:59:14.690  1423  1700 I WindowManager:   vsysui=1600400
12-22 20:59:14.690  1423  1700 I WindowManager:   bhv=1
12-22 20:59:14.690  1423  1700 I WindowManager:   fitTypes=206
12-22 20:59:14.690  1423  1700 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:14.690  1423  1700 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:14.694  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(948 x 1048) immediately
12-22 20:59:14.707  1423  2228 I WindowManager: Relayout Window{e382cc2 u0 SubLauncherWindow}: viewVisibility=0 req=1422x1500 ty=1000 d1
12-22 20:59:14.708  1423  2228 D WindowManager: Changing focus from Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity} to Window{e382cc2 u0 SubLauncherWindow} displayId=1 Callers=com.android.server.wm.WindowManagerService.relayoutWindow:1359 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 
12-22 20:59:14.708  1423  2228 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0xa7dc9da
12-22 20:59:14.708  1423  2228 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785, destroy=false, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc
12-22 20:59:14.708  1423  2228 I WindowManager: Reparenting to leash, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc, leashParent=Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785
12-22 20:59:14.708  1423  2228 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0xaef8cb5
12-22 20:59:14.709  1423  2228 I WindowManager: Relayout hash=e382cc2, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize} layoutInDisplayCutoutMode=always ty=APPLICATION_PANEL fmt=TRANSLUCENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY
12-22 20:59:14.709  1423  2228 I WindowManager:   fl=80080260
12-22 20:59:14.709  1423  2228 I WindowManager:   vsysui=1600400
12-22 20:59:14.709  1423  2228 I WindowManager:   bhv=1
12-22 20:59:14.709  1423  2228 I WindowManager:   fitTypes=206
12-22 20:59:14.709  1423  2228 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:14.709  1423  2228 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:14.713  1423  2228 E ActivityManager: Sending non-protected broadcast com.samsung.android.app.aodservice.ACTION_COVER_HOME_QUICK_PANEL_TOUCH_AREA_CHANGED from system 2022:com.android.systemui/u0a55 pkg com.android.systemui
12-22 20:59:14.713  1423  2228 E ActivityManager: java.lang.Throwable
12-22 20:59:14.713  1423  2228 E ActivityManager: 	at com.android.server.am.BroadcastController.checkBroadcastFromSystem(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:252)
12-22 20:59:14.713  1423  2228 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLockedTraced(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:344)
12-22 20:59:14.713  1423  2228 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLocked(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:18)
12-22 20:59:14.713  1423  2228 E ActivityManager: 	at com.android.server.am.ActivityManagerService.broadcastIntentWithFeature(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:149)
12-22 20:59:14.713  1423  2228 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact$broadcastIntentWithFeature$(IActivityManager.java:12241)
12-22 20:59:14.713  1423  2228 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact(IActivityManager.java:3154)
12-22 20:59:14.713  1423  2228 E ActivityManager: 	at com.android.server.am.ActivityManagerService.onTransact(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:425)
12-22 20:59:14.713  1423  2228 E ActivityManager: 	at android.os.Binder.execTransactInternal(Binder.java:1462)
12-22 20:59:14.713  1423  2228 E ActivityManager: 	at android.os.Binder.execTransact(Binder.java:1401)
12-22 20:59:14.728  1423  1700 D InputDispatcher: Once focus requested (1): e382cc2 SubLauncherWindow
12-22 20:59:14.728  1423  1700 D InputDispatcher: Focus left window (1): 9a3d1c6 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity
12-22 20:59:14.728  1423  1700 D InputDispatcher: Focus entered window (1): e382cc2 SubLauncherWindow
12-22 20:59:14.743  1423  1754 I InputMethodManagerService: startInputOrWindowGainedFocusInternalLocked: Rejecting unbind in case of no editor in cover screen.
12-22 20:59:14.743  1423  1754 I InputMethodManagerService: startInputUncheckedLocked: Rejecting unbind/bind in case of no editor in cover screen.
12-22 20:59:14.747  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x1, time=33714351020000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:14.747  1423  1695 I InputDispatcher: Delivering touch to (2022): action: 0x1, f=0x0, d=1, '9a3d1c6', t=1 +(0,-35)*(1.50)
12-22 20:59:14.815  1423  1636 E ActivityManager: Sending non-protected broadcast com.samsung.android.app.aodservice.ACTION_COVER_HOME_FOCUS_CHANGED from system 2022:com.android.systemui/u0a55 pkg com.android.systemui
12-22 20:59:14.815  1423  1636 E ActivityManager: java.lang.Throwable
12-22 20:59:14.815  1423  1636 E ActivityManager: 	at com.android.server.am.BroadcastController.checkBroadcastFromSystem(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:252)
12-22 20:59:14.815  1423  1636 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLockedTraced(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:344)
12-22 20:59:14.815  1423  1636 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLocked(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:18)
12-22 20:59:14.815  1423  1636 E ActivityManager: 	at com.android.server.am.ActivityManagerService.broadcastIntentWithFeature(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:149)
12-22 20:59:14.815  1423  1636 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact$broadcastIntentWithFeature$(IActivityManager.java:12241)
12-22 20:59:14.815  1423  1636 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact(IActivityManager.java:3154)
12-22 20:59:14.815  1423  1636 E ActivityManager: 	at com.android.server.am.ActivityManagerService.onTransact(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:425)
12-22 20:59:14.815  1423  1636 E ActivityManager: 	at android.os.Binder.execTransactInternal(Binder.java:1462)
12-22 20:59:14.815  1423  1636 E ActivityManager: 	at android.os.Binder.execTransact(Binder.java:1401)
12-22 20:59:14.815  1423  1700 E ActivityManager: Sending non-protected broadcast com.samsung.android.app.aodservice.ACTION_FLOATING_SHORTCUT_AVAILABLE_CHANGED from system 2022:com.android.systemui/u0a55 pkg com.android.systemui
12-22 20:59:14.815  1423  1700 E ActivityManager: java.lang.Throwable
12-22 20:59:14.815  1423  1700 E ActivityManager: 	at com.android.server.am.BroadcastController.checkBroadcastFromSystem(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:252)
12-22 20:59:14.815  1423  1700 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLockedTraced(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:344)
12-22 20:59:14.815  1423  1700 E ActivityManager: 	at com.android.server.am.BroadcastController.broadcastIntentLocked(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:18)
12-22 20:59:14.815  1423  1700 E ActivityManager: 	at com.android.server.am.ActivityManagerService.broadcastIntentWithFeature(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:149)
12-22 20:59:14.815  1423  1700 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact$broadcastIntentWithFeature$(IActivityManager.java:12241)
12-22 20:59:14.815  1423  1700 E ActivityManager: 	at android.app.IActivityManager$Stub.onTransact(IActivityManager.java:3154)
12-22 20:59:14.815  1423  1700 E ActivityManager: 	at com.android.server.am.ActivityManagerService.onTransact(qb/103888019 03a5a67e5cf815856c8cb02a780996d8495d88e5abfc1f4625d73e5d10674898:425)
12-22 20:59:14.815  1423  1700 E ActivityManager: 	at android.os.Binder.execTransactInternal(Binder.java:1462)
12-22 20:59:14.815  1423  1700 E ActivityManager: 	at android.os.Binder.execTransact(Binder.java:1401)
12-22 20:59:14.875  1423  1754 I InputMethodManagerService: startInputUncheckedLocked: Rejecting unbind/bind in case of no editor in cover screen.
12-22 20:59:15.058  1423  1700 D WindowManager: updateSystemBarAttributes: displayId=1, focusedCanBeNavColorWin=false, win=Window{e382cc2 u0 SubLauncherWindow}, navColorWin=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}, caller=com.android.server.wm.InsetsStateController.onRequestedVisibleTypesChanged:86 com.android.server.wm.InsetsPolicy.onRequestedVisibleTypesChanged:3 com.android.server.wm.Session.updateRequestedVisibleTypes:37 
12-22 20:59:15.058  1423  1700 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[], requestedVisibilities=-9
12-22 20:59:15.063  1423  1636 I WindowManager: Relayout Window{e382cc2 u0 SubLauncherWindow}: viewVisibility=0 req=1422x1500 ty=1000 d1
12-22 20:59:15.066  1423  1636 I WindowManager: Relayout hash=e382cc2, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize} layoutInDisplayCutoutMode=always ty=APPLICATION_PANEL fmt=TRANSLUCENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY
12-22 20:59:15.066  1423  1636 I WindowManager:   fl=80080260
12-22 20:59:15.066  1423  1636 I WindowManager:   vsysui=400
12-22 20:59:15.066  1423  1636 I WindowManager:   bhv=1
12-22 20:59:15.066  1423  1636 I WindowManager:   fitTypes=206
12-22 20:59:15.066  1423  1636 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:15.066  1423  1636 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:15.068  1423  1700 I WindowManager: Relayout Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}: viewVisibility=0 req=1422x1500 ty=1 d1
12-22 20:59:15.070  1423  1700 I WindowManager: Relayout hash=9a3d1c6, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY userActivityTimeout=30000
12-22 20:59:15.070  1423  1700 I WindowManager:   fl=81910100
12-22 20:59:15.070  1423  1700 I WindowManager:   pfl=10008850
12-22 20:59:15.070  1423  1700 I WindowManager:   bhv=1
12-22 20:59:15.070  1423  1700 I WindowManager:   fitSides=0
12-22 20:59:15.070  1423  1700 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:15.070  1423  1700 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=0 naviIconColor=0}
12-22 20:59:15.073  1423  1700 I WindowManager: Relayout Window{d2cc959 u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=-1x-1 ty=2013 d1
12-22 20:59:15.074  1423  1700 I WindowManager: Relayout hash=d2cc959, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-22 20:59:15.074  1423  1700 I WindowManager:   fl=10318
12-22 20:59:15.074  1423  1700 I WindowManager:   pfl=14
12-22 20:59:15.074  1423  1700 I WindowManager:   bhv=1
12-22 20:59:15.074  1423  1700 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:15.074  1423  1700 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:15.638  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x0, time=33715241033000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:15.638  1423  1695 I InputDispatcher: Delivering touch to (7845): action: 0x4, f=0x0, d=1, '2a89570', t=1 
12-22 20:59:15.638  1423  1695 I InputDispatcher: Delivering touch to (2022): action: 0x4, f=0x0, d=1, 'e983ff1', t=1 
12-22 20:59:15.638  1423  1695 I InputDispatcher: Delivering touch to (2022): action: 0x0, f=0x0, d=1, 'e382cc2', t=1 +(0,-35)*(1.50)
12-22 20:59:15.714  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x1, time=33715317529000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:15.714  1423  1695 I InputDispatcher: Delivering touch to (2022): action: 0x1, f=0x0, d=1, 'e382cc2', t=1 +(0,-35)*(1.50)
12-22 20:59:15.809  1423  1700 D WindowManager: Transition is created, t=TransitionRecord{50f1623 id=-1 type=OPEN flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createAndStartCollecting:106 com.android.server.wm.ActivityStarter.executeRequest:3537 com.android.server.wm.ActivityStarter.execute:1873 com.android.server.wm.ActivityTaskManagerService.startActivityAsUser:87 
12-22 20:59:15.810  1423  4800 I InputMethodManagerService: isInputMethodShown: isShown=false
12-22 20:59:15.818  1423  2270 I InputMethodManagerService: isInputMethodShown: isShown=false
12-22 20:59:15.823  1423  1700 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{e382cc2 u0 SubLauncherWindow}
12-22 20:59:15.823  1423  1700 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}
12-22 20:59:15.823  1423  1700 I WindowManager: getCoverScreenRotation 0, sensor= 0, isFlat=false, isDual=false
12-22 20:59:15.826  2022  2160 D ViewRootImpl: desktopMode is false
12-22 20:59:15.826  2022  2160 I ViewRootImpl: dVRR is disabled
12-22 20:59:15.830  1423  1700 I ActivityManager: Changes in 10333 19 to 6, 0 to 128
12-22 20:59:15.833  1423  1700 D WindowManager: App launched activity=ActivityRecord{37570336 u0 com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.MainActivity t19274}, state=1
12-22 20:59:15.833  1423  1700 D WindowManager: App launched activity=ActivityRecord{37570336 u0 com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.MainActivity t19274}, state=1
12-22 20:59:15.833  1423  1700 D WindowManager: App launched activity=ActivityRecord{37570336 u0 com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.MainActivity t19274}, state=1
12-22 20:59:15.836  1423  1636 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{2134168 u0 Splash Screen com.katsuyamaki.DroidOSTrackpadKeyboard}
12-22 20:59:15.842  1423  1636 I WindowManager: Relayout Window{2134168 u0 Splash Screen com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=1422x1169 ty=3 d1
12-22 20:59:15.843  1423  1636 D WindowManager: makeSurface duration=0 name=Splash Screen com.katsuyamaki.DroidOSTrackpadKeyboard$_2022
12-22 20:59:15.843  1423  1636 D WindowManager: Changing focus from Window{e382cc2 u0 SubLauncherWindow} to null displayId=1 Callers=com.android.server.wm.WindowManagerService.relayoutWindow:1359 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 
12-22 20:59:15.843  1423  1636 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0xaef8cb5
12-22 20:59:15.843  1423  1636 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785, destroy=false, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc
12-22 20:59:15.843  1423  1636 I WindowManager: Reparenting to leash, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc, leashParent=Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785
12-22 20:59:15.844  1423  1636 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0xf7412b2
12-22 20:59:15.844  1423  1636 D WindowManager: App launched activity=ActivityRecord{37570336 u0 com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.MainActivity t19274}, state=1
12-22 20:59:15.844  1423  1636 I WindowManager: Relayout hash=2134168, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=pan} ty=APPLICATION_STARTING fmt=TRANSLUCENT wanim=0x1030317
12-22 20:59:15.844  1423  1636 I WindowManager:   fl=81030118
12-22 20:59:15.844  1423  1636 I WindowManager:   pfl=10000010
12-22 20:59:15.844  1423  1636 I WindowManager:   bhv=1
12-22 20:59:15.844  1423  1636 I WindowManager:   fitSides=0
12-22 20:59:15.844  1423  1636 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:15.844  1423  1636 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:15.845  1423  1556 I ActivityManager: Start proc 31283:com.katsuyamaki.DroidOSTrackpadKeyboard/u0a515 for next-top-activity {com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.MainActivity}
12-22 20:59:15.845  1423  1556 I ActivityManager: ProcessObserver broadcast disabled
12-22 20:59:15.849  1423  1636 D WindowManager: finishDrawingWindow: Window{2134168 u0 Splash Screen com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:15.853  1423  1636 D InputDispatcher: Focus left window (1): e382cc2 SubLauncherWindow
12-22 20:59:15.877  1423  1636 I ActivityManager: Changes in 10515 19 to 2, 0 to 255
12-22 20:59:15.952  1423  1554 W ActivityManager: Rescheduling restart of crashed service com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.OverlayService in 7663ms for mem-pressure-event
12-22 20:59:15.953  1423  2228 D WindowManager: App launched activity=ActivityRecord{37570336 u0 com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.MainActivity t19274}, state=1
12-22 20:59:15.953  1423  2228 I WindowManager: getCoverScreenRotation 0, sensor= 0, isFlat=false, isDual=false
12-22 20:59:15.954  1423  2228 D InputDispatcher: Focused application(1): ActivityRecord{37570336 u0 com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.MainActivity t19274}
12-22 20:59:15.959  1423  2270 D WindowManager: Remove SleepToken: tag=keyguard, displayId=0
12-22 20:59:15.960  1423  1754 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-22 20:59:15.962  1423  1754 W InputMethodManagerService: callbackImeWindowStatus: skipped topFocusedDisplayId=1, curTokenDisplayId=0
12-22 20:59:15.962  1423  1754 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-22 20:59:15.963  1423  1754 W InputMethodManagerService: callbackImeWindowStatus: skipped topFocusedDisplayId=1, curTokenDisplayId=0
12-22 20:59:15.966  1423  2267 I WindowManager: Relayout Window{e983ff1 u0 NavigationBar1}: viewVisibility=0 req=1422x66 ty=2019 d1
12-22 20:59:15.968  1423  2267 I WindowManager: Relayout hash=e983ff1, pid=2022, syncId=-1: mAttrs={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:15.968  1423  2267 I WindowManager:   fl=20040028
12-22 20:59:15.968  1423  2267 I WindowManager:   pfl=31000000
12-22 20:59:15.968  1423  2267 I WindowManager:   bhv=1
12-22 20:59:15.968  1423  2267 I WindowManager:   providedInsets:
12-22 20:59:15.968  1423  2267 I WindowManager:     InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=66}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:15.968  1423  2267 I WindowManager:     InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:15.968  1423  2267 I WindowManager:     InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:15.968  1423  2267 I WindowManager:     InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:15.968  1423  2267 I WindowManager:     InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:15.968  1423  2267 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:15.968  1423  2267 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:15.968  1423  2267 I WindowManager:   paramsForRotation:
12-22 20:59:15.968  1423  2267 I WindowManager:     ROTATION_0={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:15.968  1423  2267 I WindowManager:       fl=20040028
12-22 20:59:15.968  1423  2267 I WindowManager:       pfl=31000000
12-22 20:59:15.968  1423  2267 I WindowManager:       bhv=1
12-22 20:59:15.968  1423  2267 I WindowManager:       providedInsets:
12-22 20:59:15.968  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=66}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:15.968  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:15.968  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:15.968  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:15.968  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:15.968  1423  2267 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:15.968  1423  2267 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:15.968  1423  2267 I WindowManager:     ROTATION_90={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:15.968  1423  2267 I WindowManager:       fl=20040028
12-22 20:59:15.968  1423  2267 I WindowManager:       pfl=31000000
12-22 20:59:15.968  1423  2267 I WindowManager:       bhv=1
12-22 20:59:15.968  1423  2267 I WindowManager:       providedInsets:
12-22 20:59:15.968  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=38}}]}
12-22 20:59:15.968  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:15.968  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:15.968  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:15.968  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:15.968  1423  2267 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:15.968  1423  2267 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:15.969  1423  2267 I WindowManager:     ROTATION_180={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:15.969  1423  2267 I WindowManager:       fl=20040028
12-22 20:59:15.969  1423  2267 I WindowManager:       pfl=31000000
12-22 20:59:15.969  1423  2267 I WindowManager:       bhv=1
12-22 20:59:15.969  1423  2267 I WindowManager:       providedInsets:
12-22 20:59:15.969  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:15.969  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:15.969  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:15.969  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:15.969  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:15.969  1423  2267 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:15.969  1423  2267 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:15.969  1423  2267 I WindowManager:     ROTATION_270={(0,0)(fillx66) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:15.969  1423  2267 I WindowManager:       fl=20040028
12-22 20:59:15.969  1423  2267 I WindowManager:       pfl=31000000
12-22 20:59:15.969  1423  2267 I WindowManager:       bhv=1
12-22 20:59:15.969  1423  2267 I WindowManager:       providedInsets:
12-22 20:59:15.969  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=38}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=38}}]}
12-22 20:59:15.969  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:15.969  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:15.969  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:15.969  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#53df0024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:15.969  1423  2267 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:15.969  1423  2267 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0} naviIconColor=0}
12-22 20:59:15.971  1423  2227 I WindowManager: Relayout Window{b5eb294 u0 NotificationShade}: viewVisibility=0 req=1080x2520 ty=2040 d0
12-22 20:59:15.971  1423  2227 I WindowManager: Relayout hash=b5eb294, pid=0, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NOTIFICATION_SHADE fmt=TRANSLUCENT
12-22 20:59:15.971  1423  2227 I WindowManager:   fl=81040048
12-22 20:59:15.971  1423  2227 I WindowManager:   pfl=10000200
12-22 20:59:15.971  1423  2227 I WindowManager:   bhv=2
12-22 20:59:15.971  1423  2227 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:15.971  1423  2227 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:15.971  1423  2227 I WindowManager:   sfl=40000}
12-22 20:59:15.972  1423  2267 I WindowManager: Relayout Window{6146b28 u0 NavigationBar0}: viewVisibility=0 req=1080x75 ty=2019 d0
12-22 20:59:15.974  1423  2267 I WindowManager: Relayout hash=6146b28, pid=0, syncId=-1: mAttrs={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:15.974  1423  2267 I WindowManager:   fl=20040028
12-22 20:59:15.974  1423  2267 I WindowManager:   pfl=31001000
12-22 20:59:15.974  1423  2267 I WindowManager:   bhv=1
12-22 20:59:15.974  1423  2267 I WindowManager:   providedInsets:
12-22 20:59:15.974  1423  2267 I WindowManager:     InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:15.974  1423  2267 I WindowManager:     InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:     InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:15.974  1423  2267 I WindowManager:     InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:     InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:15.974  1423  2267 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:15.974  1423  2267 I WindowManager:   paramsForRotation:
12-22 20:59:15.974  1423  2267 I WindowManager:     ROTATION_0={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:15.974  1423  2267 I WindowManager:       fl=20040028
12-22 20:59:15.974  1423  2267 I WindowManager:       pfl=31001000
12-22 20:59:15.974  1423  2267 I WindowManager:       bhv=1
12-22 20:59:15.974  1423  2267 I WindowManager:       providedInsets:
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:15.974  1423  2267 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:15.974  1423  2267 I WindowManager:     ROTATION_90={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:15.974  1423  2267 I WindowManager:       fl=20040028
12-22 20:59:15.974  1423  2267 I WindowManager:       pfl=31001000
12-22 20:59:15.974  1423  2267 I WindowManager:       bhv=1
12-22 20:59:15.974  1423  2267 I WindowManager:       providedInsets:
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=23}}]}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:15.974  1423  2267 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:15.974  1423  2267 I WindowManager:     ROTATION_180={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:15.974  1423  2267 I WindowManager:       fl=20040028
12-22 20:59:15.974  1423  2267 I WindowManager:       pfl=31001000
12-22 20:59:15.974  1423  2267 I WindowManager:       bhv=1
12-22 20:59:15.974  1423  2267 I WindowManager:       providedInsets:
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:15.974  1423  2267 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:15.974  1423  2267 I WindowManager:     ROTATION_270={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:15.974  1423  2267 I WindowManager:       fl=20040028
12-22 20:59:15.974  1423  2267 I WindowManager:       pfl=31001000
12-22 20:59:15.974  1423  2267 I WindowManager:       bhv=1
12-22 20:59:15.974  1423  2267 I WindowManager:       providedInsets:
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=23}}]}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:15.974  1423  2267 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:15.974  1423  2267 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0} naviIconColor=0}
12-22 20:59:15.982  1423  1555 I ActivityManager: Changes in 10228 19 to 11, 0 to 128
12-22 20:59:15.982  1423  1555 I ActivityManager: Changes in 10213 19 to 11, 0 to 128
12-22 20:59:15.991  1423  4799 I ActivityManager: Changes in 10228 11 to 19, 128 to 0
12-22 20:59:15.993  1423  1636 W ActivityManager: Unable to start service Intent { xflg=0x4 cmp=com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.TrackpadService (has extras) } U=0: not found
12-22 20:59:15.996  1423  4799 I ActivityManager: Changes in 10213 11 to 19, 128 to 0
12-22 20:59:16.008  1423  1473 I WindowManager: Relayout Window{4ad78ec u0 Bouncer}: viewVisibility=4 req=1080x0 ty=2009 d0
12-22 20:59:16.008  1423  1473 I WindowManager: Relayout hash=4ad78ec, pid=2022, syncId=-1: mAttrs={(0,0)(fillx0) gr=TOP CENTER_VERTICAL sim={adjust=resize} layoutInDisplayCutoutMode=shortEdges ty=KEYGUARD_DIALOG fmt=TRANSLUCENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY userActivityTimeout=10000
12-22 20:59:16.008  1423  1473 I WindowManager:   fl=85000548
12-22 20:59:16.008  1423  1473 I WindowManager:   pfl=10000000
12-22 20:59:16.008  1423  1473 I WindowManager:   vsysui=500
12-22 20:59:16.008  1423  1473 I WindowManager:   bhv=2
12-22 20:59:16.008  1423  1473 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:16.008  1423  1473 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:16.008  1423  1473 I WindowManager:  dimAmount=0.0 screenDimDuration=0 naviIconColor=0
12-22 20:59:16.008  1423  1473 I WindowManager:   sfl=40000}
12-22 20:59:16.009  1423  2265 I InputMethodManagerService: isInputMethodShown: isShown=false
12-22 20:59:16.010  1423  4808 I ActivityManager: Changes in 10122 10 to 5, 0 to 184
12-22 20:59:16.018  1423  4807 D InputDispatcher: Focused application(1): ActivityRecord{260071160 u0 com.android.systemui/.subscreen.SubHomeActivity t9}
12-22 20:59:16.019  1423  4807 D WindowManager: Changing focus from null to Window{e382cc2 u0 SubLauncherWindow} displayId=1 Callers=com.android.server.wm.ActivityTaskManagerService.setLastResumedActivityUncheckLocked:264 com.android.server.wm.ActivityTaskSupervisor.updateTopResumedActivityIfNeeded:49 com.android.server.wm.TaskFragment.setResumedActivity:30 com.android.server.wm.ActivityRecord.setState:100 
12-22 20:59:16.019  1423  4807 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0xf7412b2
12-22 20:59:16.019  1423  4807 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785, destroy=false, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc
12-22 20:59:16.019  1423  4807 I WindowManager: Reparenting to leash, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc, leashParent=Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785
12-22 20:59:16.019  1423  4807 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0x2522e3b
12-22 20:59:16.019  1423  4807 I WindowManager: getCoverScreenRotation 0, sensor= 0, isFlat=false, isDual=false
12-22 20:59:16.036  1423  2109 D InputDispatcher: Focus entered window (1): e382cc2 SubLauncherWindow
12-22 20:59:16.036  1423  2267 D InputDispatcher: Once focus requested (1): e382cc2 SubLauncherWindow
12-22 20:59:16.042 31283 31283 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{7bda02c G.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.setupTrackpad:938 com.example.coverscreentester.OverlayService.setupUI:822 
12-22 20:59:16.046  1423  1474 I WindowManager: Relayout Window{6146b28 u0 NavigationBar0}: viewVisibility=0 req=1080x75 ty=2019 d0
12-22 20:59:16.046 31283 31283 D ViewRootImpl: desktopMode is false
12-22 20:59:16.047  1423  2109 I WindowManager: Relayout Window{5a7dcc6 u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-22 20:59:16.048  1423  1474 I WindowManager: Relayout hash=6146b28, pid=0, syncId=-1: mAttrs={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:16.048  1423  1474 I WindowManager:   fl=40028
12-22 20:59:16.048  1423  1474 I WindowManager:   pfl=31001000
12-22 20:59:16.048  1423  1474 I WindowManager:   bhv=1
12-22 20:59:16.048  1423  1474 I WindowManager:   providedInsets:
12-22 20:59:16.048  1423  1474 I WindowManager:     InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:16.048  1423  1474 I WindowManager:     InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:     InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:16.048  1423  1474 I WindowManager:     InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:     InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:16.048  1423  1474 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:16.048  1423  1474 I WindowManager:   paramsForRotation:
12-22 20:59:16.048  1423  1474 I WindowManager:     ROTATION_0={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:16.048  1423  1474 I WindowManager:       fl=20040028
12-22 20:59:16.048  1423  1474 I WindowManager:       pfl=31001000
12-22 20:59:16.048  1423  1474 I WindowManager:       bhv=1
12-22 20:59:16.048  1423  1474 I WindowManager:       providedInsets:
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:16.048  1423  1474 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:16.048  1423  1474 I WindowManager:     ROTATION_90={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:16.048  1423  1474 I WindowManager:       fl=20040028
12-22 20:59:16.048  1423  1474 I WindowManager:       pfl=31001000
12-22 20:59:16.048  1423  1474 I WindowManager:       bhv=1
12-22 20:59:16.048  1423  1474 I WindowManager:       providedInsets:
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=23}}]}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:16.048  1423  1474 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:16.048  1423  1474 I WindowManager:     ROTATION_180={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:16.048  1423  1474 I WindowManager:       fl=20040028
12-22 20:59:16.048  1423  1474 I WindowManager:       pfl=31001000
12-22 20:59:16.048  1423  1474 I WindowManager:       bhv=1
12-22 20:59:16.048  1423  1474 I WindowManager:       providedInsets:
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=null}]}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:16.048  1423  1474 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:16.048  1423  1474 I WindowManager:     ROTATION_270={(0,0)(fillx75) gr=BOTTOM CENTER_VERTICAL layoutInDisplayCutoutMode=always ty=NAVIGATION_BAR fmt=TRANSLUCENT
12-22 20:59:16.048  1423  1474 I WindowManager:       fl=20040028
12-22 20:59:16.048  1423  1474 I WindowManager:       pfl=31001000
12-22 20:59:16.048  1423  1474 I WindowManager:       bhv=1
12-22 20:59:16.048  1423  1474 I WindowManager:       providedInsets:
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140001, index=0, type=navigationBars, source=FRAME, flags=[SUPPRESS_SCRIM], insetsSize=Insets{left=0, top=0, right=0, bottom=23}, insetsSizeOverrides=[TypedInsetsSize: {windowType=INPUT_METHOD, insetsSize=Insets{left=0, top=0, right=0, bottom=23}}]}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140006, index=0, type=tappableElement, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140005, index=0, type=mandatorySystemGestures, source=FRAME, flags=[], insetsSize=Insets{left=0, top=0, right=0, bottom=50}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140004, index=0, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=46, top=0, right=0, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=46, top=0, right=0, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:         InsetsFrameProvider: {id=#44140024, index=1, type=systemGestures, source=DISPLAY, flags=[], insetsSize=Insets{left=0, top=0, right=46, bottom=0}, mMinimalInsetsSizeInDisplayCutoutSafe=Insets{left=0, top=0, right=46, bottom=0}}
12-22 20:59:16.048  1423  1474 I WindowManager:       frameRateBoostOnTouch=true
12-22 20:59:16.048  1423  1474 I WindowManager:       dvrrWindowFrameRateHint=true naviIconColor=0} naviIconColor=0}
12-22 20:59:16.050 31283 31283 I ViewRootImpl: dVRR is disabled
12-22 20:59:16.052  1423  2109 I WindowManager: Relayout hash=5a7dcc6, pid=2022, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-22 20:59:16.052  1423  2109 I WindowManager:   fl=14318
12-22 20:59:16.052  1423  2109 I WindowManager:   pfl=14
12-22 20:59:16.052  1423  2109 I WindowManager:   bhv=1
12-22 20:59:16.052  1423  2109 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:16.052  1423  2109 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:16.052  1423  2109 I WindowManager:   sfl=8}
12-22 20:59:16.058  1423  2109 D WindowManager: finishDrawingWindow: Window{5a7dcc6 u0 com.android.systemui.wallpapers.ImageWallpaper} mDrawState=HAS_DRAWN seqId=2147483647
12-22 20:59:16.069  1423  2934 I WindowManager: Relayout Window{b5eb294 u0 NotificationShade}: viewVisibility=4 req=1080x2520 ty=2040 d0
12-22 20:59:16.069  1423  2934 E WindowManager: win=Window{b5eb294 u0 NotificationShade} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=4 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1462 android.os.Binder.execTransact:1401 
12-22 20:59:16.070  1423  2934 I WindowManager: Relayout hash=b5eb294, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP CENTER_VERTICAL sim={adjust=pan} layoutInDisplayCutoutMode=always ty=NOTIFICATION_SHADE fmt=TRANSLUCENT
12-22 20:59:16.070  1423  2934 I WindowManager:   fl=81040048
12-22 20:59:16.070  1423  2934 I WindowManager:   pfl=11000200
12-22 20:59:16.070  1423  2934 I WindowManager:   bhv=2
12-22 20:59:16.070  1423  2934 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:16.070  1423  2934 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:16.070  1423  2934 I WindowManager:   sfl=40000}
12-22 20:59:16.072  1423  2109 I WindowManager: Relayout Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}: viewVisibility=0 req=1422x1500 ty=1 d1
12-22 20:59:16.072  1423  2109 I WindowManager: Relayout hash=9a3d1c6, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY userActivityTimeout=30000
12-22 20:59:16.072  1423  2109 I WindowManager:   fl=81910100
12-22 20:59:16.072  1423  2109 I WindowManager:   pfl=10008850
12-22 20:59:16.072  1423  2109 I WindowManager:   bhv=1
12-22 20:59:16.072  1423  2109 I WindowManager:   fitSides=0
12-22 20:59:16.072  1423  2109 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:16.072  1423  2109 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=0 naviIconColor=0}
12-22 20:59:16.072 31283 31283 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{6c4f248 VFE...C.. ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.setupBubble:903 com.example.coverscreentester.OverlayService.setupUI:825 
12-22 20:59:16.073 31283 31283 D ViewRootImpl: desktopMode is false
12-22 20:59:16.073 31283 31283 I ViewRootImpl: dVRR is disabled
12-22 20:59:16.077  1423  1754 I InputMethodManagerService: startInputUncheckedLocked: Rejecting unbind/bind in case of no editor in cover screen.
12-22 20:59:16.084 31283 31283 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{604921d V.E...... ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.coverscreentester.OverlayService.setupCursor:957 com.example.coverscreentester.OverlayService.setupUI:826 
12-22 20:59:16.084 31283 31283 D ViewRootImpl: desktopMode is false
12-22 20:59:16.085 31283 31283 I ViewRootImpl: dVRR is disabled
12-22 20:59:16.094  1423  2934 D WindowManager: Starting window removed Window{2134168 u0 Splash Screen com.katsuyamaki.DroidOSTrackpadKeyboard}
12-22 20:59:16.101  1423  2109 W WindowManager: Failed looking up window session=Session{eef2b83 2022:u0a10055} callers=com.android.server.wm.WindowManagerService.windowForClientLocked:1 com.android.server.wm.WindowManagerService.relayoutWindow:84 com.android.server.wm.Session.relayout:27 
12-22 20:59:16.104  1423  2934 W WindowManager: Failed looking up window session=Session{eef2b83 2022:u0a10055} callers=com.android.server.wm.WindowManagerService.windowForClientLocked:1 com.android.server.wm.Session.setOnBackInvokedCallbackInfo:15 android.view.IWindowSession$Stub.onTransact:1390 
12-22 20:59:16.104  1423  2934 I WindowManager: setOnBackInvokedCallback(): No window state for package:com.android.systemui
12-22 20:59:16.105  1423  2697 W WindowManager: Cannot find window which accessibility connection is added to
12-22 20:59:16.105  1423  2109 W WindowManager: Failed looking up window session=Session{eef2b83 2022:u0a10055} callers=com.android.server.wm.Session.remove:10 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-22 20:59:16.105  1423  2109 I WindowManager: Relayout Window{27e4888 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=8 req=527x444 ty=2032 d0
12-22 20:59:16.106  1423  2109 I WindowManager: Relayout hash=27e4888, pid=31283, syncId=-1: mAttrs={(520,1916)(527x444) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:16.106  1423  2109 I WindowManager:   fl=1040228
12-22 20:59:16.106  1423  2109 I WindowManager:   pfl=40000000
12-22 20:59:16.106  1423  2109 I WindowManager:   bhv=1
12-22 20:59:16.106  1423  2109 I WindowManager:   fitTypes=207
12-22 20:59:16.106  1423  2109 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:16.106  1423  2109 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:16.109  1423  2109 I WindowManager: Relayout Window{a0238ff u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=93x93 ty=2032 d0
12-22 20:59:16.109  1423  2109 D WindowManager: makeSurface duration=1 name=$_31283
12-22 20:59:16.110  1423  2109 I WindowManager: Relayout hash=a0238ff, pid=31283, syncId=-1: mAttrs={(929,1211)(93x93) gr=TOP START CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:16.110  1423  2109 I WindowManager:   fl=1000308
12-22 20:59:16.110  1423  2109 I WindowManager:   bhv=1
12-22 20:59:16.110  1423  2109 I WindowManager:   fitTypes=206
12-22 20:59:16.110  1423  2109 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:16.110  1423  2109 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:16.136  1423  2697 D WindowManager: finishDrawingWindow: Window{a0238ff u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:16.137  1423  2934 I WindowManager: Relayout Window{1967d93 u0 com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=50x50 ty=2032 d0
12-22 20:59:16.137  1423  2934 D WindowManager: makeSurface duration=0 name=$_31283
12-22 20:59:16.138  1423  2934 I WindowManager: Relayout hash=1967d93, pid=31283, syncId=-1: mAttrs={(540,1260)(wrapxwrap) gr=TOP LEFT CENTER sim={adjust=pan} layoutInDisplayCutoutMode=shortEdges ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:16.138  1423  2934 I WindowManager:   fl=1000318
12-22 20:59:16.138  1423  2934 I WindowManager:   bhv=1
12-22 20:59:16.138  1423  2934 I WindowManager:   fitTypes=206
12-22 20:59:16.138  1423  2934 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:16.138  1423  2934 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:16.144  1423  2697 D WindowManager: finishDrawingWindow: Window{1967d93 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:16.151  1423  2697 D WindowManager: finishDrawingWindow: Window{a0238ff u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:16.160  1423  2109 D WindowManager: finishDrawingWindow: Window{1967d93 u0 com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:17.573  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x0, time=33717172543000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:17.574  1423  1695 I InputDispatcher: Delivering touch to (7845): action: 0x4, f=0x0, d=1, '2a89570', t=1 
12-22 20:59:17.574  1423  1695 I InputDispatcher: Delivering touch to (2022): action: 0x4, f=0x0, d=1, 'e983ff1', t=1 
12-22 20:59:17.575  1423  1695 I InputDispatcher: Delivering touch to (2022): action: 0x0, f=0x0, d=1, 'e382cc2', t=1 +(0,-35)*(1.50)
12-22 20:59:17.622  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x1, time=33717225275000, f=0x0, d=1 dsdx=1.500000 dtdx=-0.000000
12-22 20:59:17.623  1423  1695 I InputDispatcher: Delivering touch to (2022): action: 0x1, f=0x0, d=1, 'e382cc2', t=1 +(0,-35)*(1.50)
12-22 20:59:17.709  1423  2697 D WindowManager: Transition is created, t=TransitionRecord{fab65f4 id=-1 type=OPEN flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createAndStartCollecting:106 com.android.server.wm.ActivityStarter.executeRequest:3537 com.android.server.wm.ActivityStarter.execute:1873 com.android.server.wm.ActivityTaskManagerService.startActivityAsUser:87 
12-22 20:59:17.718  2022  2160 D ViewRootImpl: desktopMode is false
12-22 20:59:17.719  2022  2160 I ViewRootImpl: dVRR is disabled
12-22 20:59:17.720  1423  2697 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{e382cc2 u0 SubLauncherWindow}
12-22 20:59:17.720  1423  2697 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}
12-22 20:59:17.721  1423  2697 I WindowManager: getCoverScreenRotation 0, sensor= 0, isFlat=false, isDual=false
12-22 20:59:17.728  1423  2934 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{151e3bc u0 Splash Screen com.katsuyamaki.DroidOSTrackpadKeyboard}
12-22 20:59:17.729  1423  1473 I WindowManager: getCoverScreenRotation 0, sensor= 0, isFlat=false, isDual=false
12-22 20:59:17.731  1423  1473 D InputDispatcher: Focused application(1): ActivityRecord{63107357 u0 com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.MainActivity t19275}
12-22 20:59:17.731  1423  1473 D WindowManager: Changing focus from Window{e382cc2 u0 SubLauncherWindow} to null displayId=1 Callers=com.android.server.wm.ActivityTaskManagerService.setLastResumedActivityUncheckLocked:264 com.android.server.wm.ActivityTaskSupervisor.updateTopResumedActivityIfNeeded:49 com.android.server.wm.TaskFragment.setResumedActivity:30 com.android.server.wm.ActivityRecord.setState:100 
12-22 20:59:17.731  1423  1473 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0x2522e3b
12-22 20:59:17.731  1423  1473 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785, destroy=false, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc
12-22 20:59:17.731  1423  1473 I WindowManager: Reparenting to leash, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc, leashParent=Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785
12-22 20:59:17.731  1423  1473 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0xbf43bcb
12-22 20:59:17.737  1423  2697 W ActivityManager: Unable to start service Intent { xflg=0x4 cmp=com.katsuyamaki.DroidOSTrackpadKeyboard/com.example.coverscreentester.TrackpadService (has extras) } U=0: not found
12-22 20:59:17.737  1423  1473 I WindowManager: Relayout Window{151e3bc u0 Splash Screen com.katsuyamaki.DroidOSTrackpadKeyboard}: viewVisibility=0 req=1422x1169 ty=3 d1
12-22 20:59:17.737  1423  1473 D WindowManager: makeSurface duration=0 name=Splash Screen com.katsuyamaki.DroidOSTrackpadKeyboard$_2022
12-22 20:59:17.739  1423  1473 I WindowManager: Relayout hash=151e3bc, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=pan} ty=APPLICATION_STARTING fmt=TRANSLUCENT wanim=0x1030317
12-22 20:59:17.739  1423  1473 I WindowManager:   fl=81030118
12-22 20:59:17.739  1423  1473 I WindowManager:   pfl=10000010
12-22 20:59:17.739  1423  1473 I WindowManager:   bhv=1
12-22 20:59:17.739  1423  1473 I WindowManager:   fitSides=0
12-22 20:59:17.739  1423  1473 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:17.739  1423  1473 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:17.742  1423  4808 D InputDispatcher: Focused application(1): ActivityRecord{260071160 u0 com.android.systemui/.subscreen.SubHomeActivity t9}
12-22 20:59:17.742  1423  4808 D WindowManager: Changing focus from null to Window{e382cc2 u0 SubLauncherWindow} displayId=1 Callers=com.android.server.wm.ActivityTaskManagerService.setLastResumedActivityUncheckLocked:264 com.android.server.wm.ActivityTaskSupervisor.updateTopResumedActivityIfNeeded:49 com.android.server.wm.TaskFragment.setResumedActivity:30 com.android.server.wm.ActivityRecord.setState:100 
12-22 20:59:17.742  1423  4808 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0xbf43bcb
12-22 20:59:17.742  1423  4808 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785, destroy=false, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc
12-22 20:59:17.742  1423  4808 I WindowManager: Reparenting to leash, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc, leashParent=Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785
12-22 20:59:17.742  1423  4808 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0xb58ee54
12-22 20:59:17.743  1423  4808 I WindowManager: getCoverScreenRotation 0, sensor= 0, isFlat=false, isDual=false
12-22 20:59:17.745  1423  2109 D WindowManager: finishDrawingWindow: Window{151e3bc u0 Splash Screen com.katsuyamaki.DroidOSTrackpadKeyboard} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:17.745  1423  4808 D InputDispatcher: Once focus requested (1): <null>
12-22 20:59:17.745  1423  4808 D InputDispatcher: Focus request (1): <null> but waiting because NO_WINDOW
12-22 20:59:17.745  1423  4808 D InputDispatcher: Focus left window (1): e382cc2 SubLauncherWindow
12-22 20:59:17.754  1423  4808 D WindowManager: Starting window removed Window{151e3bc u0 Splash Screen com.katsuyamaki.DroidOSTrackpadKeyboard}
12-22 20:59:17.761  1423  4808 W WindowManager: Failed looking up window session=Session{eef2b83 2022:u0a10055} callers=com.android.server.wm.WindowManagerService.windowForClientLocked:1 com.android.server.wm.WindowManagerService.relayoutWindow:84 com.android.server.wm.Session.relayout:27 
12-22 20:59:17.762  1423  4808 D InputDispatcher: Once focus requested (1): e382cc2 SubLauncherWindow
12-22 20:59:17.762  1423  4808 D InputDispatcher: Focus request (1): e382cc2 SubLauncherWindow but waiting because NO_WINDOW
12-22 20:59:17.763  1423  2934 D InputDispatcher: Focus entered window (1): e382cc2 SubLauncherWindow
12-22 20:59:17.763  1423  2109 W WindowManager: Failed looking up window session=Session{eef2b83 2022:u0a10055} callers=com.android.server.wm.WindowManagerService.windowForClientLocked:1 com.android.server.wm.Session.setOnBackInvokedCallbackInfo:15 android.view.IWindowSession$Stub.onTransact:1390 
12-22 20:59:17.763  1423  2109 I WindowManager: setOnBackInvokedCallback(): No window state for package:com.android.systemui
12-22 20:59:17.764  1423  2934 W WindowManager: Cannot find window which accessibility connection is added to
12-22 20:59:17.764  1423  1473 W WindowManager: Failed looking up window session=Session{eef2b83 2022:u0a10055} callers=com.android.server.wm.Session.remove:10 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-22 20:59:17.770  1423  1754 I InputMethodManagerService: startInputUncheckedLocked: Rejecting unbind/bind in case of no editor in cover screen.
12-22 20:59:18.804  1423  1549 D WindowManager: onStateChanged, state=1
12-22 20:59:18.808  1423  1549 D WindowManager: rotationForOrientation, orientationSource=DefaultTaskDisplayArea_d0@131230281
12-22 20:59:18.808  1423  1549 I WindowManager: getCoverScreenRotation 0, sensor= 0, isFlat=false, isDual=false
12-22 20:59:19.045  1423  1549 D WindowManager: onStateChanged, state=2
12-22 20:59:19.056  1423  1549 D WindowManager: Changing focus from Window{e382cc2 u0 SubLauncherWindow} to null displayId=1 Callers=com.android.server.wm.RootWindowContainer.onChildPositionChanged:8 com.android.server.wm.WindowContainer.positionChildAt:69 com.android.server.wm.RootWindowContainer.positionChildAt:8 com.android.server.wm.WindowManagerServiceExt.moveDisplayToTop:37 
12-22 20:59:19.056  1423  1549 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0xb58ee54
12-22 20:59:19.056  1423  1549 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785, destroy=false, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc
12-22 20:59:19.057  1423  1549 I WindowManager: Reparenting to leash, surface=Surface(name=e983ff1 NavigationBar1)/@0x74594fc, leashParent=Surface(name=WindowToken{4641b98 type=2019 android.os.BinderProxy@d3c750a})/@0x2ed6785
12-22 20:59:19.057  1423  1549 D WindowManager: makeSurface duration=1 leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0x4e1158f
12-22 20:59:19.058  1423  1549 D InputDispatcher: Focused display: 1 -> 0
12-22 20:59:19.058  1423  1549 W InputDispatcher: Focused display #0 does not have a focused window.
12-22 20:59:19.058  1423  1549 E InputDispatcher: But another display has a focused window
12-22 20:59:19.058  1423  1549 E InputDispatcher:   FocusedWindows:
12-22 20:59:19.058  1423  1549 E InputDispatcher:     displayId=1, name='e382cc2 SubLauncherWindow'
12-22 20:59:19.060  1423  1549 I WindowManager: getCoverScreenRotation 0, sensor= 0, isFlat=false, isDual=false
12-22 20:59:19.064  2022  2022 I WindowManager: WindowManagerGlobal#removeView, ty=2019, view=com.android.systemui.navigationbar.views.NavigationBarFrame{7824b18 V.E...... ......I. 0,0-1422,66 #7f0a083a app:id/navigation_bar_frame}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeViewImmediate:216 com.android.systemui.navigationbar.views.NavigationBar.destroyView:32 
12-22 20:59:19.064  2022  2022 I WindowManager: WindowManagerGlobal#removeView, ty=2024, view=com.android.systemui.navigationbar.gestural.QuickswitchOrientedNavHandle{2671829 G.E...... ......I. 0,0-0,0 #7f0a0b72 app:id/secondary_home_handle}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.android.systemui.navigationbar.views.NavigationBar.onViewDetached:75 
12-22 20:59:19.066  1423  1604 D WindowManager: Create SleepToken: tag=Display-off, displayId=1
12-22 20:59:19.069  1423  4800 I WindowManager: Cancelling animation restarting=false, leash=Surface(name=Surface(name=e983ff1 NavigationBar1)/@0x74594fc - animation-leash of insets_animation)/@0x4e1158f
12-22 20:59:19.073  2022  2022 I WindowManager: WindowManagerGlobal#removeView, ty=2024, view=com.android.systemui.navigationbar.gestural.SamsungBackPanel{3740d0b G.ED..... ......I. 0,0-164,419}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.android.systemui.navigationbar.gestural.BackPanelController.onDestroy:12 
12-22 20:59:19.077  1423  4800 D InputDispatcher: Once focus requested (1): <null>
12-22 20:59:19.077  1423  4800 D InputDispatcher: Focus request (1): <null> but waiting because NO_WINDOW
12-22 20:59:19.077  1423  4800 D InputDispatcher: Focus left window (1): e382cc2 SubLauncherWindow
12-22 20:59:19.082  1423  1549 I ActivityManager: Changes in 10375 2 to 12, 255 to 0
12-22 20:59:19.092  1423  1604 D WindowManager: Remove SleepToken: tag=Display-off, displayId=0
12-22 20:59:19.092  1423  1604 D WindowManager: Transition is created, t=TransitionRecord{49b523 id=-1 type=WAKE flags=0x0}, caller=com.android.server.wm.Transition.<init>:182 com.android.server.wm.TransitionController.createTransition:17 com.android.server.wm.RootWindowContainer.applySleepTokens:209 com.android.server.wm.ActivityTaskManagerService.updateSleepIfNeededLocked:90 com.android.server.wm.RootWindowContainer.removeSleepToken:131 
12-22 20:59:19.093  1423  1604 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{f704bfc u0 com.termux/com.termux.app.TermuxActivity}
12-22 20:59:19.099  1423  1604 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{64185e1 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}
12-22 20:59:19.099  1423  1604 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{e382cc2 u0 SubLauncherWindow}
12-22 20:59:19.099  1423  1604 D WindowManager: prepareSync <SYNC_STATE_WAITING_FOR_DRAW>, mPrepareSyncSeqId=0, win=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}
12-22 20:59:19.100  1423  1604 D WindowManager: rotationForOrientation, orientationSource=ActivityRecord{127106347 u0 com.sec.android.app.launcher/.activities.LauncherActivity t18982}
12-22 20:59:19.104  1423  2109 I WindowManager: Relayout Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}: viewVisibility=0 req=1422x1500 ty=1 d1
12-22 20:59:19.104  1423  2109 D WindowManager: updateSystemBarAttributes: displayId=1, focusedCanBeNavColorWin=false, win=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}, navColorWin=null, caller=com.android.server.wm.DisplayPolicy.finishPostLayoutPolicyLw:17 com.android.server.wm.RootWindowContainer.applySurfaceChangesTransaction$1:195 com.android.server.wm.RootWindowContainer.performSurfacePlacementNoTrace:86 
12-22 20:59:19.104  1423  2109 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[], requestedVisibilities=-11
12-22 20:59:19.106  1423  2109 I WindowManager: Relayout hash=9a3d1c6, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=pan forwardNavigation} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY userActivityTimeout=30000
12-22 20:59:19.106  1423  2109 I WindowManager:   fl=81910100
12-22 20:59:19.106  1423  2109 I WindowManager:   pfl=10008850
12-22 20:59:19.106  1423  2109 I WindowManager:   vsysui=1600000
12-22 20:59:19.106  1423  2109 I WindowManager:   bhv=1
12-22 20:59:19.106  1423  2109 I WindowManager:   fitSides=0
12-22 20:59:19.106  1423  2109 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.106  1423  2109 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=0 naviIconColor=0}
12-22 20:59:19.106  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(948 x 1048) immediately
12-22 20:59:19.107 22381 22381 D ViewRootImpl: Skipping stats log for color mode
12-22 20:59:19.113  1423  1855 I ActivityManager: Changes in 10342 4 to 2, 254 to 255
12-22 20:59:19.115  1423  2784 I WindowManager: Relayout Window{e382cc2 u0 SubLauncherWindow}: viewVisibility=0 req=1422x1500 ty=1000 d1
12-22 20:59:19.115  1423  2784 I WindowManager: Relayout hash=e382cc2, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize} layoutInDisplayCutoutMode=always ty=APPLICATION_PANEL fmt=TRANSLUCENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY
12-22 20:59:19.115  1423  2784 I WindowManager:   fl=80080260
12-22 20:59:19.115  1423  2784 I WindowManager:   vsysui=1600400
12-22 20:59:19.115  1423  2784 I WindowManager:   bhv=1
12-22 20:59:19.115  1423  2784 I WindowManager:   fitTypes=206
12-22 20:59:19.115  1423  2784 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.115  1423  2784 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:19.118  1423  1855 I ActivityManager: Changes in 10155 5 to 2, 184 to 255
12-22 20:59:19.121  1423  2265 I ActivityManager: Changes in 10395 19 to 3, 0 to 144
12-22 20:59:19.123  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-22 20:59:19.127  1423  2109 I WindowManager: Relayout Window{d2cc959 u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=-1x-1 ty=2013 d1
12-22 20:59:19.130  1423  1547 I WindowManager: Waiting for transition
12-22 20:59:19.130  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:19.131  1423  2109 I WindowManager: Relayout hash=d2cc959, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-22 20:59:19.131  1423  2109 I WindowManager:   fl=10318
12-22 20:59:19.131  1423  2109 I WindowManager:   pfl=14
12-22 20:59:19.131  1423  2109 I WindowManager:   bhv=1
12-22 20:59:19.131  1423  2109 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.131  1423  2109 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:19.145  1423  1549 I ActivityManager: Changes in 10375 12 to 2, 0 to 255
12-22 20:59:19.153  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-22 20:59:19.154  1423  1547 I WindowManager: Waiting for transition
12-22 20:59:19.154  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:19.154  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(948 x 1048) immediately
12-22 20:59:19.156  1423  1547 I WindowManager: Waiting for transition
12-22 20:59:19.158  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:19.176  1423  2109 I WindowManager: Relayout Window{64185e1 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}: viewVisibility=0 req=1080x2520 ty=1 d0
12-22 20:59:19.176  1423  2109 D WindowManager: makeSurface duration=0 name=com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity$_2712
12-22 20:59:19.177  1423  2109 I WindowManager: Waiting for transition
12-22 20:59:19.177  1423  2109 I WindowManager: Relayout hash=64185e1, pid=2712, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=nothing} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT wanim=0x1030306
12-22 20:59:19.177  1423  2109 I WindowManager:   fl=81910100
12-22 20:59:19.177  1423  2109 I WindowManager:   pfl=10008840
12-22 20:59:19.177  1423  2109 I WindowManager:   bhv=2
12-22 20:59:19.177  1423  2109 I WindowManager:   fitSides=0
12-22 20:59:19.177  1423  2109 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.177  1423  2109 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=200000 naviIconColor=0}
12-22 20:59:19.177  1423  1474 I WindowManager: Relayout Window{e382cc2 u0 SubLauncherWindow}: viewVisibility=0 req=1422x1500 ty=1000 d1
12-22 20:59:19.178  1423  1474 I WindowManager: Waiting for transition
12-22 20:59:19.178  1423  1474 I WindowManager: Waiting for transition
12-22 20:59:19.179  1423  4812 I WindowManager: Relayout Window{f704bfc u0 com.termux/com.termux.app.TermuxActivity}: viewVisibility=0 req=1080x1260 ty=1 d0
12-22 20:59:19.179  1423  4812 D WindowManager: makeSurface duration=0 name=com.termux/com.termux.app.TermuxActivity$_22381
12-22 20:59:19.179  1423  1474 I WindowManager: Relayout hash=e382cc2, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize} layoutInDisplayCutoutMode=always ty=APPLICATION_PANEL fmt=TRANSLUCENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY
12-22 20:59:19.179  1423  1474 I WindowManager:   fl=81080278
12-22 20:59:19.179  1423  1474 I WindowManager:   vsysui=1600400
12-22 20:59:19.179  1423  1474 I WindowManager:   bhv=1
12-22 20:59:19.179  1423  1474 I WindowManager:   fitTypes=206
12-22 20:59:19.179  1423  1474 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.179  1423  1474 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:19.179  1423  4812 I WindowManager: Waiting for transition
12-22 20:59:19.180  1423  4812 D WindowManager: Changing focus from null to Window{f704bfc u0 com.termux/com.termux.app.TermuxActivity} displayId=0 Callers=com.android.server.wm.WindowManagerService.relayoutWindow:1359 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 
12-22 20:59:19.180  1423  4812 I WindowManager: Cancelling animation restarting=true, leash=Surface(name=Surface(name=6146b28 NavigationBar0)/@0x1b76ee0 - animation-leash of insets_animation)/@0x1073eb5
12-22 20:59:19.180  1423  4812 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{abeb61a type=2019 android.os.BinderProxy@2cf173c})/@0x22a599, destroy=false, surface=Surface(name=6146b28 NavigationBar0)/@0x1b76ee0
12-22 20:59:19.180  1423  4812 I WindowManager: Reparenting to leash, surface=Surface(name=6146b28 NavigationBar0)/@0x1b76ee0, leashParent=Surface(name=WindowToken{abeb61a type=2019 android.os.BinderProxy@2cf173c})/@0x22a599
12-22 20:59:19.180  1423  4812 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=6146b28 NavigationBar0)/@0x1b76ee0 - animation-leash of insets_animation)/@0x2068762
12-22 20:59:19.180  1423  4812 I WindowManager: Cancelling animation restarting=false, leash=Surface(name=Surface(name=88c439 StatusBar)/@0x6820c3f - animation-leash of insets_animation)/@0x24f757a
12-22 20:59:19.180  1423  4812 I WindowManager: Reparenting to original parent: Surface(name=WindowToken{9be6400 type=2000 android.os.BinderProxy@ec85d32})/@0xcd4990c, destroy=false, surface=Surface(name=88c439 StatusBar)/@0x6820c3f
12-22 20:59:19.180  1423  4812 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{f704bfc u0 com.termux/com.termux.app.TermuxActivity}, navColorWin=null, caller=com.android.server.wm.WindowManagerService.updateFocusedWindowLocked:372 com.android.server.wm.WindowManagerService.relayoutWindow:1359 com.android.server.wm.Session.relayout:27 
12-22 20:59:19.180  1423  4812 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[], requestedVisibilities=-9
12-22 20:59:19.181  1423  4812 I WindowManager: Relayout hash=f704bfc, pid=22381, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize} ty=BASE_APPLICATION wanim=0x1030317
12-22 20:59:19.181  1423  4812 I WindowManager:   fl=8d810100
12-22 20:59:19.181  1423  4812 I WindowManager:   pfl=10008040
12-22 20:59:19.181  1423  4812 I WindowManager:   vsysui=700
12-22 20:59:19.181  1423  4812 I WindowManager:   bhv=1
12-22 20:59:19.181  1423  4812 I WindowManager:   fitSides=0
12-22 20:59:19.181  1423  4812 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.181  1423  4812 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:19.181  1423  4800 I WindowManager: Relayout Window{2044d8a u0 com.google.android.googlequicksearchbox/com.google.android.apps.search.assistant.surfaces.voice.robin.main.MainActivity}: viewVisibility=8 req=1080x1260 ty=1 d0
12-22 20:59:19.181  1423  4800 I WindowManager: Waiting for transition
12-22 20:59:19.182  1423  4800 I WindowManager: Relayout hash=2044d8a, pid=14008, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize forwardNavigation} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION wanim=0x1030317
12-22 20:59:19.182  1423  4800 I WindowManager:   fl=80810100
12-22 20:59:19.182  1423  4800 I WindowManager:   pfl=10008840
12-22 20:59:19.182  1423  4800 I WindowManager:   bhv=1
12-22 20:59:19.182  1423  4800 I WindowManager:   fitSides=0
12-22 20:59:19.182  1423  4800 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.182  1423  4800 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:19.188  1423  2220 D WindowManager: finishDrawingWindow: Window{64185e1 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:19.190  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:19.191  1423  1550 I WindowManager: Waiting for transition
12-22 20:59:19.199  1423  1549 D WindowManager: onStateChanged, state=3
12-22 20:59:19.200  1423  2697 D WindowManager: finishDrawingWindow: Window{f704bfc u0 com.termux/com.termux.app.TermuxActivity} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:19.203  1423  1550 D WindowManager: All windows drawn!
12-22 20:59:19.204  1423  1550 I WindowManager: Reparenting to leash, surface=Surface(name=88c439 StatusBar)/@0x6820c3f, leashParent=Surface(name=WindowToken{9be6400 type=2000 android.os.BinderProxy@ec85d32})/@0xcd4990c
12-22 20:59:19.204  1423  1550 D WindowManager: makeSurface duration=0 leash=Surface(name=Surface(name=88c439 StatusBar)/@0x6820c3f - animation-leash of insets_animation)/@0x5a55ec2
12-22 20:59:19.204  1423  1550 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{f704bfc u0 com.termux/com.termux.app.TermuxActivity}, navColorWin=Window{64185e1 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.DisplayPolicy.finishPostLayoutPolicyLw:17 com.android.server.wm.RootWindowContainer.applySurfaceChangesTransaction$1:195 com.android.server.wm.RootWindowContainer.performSurfacePlacementNoTrace:86 
12-22 20:59:19.204  1423  1550 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-9
12-22 20:59:19.205  1423  1547 D WindowManager: finishScreenTurningOn: mAwake=true, mScreenOnEarly=true, mScreenOnFully=false, mKeyguardDrawComplete=true, mWindowManagerDrawComplete=true
12-22 20:59:19.208  2022  2106 D ViewRootImpl: desktopMode is false
12-22 20:59:19.209  2022  2106 I ViewRootImpl: dVRR is disabled
12-22 20:59:19.209  1423  2697 D InputDispatcher: Once focus requested (0): f704bfc com.termux/com.termux.app.TermuxActivity
12-22 20:59:19.209  1423  2697 D InputDispatcher: Focus request (0): f704bfc com.termux/com.termux.app.TermuxActivity but waiting because NOT_VISIBLE
12-22 20:59:19.221  1423  2109 W WindowManager: Cannot find window which accessibility connection is added to
12-22 20:59:19.232  1423  1474 W WindowManager: Failed looking up window session=Session{eef2b83 2022:u0a10055} callers=com.android.server.wm.Session.remove:10 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-22 20:59:19.233  1423  2220 W WindowManager: Failed looking up window session=Session{eef2b83 2022:u0a10055} callers=com.android.server.wm.Session.remove:10 android.view.IWindowSession$Stub.onTransact:804 com.android.server.wm.Session.onTransact:1 
12-22 20:59:19.239  1423  2934 D InputDispatcher: Focus entered window (0): f704bfc com.termux/com.termux.app.TermuxActivity
12-22 20:59:19.252  1423  2934 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=19260)/@0xd898151} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-22 20:59:19.258  1423  4808 I WindowManager: Relayout Window{5a7dcc6 u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-22 20:59:19.262  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-22 20:59:19.263  1423  4808 I WindowManager: Relayout hash=5a7dcc6, pid=2022, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-22 20:59:19.263  1423  4808 I WindowManager:   fl=14318
12-22 20:59:19.263  1423  4808 I WindowManager:   pfl=14
12-22 20:59:19.263  1423  4808 I WindowManager:   bhv=1
12-22 20:59:19.263  1423  4808 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.263  1423  4808 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:19.263  1423  4808 I WindowManager:   sfl=8}
12-22 20:59:19.264  1423  1754 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-22 20:59:19.264  1423  1754 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-22 20:59:19.264  1423  1754 D InputMethodManagerService: checkDisplayOfStartInputAndUpdateKeyboard: displayId=0, mFocusedDisplayId=0
12-22 20:59:19.264  1423  1754 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-22 20:59:19.265  1423  1754 I ActivityManager: Changes in 10332 7 to 5, 128 to 255
12-22 20:59:19.266  1423  1754 I InputMethodManagerService: attachNewInputLocked: showCurrentInputInternal, softInputModeState=STATE_UNSPECIFIED|ADJUST_RESIZE
12-22 20:59:19.266  1423  4812 I InputMethodManagerService: isAccessoryKeyboard 0
12-22 20:59:19.267  1423  1855 I InputMethodManagerService: isAccessoryKeyboard 0
12-22 20:59:19.268  1423  2934 D WindowManager: finishDrawingWindow: Window{5a7dcc6 u0 com.android.systemui.wallpapers.ImageWallpaper} mDrawState=HAS_DRAWN seqId=2147483647
12-22 20:59:19.269  1423  4800 D WindowManager: updateSystemBarAttributes: displayId=0, focusedCanBeNavColorWin=false, win=Window{f704bfc u0 com.termux/com.termux.app.TermuxActivity}, navColorWin=Window{64185e1 u0 com.sec.android.app.launcher/com.sec.android.app.launcher.activities.LauncherActivity}, caller=com.android.server.wm.InsetsStateController.onRequestedVisibleTypesChanged:86 com.android.server.wm.WindowManagerService.updateDisplayWindowRequestedVisibleTypes:148 android.view.IWindowManager$Stub.onTransact:3601 
12-22 20:59:19.269  1423  4800 D WindowManager: updateSystemBarAttributes, bhv=1, apr=0, statusBarAprRegions=[AppearanceRegion{ bounds=[0,0][1080,2520]}], requestedVisibilities=-1
12-22 20:59:19.276  1423  2071 I WindowManager: Relayout Window{e382cc2 u0 SubLauncherWindow}: viewVisibility=8 req=1422x1500 ty=1000 d1
12-22 20:59:19.280  1423  2071 I WindowManager: Relayout hash=e382cc2, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=resize} layoutInDisplayCutoutMode=always ty=APPLICATION_PANEL fmt=TRANSLUCENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY
12-22 20:59:19.280  1423  2071 I WindowManager:   fl=81080278
12-22 20:59:19.280  1423  2071 I WindowManager:   vsysui=1600400
12-22 20:59:19.280  1423  2071 I WindowManager:   bhv=1
12-22 20:59:19.280  1423  2071 I WindowManager:   fitTypes=206
12-22 20:59:19.280  1423  2071 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.280  1423  2071 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:19.280  1423  2934 D InputMethodManagerService: isImeSwitcherDisabledPackage : false
12-22 20:59:19.280  1423  2934 D InputMethodManagerService: shouldShowImeSwitcherLocked : checking vis : 3
12-22 20:59:19.281  1423  2934 I InputMethodManagerService: setImeWindowStatus: vis=3, backDisposition=0
12-22 20:59:19.284  1423  1549 D InputMethodManagerService: ACCESS_CONTROL_ENABLED = false, ACCESS_CONTROL_KEYBOARD_BLOCK = true
12-22 20:59:19.285  1423  2071 I WindowManager: Relayout Window{f748235 u0 InputMethod}: viewVisibility=0 req=1080x2404 ty=2011 d0
12-22 20:59:19.285  1423  2071 D WindowManager: makeSurface duration=0 name=InputMethod$_4255
12-22 20:59:19.286  1423  2071 I WindowManager: Relayout hash=f748235, pid=4255, syncId=-1: mAttrs={(0,0)(fillxfill) gr=BOTTOM CENTER_VERTICAL sim={adjust=pan} ty=INPUT_METHOD fmt=TRANSPARENT wanim=0x1030056 preferredMinDisplayRefreshRate=60.0 receive insets ignoring z-order
12-22 20:59:19.286  1423  2071 I WindowManager:   fl=81800108
12-22 20:59:19.286  1423  2071 I WindowManager:   pfl=14000000
12-22 20:59:19.286  1423  2071 I WindowManager:   bhv=1
12-22 20:59:19.286  1423  2071 I WindowManager:   fitTypes=3
12-22 20:59:19.286  1423  2071 I WindowManager:   fitSides=7
12-22 20:59:19.286  1423  2071 I WindowManager:   fitIgnoreVis
12-22 20:59:19.286  1423  2071 I WindowManager:   dvrrWindowFrameRateHint=true
12-22 20:59:19.286  1423  2071 I WindowManager:  dimAmount=0.18 dimDuration=150 naviIconColor=0}
12-22 20:59:19.286  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-22 20:59:19.288  1423  2784 I WindowManager: Relayout Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity}: viewVisibility=8 req=1422x1500 ty=1 d1
12-22 20:59:19.289  1423  2784 I WindowManager: Relayout hash=9a3d1c6, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=pan forwardNavigation} layoutInDisplayCutoutMode=always ty=BASE_APPLICATION fmt=TRANSPARENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY userActivityTimeout=30000
12-22 20:59:19.289  1423  2784 I WindowManager:   fl=81910100
12-22 20:59:19.289  1423  2784 I WindowManager:   pfl=10008850
12-22 20:59:19.289  1423  2784 I WindowManager:   vsysui=1600000
12-22 20:59:19.289  1423  2784 I WindowManager:   bhv=1
12-22 20:59:19.289  1423  2784 I WindowManager:   fitSides=0
12-22 20:59:19.289  1423  2784 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.289  1423  2784 I WindowManager:   dvrrWindowFrameRateHint=true screenDimDuration=0 naviIconColor=0}
12-22 20:59:19.290  1423  2934 D WindowManager: setInsetsWindow Window{f748235 u0 InputMethod}, contentInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), visibleInsets=Rect(0, 2329 - 0, 0) -> Rect(0, 1904 - 0, 0), touchableRegion=SkRegion() -> SkRegion((0,1904,1080,2404)), touchableInsets 3 -> 3
12-22 20:59:19.293  1423  2697 E WindowManager: win=Window{e382cc2 u0 SubLauncherWindow} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowState.destroySurface:24 com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:192 com.android.server.wm.ActivityClientController.activityStopped:97 android.app.IActivityClientController$Stub.onTransact:726 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1462 
12-22 20:59:19.294  1423  2697 E WindowManager: win=Window{9a3d1c6 u0 com.android.systemui/com.android.systemui.subscreen.SubHomeActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:192 com.android.server.wm.ActivityClientController.activityStopped:97 android.app.IActivityClientController$Stub.onTransact:726 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1462 android.os.Binder.execTransact:1401 
12-22 20:59:19.295  1423  4800 I WindowManager: Relayout Window{5a7dcc6 u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-22 20:59:19.297  1423  1473 I InputMethodManagerService: isAccessoryKeyboard 0
12-22 20:59:19.298  1423  4794 I InputMethodManagerService: isAccessoryKeyboard 0
12-22 20:59:19.298  1423  4800 I WindowManager: Relayout hash=5a7dcc6, pid=2022, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-22 20:59:19.298  1423  4800 I WindowManager:   fl=14318
12-22 20:59:19.298  1423  4800 I WindowManager:   pfl=14
12-22 20:59:19.298  1423  4800 I WindowManager:   bhv=1
12-22 20:59:19.298  1423  4800 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.298  1423  4800 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:19.298  1423  4800 I WindowManager:   sfl=8}
12-22 20:59:19.298  1423  2071 D WindowManager: finishDrawingWindow: Window{f748235 u0 InputMethod} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:19.312  1423  2697 I WindowManager: Relayout Window{5a7dcc6 u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-22 20:59:19.313  1423  2697 I WindowManager: Relayout hash=5a7dcc6, pid=2022, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-22 20:59:19.313  1423  2697 I WindowManager:   fl=14318
12-22 20:59:19.313  1423  2697 I WindowManager:   pfl=14
12-22 20:59:19.313  1423  2697 I WindowManager:   bhv=1
12-22 20:59:19.313  1423  2697 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.313  1423  2697 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:19.313  1423  2697 I WindowManager:   sfl=8}
12-22 20:59:19.313  1423  4794 I WindowManager: Relayout Window{5a7dcc6 u0 com.android.systemui.wallpapers.ImageWallpaper}: viewVisibility=0 req=2486x2520 ty=2013 d0
12-22 20:59:19.314  1423  1473 D WindowManager: finishDrawingWindow: Window{f748235 u0 InputMethod} mDrawState=HAS_DRAWN seqId=0
12-22 20:59:19.315  1423  4794 I WindowManager: Relayout hash=5a7dcc6, pid=2022, syncId=-1: mAttrs={(0,0)(2486x2520) gr=TOP START CENTER layoutInDisplayCutoutMode=always ty=WALLPAPER fmt=RGBX_8888 wanim=0x1030328
12-22 20:59:19.315  1423  4794 I WindowManager:   fl=14318
12-22 20:59:19.315  1423  4794 I WindowManager:   pfl=14
12-22 20:59:19.315  1423  4794 I WindowManager:   bhv=1
12-22 20:59:19.315  1423  4794 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.315  1423  4794 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0
12-22 20:59:19.315  1423  4794 I WindowManager:   sfl=8}
12-22 20:59:19.316  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-22 20:59:19.347  1423  1473 I WindowManager: Relayout Window{e382cc2 u0 SubLauncherWindow}: viewVisibility=8 req=1422x1500 ty=1000 d1
12-22 20:59:19.348  1423  1473 I WindowManager: Relayout hash=e382cc2, pid=2022, syncId=-1: mAttrs={(0,0)(fillxfill) sim={adjust=pan} layoutInDisplayCutoutMode=always ty=APPLICATION_PANEL fmt=TRANSLUCENT if=INPUT_FEATURE_DISABLE_USER_ACTIVITY
12-22 20:59:19.348  1423  1473 I WindowManager:   fl=81080278
12-22 20:59:19.348  1423  1473 I WindowManager:   vsysui=1600400
12-22 20:59:19.348  1423  1473 I WindowManager:   bhv=1
12-22 20:59:19.348  1423  1473 I WindowManager:   fitTypes=206
12-22 20:59:19.348  1423  1473 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:19.348  1423  1473 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:19.634  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-22 20:59:19.638  1423  2220 I ActivityManager: Changes in 10053 15 to 6, 0 to 128
12-22 20:59:19.836  1423  1423 I WindowManager: getCoverScreenRotation 0, sensor= 0, isFlat=false, isDual=false
12-22 20:59:20.145  7845  7845 I WindowManager: WindowManagerGlobal#removeView, ty=2032, view=android.widget.FrameLayout{d4e1542 VFE...C.. ........ 0,0-63,63}, caller=android.view.WindowManagerGlobal.removeView:654 android.view.WindowManagerImpl.removeView:211 com.example.quadrantlauncher.FloatingLauncherService.performDisplayChange:1054 
12-22 20:59:20.176  7845  7845 I WindowManager: WindowManagerGlobal#addView, ty=2032, view=android.widget.FrameLayout{61f4511 VFE...C.. ......I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 com.example.quadrantlauncher.FloatingLauncherService.setupBubble:607 com.example.quadrantlauncher.FloatingLauncherService.performDisplayChange:1071 
12-22 20:59:20.178  7845  7845 D ViewRootImpl: desktopMode is false
12-22 20:59:20.178  7845  7845 I ViewRootImpl: dVRR is disabled
12-22 20:59:20.217  1423  4794 I WindowManager: Relayout Window{29eb9ea u0 com.katsuyamaki.DroidOSLauncher}: viewVisibility=0 req=63x63 ty=2032 d0
12-22 20:59:20.218  1423  4794 D WindowManager: makeSurface duration=0 name=$_7845
12-22 20:59:20.220  1423  4794 I WindowManager: Relayout hash=29eb9ea, pid=7845, syncId=-1: mAttrs={(50,200)(wrapxwrap) gr=TOP START CENTER sim={adjust=pan} ty=ACCESSIBILITY_OVERLAY fmt=TRANSLUCENT
12-22 20:59:20.220  1423  4794 I WindowManager:   fl=1040308
12-22 20:59:20.220  1423  4794 I WindowManager:   bhv=1
12-22 20:59:20.220  1423  4794 I WindowManager:   fitTypes=206
12-22 20:59:20.220  1423  4794 I WindowManager:   frameRateBoostOnTouch=true
12-22 20:59:20.220  1423  4794 I WindowManager:   dvrrWindowFrameRateHint=true naviIconColor=0}
12-22 20:59:20.255  1423  4794 D WindowManager: finishDrawingWindow: Window{29eb9ea u0 com.katsuyamaki.DroidOSLauncher} mDrawState=DRAW_PENDING seqId=0
12-22 20:59:20.258  1423  4794 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=19260)/@0xd898151} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-22 20:59:20.283  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-22 20:59:21.847  1423  1554 I ActivityManager: Changes in 5009 5 to 6, 144 to 128
12-22 20:59:21.859  1423  1554 I ActivityManager: Changes in 10333 6 to 15, 128 to 0
12-22 20:59:22.304  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x0, time=33721904093000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-22 20:59:22.305  1423  1695 I InputDispatcher: Delivering touch to (7845): action: 0x4, f=0x0, d=0, '29eb9ea', t=1 
12-22 20:59:22.305  1423  1695 I InputDispatcher: Delivering touch to (2022): action: 0x4, f=0x0, d=0, '6146b28', t=1 
12-22 20:59:22.305  1423  1695 I InputDispatcher: Delivering touch to (22381): action: 0x0, f=0x0, d=0, 'f704bfc', t=1 +(0,-116)
12-22 20:59:22.331  1423  2220 E InputDispatcher: Embedded{TaskInputSink of Surface(name=Decor container of Task=19260)/@0xd898151} has feature NO_INPUT_WINDOW, but a non-null token. Clearing
12-22 20:59:22.334  1423  1547 D WindowManager: DeferredDisplayUpdater: partially applying DisplayInfo(1080 x 2520) immediately
12-22 20:59:22.383  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x1, time=33721986802000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-22 20:59:22.383  1423  1695 I InputDispatcher: Delivering touch to (22381): action: 0x1, f=0x0, d=0, 'f704bfc', t=1 +(0,-116)
12-22 20:59:23.270  1423  1423 D InputDispatcher: Inject motion (0/0): action=0x0, time=33722870090000, f=0x0, d=0 dsdx=1.000000 dtdx=0.000000
12-22 20:59:23.270  1423  1695 I InputDispatcher: Delivering touch to (7845): action: 0x4, f=0x0, d=0, '29eb9ea', t=1 
12-22 20:59:23.270  1423  1695 I InputDispatcher: Delivering touch to (2022): action: 0x4, f=0x0, d=0, '6146b28', t=1 
12-22 20:59:23.270  1423  1695 I InputDispatcher: Delivering touch to (4255): action: 0x0, f=0x0, d=0, 'f748235', t=1 +(0,-116)
