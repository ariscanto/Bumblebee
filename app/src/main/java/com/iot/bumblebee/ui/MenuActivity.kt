package com.iot.bumblebee.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iot.bumblebee.databinding.MenuActivityBinding

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: MenuActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MenuActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViews()


    }
    private fun initViews(){
        setupListeners()
    }

    private fun setupListeners() {

        binding.tarefas.setOnClickListener {
            val intent = Intent(this, BumblebeeActivity::class.java)
            startActivity(intent)
        }


        binding.corridas.setOnClickListener {
            val intent = Intent(this, SobreActivity::class.java)
            startActivity(intent)
        }

    }

}