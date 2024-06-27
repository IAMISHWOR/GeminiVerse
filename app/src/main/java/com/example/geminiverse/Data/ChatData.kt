package com.example.geminiverse.Data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

object ChatData{
    val apikey = "AIzaSyBc3vsVFrK0Hvk5mlrENd_4C-cDYrRWzlU"
    suspend fun getResponse(prompt:String):Chat{
        val generativeModel = GenerativeModel("gemini-pro", apikey)
        try {
            val response = generativeModel.generateContent(prompt)

            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFormUser = false
            )
        }catch (e:Exception){
            return Chat(
                prompt = e.message ?: ("please give us more information about your query.kindly" +
                        " requested from the ik brand"),
                bitmap = null,
                isFormUser = false
            )
        }
    }
    suspend fun getResponseWithBitmap(prompt:String,bitmap: Bitmap):Chat{
        val generativeModel = GenerativeModel("gemini-pro-vision", apikey)
        try {
            val inputContent = content {
                text(prompt)
                image(bitmap)
            }
            val response = generativeModel.generateContent(inputContent)

            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFormUser = false
            )
        }catch (e:Exception){
            return Chat(
                prompt = e.message ?: ("please give us more information about your query.kindly" +
                        " requested from the ik brand"),
                bitmap = null,
                isFormUser = false
            )
        }
    }
}