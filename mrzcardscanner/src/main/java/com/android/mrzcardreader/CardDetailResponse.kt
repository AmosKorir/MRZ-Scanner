package com.android.mrzcardreader

import com.android.mrzcardreader.camera.models.IdData

 interface CardDetailResponse {
    fun onCardRead(card: IdData)

    fun onCardReadingCancelled()

    fun onFailed(e: Exception)
}