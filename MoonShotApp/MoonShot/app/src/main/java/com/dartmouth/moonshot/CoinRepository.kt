package com.dartmouth.moonshot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import androidx.annotation.NonNull
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

//sources used: https://github.com/adnan0786/ChatMeKotlin/blob/master/app/src/main/java/com/example/chatmekotlin/Repository/AppRepo.kt
//https://prus-piotr.medium.com/use-kotlin-coroutines-for-firebase-real-time-database-streams-e28d6083709c

class CoinRepository {

    private var liveData: MutableLiveData<ArrayList<Coin>>? = null
    private var liveDataSavedC: MutableLiveData<ArrayList<Coin>>? = null
    private lateinit var databaseReference: DatabaseReference

    private var mFirebaseAuth = FirebaseAuth.getInstance()

    //val allWorkoutEntries: Flow<ArrayList<Coin>> = workoutDatabaseDao.getAllWorkoutEntries()

    object StaticFunction {
        private var instance: CoinRepository? = null
        fun getInstance(): CoinRepository {
            if (instance == null)
                instance = CoinRepository()

            return instance!!
        }
    }

    /*interface ValueEventListener {
        fun onDataChange(snapshot: DataSnapshot)
        fun onCancelled(error: DatabaseError)
    }*/

    fun DatabaseReference.observeValue(): Flow<DataSnapshot?> =
        callbackFlow {
            val listener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    offer(snapshot)
                }
            }
            addValueEventListener(listener)
            awaitClose { removeEventListener(listener) }
        }

    fun getAllCoins(): Flow<ArrayList<Coin>> = Firebase.database.getReference("Coins").observeValue().map {
        var coinsList = ArrayList<Coin>()
        if (it != null) {
            for (snap in it.children) {
                val coinModel = snap.getValue(Coin::class.java)
                if (coinModel != null) {
                    coinsList.add(coinModel)
                }
            }
        }
        //it?.value as ArrayList<Coin>
        coinsList
    }

    //IF WE WANT TO USE A COROUTINE FOR THIS IN THE VIEWMODEL USE THIS INSTEAD
    /*fun getSavedCoins(coinIDs: ArrayList<String>): Flow<ArrayList<Coin>> = Firebase.database.getReference("Coins").observeValue().map {
        var savedCoinsList = ArrayList<Coin>()
        if (it != null) {
            for (snap in it.children) {
                val coinModel = snap.getValue(Coin::class.java)
                if (coinModel != null && coinIDs.contains(coinModel.id)) {
                    savedCoinsList.add(coinModel)
                }
            }
        }
        //it?.value as ArrayList<Coin>
        savedCoinsList
    }*/

    fun getSavedCoins(coinIDs: ArrayList<String>): LiveData<ArrayList<Coin>>{
        var savedCoinsList = ArrayList<Coin>()
        if (liveDataSavedC == null){
            liveDataSavedC = MutableLiveData()
        }
        databaseReference =
            Firebase.database.getReference("Coins")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for(snap in snapshot.children){
                        val coinModel = snap.getValue(Coin::class.java)
                        if (coinModel != null && coinIDs.contains(coinModel.id)) {
                            savedCoinsList.add(coinModel)
                        }
                    }
                    liveDataSavedC!!.postValue(savedCoinsList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return liveDataSavedC!!
    }


}
