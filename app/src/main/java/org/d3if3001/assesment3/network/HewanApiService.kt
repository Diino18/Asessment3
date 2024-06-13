package org.d3if3001.assesment3.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.d3if3001.assesment3.model.Hewan
import org.d3if3001.assesment3.model.OpStatus
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

private const val BASE_URL  = "https://unspoken.my.id/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit
    .Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()
interface HewanApiService {
    @GET("sepatu.php")
    suspend fun getHewan(
        @Header("Authorization") userId: String
    ):List<Hewan>

    @Multipart
    @POST("sepatu.php")
    suspend fun postHewan(
        @Header("Authorization") userId: String,
        @Part("namaSepatu") namaSepatu: RequestBody,
        @Part("ukuranSepatu") ukuranSepatu: RequestBody,
        @Part imageId: MultipartBody.Part
    ): OpStatus
    @DELETE("sepatu.php")
    suspend fun deleteHewan(
        @Header("Authorization") userId: String,
        @Query("id") id: Long
    ): OpStatus
}

object HewanApi{
    val service: HewanApiService by lazy {
        retrofit.create(HewanApiService::class.java)
    }

    fun getHewanUrl(imageId: String):String{
        return "${BASE_URL}image.php?id=$imageId"
    }
}
enum class ApiStatus { LOADING, SUCCESS, FAILED }