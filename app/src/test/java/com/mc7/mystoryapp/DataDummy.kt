package com.mc7.mystoryapp

import com.mc7.mystoryapp.data.remote.response.StoryItem

object DataDummy {
    fun generateDummyStoryItem(): List<StoryItem> {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryItem(
                "",
                "",
                "",
                "",
                0.0,
                i.toString(),
                0.0
            )
            items.add(quote)
        }
        return items
    }
}