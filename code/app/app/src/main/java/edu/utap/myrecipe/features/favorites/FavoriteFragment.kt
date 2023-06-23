package edu.utap.myrecipe.features.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import edu.utap.myrecipe.R
import edu.utap.myrecipe.databinding.FragmentFavoriteBinding
import edu.utap.myrecipe.features.recipe.RecipeFragment
import edu.utap.myrecipe.features.recipe.RecipeViewModel
import edu.utap.myrecipe.shared.navigation.NavigationUtil

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {
    companion object {
        fun newInstance(): FavoriteFragment {
            return FavoriteFragment()
        }
    }

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by activityViewModels()
    private val recipeViewModel: RecipeViewModel by activityViewModels()
    private lateinit var adapter: FavoriteRecipeAdapter

    private fun initAdapter(binding: FragmentFavoriteBinding) {
        adapter = FavoriteRecipeAdapter({
            recipeViewModel.setRecipe(it)
            NavigationUtil.getInstance()
                ?.navigate(RecipeFragment.newInstance(), "recipeFragTag", this, "favoriteFragTag")
        }, {
            viewModel.removeFavorite(it)
        })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val divider = MaterialDividerItemDecoration(
            requireContext(), LinearLayoutManager.VERTICAL
        )
        binding.recyclerView.addItemDecoration(divider)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)
        initAdapter(binding)
        viewModel.observeRecipes().observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.observeIsFetching().observe(viewLifecycleOwner) {
            if (it) binding.loading.show()
            else binding.loading.visibility = View.GONE
        }

        binding.loading.hide()
        viewModel.getRecipes()
    }
}