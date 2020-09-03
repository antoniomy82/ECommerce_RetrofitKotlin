package antoniomy82.ecommerce_retrofitkotlin.ui

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
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import antoniomy82.ecommerce_retrofitkotlin.R
import antoniomy82.ecommerce_retrofitkotlin.interfaces.ApiService
import antoniomy82.ecommerce_retrofitkotlin.models.Ecommerce
import antoniomy82.ecommerce_retrofitkotlin.utils.GPSTracker
import antoniomy82.ecommerce_retrofitkotlin.viewmodel.EcommerceViewModel
import antoniomy82.ecommerce_retrofitkotlin.viewmodel.EcommerceViewModel.Companion.setEcommerceList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 *  Creado por Antonio Javier Morales Yáñez on 23/08/2020
 *  Github : https://github.com/antoniomy82
 *  Likedin: https://www.linkedin.com/in/antonio-javiermorales-yáñez-85a96b137/
 *  email: antoniomy82@gmail.com
 */

//Clase que funciona como controlador del proyecto
class MainActivity : AppCompatActivity() {


    lateinit var service: ApiService
    private val URL = "http://prod.klikin.com/commerces/public/"
    private var myCategory: String? = null
    private var tvLoad: TextView? = null
    private var btResult: Button? = null
    private var edDireccion: EditText? = null
    private var miUbicacion: Location? = null
    private var imGPS: ImageView? = null
    private var progressBar: ProgressBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "  eCommerce"
        toolbar.setLogo(R.drawable.ico_personal)
        setSupportActionBar(toolbar)

        val spCateory: Spinner = findViewById(R.id.sp_category) //Acceso al spiner
        val categorias = resources.getStringArray(R.array.Categories) //Acceso a lista de items
        btResult = findViewById(R.id.bt_resultado)
        tvLoad = findViewById(R.id.tvLoad)
        edDireccion = findViewById(R.id.edDireccion)
        imGPS = findViewById(R.id.imGPS)
        progressBar = findViewById(R.id.progressBar)


        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        spCateory.adapter = spAdapter


        //Spinner Categoria
        spCateory.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                tvLoad?.visibility = View.VISIBLE
                progressBar?.visibility = View.VISIBLE
                btResult?.visibility = View.INVISIBLE

                //Inicalizo valores
                edDireccion?.setText(R.string.aviso_gps)
                miUbicacion = null
                imGPS?.visibility = View.INVISIBLE

                myCategory = categorias[position]

                getEcommerceList() //Realizo el parseo
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        tvLoad?.visibility = View.VISIBLE


        //Botón que optiene la dirección mediante GPS de nuestro Smartphone
        findViewById<View>(R.id.imGPS).setOnClickListener {
            gps()
            if (miUbicacion != null) {
                tvLoad?.visibility = View.INVISIBLE
                btResult?.visibility = View.VISIBLE
            }
        }

        //Botón que muestra una lista de todos los eComercios, filtrados por categoría y distancia al Smartphone
        btResult?.setOnClickListener {
            if (miUbicacion == null) {
                tvLoad?.visibility = View.VISIBLE
            } else {
                val intent = Intent(applicationContext, ResultActivity::class.java)
                startActivity(intent)
            }
        }
    }

    /**
     * BLOQUE DE FUNCIONES AUXILIARES  -- LÓGICA
     */

    //Función que recibe todos los eCommercios que coincidan con la categoría seleccionada
    fun getEcommerceList() {

        //Recibimos todos los ECommerce
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Creamos el servicio para hacer las llamadas
        service = retrofit.create<ApiService>(
            ApiService::class.java
        )


        service.getAllEcommerces().enqueue(object : Callback<List<Ecommerce>> {
            override fun onResponse(
                call: Call<List<Ecommerce>>?,
                response: Response<List<Ecommerce>>?
            ) {

                val comercios = response?.body()
                val lenght: Int = comercios!!.size
                var contador = 0
                val ecommerceList = ArrayList<Ecommerce>() //Inicializo

                for (i: Int in 0 until lenght) {
                    if (myCategory == (comercios[i].category.toString())) {
                        ecommerceList.add(
                            Ecommerce(
                                comercios[i].shortDescription,
                                comercios[i].name,
                                comercios[i].category,
                                comercios[i].latitude,
                                comercios[i].longitude,
                                comercios[i].address,
                                comercios[i].contact,
                                comercios[i].social,
                                comercios[i].logo
                            )
                        )
                        contador++
                    }
                    ecommerceList.let { setEcommerceList(it) } // Paso el resultado a ViewModel

                }
                tvLoad?.visibility = View.INVISIBLE
                btResult?.visibility = View.INVISIBLE
                imGPS?.visibility = View.VISIBLE
                progressBar?.visibility = View.INVISIBLE
                Toast.makeText(
                    this@MainActivity,
                    "$myCategory : $contador  coincidencias ",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onFailure(call: Call<List<Ecommerce>>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }


    //Ordeno la lista de eComercios por la distancia con respecto a la ubicación del smartphone
    private fun sortByDistance() {

        val ecommerceList = EcommerceViewModel.getEcommerceList()

        //Calculo la distancia entre el smartphone y los eComercios, y las introduzco en su variable distance

        for (i: Int in 0 until EcommerceViewModel.getSize()!!) {
            if ((miUbicacion != null) && (ecommerceList!![i].myLocation != null)) {
                ecommerceList[i].distance =
                    (miUbicacion!!.distanceTo(ecommerceList[i].myLocation))
            }
        }

        //Ordeno el ArrayList por distance
        ecommerceList!!.sortBy { it.distance }
        setEcommerceList(ecommerceList)
    }

    //Función usada para obtener Location (usado para distancia), en cada registro de la lista de eComercios por categoria
    private fun getLocation(latitude: Double, longitude: Double, name: String): Location {
        val myLocation = Location(name)
        myLocation.latitude = latitude
        myLocation.longitude = longitude

        return myLocation
    }

    //Dialog para habilitar GPS
    private fun gps() {
        val address: String //Use to paint GeoLocation in edText or Toast
        val gps = GPSTracker(
            applicationContext,
            this
        )

        //Chequeo si tenemos los permisos de GPS
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )

        } else { //Tengo permisos
            if (gps.canGetLocation()) { //Tengo GPS activo
                val latitude: Double = gps.getLatitude()
                val longitude: Double = gps.getLongitude()

                miUbicacion = getLocation(
                    gps.getLatitude(),
                    gps.getLongitude(),
                    "miUbicacion"
                ) //Obtengo mi location

                sortByDistance() //Ordeno la lista de eComercios por categoria respecto a mi posición

                Toast.makeText(
                    applicationContext,
                    "eComercios más próximos ¡calculados!",
                    Toast.LENGTH_SHORT
                ).show()

                address = gps.getLocationAddress(latitude, longitude)
                edDireccion!!.setText(address)

            } else { dialogNoGPS() }//GPS deshabilitado o no tengo GPS
        }
    }

    //Dialog GPS desactivado
    @Suppress("NAME_SHADOWING")
    private fun dialogNoGPS() {
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setTitle("GPS DESACTIVADO")
        dialog.setMessage("¿Desea activar GPS?")
        dialog.setPositiveButton("Aceptar") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            this.startActivity(intent)
        }
            .setNegativeButton("Cancelar ") { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialog.create()
        alert.show()
    }

}






