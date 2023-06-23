package edu.utap.myrecipe.features.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.myrecipe.databinding.SavedRecipeBinding
import edu.utap.myrecipe.shared.glide.Glide
import edu.utap.myrecipe.shared.model.SavedRecipe
import edu.utap.myrecipe.shared.model.SavedRecipeDiff

class SavedRecipeAdapter(private val onClick: (recipe: SavedRecipe) -> Unit) :
    ListAdapter<SavedRecipe, SavedRecipeAdapter.VH>(SavedRecipeDiff()) {
    inner class VH(private val rowPostBinding: SavedRecipeBinding) :
        RecyclerView.ViewHolder(rowPostBinding.root) {
        fun bindTo(recipe: SavedRecipe) {
            Glide.glideFetch(recipe.image, rowPostBinding.image)
            rowPostBinding.title.text = recipe.getWrappedTitle()
            rowPostBinding.title.setOnClickListener {
                onClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            SavedRecipeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindTo(getItem(position))
    }
}