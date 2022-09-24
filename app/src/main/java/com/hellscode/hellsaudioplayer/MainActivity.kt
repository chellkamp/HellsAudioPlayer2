package com.hellscode.hellsaudioplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hellscode.util.WaitSplash

class MainActivity : AppCompatActivity() {
    private var w: WaitSplash? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        w = WaitSplash(this, R.drawable.ic_wait)

        w!!.start()

        val t = Thread(
            {
                Thread.sleep(5000)

                runOnUiThread{w!!.stop()}
            }
        )
        t.start()

    }
}