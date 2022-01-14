package com.dartmouth.moonshot

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope

class CoinViewModel: ViewModel() {
    private var coinRepo = CoinRepository.StaticFunction.getInstance()

    val allCoins: LiveData<ArrayList<Coin>> = coinRepo.getAllCoins().asLiveData(CoroutineScope(Dispatchers.IO).coroutineContext)

    /*fun getAllCoins(): LiveData<ArrayList<Coin>> {
        return coinRepo.getAllCoins()
    }*/

    fun getSavedCoins(coinIDList: ArrayList<String>): LiveData<ArrayList<Coin>> {
        return coinRepo.getSavedCoins(coinIDList)//.asLiveData(CoroutineScope(Dispatchers.IO).coroutineContext)
    }

}