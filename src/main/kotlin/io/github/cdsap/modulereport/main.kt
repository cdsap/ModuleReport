package io.github.cdsap.modulereport

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import io.github.cdsap.geapi.client.model.ClientType
import io.github.cdsap.geapi.client.model.Filter
import io.github.cdsap.geapi.client.network.ClientConf
import io.github.cdsap.geapi.client.network.GEClient
import io.github.cdsap.geapi.client.repository.impl.GradleRepositoryImpl
import io.github.cdsap.modulereport.report.ModuleReport
import kotlinx.coroutines.runBlocking


fun main(args: Array<String>) {
    ModuleReport().main(args)
}

class ModuleReport : CliktCommand() {
    private val apiKey: String by option().required()
    private val url by option().required()
    private val maxBuilds by option().int().default(1000).check("max builds to process 50000") { it <= 50000 }
    private val project: String? by option().required()
    private val tags: List<String> by option().multiple(default = emptyList())
        .check("tags can't be empty") { it.isNotEmpty() }
    private val concurrentCalls by option().int().default(150)
    private val concurrentCallsCache by option().int().default(10)
    private val requestedTask by option().required()
    private val includeFailedBuilds by option().flag(default = true)
    private val user: String? by option()
    private val sinceBuildId: String? by option()
    private val taskType by option()
    private val exclusiveTags by option().flag(default = true)

    override fun run() {
        val filter = Filter(
            maxBuilds = maxBuilds,
            project = project,
            tags = tags.map { it.replaceFirst("not:", "!") },
            user = user,
            concurrentCalls = concurrentCalls,
            includeFailedBuilds = includeFailedBuilds,
            requestedTask = requestedTask,
            concurrentCallsConservative = concurrentCallsCache,
            sinceBuildId = sinceBuildId,
            exclusiveTags = exclusiveTags,
            clientType = ClientType.CLI
        )

        val repository = GradleRepositoryImpl(
            GEClient(
                apiKey, url, ClientConf(
                    maxRetries = 300,
                    exponentialBase = 1.0,
                    exponentialMaxDelay = 5000
                )
            )
        )

        val cacheRepository = GradleRepositoryImpl(
            GEClient(
                apiKey, url, ClientConf(
                    maxRetries = 100,
                    exponentialBase = 1.0,
                    exponentialMaxDelay = 10000
                )
            )
        )

        runBlocking {
                ModuleReport(filter, repository, cacheRepository).process()
        }
    }
}
