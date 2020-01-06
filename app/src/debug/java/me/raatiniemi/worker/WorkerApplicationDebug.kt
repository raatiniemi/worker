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

package me.raatiniemi.worker

import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader

@Suppress("unused")
class WorkerApplicationDebug : WorkerApplication() {
    override fun onCreate() {
        super.onCreate()

        configureFlipper()
    }

    private fun configureFlipper() {
        SoLoader.init(this, false)

        if (FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(configureLayoutInspectorPlugin())
            client.addPlugin(configureDatabasePlugin())
            client.addPlugin(configureSharedPreferencesPlugin())
            client.start()
        }
    }

    private fun configureLayoutInspectorPlugin(): InspectorFlipperPlugin {
        return InspectorFlipperPlugin(this, DescriptorMapping.withDefaults())
    }

    private fun configureDatabasePlugin(): DatabasesFlipperPlugin {
        return DatabasesFlipperPlugin(this)
    }

    private fun configureSharedPreferencesPlugin(): SharedPreferencesFlipperPlugin {
        return SharedPreferencesFlipperPlugin(this)
    }
}
