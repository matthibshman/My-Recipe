package edu.utap.myrecipe.features.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.myrecipe.databinding.FavoriteRecipeBinding
import edu.utap.myrecipe.shared.model.SavedRecipe
import edu.utap.myrecipe.shared.model.SavedRecipeDiff

class FavoriteRecipeAdapter(
    private val onClick: (recipe: SavedRecipe) -> Unit,
    private val onFavoriteClick: (recipe: SavedRecipe) -> Unit
) : ListAdapter<SavedRecipe, FavoriteRecipeAdapter.VH>(SavedRecipeDiff()) {
    inner class VH(private val favoriteRecipeBinding: FavoriteRecipeBinding) :
        RecyclerView.ViewHolder(favoriteRecipeBinding.root) {
        fun bindTo(recipe: SavedRecipe) {
            favoriteRecipeBinding.title.text = recipe.getWrappedTitle(32)
            favoriteRecipeBinding.favorite.isChecked = recipe.favorite
            favoriteRecipeBinding.title.setOnClickListener {
                onClick(recipe)
            }
            favoriteRecipeBinding.favorite.setOnClickListener {
                onFavoriteClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            FavoriteRecipeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindTo(getItem(position))
    }
}