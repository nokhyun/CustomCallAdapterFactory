package com.nokhyun.relaynetwork

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokhyun.service.onFailure
import com.nokhyun.service.onSuccess
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainViewModel(
    private val serviceRepository: ServiceRepository = ServiceRepositoryImpl()
) : ViewModel() {

    private val ceh = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("[CoroutineExceptionHandler]", "throwable: ${throwable}\ncoroutineContext: ${coroutineContext}")
        _resultState.value = NetworkUIState.FAILURE
    }

    private val _resultState: MutableState<NetworkUIState> = mutableStateOf(NetworkUIState.NONE)
    val resultState: State<NetworkUIState> = _resultState

    fun receiveData() {
        _resultState.value = NetworkUIState.LOADING
        viewModelScope.launch {
            serviceRepository.receiveData()
                .flatMapMerge { flowOf(serviceRepository.sendData(it).collect()) }
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                    Log.e("[Network Error]", "message: ${it.message}")
                    _resultState.value = NetworkUIState.FAILURE
                }
                .collectLatest {
                    _resultState.value = NetworkUIState.SUCCESS
                }
        }
    }

    fun todos() {
        _resultState.value = NetworkUIState.LOADING
        viewModelScope.launch(ceh) {
            serviceRepository.todo1()
                .catch {
                    Log.e("[catch]", "result: $it")
                }
                .collectLatest {
                    Log.e("[todos]", "result: $it")
                    it.onSuccess {
                        _resultState.value = NetworkUIState.SUCCESS
                    }.onFailure {
                        // ceh coroutineContext 를 설정해놔도 onFailure 가 호출됨.
                        // Retrofit의 CallAdapterFactory로 일괄 처리
                        Log.e("[onFailure]", "result: $it")
                        _resultState.value = NetworkUIState.FAILURE
                    }
                }
        }
    }
}

sealed class NetworkUIState(
    val stateMessage: String = ""
) {
    object NONE : NetworkUIState(stateMessage = "")
    object LOADING : NetworkUIState(stateMessage = "LOADING...")
    object SUCCESS : NetworkUIState(stateMessage = "SUCCESS")
    object FAILURE : NetworkUIState(stateMessage = "FAILURE")
}