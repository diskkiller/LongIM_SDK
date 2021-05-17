package com.longbei.im_push_service_sdk.common.app.kit.airpanel;

import android.app.Activity;


/**
 * @version 1.0.0
 */

interface Contract extends AirPanel.Boss {
    interface Panel extends AirPanel.Boss {
        void adjustPanelHeight(int heightMeasureSpec);
    }

    interface Helper extends Panel, AirPanel.PanelListener {
        int calculateHeightMeasureSpec(int heightMeasureSpec);

        void setup(Activity activity);
    }
}
