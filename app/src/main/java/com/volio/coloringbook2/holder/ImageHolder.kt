package com.volio.coloringbook2.holder


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.volio.coloringbook2.R

class ImageHolder(itemView: View) : ViewHolder(itemView) {
    val img: ImageView = itemView.findViewById(R.id.img_list)
    val imgType: ImageView = itemView.findViewById(R.id.img_type_image)
    val page: TextView = itemView.findViewById(R.id.tv_page)

}
