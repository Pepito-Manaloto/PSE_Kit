package com.aaron.pseplanner.listener;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;

import com.aaron.pseplanner.R;

/**
 * Created by Aaron on 11/30/2016.
 */

public class ImageViewOnClickCollapseExpand implements View.OnClickListener
{
    private ImageView imageView;
    private String collapse;
    private String expand;

    public ImageViewOnClickCollapseExpand(Context context, ImageView imageView)
    {
        Resources resources = context.getResources();

        this.imageView = imageView;
        this.collapse = resources.getString(R.string.collapse_arrow);
        this.expand = resources.getString(R.string.expand_arrow);
    }

    /**
     * Toggle update the image view's image.
     */
    @Override
    public void onClick(View v)
    {
        if(this.collapse.equals(this.imageView.getContentDescription()))
        {
            this.imageView.setImageResource(R.mipmap.expand_arrow);
            this.imageView.setContentDescription(this.expand);

            // TODO: Show expanded view
        }
        else
        {
            this.imageView.setImageResource(R.mipmap.collapse_arrow);
            this.imageView.setContentDescription(this.collapse);

            // TODO: Hide expanded view
        }
    }
}
