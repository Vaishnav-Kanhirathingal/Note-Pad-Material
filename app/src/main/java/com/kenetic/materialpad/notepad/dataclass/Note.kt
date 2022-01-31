package com.kenetic.materialpad.notepad.dataclass

data class Note(
    var isAListItem: Boolean,
    var listItemIsChecked: Boolean,
    var content: String
)