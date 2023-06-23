package edu.utap.myrecipe.shared.model

import androidx.recyclerview.widget.DiffUtil
import java.util.*

data class SavedRecipe(
    val id: String,
    val image: String,
    val title: String,
    val cooked: Boolean,
    val favorite: Boolean,
    val dateAdded: Date
) {
    fun getWrappedTitle(charLimit: Int = 40): String {
        var charsSeen = 0
        return title.fold("") { acc, curr ->
            charsSeen += 1
            if (charsSeen > charLimit && curr.isWhitespace()) {
                charsSeen = 0
                "$acc$curr\n"
            } else "$acc$curr"
        }
    }
}

class SavedRecipeDiff : DiffUtil.ItemCallback<SavedRecipe>() {
    override fun areItemsTheSame(oldItem: SavedRecipe, newItem: SavedRecipe): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SavedRecipe, newItem: SavedRecipe): Boolean =
        oldItem.cooked == newItem.cooked && oldItem.favorite == newItem.favorite
}