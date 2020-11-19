package antoniomy82.ecommerce.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import antoniomy82.ecommerce.R
import antoniomy82.ecommerce.viewmodel.EcommerceViewModel

/**
 *  Creado por Antonio Javier Morales Yáñez on 25/08/2020
 *  Github : https://github.com/antoniomy82
 *  Likedin: https://www.linkedin.com/in/antonio-javiermorales-yáñez-85a96b137/
 *  email: antoniomy82@gmail.com
 */

class ResultActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerViewAdapter? = null
    private var manager: RecyclerView.LayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "  Listado generado"
        toolbar.setLogo(R.drawable.ico_personal)
        setSupportActionBar(toolbar)

        //Cargo todos los parámetros de recyclerView en mi RecyclerViewAdapter
        manager = LinearLayoutManager(this) //Mostrar como LinearLayout

        recyclerView =
            findViewById(R.id.rvCategory) //Aquí definimos dónde tenemos la vista del recyclerView XML
        recyclerView?.layoutManager = manager

        adapter = RecyclerViewAdapter(this, EcommerceViewModel.getEcommerceList())
        recyclerView?.adapter = adapter

    }
}