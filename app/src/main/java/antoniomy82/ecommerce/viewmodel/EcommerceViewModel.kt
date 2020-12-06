package antoniomy82.ecommerce.viewmodel

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.webkit.URLUtil
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import antoniomy82.ecommerce.R
import antoniomy82.ecommerce.databinding.ActivityDetailBinding
import antoniomy82.ecommerce.databinding.FragmentMainBinding
import antoniomy82.ecommerce.model.Ecommerce
import antoniomy82.ecommerce.model.EcommerceObservable
import antoniomy82.ecommerce.ui.ListActivity
import antoniomy82.ecommerce.utils.GPSTracker
import com.squareup.picasso.Picasso


class EcommerceViewModel : ViewModel() {

    private var mContextHome: Context? = null
    private var mActivityHome: Activity? = null
    private var categoriesList: List<String>? = null //Observable
    private var ecommerceList = ArrayList<Ecommerce>() //Observable

    var fragmentMainBinding: FragmentMainBinding? = null

    var activityDetailBinding: ActivityDetailBinding? = null

    private var ecommerceObservable: EcommerceObservable = EcommerceObservable()

    private var myLocation: Location? = null
    private var myAddress = MutableLiveData<String>()
    private var gps = GPSTracker()

    private var mContextDetail: Context? = null
    private var thisEcommerce: Ecommerce? = null


    companion object {
        private var myEcommerces = ArrayList<Ecommerce>()
        private var myItem: Int = 0

        fun getEcommercesListCompanion(): ArrayList<Ecommerce> {
            return myEcommerces
        }

        fun setEcommercesListCompanion(myEcommerce: ArrayList<Ecommerce>) {
            this.myEcommerces = myEcommerce
        }

        fun getEcommerceCompanion(position: Int): Ecommerce {
            return myEcommerces[position]
        }

        fun selectedEcommerce(position: Int) {
            this.myItem = position
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
        fragmentMainBinding: FragmentMainBinding?
    ) {
        this.mActivityHome = activity
        this.fragmentMainBinding = fragmentMainBinding
        gps = GPSTracker(myContext, activity)

        //Toolbar
        fragmentMainBinding?.toolbarMain?.title = "  eCommerce"
        fragmentMainBinding?.toolbarMain?.setLogo(R.drawable.ico_personal)
    }


    fun startCategoriesLoad(selectedCategory: Int) {
        //Caso inicial
        if (selectedCategory == 99) { //Caso base
            callCategoriesList()
            fragmentMainBinding?.btResultado?.visibility = View.GONE
        }
    }

    fun setSpinnerCategories() {
        fragmentMainBinding?.spCategory?.onItemSelectedListener =
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
        val dialog = AlertDialog.Builder(mActivityHome)
        dialog.setCancelable(false)
        dialog.setTitle("GPS DESACTIVADO")
        dialog.setMessage("¿Desea activar GPS o usar dirección por defecto?")

        dialog.setPositiveButton("Activar GPS") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mActivityHome?.startActivity(intent)

        }
            .setNegativeButton("Dirección por defecto ") { _, _ ->

                myAddress.value = "Puerta del Sol, 28013, Madrid, Spain"
                myLocation = gps.getLocationFromAddress(myAddress.value.toString())
                fragmentMainBinding?.apply {
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

            fragmentMainBinding?.progressBar?.visibility = View.VISIBLE
            fragmentMainBinding?.tvLoad?.setTextColor(Color.parseColor("#0492C2"))
            fragmentMainBinding?.tvLoad?.text = "Obteniendo localización GPS"
            fragmentMainBinding?.tvLoad?.visibility = View.VISIBLE

            if (myLocation != null) {
                myAddress.value = myLocation?.longitude?.let {
                    myLocation?.latitude?.let { it1 ->
                        gps.getAddressFromLocation(it1, it)
                    }
                }
                //Otra opción de Binding con una etiqueta String en el XML
                //activityMainBinding?.setVariable(BR.labelAddress, getMiDireccion().toString())
                val runnable = Runnable {
                    fragmentMainBinding?.edDireccion?.text = myAddress.value.toString()
                    //binding?.edDireccion?.setText(myAddress.value.toString())
                    fragmentMainBinding?.btResultado?.visibility = View.VISIBLE
                    fragmentMainBinding?.progressBar?.visibility = View.GONE
                    fragmentMainBinding?.tvLoad?.visibility = View.GONE
                }
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(runnable, 1000)
            } else {
                onClickGPS()
            }

        } else {
            dialogGpsOff()
        }
    }

    //Botón que muestra resultados
    fun onClickResult() {

        myLocation?.let {
            sortByDistance(it)
        }

        fragmentMainBinding?.progressBar?.visibility = View.VISIBLE
        fragmentMainBinding?.tvLoad?.visibility = View.VISIBLE
        fragmentMainBinding?.tvLoad?.setTextColor(Color.parseColor("#D50000"))
        fragmentMainBinding?.tvLoad?.text = "Calculando eCommercios Próximos"

        val runnable = Runnable {
            if (ecommerceList[0].distance == null) {
                onClickResult()
            } else {
                fragmentMainBinding?.progressBar?.visibility = View.GONE
                fragmentMainBinding?.tvLoad?.visibility = View.GONE

                //Navigation Drawer
                val intent = Intent(mActivityHome, ListActivity::class.java)
                mActivityHome?.startActivity(intent)

            }
        }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 1000)
    }


