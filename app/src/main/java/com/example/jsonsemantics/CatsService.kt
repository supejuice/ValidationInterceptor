package com.example.jsonsemantics

import retrofit2.http.GET

interface CatsService {
    @GET("facts")
    suspend fun getFacts() : CatFacts
}