package antoniomy82.ecommerce_retrofitkotlin.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import antoniomy82.ecommerce_retrofitkotlin.R
import antoniomy82.ecommerce_retrofitkotlin.viewmodel.EcommerceViewModel

/**
 *  Creado por Antonio Javier Morales Yáñez on 23/08/2020
 *  Github : https://github.com/antoniomy82
 *  Likedin: https://www.linkedin.com/in/antonio-javiermorales-yáñez-85a96b137/
 *  email: antoniomy82@gmail.com
 */

//Clase que funciona como controlador del proyecto
class MainActivity : AppCompatActivity() {

    private var myCategory: String? = null
    private var tvLoad: TextView? = null
    private var btResult: Button? = null
    private var edDireccion: EditText? = null
    private var imGPS: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var spCateory: Spinner? = null
    private var categorias: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "  eCommerce"
        toolbar.setLogo(R.drawable.ico_personal)
        setSupportActionBar(toolbar)


        btResult = findViewById(R.id.bt_resultado)
        progressBar = findViewById(R.id.progressBar)
        tvLoad = findViewById(R.id.tvLoad)
        edDireccion = findViewById(R.id.edDireccion)
        imGPS = findViewById(R.id.imGPS)
        spCateory = findViewById(R.id.sp_category) //Acceso al spiner
        categorias = resources.getStringArray(R.array.Categories) //Acceso a lista de items

        //Inicializo
        btResult?.visibility = View.INVISIBLE
        edDireccion?.setText(R.string.aviso_gps)


        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias!!)
        spCateory?.adapter = spAdapter

        //Spinner Categoria
        spCateory?.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //Inicalizo valores
                tvLoad?.visibility = View.VISIBLE
                progressBar?.visibility = View.VISIBLE
                btResult?.visibility = View.INVISIBLE

                edDireccion?.setText(R.string.aviso_gps)
                myCategory = categorias?.get(position)

                EcommerceViewModel.getRetrofitEcommerceList(myCategory.toString()) //Realizo el parseo

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                EcommerceViewModel.getRetrofitEcommerceList(myCategory.toString()) //Realizo el parseo

            }
        }


        //Botón que optiene la dirección mediante GPS de nuestro Smartphone
        findViewById<View>(R.id.imGPS).setOnClickListener {

            EcommerceViewModel.gps(applicationContext, this)

            if (EcommerceViewModel.getMiUbicacion() != null) {
                tvLoad?.visibility = View.INVISIBLE
                progressBar?.visibility = View.INVISIBLE
                edDireccion!!.setText(EcommerceViewModel.getMiDireccion())
                btResult?.visibility = View.VISIBLE


            } else {
                dialogNoGPS()
            }
        }

        //Botón que muestra una lista de todos los eComercios, filtrados por categoría y distancia al Smartphone
        btResult?.setOnClickListener {

            if (EcommerceViewModel.getMiUbicacion() == null) {
                tvLoad?.visibility = View.VISIBLE
                progressBar?.visibility = View.VISIBLE
                edDireccion!!.setText(R.string.aviso_gps)
            } else {
                btResult?.visibility = View.VISIBLE
                val intent = Intent(applicationContext, ResultActivity::class.java)
                startActivity(intent)
            }
        }
    }

    //Dialog por si el GPS está apagado
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