package com.example.jsonsemantics

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.jsonsemantics.databinding.ActivityMainBinding
import com.example.jsonsemantics.lib.ValidationInterceptor
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private fun validationLogger(): ValidationInterceptor.Logger =
        object : ValidationInterceptor.Logger {
            override fun log(url: String, jsonPath: String, message: String) {
                Log.i(
                    this.javaClass.simpleName,
                    "; URL = $url; JSON_PATH = $jsonPath ; MSG = $message"
                )
            }
        }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(ValidationInterceptor(validationLogger()))
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

    private val service by lazy {
        retrofit.create(CatsService::class.java)
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        lifecycleScope.launch {
            val fact = service.getFacts().first()
            binding.mainTv.text = fact.text
        }
    }
}