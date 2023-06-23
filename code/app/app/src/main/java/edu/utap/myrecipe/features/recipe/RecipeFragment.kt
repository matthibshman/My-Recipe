package edu.utap.myrecipe.features.recipe

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.utap.myrecipe.R
import edu.utap.myrecipe.databinding.FragmentRecipeBinding
import edu.utap.myrecipe.shared.glide.Glide
import edu.utap.myrecipe.shared.navigation.NavigationUtil

class RecipeFragment : Fragment(R.layout.fragment_recipe) {
    companion object {
        fun newInstance(): RecipeFragment {
            return RecipeFragment()
        }
    }

    private val EMPTY_CONTENT_MESSAGE = "Not available"

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRecipeBinding.bind(view)
        setupDisplay()

        viewModel.observeIsFetching().observe(viewLifecycleOwner) {
            if (it) binding.loading.show()
            else binding.loading.visibility = View.GONE
        }

        viewModel.observeRecipe().observe(viewLifecycleOwner) {
            binding.title.text = it.title
            if (it == null || it.image.isBlank()) Log.d("RecipeFragment", it.image)
            else Glide.glideFetch(it.image, binding.image)
            binding.cooked.isChecked = it.cooked
            binding.favorite.isChecked = it.favorite
        }

        viewModel.observeRecipeInstructions().observe(viewLifecycleOwner) {
            binding.equipmentList.text = if (it.equipment.isEmpty()) EMPTY_CONTENT_MESSAGE
            else it.equipment.fold("") { acc, curr -> "$acc● $curr\n" }
            binding.ingredientList.text = if (it.ingredients.isEmpty()) EMPTY_CONTENT_MESSAGE
            else it.ingredients.fold("") { acc, curr -> "$acc● $curr\n" }
            binding.stepsList.text = if (it.steps.isEmpty()) EMPTY_CONTENT_MESSAGE
            else it.steps.fold("") { acc, curr -> "$acc${curr.number}: ${curr.instruction}\n" }
        }
    }

    private fun setupDisplay() {
        binding.loading.hide()
        binding.equipmentList.text = ""
        binding.ingredientList.text = ""
        binding.stepsList.text = ""

        binding.back.setOnClickListener {
            NavigationUtil.getInstance()?.previous()
        }

        binding.favorite.setOnClickListener {
            viewModel.favorite()
        }

        binding.cooked.setOnClickListener {
            viewModel.cooked()
        }
    }
}