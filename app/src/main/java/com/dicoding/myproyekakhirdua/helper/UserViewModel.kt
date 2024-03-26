package com.dicoding.myproyekakhirdua.helper
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.myproyekakhirdua.data.response.DetailUserResponse
import com.dicoding.myproyekakhirdua.data.response.GithubResponse
import com.dicoding.myproyekakhirdua.data.response.ItemsItem
import com.dicoding.myproyekakhirdua.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    fun setUserName(userName: String) {
        _userName.value = userName
    }

    private val _userList = MutableLiveData<List<ItemsItem>>()
    val userList: LiveData<List<ItemsItem>>
        get() = _userList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean>
        get() = _isError

    fun resetError() {
        _isError.value = false
    }

    private val _detailUserResponse = MutableLiveData<DetailUserResponse>()
    val detailUserResponse: LiveData<DetailUserResponse>
        get() = _detailUserResponse

    fun findUsers(context: Context, query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchListUsers(query, 1000)
        client.enqueue(object : Callback<GithubResponse> {
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _userList.value = (responseBody?.items ?: emptyList()) as List<ItemsItem>?
                    }
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    fun getDetailUser(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchDetailUser(username)
        client.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val detailUserResponse = response.body()
                    if (detailUserResponse != null) {
                        _detailUserResponse.value = detailUserResponse
                    }
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }
}