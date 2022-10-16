package com.hellscode.hellsaudioplayer.catalog

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
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
    fun allSongs(): ArrayList<SongEntry> {
        val retVal = ArrayList<SongEntry>()

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

            do {
                val curEntry = SongEntry()

                if (idxId != -1) {
                    curEntry.id = cursor.getLong(idxId)
                }

                if (idxTitle != -1) {
                    curEntry.title = cursor.getString(idxTitle) ?: ""
                }

                if (idxArtist != -1) {
                    curEntry.artist = cursor.getString(idxArtist) ?: ""
                }

                if (idxAlbum != -1) {
                    curEntry.album = cursor.getString(idxAlbum) ?: ""
                }

                if (idxAlbumId != -1) {
                    curEntry.albumId = cursor.getLong(idxAlbumId)
                }

                if (idxDataPath != -1) {
                    curEntry.dataPath = cursor.getString(idxDataPath) ?: ""
                }

                retVal.add(curEntry)
            } while(cursor.moveToNext())
        }

        cursor?.close()

        return retVal
    }

    /**
     * Represents a song entry from the media database
     */
    class SongEntry internal constructor() {
        var id: Long = -1
            internal set

        var title: String = ""
            internal set

        var artist: String = ""
            internal set

        var album: String = ""
            internal set

        var albumId: Long = -1
            internal set

        var dataPath: String = ""
            internal set
    }

}