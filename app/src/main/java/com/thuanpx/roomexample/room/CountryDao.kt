package com.thuanpx.roomexample.room

import androidx.room.Dao
import androidx.room.Query
import com.thuanpx.roomexample.Constant
import com.thuanpx.roomexample.model.City
import io.reactivex.Flowable

/**
 * Copyright © 2020 Neolab VN.
 * Created by ThuanPx on 5/27/20.
 */

@Dao
interface CountryDao {

    @Query("SELECT * FROM cities ORDER BY city ASC LIMIT ${Constant.LIMIT} OFFSET :offset")
    fun getAllCountry(offset: Int): Flowable<List<City>>
}