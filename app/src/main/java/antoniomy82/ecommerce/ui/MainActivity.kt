package antoniomy82.ecommerce.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import antoniomy82.ecommerce.R
import antoniomy82.ecommerce.databinding.ActivityMainBinding
import antoniomy82.ecommerce.viewmodel.EcommerceViewModel

/**
 *  Creado por Antonio Javier Morales Yáñez on 23/08/2020
 *  Github : https://github.com/antoniomy82
 *  Likedin: https://www.linkedin.com/in/antonio-javiermorales-yáñez-85a96b137/
 *  email: antoniomy82@gmail.com
 */

//Clase que funciona como controlador del proyecto
class MainActivity : AppCompatActivity() {

    private var ecommerceViewModel: EcommerceViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Creo el bindeo
        val activityMainBinding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(activityMainBinding.root) //Asigno el contenido a la vista, osea el Binding

        //Creo liveData
        ecommerceViewModel = ViewModelProvider(this).get(EcommerceViewModel::class.java)

        //Asigno Lifedata al modelo del binding
        activityMainBinding.model = ecommerceViewModel  //SetModel

        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "  eCommerce"
        toolbar.setLogo(R.drawable.ico_personal)
        setSupportActionBar(toolbar)

        setupMainBinding(activityMainBinding)
    }

    private fun setupMainBinding(activityMainBinding: ActivityMainBinding) {

        ecommerceViewModel?.setMainActivityContextBinding(
            applicationContext,
            this,
            activityMainBinding
        ) //Paso el contexto y el binding a activity Main

        //LiveData
        ecommerceViewModel?.callCategoriesList()

        ecommerceViewModel?.getCategoriesList()?.observe(this, { categoriesList ->
            val spinnerCategory =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesList)
            activityMainBinding.spCategory.adapter = spinnerCategory

            activityMainBinding.progressBar.visibility = View.GONE
            activityMainBinding.tvLoad.visibility = View.GONE

            ecommerceViewModel?.setCategoriesList(categoriesList)
        })

        //Paso los datos obtenidos al Companion Object
        ecommerceViewModel?.getEcommercesList()?.observe(this, { ecommerceList ->
            EcommerceViewModel.setEcommercesListCompanion(ecommerceList)
            ecommerceViewModel?.setEcommercesList(ecommerceList)
        })
    }

}