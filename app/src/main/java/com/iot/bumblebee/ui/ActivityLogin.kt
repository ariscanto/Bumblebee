package com.iot.bumblebee.ui


import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import androidx.cardview.widget.CardView
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.iot.bumblebee.R

class ActivityLogin : AppCompatActivity() {
    private lateinit var userMail: EditText
    private lateinit var userPassword: EditText
    private lateinit var btnLogin: CardView
    private lateinit var loginProgress: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val linkRegisterUser = findViewById<View>(R.id.linkCadastro) as TextView
        linkRegisterUser.setOnClickListener {
            val builder = CircularReveal.Builder(
                this,
                linkRegisterUser,
                Intent(this, RegisterActivity::class.java),
                1000
            ).apply {
                revealColor = ContextCompat.getColor(
                    this@ActivityLogin,
                    R.color.black_500
                )
            }
            CircularReveal.presentActivity(builder)
        }
        userMail = findViewById(R.id.editTextTextEmailAddress)
        userPassword = findViewById(R.id.editTextTextPasswordAdress)
        btnLogin = findViewById(R.id.btnLogin)
        loginProgress = findViewById(R.id.progressBar)
        mAuth = FirebaseAuth.getInstance()
        loginProgress.visibility = View.INVISIBLE
        btnLogin.setOnClickListener(View.OnClickListener {
            loginProgress.visibility = View.VISIBLE
            //loginProgress.indeterminateDrawable.setColorFilter(0xFFFF0000.toInt(), android.graphics.PorterDuff.Mode.MULTIPLY);
            setColorFilter(loginProgress.indeterminateDrawable, ResourcesCompat.getColor(applicationContext.resources, R.color.white, null))
            btnLogin.visibility = View.VISIBLE
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
                        R.color.black_500
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

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
}