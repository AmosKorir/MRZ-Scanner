package com.android.mrzcardreader.camera.models

import java.io.Serializable

data class CardDocument(
    var firstName: String,
    var middleName: String,
    var lastName: String,
    var gender:String,
    var documentNo:String,
    var dateOfBirth:String,
    var idNo: String,
    var nationality:String,
    var documentType:String
):Serializable