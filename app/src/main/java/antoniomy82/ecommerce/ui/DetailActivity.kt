
package antoniomy82.ecommerce.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import antoniomy82.ecommerce.R
import antoniomy82.ecommerce.databinding.ActivityDetailBinding
import antoniomy82.ecommerce.viewmodel.EcommerceViewModel

/**
 *  Creado por Antonio Javier Morales Yáñez on 25/08/2020
 *  Github : https://github.com/antoniomy82
 */

class DetailActivity : AppCompatActivity() {

    private var ecommerceViewModel: EcommerceViewModel? = null
    private var activityDetailBinding: ActivityDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Creo el bindeo
        activityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setContentView(activityDetailBinding?.root) //Asigno el contenido a la vista, osea el Binding

        ecommerceViewModel = ViewModelProvider(this).get(EcommerceViewModel::class.java)

        activityDetailBinding?.detailVm =
            ecommerceViewModel  //SetModel - asigno LiveData al modelo del binding

        ecommerceViewModel?.setDetailBinding(activityDetailBinding!!, this)
    }
}
