package edu.utap.myrecipe.shared.animation

import android.animation.Animator
import androidx.core.view.isInvisible
import com.airbnb.lottie.LottieAnimationView

class AnimationListener(private val animationView: LottieAnimationView, val onRepeat: () -> Unit) :
    Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {}

    override fun onAnimationCancel(animation: Animator?) {}

    override fun onAnimationRepeat(animation: Animator?) {
        animationView.pauseAnimation()
        animationView.isInvisible = true
        onRepeat()
    }
}