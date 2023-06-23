package edu.utap.myrecipe.features.saved

import edu.utap.myrecipe.shared.api.MyRecipeApi
import edu.utap.myrecipe.shared.api.Repo
import edu.utap.myrecipe.shared.model.SavedRecipe

class SavedRepo : Repo() {
    suspend fun getSavedRecipes(username: String): List<SavedRecipe> {
        var result = MyRecipeApi.tryAPICall { api.getUserRecipes(username) }
        result = result ?: MyRecipeApi.tryAPICall { api.getUserRecipes(username) }
        return result ?: emptyList()
    }
}