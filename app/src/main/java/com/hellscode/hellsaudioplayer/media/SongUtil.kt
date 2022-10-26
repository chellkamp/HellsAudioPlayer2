package com.hellscode.hellsaudioplayer.media

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat

class SongUtil {
    companion object {
        private const val SONG_MEDIA_ID_ROOT = "song"

        private const val EXTRA_ARTIST = "artist"
        private const val EXTRA_ALBUM = "album"
        private const val EXTRA_ALBUM_ID = "album_id"


        private fun songIdToMediaId(id: Long): String {
            return "${SONG_MEDIA_ID_ROOT}/${id}"
        }

        private fun mediaIdToSongId(id: String?): Long {
            var retVal: Long = -1

            if (id != null) {
                val parts: List<String> = id.split("/")

                if (parts[0] == SONG_MEDIA_ID_ROOT) {
                    retVal = parts[1].toLongOrNull() ?: -1
                }
            }

            return retVal
        }

        /**
         * Creates a [MediaBrowserCompat.MediaItem] object that contains song data.
         *
         * @param id ID of song in media database
         * @param title song title
         * @param artist artist name
         * @param album album name
         * @param albumId ID of album in media database
         * @param dataPath URI path of song
         * @return new [MediaBrowserCompat.MediaItem] object
         */
        fun createSongItem(
            id: Long?,
            title: String?,
            artist: String?,
            album: String?,
            albumId: Long?,
            dataPath: String?
        ) : MediaBrowserCompat.MediaItem {
            val extras = Bundle()
            val builder: MediaDescriptionCompat.Builder = MediaDescriptionCompat.Builder()

            if (id != null) {
                builder.setMediaId(songIdToMediaId(id))
            }

            if (title != null) {
                builder.setTitle(title)
            }

            if (artist != null || album != null) {
                builder.setSubtitle("$artist - $album")
            }

            if (artist != null) {
                extras.putString(EXTRA_ARTIST, artist)
            }

            if (album != null) {
                extras.putString(EXTRA_ALBUM, album)
            }

            if (albumId != null) {
                extras.putLong(EXTRA_ALBUM_ID, albumId)
            }

            if (dataPath != null) {
                builder.setMediaUri(Uri.parse(dataPath))
            }

            builder.setExtras(extras)

            return MediaBrowserCompat.MediaItem(builder.build(), FLAG_BROWSABLE or FLAG_PLAYABLE)
        }// end createSongItem()

        /**
         * Get ID used for media service from a [MediaBrowserCompat.MediaItem] object that
         * represents a song.
         * @param item media item to extract from
         * @return ID used for media service
         */
        fun getMediaId(item: MediaBrowserCompat.MediaItem): String? {
            return item.mediaId
        }

        /**
         * Get ID of song in media database from a [MediaBrowserCompat.MediaItem] that
         * represents a song.
         * @param item media item to extract from
         * @return ID of song in media database
         */
        fun getId(item: MediaBrowserCompat.MediaItem): Long {
            return mediaIdToSongId(item.mediaId)
        }

        /**
         * Gets the song title from a [MediaBrowserCompat.MediaItem] that represents a song.
         * @param item media item to extract from
         * @return title
         */
        fun getTitle(item: MediaBrowserCompat.MediaItem): CharSequence {
            return item.description.title ?: ""
        }

        /**
         * Gets the artist name from a [MediaBrowserCompat.MediaItem] object that represents a song.
         * @param item media item to extract from
         * @return artist name
         */
        fun getArtist(item: MediaBrowserCompat.MediaItem): String {
            return item.description.extras?.getString(EXTRA_ARTIST) ?: ""
        }

        /**
         * Gets the album name from a [MediaBrowserCompat.MediaItem] object that represents a song.
         * @param item media item to extract from
         * @return album name
         */
        fun getAlbum(item: MediaBrowserCompat.MediaItem): String {
            return item.description.extras?.getString(EXTRA_ALBUM) ?: ""
        }

        /**
         * Gets the album ID from a [MediaBrowserCompat.MediaItem] object that represents a song.
         * @param item media item to extract from
         * @return album ID
         */
        fun getAlbumId(item: MediaBrowserCompat.MediaItem): Long {
            return item.description.extras?.getLong(EXTRA_ALBUM_ID) ?: -1
        }

        /**
         * Gets the URI path of the song data from a [MediaBrowserCompat.MediaItem] object that
         * represents a song.
         * @param item media item to extract from
         * @return data path
         */
        fun getDataPath(item: MediaBrowserCompat.MediaItem): Uri? {
            return item.description.mediaUri
        }

    }// end companion object


}