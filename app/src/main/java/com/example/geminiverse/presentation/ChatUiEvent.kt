package com.example.geminiverse.presentation

import android.graphics.Bitmap

sealed interface ChatUiEvent {
    data class updatePompt(val newPrompt:String):ChatUiEvent
    data class sendPompt(
        val prompt:String,
        val bitmap: Bitmap?
    ) : ChatUiEvent
}