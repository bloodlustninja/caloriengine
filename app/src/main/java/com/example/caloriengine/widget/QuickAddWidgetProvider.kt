package com.example.caloriengine.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.example.caloriengine.MainActivity
import com.example.caloriengine.QuickLogActivity
import com.example.caloriengine.R
import java.util.Calendar

class QuickAddWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun triggerWidgetUpdate(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, QuickAddWidgetProvider::class.java))
            for (id in ids) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_quick_add)

            val prefs = context.getSharedPreferences("cyber_storage", Context.MODE_PRIVATE)
            val activeTheme = prefs.getString("cyber_theme", "GREEN") ?: "GREEN"

            when (activeTheme) {
                "ARCADE" -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg_arcade)
                    views.setInt(R.id.widget_open_app_btn, "setBackgroundResource", R.drawable.widget_btn_bg_arcade)
                    views.setTextColor(R.id.widget_meal_tag, Color.parseColor("#FF4500"))
                    views.setTextColor(R.id.widget_quick_sub, Color.parseColor("#121316"))
                    views.setTextColor(R.id.widget_open_app_btn, Color.parseColor("#FFFFFF"))
                }
                "SELPHIE" -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg_selphie)
                    views.setInt(R.id.widget_open_app_btn, "setBackgroundResource", R.drawable.widget_btn_bg_selphie)
                    views.setTextColor(R.id.widget_meal_tag, Color.parseColor("#FF2D55"))
                    views.setTextColor(R.id.widget_quick_sub, Color.parseColor("#F0F0F0"))
                    views.setTextColor(R.id.widget_open_app_btn, Color.parseColor("#FFFFFF"))
                }
                "BEIGE" -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg_beige)
                    views.setInt(R.id.widget_open_app_btn, "setBackgroundResource", R.drawable.widget_btn_bg_beige)
                    views.setTextColor(R.id.widget_meal_tag, Color.parseColor("#0044AA"))
                    views.setTextColor(R.id.widget_quick_sub, Color.parseColor("#1A1A1A"))
                    views.setTextColor(R.id.widget_open_app_btn, Color.parseColor("#FFFFFF"))
                }
                "PURPLE" -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg_purple)
                    views.setInt(R.id.widget_open_app_btn, "setBackgroundResource", R.drawable.widget_btn_bg_purple)
                    views.setTextColor(R.id.widget_meal_tag, Color.parseColor("#F472B6"))
                    views.setTextColor(R.id.widget_quick_sub, Color.parseColor("#93C5FD"))
                    views.setTextColor(R.id.widget_open_app_btn, Color.parseColor("#FFFFFF"))
                }
                else -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg)
                    views.setInt(R.id.widget_open_app_btn, "setBackgroundResource", R.drawable.widget_btn_bg)
                    views.setTextColor(R.id.widget_meal_tag, Color.parseColor("#00FF66"))
                    views.setTextColor(R.id.widget_quick_sub, Color.parseColor("#008837"))
                    views.setTextColor(R.id.widget_open_app_btn, Color.parseColor("#00FF66"))
                }
            }

            // Auto-detect current meal based on time of day
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val totalMins = hour * 60 + minute

            val (mealTag, mealExtra) = when {
                totalMins in (5 * 60)..(11 * 60 + 29) -> "[ BREAKFAST // AUTO ]" to "BREAKFAST"
                totalMins in (11 * 60 + 30)..(15 * 60 + 59) -> "[ LUNCH // AUTO ]" to "LUNCH"
                totalMins in (16 * 60)..(19 * 60 + 29) -> "[ SNACKS // AUTO ]" to "SNACKS"
                else -> "[ DINNER // AUTO ]" to "DINNER"
            }

            views.setTextViewText(R.id.widget_meal_tag, mealTag)

            // PendingIntent for Quick Log Overlay
            val quickLogIntent = Intent(context, QuickLogActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                putExtra("QUICK_LOG_MEAL", mealExtra)
            }
            val quickLogPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId * 10 + 1,
                quickLogIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_quick_add_action, quickLogPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_circles_container, quickLogPendingIntent)

            // PendingIntent for Opening Entire App
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId * 10 + 2,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_open_app_btn, openAppPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
