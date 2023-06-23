package edu.utap.myrecipe.shared.api

import android.util.Log
import edu.utap.myrecipe.shared.model.Recipe
import edu.utap.myrecipe.shared.model.RecipeInstructions
import edu.utap.myrecipe.shared.model.SaveRecipe
import edu.utap.myrecipe.shared.model.SavedRecipe
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MyRecipeApi {
    @GET("getRandomRecipes?number=10")
    suspend fun getRecipes(
        @Query("filters") filters: String,
        @Query("dairyFree") dairyFree: Boolean,
        @Query("glutenFree") glutenFree: Boolean
    ): List<Recipe>

    @GET("getRecipeInstructions")
    suspend fun getRecipeInstructions(@Query("id") id: String): RecipeInstructions

    @GET("getUserRecipes")
    suspend fun getUserRecipes(
        @Query("username") username: String
    ): List<SavedRecipe>

    @POST("saveRecipe")
    suspend fun saveRecipe(@Body recipe: SaveRecipe)

    companion object {
        private const val LOCAL_TESTING = false
        private var instance: MyRecipeApi? = null

        private const val httpURL =
            "https://us-central1-android-final-project-d0ec1.cloudfunctions.net/"
        private const val localURL = "http://10.0.2.2:5001/android-final-project-d0ec1/us-central1/"

        fun create(): MyRecipeApi {
            if (instance == null) instance = create(if (LOCAL_TESTING) localURL else httpURL)
            return instance!!
        }

        private fun create(httpUrl: String): MyRecipeApi {
            val client = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BASIC
            }).build()
            return Retrofit.Builder().baseUrl(httpUrl).client(client)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(MyRecipeApi::class.java)
        }

        suspend fun <T> tryAPICall(execute: suspend () -> T) = try {
            execute()
        } catch (e: Exception) {
            Log.e("", e.toString())
            null
        }
    }
}