/*
 * Copyright (C) 2015-2016 Worker Project
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

package me.raatiniemi.worker.presentation.view;

public interface TimesheetView {
    /**
     * Show message for failed timesheet retrieval.
     */
    void showGetTimesheetErrorMessage();

    /**
     * Show message for failed item deletion.
     *
     * @param numberOfItems Number of items that were supposed to be deleted.
     */
    void showDeleteErrorMessage(int numberOfItems);

    /**
     * Show message for failed item registration.
     *
     * @param numberOfItems Number of items that were supposed to be updated.
     */
    void showRegisterErrorMessage(int numberOfItems);

    /**
     * Initiate refresh of the view.
     */
    void refresh();

    /**
     * The view have finished loading data.
     */
    void finishLoading();
}
