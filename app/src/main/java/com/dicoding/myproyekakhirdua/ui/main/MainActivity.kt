package com.dicoding.myproyekakhirdua.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.myproyekakhirdua.R
import com.dicoding.myproyekakhirdua.data.response.ItemsItem
import com.dicoding.myproyekakhirdua.databinding.ActivityMainBinding
import com.dicoding.myproyekakhirdua.helper.MainSettingViewModel
import com.dicoding.myproyekakhirdua.helper.SettingPreferences
import com.dicoding.myproyekakhirdua.helper.SettingViewModelFactory
import com.dicoding.myproyekakhirdua.helper.UserViewModel
import com.dicoding.myproyekakhirdua.helper.dataStore
import com.dicoding.myproyekakhirdua.ui.adapter.UserListAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pref = SettingPreferences.getInstance(application.dataStore)
        val mainSettingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(
            MainSettingViewModel::class.java
        )

        mainSettingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->

            val iconColor = if (isDarkModeActive) R.color.white else R.color.black
            binding.favorite.setColorFilter(ContextCompat.getColor(this, iconColor))
            binding.setting.setColorFilter(ContextCompat.getColor(this, iconColor))

            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        userViewModel.userList.observe(this, Observer { userList ->
            userList?.let { setUserData(it) }
        })

        userViewModel.isLoading.observe(this, Observer { isLoading ->
            showLoading(isLoading)
        })

        userViewModel.findUsers(this@MainActivity, "Arif")

        val layoutManager = LinearLayoutManager(this)
        binding.rvUsers.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUsers.addItemDecoration(itemDecoration)

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                val query = searchView.text?.toString() ?: ""
                searchBar.setText(query)
                searchView.hide()
                userViewModel.findUsers(this@MainActivity, query)
                true
            }
        }

        userViewModel.isError.observe(this, Observer { isError ->
            if (isError) {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                userViewModel.resetError()
            }
        })

        binding.favorite.setOnClickListener {
            val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
            startActivity(intent)
        }

        binding.setting.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setUserData(userList: List<ItemsItem?>?) {

        val adapter = UserListAdapter()
        adapter.submitList(userList)
        binding.rvUsers.adapter = adapter

        adapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                showLoading(true)
                val intent = Intent(this@MainActivity, DetailUserActivity::class.java).apply {
                    putExtra(DetailUserActivity.EXTRA_USER_NAME, data.login)
                    putExtra(DetailUserActivity.EXTRA_AVATAR_URL, data.avatarUrl)
                }
                startActivity(intent)
                showLoading(false)
            }
        })
    }

    fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
