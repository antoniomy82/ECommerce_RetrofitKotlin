package antoniomy82.ecommerce_retrofitkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var service: ApiService
    private val URL = "http://prod.klikin.com/commerces/public/"
    var tv_user: TextView? = null
    var str:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_user = findViewById(R.id.tv_users)

        getAll()
    }


    fun getAll() {

        //Recibimos todos los ECommerce
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Creamos el servicio para hacer las llamadas
        service = retrofit.create<ApiService>(ApiService::class.java)

        service.getAllPosts().enqueue(object : Callback<List<Ecommerce>> {
            override fun onResponse(call: Call<List<Ecommerce>>?, response: Response<List<Ecommerce>>?) {

                var comercios = response?.body()
                var lenght: Int = comercios!!.size

                for (i:Int in 0 until lenght){
                    str = str + "\n" + comercios.get(i).slug + " " + comercios.get(i).category

                }
                tv_user!!.text = str

            }

            override fun onFailure(call: Call<List<Ecommerce>>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

}






