/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Author qiujuer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.longbei.im_push_service_sdk.common.app.kit.ui.compat;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.longbei.im_push_service_sdk.common.app.kit.ui.drawable.BalloonMarkerDrawable;


/**
 * Wrapper compatibility class to call some API-Specific methods
 * And offer alternate procedures when possible
 *
 * @hide
 */
public class UiCompat {

    /**
     * Sets the custom Outline provider on API>=21.
     * Does nothing on API<21
     *
     * @param view                  View
     * @param balloonMarkerDrawable OutlineProvider Drawable
     */
    public static void setOutlineProvider(View view, final BalloonMarkerDrawable balloonMarkerDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UiCompatNotCrash.setOutlineProvider(view, balloonMarkerDrawable);
        }
    }

    /**
     * As our DiscreteSeekBar implementation uses a circular drawable on API < 21
     * we want to use the same method to set its bounds as the Ripple's hotspot bounds.
     *
     * @param drawable Drawable
     * @param left     Left
     * @param top      Top
     * @param right    Right
     * @param bottom   Bottom
     */
    public static void setHotspotBounds(Drawable drawable, int left, int top, int right, int bottom) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //We don't want the full size rect, Lollipop ripple would be too big
            int size = (right - left) / 8;
            drawable.setHotspotBounds(left + size, top + size, right - size, bottom - size);
        } else {
            drawable.setBounds(left, top, right, bottom);
        }
    }

    /**
     * android.support.v4.view.ViewCompat SHOULD include this once and for all!!
     * But it doesn't...
     *
     * @param view       View
     * @param background DrawableBackground
     */
    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            UiCompatNotCrash.setBackground(view, background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    /**
     * Sets the TextView text direction attribute when possible
     *
     * @param textView      TextView
     * @param textDirection Text Direction
     * @see TextView#setTextDirection(int)
     */
    public static void setTextDirection(TextView textView, int textDirection) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            UiCompatNotCrash.setTextDirection(textView, textDirection);
        }
    }

    /**
     * Returns a themed color integer associated with a particular resource ID.
     * If the resource holds a complex {@link ColorStateList}, then the default
     * color from the set is returned.
     *
     * @param resources Resources
     * @param id        The desired resource identifier, as generated by the aapt
     *                  tool. This integer encodes the package, type, and resource
     *                  entry. The value 0 is an invalid identifier.
     * @return A single color value in the form 0xAARRGGBB.
     */
    public static int getColor(Resources resources, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return resources.getColor(id, null);
        } else {
            return resources.getColor(id);
        }
    }

    /**
     * Returns a themed color state list associated with a particular resource
     * ID. The resource may contain either a single raw color value or a
     * complex {@link ColorStateList} holding multiple possible colors.
     *
     * @param resources Resources
     * @param id        The desired resource identifier of a {@link ColorStateList},
     *                  as generated by the aapt tool. This integer encodes the
     *                  package, type, and resource entry. The value 0 is an invalid
     *                  identifier.
     * @return A themed ColorStateList object containing either a single solid
     * color or multiple colors that can be selected based on a state.
     */
    public static ColorStateList getColorStateList(Resources resources, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return resources.getColorStateList(id, null);
        } else {
            return resources.getColorStateList(id);
        }
    }
}
