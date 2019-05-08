/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.home.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_home.*
import me.raatiniemi.worker.R
import me.raatiniemi.worker.data.service.ongoing.ReloadNotificationService

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        configureView()
    }

    override fun onResume() {
        super.onResume()

        ReloadNotificationService.startServiceWithContext(this)
    }

    private fun configureView() {
        setSupportActionBar(tbMain)

        val navController = findNavController(R.id.fragmentContainer)
        val appBarConfiguration = AppBarConfiguration(navController.graph, dlProjects)
        tbMain.setupWithNavController(navController, appBarConfiguration)
        nvProjects.setupWithNavController(navController)
    }
}