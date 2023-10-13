package io.github.cdsap.modulereport.report

import io.github.cdsap.geapi.client.model.Build

data class ModuleMetrics(
    val module: String,
    val outcomes: Map<String,Int>,
    val duration: Long
)

class ModuleStats(
    private val builds: List<Build>,
    private val filterByTaskType: Boolean
) {

    fun get(): List<ModuleMetrics> {
        val moduleMetrics = mutableListOf<ModuleMetrics>()
        builds.forEach {
            it.taskExecution.groupBy { getModule(it.taskPath) }.forEach {
                val module = it.key
                val outcomes = mutableMapOf<String,Int>()
                it.value.groupBy { it.avoidanceOutcome }.forEach {
                    outcomes[it.key] = it.value.size
                }
                moduleMetrics.add(ModuleMetrics(module,outcomes,it.value.sumOf { it.duration }))
            }
        }

        return moduleMetrics
    }

}

fun getModule(path: String) = path.split(":").dropLast(1).joinToString(":")
