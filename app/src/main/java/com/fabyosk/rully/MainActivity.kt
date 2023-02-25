package com.fabyosk.rully

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fabyosk.rully.data.controllers.WifiController
import com.fabyosk.rully.data.models.Rule
import com.fabyosk.rully.data.models.actions.ToastAction
import com.fabyosk.rully.ui.theme.RullyTheme
import com.fabyosk.rully.ui.screens.NewWifiRuleScreen


class MainActivity : ComponentActivity() {
    lateinit var wifiManager: WifiManager
    var wifiController = WifiController.getInstance()
    var resultList = ArrayList<ScanResult>()

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            resultList = wifiManager.scanResults as ArrayList<ScanResult>
        }
    }

    val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                val wifiManager =
                    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val networkInfo =
                    intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                if (networkInfo?.isConnected == true) {
                    Toast.makeText(context, "WIFI is connected", Toast.LENGTH_SHORT).show()
                    val ssid = wifiManager.connectionInfo.ssid.replace("\"", "")
                    wifiController.executeRules(ssid)
                } else {
                    // WiFi connection lost
//                sendNotification(context, "Disconnected from WiFi")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wifiManager = this.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        setContent {
            RullyTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "NewWifiRule") {
//                    composable("home") {
//                        HomeScreen(navController)
//                    }
                    composable("NewWifiRule") {
                        NewWifiRuleScreen(
                            wifiManager = wifiManager,
                            navController = navController,
                            context = applicationContext
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        wifiManager.startScan()
        registerReceiver(broadcastReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        registerReceiver(networkReceiver, IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION))
    }
}

//@Composable
//fun HomeScreen(navController: NavController) {
//    Button(
//        onClick = { navController.navigate("selectWifi") },
//        modifier = Modifier.padding(16.dp)
//    ) {
//        Text("Select Wi-Fi Network")
//    }
//}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

class WifiReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
            if (networkInfo?.isConnected == true) {
                // WiFi connection established
                val ssid = wifiManager.connectionInfo.ssid
                sendNotification(context, "Connected to $ssid")
                val rules = Rule("Wifi connected");
//                val toastAction = ToastAction("Wifi connected", "Connecxted", context);
//                rules.addNewAction(toastAction);
//                rules.executeActions();
            } else {
                // WiFi connection lost
                sendNotification(context, "Disconnected from WiFi")
            }
        }
    }

    private fun sendNotification(context: Context, message: String) {
        val notificationManager = ContextCompat.getSystemService(
            context, NotificationManager::class.java
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "wifi_changes", "WiFi Changes", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, "wifi_changes")
            .setSmallIcon(androidx.core.R.drawable.notification_template_icon_bg)
            .setContentTitle("WiFi Status")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, notificationBuilder.build())
    }
}
