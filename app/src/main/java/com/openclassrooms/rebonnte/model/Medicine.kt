package com.openclassrooms.rebonnte.model

import java.util.UUID

data class Medicine(
    var name: String,
    var stock: Int,
    var nameAisle: String,
    var histories: List<History>,
    var id: String = UUID.randomUUID().toString()
) {
    constructor() : this("", 0, "", emptyList(), UUID.randomUUID().toString())
}
