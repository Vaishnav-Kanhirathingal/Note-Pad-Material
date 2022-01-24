package com.kenetic.materialpad.notepad.dataclass

data class Notes(
    var isAListItem: Boolean,
    var listItemIsChecked: Boolean,
    var content: String
)