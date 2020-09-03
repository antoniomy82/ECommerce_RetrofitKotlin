package antoniomy82.ecommerce_retrofitkotlin.viewmodel

import androidx.lifecycle.ViewModel
import antoniomy82.ecommerce_retrofitkotlin.models.Ecommerce

class EcommerceViewModel : ViewModel() {

    companion object {
        var ecommerceList: ArrayList<Ecommerce>? = null

        @JvmName("getEcommerceList1")
        fun getEcommerceList(): ArrayList<Ecommerce>? {
            return ecommerceList
        }

        fun getEcommerce(indice: Int): Ecommerce? {
            return ecommerceList?.get(indice)
        }

        fun getSize(): Int? {
            return ecommerceList?.size
        }

        @JvmName("setEcommerceList1")
        fun setEcommerceList(myList: ArrayList<Ecommerce>) {
            this.ecommerceList = myList
        }
    }
}