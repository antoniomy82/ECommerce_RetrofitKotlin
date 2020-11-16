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
import antoniomy82.ecommerce_retrofitkotlin.model.Ecommerce
import antoniomy82.ecommerce_retrofitkotlin.model.EcommerceRepository
import antoniomy82.ecommerce_retrofitkotlin.model.EcommerceRepositoryImpl
import antoniomy82.ecommerce_retrofitkotlin.utils.GPSTracker


class EcommerceViewModel : ViewModel() {

    companion object {
        private var ecommerceRepository: EcommerceRepository = EcommerceRepositoryImpl()
        private var ecommerceList = ArrayList<Ecommerce>()
        private var myLocation: Location? = null
        private var miDireccion: String? = null

        fun getEcommerceList(): ArrayList<Ecommerce>? {
            return ecommerceList
        }

        fun getEcommerce(indice: Int): Ecommerce? {
            return ecommerceList[indice]
        }


        fun getMyLocation(): Location? {
            return myLocation
        }


        fun getMiDireccion(): String? {
            return miDireccion
        }


        //Función que recibe todos los eCommercios que coincidan con la categoría seleccionada
        fun getRetrofitEcommerceList(myCategory: String) {
            this.ecommerceList = ecommerceRepository.getEcommerces(myCategory)
        }

        //Ordeno la lista de eComercios por la distancia con respecto a la ubicación del smartphone
        fun sortByDistance(miUbicacion: Location) {

            for (i: Int in 0 until ecommerceList.size) {
                if (ecommerceList[i].myLocation != null) {
                    ecommerceList[i].distance =
                        (miUbicacion.distanceTo(ecommerceList[i].myLocation).div(1000))
                }
            }

            //Ordeno el ArrayList por distance
            ecommerceList.sortBy { it.distance }
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

                    myLocation = getLocation(
                        gps.getLatitude(),
                        gps.getLongitude(),
                        "miUbicacion"
                    ) //Obtengo mi location


                    myLocation?.let { sortByDistance(it) }//Ordeno la lista de eComercios por categoria respecto a mi posición

                    miDireccion = gps.getLocationAddress(latitude, longitude)

                    while (ecommerceList[0].distance == null) {
                        myLocation?.let { sortByDistance(it) }//Ordeno la lista de eComercios por categoria respecto a mi posición
                    }

                } else {//GPS deshabilitado o no tengo GPS
                    Toast.makeText(context, "Active GPS", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}