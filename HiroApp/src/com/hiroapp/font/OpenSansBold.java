/**
 * Created by Jaycon Systems on 01/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */
package com.hiroapp.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class OpenSansBold extends TextView{

	public OpenSansBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OpenSansBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenSansBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                                               "fonts/OpenSansBold.ttf");
        setTypeface(tf);
    }
}
