
package antoniomy82.ecommerce_retrofitkotlin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso


class DetailActivity : AppCompatActivity() {

    private var miEcommerce: Ecommerce?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //Definimos los enlaces con activity_detail
        var im_Dlogo : ImageView? =findViewById(R.id.imLogo)
        val tv_Dnombre: TextView? =findViewById(R.id.tv_Dnombre)
        val tv_Dcategoria: TextView? =findViewById(R.id.tv_Dcategoria)
        val tv_Ddireccion: TextView? =findViewById(R.id.tv_Ddireccion)
        val tv_Demail: TextView? =findViewById(R.id.tv_Demail)
        val tv_Dtelefono: TextView? =findViewById(R.id.tv_Dtelefono)
        //val im_maps: ImageView? =findViewById(R.id.imMaps)


        miEcommerce=MainActivity.getEcommerce(intent.getIntExtra("miIndice",0))
        val direccion:String =miEcommerce!!.address!!.street+ ", " + miEcommerce!!.address!!.zip+ " , "+miEcommerce!!.address!!.city+ ","+miEcommerce!!.address!!.country

        //im_Dlogo!!.setImageResource(miEcommerce!!.logo!!.url)
        tv_Dnombre!!.text=miEcommerce!!.name
        tv_Dcategoria!!.text=miEcommerce!!.category
        tv_Ddireccion!!.text=direccion
        tv_Demail!!.text=miEcommerce!!.contact!!.email
        tv_Dtelefono!!.text=miEcommerce!!.contact!!.phone


        if((miEcommerce?.logo?.url)!=null) {
            Picasso.get().load(miEcommerce!!.logo!!.url).placeholder(R.drawable.nologo).into(im_Dlogo)
            Log.d("Dentro Picasso", "Se pasa por el carajo isNotEmpty")
        }
        else{
            Picasso.get().load(R.drawable.noimage).into(im_Dlogo)
        }

        findViewById<View>(R.id.imMaps).setOnClickListener {
            if (miEcommerce?.myLocation != null) {
                val intentUri = Uri.parse("geo:"+miEcommerce?.latitude+"?z=16&q="+miEcommerce?.longitude+"("+miEcommerce?.address?.street+","+miEcommerce?.address?.city+","+miEcommerce?.address?.country+")")
                val intent = Intent(Intent.ACTION_VIEW, intentUri)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No hay dirección o esta en formato incorrecto", Toast.LENGTH_LONG).show()
            }
        }


        findViewById<View>(R.id.imTwitter).setOnClickListener {

            val miUri: String? = miEcommerce?.social?.twitter
            if(URLUtil.isValidUrl(miUri)){
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(miUri)))
            }
            else{
                Toast.makeText(applicationContext, "No hay Twitter o enlace erroneo", Toast.LENGTH_LONG).show()
            }
        }

        findViewById<View>(R.id.imInstagram).setOnClickListener {
            val miUri:String? =miEcommerce?.social?.instagram
            if(URLUtil.isValidUrl(miUri)){
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(miUri)))
            }
            else{
                Toast.makeText(applicationContext, "No hay Instagram o enlace erroneo", Toast.LENGTH_LONG).show()
            }
        }

        findViewById<View>(R.id.imFB).setOnClickListener {
            val miUri:String? =miEcommerce?.social?.facebook
            if(URLUtil.isValidUrl(miUri)){
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(miUri)))
            }
            else{
                Toast.makeText(applicationContext, "No hay Facebook o enlace erroneo", Toast.LENGTH_LONG).show()
            }
        }

    }
}
