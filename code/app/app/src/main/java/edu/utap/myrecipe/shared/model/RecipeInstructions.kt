package edu.utap.myrecipe.shared.model

data class RecipeInstructions(
    val id: String,
    val equipment: List<String>,
    val ingredients: List<String>,
    val steps: List<RecipeStep>
)
