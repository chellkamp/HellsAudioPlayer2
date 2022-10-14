package com.hellscode.util.permission

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Wraps up permission request and callback.
 *
 * The object must be created during activity initialization
 */
class ActivityPermRequestWrapper(
    activity: AppCompatActivity,
    callback: ActivityResultCallback<Boolean>
){
    private val _activity: AppCompatActivity = activity
    private val _callback: ActivityResultCallback<Boolean> = callback

    private val _launcher: ActivityResultLauncher<String> =
        _activity.registerForActivityResult(
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
        if (ContextCompat.checkSelfPermission(_activity, permission) == PackageManager.PERMISSION_GRANTED) {
            MainScope().launch {
                _callback.onActivityResult(true)
            }
        } else {
            _launcher.launch(permission)
        }
    }
}
