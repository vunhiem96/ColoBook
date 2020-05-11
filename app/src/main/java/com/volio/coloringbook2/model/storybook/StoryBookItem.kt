package com.volio.coloringbook2.model.storybook



data class StoryBookItem(
    val book_id: String,
    val book_image_url: String,
    val book_name: String,
    val is_pro: String,
    val list: List<ImageStory>,
    val priority: String
)