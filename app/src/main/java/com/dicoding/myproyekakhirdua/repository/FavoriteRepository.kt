package com.dicoding.myproyekakhirdua.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.myproyekakhirdua.database.Favorite
import com.dicoding.myproyekakhirdua.database.FavoriteDao
import com.dicoding.myproyekakhirdua.database.FavoriteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository(application: Application) {
    private val mFavoritesDao: FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        mFavoritesDao = db.favoriteDao()
    }

    fun getAllFavorites(): LiveData<List<Favorite>> = mFavoritesDao.getAllFavorites()

    fun insert(favorite: Favorite) {
        executorService.execute { mFavoritesDao.insert(favorite) }
    }

    fun delete(favorite: Favorite) {
        executorService.execute { mFavoritesDao.delete(favorite) }
    }

    fun getFavoriteUsers(): LiveData<List<Favorite>> {
        return mFavoritesDao.getFavoriteUsers()
    }
}