package me.raatiniemi.worker.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

/**
 * Display hint for the image via long click.
 */
public class HintedImageButtonListener implements View.OnLongClickListener {
    protected Context mContext;

    /**
     * Construct the hinted image button listener.
     *
     * @param context Context to use.
     */
    public HintedImageButtonListener(Context context) {
        mContext = context;
    }

    /**
     * Display the content description of the view in a toast message.
     *
     * @param view View pressed by the user.
     * @return True if the long click was consumed, otherwise false.
     */
    @Override
    public boolean onLongClick(View view) {
        if (null != view) {
            // Check that the view actually have content description to display.
            CharSequence description = view.getContentDescription();
            if (!TextUtils.isEmpty(description)) {
                // Retrieve the position of the view.
                int[] position = new int[2];
                view.getLocationInWindow(position);

                Toast toast = Toast.makeText(mContext, description, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.LEFT, position[0], position[1]);
                toast.show();

                return true;
            }
        }
        return false;
    }
}
