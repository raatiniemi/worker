/*
 * Copyright (C) 2022 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.home.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import me.raatiniemi.worker.R
import me.raatiniemi.worker.databinding.ActivityHomeBinding
import me.raatiniemi.worker.feature.ongoing.service.ReloadNotificationService

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        configureView()
        reloadOngoingNotifications()
    }

    private fun configureView() {
        setSupportActionBar(binding.tbMain)

        val navController = findNavController(R.id.fragmentContainer)
        val appBarConfiguration = AppBarConfiguration(navController.graph, binding.dlProjects)
        binding.tbMain.setupWithNavController(navController, appBarConfiguration)
        binding.nvProjects.setupWithNavController(navController)
    }

    private fun reloadOngoingNotifications() {
        Intent(this, ReloadNotificationService::class.java)
            .also { startService(it) }
    }
}
