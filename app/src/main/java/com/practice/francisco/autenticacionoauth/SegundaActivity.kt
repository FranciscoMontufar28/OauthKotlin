package com.practice.francisco.autenticacionoauth

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson
import com.practice.francisco.autenticacionoauth.RecyclerView.AdaptadorCustom
import com.practice.francisco.autenticacionoauth.RecyclerView.ClickListener
import com.practice.francisco.autenticacionoauth.RecyclerView.LongClickListener


class SegundaActivity : AppCompatActivity() {

    var lista: RecyclerView? = null
    var adaptador: AdaptadorCustom? = null
    var layoutManager: RecyclerView.LayoutManager? = null

    private val permisoFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION
    private val CODIGO_SOLICITUD_PERMISO = 100
    private var fusedLocationClient : FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var callback: LocationCallback? = null


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segunda)

        val fsq = Foursquare(this)
        lista = findViewById(R.id.lista)
        lista?.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(this)
        lista?.layoutManager = layoutManager

        //Toast.makeText(this, fsq.obtenerToken(), Toast.LENGTH_LONG).show()

        fusedLocationClient = FusedLocationProviderClient(this)
        inicializarLocationRequest()

        callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                for (ubicacion in locationResult?.locations!!) {
                    Toast.makeText(
                        applicationContext,
                        ubicacion.latitude.toString() + " " + ubicacion.longitude.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    obtenerLugares(ubicacion.latitude.toString(), ubicacion.longitude.toString(), fsq.obtenerToken()!!)
                }
            }
        }

        obtenerLugares("", "", fsq.obtenerToken()!!)

    }

    @SuppressLint("RestrictedApi")
    private fun inicializarLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest?.interval = 10000
        locationRequest?.fastestInterval = 10000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun validarPermisosUbicacion(): Boolean {
        val hayUbicacionPrecisa =
            ActivityCompat.checkSelfPermission(this, permisoFineLocation) == PackageManager.PERMISSION_GRANTED
        val hayUbicacionOrdinaria =
            ActivityCompat.checkSelfPermission(this, permisoCoarseLocation) == PackageManager.PERMISSION_GRANTED

        return hayUbicacionPrecisa && hayUbicacionOrdinaria
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        fusedLocationClient?.requestLocationUpdates(locationRequest, callback, null)
    }

    private fun pedirPermiso() {
        val deboProveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(this, permisoFineLocation)
        if (deboProveerContexto) {
            //Mensaje con explicacion adicional
            solicitarPermiso()
        } else {
            solicitarPermiso()
        }
    }

    private fun solicitarPermiso() {
        ActivityCompat.requestPermissions(this, arrayOf(permisoFineLocation, permisoCoarseLocation), CODIGO_SOLICITUD_PERMISO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CODIGO_SOLICITUD_PERMISO -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //obtener ubicacion
                    obtenerUbicacion()
                } else {
                    Toast.makeText(this, "No se concedio permiso de ubicación", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun detenerActualizacionUbicacion() {
        fusedLocationClient?.removeLocationUpdates(callback)
    }

    override fun onStart() {
        super.onStart()
        if (validarPermisosUbicacion()) {
            obtenerUbicacion()
        } else {
            pedirPermiso()
        }

    }

    override fun onPause() {
        super.onPause()
        detenerActualizacionUbicacion()
    }

    fun obtenerLugares(lat:String, lon:String, token:String){
        val queue = Volley.newRequestQueue(this)
        //var url = "https://api.foursquare.com/v2/venues/search?ll=40.7484,-73.9857&oauth_token="+"K40EH2T5ICFBYFSK23W0WERC2ULKLLETPR0ULN30JUO2CW2E"+"&v=20190505"
        var url = "https://api.foursquare.com/v2/venues/search?ll="+lat+","+lon+"&oauth_token="+token+"&v=20190505"
        val solicitud = StringRequest(Request.Method.GET, url, Response.Listener<String>{
            response ->
                Log.d("RESPONSE HTTP", response)
            val gson = Gson()
            val venues = gson.fromJson(response, FoursquareRequest::class.java)
            Log.d("VENUES", venues.response?.venues?.size.toString())

            adaptador = AdaptadorCustom(venues.response?.venues!!, object:ClickListener{
                override fun onClick(vista: View, index: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }, object : LongClickListener{
                override fun longClick(vista: View, index: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })

            lista?.adapter = adaptador
        },Response.ErrorListener {

        })
        queue.add(solicitud)
    }
}
