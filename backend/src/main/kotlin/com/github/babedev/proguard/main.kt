package com.github.babedev.proguard

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.File

fun main(args: Array<String>) {

    val server = embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respondText("Hi", ContentType.Text.Html)
            }

            get("/{group}/{artifact}") {
                val group = call.parameters["group"]
                val artifact = call.parameters["artifact"]

                val proguardFolder = File("backend/data/$group/$artifact")

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

    server.start(true)
}