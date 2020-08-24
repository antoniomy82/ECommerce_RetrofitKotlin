package antoniomy82.ecommerce_retrofitkotlin

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    lateinit var service: ApiService
    private val URL = "http://prod.klikin.com/commerces/public/"

    var myCategory:String?=null
    private var tvLoad:TextView? =null
    private var btResult:Button? = null
    private var edDireccion: EditText? = null
    private var miUbicacion:Location?=null


    companion object{
        private var categoryList:ArrayList<Ecommerce>?=null

        fun getCategoryList(): ArrayList<Ecommerce> {
            return categoryList!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spCateory:Spinner=findViewById(R.id.sp_category) //Acceso al spiner
        val categorias=resources.getStringArray(R.array.Categories) //Acceso a lista de items
        btResult=findViewById(R.id.bt_resultado)
        tvLoad=findViewById(R.id.tvLoad)
        edDireccion = findViewById(R.id.edDireccion)

        categoryList=ArrayList<Ecommerce>() //Inicializo

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

        //Cuando obtenemos la dirección GPS
        findViewById<View>(R.id.imGPS).setOnClickListener { gps() }
    }


    fun getAll() {

        //Recibimos todos los ECommerce
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Creamos el servicio para hacer las llamadas
        service = retrofit.create<ApiService>(ApiService::class.java)
       // categoryList=ArrayList<Ecommerce>() //Inicializo

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
                Toast.makeText(this@MainActivity, "$myCategory : $contador  coincidencias ", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<List<Ecommerce>>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    fun getLocation (latitude:Double, longitude:Double, name:String): Location {
        val myLocation = Location(name)
        myLocation.latitude = latitude
        myLocation.longitude= longitude

        return myLocation
    }

    fun sortByLocation() {
        categoryList= getCategoryList()
        val lenght: Int = categoryList!!.size
        var contador:Int = 0;

        for (i: Int in 0 until lenght) {
            if((miUbicacion!=null)&&(categoryList!!.get(i).myLocation!=null)){
                categoryList!!.get(i).distance=(miUbicacion!!.distanceTo(categoryList!!.get(i).myLocation))
                contador++
            }
        }

        Toast.makeText(this@MainActivity, " $contador  ordenados ", Toast.LENGTH_LONG).show()
    }


    /*

    //Ordenamos la lista de contactos por nombre
    fun ordenarListaContactos(lista: java.util.ArrayList<Contactos>?) {
        lista!!.sortWith(Comparator { c1, c2 -> c1.nombre.compareTo(c2.nombre) })
        listaContactos = lista
    }
*/

    /**
     * Dialog to Enable GPS
     */
    fun gps() {
        val address: String //Use to paint GeoLocation in edText or Toast
        val gps = GPSTracker(applicationContext, this)

        //Check GPS Permissions
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        } else { //Tengo permisos
            if (gps.canGetLocation()) { //I have GPS enable
                val latitude: Double = gps.getLatitude()
                val longitude: Double = gps.getLongitude()

                miUbicacion= getLocation(gps.getLatitude(),gps.getLongitude(),"miUbicacion") //Obtengo mi location
                sortByLocation()

                // \n is for new line
                Toast.makeText(applicationContext, "Ubicación - \nLat: $latitude\nLong: $longitude", Toast.LENGTH_LONG).show()
                address = gps.getLocationAddress(latitude, longitude)
                edDireccion!!.setText(address)

            } else { dialogNoGPS() }//GPS deshabilitado o no tengo GPS
        }
    } //onClick


    //GPS desactivado
    private fun dialogNoGPS() {
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setTitle("GPS DESACTIVADO")
        dialog.setMessage("¿Desea activar GPS?")
        dialog.setPositiveButton("Aceptar") { dialog, id ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            this.startActivity(intent)
        }
            .setNegativeButton("Cancelar ") { dialog, which -> //Action for "Cancel".
                dialog.cancel()
            }
        val alert = dialog.create()
        alert.show()
    }


}






