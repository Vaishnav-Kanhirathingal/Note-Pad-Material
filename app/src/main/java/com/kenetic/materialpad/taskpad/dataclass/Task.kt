package com.kenetic.materialpad.taskpad.dataclass

data class Task(
    var isDone: Boolean,
    var task: String,
    val hasReminders: Boolean
    //reminder
)