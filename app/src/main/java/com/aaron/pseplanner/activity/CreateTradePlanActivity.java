package com.aaron.pseplanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.fragment.DatePickerFragment;
import com.aaron.pseplanner.listener.EditTextOnFocusChangeHideKeyboard;
import com.aaron.pseplanner.listener.EditTextOnTextChangeAddComma;
import com.aaron.pseplanner.listener.EditTextOnTextChangeWrapper;
import com.aaron.pseplanner.service.ViewUtils;

import static com.aaron.pseplanner.constant.Constants.LOG_TAG;

/**
 * Create Trade Plan Activity. Does not contain navigation views or menu items.
 */
public class CreateTradePlanActivity extends AppCompatActivity
{
    public static final String CLASS_NAME = CreateTradePlanActivity.class.getSimpleName();
    private Ticker selectedStock;

    private EditText sharesEditText;
    private EditText entryDateEditText;
    private EditText stopDateEditText;
    private EditText stopLossEditText;
    private EditText targetEditText;
    private EditText capitalEditText;

    private LinearLayout entryTranchesLayout;

    private Button addTrancheButton;
    private Button addTradePlanButton;

    /**
     * Inflates the UI.
     *
     * @param savedInstanceState this Bundle is unused in this method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trade_plan);

        if(savedInstanceState != null)
        {
            this.selectedStock = savedInstanceState.getParcelable(DataKey.EXTRA_TICKER.toString());
        }
        else
        {
            this.selectedStock = getIntent().getParcelableExtra(DataKey.EXTRA_TICKER.toString());
        }

        Log.d(LOG_TAG, CLASS_NAME + ": onCreate." + selectedStock);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.create_trade_plan_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView stockLabel = (TextView) findViewById(R.id.textview_stock);
        stockLabel.setText(this.selectedStock.getSymbol());

        this.sharesEditText = (EditText) findViewById(R.id.edittext_shares);
        this.stopLossEditText = (EditText) findViewById(R.id.edittext_stop_loss);
        this.targetEditText = (EditText) findViewById(R.id.edittext_target);
        this.capitalEditText = (EditText) findViewById(R.id.edittext_capital);
        setEditTextOnFocusChangeListener(this.sharesEditText, this.stopLossEditText, this.targetEditText, this.capitalEditText);
        setEditTextTextChangeListener(this.sharesEditText, this.stopLossEditText, this.targetEditText, this.capitalEditText);

        this.entryDateEditText = (EditText) findViewById(R.id.edittext_entry_date);
        this.stopDateEditText = (EditText) findViewById(R.id.edittext_stop_date);
        setDateEditTextOnClickListener(this.entryDateEditText, this.stopDateEditText);

        this.entryTranchesLayout = (LinearLayout) findViewById(R.id.entry_tranches_container);

        this.addTrancheButton = (Button) findViewById(R.id.button_add_tranche);
        this.addTrancheButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LayoutInflater inflater = LayoutInflater.from(CreateTradePlanActivity.this);
                View inflatedLayout = inflater.inflate(R.layout.entry_tranche, null, false);
                setEntryTrancheViewsProperties(inflatedLayout);
            }
        });

        this.addTradePlanButton = (Button) findViewById(R.id.button_add_trade_plan);
        // TODO: add onclick listener, which will pop up confirmation dialog if risk-reward ratio not met
        this.addTradePlanButton.setOnClickListener(null);
    }

    /**
     * This method is called when a user selects an item in the menu bar. Home button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                this.setActivityResult(Activity.RESULT_CANCELED);
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * Saves current state in memory, when this activity is temporarily destroyed.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putParcelable(DataKey.EXTRA_TICKER.toString(), this.selectedStock);

        Log.d(LOG_TAG, CLASS_NAME + ": onSaveInstanceState");
    }

    /**
     * Sets the stock of the created trade plan if created, then sends it to the main activity.
     *
     * @param resultCode the result of the user's action
     */
    private void setActivityResult(int resultCode)
    {
        Intent data = new Intent();

        if(resultCode == Activity.RESULT_OK)
        {
            data.putExtra(DataKey.EXTRA_TICKER.toString(), this.selectedStock);
        }

        setResult(resultCode, data);
        finish();
    }

    /**
     * Sets the entry tranche properties and listeners. See entry_tranche.xml for the views to set.
     *
     * @param entryTrancheContainer the container/layout of the views to set
     */
    private void setEntryTrancheViewsProperties(final View entryTrancheContainer)
    {
        final int numOfEntryTranche = entryTranchesLayout.getChildCount();
        TextView labelTranche = (TextView) entryTrancheContainer.findViewById(R.id.label_tranche);
        labelTranche.setText(getString(R.string.create_trade_plan_tranche, ViewUtils.getOrdinalNumber(numOfEntryTranche)));

        ImageView removeTranche = (ImageView) entryTrancheContainer.findViewById(R.id.imageview_remove_tranche);
        removeTranche.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Removes this entry tranche from the view container.
             */
            @Override
            public void onClick(View v)
            {
                entryTranchesLayout.removeView(entryTrancheContainer);
                //TODO: update tranche label and tranche weight

//                for(int i=0; i < numOfEntryTranche; i++)
//                {
//
//                }
            }
        });

        EditText entryPrice = (EditText) entryTrancheContainer.findViewById(R.id.edittext_entry_price);
        setEditTextTextChangeListener(entryPrice);
        EditText trancheWeight = (EditText) entryTrancheContainer.findViewById(R.id.edittext_tranche_weight);
        // TODO: prevent user from deleting '%'
        if(numOfEntryTranche == 0)
        {
            trancheWeight.setText(R.string.one_hundred_percent);
        }
        else
        {
            trancheWeight.setText(R.string.default_value_percent);
        }

        entryTranchesLayout.addView(entryTrancheContainer);
    }

    /**
     * Sets the on focus change listener for edit texts. Will hide keyboard on focus change.
     */
    private void setEditTextOnFocusChangeListener(EditText... editTexts)
    {
        EditTextOnFocusChangeHideKeyboard listener = new EditTextOnFocusChangeHideKeyboard(this);
        for(EditText editText : editTexts)
        {
            editText.setOnFocusChangeListener(listener);
        }
    }

    /**
     * Sets the text change listener for edit texts. Will format the input (adds commas and round off decimal to 4 places).
     */
    private void setEditTextTextChangeListener(EditText... editTexts)
    {
        for(EditText editText : editTexts)
        {
            editText.addTextChangedListener(new EditTextOnTextChangeWrapper(editText, new EditTextOnTextChangeAddComma(editText, ViewUtils.getEditTextMaxLength(editText.getFilters(), CLASS_NAME))));
        }
    }

    /**
     * Sets the EditText date picker listener.
     */
    private void setDateEditTextOnClickListener(EditText... editTexts)
    {
        for(final EditText editText : editTexts)
        {
            editText.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // Initialize a new date picker dialog fragment
                    DialogFragment dialogFragment = DatePickerFragment.newInstance(editText.getId());

                    // Show the date picker dialog fragment
                    dialogFragment.show(CreateTradePlanActivity.this.getSupportFragmentManager(), DatePickerFragment.CLASS_NAME);
                }
            });
        }
    }
}