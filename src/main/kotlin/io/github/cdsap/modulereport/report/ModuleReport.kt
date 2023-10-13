package io.github.cdsap.modulereport.report


import io.github.cdsap.geapi.client.domain.impl.GetBuildsFromQueryWithAttributesRequest
import io.github.cdsap.geapi.client.domain.impl.GetBuildsWithCachePerformanceRequest
import io.github.cdsap.geapi.client.model.Filter
import io.github.cdsap.geapi.client.repository.GradleEnterpriseRepository

import io.github.cdsap.modulereport.view.ModuleStateView

class ModuleReport(
    private val filter: Filter,
    private val repository: GradleEnterpriseRepository,
    private val cacheRepository: GradleEnterpriseRepository
) {

    suspend fun process() {
        val getBuildScans = GetBuildsFromQueryWithAttributesRequest(repository)
        val getOutcome = GetBuildsWithCachePerformanceRequest(cacheRepository)
        val buildScansFiltered = getBuildScans.get(filter)
        val outcome = getOutcome.get(buildScansFiltered, filter).sortedBy { it.buildStartTime }

        if (outcome.isNotEmpty()) {

            val moduleStats =  ModuleStats(outcome, false).get()
            if (moduleStats.isNotEmpty()) {
                ModuleStateView(moduleStats).print()
            }
        }
    }

}
