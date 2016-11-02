package com.vipulasri.artisto.widget.fresco_custom;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.facebook.imagepipeline.request.BasePostprocessor;

/**
 * Created by HP-HP on 18-10-2016.
 */

public class BlurPostProcessor extends BasePostprocessor
{
    private static final float BLUR_RADIUS_IMAGE = 7.5f;
    private RenderScript mRenderScript;

    public BlurPostProcessor(RenderScript renderScript)
    {
        mRenderScript = renderScript;
    }

    @Override
    public String getName()
    {
        return BlurPostProcessor.class.getSimpleName();
    }

    @Override
    public void process(Bitmap bitmap)
    {
        Allocation alloc = Allocation.createFromBitmap(mRenderScript, bitmap);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
        blur.setInput(alloc);
        blur.setRadius(BLUR_RADIUS_IMAGE);
        blur.forEach(alloc);
        alloc.copyTo(bitmap);
    }
}