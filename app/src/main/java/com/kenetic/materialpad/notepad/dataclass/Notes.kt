package com.kenetic.materialpad.notepad.dataclass

data class Notes(
    val isAListItem: Boolean,
    var listItemIsChecked: Boolean,
    var content: String
)