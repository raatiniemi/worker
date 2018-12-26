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

package me.raatiniemi.worker.features.shared.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import me.raatiniemi.worker.R;

import static me.raatiniemi.worker.util.NullUtil.nonNull;

public abstract class BaseDialogFragment extends DialogFragment {
    private DialogInterface.OnCancelListener onCancelListener;
    private DialogInterface.OnDismissListener onDismissListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isStateValid()) {
            dismissDialogWithInvalidState();
        }
    }

    boolean isStateValid() {
        return true;
    }

    private void dismissDialogWithInvalidState() {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                R.string.error_message_unknown,
                Snackbar.LENGTH_SHORT
        ).show();

        dismiss();
    }

    void setOnCancelListener(@NonNull DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    @Override
    public void onCancel(@Nullable DialogInterface dialog) {
        super.onCancel(dialog);

        if (nonNull(onCancelListener)) {
            onCancelListener.onCancel(dialog);
        }
    }

    void setOnDismissListener(@NonNull DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(@Nullable DialogInterface dialog) {
        super.onDismiss(dialog);

        if (nonNull(onDismissListener)) {
            onDismissListener.onDismiss(dialog);
        }
    }
}
