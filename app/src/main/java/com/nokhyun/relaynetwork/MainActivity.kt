package com.nokhyun.relaynetwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nokhyun.relaynetwork.ui.theme.RelayNetworkTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel = viewModel()

            RelayNetworkTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier.wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "fetch todos")
                        Button(
                            modifier = Modifier.wrapContentSize(),
//                            onClick = { mainViewModel.receiveData() }
                            onClick = { mainViewModel.todos() }
                        ) {
                            Text(text = "Click")
                        }

                        Text(
                            text = when (mainViewModel.resultState.value) {
                                NetworkUIState.SUCCESS -> mainViewModel.resultState.value.stateMessage
                                NetworkUIState.FAILURE -> mainViewModel.resultState.value.stateMessage
                                NetworkUIState.LOADING -> mainViewModel.resultState.value.stateMessage
                                NetworkUIState.NONE -> ""
                            }
                        )

                        AnimatedVisibility(visible = mainViewModel.resultState.value == NetworkUIState.LOADING) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}