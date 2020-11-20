package antoniomy82.ecommerce.model

import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData

class EcommerceObservable : BaseObservable() {

    private var ecommerceRepository: EcommerceRepository = EcommerceRepositoryImpl()

    fun callEcommerces(myCategory: String) {
        return ecommerceRepository.callEcommerces(myCategory)
    }

    fun callCategoriesList() {
        ecommerceRepository.callCategoriesList()
    }

    fun getCategoriesList(): MutableLiveData<List<String>> {
        return ecommerceRepository.getCategoriesList()
    }

    fun getEcommerces(): MutableLiveData<ArrayList<Ecommerce>> {
        return ecommerceRepository.getEcommerces()
    }

}