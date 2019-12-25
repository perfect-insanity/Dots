package com.example.dots

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DescActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desc)

        supportActionBar?.apply {
            title = getString(R.string.desc)
        }
    }
}