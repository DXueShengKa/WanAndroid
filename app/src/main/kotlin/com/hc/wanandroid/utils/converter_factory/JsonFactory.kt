package com.hc.wanandroid.utils.converter_factory

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.StringFormat
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


internal class JsonFactory(
    private val contentType: MediaType,
    private val serializer: Serializer
) : Converter.Factory() {
    // Retaining interface contract.
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val loader = serializer.serializer(type)
        return Converter {
            serializer.fromResponseBody(loader, it)
        }
    }

    // Retaining interface contract.
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<Any, RequestBody> {
        val saver = serializer.serializer(type)
        return Converter {
            serializer.toRequestBody(contentType, saver, it)
        }
    }
}

/**
 * Return a [Converter.Factory] which uses Kotlin serialization for string-based payloads.
 *
 * Because Kotlin serialization is so flexible in the types it supports, this converter assumes
 * that it can handle all types. If you are mixing this with something else, you must add this
 * instance last to allow the other converters a chance to see their types.
 */
@OptIn(ExperimentalSerializationApi::class)
fun StringFormat.asConverterFactory(contentType: MediaType): Converter.Factory {
    return JsonFactory(
        contentType,
        Serializer.FromString(this)
    )
}

/**
 * Return a [Converter.Factory] which uses Kotlin serialization for byte-based payloads.
 *
 * Because Kotlin serialization is so flexible in the types it supports, this converter assumes
 * that it can handle all types. If you are mixing this with something else, you must add this
 * instance last to allow the other converters a chance to see their types.
 */
@OptIn(ExperimentalSerializationApi::class)
fun BinaryFormat.asConverterFactory(contentType: MediaType): Converter.Factory {
    return JsonFactory(contentType, Serializer.FromBytes(this))
}