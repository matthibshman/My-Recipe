package edu.utap.myrecipe.features.saved

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.myrecipe.R
import edu.utap.myrecipe.databinding.FragmentSavedBinding
import edu.utap.myrecipe.features.recipe.RecipeFragment
import edu.utap.myrecipe.features.recipe.RecipeViewModel
import edu.utap.myrecipe.shared.navigation.NavigationUtil

class SavedFragment : Fragment(R.layout.fragment_saved) {
    companion object {
        fun newInstance(): SavedFragment {
            return SavedFragment()
        }
    }

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SavedViewModel by activityViewModels()
    private val recipeViewModel: RecipeViewModel by activityViewModels()
    private lateinit var adapter: SavedRecipeAdapter

    private fun initAdapter(binding: FragmentSavedBinding) {
        adapter = SavedRecipeAdapter {
            recipeViewModel.setRecipe(it)
            NavigationUtil.getInstance()
                ?.navigate(RecipeFragment.newInstance(), "recipeFragTag", this, "savedFragTag")
        }
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSavedBinding.bind(view)
        initAdapter(binding)
        setupFilters()

        viewModel.observeRecipes().observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
            // scroll to top when data changed
            binding.recyclerView.post {
                binding.recyclerView.layoutManager?.scrollToPosition(0)
            }
        }

        viewModel.observeIsFetching().observe(viewLifecycleOwner) {
            if (it) binding.loading.show()
            else binding.loading.visibility = View.GONE
        }

        binding.loading.hide()
        viewModel.getRecipes()
    }

    private fun setupFilters() {
        binding.sortBy.check(
            if (viewModel.isSortAscending()) binding.oldest.id
            else binding.newest.id
        )
        binding.filter.check(
            when (viewModel.getFilter()) {
                "cooked" -> binding.cooked.id
                "uncooked" -> binding.uncooked.id
                else -> binding.all.id
            }
        )

        binding.newest.setOnClickListener {
            viewModel.sort(false)
        }
        binding.oldest.setOnClickListener {
            viewModel.sort(true)
        }
        binding.all.setOnClickListener {
            viewModel.filter("all")
        }
        binding.cooked.setOnClickListener {
            viewModel.filter("cooked")
        }
        binding.uncooked.setOnClickListener {
            viewModel.filter("uncooked")
        }
    }
}