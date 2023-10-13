package io.github.cdsap.modulereport.view

import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table
import io.github.cdsap.modulereport.report.ModuleMetrics

class ModuleStateView(private val moduleMetrics: List<ModuleMetrics>) {

    fun print() {
        printTasks(moduleMetrics)
    }

    private fun printTasks(module: List<ModuleMetrics>) {
        printReport(module)

    }

    private fun printReport(
        module: List<ModuleMetrics>
    ) {
        println(
            table {
                cellStyle {
                    border = true
                    alignment = TextAlignment.MiddleLeft
                    paddingLeft = 1
                    paddingRight = 1
                }
                body {
                    row {
                        cell("Modules") {
                            columnSpan = 4
                            padding = 3
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    moduleMetrics.groupBy {it.module}.toSortedMap().forEach {
                        row {
                            cell(it.key) {
                            }
                            cell(it.value.sumOf { it.duration }) {
                            }
                            cell("") {
                            }
                        }
                    }

                }
            }
        )
    }

}