    fun setDetailBinding(activityDetailBinding: ActivityDetailBinding, context: Context) {

        this.activityDetailBinding = activityDetailBinding
        this.mContextDetail = context

        activityDetailBinding.toolbarMain.title = "  Detalle eComercio"
        activityDetailBinding.toolbarMain.setLogo(R.drawable.ico_personal)

        thisEcommerce = getEcommerceCompanion(myItem)

        val mAddress: String =
            thisEcommerce?.address?.street + ", " + thisEcommerce?.address?.zip + " , " + thisEcommerce?.address?.city + "," + thisEcommerce?.address?.country

        activityDetailBinding.tvDnombre.text = thisEcommerce?.name.toString()
        activityDetailBinding.tvDshortDescription.text = thisEcommerce?.shortDescription
        activityDetailBinding.tvDdireccion.text = mAddress
        activityDetailBinding.tvDemail.text = thisEcommerce?.contact?.email
        activityDetailBinding.tvDtelefono.text = thisEcommerce?.contact?.phone


        //Cargo el logo almacenado con Picasso
        if ((thisEcommerce?.logo?.url) != null) {
            Picasso.get().load(thisEcommerce?.logo?.url).placeholder(R.drawable.nologo)
                .into(activityDetailBinding.imLogo)

        } else {
            Picasso.get().load(R.drawable.noimage).into(activityDetailBinding.imLogo)
        }
    }

    fun sendEmail() {
        val miEmail =
            arrayOf<String>(thisEcommerce?.contact!!.email) //Tengo que hacer este cast raro si o si, sino no me aparece email
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, miEmail)
            putExtra(Intent.EXTRA_SUBJECT, " Soy Antonio J Morales -->Contráteme Xdd ")
        }

        if (mContextDetail?.let { intent.resolveActivity(it.packageManager) } != null) {
            mContextDetail?.startActivity(intent)
        }
    }

    fun callDialer() {
        if (thisEcommerce?.contact?.phone != "" || thisEcommerce?.contact?.phone != null) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + thisEcommerce?.contact?.phone)

            if (mContextDetail?.packageManager?.let { intent.resolveActivity(it) } != null) {
                mContextDetail?.startActivity(intent)
            }
        } else {
            Toast.makeText(
                mContextDetail,
                "No hay teléfono en ese campo",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun launchMaps() {
        if (thisEcommerce?.myLocation != null) {
            val intentUri =
                Uri.parse("geo:" + thisEcommerce?.latitude + "?z=16&q=" + thisEcommerce?.longitude + "(" + thisEcommerce?.address?.street + "," + thisEcommerce?.address?.city + "," + thisEcommerce?.address?.country + ")")
            val intent = Intent(Intent.ACTION_VIEW, intentUri)
            mContextDetail?.startActivity(intent)
        } else {
            Toast.makeText(
                mContextDetail,
                "No hay dirección o esta en formato incorrecto",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun callTwitter() {
        val miUri: String? = thisEcommerce?.social?.twitter
        if (URLUtil.isValidUrl(miUri)) {
            mContextDetail?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(miUri)))
        } else {
            Toast.makeText(
                mContextDetail,
                "No hay Twitter o enlace erroneo",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun callInstagram() {
        val miUri: String? = thisEcommerce?.social?.instagram
        if (URLUtil.isValidUrl(miUri)) {
            mContextDetail?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(miUri)))
        } else {
            Toast.makeText(
                mContextDetail,
                "No hay Instagram o enlace erroneo",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun callFacebook() {
        val miUri: String? = thisEcommerce?.social?.facebook
        if (URLUtil.isValidUrl(miUri)) {
            mContextDetail?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(miUri)))
        } else {
            Toast.makeText(
                mContextDetail,
                "No hay Facebook o enlace erroneo",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}



