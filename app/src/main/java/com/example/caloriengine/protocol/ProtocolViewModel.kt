package com.example.caloriengine.protocol

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class ProtocolViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("protocol_prefs", Context.MODE_PRIVATE)

    private val quotes = listOf(
        "\"The resistance that you fight physically in the gym and in life can only build a strong character.\"\n— ARNOLD SCHWARZENEGGER",
        "\"Everybody wants to be a bodybuilder, but nobody wants to lift no heavy-ass weights!\"\n— RONNIE COLEMAN",
        "\"Concentration, focus, and relentless intensity. You don't train to fail, you train to succeed.\"\n— DORIAN YATES",
        "\"Failure is not an option. It is a prerequisite for growth.\"\n— TOM PLATZ",
        "\"In the midst of chaos, there is also opportunity. Victory belongs to those who prepare.\"\n— SUN TZU",
        "\"You have power over your mind - not outside events. Realize this, and you will find strength.\"\n— MARCUS AURELIUS",
        "\"There is nothing outside of yourself that can enable you to get stronger. Everything is within.\"\n— MIYAMOTO MUSASHI",
        "\"There is nothing impossible to him who will try.\"\n— ALEXANDER THE GREAT",
        "\"Cowards die many times before their deaths; the valiant taste of death but once.\"\n— JULIUS CAESAR",
        "\"Victory belongs to the most persevering. Impossible is a word found only in the dictionary of fools.\"\n— NAPOLEON BONAPARTE",
        "\"A good plan violently executed now is better than a perfect plan executed next week.\"\n— GEN. GEORGE S. PATTON",
        "\"Difficulties strengthen the mind, as labor does the body.\"\n— SENECA",
        "\"Stimulate, don't annihilate. Consistency is the key to longevity and strength.\"\n— LEE HANEY",
        "\"Conquer yourself first before you attempt to conquer the world.\"\n— GENGHIS KHAN"
    )

    private val _uiState = MutableStateFlow(ProtocolUiState())
    val uiState: StateFlow<ProtocolUiState> = _uiState.asStateFlow()

    init {
        val todayIdx = getTodayDayIndex()
        val savedSplit = loadWeeklySplit()
        
        _uiState.update { state ->
            state.copy(
                currentDayIndex = todayIdx,
                selectedDayIndex = todayIdx,
                weeklySplit = savedSplit,
                currentQuote = quotes.random()
            )
        }
        updateChecklistForSelectedDay()
    }

    private fun getTodayDayIndex(): Int {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
    }

    fun onQuoteTapped() {
        val nextQuote = quotes.filter { it != _uiState.value.currentQuote }.randomOrNull() ?: quotes.first()
        _uiState.update { it.copy(currentQuote = nextQuote) }
    }

    fun onDaySelected(dayIndex: Int) {
        _uiState.update { it.copy(selectedDayIndex = dayIndex) }
        updateChecklistForSelectedDay()
    }

    fun onUpdateSplit(dayIndex: Int, newSplit: SplitType) {
        val updatedMap = _uiState.value.weeklySplit.toMutableMap()
        updatedMap[dayIndex] = newSplit
        
        saveWeeklySplit(updatedMap)

        _uiState.update { it.copy(weeklySplit = updatedMap) }
        updateChecklistForSelectedDay()
    }

    fun onToggleExercise(exerciseId: String) {
        val updatedList = _uiState.value.todayChecklist.map { ex ->
            if (ex.id == exerciseId) ex.copy(isDone = !ex.isDone) else ex
        }
        _uiState.update { it.copy(todayChecklist = updatedList) }
    }

    fun onExecuteProtocol() {
        val allDone = _uiState.value.todayChecklist.map { it.copy(isDone = true) }
        _uiState.update { 
            it.copy(
                todayChecklist = allDone,
                isCompleted = true
            )
        }
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        prefs.edit().putBoolean("protocol_executed_$todayStr", true).apply()
    }

    private fun updateChecklistForSelectedDay() {
        val currentSplit = _uiState.value.weeklySplit[_uiState.value.selectedDayIndex] ?: SplitType.REST
        val exercises = when (currentSplit) {
            SplitType.PUSH -> listOf(
                ProtocolExercise("ex_1", "BARBELL BENCH PRESS", "4 SETS x 8 REPS"),
                ProtocolExercise("ex_2", "OVERHEAD SHOULDER PRESS", "3 SETS x 10 REPS"),
                ProtocolExercise("ex_3", "INCLINE DUMBBELL PRESS", "3 SETS x 12 REPS"),
                ProtocolExercise("ex_4", "TRICEP CABLE PRESS", "3 SETS x 15 REPS"),
                ProtocolExercise("ex_5", "LATERAL RAISES", "4 SETS x 15 REPS")
            )
            SplitType.PULL -> listOf(
                ProtocolExercise("ex_1", "CONVENTIONAL DEADLIFT", "4 SETS x 5 REPS"),
                ProtocolExercise("ex_2", "BARBELL BENT-OVER ROW", "4 SETS x 8 REPS"),
                ProtocolExercise("ex_3", "LAT PULLDOWNS", "3 SETS x 10 REPS"),
                ProtocolExercise("ex_4", "FACE PULLS", "3 SETS x 15 REPS"),
                ProtocolExercise("ex_5", "EZ-BAR BICEP CURLS", "3 SETS x 12 REPS")
            )
            SplitType.LEGS -> listOf(
                ProtocolExercise("ex_1", "BARBELL BACK SQUAT", "4 SETS x 8 REPS"),
                ProtocolExercise("ex_2", "ROMANIAN DEADLIFT", "3 SETS x 10 REPS"),
                ProtocolExercise("ex_3", "LEG PRESS", "3 SETS x 12 REPS"),
                ProtocolExercise("ex_4", "HAMSTRING CURLS", "3 SETS x 15 REPS")
            )
            SplitType.UPPER -> listOf(
                ProtocolExercise("ex_1", "INCLINE BENCH PRESS", "4 SETS x 8 REPS"),
                ProtocolExercise("ex_2", "CHEST-SUPPORTED ROW", "4 SETS x 10 REPS")
            )
            SplitType.LOWER -> listOf(
                ProtocolExercise("ex_1", "FRONT SQUAT / HACK SQUAT", "4 SETS x 8 REPS"),
                ProtocolExercise("ex_2", "LEG EXTENSIONS", "3 SETS x 15 REPS")
            )
            SplitType.CARDIO -> listOf(
                ProtocolExercise("ex_1", "ZONE 2 CARDIO", "30 MINS"),
                ProtocolExercise("ex_2", "HIIT SPRINT INTERVALS", "10 MINS")
            )
            SplitType.CUSTOM -> listOf(
                ProtocolExercise("ex_1", "CUSTOM COMPOUND MOVEMENT", "4 SETS x 10 REPS")
            )
            SplitType.REST -> emptyList()
        }

        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val isTodayExecuted = prefs.getBoolean("protocol_executed_$todayStr", false)

        _uiState.update { 
            it.copy(
                todayChecklist = exercises,
                isCompleted = isTodayExecuted
            )
        }
    }

    private fun loadWeeklySplit(): Map<Int, SplitType> {
        val defaultMap = mapOf(
            0 to SplitType.PUSH, 1 to SplitType.PULL, 2 to SplitType.LEGS,
            3 to SplitType.REST, 4 to SplitType.UPPER, 5 to SplitType.LOWER, 6 to SplitType.REST
        )
        val result = mutableMapOf<Int, SplitType>()
        for (i in 0..6) {
            val savedName = prefs.getString("split_day_$i", defaultMap[i]?.name)
            result[i] = try { SplitType.valueOf(savedName ?: SplitType.REST.name) } catch (e: Exception) { defaultMap[i] ?: SplitType.REST }
        }
        return result
    }

    private fun saveWeeklySplit(map: Map<Int, SplitType>) {
        val editor = prefs.edit()
        map.forEach { (i, split) -> editor.putString("split_day_$i", split.name) }
        editor.apply()
    }
}
