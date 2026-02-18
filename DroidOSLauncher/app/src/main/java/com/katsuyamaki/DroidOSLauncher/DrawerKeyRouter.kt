package com.katsuyamaki.DroidOSLauncher

import android.view.KeyEvent

class DrawerKeyRouter {

    enum class Source {
        HARDWARE,
        REMOTE,
        SEARCH_LISTENER,
        DRAWER_LISTENER
    }

    enum class Area {
        SEARCH,
        QUEUE,
        LIST,
        OTHER
    }

    data class Context(
        val source: Source,
        val area: Area,
        val isExpanded: Boolean,
        val isNameEditorActive: Boolean,
        val pendingCommandActive: Boolean,
        val selectedListIndex: Int,
        val queueSelectedIndex: Int,
        val displayListSize: Int,
        val queueSize: Int,
        val nowMs: Long,
        val lastNavMs: Long
    )

    sealed class Decision {
        object NotHandled : Decision()
        data class Handled(val consume: Boolean = true) : Decision()
        data class MoveList(val delta: Int) : Decision()
        data class MoveQueue(val delta: Int) : Decision()
        object ToSearch : Decision()
        object ToList : Decision()
        object ToQueue : Decision()
        object ActivateSelectedListItem : Decision()
        object ActivateSelectedQueueItem : Decision()
        object ConfirmPendingQueueCommand : Decision()
        object CommitNameEdit : Decision()
        object PassThroughToEditor : Decision()
        object AbortPendingCommand : Decision()
        data class PendingCommandNumber(val number: Int) : Decision()
    }

    fun routeListKey(ctx: Context, keyCode: Int): Decision {
        if (!ctx.isExpanded || ctx.area != Area.LIST) return Decision.NotHandled

        if (ctx.isNameEditorActive) {
            return when (keyCode) {
                KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> Decision.CommitNameEdit
                else -> {
                    if (ctx.source == Source.DRAWER_LISTENER || ctx.source == Source.SEARCH_LISTENER) {
                        Decision.PassThroughToEditor
                    } else {
                        Decision.Handled(consume = true)
                    }
                }
            }
        }

        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (ctx.selectedListIndex > 0) Decision.MoveList(-1)
                else if (ctx.queueSize > 0) Decision.ToQueue
                else Decision.ToSearch
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (ctx.selectedListIndex < ctx.displayListSize - 1) Decision.MoveList(1)
                else Decision.Handled()
            }
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_SPACE -> Decision.ActivateSelectedListItem
            else -> Decision.NotHandled
        }
    }
}