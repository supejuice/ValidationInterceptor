package com.example.jsonsemantics


import com.google.gson.annotations.SerializedName

data class CatFactsItem(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("deleted")
    val deleted: Boolean,
    @SerializedName("_id")
    val id: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("status")
    val status: Status,
    @SerializedName("text")
    val text: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("used")
    val used: Boolean,
    @SerializedName("user")
    val user: String,
    @SerializedName("__v")
    val v: Int
)