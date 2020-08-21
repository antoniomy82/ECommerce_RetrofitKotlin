package antoniomy82.ecommerce_retrofitkotlin

import android.graphics.Insets
import android.graphics.Insets.add
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST

class MainActivity : AppCompatActivity() {

    lateinit var service: ApiService
    private val URL = "http://prod.klikin.com/commerces/public/"
    var listaComercios: ArrayList<Post>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Creamos el servicio para hacer las llamadas
        service = retrofit.create<ApiService>(ApiService::class.java)

        getAllPosts()


    }


    fun getAllPosts() {
        //Recibimos todos los ECommerce

        listaComercios = ArrayList<Post>() //Inicializo ArrayList

        service.getAllPosts().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>?, response: Response<List<Post>>?) {
                val posts = response?.body()
                Log.i("@Todos-->", Gson().toJson(posts))
                listaComercios?.add(Post(Gson().toJson(posts)))
                todaLista()
            }

            override fun onFailure(call: Call<List<Post>>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    fun todaLista() {


        Log.d("Lista de Comercios ", listaComercios?.size.toString())
    }
}






