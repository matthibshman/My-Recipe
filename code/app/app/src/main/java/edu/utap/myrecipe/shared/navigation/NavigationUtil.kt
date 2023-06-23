package edu.utap.myrecipe.shared.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import edu.utap.myrecipe.R

class NavigationUtil private constructor(private val fragmentManager: FragmentManager) {
    companion object {
        private var instance: NavigationUtil? = null

        fun getInstance(
            fragmentManager: FragmentManager,
        ): NavigationUtil? {
            if (instance == null) instance = NavigationUtil(fragmentManager)
            return instance
        }

        fun getInstance() = instance
    }

    private var previousFragment: Fragment? = null
    private var previousTag = ""

    fun navigate(fragment: Fragment, tag: String) {
        fragmentManager.commit {
            replace(R.id.mainFragment, fragment, tag)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }

    fun navigate(fragment: Fragment, tag: String, prevFragment: Fragment, prevTag: String) {
        previousFragment = prevFragment
        previousTag = prevTag
        navigate(fragment, tag)
    }

    fun previous() {
        previousFragment?.let {
            fragmentManager.commit {
                replace(R.id.mainFragment, it, previousTag)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }
        }
    }
}