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

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    lateinit var service: ApiService
    private val URL = "http://prod.klikin.com/commerces/public/"

    var myCategory:String?=null
    private var tvLoad:TextView? =null
    private var btResult:Button? = null
    private var edDireccion: EditText? = null
    private var miUbicacion:Location?=null
    private var imGPS:ImageView?=null


    companion object{
        private var ecommerceList:ArrayList<Ecommerce>?=null

        fun getEcommerceList(): ArrayList<Ecommerce>? {
            return ecommerceList
        }

        fun getEcommerce(indice: Int): Ecommerce? {
            return ecommerceList?.get(indice)
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
        imGPS=findViewById(R.id.imGPS)


        val sp_adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        spCateory.adapter = sp_adapter


        //Spinner Categoria
        spCateory.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                tvLoad?.visibility = View.VISIBLE
                btResult?.visibility= View.INVISIBLE
                //Inicalizo valores
                ecommerceList=null
                edDireccion?.setText(R.string.aviso_gps)
                miUbicacion=null
                imGPS?.visibility=View.INVISIBLE

                myCategory=categorias[position]

                getAll() //Realizo el parseo
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        tvLoad?.visibility = View.VISIBLE


        btResult?.setOnClickListener {
            if(miUbicacion==null){
                tvLoad?.visibility = View.VISIBLE
            } else {
                val intent = Intent(applicationContext, Result::class.java)
                startActivity(intent)
            }
        }

        //Cuando obtenemos la dirección GPS
        findViewById<View>(R.id.imGPS).setOnClickListener { gps()
            if (miUbicacion!=null) {
                tvLoad?.visibility = View.INVISIBLE
                btResult?.visibility = View.VISIBLE
            }
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

        ecommerceList=ArrayList<Ecommerce>() //Inicializo

        service.getAllPosts().enqueue(object : Callback<List<Ecommerce>> {
            override fun onResponse(call: Call<List<Ecommerce>>?, response: Response<List<Ecommerce>>?) {

                val comercios = response?.body()
                val lenght: Int = comercios!!.size
                var contador = 0

                for (i:Int in 0 until lenght){
                    if(myCategory==(comercios[i].category.toString())){
                        ecommerceList?.add(Ecommerce(
                            comercios[i].shortDescription,
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
                btResult?.visibility= View.INVISIBLE
                imGPS?.visibility=View.VISIBLE
                Toast.makeText(this@MainActivity, "$myCategory : $contador  coincidencias ", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<List<Ecommerce>>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    private fun getLocation (latitude:Double, longitude:Double, name:String): Location {
        val myLocation = Location(name)
        myLocation.latitude = latitude
        myLocation.longitude= longitude

        return myLocation
    }

    //Relleno las distancias respecto a la ubicación actual y ordeno la lista de ecomercios
    private fun sortByDistance() {
        ecommerceList= getEcommerceList()
        val lenght: Int = ecommerceList!!.size
        var contador = 0

        //Relleno las distancias entre el Smartphone y la ubicación del comercio.
        for (i: Int in 0 until lenght) {
            if((miUbicacion!=null)&&(ecommerceList!!.get(i).myLocation!=null)){
                ecommerceList!!.get(i).distance=(miUbicacion!!.distanceTo(ecommerceList!!.get(i).myLocation))
                contador++
            }
        }

        //Ordeno el ArrayList
        ecommerceList!!.sortBy { it.distance }
    }


    /**
     * Dialog to Enable GPS
     */
    private fun gps() {
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

                miUbicacion = getLocation(
                    gps.getLatitude(),
                    gps.getLongitude(),
                    "miUbicacion"
                ) //Obtengo mi location
                sortByDistance()

                // \n is for new line
                Toast.makeText(
                    applicationContext,
                    "eComercios más próximos ¡calculados!",
                    Toast.LENGTH_SHORT
                ).show()
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
        dialog.setPositiveButton("Aceptar") { _, id ->
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






