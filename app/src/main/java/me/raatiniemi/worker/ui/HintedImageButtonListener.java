package me.raatiniemi.worker.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class HintedImageButtonListener implements View.OnLongClickListener
{
    protected Context mContext;

    public HintedImageButtonListener(Context context)
    {
        mContext = context;
    }

    @Override
    public boolean onLongClick(View view)
    {
        if (null != view) {
            CharSequence description = view.getContentDescription();
            if (!TextUtils.isEmpty(description)) {
                int[] position = new int[2];
                view.getLocationInWindow(position);

                Toast toast = Toast.makeText(mContext, description, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.LEFT, position[0], position[1]);
                toast.show();

                return true;
            }
        }
        return false;
    }
}
