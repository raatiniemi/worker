/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.raatiniemi.worker.features.shared.presenter.BasePresenter;

import static me.raatiniemi.worker.util.PresenterUtil.detachViewIfNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class PresenterUtilTest {
    @Test
    public void detachViewIfNotNull_withNull() {
        detachViewIfNotNull(null);
    }

    @Test
    public void detachViewIfNotNull_withPresenter() {
        BasePresenter presenter = mock(BasePresenter.class);

        detachViewIfNotNull(presenter);

        verify(presenter).detachView();
    }
}
