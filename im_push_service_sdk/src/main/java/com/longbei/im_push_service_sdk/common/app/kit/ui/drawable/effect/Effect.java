/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 07/27/2015
 * Changed 07/27/2015
 * Version 1.0.0
 * Author Qiujuer
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
package com.longbei.im_push_service_sdk.common.app.kit.ui.drawable.effect;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;

import com.longbei.im_push_service_sdk.common.app.kit.ui.Ui;


/**
 * Created by Qiujuer on 2015/3/27.
 * This is TouchEffectDrawable draw Effect
 */
@SuppressWarnings("WeakerAccess")
public abstract class Effect {
    private float mWidth;
    private float mHeight;

    /**
     * Returns the width of the Shape.
     */
    public final float getWidth() {
        return mWidth;
    }

    /**
     * Returns the height of the Shape.
     */
    public final float getHeight() {
        return mHeight;
    }

    /**
     * Draw this shape into the provided Canvas, with the provided Paint.
     * Before calling this, you must call {@link #resize(float, float)}.
     *
     * @param canvas the Canvas within which this shape should be drawn
     * @param paint  the Paint object that defines this shape's characteristics
     */
    public abstract void draw(Canvas canvas, Paint paint);

    /**
     * Resizes the dimensions of this shape.
     * Must be called before {@link #draw(Canvas, Paint)}.
     *
     * @param width  the width of the shape (in pixels)
     * @param height the height of the shape (in pixels)
     */
    public final void resize(float width, float height) {
        if (width < 0) {
            width = 0;
        }
        if (height < 0) {
            height = 0;
        }
        if (mWidth != width || mHeight != height) {
            mWidth = width;
            mHeight = height;
            onResize(width, height);
        }
    }

    public void touchCancel(float x, float y) {

    }

    public void touchDown(float x, float y) {

    }

    public void touchReleased(float x, float y) {

    }

    public void touchMove(float x, float y) {

    }

    public abstract void animationEnter(float factor);

    public abstract void animationExit(float factor);

    /**
     * Set the draw paint alpha by modulateAlpha
     *
     * @param paint Paint
     * @param alpha Alpha
     * @return PrevAlpha
     */
    protected int setPaintAlpha(Paint paint, int alpha) {
        final int prevAlpha = paint.getAlpha();
        paint.setAlpha(Ui.modulateAlpha(prevAlpha, alpha));
        return prevAlpha;
    }


    /**
     * Checks whether the Shape is opaque.
     * Default impl returns true. Override if your subclass can be opaque.
     *
     * @return true if any part of the drawable is <em>not</em> opaque.
     */
    public boolean hasAlpha() {
        return true;
    }

    /**
     * Callback method called when {@link #resize(float, float)} is executed.
     *
     * @param width  the new width of the Shape
     * @param height the new height of the Shape
     */
    protected void onResize(float width, float height) {
    }

    /**
     * Compute the Outline of the shape and return it in the supplied Outline
     * parameter. The default implementation does nothing and {@code outline} is not changed.
     *
     * @param outline The Outline to be populated with the result. Should not be null.
     */
    public void getOutline(Outline outline) {
    }

    @Override
    public Effect clone() throws CloneNotSupportedException {
        return (Effect) super.clone();
    }
}
