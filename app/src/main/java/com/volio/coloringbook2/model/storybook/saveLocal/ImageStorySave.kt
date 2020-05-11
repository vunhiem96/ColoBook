package com.volio.coloringbook2.model.storybook.saveLocal

data class ImageStorySave(
    val book_id: String,
    val image_id: String,
    val image_url: String,
    val priority: String,
    val thumbnail_url: String,
    val saveLocal:Boolean
)