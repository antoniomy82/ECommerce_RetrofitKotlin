package antoniomy82.ecommerce.model

import android.os.Handler
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EcommerceRepositoryImpl : EcommerceRepository {

    private var categoriesList: List<String>? = null
    var parseCategoriesFinished: String? = "NO"

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

    override fun getCategoriesList(): List<String>? {

        parseCategoriesList()


        if (categoriesList == null && parseCategoriesFinished != "ERROR") {
            val runnable = Runnable {
                getCategoriesList()
                Log.d("Esperando", " ...")
            }
            val handler = Handler()
            handler.postDelayed(runnable, 2000)
        }

        Log.d("Categorias get ", categoriesList.toString())
        return categoriesList

    }

    fun parseCategoriesList() {

        val categories = mutableListOf<String>()
        //val categoriesList = ArrayList<String>() //Inicializo


        ApiAdapter().api?.getAllEcommerces()?.enqueue(object : Callback<List<Ecommerce>> {

            override fun onResponse(
                call: Call<List<Ecommerce>>?,
                response: Response<List<Ecommerce>>?
            ) {

                if (response?.isSuccessful!!) {
                    val comercios = response.body()
                    //  val categories= mutableListOf<String>()

                    for (i in 0 until comercios?.size!!) {
                        //categoriesList[i].add(comercios[i].category.toString())
                        categories.add(comercios[i].category.toString())

                    }

                    categoriesList = categories.distinct()
                    parseCategoriesFinished = "OK"
                } else {
                    parseCategoriesFinished = "ERROR"
                }
            }

            override fun onFailure(call: Call<List<Ecommerce>>?, t: Throwable?) {
                t?.printStackTrace()
                parseCategoriesFinished = "ERROR"
            }
        })
    }
}