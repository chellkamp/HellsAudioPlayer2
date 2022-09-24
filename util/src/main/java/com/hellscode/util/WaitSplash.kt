package com.hellscode.util

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

class WaitSplash(activity: FragmentActivity, imageResourceId: Int = 0) {
    private val _activity: FragmentActivity = activity
    private val _imageResourceId: Int = imageResourceId

    private var _startCount: Int = 0
    private val _lockObj = Object()
    private var _dlg: SplashDialog? = null

    fun start() {
        synchronized(_lockObj) {
            if (_startCount == 0) {
                // throw up the dialog fragment
                _dlg = SplashDialog(_imageResourceId)
                _dlg!!.show(_activity.supportFragmentManager, null)
            }
            ++_startCount
        }
    }

    fun stop() {
        synchronized(_lockObj) {
            if (_startCount == 1) {
                // close the dialog fragment
                _dlg!!.dismiss()
            }

            if (_startCount > 0) {
                --_startCount
            }
        }
    }

    class SplashDialog(imageResourceId: Int): DialogFragment() {
        private val _imageResourceId = imageResourceId

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_wait)
                .setCancelable(false).create()
        }

        override fun onStart() {
            super.onStart()
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val iv: AppCompatImageView = dialog!!.findViewById(R.id.wait_dlg_img_src) as AppCompatImageView

            if (_imageResourceId != 0) {
                val drawable: Drawable? = ResourcesCompat.getDrawable(requireContext().resources, _imageResourceId, null)
                iv.setImageDrawable(drawable)
            }

            val animation: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)
            iv.animation = animation
            animation.start()
        }

    }// end class SplashDialog
}