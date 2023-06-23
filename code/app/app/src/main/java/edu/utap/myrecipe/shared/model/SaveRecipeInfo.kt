package edu.utap.myrecipe.shared.model

import java.util.*

data class SaveRecipeInfo(
    val id: String,
    val name: String,
    val url: String,
    val dateAdded: Date,
    val cooked: Boolean,
    val favorite: Boolean
)