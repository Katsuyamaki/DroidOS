package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad

import android.content.ComponentName
import android.content.ServiceConnection
import rikka.shizuku.Shizuku

object ShizukuBinder {

    fun bind(component: ComponentName, connection: ServiceConnection, debug: Boolean, version: Int) {
        val args = Shizuku.UserServiceArgs(component)
            .processNameSuffix("shell")
            .daemon(false)
            .debuggable(debug)
            .version(version)
        Shizuku.bindUserService(args, connection)
    }

    fun unbind(component: ComponentName, connection: ServiceConnection) {
        val args = Shizuku.UserServiceArgs(component)
            .processNameSuffix("shell")
            .daemon(false)
        Shizuku.unbindUserService(args, connection, true)
    }
}