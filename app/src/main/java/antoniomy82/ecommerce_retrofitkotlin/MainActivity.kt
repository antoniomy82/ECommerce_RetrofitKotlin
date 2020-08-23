package antoniomy82.ecommerce_retrofitkotlin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
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
    var myCategory:String?=null
    private var tvLoad:TextView? =null
    private var btResult:Button? = null


    companion object{
        private var categoryList:ArrayList<Ecommerce>?=null

        fun getCategoryList(): ArrayList<Ecommerce> {
            return categoryList!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // tv_user = findViewById(R.id.tv_users)
      // var  tv_selectCategoria=findViewById(R.id.tv_SelectCategory)
        val spCateory:Spinner=findViewById(R.id.sp_category) //Acceso al spiner
        val categorias=resources.getStringArray(R.array.Categories) //Acceso a lista de items
        btResult=findViewById(R.id.bt_resultado)
        tvLoad=findViewById(R.id.tvLoad)

        if(spCateory!=null){
            val sp_adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
            spCateory.adapter = sp_adapter
        }


        //Spinner Categoria
        spCateory.setOnItemSelectedListener(object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                tvLoad?.visibility = View.VISIBLE
                btResult?.visibility= View.INVISIBLE

                myCategory=categorias[position]
                getAll() //Realizo el parseo
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        tvLoad?.visibility = View.VISIBLE


        btResult?.setOnClickListener {
                val intent = Intent(applicationContext, Result::class.java)
                startActivity(intent)
        }

    }


    fun getAll() {

        //Recibimos todos los ECommerce
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Creamos el servicio para hacer las llamadas
        service = retrofit.create<ApiService>(ApiService::class.java)
        categoryList=ArrayList<Ecommerce>() //Inicializo

        categoryList=ArrayList<Ecommerce>() //Inicializo

        service.getAllPosts().enqueue(object : Callback<List<Ecommerce>> {
            override fun onResponse(call: Call<List<Ecommerce>>?, response: Response<List<Ecommerce>>?) {

                val comercios = response?.body()
                val lenght: Int = comercios!!.size
                var contador:Int = 0

                for (i:Int in 0 until lenght){
                   if(myCategory==(comercios.get(i).category.toString())){
                        categoryList?.add(Ecommerce(
                            comercios[i].slug,
                            comercios[i].name,
                            comercios[i].category,
                            comercios[i].latitude,
                            comercios[i].longitude,
                            comercios[i].address,
                            comercios[i].contact,
                            comercios[i].social,
                            comercios[i].logo
                        ))
                        contador++
                    }
                }
                tvLoad?.visibility= View.INVISIBLE
                btResult?.visibility= View.VISIBLE
                Toast.makeText(this@MainActivity, myCategory+ " : "+contador+ "  coincidencias " , Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<List<Ecommerce>>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

}






