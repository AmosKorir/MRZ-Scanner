package com.android.mrzcardreader

import com.android.mrzcardreader.camera.models.CardDocument

 interface CardDetailResponse {
    fun onCardRead(card: CardDocument)

    fun onCardReadingCancelled()

    fun onFailed(e: Exception)
}