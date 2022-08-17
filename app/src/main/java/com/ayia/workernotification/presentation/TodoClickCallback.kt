package com.ayia.workernotification.presentation

import com.ayia.workernotification.domain.Todo

interface TodoClickCallback {

    fun onClick(todo: Todo, position: Int)

    fun onLongClick(todo: Todo, position: Int) : Boolean{
       return false
    }

}