/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.features.projects.viewmodel;

import android.support.annotation.NonNull;

import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.raatiniemi.worker.features.projects.model.ProjectsItem;
import rx.Observable;
import rx.subjects.PublishSubject;

import static me.raatiniemi.worker.presentation.util.RxUtil.hideErrors;

public interface RefreshActiveProjectsViewModel {
    interface Input {
        void projects(@NonNull List<ProjectsItem> projects);
    }

    interface Output {
        @NonNull
        Observable<List<Integer>> positionsForActiveProjects();
    }

    class ViewModel implements Input, Output {
        private final Input input;
        private final Output output;

        private final PublishSubject<List<ProjectsItem>> projects = PublishSubject.create();
        private final PublishSubject<List<Integer>> positions = PublishSubject.create();

        public ViewModel() {
            input = this;
            output = this;

            projects.map(ViewModel::getPositionsForActiveProjects)
                    .compose(hideErrors())
                    .subscribe(positions);
        }

        @NonNull
        private static List<Integer> getPositionsForActiveProjects(@NonNull List<ProjectsItem> items) {
            if (items.isEmpty()) {
                return Collections.emptyList();
            }

            List<Integer> activePositions = new ArrayList<>();
            for (ProjectsItem item : items) {
                if (!item.isActive()) {
                    continue;
                }

                activePositions.add(items.indexOf(item));
            }

            return activePositions;
        }

        @Override
        public void projects(@NonNull List<ProjectsItem> projects) {
            this.projects.onNext(projects);
        }

        @NonNull
        @Override
        @RxLogObservable
        public Observable<List<Integer>> positionsForActiveProjects() {
            return positions.asObservable();
        }

        @NonNull
        public Input input() {
            return input;
        }

        @NonNull
        public Output output() {
            return output;
        }
    }
}
