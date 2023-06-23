package edu.utap.myrecipe.features.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utap.myrecipe.shared.model.SaveRecipe
import edu.utap.myrecipe.shared.model.SaveRecipeInfo
import edu.utap.myrecipe.shared.model.SavedRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FavoriteViewModel : ViewModel() {
    private val favoriteRepo = FavoriteRepo()

    private var favoriteRecipes = MutableLiveData<List<SavedRecipe>>()
    private var isFetching = MutableLiveData(false)

    fun getRecipes() {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            favoriteRepo.getUser().let {
                if (it != null) {
                    isFetching.postValue(true)
                    favoriteRecipes.postValue(favoriteRepo.getFavoriteRecipes(it.uid))
                    isFetching.postValue(false)
                }
            }
        }
    }

    fun removeFavorite(recipe: SavedRecipe) {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            favoriteRepo.getUser()?.let {
                SaveRecipe(
                    it.uid, "favorite", recipe.id, SaveRecipeInfo(
                        recipe.id,
                        recipe.title,
                        recipe.image,
                        Date(),
                        recipe.cooked,
                        !recipe.favorite
                    )
                )
            }?.let {
                favoriteRepo.favoriteRecipe(
                    it
                )
            }
        }

        favoriteRecipes.postValue(favoriteRecipes.value?.filter { it -> it.id != recipe.id })
    }

    fun observeRecipes(): LiveData<List<SavedRecipe>> = favoriteRecipes
    fun observeIsFetching() = isFetching
}