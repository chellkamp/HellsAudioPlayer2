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
import android.support.v4.media.MediaBrowserCompat
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.hellscode.hellsaudioplayer.media.SongUtil

class AllSongsAdapter(private val context: Context): RecyclerView.Adapter<AllSongsAdapter.ViewHolder>() {

    private val _mmr: MediaMetadataRetriever = MediaMetadataRetriever()

    private var _data: List<MediaBrowserCompat.MediaItem> = emptyList()

    /**
     * source data
     */
    var data: List<MediaBrowserCompat.MediaItem>
        get() = _data
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            _data = value
            notifyDataSetChanged()
        }

    /**
     * Get number of data items
     */
    override fun getItemCount(): Int = _data.size

    /**
     * Called whenever it's time to create a new view holder
     * @param parent container that view should be configured to go into
     * @param viewType number representing view type to use.  This argument is ignored.
     * @return created view holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.li_song, parent, false)
        return ViewHolder(itemView)
    }

    /**
     * Called when it's time to bind data for the given position
     * to a view holder.
     * @param holder view holder
     * @param position index of data to bind into holder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, _mmr, data[position])
    }

    /**
     * Holds view data for a song entry
     * @param itemView view to be wrapped
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
         * @param context context
         * @param mmr retriever to be used when loading thumbnails (version 28 and below)
         * @param data song data to be bound to this view holder
         */
        fun bind(context: Context, mmr: MediaMetadataRetriever, data: MediaBrowserCompat.MediaItem) {
            _titleView.text = SongUtil.getTitle(data)
            _artistView.text = SongUtil.getArtist(data)
            _albumView.text = SongUtil.getAlbum(data)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                bindArtworkV29(context, data)
            } else {
                bindArtworkOld(context, mmr, data)
            }

        }

        /**
         * Retrieves and binds album artwork
         * for the given music item represented by data
         * @param context context
         * @param data song data
         */
        @RequiresApi(Build.VERSION_CODES.Q)
        private fun bindArtworkV29(context: Context, data: MediaBrowserCompat.MediaItem) {
            val artDimenPx: Int = context.resources.getDimensionPixelSize(R.dimen.song_art)
            val albumPath: Uri = ContentUris.withAppendedId(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, SongUtil.getAlbumId(data)
            )
            val b: Bitmap = context.contentResolver.loadThumbnail(
                albumPath, Size(artDimenPx, artDimenPx), null
            )

            _albumArtView.setImageBitmap(b)
        }

        /**
         * For versions before 29, retrieves and binds album artwork
         * for the given music item represented by data
         * @param context context
         * @param mmr used for thumbnail retrieval
         * @param data song data
         */
        private fun bindArtworkOld(context: Context, mmr: MediaMetadataRetriever, data: MediaBrowserCompat.MediaItem) {
            mmr.setDataSource(SongUtil.getDataPath(data)?.path ?: "")
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
