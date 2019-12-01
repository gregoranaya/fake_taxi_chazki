package com.gregor.anaya.chazki

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.order_layout.*

class OrderActivity : AppCompatActivity(){



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_layout)
        bt_order.setOnClickListener {
            val intent = Intent(this,TripActivity::class.java)
            startActivity(intent)
        }

    }
}

