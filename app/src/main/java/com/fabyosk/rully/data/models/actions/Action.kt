package com.fabyosk.rully.data.models.actions

import com.fabyosk.rully.data.interfaces.IAction

abstract class Action: IAction {
    abstract override fun execute()
//    abstract override fun getDescription()
}