package antoniomy82.ecommerce_retrofitkotlin.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import antoniomy82.ecommerce_retrofitkotlin.interfaces.ApiService
import antoniomy82.ecommerce_retrofitkotlin.models.Ecommerce
import antoniomy82.ecommerce_retrofitkotlin.utils.GPSTracker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class EcommerceViewModel : ViewModel() {

    companion object {
        private var ecommerceList: ArrayList<Ecommerce>? = null
        private lateinit var service: ApiService
        private const val URL = "http://prod.klikin.com/commerces/public/"
        private var miUbicacion: Location? = null
        private var miDireccion: String? = null

        fun getEcommerceList(): ArrayList<Ecommerce>? {
            return ecommerceList
        }

        fun getEcommerce(indice: Int): Ecommerce? {
            return ecommerceList?.get(indice)
        }

        fun setEcommerceList(myList: ArrayList<Ecommerce>) {
            this.ecommerceList = myList
        }

        fun getMiUbicacion(): Location? {
            return miUbicacion
        }


        fun getMiDireccion(): String? {
            return miDireccion
        }


        //Función que recibe todos los eCommercios que coincidan con la categoría seleccionada
        fun getRetrofitEcommerceList(myCategory: String) {

            //Recibimos todos los ECommerce
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            //Creamos el servicio para hacer las llamadas
            service = retrofit.create<ApiService>(ApiService::class.java)

            service.getAllEcommerces().enqueue(object : Callback<List<Ecommerce>> {
                override fun onResponse(
                    call: Call<List<Ecommerce>>?,
                    response: Response<List<Ecommerce>>?
                ) {
                    val comercios = response?.body()
                    val lenght: Int = comercios!!.size

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

                        }
                        setEcommerceList(ecommerceList) // Paso el resultado a ViewModel
                    }
                }

                override fun onFailure(call: Call<List<Ecommerce>>?, t: Throwable?) {
                    t?.printStackTrace()
                }
            })
        }

        //Ordeno la lista de eComercios por la distancia con respecto a la ubicación del smartphone
        fun sortByDistance(miUbicacion: Location) {

            for (i: Int in 0 until (ecommerceList?.size!!)) {
                if (ecommerceList!![i].myLocation != null) {
                    ecommerceList!![i].distance =
                        (miUbicacion.distanceTo(ecommerceList!![i].myLocation).div(1000))
                }
            }

            //Ordeno el ArrayList por distance
            ecommerceList?.sortBy { it.distance }
        }

        //Función usada para obtener Location (usado para distancia), en cada registro de la lista de eComercios por categoria
        private fun getLocation(latitude: Double, longitude: Double, name: String): Location {
            val myLocation = Location(name)
            myLocation.latitude = latitude
            myLocation.longitude = longitude

            return myLocation
        }

        //Dialog para habilitar GPS
        fun gps(context: Context?, activity: Activity?) {
            val gps = GPSTracker(context, activity)

            //Chequeo si tenemos los permisos de GPS
            if (context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED && context?.let {
                    ActivityCompat.checkSelfPermission(
                        it, Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED) {

                if (activity != null) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                }

            } else { //Tengo permisos
                if (gps.canGetLocation()) { //Tengo GPS activo
                    val latitude: Double = gps.getLatitude()
                    val longitude: Double = gps.getLongitude()

                    miUbicacion = getLocation(
                        gps.getLatitude(),
                        gps.getLongitude(),
                        "miUbicacion"
                    ) //Obtengo mi location


                    if (ecommerceList?.size != null) {
                        miUbicacion?.let { sortByDistance(it) }//Ordeno la lista de eComercios por categoria respecto a mi posición

                        miDireccion = gps.getLocationAddress(latitude, longitude)
                    }

                    while (ecommerceList?.get(0)?.distance == null) {
                        miUbicacion?.let { sortByDistance(it) }//Ordeno la lista de eComercios por categoria respecto a mi posición
                    }

                } else {//GPS deshabilitado o no tengo GPS
                    Toast.makeText(context, "Active GPS", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}