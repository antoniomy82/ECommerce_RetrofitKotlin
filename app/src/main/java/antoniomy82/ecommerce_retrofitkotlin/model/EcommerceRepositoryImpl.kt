package antoniomy82.ecommerce_retrofitkotlin.model

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EcommerceRepositoryImpl : EcommerceRepository {

    override fun getEcommerces(myCategory: String): ArrayList<Ecommerce> {

        val ecommerceList = ArrayList<Ecommerce>() //Inicializo

        ApiAdapter().api?.getAllEcommerces()?.enqueue(object : Callback<List<Ecommerce>> {

            override fun onResponse(
                call: Call<List<Ecommerce>>?,
                response: Response<List<Ecommerce>>?
            ) {
                val comercios = response?.body()

                for (i in 0 until comercios?.size!!) {
                    if (myCategory == (comercios[i].category.toString())) {
                        ecommerceList.add(
                            Ecommerce(
                                comercios[i].shortDescription,
                                comercios[i].name,
                                comercios[i].category,
                                comercios[i].latitude,
                                comercios[i].longitude,
                                comercios[i].address,
                                comercios[i].contact,
                                comercios[i].social,
                                comercios[i].logo
                            )
                        )
                    }
                }
            }

            override fun onFailure(call: Call<List<Ecommerce>>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
        return ecommerceList
    }
}