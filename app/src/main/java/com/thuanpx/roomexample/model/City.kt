package com.thuanpx.roomexample.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Copyright © 2020 Neolab VN.
 * Created by ThuanPx on 5/27/20.
 */

@Entity(tableName = "cities")
data class City(
    @PrimaryKey val id: String,
    val country: String?,
    val city: String?,
    val population: Int?
)