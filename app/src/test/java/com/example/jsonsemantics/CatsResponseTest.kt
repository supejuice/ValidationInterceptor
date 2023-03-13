package com.example.jsonsemantics

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jsonsemantics.lib.ValidationInterceptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

@RunWith(MockitoJUnitRunner::class)
class CatsResponseTest {

    var errorCount = 0

    private val logger: (path: String, message: String) -> Unit = { path, message ->
        errorCount++
        println("$path $message")
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(ValidationInterceptor(logger))
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            //.baseUrl("https://cat-fact.herokuapp.com/")
            .baseUrl("https://cat-fact.herokuapp.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()


    private val catsService by lazy { retrofit.create(CatsService::class.java) }

    private val mockWebServer by lazy { MockWebServer() }


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockWebServer.start()
    }

    @Test
    fun `fetch details validate`() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("cats_response.json").content)
        mockWebServer.enqueue(response)
        // Act
        CoroutineScope(Dispatchers.Default).launch {
            catsService.getFacts()
        }
        assert(errorCount != 0)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

}