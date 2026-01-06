package com.example.voicecommandaiapp.util

import com.example.voicecommandaiapp.model.TaskStatus
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

class TaskStatusAdapter : TypeAdapter<TaskStatus>() {

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: TaskStatus?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.value(value.name.lowercase())
    }

    @Throws(IOException::class)
    override fun read(input: JsonReader): TaskStatus {
        val status = input.nextString().uppercase()
        return when (status) {
            "UPCOMING" -> TaskStatus.UPCOMING
            "PENDING" -> TaskStatus.PENDING
            "OVERDUE" -> TaskStatus.OVERDUE
            "COMPLETED" -> TaskStatus.COMPLETED
            else -> TaskStatus.PENDING // Default value
        }
    }
}
