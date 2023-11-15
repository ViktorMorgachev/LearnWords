package com.learn.worlds.data.remote.ai

import com.learn.worlds.BuildConfig
import com.learn.worlds.utils.Keys
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.http.HttpHeaders
import timber.log.Timber



val ktorHttpClient = HttpClient(OkHttp){

    install(Auth) {
        BearerTokens(Keys.token, Keys.token)
    }

    engine {
        config {
            followRedirects(true)
        }

    }

    install(UserAgent) {
        agent = "Ktor client"
    }

    if (BuildConfig.DEBUG){
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
            filter { request ->
                request.url.host.contains("ktor.io")
            }
           // sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }

    install(ResponseObserver) {
        onResponse { response ->
            Timber.d("Response status: ${response.status.value} timeout: ${System.currentTimeMillis()}")
        }
    }



}