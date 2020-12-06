package antoniomy82.ecommerce.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import antoniomy82.ecommerce.R
import antoniomy82.ecommerce.databinding.FragmentMainBinding
import antoniomy82.ecommerce.viewmodel.EcommerceViewModel

class MainFragment : Fragment() {

    private var ecommerceViewModel: EcommerceViewModel? = null
    private var fragmentMainBinding: FragmentMainBinding? = null

    private var lastSelected: Int = 99

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        return fragmentMainBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Creo liveData
        ecommerceViewModel = ViewModelProvider(this).get(EcommerceViewModel::class.java)
        fragmentMainBinding?.mainVm =
            ecommerceViewModel  //SetModel - asigno LiveData al modelo del binding

        setupBindingLiveData(savedInstanceState)
    }


    private fun setupBindingLiveData(savedInstanceState: Bundle?) {

        fragmentMainBinding.let {
            if (it != null) {
                activity?.applicationContext?.let { it1 ->
                    ecommerceViewModel?.setMainActivityContextBinding(
                        it1,
                        activity,
                        it
                    )
                }
            }
        } //Paso el contexto y el binding de activity Main

        if (savedInstanceState != null) {
            lastSelected = savedInstanceState.getInt("positionSpinner", 0)
        }

        ecommerceViewModel?.startCategoriesLoad(lastSelected)

        /*
            LiveData
         */

        //Actualizo el contenido de spinner
        ecommerceViewModel?.getCategoriesList()?.observe(viewLifecycleOwner, { categoriesList ->
            val spinnerCategory = activity?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    categoriesList
                )
            }

            fragmentMainBinding?.spCategory?.adapter = spinnerCategory

            fragmentMainBinding?.progressBar?.visibility = View.GONE
            fragmentMainBinding?.tvLoad?.visibility = View.GONE

            ecommerceViewModel?.setCategoriesList(categoriesList)
        })

        //Paso los datos obtenidos al Companion Object
        ecommerceViewModel?.getEcommercesList()?.observe(viewLifecycleOwner, { ecommerceList ->
            EcommerceViewModel.setEcommercesListCompanion(ecommerceList)
            ecommerceViewModel?.setEcommercesList(ecommerceList)
        })

        ecommerceViewModel?.setSpinnerCategories()

        //Hay que meter un retardo, porque se machaca y no da tiempo hacer setSelection (fallo raro)
        Handler(Looper.getMainLooper()).postDelayed({
            fragmentMainBinding?.spCategory?.setSelection(
                lastSelected,
                true
            )  //last selected category into Spinner (Used to Portrait/Landscape)
        }, 100)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        fragmentMainBinding?.spCategory?.selectedItemPosition?.let {
            outState.putInt("positionSpinner", it)
        }
    }

}


