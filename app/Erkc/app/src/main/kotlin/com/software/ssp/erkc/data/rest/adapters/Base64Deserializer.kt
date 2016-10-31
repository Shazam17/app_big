package com.software.ssp.erkc.data.rest.adapters

import android.util.Base64
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * @author Alexander Popov on 31/10/2016.
 */
class Base64Deserializer : JsonDeserializer<ByteArray> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ByteArray {
        return Base64.decode(json?.asString, Base64.NO_WRAP)
    }

}