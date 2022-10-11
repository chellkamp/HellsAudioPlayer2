package com.hellscode.util.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.hellscode.util.R

/**
 * Provides a splash screen with a rotating icon to disable
 * the display while a task is running in the background.
 * Sample usage:
 *   Open load splash before task begins...
 *      val w: WaitSplash(fragActivity, R.id.drawable.myicon)
 *      w.show()
 *
 *   After task is complete...
 *      w.hide()
 *
 * The splash is meant to stay up until hide() is called enough
 * times to match the number of times that show() was called,
 * allowing multiple tasks to share usage until all of those tasks
 * are complete.
 */
@Suppress("unused")
class WaitSplash(activity: FragmentActivity, imageResourceId: Int = 0) {
    private val _activity: FragmentActivity = activity
    private val _imageResourceId: Int = imageResourceId

    private var _startCount: Int = 0
    private val _lockObj = Object()
    private var _dlg: SplashDialog? = null

    /**
     * Display the splash screen, if not already up.
     */
    fun show() {
        synchronized(_lockObj) {
            if (_startCount == 0) {
                // throw up the dialog fragment
                _dlg = SplashDialog(_imageResourceId, _activity)
                _dlg!!.show(_activity.supportFragmentManager, null)
            }
            ++_startCount
        }
    }

    /**
     * Hide the splash screen, if this function has been called enough times to match the calls
     * to show()
     */
    fun hide() {
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

    /**
     * Gets whether the splash screen is visible.
     */
    val isVisible: Boolean
    get() {
        synchronized(_lockObj) {
            return _startCount > 0
        }
    }

    class SplashDialog(@DrawableRes imageResourceId: Int, activity: FragmentActivity): DialogFragment() {
        private val _imageResourceId = imageResourceId
        private val _activity = activity

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val d: Dialog = AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_wait)
                .setCancelable(false).create()

            d.setCanceledOnTouchOutside(false)
            d.window!!.setBackgroundDrawable((ColorDrawable(Color.TRANSPARENT)))

            // we don't want the BACK button to close the dialog.  We just want the event to
            // pass through to the activity containing it.
            d.setOnKeyListener{
                _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    _activity.onBackPressed()
                }
                return@setOnKeyListener true
            }

            return d
        }

        override fun onStart() {
            super.onStart()

            val iv: AppCompatImageView = dialog!!.findViewById(R.id.wait_dlg_img_src) as AppCompatImageView

            if (_imageResourceId != 0) {
                val drawable: Drawable? =
                    ResourcesCompat.getDrawable(requireContext().resources, _imageResourceId, null)
                iv.setImageDrawable(drawable)
            }

            val animation: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)
            iv.animation = animation
            animation.start()
        }

    }// end class SplashDialog
}