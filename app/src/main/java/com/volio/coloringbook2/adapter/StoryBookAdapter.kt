package com.volio.coloringbook2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.volio.coloringbook2.R
import com.volio.coloringbook2.model.storybook.StoryBookItem

import kotlinx.android.synthetic.main.item_storybook.view.*
import java.util.*


class StoryBookAdapter(
    var context: Context,
    var listStoryBook: ArrayList<StoryBookItem>,
    private val listener:ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StoryBookViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_storybook, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listStoryBook.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val storyBook = listStoryBook[position]
        val url = "http://mycat.asia/volio_colorbook/"
        val url2 = "${url}${storyBook.book_image_url}"
        Glide.with(context).load(url2).placeholder(R.drawable.ic_splash).into(holder.itemView.img_list_story)
        holder.itemView.item_storybook.setOnClickListener {
            listener.onClick(position)
        }

    }


    interface ItemClickListener {
        fun onClick(pos: Int)
    }

    class StoryBookViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

}
