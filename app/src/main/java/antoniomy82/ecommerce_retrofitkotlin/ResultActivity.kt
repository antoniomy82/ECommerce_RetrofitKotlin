package antoniomy82.ecommerce_retrofitkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *  Creado por Antonio Javier Morales Yáñez on 25/08/2020
 *  Github : https://github.com/antoniomy82
 *  Likedin: https://www.linkedin.com/in/antonio-javiermorales-yáñez-85a96b137/
 *  email: antoniomy82@gmail.com
 */

class ResultActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var adapter: AdaptadorRecyclerView? = null
    private var manager: RecyclerView.LayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        //Cargo todos los parámetros de recyclerView en mi AdaptadorRecyclerView
        manager = LinearLayoutManager(this) //Mostrar como LinearLayout

        recyclerView =
            findViewById(R.id.rvCategory) //Aquí definimos dónde tenemos la vista del recyclerView XML
        recyclerView?.layoutManager = manager
        adapter = AdaptadorRecyclerView(
            this,
            MainActivity.getEcommerceList()
        ) //lista linearLayout o grid para grid Layout
        recyclerView?.adapter = adapter
    }
}