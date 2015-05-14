/**
 * Created by Jaycon Systems on 01/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */
package com.hiroapp.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class OpenSansLight extends TextView{

	public OpenSansLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OpenSansLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenSansLight(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                                               "fonts/OpenSansLight.ttf");
        setTypeface(tf);
    }
}
