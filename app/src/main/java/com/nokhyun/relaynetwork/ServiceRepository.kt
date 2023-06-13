package com.nokhyun.relaynetwork

import android.util.Log
import com.nokhyun.service.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.await

interface ServiceRepository {
    suspend fun receiveData(): Flow<Any>
    suspend fun sendData(data: Any): Flow<Any>
    suspend fun todo1(): Flow<ApiResult<Any>>
}

class ServiceRepositoryImpl(
//    private val receiveService: Service = Network.receiverService(Service::class.java),
//    private val sendService: Service = Network.sendService(Service::class.java),
    private val todosService: com.nokhyun.service.Service = com.nokhyun.service.Network.todosRetrofit

) : ServiceRepository {
    override suspend fun receiveData(): Flow<Any> = flow {
//        emit(receiveService.fetch().await())
        emit("")
    }

    override suspend fun sendData(data: Any): Flow<Any> = flow {
        Log.e("[repo]", ":: sendData: $data")
//        emit(sendService.insert(data).await())
        emit("")
    }

    override suspend fun todo1(): Flow<ApiResult<Any>> = flow {
        Log.e("[repo]", ":: todo1")
        emit(todosService.getTodos())
    }
}