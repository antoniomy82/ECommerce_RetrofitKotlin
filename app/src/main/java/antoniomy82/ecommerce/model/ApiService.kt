package antoniomy82.ecommerce.model

import retrofit2.Call
import retrofit2.http.GET

/**
 *  Creado por Antonio Javier Morales Yáñez on 24/08/2020
 *  Github : https://github.com/antoniomy82
 */

interface ApiService {

    @GET("?category")
    fun getAllEcommerces(): Call<List<Ecommerce>>

}