package com.hellscode.hellsaudioplayer

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.hellscode.hellsaudioplayer.catalog.LocalCatalog
/*
class AllSongsAdapter(val data: List<LocalCatalog.SongEntry>): RecyclerView.Adapter<AllSongsAdapter.ViewHolder>() {

    override fun getItemCount(): Int = data.size


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private lateinit var _titleView: TextView

        var albumArtView: AppCompatImageView

        var title: String
            get() = _titleView.text.toString()
            set(value) { _titleView.text = value }

        init {
            _titleView = itemView.findViewById(R.id.song_title)
            albumArtView = itemView.findViewById(R.id.artwork)
        }

        fun bind(data: LocalCatalog.SongEntry) {

        }

    }
}

 */