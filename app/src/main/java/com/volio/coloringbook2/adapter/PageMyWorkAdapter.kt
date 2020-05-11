package com.volio.coloringbook2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.volio.coloringbook2.R
import com.volio.coloringbook2.database.config
import com.volio.coloringbook2.model.storybook.ImageStory
import com.volio.coloringbook2.model.storybook.saveLocal.ImageStorySave

class PageMyWorkAdapter(var context: Context,
                        var listStoryBook: List<ImageStorySave>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return listStoryBook.size
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val linearLayout = LayoutInflater.from(container.context).inflate(R.layout.layout_page_story, null) as LinearLayout
        val textView = linearLayout.findViewById<View>(R.id.pager_story) as TextView
        val img_page = linearLayout.findViewById<View>(R.id.img_page) as ImageView
        val storyBook = listStoryBook[position]

        Glide.with(context).load(storyBook.image_url).placeholder(R.drawable.ic_splash).into(img_page)
//        context.config.urlImage = url2
        textView.text = "Page ${position + 1}"
        container.addView(linearLayout)
        return linearLayout
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as LinearLayout?
        container.removeView(view)
    }


}