package com.example.caloriengine.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.widget.RemoteViews
import com.example.caloriengine.MainActivity
import com.example.caloriengine.QuickLogActivity
import com.example.caloriengine.R
import org.json.JSONArray
import java.util.Calendar

class MetabolicHudWidgetProvider : AppWidgetProvider() {

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
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, MetabolicHudWidgetProvider::class.java))
            for (id in ids) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_metabolic_hud)

            val prefs = context.getSharedPreferences("cyber_storage", Context.MODE_PRIVATE)
            val activeTheme = prefs.getString("cyber_theme", "GREEN") ?: "GREEN"
            val consumed = prefs.getInt("cyber_consumed", 0)
            val burned = prefs.getInt("cyber_burned", 0)
            val target = prefs.getInt("cyber_target", 2150)

            val netCalories = Math.max(0, consumed - burned)

            var proteinSum = 0.0; var carbsSum = 0.0; var fatsSum = 0.0
            val unitsJsonStr = prefs.getString("cyber_units", null)
            if (unitsJsonStr != null) {
                try {
                    val unitsArray = JSONArray(unitsJsonStr)
                    for (i in 0 until unitsArray.length()) {
                        val items = unitsArray.getJSONObject(i).optJSONArray("items") ?: JSONArray()
                        for (j in 0 until items.length()) {
                            val it = items.getJSONObject(j)
                            val kcal = it.optInt("kcal", 0)
                            proteinSum += it.optDouble("p", (kcal * 0.1))
                            carbsSum += it.optDouble("c", (kcal * 0.6 / 4.0))
                            fatsSum += it.optDouble("f", (kcal * 0.3 / 9.0))
                        }
                    }
                } catch (e: Exception) {}
            }

            val protein = Math.round(proteinSum).toInt()
            val carbs = Math.round(carbsSum).toInt()
            val fats = Math.round(fatsSum).toInt()

            val ratio = if (target > 0) netCalories.toDouble() / target else 0.0

            val primaryColor: Int
            val secondaryColor: Int
            val buttonTextColor: Int

            when (activeTheme) {
                "ARCADE" -> {
                    views.setInt(R.id.hud_widget_root, "setBackgroundResource", R.drawable.widget_bg_arcade)
                    views.setInt(R.id.hud_quick_log_btn, "setBackgroundResource", R.drawable.widget_btn_bg_arcade)
                    views.setInt(R.id.hud_open_app_btn, "setBackgroundResource", R.drawable.widget_btn_bg_arcade)
                    primaryColor = Color.parseColor("#FF4500")
                    secondaryColor = Color.parseColor("#121316")
                    buttonTextColor = Color.parseColor("#FFFFFF")
                }
                "SELPHIE" -> {
                    views.setInt(R.id.hud_widget_root, "setBackgroundResource", R.drawable.widget_bg_selphie)
                    views.setInt(R.id.hud_quick_log_btn, "setBackgroundResource", R.drawable.widget_btn_bg_selphie)
                    views.setInt(R.id.hud_open_app_btn, "setBackgroundResource", R.drawable.widget_btn_bg_selphie)
                    primaryColor = Color.parseColor("#FF2D55")
                    secondaryColor = Color.parseColor("#F0F0F0")
                    buttonTextColor = Color.parseColor("#FFFFFF")
                }
                "BEIGE" -> {
                    views.setInt(R.id.hud_widget_root, "setBackgroundResource", R.drawable.widget_bg_beige)
                    views.setInt(R.id.hud_quick_log_btn, "setBackgroundResource", R.drawable.widget_btn_bg_beige)
                    views.setInt(R.id.hud_open_app_btn, "setBackgroundResource", R.drawable.widget_btn_bg_beige)
                    primaryColor = Color.parseColor("#0044AA")
                    secondaryColor = Color.parseColor("#1A1A1A")
                    buttonTextColor = Color.parseColor("#FFFFFF")
                }
                "PURPLE" -> {
                    views.setInt(R.id.hud_widget_root, "setBackgroundResource", R.drawable.widget_bg_purple)
                    views.setInt(R.id.hud_quick_log_btn, "setBackgroundResource", R.drawable.widget_btn_bg_purple)
                    views.setInt(R.id.hud_open_app_btn, "setBackgroundResource", R.drawable.widget_btn_bg_purple)
                    primaryColor = Color.parseColor("#F472B6")
                    secondaryColor = Color.parseColor("#93C5FD")
                    buttonTextColor = Color.parseColor("#FFFFFF")
                }
                else -> {
                    views.setInt(R.id.hud_widget_root, "setBackgroundResource", R.drawable.widget_bg)
                    views.setInt(R.id.hud_quick_log_btn, "setBackgroundResource", R.drawable.widget_btn_bg)
                    views.setInt(R.id.hud_open_app_btn, "setBackgroundResource", R.drawable.widget_btn_bg)
                    primaryColor = Color.parseColor("#00FF66")
                    secondaryColor = Color.parseColor("#008837")
                    buttonTextColor = Color.parseColor("#00FF66")
                }
            }

            views.setTextColor(R.id.hud_app_title, primaryColor)
            views.setTextColor(R.id.hud_meal_tag, secondaryColor)
            views.setTextColor(R.id.hud_kcal_readout, primaryColor)
            views.setTextColor(R.id.hud_protein_text, primaryColor)
            views.setTextColor(R.id.hud_carbs_text, primaryColor)
            views.setTextColor(R.id.hud_fats_text, primaryColor)
            views.setTextColor(R.id.hud_quick_log_btn, buttonTextColor)
            views.setTextColor(R.id.hud_open_app_btn, buttonTextColor)

            views.setTextViewText(R.id.hud_kcal_readout, "$netCalories / $target KCAL")
            views.setTextViewText(R.id.hud_protein_text, "P: ${protein}G")
            views.setTextViewText(R.id.hud_carbs_text, "C: ${carbs}G")
            views.setTextViewText(R.id.hud_fats_text, "F: ${fats}G")

            // Auto-detect meal - Compact Tag
            val calendar = Calendar.getInstance()
            val totalMins = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
            val (mealTag, mealExtra) = when {
                totalMins in (5 * 60)..(11 * 60 + 29) -> "[ BREAKFAST ]" to "BREAKFAST"
                totalMins in (11 * 60 + 30)..(15 * 60 + 59) -> "[ LUNCH ]" to "LUNCH"
                totalMins in (16 * 60)..(19 * 60 + 29) -> "[ SNACKS ]" to "SNACKS"
                else -> "[ DINNER ]" to "DINNER"
            }
            views.setTextViewText(R.id.hud_meal_tag, mealTag)

            // Biometric Matrix Sync Waveform Labeling
            val (waveformLabel, labelColor) = when {
                netCalories == 0 || ratio <= 0.0 -> "[ FLATLINE // 0 KCAL ]" to secondaryColor
                ratio < 0.45 -> "[ LOW FREQ ]" to Color.parseColor("#FF8C00")
                ratio <= 1.05 -> "[ SYNC OPTIMAL ]" to primaryColor
                else -> "[ SYSTEM OVERLOAD ]" to Color.parseColor("#FF2A4B")
            }
            views.setTextViewText(R.id.hud_waveform_label, waveformLabel)
            views.setTextColor(R.id.hud_waveform_label, labelColor)

            // Render Smooth Rounded Dual Overlapping Waveform Bitmap
            val waveformBitmap = generateSmoothDualWaveformBitmap(context, netCalories, ratio, activeTheme, primaryColor)
            views.setImageViewBitmap(R.id.hud_waveform_image, waveformBitmap)

            // PendingIntent for Quick Log Floating Overlay
            val quickLogIntent = Intent(context, QuickLogActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                putExtra("QUICK_LOG_MEAL", mealExtra)
            }
            val quickLogPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId * 20 + 1,
                quickLogIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.hud_quick_log_btn, quickLogPendingIntent)

            // PendingIntent for Main App
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId * 20 + 2,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.hud_open_app_btn, openAppPendingIntent)
            views.setOnClickPendingIntent(R.id.hud_widget_root, openAppPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // Smooth Rounded Dual Waveform Generator
        private fun generateSmoothDualWaveformBitmap(
            context: Context,
            netCalories: Int,
            ratio: Double,
            theme: String,
            primaryColor: Int,
            widthDp: Int = 220,
            heightDp: Int = 42
        ): Bitmap {
            val density = context.resources.displayMetrics.density
            val width = Math.max(120, (widthDp * density).toInt())
            val height = Math.max(30, (heightDp * density).toInt())

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val centerY = height / 2f
            val targetAmplitude = height * 0.38f
            val targetFrequency = 5.0 * Math.PI / width

            // 1. Draw Target Reference Wave (Smooth Translucent White/Dim Baseline Goal Wave)
            val targetPaint = Paint().apply {
                isAntiAlias = true
                strokeWidth = 1.8f * density
                style = Paint.Style.STROKE
                color = when (theme) {
                    "ARCADE" -> Color.parseColor("#30121316")
                    "SELPHIE" -> Color.parseColor("#60FF2D55")
                    "BEIGE" -> Color.parseColor("#999999")
                    "PURPLE" -> Color.parseColor("#60A390C0")
                    else -> Color.parseColor("#60FFFFFF")
                }
            }

            val targetPath = Path()
            targetPath.moveTo(0f, centerY)
            for (x in 0..width) {
                val y = centerY + (targetAmplitude * Math.sin(x * targetFrequency)).toFloat()
                targetPath.lineTo(x.toFloat(), y)
            }
            canvas.drawPath(targetPath, targetPaint)

            // 2. Draw Live Energy Wave
            val livePaint = Paint().apply {
                isAntiAlias = true
                strokeWidth = 2.4f * density
                style = Paint.Style.STROKE
            }

            val livePath = Path()
            livePath.moveTo(0f, centerY)

            if (netCalories == 0 || ratio <= 0.0) {
                // 100% Flat Line at 0 KCAL
                livePaint.color = when (theme) {
                    "ARCADE" -> Color.parseColor("#888075")
                    "SELPHIE" -> Color.parseColor("#7D838A")
                    "BEIGE" -> Color.parseColor("#777777")
                    "PURPLE" -> Color.parseColor("#A390C0")
                    else -> Color.parseColor("#008837")
                }
                livePath.lineTo(width.toFloat(), centerY)
            } else if (ratio < 0.45) {
                // Low State: Smooth low-amplitude rounded wave
                livePaint.color = when (theme) {
                    "ARCADE" -> Color.parseColor("#FF4500")
                    "SELPHIE" -> Color.parseColor("#FF2D55")
                    "BEIGE" -> Color.parseColor("#777777")
                    "PURPLE" -> Color.parseColor("#A390C0")
                    else -> Color.parseColor("#FF8C00")
                }
                val lowAmp = targetAmplitude * (0.2 + ratio * 0.4)
                val lowFreq = 7.0 * Math.PI / width
                for (x in 0..width) {
                    val y = centerY + (lowAmp * Math.sin(x * lowFreq)).toFloat()
                    livePath.lineTo(x.toFloat(), y)
                }
            } else if (ratio <= 1.05) {
                // Syncing / Optimal State: Smooth rounded wave scaling to 100% OVERLAP with Target Wave!
                livePaint.color = primaryColor
                val liveAmp = targetAmplitude * Math.min(1.0, ratio)
                for (x in 0..width) {
                    val y = centerY + (liveAmp * Math.sin(x * targetFrequency)).toFloat()
                    livePath.lineTo(x.toFloat(), y)
                }
            } else {
                // Overload State: Smooth rounded high-amplitude overcharged wave
                livePaint.color = Color.parseColor("#FF2A4B")
                val overloadAmp = height * 0.46f
                val overloadFreq = 8.0 * Math.PI / width
                for (x in 0..width) {
                    val y = centerY + (overloadAmp * Math.sin(x * overloadFreq)).toFloat()
                    livePath.lineTo(x.toFloat(), y)
                }
            }

            canvas.drawPath(livePath, livePaint)
            return bitmap
        }
    }
}
