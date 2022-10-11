package com.hellscode.hellsaudioplayer.catalog

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalCatalog @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        private val queryProjection: Array<String> = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA
        )

        private val querySelection = "${MediaStore.Audio.Media.IS_MUSIC} <> 0"

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

            val idx_id: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val idx_title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idx_artist: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val idx_album: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val idx_dataPath: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            do {
                val curEntry: SongEntry = SongEntry()

                if (idx_id != -1) {
                    curEntry.id = cursor.getInt(idx_id)
                }

                if (idx_title != -1) {
                    curEntry.title = cursor.getString(idx_title) ?: ""
                }

                if (idx_artist != -1) {
                    curEntry.artist = cursor.getString(idx_artist) ?: ""
                }

                if (idx_album != -1) {
                    curEntry.album = cursor.getString(idx_album) ?: ""
                }

                if (idx_dataPath != -1) {
                    curEntry.dataPath = cursor.getString(idx_dataPath) ?: ""
                }

                retVal.add(curEntry)
            } while(cursor.moveToNext())
        }

        if (cursor != null) {
            cursor.close()
        }

        return retVal
    }

    /**
     * Represents a song entry from the media database
     */
    class SongEntry internal constructor() {
        private var _id: Int = -1
        private var _title: String = ""
        private var _artist: String = ""
        private var _album: String = ""
        private var _dataPath: String = ""

        var id: Int
        get() = _id
        internal set(value: Int) { _id = value}

        var title: String
        get() = _title
        internal set(value: String) { _title = value }

        var artist: String
        get() = _artist
        internal set(value: String) { _artist = value }

        var album: String
        get() = _album
        internal set(value: String) { _album = value }

        var dataPath: String
        get() = _dataPath
        internal set(value: String) { _dataPath = value }
    }

}