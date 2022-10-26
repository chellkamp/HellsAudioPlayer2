package com.hellscode.hellsaudioplayer.service

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.hellscode.hellsaudioplayer.R
import com.hellscode.hellsaudioplayer.catalog.LocalCatalog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService: MediaBrowserServiceCompat() {
    companion object {
        private const val ROOT_ID = "root"
        private const val ALL_ID = "all"
        private const val LOG_TAG = "MusicSvc"
    }

    private val _localCatalog: LocalCatalog = LocalCatalog(this)

    private var _mediaSession: MediaSessionCompat? = null
    private lateinit var _stateBuilder: PlaybackStateCompat.Builder

    override fun onCreate() {
        super.onCreate()

        _mediaSession = MediaSessionCompat(this, LOG_TAG)

        _stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PLAY_PAUSE
            )

        // initialize state/inputs of media session
        _mediaSession!!.setPlaybackState(_stateBuilder.build())
        _mediaSession!!.setCallback(Callback())
        sessionToken = _mediaSession!!.sessionToken

    }

    /**
     * Get root
     */
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return MediaBrowserServiceCompat.BrowserRoot(ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        var resultList: MutableList<MediaBrowserCompat.MediaItem>? = null

        when(parentId) {
            ROOT_ID -> {
                resultList = mutableListOf() // something's going in here, time to initialize.

                val desc: MediaDescriptionCompat = MediaDescriptionCompat.Builder()
                    .setMediaId(ALL_ID)
                    .setTitle(baseContext.resources.getString(R.string.category_all_title))
                    .setDescription(baseContext.resources.getString(R.string.category_all_description))
                    .build()

                val mediaItem: MediaBrowserCompat.MediaItem = MediaBrowserCompat.MediaItem(
                    desc,
                    MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
                )

                resultList.add(mediaItem)
            }

            ALL_ID -> {
                // implement use of catalog.allSongs()
                // Left off following https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice.
                // Also, consider whether we need to re-do where access
                // permissions are requested.
                TODO("song list")
            }
        }

        result.sendResult(resultList)
    }


    class Callback: MediaSessionCompat.Callback() {

    }

}