package edu.utap.myrecipe.features.login

import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    fun updateUser() {
        firebaseAuthLiveData.updateUser()
    }
}