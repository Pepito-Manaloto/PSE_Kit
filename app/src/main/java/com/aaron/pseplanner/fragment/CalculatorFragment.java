package com.aaron.pseplanner.fragment;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.listener.EditTextOnFocusChangeHideKeyboard;
import com.aaron.pseplanner.listener.ImageViewOnClickCollapseExpand;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class CalculatorFragment extends Fragment
{
    private EditText buyPriceEditText;
    private EditText sharesEditText;
    private EditText sellPriceEditText;

    private ImageView buyNetAmountImageView;
    private ImageView sellNetAmountImageView;

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_calculator, parent, false);
        final Resources resource = getResources();

        this.buyPriceEditText = (EditText) view.findViewById(R.id.edittext_buy_price);
        this.sharesEditText = (EditText) view.findViewById(R.id.edittext_shares);
        this.sellPriceEditText = (EditText) view.findViewById(R.id.edittext_sell_price);
        this.setEditTextOnFocusChangeListener(this.buyPriceEditText, this.sharesEditText, this.sellPriceEditText);

        this.buyNetAmountImageView = (ImageView) view.findViewById(R.id.imageview_buy_net_amount);
        this.sellNetAmountImageView = (ImageView) view.findViewById(R.id.imageview_sell_net_amount);
        this.setImageViewOnClickListener(this.buyNetAmountImageView, this.sellNetAmountImageView);

        return view;
    }

    /**
     * Sets the on focus change listener for edit texts. Will hide keyboard on focus change.
     */
    private void setEditTextOnFocusChangeListener(EditText... editTexts)
    {
        EditTextOnFocusChangeHideKeyboard listener = new EditTextOnFocusChangeHideKeyboard(this.getActivity());
        for(EditText editText : editTexts)
        {
            editText.setOnFocusChangeListener(listener);
        }
    }

    /**
     * Sets the on click listener for image views. Will toggle update the image on click.
     */
    private void setImageViewOnClickListener(ImageView... imageViews)
    {
        for(ImageView imageView : imageViews)
        {
            imageView.setOnClickListener(new ImageViewOnClickCollapseExpand(this.getActivity(), imageView));
        }
    }
}
