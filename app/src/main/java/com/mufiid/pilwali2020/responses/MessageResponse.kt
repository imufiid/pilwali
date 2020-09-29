package com.mufiid.pilwali2020.responses

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("message") var message: String? = null,
    @SerializedName("status") var status: Int? = null
)