package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.btn_search)
        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку 'Поиск'!", Toast.LENGTH_SHORT).show()
            }
        }
        buttonSearch.setOnClickListener(buttonClickListener)

        val buttonLibrary = findViewById<Button>(R.id.btn_library)
        buttonLibrary.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку 'Медиатека'!", Toast.LENGTH_SHORT).show()
        }

        val buttonSettings = findViewById<Button>(R.id.btn_settings)
        buttonSettings.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку 'Настройки'!", Toast.LENGTH_SHORT).show()
        }
    }
}