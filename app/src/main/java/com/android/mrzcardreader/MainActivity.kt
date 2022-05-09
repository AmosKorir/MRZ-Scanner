package com.android.mrzcardreader

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.mrzcardreader.camera.MrzCameraManager
import com.android.mrzcardreader.camera.models.CardDocument
import com.android.mrzcardreader.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var cameraManager: MrzCameraManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.scanButton.setOnClickListener {
            openScanner()
        }

    }

    private fun openScanner() {
        MrzBuilder(this, this.activityResultRegistry)
            .setOnCardDetailsResponse(object : CardDetailResponse {
                override fun onCardRead(card: CardDocument) {
                    Toast.makeText(this@MainActivity, card.toString(), Toast.LENGTH_SHORT).show()

                }

                override fun onCardReadingCancelled() {
                    Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_SHORT).show()
                }

                override fun onFailed(e: Exception) {
                    Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()
                }

            })
            .start()
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
                    this, "Permissions not granted by the user.",
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
}