package com.example.geminiverse.presentation

import android.graphics.Bitmap
import com.example.geminiverse.Data.Chat

data class ChatState (
    val chatList : MutableList<Chat> = mutableListOf(),
    val prompt:String = "",
    val bitmap: Bitmap? = null
)