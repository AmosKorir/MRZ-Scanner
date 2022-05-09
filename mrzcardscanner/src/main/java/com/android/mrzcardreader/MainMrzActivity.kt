package com.android.mrzcardreader

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.mrzcardreader.camera.MrzCameraManager
import com.android.mrzcardreader.camera.models.CardDocument
import com.android.mrzcardscanner.databinding.ActivityMrzBinding


class MainMrzActivity : AppCompatActivity(), CardResult {
    private lateinit var viewBinding: ActivityMrzBinding
    private lateinit var cameraManager: MrzCameraManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMrzBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initializeCamera()
        startCamera()

    }

    private fun initializeCamera() {
        cameraManager =
            MrzCameraManager(
                this,
                this,
                viewBinding,
                this
            )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        cameraManager.startCamera()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)

    }

    override fun cardDetails(card: CardDocument) {
        val intent = Intent("MRZ_ACTION")
        intent.putExtra("card", card)
        setResult(505, intent)
        finish()
    }
}