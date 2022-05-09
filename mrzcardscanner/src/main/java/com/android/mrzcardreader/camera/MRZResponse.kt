package com.android.mrzcardreader.camera

import com.android.mrzcardreader.camera.models.CardDocument


interface MRZResponse{

    fun cardResponse(card: CardDocument)

    fun cardReadResponse()

    fun failedToRead()
}