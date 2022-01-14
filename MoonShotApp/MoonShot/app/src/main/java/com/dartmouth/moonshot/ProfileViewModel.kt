package com.dartmouth.moonshot

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ProfileViewModel: ViewModel() {
    private var profileRepo = ProfileRepository.StaticFunction.getInstance()

   // val user: LiveData<User> = profileRepo.getUser().asLiveData(CoroutineScope(Dispatchers.IO).coroutineContext)

    fun getUser(): LiveData<User> {
        return profileRepo.getUser()
    }

    fun updateName(userName: String?) {
        profileRepo.updateName(userName!!)
    }

    fun updateBio(bioText: String?) {
        profileRepo.updateBio(bioText!!)
    }

    fun updateImage(imagePath: Uri, changeImg: Int) {
        profileRepo.updateImage(imagePath, changeImg)
    }

    fun updateSavedCoins(coinList: ArrayList<String>) {
        profileRepo.updateSavedCoins(coinList)
    }
}