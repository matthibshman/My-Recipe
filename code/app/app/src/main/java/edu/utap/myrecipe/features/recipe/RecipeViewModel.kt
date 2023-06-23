package edu.utap.myrecipe.features.recipe

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utap.myrecipe.shared.model.RecipeInstructions
import edu.utap.myrecipe.shared.model.SaveRecipe
import edu.utap.myrecipe.shared.model.SaveRecipeInfo
import edu.utap.myrecipe.shared.model.SavedRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RecipeViewModel : ViewModel() {

    private val recipeRepo = RecipeRepo()

    private var currentRecipe = MutableLiveData<SavedRecipe>()
    private var isFetching = MutableLiveData(false)
    private var recipeInstructions = MediatorLiveData<RecipeInstructions>().apply {
        val instructions = this
        this.addSource(currentRecipe) {
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                isFetching.postValue(true)
                instructions.postValue(recipeRepo.getRecipeInstructions(it.id))
                isFetching.postValue(false)
            }
        }
    }

    fun setRecipe(recipe: SavedRecipe) {
        currentRecipe.value = recipe
    }

    fun favorite() {
        currentRecipe.value?.let { recipe ->
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                recipeRepo.getUser()?.let {
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
                    recipeRepo.favoriteRecipe(
                        it
                    )
                }
            }
        }
    }

    fun cooked() {
        currentRecipe.value?.let { recipe ->
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                recipeRepo.getUser()?.let {
                    SaveRecipe(
                        it.uid, "cooked", recipe.id, SaveRecipeInfo(
                            recipe.id,
                            recipe.title,
                            recipe.image,
                            Date(),
                            !recipe.cooked,
                            recipe.favorite
                        )
                    )
                }?.let {
                    recipeRepo.cookedRecipe(it)
                }
            }
        }
    }

    fun observeRecipe() = currentRecipe
    fun observeRecipeInstructions() = recipeInstructions
    fun observeIsFetching() = isFetching
}