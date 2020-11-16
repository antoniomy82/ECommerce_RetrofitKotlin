package antoniomy82.ecommerce_retrofitkotlin.model

interface EcommerceRepository {

    fun getEcommerces(myCategory: String): ArrayList<Ecommerce>
}