package com.nokhyun.service

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)

        if (rawType != Call::class.java) return null
        check(returnType is ParameterizedType) {
            val name = parseTypeName(returnType)

            throw IllegalArgumentException(
                "Return type must be parameterized as $name<Foo> or $name<out Foo>"
            )
        }

        return when (rawType) {
            Call::class.java -> apiResultAdapter(returnType)
            else -> null
        }
    }

    private fun apiResultAdapter(
        returnType: ParameterizedType
    ): CallAdapter<Type, out Call<out Any>>? {
        val wrapperType = getParameterUpperBound(0, returnType)
        return when (getRawType(wrapperType)) {
            ApiResult::class.java -> {
                val bodyType = extractReturnType(wrapperType, returnType)
                ApiResultCallAdapter(bodyType)
            }

            else -> null
        }
    }

    private fun extractReturnType(
        wrapperType: Type,
        returnType: ParameterizedType
    ): Type {
        if (wrapperType !is ParameterizedType) {
            val name = parseTypeName(returnType)
            throw IllegalArgumentException(
                "Return type must be parameterized as $name<ResponseBody>"
            )
        }

        return getParameterUpperBound(0, wrapperType)
    }

    private fun parseTypeName(type: Type): String {
        return type.toString()
            .split(".")
            .last()
    }
}
