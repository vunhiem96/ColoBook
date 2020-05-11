package com.volio.coloringbook2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.volio.coloringbook2.R
import com.volio.coloringbook2.database.CalendarDatabase
import com.volio.coloringbook2.database.SaveStoryDao
import com.volio.coloringbook2.java.PhotorThread
import com.volio.coloringbook2.model.storybook.saveLocal.StoryBookSave
import kotlinx.android.synthetic.main.item_storybook.view.img_list_story_save
import kotlinx.android.synthetic.main.item_storybook.view.item_storybook
import kotlinx.android.synthetic.main.item_storybook_save.view.*
import java.util.*

class StorySaveAdapter(
    var context: Context,
    var listStoryBook: ArrayList<StoryBookSave>,
    private val listener: ItemClickListener,
    private val listener2: ItemClickListener2
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var storyBookdao: SaveStoryDao? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StorySaveAdapter.SaveBookViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_storybook_save, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listStoryBook.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        storyBookdao = CalendarDatabase.invoke(context).saveStoryDao()
        val storyBook = listStoryBook[position]
        val url = "http://mycat.asia/volio_colorbook/"
        val url2 = "${url}${storyBook.book_image_url}"
        Glide.with(context).load(url2).placeholder(R.drawable.ic_splash).into(holder.itemView.img_list_story_save)
        holder.itemView.date_story.text = "Date: ${listStoryBook[position].date}"
        holder.itemView.time_story.text = "Date: ${listStoryBook[position].time}"

        val list = listStoryBook[position].list
        var count = 0
        for (i in 0..list.size - 1) {
            if (list[i].saveLocal == true) {
                count = count + 1
            }
        }
        holder.itemView.tv_finish_story.text = "$count/${list.size} pages"


        holder.itemView.item_storybook.setOnClickListener {
            listener.onClick(position)
        }

        holder.itemView.delete_story.setOnClickListener {
           listener2.onClick(position)
            DeleteList(listStoryBook[position].book_id)

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
    interface ItemClickListener2 {
        fun onClick(pos: Int)
    }

    class SaveBookViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    fun DeleteList(id: String) {

        PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
            override fun doingBackground() {
                storyBookdao!!.deleteById(id)
            }

            override fun onCompleted() {
//                imageModel.add(imageModel1!!)
//                imageModel2 = imageModel

            }

            override fun onCancel() {
            }

        })

    }
}