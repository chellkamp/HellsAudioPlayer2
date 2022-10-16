package com.hellscode.hellsaudioplayer

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hellscode.hellsaudioplayer.catalog.LocalCatalog
import com.hellscode.util.permission.FragmentPermRequestWrapper
import com.hellscode.util.ui.WaitSplash
import com.hellscode.util.ui.WaitSplashHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SongListFragment: Fragment() {

    companion object {
        fun createInstance(): SongListFragment {
            return SongListFragment()
        }
    }

    //Activity callbacks.
    private val filePermForLoadReq: FragmentPermRequestWrapper =
        FragmentPermRequestWrapper(this) { canLoadData(it) }

    @Inject lateinit var localCatalog: LocalCatalog

    private var _waitSplash: WaitSplash? = null

    private lateinit var _rvList: RecyclerView
    private lateinit var _listAdapter: AllSongsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _waitSplash = (activity as WaitSplashHolder?)?.waitSplash
    }

    override fun onDetach() {
        _waitSplash = null
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.fragment_song_list, container, false)
        _rvList = v.findViewById(R.id.rv_list)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _listAdapter = AllSongsAdapter(requireContext())

        _rvList.layoutManager = LinearLayoutManager(requireContext())
        _rvList.adapter = _listAdapter

        filePermForLoadReq.requestPermissionIfNotGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    /**
     * After we check for file access permissions, we end up here to examine the result.
     * If the result is good, then we proceed with loading.
     */
    private fun canLoadData(isGranted: Boolean?) {
        if (isGranted == true) {
            // begin data load
            _waitSplash?.show()

            CoroutineScope(Dispatchers.Default).launch {
                val data: List<LocalCatalog.SongEntry> = localCatalog.allSongs()

                MainScope().launch {

                    // attach data to recyclerview adapter
                    _listAdapter.data = data

                    _waitSplash?.hide()
                    Toast.makeText(requireContext(), "Is it safe yet?", Toast.LENGTH_LONG).show()
                }
            }

        }
    }
}