package com.example.remitconnect.data.local.entities.typeConverters

import androidx.room.TypeConverter
import com.example.remitconnect.data.model.Recipient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipientConverter {
    /**
     * Convert a a list of [Recipient] to a Json
     */
    @TypeConverter
    fun fromTypesJson(recipient: Recipient?): String {
        return Gson().toJson(recipient) ?: ""
    }

    /**
     * Convert a json to a list of [Recipient]
     */
    @TypeConverter
    fun toTypesList(jsonTypes: String?): Recipient? {
        val notesType = object : TypeToken<Recipient>() {}.type
        return Gson().fromJson(jsonTypes, notesType)
    }
}