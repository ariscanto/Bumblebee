package com.iot.bumblebee.ui

import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.iot.bumblebee.R

class ActivityShowLogin : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var currentUser: FirebaseUser? = null
    lateinit var circularReveal: CircularReveal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_login)

        val view = findViewById<View>(R.id.layoutShowSplash)
        circularReveal = CircularReveal(view)
        circularReveal.onActivityCreate(intent)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser

        userData()
        Handler().postDelayed({
            startActivity(Intent(this, ActivityBumblebee::class.java))
            finish()
        }, 3550)
    }


    fun userData() {
        val userName = findViewById<TextView>(R.id.name)
        userName.text = currentUser!!.displayName
        shakeItBaby()
    }

    private fun  shakeItBaby(){
        if(Build.VERSION.SDK_INT >= 26){
            (applicationContext.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(
                VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE))
        }else{
            (applicationContext.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(250)
        }
    }


}
