package io.github.rlimapro.ondeestacionei.ui.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class ParkingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = ParkingWidget()
}