package edu.utap.myrecipe.shared.api

import com.google.firebase.auth.FirebaseAuth

abstract class Repo {
    val api = MyRecipeApi.create()

    fun getUser() = FirebaseAuth.getInstance().currentUser
}