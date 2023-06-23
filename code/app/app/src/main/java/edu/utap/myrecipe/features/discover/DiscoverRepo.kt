package edu.utap.myrecipe.features.discover

import edu.utap.myrecipe.shared.api.MyRecipeApi
import edu.utap.myrecipe.shared.api.Repo
import edu.utap.myrecipe.shared.model.Recipe
import edu.utap.myrecipe.shared.model.SaveRecipe

class DiscoverRepo : Repo() {

    suspend fun getRecipes(filters: String, dairyFree: Boolean, glutenFree: Boolean): List<Recipe> {
        var result = MyRecipeApi.tryAPICall { api.getRecipes(filters, dairyFree, glutenFree) }
        result = result ?: MyRecipeApi.tryAPICall { api.getRecipes(filters, dairyFree, glutenFree) }
        return result ?: emptyList()
    }

    suspend fun saveRecipe(saveRecipe: SaveRecipe) {
        val result = MyRecipeApi.tryAPICall { api.saveRecipe(saveRecipe) }
        if (result == null) {
            MyRecipeApi.tryAPICall { api.saveRecipe(saveRecipe) }
        }
    }
}