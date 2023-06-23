package edu.utap.myrecipe.features.discover

import android.os.Bundle
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.chip.Chip
import edu.utap.myrecipe.R
import edu.utap.myrecipe.databinding.FragmentDiscoverBinding
import edu.utap.myrecipe.shared.animation.AnimationListener
import edu.utap.myrecipe.shared.gesture.GestureListener
import edu.utap.myrecipe.shared.glide.Glide


class DiscoverFragment : Fragment(R.layout.fragment_discover) {
    companion object {
        fun newInstance(): DiscoverFragment {
            return DiscoverFragment()
        }
    }

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiscoverViewModel by activityViewModels()

    private lateinit var filters: List<Chip>
    private lateinit var mDetector: GestureDetectorCompat
    private lateinit var xAnimation: LottieAnimationView
    private lateinit var checkAnimation: LottieAnimationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDiscoverBinding.bind(view)
        setupFilters()
        setupAnimations()
        setupSwiping()

        viewModel.observeCurrentRecipe().observe(viewLifecycleOwner) {
            binding.title.text = it.title
            if (it == null || it.image == null || it.image.isBlank()) viewModel.nextRecipe()
            else Glide.glideFetch(it.image, binding.picture)
        }
        viewModel.observeIsFetching().observe(viewLifecycleOwner) {
            if (it) binding.loading.show()
            else binding.loading.hide()
        }

        binding.loading.hide()
        viewModel.getRecipes()
    }

    private fun setupFilters() {
        filters = listOf(
            binding.vegetarian, binding.vegan, binding.keto
        )

        filters.forEach {
            it.isChecked = viewModel.getFilters().contains(it.text.toString().lowercase())
        }
        binding.dairy.isChecked = viewModel.isDairyFree()
        binding.gluten.isChecked = viewModel.isGlutenFree()

        listOf(
            binding.vegetarian, binding.vegan, binding.keto, binding.dairy, binding.gluten
        ).forEach { chip ->
            chip.setOnClickListener {
                val filter = filters.filter { it.isChecked }.fold("") { acc, curr ->
                    acc + "${curr.text.toString().lowercase()},"
                }

                viewModel.setFilters(filter, binding.dairy.isChecked, binding.gluten.isChecked)
            }
        }
    }

    private fun setupAnimations() {
        xAnimation = binding.xAnimation
        checkAnimation = binding.checkAnimation
        xAnimation.repeatCount = LottieDrawable.INFINITE
        checkAnimation.repeatCount = LottieDrawable.INFINITE
        xAnimation.addAnimatorListener(AnimationListener(xAnimation) { viewModel.nextRecipe() })
        checkAnimation.addAnimatorListener(AnimationListener(checkAnimation) { viewModel.saveRecipe() })
    }

    private fun setupSwiping() {
        mDetector = GestureDetectorCompat(requireContext(), GestureListener(
            onSwipeRight = {
                checkAnimation.isVisible = true
                checkAnimation.playAnimation()
            },
            onSwipeLeft = {
                xAnimation.isVisible = true
                xAnimation.playAnimation()
            })
        )
        binding.picture.setOnTouchListener { _, event ->
            mDetector.onTouchEvent(event)
        }
    }
}