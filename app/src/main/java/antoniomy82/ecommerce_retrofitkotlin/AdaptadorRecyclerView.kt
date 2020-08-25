package antoniomy82.ecommerce_retrofitkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

/**
 *  Creado por Antonio Javier Morales Yáñez on 23/08/2020
 *  Puedes descargar el código de mi Github : https://github.com/antoniomy82
 */

class AdaptadorRecyclerView(var context: Context, listaItems: ArrayList<Ecommerce>?) : RecyclerView.Adapter<AdaptadorRecyclerView.ViewHolder>() {

    var listaFull: ArrayList<Ecommerce>?=null
    var listaCopia: ArrayList<Ecommerce>?=null

    //Constructor por parámetros
    init {
        this.listaCopia=listaItems
        listaFull = ArrayList<Ecommerce>(listaItems)
    }
    
    //Aquí es dónde vamos a crear o inflar la vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val miContentView: View =
            LayoutInflater.from(context).inflate(R.layout.recyclerview_item_lista, parent, false) //Aquí cambiamos el tipo de vista item_grid o Item_lista

        println("Create View Holder: $viewType")

        //Barra de inicio
        return ViewHolder(miContentView) //Devolvemos el ViewHolder que hemos creado debajo, con la View que acabamos de inflar
    }

    //Asignamos los datos a cada elemento de la lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ecommerce = listaCopia?.get(position) // Cargamos los elementos
        val miViewHolder = holder as ViewHolder?

        val direccion: String = ecommerce?.address?.street + " , " + ecommerce?.address?.city + " , ("+ ecommerce?.address?.country+")"
        val distancia: String = "Distancia: "+String.format("%.2f",(ecommerce?.distance)?.div(1000)) + " Km"  //Convierto a KM con 2 decimales
        val cateoria : String = "Categoria: "+ ecommerce?.category
        if (miViewHolder != null) {

            miViewHolder.tvNombre.text = ecommerce?.name
            if(!direccion.isNullOrBlank()){miViewHolder.tvDireccion.text = direccion}
            if(!distancia.isNullOrBlank()){miViewHolder.tvDistancia.text = distancia}
            if(!cateoria.isNullOrBlank()){miViewHolder.tvCategoria.text = cateoria}
        }

        //Pongo las celdas pares de otro color
        if (position % 2 == 0) {
            miViewHolder!!.itemView.setBackgroundColor(Color.parseColor("#eeeeee")) //android:background="#FFFFFF"
        } else {
            miViewHolder!!.itemView.setBackgroundColor(Color.parseColor("#FFFFFF")) //"#e6e2d3"
        }


        println("Bind View Holder: $position")


        //Listener cuando clicamos en un item.
        miViewHolder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java) //Activity inicio, activity destino
            intent.putExtra("miIndice", position) //Envío la posición dentro de lista
            context.startActivity(intent)
        }
      //  MainActivity.stopProgressBar()
    }

    override fun getItemCount(): Int {
        return listaCopia!!.size
    }


    //Definimos nuestro holder, en base a los campos que tenemos en nuestros "item"
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        var tvDireccion : TextView = itemView.findViewById(R.id.tvDireccion)
        var tvDistancia : TextView = itemView.findViewById(R.id.tvDistancia)
        var tvCategoria : TextView = itemView.findViewById(R.id.tvCategoria)

    }


}