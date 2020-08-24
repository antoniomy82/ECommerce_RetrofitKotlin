package antoniomy82.ecommerce_retrofitkotlin

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 *  Creado por Antonio Javier Morales Yáñez on 23/08/2020
 *  Puedes descargar el código de mi Github : https://github.com/antoniomy82
 */

class AdaptadorRecyclerView(var context: Context, listaItems: ArrayList<Ecommerce>) : RecyclerView.Adapter<AdaptadorRecyclerView.ViewHolder>(), Filterable {

    var listaFull: ArrayList<Ecommerce>?=null
    var listaCopia: ArrayList<Ecommerce>?=null

    //Constructor por parámetros
    init {
        this.listaCopia=listaItems
        listaFull = ArrayList<Ecommerce>(listaItems)
    }
    
    //Aquí es dónde vamos a crear o inflar la vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        lateinit var miContentView: View

        miContentView = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_lista, parent, false) //Aquí cambiamos el tipo de vista item_grid o Item_lista

        println("Create View Holder: $viewType")

        //Barra de inicio
        return ViewHolder(miContentView) //Devolvemos el ViewHolder que hemos creado debajo, con la View que acabamos de inflar
    }

    //Asignamos los datos a cada elemento de la lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ecommerce = listaCopia!![position] // Cargamos los elementos
        val miViewHolder = holder as ViewHolder?

        val direccion: String = ecommerce.address!!.street + " , " + ecommerce.address!!.city + " , ("+ ecommerce.address!!.country+")"
        val distancia: String = "Distancia: "+String.format("%.2f",(ecommerce.distance)?.div(1000)) + " Km"  //Convierto a KM con 2 decimales
        val cateoria : String = "Categoria: "+ ecommerce.category
        if (miViewHolder != null) {
            miViewHolder.tvNombre.text = ecommerce.name
            miViewHolder.tvDireccion.text = direccion
            miViewHolder.tvDistancia.text = distancia
            miViewHolder.tvCategoria.text = cateoria
        }

        //Pongo las celdas pares de otro color
        if (position % 2 == 0) {
            miViewHolder!!.itemView.setBackgroundColor(Color.parseColor("#eeeeee")) //android:background="#FFFFFF"
        } else {
            miViewHolder!!.itemView.setBackgroundColor(Color.parseColor("#FFFFFF")) //"#e6e2d3"
        }


        println("Bind View Holder: $position")

       /*
        //Listener cuando clicamos en un item.
        miViewHolder.itemView.setOnClickListener {
            val intent = Intent(context, ContactoDetalle::class.java) //Activity inicio, activity destino
            intent.putExtra("miIndice", position) //Envío la posición dentro de lista
            context.startActivity(intent)
        }*/
      //  MainActivity.stopProgressBar()
    }

    override fun getItemCount(): Int {
        return listaCopia!!.size
    }


    //Definimos nuestro holder, en base a los campos que tenemos en nuestros "item"
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        var tvDireccion : TextView = itemView.findViewById(R.id.tvDireccion)//ListView
        var tvDistancia : TextView = itemView.findViewById(R.id.tvDistancia)
        var tvCategoria : TextView = itemView.findViewById(R.id.tvCategoria)

    }

    //Barra de búsqueda
    override fun getFilter(): Filter {
        return filtroBusqueda
    }

    //Resultados de busqueda
    private val filtroBusqueda: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<Ecommerce> =
                ArrayList<Ecommerce>()
            if (constraint.isEmpty()) {
                listaFull?.let { filteredList.addAll(it) }
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim { it <= ' ' }

                Log.d("ListaItems", listaFull?.size.toString())

                for (item in listaFull!!) {
                    if (item.name?.toLowerCase(Locale.ROOT)?.contains(filterPattern)!!) {
                        Log.d("Item->", item.name!!)
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            //MainActivity.setEsBusqueda(listaFull) //Activamos un booleano para saber que tipo de carga hay que hacer y hacemos una copia de la lista de contactos
            listaCopia?.clear()
            listaCopia?.addAll(results.values as ArrayList<Ecommerce>)
            notifyDataSetChanged()
        }
    }
}