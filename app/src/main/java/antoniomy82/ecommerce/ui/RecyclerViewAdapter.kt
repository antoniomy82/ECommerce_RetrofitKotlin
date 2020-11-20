package antoniomy82.ecommerce.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import antoniomy82.ecommerce.R
import antoniomy82.ecommerce.model.Ecommerce

/**
 *  Creado por Antonio Javier Morales Yáñez on 24/08/2020
 *  Github : https://github.com/antoniomy82
 *  Likedin: https://www.linkedin.com/in/antonio-javiermorales-yáñez-85a96b137/
 *  email: antoniomy82@gmail.com
 */

class RecyclerViewAdapter(var context: Context, ecommercesList: ArrayList<Ecommerce>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    var ecommercesList: ArrayList<Ecommerce>? = null

    init {
        this.ecommercesList = ecommercesList
    }


    //Aquí es dónde vamos a crear o inflar la vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val miContentView: View =
            LayoutInflater.from(context).inflate(R.layout.recyclerview_item_lista, parent, false)

        println("Create View Holder: $viewType")


        return ViewHolder(miContentView) //Devolvemos el ViewHolder que hemos creado debajo, con la View que acabamos de inflar
    }

    //Asignamos los datos a cada elemento de la lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ecommerce = ecommercesList?.get(position) // Cargamos los elementos
        val miViewHolder = holder as ViewHolder?

        val direccion: String =
            ecommerce?.address?.street + " , " + ecommerce?.address?.city + " , (" + ecommerce?.address?.country + ")"
        val distancia: String = "Distancia: " + String.format(
            "%.2f",
            (ecommerce?.distance)
        ) + " Km"  //Convierto a KM con 2 decimales
        val categoria: String? = ecommerce?.category

        if (miViewHolder != null) {
            miViewHolder.tvNombre.text = ecommerce?.name

            if (!direccion.isBlank()) {
                miViewHolder.tvDireccion.text = direccion
            }
            if (!distancia.isBlank()) {
                miViewHolder.tvDistancia.text = distancia
            }
            if (!categoria.isNullOrBlank()) {
                miViewHolder.tvCategoria.text = categoria
            }
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

    }

    override fun getItemCount(): Int {

        return ecommercesList?.size ?: 0
    }


    //Definimos nuestro holder, en base a los campos que tenemos en nuestros "item"
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        var tvDireccion : TextView = itemView.findViewById(R.id.tvDireccion)
        var tvDistancia : TextView = itemView.findViewById(R.id.tvDistancia)
        var tvCategoria : TextView = itemView.findViewById(R.id.tvCategoria)
    }

}