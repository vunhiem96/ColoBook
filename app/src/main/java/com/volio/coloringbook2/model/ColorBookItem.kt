package com.volio.coloringbook2.model

import com.google.gson.annotations.Expose

data class ColorBookItem(
    @Expose var cate_id: String,
    @Expose val cate_name: String,
    @Expose val listType: List<Type>,
    @Expose val priority: String
)