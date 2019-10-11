/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.jobs

import android.app.job.JobParameters
import android.app.job.JobService
import ch.deletescape.lawnchair.feed.JobScope
import kotlinx.coroutines.launch

@Suppress("MapGetWithNotNullAssertionOperator")
class JobSchedulerService : JobService() {
    override fun onStartJob(params: JobParameters) = synchronized(JobSchedulerService) {
        if (idCallbacks[params.jobId] != null) {
            JobScope.launch {
                idCallbacks[params.jobId]!! {
                    jobFinished(params, it)
                }
            }
            return@synchronized true
        }
        return@synchronized false
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }

    companion object {
        val idCallbacks: Map<Int, (finished: (reschedule: Boolean) -> Unit) -> Unit> = mutableMapOf()
    }
}