package com.hellscode.util.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Wraps up permission request and callback.
 *
 * The object must be created during activity initialization
 */
class FragmentPermRequestWrapper(
    fragment: Fragment,
    callback: ActivityResultCallback<Boolean>
) {
    private val _fragment: Fragment = fragment
    private val _callback: ActivityResultCallback<Boolean> = callback

    private val _launcher: ActivityResultLauncher<String> =
        _fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            callback
        )

    /**
     * Make sure we either have the permission or tried to ask the user for permission
     * before running the callback.
     *
     * @param permission name of needed permission
     */
    fun requestPermissionIfNotGranted(permission: String) {
        val context: Context? = _fragment.context

        if (context == null) {
            MainScope().launch {
                _callback.onActivityResult(null)
            }
        } else if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            MainScope().launch {
                _callback.onActivityResult(true)
            }
        } else {
            _launcher.launch(permission)
        }
    }
}
