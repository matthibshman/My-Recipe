package edu.utap.myrecipe.features.favorites

import edu.utap.myrecipe.shared.api.MyRecipeApi
import edu.utap.myrecipe.shared.api.Repo
import edu.utap.myrecipe.shared.model.SaveRecipe
import edu.utap.myrecipe.shared.model.SavedRecipe

class FavoriteRepo : Repo() {
    suspend fun getFavoriteRecipes(username: String): List<SavedRecipe> {
        var result = MyRecipeApi.tryAPICall { api.getUserRecipes(username) }
        result = result ?: MyRecipeApi.tryAPICall { api.getUserRecipes(username) }
        result = result ?: emptyList()
        return result.filter { it.favorite }
    }

    suspend fun favoriteRecipe(saveRecipe: SaveRecipe) {
        val result = MyRecipeApi.tryAPICall { api.saveRecipe(saveRecipe) }
        if (result == null) {
            MyRecipeApi.tryAPICall { api.saveRecipe(saveRecipe) }
        }
    }
}