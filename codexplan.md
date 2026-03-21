FILE_UPDATE: DroidOSKeyboardTrackpad/app/src/main/java/com/katsuyamaki/DroidOSTrackpadKeyboard/TrackpadMenuManager.kt
REASON: Centralize BT mirror menu mouse forwarding logic and use raw screen coordinates so child-view boundaries do not cause movement stalls/jumps.
SEARCH_BLOCK:
```kotlin
    private fun maybeReassertBtCaptureTop() {
        if (!isBtMirrorMouseCaptureActive()) return
        val now = android.os.SystemClock.uptimeMillis()
        if (now - lastBtCaptureReassertAt < BT_CAPTURE_REASSERT_COOLDOWN_MS) return
        lastBtCaptureReassertAt = now
        service.btMouseManager?.bringToFront()
        service.handler.postDelayed({ service.btMouseManager?.bringToFront() }, 24L)
    }

    fun show() {
```
REPLACE_BLOCK:
```kotlin
    private fun maybeReassertBtCaptureTop() {
        if (!isBtMirrorMouseCaptureActive()) return
        val now = android.os.SystemClock.uptimeMillis()
        if (now - lastBtCaptureReassertAt < BT_CAPTURE_REASSERT_COOLDOWN_MS) return
        lastBtCaptureReassertAt = now
        service.btMouseManager?.bringToFront()
        service.handler.postDelayed({ service.btMouseManager?.bringToFront() }, 24L)
    }

    private fun handleMenuMouseGenericEvent(event: MotionEvent): Boolean {
        val isMouseSource = event.isFromSource(InputDevice.SOURCE_MOUSE) ||
            event.isFromSource(InputDevice.SOURCE_MOUSE_RELATIVE)
        if (!isMouseSource || !isBtMirrorMouseCaptureActive()) return false

        maybeReassertBtCaptureTop()

        when (event.actionMasked) {
            MotionEvent.ACTION_HOVER_ENTER -> {
                lastMouseX = event.rawX
                lastMouseY = event.rawY
                hasMouseBaseline = true
            }
            MotionEvent.ACTION_HOVER_MOVE -> {
                if (!hasMouseBaseline) {
                    lastMouseX = event.rawX
                    lastMouseY = event.rawY
                    hasMouseBaseline = true
                    return true
                }

                val dx = event.rawX - lastMouseX
                val dy = event.rawY - lastMouseY
                lastMouseX = event.rawX
                lastMouseY = event.rawY

                if (dx != 0f || dy != 0f) {
                    service.handleExternalMouseMove(dx, dy, false)
                }
            }
            MotionEvent.ACTION_SCROLL -> {
                val v = event.getAxisValue(MotionEvent.AXIS_VSCROLL)
                val h = event.getAxisValue(MotionEvent.AXIS_HSCROLL)
                service.injectScroll(h * 10f, v * 10f)
            }
            MotionEvent.ACTION_HOVER_EXIT -> {
                hasMouseBaseline = false
            }
        }

        return true
    }

    private fun handleMenuMouseTouchEvent(event: MotionEvent): Boolean {
        val toolType = event.getToolType(0)
        val isMouse = toolType == MotionEvent.TOOL_TYPE_MOUSE || event.isFromSource(InputDevice.SOURCE_MOUSE)
        if (!isMouse || !isBtMirrorMouseCaptureActive()) return false

        maybeReassertBtCaptureTop()

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastMouseX = event.rawX
                lastMouseY = event.rawY
                mouseDownX = event.rawX
                mouseDownY = event.rawY
                hasMouseBaseline = true
                isMouseDragging = false
                isMousePressed = (event.buttonState and MotionEvent.BUTTON_SECONDARY) == 0
            }
            MotionEvent.ACTION_MOVE -> {
                if (!hasMouseBaseline) {
                    lastMouseX = event.rawX
                    lastMouseY = event.rawY
                    hasMouseBaseline = true
                    return true
                }

                val dx = event.rawX - lastMouseX
                val dy = event.rawY - lastMouseY
                lastMouseX = event.rawX
                lastMouseY = event.rawY

                if (isMousePressed && !isMouseDragging) {
                    val totalDx = event.rawX - mouseDownX
                    val totalDy = event.rawY - mouseDownY
                    if (kotlin.math.abs(totalDx) > MENU_MOUSE_DRAG_THRESHOLD || kotlin.math.abs(totalDy) > MENU_MOUSE_DRAG_THRESHOLD) {
                        isMouseDragging = true
                        service.handleExternalTouchDown()
                    }
                }

                if (isMouseDragging) {
                    service.handleExternalMouseMove(dx, dy, true)
                } else if (dx != 0f || dy != 0f) {
                    service.handleExternalMouseMove(dx, dy, false)
                }
            }
            MotionEvent.ACTION_UP -> {
                val isRight = (event.buttonState and MotionEvent.BUTTON_SECONDARY) != 0 ||
                    event.actionButton == MotionEvent.BUTTON_SECONDARY

                if (isRight) {
                    service.handleExternalMouseClick(true)
                } else if (isMouseDragging) {
                    service.handleExternalTouchUp()
                } else {
                    service.handleExternalMouseClick(false)
                }

                isMouseDragging = false
                isMousePressed = false
                hasMouseBaseline = false
            }
            MotionEvent.ACTION_CANCEL -> {
                if (isMouseDragging) {
                    service.handleExternalTouchUp()
                }
                isMouseDragging = false
                isMousePressed = false
                hasMouseBaseline = false
            }
        }

        return true
    }

    private fun attachBtMirrorMousePassthrough(view: View?) {
        if (view == null) return
        view.setOnGenericMotionListener { _, event ->
            handleMenuMouseGenericEvent(event)
        }
        view.setOnTouchListener { _, event ->
            handleMenuMouseTouchEvent(event)
        }
    }

    fun show() {
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: DroidOSKeyboardTrackpad/app/src/main/java/com/katsuyamaki/DroidOSTrackpadKeyboard/TrackpadMenuManager.kt
REASON: Apply BT mirror mouse pass-through on root and key child views (container/recycler/close button) because child views can consume events before root-level listeners.
SEARCH_BLOCK:
```kotlin
        // Close button logic
        drawerView?.findViewById<View>(R.id.btn_close_menu)?.setOnClickListener { hide() }

        // If BT mirror capture is active and menu gets the mouse event, route it
        // to the external cursor handlers and reassert BT capture layer once.
        drawerView?.setOnGenericMotionListener { _, event ->
            val isMouseSource = event.isFromSource(InputDevice.SOURCE_MOUSE) ||
                event.isFromSource(InputDevice.SOURCE_MOUSE_RELATIVE)
            if (!isMouseSource || !isBtMirrorMouseCaptureActive()) {
                return@setOnGenericMotionListener false
            }

            maybeReassertBtCaptureTop()

            when (event.actionMasked) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    lastMouseX = event.x
                    lastMouseY = event.y
                    hasMouseBaseline = true
                }
                MotionEvent.ACTION_HOVER_MOVE -> {
                    if (!hasMouseBaseline) {
                        lastMouseX = event.x
                        lastMouseY = event.y
                        hasMouseBaseline = true
                        return@setOnGenericMotionListener true
                    }

                    val dx = event.x - lastMouseX
                    val dy = event.y - lastMouseY
                    lastMouseX = event.x
                    lastMouseY = event.y

                    if (dx != 0f || dy != 0f) {
                        service.handleExternalMouseMove(dx, dy, false)
                    }
                }
                MotionEvent.ACTION_SCROLL -> {
                    val v = event.getAxisValue(MotionEvent.AXIS_VSCROLL)
                    val h = event.getAxisValue(MotionEvent.AXIS_HSCROLL)
                    service.injectScroll(h * 10f, v * 10f)
                }
                MotionEvent.ACTION_HOVER_EXIT -> {
                    hasMouseBaseline = false
                }
            }
            true
        }

        drawerView?.setOnTouchListener { _, event ->
            val toolType = event.getToolType(0)
            val isMouse = toolType == MotionEvent.TOOL_TYPE_MOUSE || event.isFromSource(InputDevice.SOURCE_MOUSE)
            if (!isMouse || !isBtMirrorMouseCaptureActive()) {
                return@setOnTouchListener false
            }

            maybeReassertBtCaptureTop()

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastMouseX = event.x
                    lastMouseY = event.y
                    mouseDownX = event.x
                    mouseDownY = event.y
                    hasMouseBaseline = true
                    isMouseDragging = false
                    isMousePressed = (event.buttonState and MotionEvent.BUTTON_SECONDARY) == 0
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!hasMouseBaseline) {
                        lastMouseX = event.x
                        lastMouseY = event.y
                        hasMouseBaseline = true
                        return@setOnTouchListener true
                    }

                    val dx = event.x - lastMouseX
                    val dy = event.y - lastMouseY
                    lastMouseX = event.x
                    lastMouseY = event.y

                    if (isMousePressed && !isMouseDragging) {
                        val totalDx = event.x - mouseDownX
                        val totalDy = event.y - mouseDownY
                        if (kotlin.math.abs(totalDx) > MENU_MOUSE_DRAG_THRESHOLD || kotlin.math.abs(totalDy) > MENU_MOUSE_DRAG_THRESHOLD) {
                            isMouseDragging = true
                            service.handleExternalTouchDown()
                        }
                    }

                    if (isMouseDragging) {
                        service.handleExternalMouseMove(dx, dy, true)
                    } else if (dx != 0f || dy != 0f) {
                        service.handleExternalMouseMove(dx, dy, false)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    val isRight = (event.buttonState and MotionEvent.BUTTON_SECONDARY) != 0 ||
                        event.actionButton == MotionEvent.BUTTON_SECONDARY

                    if (isRight) {
                        service.handleExternalMouseClick(true)
                    } else if (isMouseDragging) {
                        service.handleExternalTouchUp()
                    } else {
                        service.handleExternalMouseClick(false)
                    }

                    isMouseDragging = false
                    isMousePressed = false
                    hasMouseBaseline = false
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (isMouseDragging) {
                        service.handleExternalTouchUp()
                    }
                    isMouseDragging = false
                    isMousePressed = false
                    hasMouseBaseline = false
                }
            }
            true
        }
        
        recyclerView = drawerView?.findViewById(R.id.menu_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(context)
```
REPLACE_BLOCK:
```kotlin
        // Close button logic
        drawerView?.findViewById<View>(R.id.btn_close_menu)?.setOnClickListener { hide() }

        // Root + high-traffic child views: route BT mirror mouse events back to
        // virtual cursor handlers if these overlays intercept the pointer.
        attachBtMirrorMousePassthrough(drawerView)
        attachBtMirrorMousePassthrough(drawerView?.findViewById(R.id.menu_container))
        attachBtMirrorMousePassthrough(drawerView?.findViewById(R.id.menu_recycler))
        attachBtMirrorMousePassthrough(drawerView?.findViewById(R.id.btn_close_menu))
        
        recyclerView = drawerView?.findViewById(R.id.menu_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(context)
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: DroidOSKeyboardTrackpad/app/src/main/java/com/katsuyamaki/DroidOSTrackpadKeyboard/TrackpadMenuManager.kt
REASON: Ensure tab strip icons also forward intercepted BT mouse events before click handling.
SEARCH_BLOCK:
```kotlin
        for ((id, index) in tabs) {
            drawerView?.findViewById<ImageView>(id)?.setOnClickListener { 
                // [FIX] INSTRUCTIONS TAB - DEBUG TRIGGER (5 Clicks)
                if (id == R.id.tab_help) {
                    val now = System.currentTimeMillis()
                    if (now - lastHelpClickTime < 500) {
                        helpClickCount++
                    } else {
                        helpClickCount = 1
                    }
                    lastHelpClickTime = now

                    if (helpClickCount >= 5) {
                        // Toggle debug mode in the main Service
                        service.toggleDebugMode()
                        helpClickCount = 0
                    }
                }
                
                loadTab(index) 
            }
        }
```
REPLACE_BLOCK:
```kotlin
        for ((id, index) in tabs) {
            val tabView = drawerView?.findViewById<ImageView>(id)
            attachBtMirrorMousePassthrough(tabView)
            tabView?.setOnClickListener { 
                // [FIX] INSTRUCTIONS TAB - DEBUG TRIGGER (5 Clicks)
                if (id == R.id.tab_help) {
                    val now = System.currentTimeMillis()
                    if (now - lastHelpClickTime < 500) {
                        helpClickCount++
                    } else {
                        helpClickCount = 1
                    }
                    lastHelpClickTime = now

                    if (helpClickCount >= 5) {
                        // Toggle debug mode in the main Service
                        service.toggleDebugMode()
                        helpClickCount = 0
                    }
                }
                
                loadTab(index) 
            }
        }
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: DroidOSKeyboardTrackpad/app/src/main/java/com/katsuyamaki/DroidOSTrackpadKeyboard/TrackpadMenuManager.kt
REASON: Keep drag-handle behavior for finger input while still forwarding BT mirror mouse movement when cursor overlaps the handle region.
SEARCH_BLOCK:
```kotlin
        dragHandle?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = drawerParams!!.x
                    initialY = drawerParams!!.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    drawerParams!!.x = initialX + (event.rawX - initialTouchX).toInt()
                    drawerParams!!.y = initialY + (event.rawY - initialTouchY).toInt()
                    
                    try {
                        windowManager.updateViewLayout(drawerView, drawerParams)
                    } catch (e: Exception) {}
                    true
                }
                else -> false
            }
        }
