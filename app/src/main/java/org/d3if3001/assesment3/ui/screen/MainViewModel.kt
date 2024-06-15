package org.d3if3001.mobpro1.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if3001.assesment3.model.Sepatu
import org.d3if3001.assesment3.network.ApiStatus
import org.d3if3001.assesment3.network.HewanApi
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Sepatu>())
        private set
    var status = MutableStateFlow(ApiStatus.LOADING)
        private  set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retrieveData(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = HewanApi.service.getHewan(userId)
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }
    fun saveData(userId: String, nama: String, namaLatin: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = HewanApi.service.postHewan(
                    userId,
                    nama.toRequestBody("text/plain".toMediaTypeOrNull()),
                    namaLatin.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )
                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }
    suspend fun deleteImage(userId: String, id: Long) {
        try {
            val result = HewanApi.service.deleteHewan(userId, id)
            if (result.status == "success") {
                Log.d("MainViewModel", "Berhasil menghapus gambar")
                retrieveData(userId)
            } else {
                Log.d("MainViewModel", "Gagal ID gambar: ${result.message}")
            }
        } catch (e: Exception) {
            Log.d("MainViewModel", "Gagal Hapus gambar: ${e.message}")
        }
    }
    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size
        )
        return MultipartBody.Part.createFormData(
            "image", "image.jpg", requestBody
        )
    }

    fun deleteData(userId: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = HewanApi.service.deleteHewan(userId, id)
                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun clearMessage() { errorMessage.value = null}
}