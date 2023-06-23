package edu.utap.myrecipe.features.discover

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import edu.utap.myrecipe.shared.model.Recipe
import edu.utap.myrecipe.shared.model.SaveRecipe
import edu.utap.myrecipe.shared.model.SaveRecipeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DiscoverViewModel : ViewModel() {
    private val discoverRepo = DiscoverRepo()

    private var netRecipes = MutableLiveData<List<Recipe>>()
    private var recipeIndex = MutableLiveData<Int>()
    private var isFetching = MutableLiveData(false)
    private var currentRecipe = MediatorLiveData<Recipe>().apply {
        addSource(recipeIndex) { index ->
            netRecipes.value.let { recipes ->
                if (recipes != null) {
                    if (index < recipes.size) this.value = recipes[index]
                }
            }
        }
    }

    private var filters = ""
    private var dairyFree = false
    private var glutenFree = false

    fun getRecipes(forceFetch: Boolean = false) {
        if (forceFetch || netRecipes.value == null || recipeIndex.value!! == netRecipes.value!!.size) {
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                isFetching.postValue(true)
                netRecipes.postValue(discoverRepo.getRecipes(filters, dairyFree, glutenFree))
                recipeIndex.postValue(0)
                isFetching.postValue(false)
            }
        }
    }

    fun setFilters(newFilter: String, dairyFilter: Boolean, glutenFilter: Boolean) {
        dairyFree = dairyFilter
        glutenFree = glutenFilter
        filters = newFilter
        getRecipes(true)
    }

    fun saveRecipe() {
        val recipe = recipeIndex.value?.let { netRecipes.value?.get(it) }
        if (recipe != null) {
            viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
                discoverRepo.getUser()?.let {
                    SaveRecipe(
                        it.uid, "saved", recipe.id, SaveRecipeInfo(
                            recipe.id, recipe.title, recipe.image, Date(), false, false
                        )
                    )
                }?.let {
                    discoverRepo.saveRecipe(
                        it
                    )
                }
            }
        }
        nextRecipe()
    }

    fun nextRecipe() {
        recipeIndex.value = recipeIndex.value?.plus(1)
        getRecipes()
    }

    fun observeCurrentRecipe(): LiveData<Recipe> = currentRecipe
    fun observeIsFetching() = isFetching

    fun getFilters() = filters
    fun isDairyFree() = dairyFree
    fun isGlutenFree() = glutenFree
}