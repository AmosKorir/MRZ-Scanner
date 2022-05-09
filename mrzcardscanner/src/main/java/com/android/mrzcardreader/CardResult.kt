package com.android.mrzcardreader

import com.android.mrzcardreader.camera.models.CardDocument

 interface CardResult {
    fun cardDetails(card:CardDocument)
}