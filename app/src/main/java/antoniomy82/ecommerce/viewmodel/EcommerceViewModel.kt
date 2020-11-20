package antoniomy82.ecommerce.viewmodel

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import antoniomy82.ecommerce.R
import antoniomy82.ecommerce.databinding.ActivityMainBinding
import antoniomy82.ecommerce.model.Ecommerce
import antoniomy82.ecommerce.model.EcommerceRepository
import antoniomy82.ecommerce.model.EcommerceRepositoryImpl
import antoniomy82.ecommerce.ui.ResultActivity
import antoniomy82.ecommerce.utils.GPSTracker

class EcommerceViewModel : ViewModel() {

    private var context: Context? = null
    var activityMainBinding: ActivityMainBinding? = null
    private var activity: Activity? = null
    private var categoriesList: List<String>? = listOf()
    private var spAdapter: ArrayAdapter<String>? = null


    fun setMainActivityContextBinding(
        myContext: Context,
        activity: Activity?,
        activityMainBinding: ActivityMainBinding
    ) {
        this.context = myContext
        this.activity = activity
        this.activityMainBinding = activityMainBinding

        activityMainBinding.btResultado.visibility = View.GONE

        loadSpinnerData()
        spinnerCategories()
    }

    private fun spinnerCategories() {
        //Spinner Categoria val myList = context?.resources?.getStringArray(R.array.Categories)

        activityMainBinding?.spCategory?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    activityMainBinding?.edDireccion?.setText(R.string.aviso_gps)

                    if (categoriesList != null) {
                        getRetrofitEcommerceList(
                            (categoriesList?.get(position) ?: 0).toString()
                        ) //Realizo el parseo ***
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    categoriesList?.get(0)
                        ?.let { getRetrofitEcommerceList(it) } //Realizo el parseo

                }
            }
    }

    private fun loadSpinnerData() {

        activityMainBinding?.progressBar?.visibility = View.VISIBLE
        activityMainBinding?.tvLoad?.visibility = View.VISIBLE

        val mydinamiList = getCategoriesList()

        if (mydinamiList != null) {

            categoriesList = mydinamiList
            spAdapter = context?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    categoriesList!!
                )
            }
            activityMainBinding?.spCategory?.adapter = spAdapter
            activityMainBinding?.progressBar?.visibility = View.GONE
            activityMainBinding?.tvLoad?.visibility = View.GONE

            Log.d("isNotEmpty", "LoadSpinnerCategories")
        } else {
            val runnable = Runnable {
                loadSpinnerData()
                Log.d("Esperando", " @@ runable EcommerceViewModel")
            }
            val handler = Handler()
            handler.postDelayed(runnable, 3000)
        }
    }


    companion object {
        private var ecommerceRepository: EcommerceRepository = EcommerceRepositoryImpl()
        private var ecommerceList = ArrayList<Ecommerce>()
        private var myLocation: Location? = null
        private var myAddress: String? = null

        fun getEcommerceList(): ArrayList<Ecommerce>? {
            return ecommerceList
        }

        fun getEcommerce(indice: Int): Ecommerce? {
            return ecommerceList[indice]
        }


        fun getMyLocation(): Location? {
            return myLocation
        }


        fun getMyAddress(): String? {
            return myAddress
        }


        fun getRetrofitEcommerceList(myCategory: String) {
            this.ecommerceList = ecommerceRepository.getEcommerces(myCategory)
        }

        fun getCategoriesList(): List<String>? {
            return ecommerceRepository.getCategoriesList()
        }


        //Ordeno la lista de eComercios por la distancia con respecto a la ubicación del smartphone
        fun sortByDistance(miUbicacion: Location) {

            for (i: Int in 0 until ecommerceList.size) {
                if (ecommerceList[i].myLocation != null) {
                    ecommerceList[i].distance =
                        (miUbicacion.distanceTo(ecommerceList[i].myLocation).div(1000))
                }
            }

            ecommerceList.sortBy { it.distance }  //Ordeno el ArrayList por distance
        }

        //Función usada para obtener Location (usado para distancia), en cada registro de la lista de eComercios por categoria
        private fun getLocation(latitude: Double, longitude: Double, name: String): Location {
            val myLocation = Location(name)
            myLocation.latitude = latitude
            myLocation.longitude = longitude

            return myLocation
        }

    }

    //Compruebo que tengo acceso a GPS y obtengo ubicación
    //Dividir esto en 2 funciones - chekGPS y getLocation
    private fun checkGpsAccessGetLocation(context: Context?, activity: Activity?) {
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
                myAddress = gps.getLocationAddress(latitude, longitude)

            } else {//GPS deshabilitado o no tengo GPS
                Toast.makeText(context, "Active GPS", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Dialog por si el GPS está apagado
    private fun dialogGpsOff() {
        val dialog = AlertDialog.Builder(activity)
        dialog.setCancelable(false)
        dialog.setTitle("GPS DESACTIVADO")
        dialog.setMessage("¿Desea activar GPS?")
        dialog.setPositiveButton("Aceptar") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            activity?.startActivity(intent)
        }
            .setNegativeButton("Cancelar ") { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialog.create()
        alert.show()
    }

    //Botón que obtiene la dirección mediante GPS de nuestro Smartphone
    fun onClickGPS() {
        checkGpsAccessGetLocation(context, activity)

        if (getMyLocation() != null) {

            //Otra opción de Binding con una etiqueta String en el XML
            //activityMainBinding?.setVariable(BR.labelAddress, getMiDireccion().toString())

            activityMainBinding?.edDireccion?.setText(getMyAddress().toString())

            activityMainBinding?.btResultado?.visibility = View.VISIBLE


        } else {
            dialogGpsOff()
        }
    }

    //Botón que muestra resultados
    //Poner una dirección por defecto si GPS apagado.
    fun onClickResult() {

        if (getMyLocation() == null) {
            activityMainBinding?.edDireccion?.setText(R.string.aviso_gps)
        } else {
            getMyLocation()?.let {
                sortByDistance(it)
            }

            activityMainBinding?.btResultado?.visibility = View.GONE
            activityMainBinding?.progressBar?.visibility = View.VISIBLE
            activityMainBinding?.tvLoad?.visibility = View.VISIBLE

            val runnable = Runnable {
                activityMainBinding?.progressBar?.visibility = View.GONE
                activityMainBinding?.tvLoad?.visibility = View.GONE
                val intent = Intent(activity, ResultActivity::class.java)
                activity?.startActivity(intent)
            }
            val handler = Handler()
            handler.postDelayed(runnable, 3000)

        }
    }


}