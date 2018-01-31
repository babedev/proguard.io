package com.github.babedev.proguard

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import java.io.File

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") {
            call.respondText("Hi", ContentType.Text.Html)
        }

        get("/{group}/{artifact}") {
            val group = call.parameters["group"]
            val artifact = call.parameters["artifact"]

            val proguardFolder = File("data/$group/$artifact")

            val result = if (proguardFolder.exists() && proguardFolder.isDirectory) {
                proguardFolder
                        .listFiles()
                        .sortedByDescending { it.name }
                        .firstOrNull()?.let {
                    it.listFiles().firstOrNull()?.readText() ?: {
                        "This library does not need Proguard or it is not exist in our system yet"
                    }()
                } ?: { "This library does not need Proguard or it is not exist in our system yet" }()
            } else "This library does not need Proguard or it is not exist in our system yet"

            call.respondText(result, ContentType.Text.Plain)
        }

        get("/{group}/{artifact}/{version}") {
            val group = call.parameters["group"]
            val artifact = call.parameters["artifact"]
            val version = call.parameters["version"]

            val proguardFolder = File("backend/data/$group/$artifact/$version")

            val result = if (proguardFolder.exists() && proguardFolder.isDirectory) {
                proguardFolder.listFiles().firstOrNull()?.readText() ?: {
                    "This library does not need Proguard or it is not exist in our system yet"
                }()
            } else "This library does not need Proguard or it is not exist in our system yet"

            call.respondText(result, ContentType.Text.Plain)
        }
    }
}