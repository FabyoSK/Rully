package com.fabyosk.rully.data.interfaces


interface IRule {
    val name: String
    val actions: List<IAction>
    fun executeActions()
    fun addNewAction(action: IAction)
}