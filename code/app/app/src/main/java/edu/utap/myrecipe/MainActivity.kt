package edu.utap.myrecipe

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.utap.myrecipe.features.discover.DiscoverFragment
import edu.utap.myrecipe.features.favorites.FavoriteFragment
import edu.utap.myrecipe.features.login.AuthInit
import edu.utap.myrecipe.features.login.LoginViewModel
import edu.utap.myrecipe.features.saved.SavedFragment
import edu.utap.myrecipe.shared.navigation.NavigationUtil

class MainActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var navigationUtil: NavigationUtil

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.updateUser()
        } else {
            // Sign in failed
            Log.d("MainActivity", "sign in failed ${result}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationUtil = NavigationUtil.getInstance(supportFragmentManager)!!

        navigationUtil.navigate(DiscoverFragment.newInstance(), "discoverFragTag")

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.discover -> {
                    navigationUtil.navigate(DiscoverFragment.newInstance(), "discoverFragTag")
                    true
                }
                R.id.saved -> {
                    navigationUtil.navigate(SavedFragment.newInstance(), "savedFragTag")
                    true
                }
                R.id.favorites -> {
                    navigationUtil.navigate(FavoriteFragment.newInstance(), "favoriteFragTag")
                    true
                }
                else -> false
            }
        }

        AuthInit(viewModel, signInLauncher)
    }
}