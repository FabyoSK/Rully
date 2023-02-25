package com.fabyosk.rully.data.models

import com.fabyosk.rully.data.interfaces.IAction
import com.fabyosk.rully.data.interfaces.IRule

class Rule(override val name: String): IRule {
    override val actions = mutableListOf<IAction>()

    override fun executeActions() {
        actions.forEach { it.execute() }
    }

    override fun addNewAction(action: IAction) {
        actions.add(action)
    }
}