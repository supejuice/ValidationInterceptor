package com.example.jsonsemantics


import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("feedback")
    val feedback: String,
    @SerializedName("sentCount")
    val sentCount: Int,
    @SerializedName("verified")
    val verified: Boolean
)