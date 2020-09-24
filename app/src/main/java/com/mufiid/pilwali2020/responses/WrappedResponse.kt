package com.mufiid.pilwali2020.responses

import com.google.gson.annotations.SerializedName

data class WrappedResponse<T>(
    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: T? = null
)