package com.iot.bumblebee.ui

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract.Events.*
import androidx.appcompat.app.AppCompatActivity
import com.iot.bumblebee.databinding.ActivityBumblebeeBinding
import java.util.*

class RelatorioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBumblebeeBinding
    private lateinit var runnable: Runnable



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBumblebeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAddTask.setOnClickListener {
            startActivity(Intent(this, UltrassonicoActivity::class.java))
        }

    }
}