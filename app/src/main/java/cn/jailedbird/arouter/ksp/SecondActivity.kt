package cn.jailedbird.arouter.ksp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SecondActivity<T> : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_main)
    }
}