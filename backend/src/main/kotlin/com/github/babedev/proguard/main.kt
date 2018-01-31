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

const val NOT_FOUND = "This library does not need Proguard or it is not exist in our system yet"

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

            val resource = ClassLoader.getSystemClassLoader().getResource("data/$group/$artifact")

            if (resource == null) {
                call.respondText(NOT_FOUND, ContentType.Text.Plain)
            } else {
                val proguardFolder = File(resource.file)

                val result = if (proguardFolder.exists() && proguardFolder.isDirectory) {
                    proguardFolder
                            .listFiles()
                            .sortedByDescending { it.name }
                            .firstOrNull()?.let {
                        it.listFiles().firstOrNull()?.readText() ?: {
                            NOT_FOUND
                        }()
                    } ?: { NOT_FOUND }()
                } else NOT_FOUND

                call.respondText(result, ContentType.Text.Plain)
            }
        }

        get("/{group}/{artifact}/{version}") {
            val group = call.parameters["group"]
            val artifact = call.parameters["artifact"]
            val version = call.parameters["version"]

            val proguardFolder = File(ClassLoader.getSystemClassLoader().getResource("data/$group/$artifact/$version").file)

            val result = if (proguardFolder.exists() && proguardFolder.isDirectory) {
                proguardFolder.listFiles().firstOrNull()?.readText() ?: {
                    NOT_FOUND
                }()
            } else NOT_FOUND

            call.respondText(result, ContentType.Text.Plain)
        }
    }
}