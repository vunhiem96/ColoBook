package com.volio.coloringbook2.model.storybook

data class ImageStory (
    val book_id: String,
    val image_id: String,
    val image_url: String,
    val priority: String,
    val thumbnail_url: String
)