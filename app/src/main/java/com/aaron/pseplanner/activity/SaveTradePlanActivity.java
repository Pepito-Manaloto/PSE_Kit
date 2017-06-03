package com.aaron.pseplanner.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.fragment.DatePickerFragment;
import com.aaron.pseplanner.listener.EditTextOnFocusChangeHideKeyboard;
import com.aaron.pseplanner.listener.EditTextOnTextChangeAddComma;
import com.aaron.pseplanner.listener.EditTextOnTextChangeWrapper;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.ViewUtils;
import com.aaron.pseplanner.service.implementation.DefaultCalculatorService;
import com.aaron.pseplanner.service.implementation.FacadePSEPlannerService;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.aaron.pseplanner.service.CalculatorService.ONE_HUNDRED;

/**
 * Abstract class for Create/Update Trade Plan Activity. Does not contain navigation views or menu items.
 */
public abstract class SaveTradePlanActivity extends AppCompatActivity
{
    public static final String CLASS_NAME = SaveTradePlanActivity.class.getSimpleName();
    protected CalculatorService calculator;
    protected PSEPlannerService pseService;

    protected EditText sharesEditText;
    protected EditText entryDateEditText;
    protected EditText stopDateEditText;
    protected EditText stopLossEditText;
    protected EditText targetEditText;
    protected EditText capitalEditText;

    protected LinearLayout entryTranchesLayout;
    protected LayoutInflater layoutInflater;
    protected Button saveButton;

