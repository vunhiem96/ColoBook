package com.volio.coloringbook2.model

import com.google.gson.annotations.Expose

data class Image(
    @Expose val image_id: String,
    @Expose val image_url: String,
    @Expose val tag_image: String,
    @Expose val thumbnail_url: String,
    @Expose val type_id: String
)