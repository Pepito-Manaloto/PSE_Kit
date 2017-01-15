package com.aaron.pseplanner.listener;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;

import com.aaron.pseplanner.R;

/**
 * Created by Aaron on 11/30/2016.
 * Show/hide the view based on the current state of the imageView.
 */
public class ImageViewOnClickHideExpand implements View.OnClickListener
{
    private ImageView imageView;
    private String hide;
    private String expand;
    private View viewToShowHide;

    public ImageViewOnClickHideExpand(Context context, ImageView imageView, View viewToShowHide)
    {
        Resources resources = context.getResources();

        this.imageView = imageView;
        this.hide = resources.getString(R.string.hide_arrow);
        this.expand = resources.getString(R.string.expand_arrow);
        this.viewToShowHide = viewToShowHide;
    }

    /**
     * Toggle update the image view's image.
     */
    @Override
    public void onClick(View v)
    {
        // Arrow is hide: update icon, update content description, show container
        if(this.hide.equals(this.imageView.getContentDescription()))
        {
            this.imageView.setImageResource(R.mipmap.expand_arrow);
            this.imageView.setContentDescription(this.expand);
            this.viewToShowHide.setVisibility(View.VISIBLE);
        }
        else // Arrow is expand: update icon, update content description, hide container
        {
            this.imageView.setImageResource(R.mipmap.hide_arrow);
            this.imageView.setContentDescription(this.hide);
            this.viewToShowHide.setVisibility(View.GONE);
        }
    }
}
