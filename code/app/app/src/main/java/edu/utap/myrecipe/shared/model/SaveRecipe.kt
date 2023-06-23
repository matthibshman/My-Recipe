package edu.utap.myrecipe.shared.model

data class SaveRecipe (
    val username: String,
    val type: String,
    val id: String,
    val recipe: SaveRecipeInfo
)