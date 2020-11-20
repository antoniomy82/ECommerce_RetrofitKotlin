package antoniomy82.ecommerce.model

import androidx.lifecycle.MutableLiveData

interface EcommerceRepository {

    fun callEcommerces(myCategory: String)
    fun callCategoriesList()
    fun getCategoriesList(): MutableLiveData<List<String>>
    fun getEcommerces(): MutableLiveData<ArrayList<Ecommerce>>
}