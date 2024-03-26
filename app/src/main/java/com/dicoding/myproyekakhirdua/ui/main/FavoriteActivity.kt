package com.dicoding.myproyekakhirdua.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.myproyekakhirdua.R
import com.dicoding.myproyekakhirdua.data.response.ItemsItem
import com.dicoding.myproyekakhirdua.databinding.ActivityFavoriteBinding
import com.dicoding.myproyekakhirdua.helper.MainFavoriteViewModel
import com.dicoding.myproyekakhirdua.helper.ViewModelFactory
import com.dicoding.myproyekakhirdua.ui.adapter.UserListAdapter

class FavoriteActivity : AppCompatActivity() {

    private var _activityFavoriteBinding: ActivityFavoriteBinding? = null
    private val binding get() = _activityFavoriteBinding

    private lateinit var adapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _activityFavoriteBinding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = UserListAdapter()

        binding?.rvFavorites?.layoutManager = LinearLayoutManager(this)
        binding?.rvFavorites?.setHasFixedSize(true)
        binding?.rvFavorites?.adapter = adapter

        adapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                showLoading(true)
                val intent = Intent(this@FavoriteActivity, DetailUserActivity::class.java).apply {
                    putExtra(DetailUserActivity.EXTRA_USER_NAME, data.login)
                    putExtra(DetailUserActivity.EXTRA_AVATAR_URL, data.avatarUrl)
                }
                startActivity(intent)
                showLoading(false)
            }
        })

        val viewModel = obtainViewModel(this@FavoriteActivity)
        viewModel.getFavoriteUsers().observe(this) { users ->
            val items = arrayListOf<ItemsItem>()
            users.map {
                val item = ItemsItem(login = it.username, avatarUrl = it.imageUrl)
                items.add(item)
            }
            adapter.submitList(items)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    } 

    private fun obtainViewModel(activity: AppCompatActivity): MainFavoriteViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[MainFavoriteViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityFavoriteBinding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}