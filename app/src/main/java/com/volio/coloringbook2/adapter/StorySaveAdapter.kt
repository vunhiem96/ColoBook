package com.volio.coloringbook2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.volio.coloringbook2.R
import com.volio.coloringbook2.model.storybook.StoryBookItem
import com.volio.coloringbook2.model.storybook.saveLocal.StoryBookSave
import kotlinx.android.synthetic.main.item_storybook.view.*
import kotlinx.android.synthetic.main.item_storybook.view.img_list_story_save
import kotlinx.android.synthetic.main.item_storybook.view.img_type_story
import kotlinx.android.synthetic.main.item_storybook.view.item_storybook
import kotlinx.android.synthetic.main.item_storybook_save.view.*
import java.util.*

class StorySaveAdapter(
    var context: Context,
    var listStoryBook: ArrayList<StoryBookSave>,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StorySaveAdapter.SaveBookViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_storybook_save, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listStoryBook.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val storyBook = listStoryBook[position]
        val url = "http://mycat.asia/volio_colorbook/"
        val url2 = "${url}${storyBook.book_image_url}"
        Glide.with(context).load(url2).placeholder(R.drawable.ic_splash).into(holder.itemView.img_list_story_save)

        holder.itemView.item_storybook.setOnClickListener {
            listener.onClick(position)
        }


        val size = storyBook.list.size
        holder.itemView.name_storybook_save.text = storyBook.book_name
        val type = storyBook.is_pro
        if (type == "1") {
            holder.itemView.img_type_story_save.visibility = View.VISIBLE
        }
    }


    interface ItemClickListener {
        fun onClick(pos: Int)
    }

    class SaveBookViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}