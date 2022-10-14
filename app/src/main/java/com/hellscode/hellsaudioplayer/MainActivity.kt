package com.hellscode.hellsaudioplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hellscode.util.ui.WaitSplash
import com.hellscode.util.ui.WaitSplashHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), WaitSplashHolder {

    private var _waitSplash: WaitSplash = WaitSplash(this, R.drawable.ic_wait)

    override val waitSplash: WaitSplash get() = _waitSplash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportFragmentManager.findFragmentById(R.id.all_songs_container) == null) {
            supportFragmentManager
                .beginTransaction().add(R.id.all_songs_container, SongListFragment.createInstance())
                .commit()
        }
    }

}