package antoniomy82.ecommerce_retrofitkotlin

import retrofit2.Call
import retrofit2.http.GET


interface ApiService {

    @GET("?category")
    fun getAllPosts(): Call<List<Post>>
}