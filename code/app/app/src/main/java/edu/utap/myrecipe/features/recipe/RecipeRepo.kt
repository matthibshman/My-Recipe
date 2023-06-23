package edu.utap.myrecipe.features.recipe

import edu.utap.myrecipe.shared.api.MyRecipeApi
import edu.utap.myrecipe.shared.api.Repo
import edu.utap.myrecipe.shared.model.RecipeInstructions
import edu.utap.myrecipe.shared.model.SaveRecipe

class RecipeRepo : Repo() {
    private val recipeInstructionsCache = HashMap<String, RecipeInstructions>()

    suspend fun getRecipeInstructions(id: String): RecipeInstructions? {
        val recipeInstructions =
            if (recipeInstructionsCache.contains(id)) recipeInstructionsCache[id]
            else {
                var result = MyRecipeApi.tryAPICall { api.getRecipeInstructions(id) }
                if (result == null) {
                    result = MyRecipeApi.tryAPICall { api.getRecipeInstructions(id) }
                    if (result == null) {
                        result = RecipeInstructions(id, emptyList(), emptyList(), emptyList())
                    } else {
                        recipeInstructionsCache[id] = result
                    }
                } else {
                    recipeInstructionsCache[id] = result
                }
                result
            }

        return recipeInstructions
    }

    suspend fun favoriteRecipe(saveRecipe: SaveRecipe) {
        val result = MyRecipeApi.tryAPICall { api.saveRecipe(saveRecipe) }
        if (result == null) {
            MyRecipeApi.tryAPICall { api.saveRecipe(saveRecipe) }
        }
    }

    suspend fun cookedRecipe(cookedRecipe: SaveRecipe) {
        val result = MyRecipeApi.tryAPICall { api.saveRecipe(cookedRecipe) }
        if (result == null) {
            MyRecipeApi.tryAPICall { api.saveRecipe(cookedRecipe) }
        }
    }
}