```
REPLACE_BLOCK:
```kotlin
        dragHandle?.setOnGenericMotionListener { _, event ->
            handleMenuMouseGenericEvent(event)
        }

        dragHandle?.setOnTouchListener { _, event ->
            if (handleMenuMouseTouchEvent(event)) return@setOnTouchListener true

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = drawerParams!!.x
                    initialY = drawerParams!!.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    drawerParams!!.x = initialX + (event.rawX - initialTouchX).toInt()
                    drawerParams!!.y = initialY + (event.rawY - initialTouchY).toInt()
                    
                    try {
                        windowManager.updateViewLayout(drawerView, drawerParams)
                    } catch (e: Exception) {}
                    true
                }
                else -> false
            }
        }
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: DroidOSKeyboardTrackpad/app/src/main/java/com/katsuyamaki/DroidOSTrackpadKeyboard/TrackpadMenuManager.kt
REASON: Open menu away from center while BT mirror mouse capture is active so cursor does not spawn directly under menu overlay.
SEARCH_BLOCK:
```kotlin
        drawerParams?.x = (screenWidth - menuW) / 2
        drawerParams?.y = (screenHeight - menuH) / 2
```
REPLACE_BLOCK:
```kotlin
        if (isBtMirrorMouseCaptureActive()) {
            val margin = (16 * density).toInt()
            drawerParams?.x = (screenWidth - menuW - margin).coerceAtLeast(margin)
            drawerParams?.y = margin
        } else {
            drawerParams?.x = (screenWidth - menuW) / 2
            drawerParams?.y = (screenHeight - menuH) / 2
        }
```
END_OF_UPDATE_BLOCK
