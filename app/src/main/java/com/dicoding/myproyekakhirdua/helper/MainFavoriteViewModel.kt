    package com.dicoding.myproyekakhirdua.helper

    import android.app.Application
    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.ViewModel
    import com.dicoding.myproyekakhirdua.database.Favorite
    import com.dicoding.myproyekakhirdua.database.FavoriteDao
    import com.dicoding.myproyekakhirdua.database.FavoriteRoomDatabase
    import com.dicoding.myproyekakhirdua.repository.FavoriteRepository

    class MainFavoriteViewModel(application: Application) : ViewModel() {

        private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)
//        private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

        private val favoriteDao: FavoriteDao
        private val allFavorites: LiveData<List<Favorite>>

        init {
            val database = FavoriteRoomDatabase.getDatabase(application)
            favoriteDao = database.favoriteDao()
            allFavorites = favoriteDao.getAllFavorites()
        }

        fun insert(favorite: Favorite) {
            mFavoriteRepository.insert(favorite)
        }

        fun delete(favorite: Favorite) {
            mFavoriteRepository.delete(favorite)
        }

        fun getFavoriteUserByUsername(username: String): LiveData<Favorite?> {
            return favoriteDao.getFavoriteUserByUsername(username)
        }
        fun getFavoriteUsers(): LiveData<List<Favorite>> {
            return mFavoriteRepository.getFavoriteUsers()
        }

        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean>
            get() = _isLoading
    }