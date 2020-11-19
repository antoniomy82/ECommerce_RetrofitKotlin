package antoniomy82.ecommerce.model

interface EcommerceRepository {

    fun getEcommerces(myCategory: String): ArrayList<Ecommerce>
    fun getCategoriesList(): List<String>?
}