    /**
     * Inflates the UI.
     *
     * @param savedInstanceState this Bundle is unused in this method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LogManager.debug(CLASS_NAME, "onCreate", "");

        setContentView(R.layout.activity_save_trade_plan);

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
        this.layoutInflater = inflater;
        this.entryTranchesLayout = (LinearLayout) findViewById(R.id.entry_tranches_container);
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

        this.saveButton = (Button) findViewById(R.id.button_save_trade_plan);
        this.saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String sharesStr = sharesEditText.getText().toString();
                String stopLossStr = stopLossEditText.getText().toString();
                String targetStr = targetEditText.getText().toString();
                String capitalStr = capitalEditText.getText().toString();
                String entryDateStr = entryDateEditText.getText().toString();
                String stopDateStr = stopDateEditText.getText().toString();

                // Validate inputs if blank
                if(isEditTextInputValid(sharesStr, "shares", sharesEditText) && isEditTextInputValid(stopLossStr, "stop loss", stopLossEditText) && isEditTextInputValid(targetStr, "target", targetEditText) && isEditTextInputValid(entryDateStr, "entry date", entryDateEditText) && isEditTextInputValid(stopDateStr, "stop date", stopDateEditText) && isEditTextInputValid(capitalStr, "capital", capitalEditText))
                {
                    long shares = Long.parseLong(sharesStr.replace(",", ""));
                    BigDecimal stopLoss = new BigDecimal(stopLossStr.replace(",", ""));
                    BigDecimal target = new BigDecimal(targetStr.replace(",", ""));
                    long capital = Long.parseLong(capitalStr.replace(",", ""));

                    Map<Pair<EditText, EditText>, Pair<String, String>> priceWeightMap = getTranchePriceAndWeight(entryTranchesLayout);
                    // Also validates if prices and weights are not blank
                    Pair<BigDecimal, BigDecimal> averagePriceTotalWeight = getAveragePriceTotalWeight(priceWeightMap);

                    if(averagePriceTotalWeight != null && validateTradePlanInput(shares, stopLoss, target, entryDateStr, stopDateStr, capital, priceWeightMap, averagePriceTotalWeight))
                    {
                        BigDecimal riskReward = calculator.getRiskRewardRatio(averagePriceTotalWeight.first, target, stopLoss);

                        Date entryDate = null;
                        Date stopDate = null;
                        try
                        {
                            entryDate = DatePickerFragment.DATE_FORMATTER.parse(entryDateStr);
                            stopDate = DatePickerFragment.DATE_FORMATTER.parse(stopDateStr);
                        }
                        catch(ParseException e)
                        {
                            LogManager.error(CLASS_NAME, "onClick(save button)", "Error parsing date, will use current date instead.", e);
                            entryDate = new Date();
                            stopDate = entryDate;
                        }

                        TradeDto dto = getTradeToSave(shares, stopLoss, target, capital, entryDate, stopDate, riskReward, averagePriceTotalWeight.first, priceWeightMap.values());
                        if(riskReward.doubleValue() < 2)
                        {
                            createAndShowAlertDialog(dto);
                        }
                        else
                        {
                            saveTradePlan(dto);
                        }

                        LogManager.debug(CLASS_NAME, "onCreate(saveButton)", "Saved TradePlan: " + dto);

                        setActivityResultSaveClicked(dto);
                    }
                }
            }
        });

        this.calculator = new DefaultCalculatorService();
        this.pseService = new FacadePSEPlannerService(this);
    }

    /**
     * Adds a new tranche.
     *
     * @param inflater            the LayoutInflater that creates the create_entry_tranche
     * @param entryTranchesLayout the layout containing the tranche list
     */
    protected void addTranche(LayoutInflater inflater, LinearLayout entryTranchesLayout)
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
    private boolean validateTradePlanInput(long shares, BigDecimal stopLoss, BigDecimal target, String entryDate, String stopDate, long capital, Map<Pair<EditText, EditText>, Pair<String, String>> priceWeightMap, Pair<BigDecimal, BigDecimal> averagePriceTotalWeight)
    {
        // Validate per tranche entry
        int trancheNum = 0;
        for(Map.Entry<Pair<EditText, EditText>, Pair<String, String>> entry : priceWeightMap.entrySet())
        {
            Pair<EditText, EditText> editTextPair = entry.getKey();
            EditText priceEditText = editTextPair.first;

            Pair<String, String> valuesPair = entry.getValue();
            String price = valuesPair.first;

            // Check if boardlot valid
            if(!BoardLot.isValidBoardLot(new BigDecimal(price), shares))
            {
                Toast.makeText(this, getString(R.string.boardlot_invalid), Toast.LENGTH_SHORT).show();
                priceEditText.requestFocus();
                return false;
            }
        }

        // Validate weight
        if(averagePriceTotalWeight.second.intValue() != 100)
        {
            Toast.makeText(this, getString(R.string.tranche_weight_invalid), Toast.LENGTH_SHORT).show();
            return false;
        }

        BigDecimal averagePrice = averagePriceTotalWeight.first;
        // Check if total buy is greater than capital
        if(capital < (shares * averagePrice.doubleValue()))
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
        double stopLossNum = stopLoss.doubleValue();
        if(stopLossNum >= averagePrice.doubleValue())
        {
            Toast.makeText(this, getString(R.string.stoploss_invalid, averagePrice), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if target is less than average price
        double targetNum = target.doubleValue();
        if(averagePrice.doubleValue() >= targetNum)
        {
            Toast.makeText(this, getString(R.string.target_invalid, averagePrice), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
     * @return {@code Map<Pair<EditText, EditText>, Pair<String, String>>} list of price-weight edittext and values
     */
    private Map<Pair<EditText, EditText>, Pair<String, String>> getTranchePriceAndWeight(LinearLayout entryTranchesLayout)
    {
        int numOfTranches = entryTranchesLayout.getChildCount();
        Map<Pair<EditText, EditText>, Pair<String, String>> map = new LinkedHashMap<>(numOfTranches);

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
    @Nullable
    private Pair<BigDecimal, BigDecimal> getAveragePriceTotalWeight(Map<Pair<EditText, EditText>, Pair<String, String>> priceWeightMap)
    {
        BigDecimal averagePrice = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

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
                BigDecimal priceNum = new BigDecimal(price.replace(",", ""));
                BigDecimal weightNum = new BigDecimal(weight);

                BigDecimal weightNumPercent = weightNum.movePointLeft(2);
                averagePrice = averagePrice.add(priceNum.multiply(weightNumPercent));
                totalWeight = totalWeight.add(weightNum);
            }
            else
            {
                return null;
            }
        }

        LogManager.debug(CLASS_NAME, "getAveragePriceTotalWeight", "averagePrice = " + averagePrice + " totalWeight = " + totalWeight);

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
                this.setActivityResultHome(Activity.RESULT_CANCELED);
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
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
     * Creates a TradeDto from the trade parameters.
     *
     * @param shares          the total shares
     * @param stopLoss        the stop loss
     * @param target          the target price
     * @param capital         the total capital
     * @param entryDate       the entry date
     * @param stopDate        the time stop date
     * @param riskReward      the risk reward ratio
     * @param averagePrice    the average price of all tranches
     * @param priceWeightList the price weight list of all tranches
     * @return TradeDto
     */
    protected TradeDto getTradeToSave(long shares, BigDecimal stopLoss, BigDecimal target, long capital, Date entryDate, Date stopDate, BigDecimal riskReward, BigDecimal averagePrice, Collection<Pair<String, String>> priceWeightList)
    {
        TradeDto tradeDto = new TradeDto();

        BigDecimal averagePriceAfterBuy = this.calculator.getAveragePriceAfterBuy(averagePrice);
        BigDecimal totalAmount = averagePriceAfterBuy.multiply(new BigDecimal(shares));
        BigDecimal targetTotalAmount = this.calculator.getSellNetAmount(target, shares);
        BigDecimal stopLossTotalAmount = this.calculator.getSellNetAmount(stopLoss, shares);

        String symbol = this.getSelectedSymbol();
        BigDecimal currentPrice = this.getSelectedSymbolCurrentPrice();

        tradeDto.setSymbol(symbol);
        tradeDto.setCurrentPrice(currentPrice);
        tradeDto.setAveragePrice(averagePriceAfterBuy);
        tradeDto.setTotalAmount(totalAmount);
        tradeDto.setTotalShares(shares);
        tradeDto.setPriceToBreakEven(this.calculator.getPriceToBreakEven(averagePrice));
        tradeDto.setStopLoss(stopLoss);
        tradeDto.setLossToStopLoss(stopLossTotalAmount.subtract(totalAmount));
        tradeDto.setTargetPrice(target);
        tradeDto.setGainToTarget(targetTotalAmount.subtract(totalAmount));
        tradeDto.setGainLoss(this.calculator.getGainLossAmount(averagePrice, shares, currentPrice));
        tradeDto.setGainLossPercent(this.calculator.getPercentGainLoss(averagePrice, shares, currentPrice));
        tradeDto.setCapital(capital);
        tradeDto.setPercentCapital(totalAmount.divide(new BigDecimal(capital), MathContext.DECIMAL64).multiply(ONE_HUNDRED).setScale(2, BigDecimal.ROUND_CEILING));
        tradeDto.setEntryDate(entryDate);
        tradeDto.setStopDate(stopDate);
        tradeDto.setHoldingPeriod(this.calculator.getDaysBetween(new Date(), entryDate));
        tradeDto.setRiskReward(riskReward);

        List<TradeEntryDto> list = priceWeightListToTradeEntryList(symbol, shares, priceWeightList);
        tradeDto.setTradeEntries(list);

        return tradeDto;
    }


    /**
     * Creates and show the prompt dialog if the risk-reward is less than 2.
     */
    private void createAndShowAlertDialog(final TradeDto dto)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.create_trade_plan_prompt, dto.getRiskReward().doubleValue()));

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                saveTradePlan(dto);
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
                    dialogFragment.show(SaveTradePlanActivity.this.getSupportFragmentManager(), DatePickerFragment.CLASS_NAME);
                }
            });
        }
    }

    protected List<TradeEntryDto> priceWeightListToTradeEntryList(String stock, long shares, Collection<Pair<String, String>> priceWeightList)
    {
        List<TradeEntryDto> dtos = new ArrayList<>(priceWeightList.size());

        for(Pair<String, String> priceWeight : priceWeightList)
        {
            String weightStr = priceWeight.second;
            double weightMultiplier = Double.parseDouble(weightStr) / 100;
            long partialShares = Math.round(shares * weightMultiplier);

            dtos.add(new TradeEntryDto(stock, priceWeight.first, partialShares, weightStr));
        }

        return dtos;
    }

    protected abstract void setActivityResultSaveClicked(TradeDto dto);

    protected abstract void setActivityResultHome(int resultCode);

    protected abstract void saveTradePlan(TradeDto dto);

    protected abstract String getSelectedSymbol();

    protected abstract BigDecimal getSelectedSymbolCurrentPrice();
}