package me.raatiniemi.worker.presentation.view.fragment;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import me.raatiniemi.worker.R;

import static me.raatiniemi.util.NullUtil.nonNull;

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

    protected boolean isStateValid() {
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
