package com.android.mrzcardreader.camera

import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.android.mrzcardreader.CardResult
import com.android.mrzcardreader.camera.models.IdData
import com.android.mrzcardreader.camera.overlay.TextGraphic
import com.android.mrzcardreader.camera.overlay.TextOverlay
import com.android.mrzcardreader.cardconnectors.CardConnector
import com.android.mrzcardscanner.R
import com.android.mrzcardscanner.databinding.ActivityMrzBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ImageAnalyzer(
    private val textOverlay: TextOverlay,
    private val activityMainBinding: ActivityMrzBinding,
    private val bottomSheetBehavior: BottomSheetBehavior<*>,
    private val cardResult: CardResult
) : ImageAnalysis.Analyzer, MRZResponse {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val textBlocks: MutableList<Text.TextBlock> = ArrayList()
    private var scannerRunning = true

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        mediaImage?.let {

            Log.d("imageProxy", "analyze: " + imageProxy.imageInfo.rotationDegrees)
            detectImageContent(InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees))
                .addOnSuccessListener { results ->
                    onSuccess(
                        results,
                        getImageCropRect(imageProxy)
                    )
                    imageProxy.close()
                }
                .addOnFailureListener {
                    onFailure(it)
                    imageProxy.close()
                }
        }
    }

    private fun onFailure(it: Exception) {
        it.printStackTrace()
    }

    private fun onSuccess(results: Text?, cropRect: Rect?) {
        results?.let { result ->

            val blocks = result.textBlocks

            if (blocks.isNotEmpty()) {
                cropRect?.let { imageRect ->

                    val textGraphics = blocks.map { block ->
                        TextGraphic(
                            textOverlay.context,
                            imageRect,
                            textBlock = blocks.last(),
                            textOverlay
                        )
                    }
                    val h = textGraphics.filter { textGraphic -> textGraphic.isValidTextBlock() }
                    textBlocks.clear()
                    textOverlay.add(textGraphics)
                    textBlocks.addAll(blocks)

                    val rows = textBlocks.last().text.split("\n")
                    try {
                        if (scannerRunning) {
                            CardConnector.onDetailsCaptured(textBlocks, this)

                        } else {
                            CardConnector.clear()
                        }

                    } catch (e: java.lang.Exception) {

                    }
                }
            } else {
                textOverlay.clear()
            }
        }
    }

    private fun minimizeCard() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun maximizedCard() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

    }

    private fun getImageCropRect(imageProxy: ImageProxy): Rect {
        val rotation = imageProxy.imageInfo.rotationDegrees
        if (rotation == 90 || rotation == 270) {
            return Rect(0, 0, imageProxy.height, imageProxy.width)
        }
        return Rect(0, 0, imageProxy.width, imageProxy.height)
    }

    private fun detectImageContent(image: InputImage): Task<Text> {
        return recognizer.process(image)
    }

    override fun cardResponse(card: IdData) {
        updateCardDetails(card)
        maximizedCard()
    }

    private fun updateCardDetails(idData: IdData) {
        scannerRunning = false
        val rootView = activityMainBinding.cardLayout.rootView
        rootView.findViewById<TextView>(R.id.fNameTv).text = idData.firstName
        rootView.findViewById<TextView>(R.id.lNameTv).text = idData.lastName
        rootView.findViewById<TextView>(R.id.nameTv).text = idData.middleName
        rootView.findViewById<TextView>(R.id.genderTv).text = idData.gender
        rootView.findViewById<TextView>(R.id.idTv).text = idData.idNo
        rootView.findViewById<TextView>(R.id.docNoTv).text = idData.documentNo
        rootView.findViewById<TextView>(R.id.docTypeTv).text = "ID"
        rootView.findViewById<TextView>(R.id.dobTv).text = idData.dateOfBirth

        rootView.findViewById<Button>(R.id.rescanBtn).setOnClickListener {
            minimizeCard()
            scannerRunning = true
        }
        rootView.findViewById<Button>(R.id.confirmBtn).setOnClickListener {
            cardResult.cardDetails(idData)
        }

    }

    override fun cardReadResponse() {
    }

    override fun failedToRead() {
    }
}