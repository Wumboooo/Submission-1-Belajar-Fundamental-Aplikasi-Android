package com.dicoding.myproyekakhirdua.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "Favorite")
@Parcelize
data class Favorite (
    @PrimaryKey(autoGenerate = false)
    var username: String = "",
    var imageUrl: String? = null,
):Parcelable