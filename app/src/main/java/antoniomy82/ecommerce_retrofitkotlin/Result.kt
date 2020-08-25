package antoniomy82.ecommerce_retrofitkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Result : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var adapter: AdaptadorRecyclerView? = null
    private var manager: RecyclerView.LayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        //Cargo todos los parámetros de recyclerView en mi AdaptadorRecyclerView
        manager = LinearLayoutManager(this) //Mostrar como LinearLayout

        recyclerView = findViewById(R.id.rvCategory) //Aquí definimos dónde tenemos la vista del recyclerView XML
        recyclerView?.layoutManager = manager
        adapter = AdaptadorRecyclerView(this, MainActivity?.getEcommerceList()) //lista linearLayout o grid para grid Layout
        recyclerView?.adapter = adapter
    }
}