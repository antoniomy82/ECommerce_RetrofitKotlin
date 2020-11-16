package antoniomy82.ecommerce_retrofitkotlin.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiAdapter {

    private val URL = "http://prod.klikin.com/commerces/public/"
    var api: ApiService? = null

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create<ApiService>(ApiService::class.java)
    }

}