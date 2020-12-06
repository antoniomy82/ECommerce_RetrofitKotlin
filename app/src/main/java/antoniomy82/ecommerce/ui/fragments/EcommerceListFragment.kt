package antoniomy82.ecommerce.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import antoniomy82.ecommerce.R
import antoniomy82.ecommerce.ui.RecyclerViewAdapter


class EcommerceListFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    var adapter: RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>? = null
    private var manager: RecyclerView.LayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ecommerces_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar_main)
        toolbar.title = "  Listado generado"
        toolbar.setLogo(R.drawable.ico_personal)

        recyclerView =
            view.findViewById(R.id.rvCategory) as RecyclerView//Aquí definimos dónde tenemos la vista del recyclerView (fragmentList)
        manager = LinearLayoutManager(activity) //Mostrar como LinearLayout
        recyclerView?.layoutManager = manager
        adapter = activity?.let { RecyclerViewAdapter(it) }
        recyclerView?.adapter = adapter
    }

}