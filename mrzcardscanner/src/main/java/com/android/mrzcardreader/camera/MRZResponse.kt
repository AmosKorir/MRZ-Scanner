package com.android.mrzcardreader.camera

import com.android.mrzcardreader.camera.models.IdData


interface MRZResponse{

    fun cardResponse(card: IdData)

    fun cardReadResponse()

    fun failedToRead()
}