package edu.utap.myrecipe.features.saved

import androidx.lifecycle.*
import edu.utap.myrecipe.shared.model.SavedRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedViewModel : ViewModel() {

    private val savedRepo = SavedRepo()

    private var savedRecipes = MutableLiveData<List<SavedRecipe>>()
    private var isFetching = MutableLiveData(false)
    private var filteredRecipes = MediatorLiveData<List<SavedRecipe>>().apply {
        addSource(savedRecipes) {
            this.postValue(sortList(filterList(it)))
        }
    }

    private var filter = "all"
    private var sortAscending = false

    fun getRecipes() = viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
        savedRepo.getUser().let {
            if (it != null) {
                isFetching.postValue(true)
                savedRecipes.postValue(savedRepo.getSavedRecipes(it.uid))
                isFetching.postValue(false)
            }
        }
    }

    fun sort(ascending: Boolean) {
        sortAscending = ascending
        filteredRecipes.value = sortList(filteredRecipes.value!!)
    }

    fun filter(cooked: String) {
        filter = cooked
        filteredRecipes.value = sortList(filterList(savedRecipes.value!!))
    }

    fun observeRecipes(): LiveData<List<SavedRecipe>> = filteredRecipes
    fun observeIsFetching() = isFetching

    fun getFilter() = filter
    fun isSortAscending() = sortAscending

    private fun sortList(list: List<SavedRecipe>) = if (sortAscending) {
        list.sortedBy { it.dateAdded }
    } else {
        list.sortedByDescending { it.dateAdded }
    }

    private fun filterList(list: List<SavedRecipe>) = when (filter) {
        "cooked" -> list.filter { it.cooked }
        "uncooked" -> list.filter { !it.cooked }
        else -> list
    }
}