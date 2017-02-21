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
import android.util.Pair;
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
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.ViewUtils;
import com.aaron.pseplanner.service.implementation.CalculatorServiceImpl;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Create Trade Plan Activity. Does not contain navigation views or menu items.
 */
public class CreateTradePlanActivity extends AppCompatActivity
{
    public static final String CLASS_NAME = CreateTradePlanActivity.class.getSimpleName();
    private CalculatorService calculator;
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

        LogManager.debug(CLASS_NAME, "onCreate", this.selectedStock == null ? null : this.selectedStock.toString());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_create_trade_plan);
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

        final LayoutInflater inflater = LayoutInflater.from(this);
        final LinearLayout entryTranchesLayout = (LinearLayout) findViewById(R.id.entry_tranches_container);
        addTranche(inflater, entryTranchesLayout); // Insert initial trache

        Button addTrancheButton = (Button) findViewById(R.id.button_add_tranche);
        addTrancheButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addTranche(inflater, entryTranchesLayout);
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
                Map<Pair<EditText, EditText>, Pair<String, String>> priceWeightMap = getTranchePriceAndWeight(entryTranchesLayout);
                // Also validates if prices and weights are not blank
                Pair<Double, Double> averagePriceTotalWeight = getAveragePriceTotalWeight(priceWeightMap);

                if(averagePriceTotalWeight != null && validateTradePlanInput(shares, stopLoss, target, entryDate, stopDate, capital, priceWeightMap, averagePriceTotalWeight))
                {
                    double riskReward = calculator.getRiskRewardRatio(averagePriceTotalWeight.first, Double.parseDouble(target.replace(",", "")), Double.parseDouble(stopLoss.replace(",", "")));
                    if(riskReward < 2)
                    {
                        createAndShowAlertDialog(riskReward);
                    }
                    else
                    {
                        persistTradePlan();
                    }
                }
            }
        });

        this.calculator = new CalculatorServiceImpl();
    }

    /**
     * Adds a new tranche.
     *
     * @param inflater            the LayoutInflater that creates the create_entry_tranche
     * @param entryTranchesLayout the layout containing the tranche list
     */
    private void addTranche(LayoutInflater inflater, LinearLayout entryTranchesLayout)
    {
        // Create initial tranche
        View inflatedLayout = inflater.inflate(R.layout.create_entry_tranche, null, false);
        setEntryTrancheViewsProperties(entryTranchesLayout, inflatedLayout);
    }

    /**
     * Validates all edit text inputs if not blank.
     * Validate if share and entry prices of the selected stock is a valid boardlot.
     * Validate if total weight is equal to 100.
     * Validate target, stop loss, date entry & stop, and capital.
     *
     * @param shares                  the number of shares to allot for the trade
     * @param stopLoss                the stop loss of the trade
     * @param target                  the target price of the trade
     * @param entryDate               the date where the first tranche is executed
     * @param stopDate                the time stop of the trade
     * @param capital                 the capital to allot for the trade
     * @param priceWeightMap          the entry entry_price-weight map
     * @param averagePriceTotalWeight the average price and total weight
     * @return true if all inputs are valid, else false
     */
    private boolean validateTradePlanInput(String shares, String stopLoss, String target, String entryDate, String stopDate, String capital, Map<Pair<EditText, EditText>, Pair<String, String>> priceWeightMap, Pair<Double, Double> averagePriceTotalWeight)
    {
        // Validate inputs if blank
        if(isEditTextInputValid(shares, "shares", this.sharesEditText) && isEditTextInputValid(stopLoss, "stop loss", this.stopLossEditText) &&
                isEditTextInputValid(target, "target", this.targetEditText) && isEditTextInputValid(entryDate, "entry date", this.entryDateEditText) &&
                isEditTextInputValid(stopDate, "stop date", this.stopDateEditText) && isEditTextInputValid(capital, "capital", this.capitalEditText))
        {
            double sharesNum = Double.parseDouble(shares.replace(",", ""));

            // Validate per tranche entry
            int trancheNum = 0;
            for(Map.Entry<Pair<EditText, EditText>, Pair<String, String>> entry : priceWeightMap.entrySet())
            {
                Pair<EditText, EditText> editTextPair = entry.getKey();
                EditText priceEditText = editTextPair.first;

                Pair<String, String> valuesPair = entry.getValue();
                String price = valuesPair.first;

                // Check if boardlot valid
                if(!BoardLot.isValidBoardLot(Double.parseDouble(price.replace(",", "")), sharesNum))
                {
                    Toast.makeText(this, getString(R.string.boardlot_invalid), Toast.LENGTH_SHORT).show();
                    priceEditText.requestFocus();
                    return false;
                }
            }

            // Validate weight
            if(averagePriceTotalWeight.second != 100)
            {
                Toast.makeText(this, getString(R.string.tranche_weight_invalid), Toast.LENGTH_SHORT).show();
                return false;
            }

            double averagePrice = averagePriceTotalWeight.first;
            // Check if total buy is greater than capital
            if(Integer.parseInt(capital.replace(",", "")) < (sharesNum * averagePrice))
            {
                Toast.makeText(this, getString(R.string.buy_greater_than_capital), Toast.LENGTH_SHORT).show();
                return false;
            }

            // Check if entry date is greater than stop date
            try
            {
                Date entry = DatePickerFragment.DATE_FORMATTER.parse(entryDate);
                Date stop = DatePickerFragment.DATE_FORMATTER.parse(stopDate);

                if(entry.getTime() == stop.getTime() || entry.after(stop))
                {
                    Toast.makeText(this, getString(R.string.entry_greater_than_stop_date), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            catch(ParseException e)
            {
                LogManager.error(CLASS_NAME, "validateTradePlanInput", "Failed checking entry and stop date.", e);
            }

            // Check if stop loss is greater than average price
            double stopLossNum = Double.parseDouble(stopLoss.replace(",", ""));
            if(stopLossNum >= averagePrice)
            {
                Toast.makeText(this, getString(R.string.stoploss_invalid, averagePrice), Toast.LENGTH_SHORT).show();
                return false;
            }

            // Check if target is less than average price
            double targetNum = Double.parseDouble(target.replace(",", ""));
            if(averagePrice >= targetNum)
            {
                Toast.makeText(this, getString(R.string.target_invalid, averagePrice), Toast.LENGTH_SHORT).show();
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
     * @param input    the edit text input
     * @param label    the edit text label to show in toast message
     * @param editText the edit text
     * @return true if valid else false
     */
    private boolean isEditTextInputValid(String input, String label, EditText editText)
    {
        if(StringUtils.isBlank(input))
        {
            Toast.makeText(this, getString(R.string.input_blank, label), Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Gets the price-weight of each entry traches.
     *
     * @param entryTranchesLayout the entry tranches
     * @return Map<Pair<EditText, EditText>, Pair<String, String>> list of price-weight edittext and values
     */
    private Map<Pair<EditText, EditText>, Pair<String, String>> getTranchePriceAndWeight(LinearLayout entryTranchesLayout)
    {
        int numOfTranches = entryTranchesLayout.getChildCount();
        Map<Pair<EditText, EditText>, Pair<String, String>> map = new HashMap<>(numOfTranches);

        for(int i = 0, trancheNum = 1; i < numOfTranches; i++, trancheNum++)
        {
            View entryTrancheContainer = entryTranchesLayout.getChildAt(i);
            EditText entryPrice = (EditText) entryTrancheContainer.findViewById(R.id.edittext_entry_price);
            EditText trancheWeight = (EditText) entryTrancheContainer.findViewById(R.id.edittext_tranche_weight);
            String price = entryPrice.getText().toString();
            String weight = trancheWeight.getText().toString();

            map.put(new Pair<>(entryPrice, trancheWeight), new Pair<>(price, weight));
        }

        return map;
    }

    /**
     * Gets the average price and total weight.
     *
     * @param priceWeightMap the price-weight map
     * @return the average price and total weight pair if inputs are valid, else null
     */
    private Pair<Double, Double> getAveragePriceTotalWeight(Map<Pair<EditText, EditText>, Pair<String, String>> priceWeightMap)
    {
        double averagePrice = 0;
        double totalWeight = 0;

        int trancheNum = 0;
        for(Map.Entry<Pair<EditText, EditText>, Pair<String, String>> entry : priceWeightMap.entrySet())
        {
            Pair<EditText, EditText> editTextPair = entry.getKey();
            EditText priceEditText = editTextPair.first;
            EditText weightEditText = editTextPair.second;

            Pair<String, String> valuesPair = entry.getValue();
            String price = valuesPair.first;
            String weight = valuesPair.second;

            // Increment tranche number
            trancheNum++;

            // Validate inputs if blank
            if(isEditTextInputValid(price, "price at tranche " + trancheNum, priceEditText) && isEditTextInputValid(weight, "weight at tranche " + trancheNum, weightEditText))
            {
                double priceNum = Double.parseDouble(price.replace(",", ""));
                double weightNum = Double.parseDouble(weight);

                averagePrice += priceNum;
                totalWeight += weightNum;
            }
            else
            {
                return null;
            }
        }

        return new Pair<>(averagePrice, totalWeight);
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

        LogManager.debug(CLASS_NAME, "onSaveInstanceState", "");
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
     * Sets the entry tranche properties and listeners. See create_entry_tranche.xmlche.xml for the views to set.
     *
     * @param entryTranchesLayout   the container/layout of the list of entryTrancheContainers
     * @param entryTrancheContainer the container/layout of the views to set
     */
    private void setEntryTrancheViewsProperties(final LinearLayout entryTranchesLayout, final View entryTrancheContainer)
    {
        // This serves as the index of the added view
        final int numOfEntryTranche = entryTranchesLayout.getChildCount();
        entryTrancheContainer.setTag(numOfEntryTranche);
        TextView labelTranche = (TextView) entryTrancheContainer.findViewById(R.id.label_tranche);
        labelTranche.setText(getString(R.string.label_tranche, ViewUtils.getOrdinalNumber(numOfEntryTranche)));

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

                    labelTranche.setText(getString(R.string.label_tranche, ViewUtils.getOrdinalNumber(i)));
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
        builder.setMessage(getString(R.string.create_trade_plan_prompt, riskReward));

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                persistTradePlan();
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
        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        if(messageView != null)
        {
            messageView.setGravity(Gravity.CENTER);
        }
    }

    /**
     *
     */
    private void persistTradePlan()
    {
        // TODO: insert to database
        setActivityResult(Activity.RESULT_OK);
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