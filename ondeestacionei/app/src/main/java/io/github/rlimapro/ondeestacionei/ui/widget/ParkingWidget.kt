package io.github.rlimapro.ondeestacionei.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import io.github.rlimapro.ondeestacionei.R
import io.github.rlimapro.ondeestacionei.data.AppDatabase
import io.github.rlimapro.ondeestacionei.model.ParkingLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ParkingWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val lastLocation = withContext(Dispatchers.IO) {
            try {
                val dao = AppDatabase.getDatabase(context).parkingDao()
                dao.getLatestLocation()
            } catch (e: Exception) {
                null
            }
        }

        provideContent {
            GlanceTheme {
                ParkingWidgetContent(lastLocation)
            }
        }
    }
}

@Composable
private fun ParkingWidgetContent(lastLocation: ParkingLocation?) {
    val displayText = lastLocation?.address ?: "Salvar localização"

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .cornerRadius(28.dp)
                .background(GlanceTheme.colors.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable(
                    if (lastLocation != null) {
                        actionRunCallback<OpenMapCallback>()
                    } else {
                        actionRunCallback<SaveLocationCallback>()
                    }
                ),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = displayText,
                modifier = GlanceModifier.defaultWeight(),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )

            Spacer(modifier = GlanceModifier.width(8.dp))

            Box(
                modifier = GlanceModifier
                    .size(36.dp)
                    .cornerRadius(18.dp)
                    .background(GlanceTheme.colors.primary)
                    .clickable(actionRunCallback<SaveLocationCallback>()),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_location_pin),
                    contentDescription = "Salvar localização",
                    modifier = GlanceModifier.size(20.dp)
                )
            }
        }
    }
}

class SaveLocationCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        ParkingWidgetWorker.enqueue(context)
    }
}

class OpenMapCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("navigate_to", "map")
        }
        intent?.let { context.startActivity(it) }
    }
}