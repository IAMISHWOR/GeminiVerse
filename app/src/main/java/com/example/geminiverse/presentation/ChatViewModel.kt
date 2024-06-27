package com.example.geminiverse.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiverse.Data.Chat
import com.example.geminiverse.Data.ChatData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    fun onEvent(event:ChatUiEvent){
        when(event){
            is ChatUiEvent.sendPompt -> {
                if (event.prompt.isNotBlank()) {
                    addPrompt(event.prompt, event.bitmap)
                    if (event.bitmap != null) {
                        getResponsesWithBitmap(event.prompt, event.bitmap)
                    } else {
                        getResponses(event.prompt)
                    }
                }
            }
            is ChatUiEvent.updatePompt -> {
                _chatState.update {
                    it.copy(
                        prompt = event.newPrompt
                    )
                }
            }

        }

    }
    private fun addPrompt(prompt: String, bitmap: Bitmap?) {
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, Chat(prompt, bitmap, true))
                },
                prompt = "",
                bitmap = null
            )
        }
    }
     private fun getResponses(prompt:String){
         viewModelScope.launch {
             val chat = ChatData.getResponse(prompt)
             _chatState.update {
                 it.copy(
                     chatList = it.chatList.toMutableList().apply {
                         add(0,chat)
                     },
                 )
             }
         }

    }
   private fun getResponsesWithBitmap(prompt:String,bitmap: Bitmap){
        viewModelScope.launch {
            val chat = ChatData.getResponseWithBitmap(prompt,bitmap)
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0,chat)
                    },
                )
            }
        }

    }
}