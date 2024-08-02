package com.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.bilibili.R

class PermissionActivity : AppCompatActivity() {

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        if (!checkPermission(Manifest.permission.CAMERA)) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
            Toast.makeText(this, "申请权限", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "已经有权限", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限申请成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "权限申请失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}