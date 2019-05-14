package com.practice.francisco.autenticacionoauth

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.foursquare.android.nativeoauth.FoursquareOAuth

class Foursquare(var activity: AppCompatActivity){

    private val CODIGO_CONEXION = 200
    private val CODIGO_INTERCAMBIO_TOKEN = 201
    val CLIENT_ID = Util.CLIENT_ID
    val CLIENT_SECRET = Util.CLIENT_SECRET

    private  val SETTINGS = "settings"

    init {

    }

    fun iniciarSecion(){
        val intent = FoursquareOAuth.getConnectIntent(activity.applicationContext, CLIENT_ID)
        if(FoursquareOAuth.isPlayStoreIntent(intent)){
            //Mensaje que no tiene la app
            mensaje("No tienes la app instalada en Foursquare....")
            activity.startActivity(intent)
        }else{
            activity.startActivityForResult(intent,CODIGO_CONEXION)
        }
    }

    fun mensaje(mensaje:String){
        Toast.makeText(activity.applicationContext, mensaje, Toast.LENGTH_LONG).show()
    }

    fun validadActivityResult(requestCode:Int, resultCode:Int, data:Intent?){
        when(requestCode){
            CODIGO_CONEXION ->{conexionCompleta(resultCode, data)}
            CODIGO_INTERCAMBIO_TOKEN->{intercambioTokenCompleta(resultCode, data)}
        }
    }

    fun conexionCompleta(resultCode:Int, data:Intent?){
        val codigoRespuesta = FoursquareOAuth.getAuthCodeFromResult(resultCode, data)
        val exception = codigoRespuesta.exception
        if(exception == null){
            //Autenticacion correcta
            val codigo = codigoRespuesta.code
            realizarIntercambioDeToken(codigo)
        }else{
            mensaje("No se pudo realizar la comunicación. Intentalo mas tarde...")
        }
    }

    private fun realizarIntercambioDeToken(codigo : String){
        val intent = FoursquareOAuth.getTokenExchangeIntent(activity.applicationContext, CLIENT_ID, CLIENT_SECRET, codigo)
        activity.startActivityForResult(intent, CODIGO_INTERCAMBIO_TOKEN)
    }

    private fun intercambioTokenCompleta(resultCode:Int, data:Intent?){
        val respuestaToken = FoursquareOAuth.getTokenFromResult(resultCode, data)
        val exception = respuestaToken.exception

        if (exception == null){
            val accessToken = respuestaToken.accessToken
            guardarToken(accessToken)
            //mensaje("Token: "+accessToken)
            navergarSiguienteActividad()
        }else{
            //problema al obtener el token
            mensaje("No se pudo obtener el token...")
        }
    }

    fun hayToken(): Boolean {
        return obtenerToken() != ""
    }

    fun guardarToken(token:String){
        val settings = activity.getSharedPreferences(SETTINGS, 0)
        val editor = settings.edit()
        editor.putString("accessToken", token)
        editor.commit()
    }

    fun obtenerToken():String{
        val settings = activity.getSharedPreferences(SETTINGS, 0)
        val token = settings.getString("accessToken", "")
        return token
    }

    fun navergarSiguienteActividad(){
        val intentExtra = Intent(activity, SegundaActivity::class.java)
        activity.startActivity(intentExtra)
        activity.finish()
    }
}