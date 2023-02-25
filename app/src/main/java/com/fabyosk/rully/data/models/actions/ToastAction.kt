package com.fabyosk.rully.data.models.actions

import android.content.Context
import android.widget.Toast

class ToastAction(private val message: String, private val context: Context,
): Action() {
    override val name: String = "Toast Action"
    override var description: String = "Test description"

    override fun execute() {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }



}