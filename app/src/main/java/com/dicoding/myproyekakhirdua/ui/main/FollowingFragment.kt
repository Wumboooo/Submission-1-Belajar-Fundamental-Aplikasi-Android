package com.dicoding.myproyekakhirdua.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.myproyekakhirdua.data.response.ItemsItem
import com.dicoding.myproyekakhirdua.data.retrofit.ApiConfig
import com.dicoding.myproyekakhirdua.databinding.FragmentFollowingBinding
import com.dicoding.myproyekakhirdua.helper.UserViewModel
import com.dicoding.myproyekakhirdua.ui.adapter.UserListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowingFragment : Fragment() {

    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFollowing.layoutManager = LinearLayoutManager(requireContext())

        userViewModel.userName.observe(viewLifecycleOwner, Observer { userName ->
            getFollowingData(userName)
        })
    }

    private fun getFollowingData(username: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().getFollowing(username)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val followingList = response.body()
                    setUserFollowingData(followingList)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat...", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                showLoading(false)
                Toast.makeText(requireContext(), "Gagal memuat...", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setUserFollowingData(userList: List<ItemsItem>?) {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvFollowing.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvFollowing.addItemDecoration(itemDecoration)

        val adapter = UserListAdapter()
        adapter.submitList(userList)
        binding.rvFollowing.adapter = adapter

        adapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                showLoading(true)
                val intent = Intent(requireContext(), DetailUserActivity::class.java).apply {
                    putExtra(DetailUserActivity.EXTRA_USER_NAME, data.login)
                    putExtra(DetailUserActivity.EXTRA_AVATAR_URL, data.avatarUrl)
                }
                startActivity(intent)
                showLoading(false)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}