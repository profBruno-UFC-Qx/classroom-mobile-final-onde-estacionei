package io.github.rlimapro.ondeestacionei.ui.widget

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.github.rlimapro.ondeestacionei.data.AppDatabase
import io.github.rlimapro.ondeestacionei.data.repository.ParkingRepository
import io.github.rlimapro.ondeestacionei.model.ParkingLocation
import io.github.rlimapro.ondeestacionei.network.config.RetrofitConfig
import kotlinx.coroutines.tasks.await
import java.util.Locale

class ParkingWidgetWorker(val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return Result.failure()

        return try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)

            val location = fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                ?: fusedClient.lastLocation.await()
                ?: return Result.failure()

            val address = try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    buildString {
                        addr.thoroughfare?.let { append(it) }
                        addr.subThoroughfare?.let {
                            if (isNotEmpty()) append(", ")
                            append(it)
                        }
                        if (isEmpty()) {
                            addr.locality?.let { append(it) }
                        }
                    }.takeIf { it.isNotEmpty() } ?: "Localização salva"
                } else {
                    "Localização salva"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Localização salva"
            }

            val dao = AppDatabase.getDatabase(context).parkingDao()
            val repo = ParkingRepository(dao, RetrofitConfig.orsApiService)

            repo.insert(ParkingLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                timestamp = System.currentTimeMillis(),
                address = address,
                note = ""
            ))

            ParkingWidget().updateAll(context)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<ParkingWidgetWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "save_location_widget",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}