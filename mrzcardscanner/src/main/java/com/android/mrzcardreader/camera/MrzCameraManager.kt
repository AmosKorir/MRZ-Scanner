package com.android.mrzcardreader.camera

import android.content.Context
import android.util.Log
import android.view.Surface
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.android.mrzcardreader.CardResult
import com.android.mrzcardscanner.databinding.ActivityMrzBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MrzCameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val viewBinding: ActivityMrzBinding,
    private val cardResult: CardResult
) {
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var preview: Preview
    private var findView: PreviewView = viewBinding.viewFinder
    private var cardLayout = viewBinding.cardLayout
    private var textOverLay = viewBinding.textOverLay
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    init {
        createNewExecutor()
        val bottomSheet = viewBinding.cardLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)


        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun createNewExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val viewport = findView.viewPort
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor, ImageAnalyzer(
                        textOverLay,
                        viewBinding,
                        bottomSheetBehavior,
                        cardResult
                    )
                )
            }

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetRotation(Surface.ROTATION_0)
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()


            try {

                cameraProvider.unbindAll()

                preview = Preview.Builder().build()


                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageCapture,
                    preview,
                    imageAnalyzer,
                )

                preview.setSurfaceProvider(findView.surfaceProvider)

            } catch (exc: Exception) {
                Log.e("k", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }

}