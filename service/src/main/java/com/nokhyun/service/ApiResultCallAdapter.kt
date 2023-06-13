package com.nokhyun.service

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

internal class ApiResultCallAdapter<R>(
    private val successType: Type
) : CallAdapter<R, Call<ApiResult<R>>> {
    override fun responseType(): Type = successType

    override fun adapt(call: Call<R>): Call<ApiResult<R>> = ApiResultCall(call, successType)
}

private class ApiResultCall<R>(
    private val delegate: Call<R>,
    private val successType: Type
) : Call<ApiResult<R>> {

    override fun enqueue(callback: Callback<ApiResult<R>>) {
        delegate.enqueue(object : Callback<R> {
            override fun onResponse(call: Call<R>, response: Response<R>) {
                callback.onResponse(this@ApiResultCall, Response.success(response.toApiResult()))
            }

            override fun onFailure(call: Call<R>, t: Throwable) {
                val error = if (t is IOException) {
                    ApiResult.Failure.NetworkError(throwable = t)
                } else {
                    ApiResult.Failure.UnknownApiError(t)
                }

                callback.onResponse(this@ApiResultCall, Response.success(error))
            }

            private fun Response<R>.toApiResult(): ApiResult<R> {
                if (!isSuccessful) {
                    val errorBody = errorBody()!!.string()
                    return ApiResult.Failure.HttpError(
                        code = code(),
                        message = message(),
                        body = errorBody
                    )
                }

                // Body가 있으면 성공응답.
                body()?.let { body -> return ApiResult.successOf(body) }

                return if (successType == Unit::class.java) {
                    @Suppress("UNCHECKED_CAST")
                    ApiResult.successOf(Unit as R)
                } else {
                    ApiResult.Failure.UnknownApiError(
                        IllegalArgumentException(
                            "Response code is ${code()} but body is null.\nIf you expect response body to be null then define your API method as returning Unit:\n" +
                                    "@POST fun postSomething(): ApiResult<Unit>"
                        )
                    )
                }
            }
        })
    }

    override fun clone(): Call<ApiResult<R>> {
        return ApiResultCall(delegate.clone(), successType)
    }

    override fun execute(): Response<ApiResult<R>> {
        throw UnsupportedOperationException("This adapter does not support sync execution")
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}