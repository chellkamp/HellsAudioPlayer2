package com.hellscode.hellsaudioplayer.catalog

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.hellscode.hellsaudioplayer.media.SongUtil

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalCatalog @Inject constructor(@ApplicationContext context: Context) {

    companion object {

        private val queryProjection: Array<String> = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Albums.ALBUM_ID,
            MediaStore.Audio.Media.DATA)

        private const val querySelection = "${MediaStore.Audio.Media.IS_MUSIC} <> 0"

        private val querySortOrder: String = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media._ID).joinToString()

    }

    private val _context = context

    /**
     * Get a collection of all songs on this device, ordered by song title.
     */
    fun allSongs(): ArrayList<MediaBrowserCompat.MediaItem> {
        val retVal = ArrayList<MediaBrowserCompat.MediaItem>()

        val cursor: Cursor? = _context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            queryProjection,
            querySelection,
            null,
            querySortOrder
        )

        if (cursor != null && cursor.moveToFirst()) {
            retVal.ensureCapacity(cursor.count)

            val idxId: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val idxTitle: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idxArtist: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val idxAlbum: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val idxAlbumId: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val idxDataPath: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            var id: Long? = null
            var title: String? = null
            var artist: String? = null
            var album: String? = null
            var albumId: Long? = null
            var dataPath: String? = null

            do {

                if (idxId != -1) {
                    id = cursor.getLong(idxId)
                }

                if (idxTitle != -1) {
                    title = cursor.getString(idxTitle) ?: ""
                }

                if (idxArtist != -1) {
                    artist = cursor.getString(idxArtist) ?: ""
                }

                if (idxAlbum != -1) {
                    album = cursor.getString(idxAlbum) ?: ""
                }

                if (idxAlbumId != -1) {
                    albumId = cursor.getLong(idxAlbumId)
                }

                if (idxDataPath != -1) {
                    dataPath = cursor.getString(idxDataPath) ?: ""
                }

                retVal.add(SongUtil.createSongItem(id, title, artist, album, albumId, dataPath))
            } while(cursor.moveToNext())
        }

        cursor?.close()

        return retVal
    }
}