package com.fabyosk.rully.ui.screens

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fabyosk.rully.data.controllers.WifiController
import com.fabyosk.rully.data.models.Rule
import com.fabyosk.rully.data.models.actions.Action
import com.fabyosk.rully.data.models.actions.ToastAction
import com.fabyosk.rully.data.repositories.ActionRepository

@Composable
fun NewWifiRuleScreen(wifiManager: WifiManager, navController: NavController, context: Context) {
    val nearbyWifi = remember { mutableStateListOf<String>() }
    var selectedWifi by remember { mutableStateOf<ScanResult?>(null) }
    var selectedAction by remember { mutableStateOf<Action?>(null) }

    var newRuleName: String by remember { mutableStateOf("") }
    val rules = remember { mutableStateListOf<Rule>() }

    var searchText by remember { mutableStateOf("") }
    val filteredList =
        wifiManager.scanResults
            .filter { it.SSID.contains(searchText, ignoreCase = true) }
//            .mapNotNull { it.SSID?.replace("\"", "") } ?: emptyList()

    wifiManager.startScan()
    var wifiController = WifiController.getInstance()


    fun onSelectAction(action: Action): Unit {
        selectedAction = action
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Text("Select Wi-Fi Network")
        }

        if (selectedWifi == null) {
            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search for Wi-Fi network...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Wi-Fi network list
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Wi-Fi networks found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(filteredList.size) { index ->
                        val wifi = filteredList[index]
                        WifiNetworkTile(
                            wifi = wifi,
                            onClick = {
                                selectedWifi = wifi
                            }
                        )
                    }
                }
            }
        } else {
            if (selectedAction == null) {
                ActionListScreen(
                    onSelectAction = { action -> onSelectAction(action) }, context
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if ((selectedWifi != null) && (selectedAction != null)) {
            Toast.makeText(context, "Rule added", Toast.LENGTH_SHORT).show()


            selectedWifi?.let { wifi ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Selected WIFI")

                        Text(text = wifi.SSID, fontWeight = FontWeight.Bold)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(text = wifi.description)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            selectedAction?.let { action ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Selected action")
                        Text(text = action.name, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = action.description)
                    }
                }
            }

            var rule = Rule(selectedWifi!!.SSID)
            var action = ToastAction("Rodei garai", context)
            var action2 = ToastAction("Hehe boy", context)
            rule.addNewAction(action)
            rule.addNewAction(action2)
            wifiController.addRules(rule)
        }
    }
}

@Composable
fun WifiNetworkTile(
    wifi: ScanResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wi-Fi network name
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = wifi.SSID,
                    style = MaterialTheme.typography.h6,
                )
                Text(text = "");
//                Text(
//                    text = wifi.capabilities,
//                    style = MaterialTheme.typography.body2,
//                )
            }

            // Wi-Fi network icon
//            WifiIcon(
//                modifier = Modifier.size(40.dp),
//                signalLevel = wifi.level,
//                isSecure = wifi.capabilities.contains("WPA") || wifi.capabilities.contains("WEP"),
//            )
        }
    }
}

@Composable
fun ActionListScreen(onSelectAction: (Action) -> Unit, context: Context) {
    Log.d("fsk", "ActionListScreen Inner")

    val ac = ActionRepository()
    val actions = remember { ac.getAllActions(context) }
    var expanded by remember { mutableStateOf(false) }

    Column() {
        Text(
            text = "Select an action:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Box() {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(actions.size) { index ->
                    val action = actions[index]
                    DropdownMenuItem(
                        onClick = {
                            onSelectAction(action)
                        }
                    ) {
                        Text(text = action.name)
                    }
                }
            }
        }
    }
}
