package com.example.caloriengine

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caloriengine.widget.MetabolicHudWidgetProvider
import com.example.caloriengine.widget.QuickAddWidgetProvider
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuickLogActivity : AppCompatActivity() {

    private data class FoodRef(val kcal: Int, val std: Int, val unit: String)

    private val foodDb = mapOf(
        // Street Food & Momos
        "STEAM MOMOS" to FoodRef(35, 1, "PC"),
        "STEAMED MOMOS" to FoodRef(35, 1, "PC"),
        "VEG MOMOS" to FoodRef(35, 1, "PC"),
        "MOMOS" to FoodRef(35, 1, "PC"),
        "MOMO" to FoodRef(35, 1, "PC"),
        "CHICKEN MOMOS" to FoodRef(45, 1, "PC"),
        "PANEER MOMOS" to FoodRef(45, 1, "PC"),
        "FRIED MOMOS" to FoodRef(65, 1, "PC"),
        "SPRING ROLL" to FoodRef(150, 1, "PC"),
        "CHOWMEIN" to FoodRef(220, 150, "G"),
        "NOODLES" to FoodRef(220, 150, "G"),
        "MANCHURIAN" to FoodRef(180, 150, "G"),

        // Rice & Grains
        "BHAAT" to FoodRef(130, 150, "G"),
        "BHAT" to FoodRef(130, 150, "G"),
        "RICE" to FoodRef(130, 150, "G"),
        "JEERA RICE" to FoodRef(160, 150, "G"),
        "PULAO" to FoodRef(180, 150, "G"),
        "KHICHDI" to FoodRef(180, 150, "G"),
        "CURD RICE" to FoodRef(180, 200, "G"),

        // Sabzi & Bhaji Variations
        "BHAJI" to FoodRef(120, 150, "G"),
        "SABZI" to FoodRef(120, 150, "G"),
        "SUBZI" to FoodRef(120, 150, "G"),
        "SUBJI" to FoodRef(120, 150, "G"),
        "TARKARI" to FoodRef(120, 150, "G"),
        "SUKHI BHAJI" to FoodRef(140, 150, "G"),
        "ALOO BHAJI" to FoodRef(150, 150, "G"),
        "BATATA BHAJI" to FoodRef(150, 150, "G"),
        "KANDA BHAJI" to FoodRef(250, 100, "G"),
        "BHAJIYA" to FoodRef(250, 100, "G"),
        "PAKODA" to FoodRef(250, 100, "G"),
        "PAV BHAJI" to FoodRef(400, 1, "PLATE"),
        "PAVBHAJI" to FoodRef(400, 1, "PLATE"),
        "BHINDI" to FoodRef(90, 150, "G"),
        "ALOO GOBI" to FoodRef(110, 150, "G"),
        "MIX VEG" to FoodRef(100, 150, "G"),
        "BHARLI VANGI" to FoodRef(180, 150, "G"),

        // Maharashtrian Specialties
        "MISAL PAV" to FoodRef(450, 1, "PC"),
        "MISAL" to FoodRef(350, 150, "G"),
        "KANDA POHA" to FoodRef(200, 150, "G"),
        "POHA" to FoodRef(180, 150, "G"),
        "POHE" to FoodRef(180, 150, "G"),
        "BATATA VADA" to FoodRef(180, 1, "PC"),
        "VADA PAV" to FoodRef(300, 1, "PC"),
        "VADAPAV" to FoodRef(300, 1, "PC"),
        "PAV" to FoodRef(80, 1, "PC"),
        "SABUDANA KHICHDI" to FoodRef(320, 150, "G"),
        "SABUDANA VADA" to FoodRef(220, 1, "PC"),
        "THALIPEETH" to FoodRef(220, 1, "PC"),
        "PURAN POLI" to FoodRef(300, 1, "PC"),
        "PURANPOLI" to FoodRef(300, 1, "PC"),
        "PITHLA BHAKRI" to FoodRef(350, 1, "PLATE"),
        "PITHLA" to FoodRef(160, 150, "G"),
        "PITLA" to FoodRef(160, 150, "G"),
        "BHAKRI" to FoodRef(150, 1, "PC"),
        "JOWAR BHAKRI" to FoodRef(150, 1, "PC"),
        "BAJRA BHAKRI" to FoodRef(160, 1, "PC"),
        "SOLKADI" to FoodRef(70, 150, "ML"),
        "SOL KADI" to FoodRef(70, 150, "ML"),
        "UKADICHE MODAK" to FoodRef(180, 1, "PC"),
        "MODAK" to FoodRef(180, 1, "PC"),
        "MEDU VADA" to FoodRef(180, 1, "PC"),
        "MEDUWADA" to FoodRef(180, 1, "PC"),
        "MEDUVADA" to FoodRef(180, 1, "PC"),
        "MEDU WADA" to FoodRef(180, 1, "PC"),
        "MEDU VADAI" to FoodRef(180, 1, "PC"),
        "VADA" to FoodRef(180, 1, "PC"),
        "KOTHAMBIR VADI" to FoodRef(180, 1, "PLATE"),
        "ALU VADI" to FoodRef(160, 1, "PLATE"),
        "USAL" to FoodRef(180, 150, "G"),
        "MATKI USAL" to FoodRef(180, 150, "G"),
        "VARAN BHAT" to FoodRef(280, 1, "PLATE"),
        "VARAN BHAAT" to FoodRef(280, 1, "PLATE"),
        "VARAN" to FoodRef(105, 150, "G"),
        "AMTI" to FoodRef(110, 150, "G"),
        "TAAK" to FoodRef(40, 200, "ML"),
        "BUTTERMILK" to FoodRef(40, 200, "ML"),
        "SHRIKHAND" to FoodRef(280, 100, "G"),
        "BASUNDI" to FoodRef(260, 150, "ML"),
        "ZHUNKA" to FoodRef(160, 150, "G"),
        "JUNKA" to FoodRef(160, 150, "G"),
        "KOMBDI VADE" to FoodRef(450, 1, "PLATE"),
        "TAMBDA RASA" to FoodRef(120, 150, "ML"),
        "PANDHRA RASA" to FoodRef(160, 150, "ML"),
        "SURMAI FRY" to FoodRef(250, 1, "PC"),
        "POMFRET FRY" to FoodRef(240, 1, "PC"),

        // North & South Indian Staples
        "ROTI" to FoodRef(80, 1, "PC"),
        "CHAPATI" to FoodRef(75, 1, "PC"),
        "PHULKA" to FoodRef(60, 1, "PC"),
        "FULKA" to FoodRef(60, 1, "PC"),
        "PARATHA" to FoodRef(180, 1, "PC"),
        "ALOO PARATHA" to FoodRef(250, 1, "PC"),
        "PURI" to FoodRef(110, 1, "PC"),
        "POORI" to FoodRef(110, 1, "PC"),
        "NAAN" to FoodRef(260, 1, "PC"),
        "BUTTER NAAN" to FoodRef(310, 1, "PC"),
        "DAL TADKA" to FoodRef(110, 150, "G"),
        "DAL MAKHANI" to FoodRef(150, 150, "G"),
        "DAL" to FoodRef(105, 150, "G"),
        "DAAL" to FoodRef(105, 150, "G"),
        "RAJMA" to FoodRef(180, 150, "G"),
        "CHOLE" to FoodRef(180, 150, "G"),
        "CHOLE BHATURE" to FoodRef(550, 1, "PLATE"),
        "SAMOSA" to FoodRef(250, 1, "PC"),
        "KACHORI" to FoodRef(280, 1, "PC"),
        "CHICKEN FRIED RICE" to FoodRef(163, 250, "G"),
        "CHICKEN RICE" to FoodRef(140, 250, "G"),
        "CHICKEN CURRY" to FoodRef(180, 150, "G"),
        "CHICKEN BREAST" to FoodRef(165, 100, "G"),
        "CHICKEN" to FoodRef(239, 100, "G"),
        "MUTTON" to FoodRef(280, 150, "G"),
        "MUTTON KEEMA" to FoodRef(300, 150, "G"),
        "KHEEMA" to FoodRef(300, 150, "G"),
        "PANEER BUTTER MASALA" to FoodRef(320, 150, "G"),
        "SHAHI PANEER" to FoodRef(300, 150, "G"),
        "PANEER TIKKA" to FoodRef(250, 150, "G"),
        "PANEER" to FoodRef(265, 100, "G"),
        "EGG CURRY" to FoodRef(210, 1, "PLATE"),
        "EGG BHURJI" to FoodRef(220, 1, "PLATE"),
        "OMELETTE" to FoodRef(180, 1, "PC"),
        "EGG" to FoodRef(78, 1, "PC"),
        "ANDA" to FoodRef(78, 1, "PC"),
        "IDLI" to FoodRef(60, 1, "PC"),
        "DOSA" to FoodRef(140, 1, "PC"),
        "MASALA DOSA" to FoodRef(350, 1, "PC"),
        "UTTAPAM" to FoodRef(220, 1, "PC"),
        "UPMA" to FoodRef(200, 150, "G"),
        "HALWA" to FoodRef(300, 100, "G"),
        "GAJAR HALWA" to FoodRef(280, 100, "G"),
        "GULAB JAMUN" to FoodRef(150, 1, "PC"),
        "JALEBI" to FoodRef(150, 1, "PLATE"),
        "LASSI" to FoodRef(180, 200, "ML"),
        "CHAI" to FoodRef(50, 150, "ML"),
        "TEA" to FoodRef(40, 150, "ML"),
        "COFFEE" to FoodRef(50, 150, "ML"),
        "CURD" to FoodRef(60, 100, "G"),
        "DAHI" to FoodRef(60, 100, "G"),
        "MILK" to FoodRef(120, 200, "ML"),
        "OATS" to FoodRef(389, 50, "G"),
        "BANANA" to FoodRef(89, 1, "PC"),
        "APPLE" to FoodRef(52, 1, "PC"),
        "MANGO" to FoodRef(100, 1, "PC"),
        "BREAD" to FoodRef(70, 1, "SLICE"),
        "WHEY" to FoodRef(120, 30, "G"),
        "PEANUT BUTTER" to FoodRef(90, 15, "G"),
        "FISH" to FoodRef(206, 100, "G"),
        "BURGER" to FoodRef(295, 1, "PC"),
        "PIZZA" to FoodRef(266, 1, "SLICE")
    )

    private fun parseWordToNumber(input: String): String {
        return input
            .replace(Regex("\\bONE\\b"), "1")
            .replace(Regex("\\bTWO\\b"), "2")
            .replace(Regex("\\bTHREE\\b"), "3")
            .replace(Regex("\\bFOUR\\b"), "4")
            .replace(Regex("\\bFIVE\\b"), "5")
            .replace(Regex("\\bSIX\\b"), "6")
            .replace(Regex("\\bSEVEN\\b"), "7")
            .replace(Regex("\\bEIGHT\\b"), "8")
            .replace(Regex("\\bNINE\\b"), "9")
            .replace(Regex("\\bTEN\\b"), "10")
            .replace(Regex("\\bHALF\\b"), "0.5")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_quick_log)

        val prefs = getSharedPreferences("cyber_storage", Context.MODE_PRIVATE)
        val activeTheme = prefs.getString("cyber_theme", "GREEN") ?: "GREEN"

        val rootLayout = findViewById<View>(R.id.dialog_root_layout)
        val mealName = intent.getStringExtra("QUICK_LOG_MEAL") ?: "LUNCH"
        val headerTitle = findViewById<TextView>(R.id.dialog_header_title)
        val subtitle = findViewById<TextView>(R.id.dialog_subtitle)
        val inputEdit = findViewById<EditText>(R.id.dialog_input_edit)
        val btnCancel = findViewById<TextView>(R.id.dialog_btn_cancel)
        val btnSubmit = findViewById<TextView>(R.id.dialog_btn_submit)

        // Apply Active Theme Styling across all themes
        when (activeTheme) {
            "PURPLE" -> {
                rootLayout?.setBackgroundResource(R.drawable.widget_bg_purple)
                inputEdit.setBackgroundResource(R.drawable.widget_btn_bg_purple)
                btnSubmit.setBackgroundResource(R.drawable.widget_btn_bg_purple)
                btnCancel.setBackgroundResource(R.drawable.widget_btn_bg_purple)

                headerTitle.setTextColor(Color.parseColor("#F472B6"))
                subtitle.setTextColor(Color.parseColor("#93C5FD"))
                inputEdit.setTextColor(Color.parseColor("#FFFFFF"))
                inputEdit.setHintTextColor(Color.parseColor("#8093C5FD"))
                btnSubmit.setTextColor(Color.parseColor("#FFFFFF"))
                btnCancel.setTextColor(Color.parseColor("#F43F5E"))
            }
            "BEIGE" -> {
                rootLayout?.setBackgroundResource(R.drawable.widget_bg_beige)
                inputEdit.setBackgroundResource(R.drawable.widget_btn_bg_beige)
                btnSubmit.setBackgroundResource(R.drawable.widget_btn_bg_beige)
                btnCancel.setBackgroundResource(R.drawable.widget_btn_bg_beige)

                headerTitle.setTextColor(Color.parseColor("#0044AA"))
                subtitle.setTextColor(Color.parseColor("#555555"))
                inputEdit.setTextColor(Color.parseColor("#1A1A1A"))
                inputEdit.setHintTextColor(Color.parseColor("#777777"))
                btnSubmit.setTextColor(Color.parseColor("#FFFFFF"))
                btnCancel.setTextColor(Color.parseColor("#FF8C00"))
            }
            else -> {
                rootLayout?.setBackgroundResource(R.drawable.widget_bg)
                inputEdit.setBackgroundResource(R.drawable.widget_btn_bg)
                btnSubmit.setBackgroundResource(R.drawable.widget_btn_bg)
                btnCancel.setBackgroundResource(R.drawable.widget_btn_bg)

                headerTitle.setTextColor(Color.parseColor("#00FF66"))
                subtitle.setTextColor(Color.parseColor("#008837"))
                inputEdit.setTextColor(Color.parseColor("#00FF66"))
                inputEdit.setHintTextColor(Color.parseColor("#005522"))
                btnSubmit.setTextColor(Color.parseColor("#00FF66"))
                btnCancel.setTextColor(Color.parseColor("#FF3344"))
            }
        }

        headerTitle.text = "> QUICK LOG // [ $mealName ]"

        btnCancel.setOnClickListener { finish() }

        btnSubmit.setOnClickListener {
            submitLog(inputEdit.text.toString(), mealName)
        }

        inputEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitLog(inputEdit.text.toString(), mealName)
                true
            } else {
                false
            }
        }
    }

    private fun submitLog(input: String, mealName: String) {
        val rawInput = input.trim().uppercase()
        if (rawInput.isEmpty()) {
            finish()
            return
        }

        val valStr = parseWordToNumber(rawInput)
        val parts = valStr.split(" AND ", " WITH ", "+", " , ", ",")
        var totalKcal = 0
        val itemNames = mutableListOf<String>()

        val sortedFoods = foodDb.keys.sortedByDescending { it.length }

        parts.forEach { part ->
            val p = part.trim().uppercase()
            if (p.isNotEmpty()) {
                val weightMatch = Regex("(\\d+(?:\\.\\d+)?)\\s*(G|GRAMS|ML|PCS|PC|PIECE|PIECES|SLICE|SLICES)").find(p)
                val countMatch = Regex("^(\\d+(?:\\.\\d+)?)\\s*").find(p)

                var multiplier = 1.0
                if (weightMatch != null && (weightMatch.groupValues[2] == "G" || weightMatch.groupValues[2] == "GRAMS" || weightMatch.groupValues[2] == "ML")) {
                    multiplier = weightMatch.groupValues[1].toDoubleOrNull() ?: 1.0
                } else if (countMatch != null) {
                    multiplier = countMatch.groupValues[1].toDoubleOrNull() ?: 1.0
                }

                var foundFood: FoodRef? = null
                var foodKey: String? = null

                val cleanP = p.replace(Regex("\\s+"), "")
                for (food in sortedFoods) {
                    val cleanFood = food.replace(Regex("\\s+"), "")
                    if (cleanP.contains(cleanFood)) {
                        foundFood = foodDb[food]
                        foodKey = food
                        break
                    }
                }

                var finalKcal: Int
                var displayName: String

                if (foundFood != null && foodKey != null) {
                    if (foundFood.unit == "G" || foundFood.unit == "ML") {
                        val totalWeight = if (weightMatch != null && (weightMatch.groupValues[2] == "G" || weightMatch.groupValues[2] == "GRAMS" || weightMatch.groupValues[2] == "ML")) {
                            multiplier
                        } else {
                            multiplier * foundFood.std
                        }
                        finalKcal = Math.floor((foundFood.kcal * totalWeight) / 100.0).toInt()
                        displayName = "${totalWeight.toInt()}${foundFood.unit} $foodKey"
                    } else {
                        finalKcal = Math.floor(foundFood.kcal * multiplier).toInt()
                        displayName = "${if (multiplier % 1.0 == 0.0) multiplier.toInt() else multiplier} $foodKey"
                    }
                } else {
                    finalKcal = Math.floor(150.0 * multiplier).toInt()
                    displayName = "${if (multiplier % 1.0 == 0.0) multiplier.toInt() else multiplier}x $p"
                }

                totalKcal += finalKcal
                itemNames.add(displayName)
            }
        }

        saveToSharedPreferences(mealName, itemNames.joinToString(" + "), totalKcal)

        Toast.makeText(this, "[ LOGGED $totalKcal KCAL TO $mealName ]", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun saveToSharedPreferences(mealName: String, itemName: String, kcal: Int) {
        val prefs = getSharedPreferences("cyber_storage", Context.MODE_PRIVATE)
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = prefs.getString("cyber_last_date", null)

        val editor = prefs.edit()

        // 12:00 AM Midnight Clean Sheet Reset Check
        var currentConsumed = prefs.getInt("cyber_consumed", 0)
        var unitsArray: JSONArray

        if (lastDate != todayDate) {
            // New Day Clean Sheet Reset
            currentConsumed = 0
            editor.putString("cyber_last_date", todayDate)
            editor.putInt("cyber_burned", 0)
            editor.putString("cyber_activities", "[]")

            unitsArray = JSONArray().apply {
                put(JSONObject().put("id", "UNIT_01").put("label", "BREAKFAST").put("items", JSONArray()))
                put(JSONObject().put("id", "UNIT_02").put("label", "LUNCH").put("items", JSONArray()))
                put(JSONObject().put("id", "UNIT_03").put("label", "SNACKS").put("items", JSONArray()))
                put(JSONObject().put("id", "UNIT_04").put("label", "DINNER").put("items", JSONArray()))
            }
        } else {
            val unitsJsonStr = prefs.getString("cyber_units", null)
            unitsArray = if (unitsJsonStr != null) {
                JSONArray(unitsJsonStr)
            } else {
                JSONArray().apply {
                    put(JSONObject().put("id", "UNIT_01").put("label", "BREAKFAST").put("items", JSONArray()))
                    put(JSONObject().put("id", "UNIT_02").put("label", "LUNCH").put("items", JSONArray()))
                    put(JSONObject().put("id", "UNIT_03").put("label", "SNACKS").put("items", JSONArray()))
                    put(JSONObject().put("id", "UNIT_04").put("label", "DINNER").put("items", JSONArray()))
                }
            }
        }

        currentConsumed += kcal
        editor.putInt("cyber_consumed", currentConsumed)

        val unitMap = mapOf("BREAKFAST" to "UNIT_01", "LUNCH" to "UNIT_02", "SNACKS" to "UNIT_03", "DINNER" to "UNIT_04")
        val targetUnitId = unitMap[mealName] ?: "UNIT_01"

        for (i in 0 until unitsArray.length()) {
            val unitObj = unitsArray.getJSONObject(i)
            if (unitObj.getString("id") == targetUnitId) {
                val items = unitObj.getJSONArray("items")
                items.put(JSONObject().put("name", itemName).put("kcal", kcal))
                break
            }
        }

        editor.putString("cyber_units", unitsArray.toString())
        editor.apply()

        // Trigger Instant Multi-Widget Update for 2x1 and 2x2 Widgets
        QuickAddWidgetProvider.triggerWidgetUpdate(this)
        MetabolicHudWidgetProvider.triggerWidgetUpdate(this)
    }
}
