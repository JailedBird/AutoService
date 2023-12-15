package cn.jailedbird.spi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.jailedbird.spi.app.R

class SecondActivity<T> : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_main)
    }
}