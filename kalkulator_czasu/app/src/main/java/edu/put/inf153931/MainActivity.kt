package edu.put.inf153931

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val calcDataButton = findViewById<Button>(R.id.calc_data_button)
        calcDataButton.setOnClickListener{
            val intent = Intent (this, DataActivity::class.java)
            startActivity(intent)
        }
        val calcHMSButton = findViewById<Button>(R.id.calc_hms_button)
        calcHMSButton.setOnClickListener{
            val intent = Intent (this, HMSActivity::class.java)
            startActivity(intent)
        }
    }
}