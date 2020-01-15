/*
 * Copyright (C) 2020 Tobias Raatiniemi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.feature.ongoing.view

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.feature.ongoing.model.OngoingUriCommunicator
import me.raatiniemi.worker.feature.shared.view.ongoingBuilder

internal abstract class OngoingNotification(
    val context: Context,
    val project: Project,
    isOngoing: Boolean
) {
    private val dataUri: Uri = OngoingUriCommunicator.createWith(project.id.value)
    private val builder: NotificationCompat.Builder

    @get:DrawableRes
    protected abstract val smallIcon: Int

    protected abstract val shouldUseChronometer: Boolean
    protected abstract val whenForChronometer: Long

    init {
        builder = ongoingBuilder(context)
            .setContentTitle(project.name.value)
            .setContentIntent(buildContentAction())
            .setOngoing(isOngoing)
    }

    private fun buildContentAction(): PendingIntent {
        val arguments = Bundle().apply {
            putLong("projectId", project.id.value)
            putString("projectName", project.name.value)
        }

        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.navigation_graph)
            .setDestination(R.id.navTimeReport)
            .setArguments(arguments)
            .createPendingIntent()
    }

    fun buildIntentWithService(serviceClass: Class<*>): Intent {
        return Intent(context, serviceClass)
            .apply { data = dataUri }
    }

    fun buildPendingIntentWithService(intent: Intent): PendingIntent {
        return PendingIntent.getService(
            context,
            project.id.value.toInt(),
            intent,
            PENDING_INTENT_FLAG
        )
    }

    fun getStringWithResourceId(@StringRes resourceId: Int): String {
        return context.getString(resourceId)
    }

    fun buildWithActions(vararg actions: NotificationCompat.Action): Notification {
        builder.setSmallIcon(smallIcon)

        if (shouldUseChronometer) {
            builder.setWhen(whenForChronometer)
                .setShowWhen(shouldUseChronometer)
                .setUsesChronometer(shouldUseChronometer)
        }

        actions.forEach { builder.addAction(it) }

        return builder.build()
    }

    protected abstract fun build(): Notification

    companion object {
        private const val PENDING_INTENT_FLAG = PendingIntent.FLAG_UPDATE_CURRENT
    }
}
