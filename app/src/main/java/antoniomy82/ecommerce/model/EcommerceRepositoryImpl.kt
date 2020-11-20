package antoniomy82.ecommerce.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EcommerceRepositoryImpl : EcommerceRepository {

    private var categoriesList = MutableLiveData<List<String>>()
    private var ecommerceList = MutableLiveData<ArrayList<Ecommerce>>()

    override fun callEcommerces(myCategory: String) {

        val parseList = ArrayList<Ecommerce>() //Inicializo

        ApiAdapter().api?.getAllEcommerces()?.enqueue(object : Callback<List<Ecommerce>> {

            override fun onResponse(
                call: Call<List<Ecommerce>>?,
                response: Response<List<Ecommerce>>?
            ) {
                val comercios = response?.body()


                for (i in 0 until comercios?.size!!) {
                    if (myCategory == (comercios[i].category.toString())) {
                        parseList.add(
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
                ecommerceList.value = parseList
            }

            override fun onFailure(call: Call<List<Ecommerce>>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    override fun callCategoriesList() {

        val categories = mutableListOf<String>()
        val currentCall: Call<List<Ecommerce>>? = ApiAdapter().api?.getAllEcommerces()

        currentCall?.enqueue(object : Callback<List<Ecommerce>> {

            override fun onResponse(
                call: Call<List<Ecommerce>>?,
                response: Response<List<Ecommerce>>?
            ) {

                if (response?.isSuccessful!!) {
                    val comercios = response.body()

                    for (i in 0 until comercios?.size!!) {
                        if (comercios[i].category.toString() != "null") {
                            categories.add(comercios[i].category.toString())
                        }
                    }

                    categoriesList.value = categories.distinct()
                    Log.d("callCategoriesList", categoriesList.value.toString())
                }
            }

            override fun onFailure(call: Call<List<Ecommerce>>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    override fun getCategoriesList(): MutableLiveData<List<String>> {
        return categoriesList
    }

    override fun getEcommerces(): MutableLiveData<ArrayList<Ecommerce>> {
        return ecommerceList
    }
}