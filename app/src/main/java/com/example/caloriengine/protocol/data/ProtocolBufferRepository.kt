package com.example.caloriengine.protocol.data

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

class ProtocolBufferRepository(context: Context) {
    private val db = ProtocolBufferDatabase.getInstance(context)

    val stashedCount: StateFlow<Int> = db.stashedCount

    fun refreshCount() = db.refreshCount()

    suspend fun stashProtocol(
        title: String,
        sourceUrl: String,
        targetSplit: String,
        formNotes: String = ""
    ): Long {
        return db.stashProtocol(title, sourceUrl, targetSplit, formNotes)
    }

    suspend fun stashMultipleProtocols(
        items: List<Pair<String, String>>,
        sourceUrl: String,
        targetSplit: String
    ): List<Long> {
        return db.stashMultipleProtocols(items, sourceUrl, targetSplit)
    }

    suspend fun getActiveStashedProtocols(): List<StashedProtocol> {
        return db.getAllActiveStashedProtocols()
    }

    suspend fun consumeStashedProtocol(id: Long) {
        db.markAsInactive(id)
    }
}
