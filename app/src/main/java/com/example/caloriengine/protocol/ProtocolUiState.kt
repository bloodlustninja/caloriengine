package com.example.caloriengine.protocol

data class ProtocolExercise(
    val id: String,
    val name: String,
    val setsReps: String,
    val isDone: Boolean = false
)

enum class SplitType(val label: String) {
    PUSH("PUSH"),
    PULL("PULL"),
    LEGS("LEGS"),
    UPPER("UPPER"),
    LOWER("LOWER"),
    CARDIO("CARDIO"),
    REST("REST"),
    CUSTOM("CUSTOM")
}

data class ProtocolUiState(
    val currentDayIndex: Int = 0, // 0 = MON ... 6 = SUN
    val selectedDayIndex: Int = 0,
    val weeklySplit: Map<Int, SplitType> = mapOf(
        0 to SplitType.PUSH,
        1 to SplitType.PULL,
        2 to SplitType.LEGS,
        3 to SplitType.REST,
        4 to SplitType.UPPER,
        5 to SplitType.LOWER,
        6 to SplitType.REST
    ),
    val currentQuote: String = "Rest if you must, operative. Tomorrow, the iron demands its tithe.",
    val todayChecklist: List<ProtocolExercise> = emptyList(),
    val isCompleted: Boolean = false,
    val kcalBurnOffset: Int = 300
)
