package com.dicoding.myproyekakhirdua.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.myproyekakhirdua.R
import com.dicoding.myproyekakhirdua.data.response.DetailUserResponse
import com.dicoding.myproyekakhirdua.database.Favorite
import com.dicoding.myproyekakhirdua.databinding.ActivityDetailUserBinding
import com.dicoding.myproyekakhirdua.helper.MainFavoriteViewModel
import com.dicoding.myproyekakhirdua.helper.MainSettingViewModel
import com.dicoding.myproyekakhirdua.helper.SettingPreferences
import com.dicoding.myproyekakhirdua.helper.SettingViewModelFactory
import com.dicoding.myproyekakhirdua.helper.UserViewModel
import com.dicoding.myproyekakhirdua.helper.ViewModelFactory
import com.dicoding.myproyekakhirdua.helper.dataStore
import com.dicoding.myproyekakhirdua.ui.adapter.SectionsPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailUserActivity : AppCompatActivity() {

    private var favorite: Favorite? = null
    private var isFavorite: Boolean = false

    private var _binding: ActivityDetailUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainFavoriteViewModel: MainFavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityDetailUserBinding.inflate(layoutInflater) // Initialize ViewBinding
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_view)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pref = SettingPreferences.getInstance(application.dataStore)
        val mainSettingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(
            MainSettingViewModel::class.java
        )

        mainSettingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        var userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        mainFavoriteViewModel = obtainViewModel(this@DetailUserActivity)

        val userName = intent.getStringExtra(EXTRA_USER_NAME)
        var avatarUrl = intent.getStringExtra(EXTRA_AVATAR_URL)

        userName?.let {
            userViewModel.setUserName(it)
        }

        userName?.let {
            userViewModel.getDetailUser(it)
        }

        userViewModel.isError.observe(this, Observer { isError ->
            if (isError) {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                userViewModel.resetError()
            }
        })

        userViewModel.detailUserResponse.observe(this, Observer { detailUserResponse ->
            detailUserResponse?.let {
                this.updateUI(it)
            }
        })

        userViewModel.isLoading.observe(this, Observer { isLoading ->
            showLoading(isLoading)
        })

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f

        val fabFav = findViewById<FloatingActionButton>(R.id.fab_fav)

        mainFavoriteViewModel.getFavoriteUserByUsername(userName ?: "").observe(this, Observer { favorite ->
            favorite?.let {
                this.favorite = it
                isFavorite = true
                fabFav.setImageResource(R.drawable.baseline_favorite_24)
                mainSettingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean? ->
                    isDarkModeActive?.let {
                        val iconColor = if (it) R.color.white else R.color.black
                        fabFav.setColorFilter(ContextCompat.getColor(this, iconColor))
                    }
                }
            } ?: run {
                this.favorite = null
                isFavorite = false
                fabFav.setImageResource(R.drawable.baseline_favorite_border_24)
                mainSettingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean? ->
                    isDarkModeActive?.let {
                        val iconColor = if (it) R.color.white else R.color.black
                        fabFav.setColorFilter(ContextCompat.getColor(this, iconColor))
                    }
                }
            }
        })

        fabFav.setOnClickListener {
            if (favorite != null) {
                mainFavoriteViewModel.delete(favorite!!)
                showToast("Unfavorited")
            } else {
                favorite = Favorite()
                favorite?.apply {
                    username = userName.toString()
                    imageUrl = avatarUrl.toString()
                }
                mainFavoriteViewModel.insert(favorite as Favorite)
                showToast("Favorited")
            }
        }
    }

    private fun updateUI(detailUserResponse: DetailUserResponse) {
        binding.apply {
            tvDetailUsername.text = detailUserResponse.login
            tvDetailUserRealName.text = detailUserResponse.name ?: ""
            tvDetailUserFollowers.text = "Followers ${detailUserResponse.followers}"
            tvDetailUserFollowing.text = "Following ${detailUserResponse.following}"

            Glide.with(this@DetailUserActivity)
                .load(detailUserResponse.avatarUrl)
                .into(imgDetailUserPhoto)
        }
    }

    fun showLoading(isLoading: Boolean) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainFavoriteViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(MainFavoriteViewModel::class.java)
    }

    companion object {
        const val EXTRA_USER_NAME = "extra_user_name"
        const val EXTRA_AVATAR_URL = "extra_avatar_url"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
}