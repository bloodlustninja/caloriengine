package com.example.caloriengine.protocol.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class ProtocolBufferDatabase private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "protocol_buffer.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_NAME = "stashed_protocols"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_SOURCE_URL = "source_url"
        const val COL_TARGET_SPLIT = "target_split"
        const val COL_TIMESTAMP = "timestamp"
        const val COL_IS_ACTIVE = "is_active"
        const val COL_FORM_NOTES = "form_notes"

        @Volatile
        private var INSTANCE: ProtocolBufferDatabase? = null

        fun getInstance(context: Context): ProtocolBufferDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProtocolBufferDatabase(context).also { INSTANCE = it }
            }
        }
    }

    private val _stashedCount = MutableStateFlow(0)
    val stashedCount: StateFlow<Int> = _stashedCount.asStateFlow()

    init {
        refreshCount()
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_SOURCE_URL TEXT NOT NULL,
                $COL_TARGET_SPLIT TEXT NOT NULL,
                $COL_TIMESTAMP INTEGER NOT NULL,
                $COL_IS_ACTIVE INTEGER NOT NULL DEFAULT 1,
                $COL_FORM_NOTES TEXT DEFAULT ''
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COL_FORM_NOTES TEXT DEFAULT ''")
            } catch (_: Exception) {}
        }
    }

    fun refreshCount() {
        try {
            val cursor = readableDatabase.rawQuery(
                "SELECT COUNT(*) FROM $TABLE_NAME WHERE $COL_IS_ACTIVE = 1",
                null
            )
            cursor.use {
                if (it.moveToFirst()) {
                    _stashedCount.value = it.getInt(0)
                }
            }
        } catch (_: Exception) {}
    }

    suspend fun stashProtocol(
        title: String,
        sourceUrl: String,
        targetSplit: String,
        formNotes: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, title)
            put(COL_SOURCE_URL, sourceUrl)
            put(COL_TARGET_SPLIT, targetSplit)
            put(COL_TIMESTAMP, System.currentTimeMillis())
            put(COL_IS_ACTIVE, 1)
            put(COL_FORM_NOTES, formNotes)
        }
        val id = db.insert(TABLE_NAME, null, values)
        refreshCount()
        id
    }

    suspend fun stashMultipleProtocols(
        items: List<Pair<String, String>>, // (title, formNotes)
        sourceUrl: String,
        targetSplit: String
    ): List<Long> = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val ids = mutableListOf<Long>()
        db.beginTransaction()
        try {
            val now = System.currentTimeMillis()
            for ((title, notes) in items) {
                if (title.isNotBlank()) {
                    val values = ContentValues().apply {
                        put(COL_TITLE, title)
                        put(COL_SOURCE_URL, sourceUrl)
                        put(COL_TARGET_SPLIT, targetSplit)
                        put(COL_TIMESTAMP, now)
                        put(COL_IS_ACTIVE, 1)
                        put(COL_FORM_NOTES, notes)
                    }
                    val id = db.insert(TABLE_NAME, null, values)
                    ids.add(id)
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        refreshCount()
        ids
    }

    suspend fun getAllActiveStashedProtocols(): List<StashedProtocol> = withContext(Dispatchers.IO) {
        val list = mutableListOf<StashedProtocol>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_IS_ACTIVE = ?",
            arrayOf("1"),
            null,
            null,
            "$COL_TIMESTAMP DESC"
        )
        cursor.use {
            val idIdx = it.getColumnIndexOrThrow(COL_ID)
            val titleIdx = it.getColumnIndexOrThrow(COL_TITLE)
            val sourceIdx = it.getColumnIndexOrThrow(COL_SOURCE_URL)
            val splitIdx = it.getColumnIndexOrThrow(COL_TARGET_SPLIT)
            val timeIdx = it.getColumnIndexOrThrow(COL_TIMESTAMP)
            val activeIdx = it.getColumnIndexOrThrow(COL_IS_ACTIVE)
            val notesIdx = it.getColumnIndex(COL_FORM_NOTES)

            while (it.moveToNext()) {
                val notes = if (notesIdx != -1) it.getString(notesIdx) ?: "" else ""
                list.add(
                    StashedProtocol(
                        id = it.getLong(idIdx),
                        title = it.getString(titleIdx),
                        sourceUrl = it.getString(sourceIdx),
                        targetSplit = it.getString(splitIdx),
                        timestamp = it.getLong(timeIdx),
                        isActive = it.getInt(activeIdx) == 1,
                        formNotes = notes
                    )
                )
            }
        }
        list
    }

    suspend fun markAsInactive(id: Long) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_IS_ACTIVE, 0)
        }
        db.update(TABLE_NAME, values, "$COL_ID = ?", arrayOf(id.toString()))
        refreshCount()
    }
}
