package com.android.mrzcardreader

import com.android.mrzcardreader.camera.models.IdData

 interface CardResult {
    fun cardDetails(card:IdData)
}