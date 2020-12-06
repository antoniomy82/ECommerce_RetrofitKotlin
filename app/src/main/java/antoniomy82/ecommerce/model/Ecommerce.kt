package antoniomy82.ecommerce.model

import android.location.Location

/**
 *  Creado por Antonio Javier Morales Yáñez on 23/08/2020
 *  Github : https://github.com/antoniomy82
 */

data class Ecommerce(
    var shortDescription: String? = null,
    var name: String? = null,
    var category: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var address: MyAddress? = null,
    var contact: MyContact? = null,
    var social: MySocial? = null,
    var logo: MyLogo? = null,
    var myLocation: Location? = null,
    var distance: Float? = null
){
    init {
        //Cargo el valor Location para calcular distancias
        if(latitude!=null && longitude!=null) {
            this.myLocation= Location(this.name)
            this.myLocation?.latitude = this.latitude!!
            this.myLocation?.longitude = this.longitude!!
        }
    }
}


//Clases adicionales para hacer la correspondencia con el parseo
class MyAddress(val street: String, val country: String, val city: String, val zip: String)

class MyContact(val email: String, val phone: String)

class MySocial(val twitter: String, val instagram: String, val facebook: String)

class MyLogo(val url: String)

