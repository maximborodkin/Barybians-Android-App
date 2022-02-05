package ru.maxim.barybians.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import dagger.Reusable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Reusable
class NetworkUtils @Inject constructor(
    private val applicationContext: Context,
    private val applicationScope: CoroutineScope
) {
    init {
        registerNetworkStateChangesListener()
    }

    private val _networkStateChangeListener = MutableStateFlow(true)
    val networkStateChangeListener: StateFlow<Boolean> = _networkStateChangeListener.asStateFlow()

    fun isOnline(): Boolean = networkStateChangeListener.value

    private fun registerNetworkStateChangesListener() {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                applicationScope.launch {
                    _networkStateChangeListener.emit(true)
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                applicationScope.launch {
                    _networkStateChangeListener.emit(false)
                }
            }
        }
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
}