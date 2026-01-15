package io.github.rlimapro.ondeestacionei.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GpsMonitor(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val isGpsEnabled: Flow<Boolean> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    trySend(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                }
            }
        }

        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(receiver, filter)

        trySend(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))

        awaitClose { context.unregisterReceiver(receiver) }
    }
}