package com.thuanpx.roomexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.thuanpx.roomexample.baseRecyclerView.SuperRecyclerView
import com.thuanpx.roomexample.model.City
import com.thuanpx.roomexample.room.CountryDao
import com.thuanpx.roomexample.room.CountryDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SuperRecyclerView.LoadDataListener {

    private lateinit var countryDao: CountryDao
    private var mainAdapter: MainAdapter? = null
    private val compositeDisposable = CompositeDisposable()
    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        countryDao = CountryDatabase.getInstance(this).countryDao()
        getDataCountry()
        initRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        mainAdapter = null
    }

    override fun onLoadMore(page: Int) {
        offset += Constant.LIMIT
        getDataCountry(isLoadMore = true)
    }

    override fun onRefreshData() {
        offset = 0
        getDataCountry(isRefreshData = true)
    }

    override fun onScroll() {
    }

    private fun getDataCountry(isRefreshData: Boolean = false, isLoadMore: Boolean = false) {
        compositeDisposable.add(
            countryDao.getAllCountry(offset)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (isRefreshData) {
                        rv.stopRefreshData()
                        rv.refreshAdapter()
                        mainAdapter?.replaceData(it.toMutableList())
                    } else {
                        if (isLoadMore) rv.stopLoadMore()
                        val cities = mutableListOf<City>()
                        run {
                            cities.addAll(mainAdapter?.getData()?.toMutableList() ?: return@run)
                        }
                        cities.addAll(it)
                        mainAdapter?.submitData(cities)
                    }
                    Log.i("Database ", it.toString())
                }, {
                    Log.i("Database error", it.message ?: "")
                })
        )
    }

    private fun initRecyclerView() {
        mainAdapter = MainAdapter()
        rv.apply {
            setAdapter(mainAdapter ?: return)
            setLayoutManager(LinearLayoutManager(this@MainActivity))
            setLoadDataListener(this@MainActivity)
        }
    }
}