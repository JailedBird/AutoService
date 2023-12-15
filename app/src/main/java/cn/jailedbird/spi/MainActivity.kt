package cn.jailedbird.spi

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.jailedbird.spi.app.R
import cn.jailedbird.spi.test.TestInterface1
import cn.jailedbird.spi.test.TestInterface3
import java.util.ServiceLoader

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.test1).setOnClickListener {
            val service = ServiceLoader.load(TestInterface1::class.java).firstOrNull()
            if (service != null) {
                Toast.makeText(this, "已加载", Toast.LENGTH_SHORT).show()
            }

            service?.hello()
        }

        findViewById<Button>(R.id.test3).setOnClickListener {
            val loader  = ServiceLoader.load(TestInterface3::class.java)
            loader.forEach {service->
                Toast.makeText(this, "已加载 $service :#${service.hashCode()}", Toast.LENGTH_SHORT).show()
                service.hello()
            }

        }
    }
}