package com.star_zero.moshi.enumadapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter
import com.squareup.moshi.rawType
import java.lang.reflect.Type

annotation class MoshiEnumFallback

/**
 * Annotating @MoshiEnumFallback will automatically add a fallback when an unknown value comes in the Enum.
 */
class MoshiEnumAdapterFactory : JsonAdapter.Factory {

    @Suppress("TYPE_MISMATCH_WARNING", "UNCHECKED_CAST")
    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        val rawType = type.rawType
        if (!Enum::class.java.isAssignableFrom(rawType)) {
            return null
        }

        var fallbackEnum: Enum<*>? = null

        rawType.enumConstants.forEach {
            val enumElement = it as Enum<*>
            val fallback =
                rawType.getField(enumElement.name).getAnnotation(MoshiEnumFallback::class.java)
            if (fallback != null) {
                fallbackEnum = enumElement
                return@forEach
            }
        }

        return if (fallbackEnum == null) {
            EnumJsonAdapter.create(type as Class<Enum<*>>)
        } else {
            EnumJsonAdapter.create(type as Class<Enum<*>>).withUnknownFallback(fallbackEnum)
        }
    }
}
