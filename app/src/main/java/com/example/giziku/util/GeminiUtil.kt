package com.example.giziku.util

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel

import android.util.Base64
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.ImagePart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

suspend fun modelCall(imageBitmap: Bitmap): String = withContext(Dispatchers.IO) {
    val model = GenerativeModel(
        modelName = "gemini-flash",
        apiKey = "AIzaSyCpJ61DKwKdbz2XfAGRuQLAGoQG1ykNR7k",
    )

    val answer = model.generateContent(
        prompt = imageBitmap
    ).text ?: "Tak ada respon"

    return@withContext answer
}
