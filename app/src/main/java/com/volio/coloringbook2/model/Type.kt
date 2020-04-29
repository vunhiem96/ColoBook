package com.volio.coloringbook2.model

import com.google.gson.annotations.Expose
import com.volio.coloringbook2.model.Image

data class Type(
    @Expose val cate_id: String,
    @Expose val image_type_url: String,
    @Expose val listImage: List<Image>,
    @Expose val name: String,
    @Expose val priority: String,
    @Expose val tag_type: String,
    @Expose  val type_id: String
)