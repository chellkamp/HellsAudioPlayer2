package com.hellscode.hellsaudioplayer

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.hellscode.hellsaudioplayer.catalog.LocalCatalog

class AllSongsAdapter(private val context: Context): RecyclerView.Adapter<AllSongsAdapter.ViewHolder>() {

    private val _mmr: MediaMetadataRetriever = MediaMetadataRetriever()

    private var _data: List<LocalCatalog.SongEntry> = emptyList()

    var data: List<LocalCatalog.SongEntry>
        get() = _data
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            _data = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = _data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.li_song, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, _mmr, data[position])
    }

    /**
     * Holds view data for a song entry
     */
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private var _itemView: View
        private var _titleView: TextView
        private var _artistView: TextView
        private var _albumView: TextView

        private var _albumArtView: AppCompatImageView

        val title: String
            get() = _titleView.text.toString()

        val artist: String
            get() = _artistView.text.toString()

        val album: String
            get() = _albumView.text.toString()

        init {
            _itemView = itemView
            _titleView = itemView.findViewById(R.id.song_title)
            _artistView = itemView.findViewById(R.id.song_artist)
            _albumView = itemView.findViewById(R.id.song_album)
            _albumArtView = itemView.findViewById(R.id.artwork)
        }

        /**
         * Bind song data to a view
         */
        fun bind(context: Context, mmr: MediaMetadataRetriever, data: LocalCatalog.SongEntry) {
            _titleView.text = data.title
            _artistView.text = data.artist
            _albumView.text = data.album

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                bindArtworkV29(context, data)
            } else {
                bindArtworkOld(context, mmr, data)
            }

        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun bindArtworkV29(context: Context, data: LocalCatalog.SongEntry) {
            val artDimenPx: Int = context.resources.getDimensionPixelSize(R.dimen.song_art)
            val albumPath: Uri = ContentUris.withAppendedId(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, data.albumId
            )
            val b: Bitmap = context.contentResolver.loadThumbnail(
                albumPath, Size(artDimenPx, artDimenPx), null
            )

            _albumArtView.setImageBitmap(b)
        }

        private fun bindArtworkOld(context: Context, mmr: MediaMetadataRetriever, data: LocalCatalog.SongEntry) {
            mmr.setDataSource(data.dataPath)
            val bytes: ByteArray? = mmr.embeddedPicture

            if (bytes != null) {
                val artDimenPx: Int = context.resources.getDimensionPixelSize(R.dimen.song_art)
                val bo: BitmapFactory.Options = BitmapFactory.Options()
                bo.outWidth = artDimenPx
                bo.outHeight = artDimenPx

                val b: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                _albumArtView.setImageBitmap(b)
            }
        }

    }
}
