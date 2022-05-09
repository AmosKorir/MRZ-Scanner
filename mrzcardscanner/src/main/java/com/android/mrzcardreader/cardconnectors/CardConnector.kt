package com.android.mrzcardreader.cardconnectors

import android.media.AudioManager
import android.media.ToneGenerator
import com.android.mrzcardreader.camera.MRZResponse
import com.android.mrzcardreader.camera.models.CardDocument
import com.google.mlkit.vision.text.Text
import ocr.MrzReader


object CardConnector {
    private val mrzReader = MrzReader()

    fun onDetailsCaptured(details: MutableList<Text.TextBlock>, mrzResponse: MRZResponse) {
        val mrzString = cleanMRZ(details.last().text)

        val cardType = mrzReader.getCardType(mrzString)

        val isValidCard = mrzReader.isValidCard(cardType, mrzString)

        if (!isValidCard) {

            return
        } else {

            kotlin.runCatching {
                val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
            }

            val readCard = mrzReader.readDocument(cardType, mrzString)
            val card = CardDocument(
                readCard.firstName,
                readCard.secondName,
                readCard.lastName,
                readCard.gender,
                readCard.documentNumber,
                readCard.dateOfBirth,
                readCard.id,
                readCard.country,
                readCard.documentType
            )
            mrzResponse.cardResponse(card)
        }
    }

    fun clear() {
        mrzReader.clear()
    }

    private fun cleanMRZ(details_: String): String {
        var details = details_.replace(" ", "")
        details = details.replace("«", "<")
        details = details.replace("e", "<")
        details = details.replace("c", "<")
        details = details.replace("€", "<")
        return details
    }
}