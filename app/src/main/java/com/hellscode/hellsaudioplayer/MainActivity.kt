package com.hellscode.hellsaudioplayer

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import com.hellscode.hellsaudioplayer.catalog.LocalCatalog
import com.hellscode.util.permission.PermRequestWrapper
import com.hellscode.util.ui.WaitSplash
import com.hellscode.util.ui.WaitSplashHolder
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.processor.internal.definecomponent.codegen._dagger_hilt_android_components_ViewWithFragmentComponent
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), WaitSplashHolder {
    @Inject
    lateinit var localCatalog: LocalCatalog

    private var _waitSplash: WaitSplash = WaitSplash(this, R.drawable.ic_wait)

    override val waitSplash: WaitSplash get() = _waitSplash


    private val permBeforeLoadAllWrapper: PermRequestWrapper = PermRequestWrapper(this){
        isGranted ->
        handlePermBeforeLoadAllResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportFragmentManager.findFragmentById(R.id.all_songs_container) == null) {
            supportFragmentManager
                .beginTransaction().add(R.id.all_songs_container, SongListFragment.createInstance())
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()


        _waitSplash = WaitSplash(this, R.drawable.ic_wait)

        _waitSplash!!.show()

        CoroutineScope(Dispatchers.Default).launch {
            delay(5000)

            MainScope().launch {
                _waitSplash!!.hide()
                permBeforeLoadAllWrapper.requestPermissionIfNotGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

    }

    /**
     * After we check permissions, we end up here to examine the result.
     * If the result is good, then we proceed with loading.
     */

    fun handlePermBeforeLoadAllResult(isGranted: Boolean?) {
        Toast.makeText(this, "Granted: ${isGranted}", Toast.LENGTH_LONG).show()

        if (isGranted ?: false) {
            MainScope().launch {
                val list: List<LocalCatalog.SongEntry> = localCatalog.allSongs()

                var i: Int = 0
                for(item: LocalCatalog.SongEntry in list) {
                    Log.i("CHRIS", "${i} - ${item.dataPath}")
                    ++i
                }
            }
        }
    }
}