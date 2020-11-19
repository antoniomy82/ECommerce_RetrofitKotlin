
package antoniomy82.ecommerce.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import antoniomy82.ecommerce.R
import antoniomy82.ecommerce.model.Ecommerce
import antoniomy82.ecommerce.viewmodel.EcommerceViewModel
import com.squareup.picasso.Picasso

/**
 *  Creado por Antonio Javier Morales Yáñez on 25/08/2020
 *  Github : https://github.com/antoniomy82
 *  Likedin: https://www.linkedin.com/in/antonio-javiermorales-yáñez-85a96b137/
 *  email: antoniomy82@gmail.com
 */

class DetailActivity : AppCompatActivity() {

    private var miEcommerce: Ecommerce?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "  Detalle eComercio"
        toolbar.setLogo(R.drawable.ico_personal)
        setSupportActionBar(toolbar)

        //Definimos los enlaces con activity_detail
        val imDlogo: ImageView? = findViewById(R.id.imLogo)
        val tvDnombre: TextView? = findViewById(R.id.tv_Dnombre)
        val tvDshortdescription: TextView? = findViewById(R.id.tv_DshortDescription)
        val tvDdireccion: TextView? = findViewById(R.id.tv_Ddireccion)
        val tvDemail: TextView? = findViewById(R.id.tv_Demail)
        val tvDtelefono: TextView? = findViewById(R.id.tv_Dtelefono)

        this.miEcommerce = EcommerceViewModel.getEcommerce(intent.getIntExtra("miIndice", 0))
        val direccion: String =
            miEcommerce!!.address!!.street + ", " + miEcommerce!!.address!!.zip + " , " + miEcommerce!!.address!!.city + "," + miEcommerce!!.address!!.country

        tvDnombre!!.text = miEcommerce!!.name
        tvDshortdescription!!.text = miEcommerce!!.shortDescription
        tvDdireccion!!.text = direccion
        tvDemail!!.text = miEcommerce!!.contact!!.email
        tvDtelefono!!.text = miEcommerce!!.contact!!.phone

        //Cargo el logo almacenado con Picasso
        if ((miEcommerce?.logo?.url) != null) {
            Picasso.get().load(miEcommerce!!.logo!!.url).placeholder(R.drawable.nologo)
                .into(imDlogo)

        } else {
            Picasso.get().load(R.drawable.noimage).into(imDlogo)
        }

        onClickListeners() //Cargo todos los onClickListeners

    }


    //Funcion que contiene todos los setOnClickListener de este activity

    fun onClickListeners() {

        //Cuando se haga click el icono de email, abrirá un cliente de correo  con la dirección que tenemos guardada
        // Nota: si no existe dirección de mail, la dejará en blanco.
        findViewById<View>(R.id.imEmail).setOnClickListener {
            val miEmail =
                arrayOf<String>(miEcommerce!!.contact!!.email) //Tengo que hacer este cast raro si o si, sino no me aparece email
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, miEmail)
                putExtra(Intent.EXTRA_SUBJECT, " Soy Antonio J Morales -->Contráteme Xdd ")
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        //Cuando se haga click sobre el icono del telefono, lanzará las llamadas (DIAL).
        // Nota: El teléfono puede no corresponder a un abonado o ser erroneo
        findViewById<View>(R.id.imTelefono).setOnClickListener {
            if (miEcommerce?.contact?.phone != "" || miEcommerce?.contact?.phone != null) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + miEcommerce?.contact?.phone)

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "No hay teléfono en ese campo",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //Cuando se haga click sobre icono maps, abrirá google maps y mostrará la ubicación del eComercio.
        //Nota: Si la dirección está mal introducida, busca la ubicación más coincidente.
        findViewById<View>(R.id.imMaps).setOnClickListener {
            if (miEcommerce?.myLocation != null) {
                val intentUri =
                    Uri.parse("geo:" + miEcommerce?.latitude + "?z=16&q=" + miEcommerce?.longitude + "(" + miEcommerce?.address?.street + "," + miEcommerce?.address?.city + "," + miEcommerce?.address?.country + ")")
                val intent = Intent(Intent.ACTION_VIEW, intentUri)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "No hay dirección o esta en formato incorrecto",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //Cuando se haga click sobre el icono twitter, abrirá la cuenta web de twitter
        //Nota: La cuenta de twitter puede estar mal introducida o no existir.
        findViewById<View>(R.id.imTwitter).setOnClickListener {
            val miUri: String? = miEcommerce?.social?.twitter
            if (URLUtil.isValidUrl(miUri)) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(miUri)))
            } else {
                Toast.makeText(
                    applicationContext,
                    "No hay Twitter o enlace erroneo",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //Cuando se haga click sobre el icono instagram, abrirá la cuenta web de instagram
        //Nota: La cuenta de instagram puede estar mal introducida o no existir.
        findViewById<View>(R.id.imInstagram).setOnClickListener {
            val miUri: String? = miEcommerce?.social?.instagram
            if (URLUtil.isValidUrl(miUri)) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(miUri)))
            } else {
                Toast.makeText(
                    applicationContext,
                    "No hay Instagram o enlace erroneo",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //Cuando se haga click sobre el icono facebook, abrirá la cuenta web de facebook
        //Nota: La cuenta de facebook puede estar mal introducida o no existir.
        findViewById<View>(R.id.imFB).setOnClickListener {
            val miUri: String? = miEcommerce?.social?.facebook
            if (URLUtil.isValidUrl(miUri)) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(miUri)))
            } else {
                Toast.makeText(
                    applicationContext,
                    "No hay Facebook o enlace erroneo",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}
