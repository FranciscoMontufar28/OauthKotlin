package com.practice.francisco.autenticacionoauth

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    var fsq: Foursquare? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fsq = Foursquare(this)

        val bLoguear = findViewById<Button>(R.id.btn_logear)

        if(fsq?.hayToken()!!){
            startActivity(Intent(this, SegundaActivity::class.java))
            finish()
        }else{
            fsq?.iniciarSecion()
        }

        bLoguear.setOnClickListener{
            fsq?.iniciarSecion()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fsq?.validadActivityResult(requestCode, resultCode, data)
    }
}
