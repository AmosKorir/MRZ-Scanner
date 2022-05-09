package com.android.mrzcardreader

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import com.android.mrzcardreader.camera.models.CardDocument

class MrzBuilder(
    private val context: Context,
    registry: ActivityResultRegistry
) {
    private var cardResponse: CardDetailResponse? = null

    private var content: ActivityResultLauncher<Intent> =
        registry.register("card", ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 505) {
                val intent: Intent? = result.data
                intent?.let {
                    try {
                        val card = it.extras?.getSerializable("card") as CardDocument
                        cardResponse?.onCardRead(card)
                    } catch (e: Exception) {
                        cardResponse?.onFailed(e)
                    }

                }
            }else if (result.resultCode == Activity.RESULT_CANCELED){
                cardResponse?.onCardReadingCancelled()
            }
        }

    fun setOnCardDetailsResponse(cardDetailResponse: CardDetailResponse) = apply {
        this.cardResponse = cardDetailResponse
    }

    fun start() = apply {
        if(cardResponse == null){
            throw (java.lang.Exception("Please set card response"))
        }
        content.launch(
            Intent(
                context,
                MainMrzActivity::class.java
            )
        )

    }


}