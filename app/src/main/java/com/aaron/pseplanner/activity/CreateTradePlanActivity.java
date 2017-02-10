package com.aaron.pseplanner.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.BoardLot;
import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.fragment.DatePickerFragment;
import com.aaron.pseplanner.listener.EditTextOnFocusChangeHideKeyboard;
import com.aaron.pseplanner.listener.EditTextOnTextChangeAddComma;
import com.aaron.pseplanner.listener.EditTextOnTextChangeWrapper;
import com.aaron.pseplanner.service.ViewUtils;

import org.apache.commons.lang3.StringUtils;

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

        sharesEditText = (EditText) findViewById(R.id.edittext_shares);
        stopLossEditText = (EditText) findViewById(R.id.edittext_stop_loss);
        targetEditText = (EditText) findViewById(R.id.edittext_target);
        capitalEditText = (EditText) findViewById(R.id.edittext_capital);
        setEditTextOnFocusChangeListener(sharesEditText, stopLossEditText, targetEditText, capitalEditText);
        setEditTextTextChangeListener(sharesEditText, stopLossEditText, targetEditText, capitalEditText);

        entryDateEditText = (EditText) findViewById(R.id.edittext_entry_date);
        stopDateEditText = (EditText) findViewById(R.id.edittext_stop_date);
        setDateEditTextOnClickListener(entryDateEditText, stopDateEditText);

        final LinearLayout entryTranchesLayout = (LinearLayout) findViewById(R.id.entry_tranches_container);
        addTranche(entryTranchesLayout); // Insert initial trache

        Button addTrancheButton = (Button) findViewById(R.id.button_add_tranche);
        addTrancheButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addTranche(entryTranchesLayout);
            }
        });

        Button addTradePlanButton = (Button) findViewById(R.id.button_add_trade_plan);
        addTradePlanButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String shares = sharesEditText.getText().toString();
                String stopLoss = stopLossEditText.getText().toString();
                String target = targetEditText.getText().toString();
                String capital = capitalEditText.getText().toString();
                String entryDate = entryDateEditText.getText().toString();
                String stopDate = stopDateEditText.getText().toString();

                if(validateTradePlanInput(shares, stopLoss, target, entryDate, stopDate, capital, entryTranchesLayout))
                {
                    // TODO: insert to database and go back to main activity ticker fragment
                }
            }
        });
    }

    /**
     * Adds a new tranche.
     *
     * @param entryTranchesLayout the layout containing the tranche list
     */
    private void addTranche(LinearLayout entryTranchesLayout)
    {
        // Create initial tranche
        LayoutInflater inflater = LayoutInflater.from(CreateTradePlanActivity.this);
        View inflatedLayout = inflater.inflate(R.layout.entry_tranche, null, false);
        setEntryTrancheViewsProperties(entryTranchesLayout, inflatedLayout);
    }

    /**
     * Validates all edit text inputs if not blank, and also check if share and entry prices of the selected stock is a valid boardlot.
     *
     * @param shares the number of shares to allot for the trade
     * @param stopLoss the stop loss of the trade
     * @param target the target price of the trade
     * @param entryDate the date where the first tranche is executed
     * @param stopDate the time stop of the trade
     * @param capital the capital to allot for the trade
     * @param entryTranchesLayout the entry tranches
     *
     * @return true if all inputs are valid, else false
     */
    private boolean validateTradePlanInput(String shares, String stopLoss, String target, String entryDate, String stopDate, String capital, LinearLayout entryTranchesLayout)
    {
        // Validate inputs if blank
        if(isEditTextInputValid(shares, "shares", this.sharesEditText) && isEditTextInputValid(stopLoss, "stopLoss", this.stopLossEditText) &&
           isEditTextInputValid(target, "target", this.targetEditText) && isEditTextInputValid(entryDate, "entryDate", this.entryDateEditText) &&
           isEditTextInputValid(stopDate, "stopDate", this.stopDateEditText) && isEditTextInputValid(capital, "capital", this.capitalEditText))
        {
            double sharesNum = Double.parseDouble(shares.replace(",", ""));
            double totalWeight = 0;

            // Validate per tranche entry
            int numOfTranches = entryTranchesLayout.getChildCount();
            for(int i = 0, trancheNum = 1; i < numOfTranches; i++, trancheNum++)
            {
                View entryTrancheContainer = entryTranchesLayout.getChildAt(i);
                EditText entryPrice = (EditText) entryTrancheContainer.findViewById(R.id.edittext_entry_price);
                EditText trancheWeight = (EditText) entryTrancheContainer.findViewById(R.id.edittext_tranche_weight);
                String price = entryPrice.getText().toString();
                String weight = trancheWeight.getText().toString();

                // Validate inputs if blank
                if(isEditTextInputValid(price, "price at tranche " + trancheNum, entryPrice) && isEditTextInputValid(weight, "weight at tranche " + trancheNum, trancheWeight))
                {
                    // Check if boardlot valid
                    if(!BoardLot.isValidBoardLot(sharesNum, Double.parseDouble(price.replace(",", ""))))
                    {
                        Toast.makeText(this, getString(R.string.boardlot_invalid), Toast.LENGTH_LONG).show();
                        entryPrice.requestFocus();
                        return false;
                    }

                    totalWeight += Double.parseDouble(weight);
                }
                else
                {
                    return false;
                }
            }

            // Validate weight
            if(totalWeight != 100)
            {
                Toast.makeText(this, getString(R.string.tranche_weight_invalid), Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Validates the edit text input. If invalid, show toast and shift focus to edit text.
     *
     * @param input the edit text input
     * @param label the edit text label to show in toast message
     * @param editText the edit text
     *
     * @return true if valid else false
     */
    private boolean isEditTextInputValid(String input, String label, EditText editText)
    {
        if(StringUtils.isBlank(input))
        {
            Toast.makeText(this, getString(R.string.input_blank, label), Toast.LENGTH_LONG).show();
            editText.requestFocus();
            return false;
        }

        return true;
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
     * @param entryTranchesLayout the container/layout of the list of entryTrancheContainers
     * @param entryTrancheContainer the container/layout of the views to set
     */
    private void setEntryTrancheViewsProperties(final LinearLayout entryTranchesLayout, final View entryTrancheContainer)
    {
        // This serves as the index of the added view
        final int numOfEntryTranche = entryTranchesLayout.getChildCount();
        entryTrancheContainer.setTag(numOfEntryTranche);
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
                // We are sure of its type
                int index = (int) entryTrancheContainer.getTag();
                entryTranchesLayout.removeView(entryTrancheContainer);

                // Gets the current child count
                int childCount = entryTranchesLayout.getChildCount();
                for(int i = index; i < childCount; i++)
                {
                    // Update the succeeding entry tranche tag/index and title
                    View nextEntryTrancheContainer = entryTranchesLayout.getChildAt(i);
                    nextEntryTrancheContainer.setTag(i);
                    TextView labelTranche = (TextView) nextEntryTrancheContainer.findViewById(R.id.label_tranche);

                    labelTranche.setText(getString(R.string.create_trade_plan_tranche, ViewUtils.getOrdinalNumber(i)));
                    // TODO: update tranche weight?
                }
            }
        });

        EditText entryPrice = (EditText) entryTrancheContainer.findViewById(R.id.edittext_entry_price);
        setEditTextTextChangeListener(entryPrice);
        EditText trancheWeight = (EditText) entryTrancheContainer.findViewById(R.id.edittext_tranche_weight);
        if(numOfEntryTranche == 0)
        {
            trancheWeight.setText(R.string.one_hundred_value);
        }
        else
        {
            trancheWeight.setText(R.string.default_value);
        }

        entryTranchesLayout.addView(entryTrancheContainer);
    }

    /**
     * Creates and show the prompt dialog if the risk-reward is less than 2.
     */
    private void createAndShowAlertDialog(double riskReward)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.create_trade_plan_prompt, 1.5));

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // TODO: persist to database and move back to main activity ticker fragment
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Align message to center.
        TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
        if(messageView != null)
        {
            messageView.setGravity(Gravity.CENTER);
        }
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