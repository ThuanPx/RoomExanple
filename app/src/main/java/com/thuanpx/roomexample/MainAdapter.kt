package com.thuanpx.roomexample

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuanpx.ktext.recyclerView.inflate
import com.thuanpx.roomexample.baseRecyclerView.BaseLoadMoreAdapter
import com.thuanpx.roomexample.baseRecyclerView.SuperDiffUtil
import com.thuanpx.roomexample.model.City
import kotlinx.android.synthetic.main.item_main.view.*

/**
 * Copyright © 2020 Neolab VN.
 * Created by ThuanPx on 5/27/20.
 */
class MainAdapter : BaseLoadMoreAdapter<City>() {

    override fun onCreateViewHolderLM(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_main)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolderLM(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.onBind(getItem(position), position)
    }

    override fun getItemViewTypeLM(position: Int): Int {
        return 0
    }

    override fun getDiffUtil(): SuperDiffUtil<City> = super.getDiffUtil().apply {
        areItemsTheSame { old, new ->
            old?.id == new?.id
        }
    }

    companion object {
        class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            @SuppressLint("SetTextI18n")
            fun onBind(city: City?, position: Int) {
                with(itemView) {
                    tvCity.text = "${city?.city} $position ${city?.id}"
                }
            }
        }
    }
}