package com.fabyosk.rully.data.interfaces

interface IAction {
    val name: String
    val description: String
    fun execute()
//    fun getDescription()
}