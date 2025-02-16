package com.openclassrooms.rebonnte.model

import java.util.UUID

data class Aisle(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var mapUrl: String? = "https://img.leboncoin.fr/api/v1/lbcpb1/images/38/44/27/384427e9153618a1f642af772923916ce6e96743.jpg?rule=ad-image"
) {

}
