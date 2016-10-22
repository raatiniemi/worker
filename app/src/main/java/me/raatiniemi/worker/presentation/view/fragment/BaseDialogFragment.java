package me.raatiniemi.worker.presentation.view.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import me.raatiniemi.worker.R;

import static me.raatiniemi.util.NullUtil.nonNull;

abstract public class BaseDialogFragment extends DialogFragment {
    private DialogInterface.OnCancelListener onCancelListener;
    private DialogInterface.OnDismissListener onDismissListener;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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

    /**
     * TODO: Remove method call when `minSdkVersion` is +23.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (!isStateValid()) {
                dismissDialogWithInvalidState();
            }
        }
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
