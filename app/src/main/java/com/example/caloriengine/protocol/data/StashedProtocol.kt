package com.example.caloriengine.protocol.data

data class StashedProtocol(
    val id: Long = 0,
    val title: String,
    val sourceUrl: String,
    val targetSplit: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val formNotes: String = ""
)
