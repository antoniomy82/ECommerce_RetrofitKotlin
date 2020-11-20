package antoniomy82.ecommerce.viewmodel

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import antoniomy82.ecommerce.R
import antoniomy82.ecommerce.databinding.ActivityMainBinding
import antoniomy82.ecommerce.model.Ecommerce
import antoniomy82.ecommerce.model.EcommerceObservable
import antoniomy82.ecommerce.ui.ResultActivity
import antoniomy82.ecommerce.utils.GPSTracker

class EcommerceViewModel : ViewModel() {

    private var context: Context? = null
    private var activity: Activity? = null
    private var categoriesList: List<String>? = null //Observable
    private var ecommerceList = ArrayList<Ecommerce>() //Observable

    var activityMainBinding: ActivityMainBinding? = null
    private var ecommerceObservable: EcommerceObservable = EcommerceObservable()

    private var myLocation: Location? = null
    private var myAddress: String? = null
    private var gps = GPSTracker()

    companion object {
        private var myEcommerces = ArrayList<Ecommerce>()

        fun getEcommercesListCompanion(): ArrayList<Ecommerce> {
            return myEcommerces
        }

        fun setEcommercesListCompanion(myEcommerce: ArrayList<Ecommerce>) {
            this.myEcommerces = myEcommerce
        }

        fun getEcommerceCompanion(position: Int): Ecommerce {
            return myEcommerces[position]
        }
    }

    fun callEcommerceList(myCategory: String) {
        ecommerceObservable.callEcommerces(myCategory)
    }

    fun callCategoriesList() {
        ecommerceObservable.callCategoriesList()
    }

    fun getCategoriesList(): MutableLiveData<List<String>> {
        return ecommerceObservable.getCategoriesList()
    }

    fun getEcommercesList(): MutableLiveData<ArrayList<Ecommerce>> {
        return ecommerceObservable.getEcommerces()
    }

    fun setCategoriesList(categoriesList: List<String>) {
        this.categoriesList = categoriesList
    }

    fun setEcommercesList(ecommerceList: ArrayList<Ecommerce>) {
        this.ecommerceList = ecommerceList
    }


    fun setMainActivityContextBinding(
        myContext: Context,
        activity: Activity?,
        activityMainBinding: ActivityMainBinding
    ) {
        this.context = myContext
        this.activity = activity
        this.activityMainBinding = activityMainBinding

        gps = GPSTracker(myContext, activity)
        activityMainBinding.btResultado.visibility = View.GONE

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
                    if (categoriesList != null) callEcommerceList(categoriesList!![position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    callEcommerceList(categoriesList!![0]) //Realizo el parseo

                }
            }
    }


    //Ordeno la lista de eComercios por la distancia con respecto a la ubicación del smartphone
    private fun sortByDistance(miUbicacion: Location) {

        for (i: Int in 0 until ecommerceList.size) {
            if (ecommerceList[i].myLocation != null) {
                ecommerceList[i].distance =
                    (miUbicacion.distanceTo(ecommerceList[i].myLocation).div(1000))
            }
        }

        ecommerceList.sortBy { it.distance }  //Ordeno el ArrayList por distance
    }


    //Dialog por si GPS está apagado
    private fun dialogGpsOff() {
        val dialog = AlertDialog.Builder(activity)
        dialog.setCancelable(false)
        dialog.setTitle("GPS DESACTIVADO")
        dialog.setMessage("¿Desea activar GPS o usar dirección por defecto?")

        dialog.setPositiveButton("Activar GPS") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            activity?.startActivity(intent)

            val runnable = Runnable {
                myLocation = gps.getLocation()
            }
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(runnable, 5000)
        }
            .setNegativeButton("Dirección por defecto ") { _, _ ->
                myAddress = "Puerta del Sol, 28013, Madrid, Spain"
                myLocation = gps.getLocationFromAddress(myAddress!!)
                activityMainBinding?.edDireccion?.setText(myAddress)
                activityMainBinding?.btResultado?.visibility = View.VISIBLE
            }
        val alert = dialog.create()
        alert.show()
    }

    //Botón que obtiene la dirección mediante GPS de nuestro Smartphone
    fun onClickGPS() {

        if (gps.gpsIsActive()) {
            myLocation = gps.getLocation()

            myLocation.let {
                myAddress =
                    gps.getAddressFromLocation(myLocation!!.latitude, myLocation!!.longitude)
                //Otra opción de Binding con una etiqueta String en el XML
                //activityMainBinding?.setVariable(BR.labelAddress, getMiDireccion().toString())
                activityMainBinding?.edDireccion?.setText(myAddress.toString())
                activityMainBinding?.btResultado?.visibility = View.VISIBLE
            }
        } else {
            Toast.makeText(context, "GPS APAGADO", Toast.LENGTH_LONG).show()
            dialogGpsOff()
        }
    }

    //Botón que muestra resultados
    fun onClickResult() {

        if (myLocation == null) {
            activityMainBinding?.edDireccion?.setText(R.string.aviso_gps)
        } else {
            myLocation?.let {
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
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(runnable, 1000)
        }
    }

}