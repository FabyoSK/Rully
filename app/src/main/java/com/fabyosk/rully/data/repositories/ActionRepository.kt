package com.fabyosk.rully.data.repositories

import android.content.Context
import com.fabyosk.rully.data.models.actions.Action
import com.fabyosk.rully.data.models.actions.ToastAction

class ActionRepository {
    fun getAllActions(context: Context): List<Action> {
        val actionList = mutableListOf<Action>()

        actionList.add(ToastAction("Test", context))

        return actionList
    }
}
