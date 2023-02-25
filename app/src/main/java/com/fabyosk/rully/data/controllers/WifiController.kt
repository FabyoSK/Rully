package com.fabyosk.rully.data.controllers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fabyosk.rully.data.models.Rule

class WifiController private constructor() {
    val rules = mutableListOf<Rule>()

    companion object {
        private val instance = WifiController()

        fun getInstance(): WifiController {
            return instance
        }
    }

    fun addRules(rule: Rule) {
        Log.d("fsk", "Rules added")

        rules.add(rule)
    }

    fun executeRules(ssid: String) {
        rules.forEach {
            if (it.name == ssid) {
                it.executeActions()
            }
        }
    }
}

