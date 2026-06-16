package com.example.data.api

import com.example.BuildConfig
import com.example.model.Puzzle
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(val parts: List<Part>)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null,
    val thinkingConfig: ThinkingConfig? = null,
    val imageConfig: ImageConfig? = null,
    val responseModalities: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class ImageConfig(
    val aspectRatio: String? = null,
    val imageSize: String? = null
)

@JsonClass(generateAdapter = true)
data class ThinkingConfig(
    val thinkingLevel: String
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.1-pro-preview:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
    
    @POST("v1beta/models/gemini-3-pro-image-preview:generateContent")
    suspend fun generateImage(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiRepository {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val service = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(GeminiApiService::class.java)

    suspend fun generatePuzzle(levelNumber: Int): Puzzle? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Provide a mock puzzle if API key is not set
            return@withContext Puzzle(
                question = "API key not configured. What is $levelNumber + 1?",
                options = listOf("$levelNumber", "${levelNumber + 1}", "${levelNumber + 2}", "0"),
                correctAnswerIndex = 1,
                hints = listOf(
                    "Think about counting up by one.",
                    "If you have $levelNumber apples and get 1 more...",
                    "It's just the next number in sequence."
                ),
                explanation = "Simple addition."
            )
        }

        val prompt = """
            Generate a fun, educational math puzzle for a game (target audience: kids and teens).
            The current level is $levelNumber. As the level increases, the puzzle should get more difficult.
            Level 1-10: Basic arithmetic, counting.
            Level 11-30: Multiplication, pattern recognition, basic algebra.
            Level 31-50: Fractions, complex word problems, logic puzzles.
            
            Return ONLY a valid JSON object matching this schema:
            {
               "question": "The puzzle question text",
               "options": ["A", "B", "C", "D"],
               "correctAnswerIndex": 0,
               "hints": [
                   "A gentle first hint that nudges the player in the right direction without being too direct.",
                   "A second clue that offers more specific guidance regarding numbers or operations.",
                   "A final hint that practically reveals the path to the solution (almost giving it away)."
               ],
               "explanation": "Explanation of the correct answer shown after completion"
            }
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                // thinkingConfig = ThinkingConfig("high") // Wait, thinking config requires gemini-3.1-pro-preview but sometimes JSON schema + thinking has constraints. I'll omit structured schema and just ask for JSON text manually.
            )
        )

        try {
            val response = service.generateContent(apiKey, request)
            val jsonStr = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            // Clean up the markdown JSON block if present
            val cleanJson = jsonStr.replace("```json", "").replace("```", "").trim()
            val adapter = moshi.adapter(Puzzle::class.java)
            adapter.fromJson(cleanJson)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun generateImage(prompt: String, resolution: String): String? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext null
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                imageConfig = ImageConfig("1:1", resolution),
                responseModalities = listOf("IMAGE", "TEXT")
            )
        )

        try {
            val response = service.generateImage(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.inlineData?.data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
