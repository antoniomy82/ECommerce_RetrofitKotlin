package antoniomy82.ecommerce.viewmodel

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    var binding: ActivityMainBinding? = null
    private var ecommerceObservable: EcommerceObservable = EcommerceObservable()

    private var myLocation: Location? = null
    private var myAddress = MutableLiveData<String>()
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


    fun getMyAddress(): MutableLiveData<String> {
        return myAddress
    }

    fun setMainActivityContextBinding(
        myContext: Context,
        activity: Activity?,
        activityMainBinding: ActivityMainBinding
    ) {
        this.context = myContext
        this.activity = activity
        this.binding = activityMainBinding

        gps = GPSTracker(myContext, activity)

    }


    fun setSpinnerCategories() {
        //Spinner Categoria val myList = context?.resources?.getStringArray(R.array.Categories)

        binding?.spCategory?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    if (categoriesList != null) {
                        callEcommerceList(categoriesList!![position])
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
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
            println("For Terminado")
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

        }
            .setNegativeButton("Dirección por defecto ") { _, _ ->

                myAddress.value = "Puerta del Sol, 28013, Madrid, Spain"
                myLocation = gps.getLocationFromAddress(myAddress.value.toString())
                binding?.apply {
                    edDireccion.text = myAddress.value.toString()
                    //edDireccion.setText(myAddress.value.toString())
                    btResultado.visibility = View.VISIBLE
                }
            }
        val alert = dialog.create()
        alert.show()
    }

    //Botón que obtiene la dirección mediante GPS de nuestro Smartphone
    fun onClickGPS() {

        if (gps.gpsIsActive()) {
            myLocation = gps.getLocation()

            binding?.progressBar?.visibility = View.VISIBLE
            binding?.tvLoad?.setTextColor(Color.parseColor("#0492C2"))
            binding?.tvLoad?.text = "Obteniendo localización GPS"
            binding?.tvLoad?.visibility = View.VISIBLE

            if (myLocation != null) {
                myAddress.value = myLocation?.longitude?.let {
                    myLocation?.latitude?.let { it1 ->
                        gps.getAddressFromLocation(it1, it)
                    }
                }
                //Otra opción de Binding con una etiqueta String en el XML
                //activityMainBinding?.setVariable(BR.labelAddress, getMiDireccion().toString())
                val runnable = Runnable {
                    binding?.edDireccion?.text = myAddress.value.toString()
                    //binding?.edDireccion?.setText(myAddress.value.toString())
                    binding?.btResultado?.visibility = View.VISIBLE
                    binding?.progressBar?.visibility = View.GONE
                    binding?.tvLoad?.visibility = View.GONE
                }
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(runnable, 1000)
            } else {
                onClickGPS()
            }

        } else {
            Toast.makeText(context, "GPS APAGADO", Toast.LENGTH_LONG).show()
            dialogGpsOff()
        }
    }

    //Botón que muestra resultados
    fun onClickResult() {

        myLocation?.let {
            sortByDistance(it)
        }

            binding?.progressBar?.visibility = View.VISIBLE
            binding?.tvLoad?.visibility = View.VISIBLE
            binding?.tvLoad?.setTextColor(Color.parseColor("#D50000"))
            binding?.tvLoad?.text = "Calculando eCommercios Próximos"

            val runnable = Runnable {
                if (ecommerceList[0].distance == null) {
                    onClickResult()
                } else {
                    binding?.progressBar?.visibility = View.GONE
                    binding?.tvLoad?.visibility = View.GONE
                    val intent = Intent(activity, ResultActivity::class.java)
                    activity?.startActivity(intent)
                }
            }
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(runnable, 1000)
    }

}