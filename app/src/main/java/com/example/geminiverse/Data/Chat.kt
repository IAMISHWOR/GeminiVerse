package com.example.geminiverse.Data

import android.graphics.Bitmap

data class Chat(
    val prompt:String,
    val bitmap:Bitmap?,
    val isFormUser:Boolean
)