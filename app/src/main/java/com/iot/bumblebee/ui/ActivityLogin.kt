package com.iot.bumblebee.ui


import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import androidx.cardview.widget.CardView
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.widget.LinearLayout
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.iot.bumblebee.R

class ActivityLogin : AppCompatActivity() {
    private lateinit var userMail: EditText
    private lateinit var userPassword: EditText
    private lateinit var btnLogin: CardView
    private lateinit var loginProgress: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var linearLayoutRedzinho: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val cardView = findViewById<View>(R.id.btnChamaTelaCadastro) as CardView
        cardView.setOnClickListener {
            val builder = CircularReveal.Builder(
                this,
                cardView,
                Intent(this, RegisterActivity::class.java),
                1000
            ).apply {
                revealColor = ContextCompat.getColor(
                    this@ActivityLogin,
                    R.color.purple_500
                )
            }
            CircularReveal.presentActivity(builder)
        }
        linearLayoutRedzinho = findViewById(R.id.linearLayoutRedzinho)
        linearLayoutRedzinho.visibility = View.VISIBLE
        userMail = findViewById(R.id.editTextTextEmailAddress)
        userPassword = findViewById(R.id.editTextTextPasswordAdress)
        btnLogin = findViewById(R.id.btnLogin)
        loginProgress = findViewById(R.id.progressBar)
        mAuth = FirebaseAuth.getInstance()
        loginProgress.visibility = View.INVISIBLE
        btnLogin.setOnClickListener(View.OnClickListener {
            loginProgress.visibility = View.VISIBLE
            btnLogin.visibility = View.INVISIBLE
            val mail = userMail.text.toString()
            val password = userPassword.text.toString()
            if (mail.isEmpty() || password.isEmpty()) {
                showMessage("Verifique os dados e tente novamente")
                btnLogin.visibility = View.VISIBLE
                loginProgress.visibility = View.INVISIBLE
            } else {
                signIn(mail, password)
            }
        })
    }

    private fun signIn(mail: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(mail, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loginProgress.visibility = View.INVISIBLE
                btnLogin.visibility = View.VISIBLE
                val builder = CircularReveal.Builder(
                    this,
                    btnLogin,
                    Intent(this, ActivityShowLogin::class.java),
                    1000
                ).apply {
                    revealColor = ContextCompat.getColor(
                        this@ActivityLogin,
                        R.color.purple_500
                    )
                }
                CircularReveal.presentActivity(builder)
            } else {
                showToastLoginFailure()
                btnLogin.visibility = View.VISIBLE
                loginProgress.visibility = View.INVISIBLE
            }
        }
    }

    private fun showMessage(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    private fun showToastLoginFailure() {
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_LONG
        val customToast = layoutInflater.inflate(R.layout.show_fail, null)
        toast.view = customToast
        toast.show()
    }

    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user != null) {
            val intent = Intent(this, ActivityBumblebee::class.java)
            startActivity(intent)
            finish()
        }
    }
}