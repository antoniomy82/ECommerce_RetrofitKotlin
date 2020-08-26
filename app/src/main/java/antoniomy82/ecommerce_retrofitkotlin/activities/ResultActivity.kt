package antoniomy82.ecommerce_retrofitkotlin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import antoniomy82.ecommerce_retrofitkotlin.R
import antoniomy82.ecommerce_retrofitkotlin.adapters.RecyclerViewAdapter

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

        //Cargo todos los parámetros de recyclerView en mi RecyclerViewAdapter
        manager = LinearLayoutManager(this) //Mostrar como LinearLayout

        recyclerView =
            findViewById(R.id.rvCategory) //Aquí definimos dónde tenemos la vista del recyclerView XML
        recyclerView?.layoutManager = manager

        adapter = RecyclerViewAdapter(this, MainActivity.getEcommerceList())
        recyclerView?.adapter = adapter
    }
}