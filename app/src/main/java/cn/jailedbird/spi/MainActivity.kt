package cn.jailedbird.spi

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cn.jailedbird.spi.app.R

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.jump).setOnClickListener {

        }
    }
}