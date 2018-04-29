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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.BoardLot;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.bean.Tranche;
import com.aaron.pseplanner.bean.TrancheAggregate;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aaron.pseplanner.service.CalculatorService.MAX_WEIGHT;
import static com.aaron.pseplanner.service.CalculatorService.ONE_HUNDRED;

/**
 * Abstract class for Create/Update Trade Plan Activity. Does not contain navigation views or menu items.
 */
public abstract class SaveTradePlanActivity extends AppCompatActivity
{
    public static final String CLASS_NAME = SaveTradePlanActivity.class.getSimpleName();

    protected CalculatorService calculator;
    protected PSEPlannerService pseService;

    @BindView(R.id.edittext_shares)
    protected EditText sharesEditText;

    @BindView(R.id.edittext_entry_date)
    protected EditText entryDateEditText;

    @BindView(R.id.edittext_stop_date)
    protected EditText stopDateEditText;

    @BindView(R.id.edittext_stop_loss)
    protected EditText stopLossEditText;

    @BindView(R.id.edittext_target)
    protected EditText targetEditText;

    @BindView(R.id.edittext_capital)
    protected EditText capitalEditText;

    @BindView(R.id.entry_tranches_container)
    protected LinearLayout entryTranchesLayout;
    protected LayoutInflater layoutInflater;

    @BindView(R.id.button_save_trade_plan)
    protected Button saveButton;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.textview_stock)
    protected TextView stockLabel;

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
        ButterKnife.bind(this);

        setEditTextOnFocusChangeListener(sharesEditText, stopLossEditText, targetEditText, capitalEditText);
        setEditTextTextChangeListener(sharesEditText, stopLossEditText, targetEditText, capitalEditText);

        setDateEditTextOnClickListener(entryDateEditText, true);
        setDateEditTextOnClickListener(stopDateEditText, false);

        final LayoutInflater inflater = LayoutInflater.from(this);
        this.layoutInflater = inflater;
        addTranche(inflater, entryTranchesLayout); // Insert initial trache

        Button addTrancheButton = findViewById(R.id.button_add_tranche);
        addTrancheButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addTranche(inflater, entryTranchesLayout);
            }
        });

        this.saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String sharesStr = sharesEditText.getText().toString();
                String stopLossStr = stopLossEditText.getText().toString();
                String targetStr = targetEditText.getText().toString();
                String capitalStr = capitalEditText.getText().toString();
                String stopDateStr = stopDateEditText.getText().toString();

                if(areAllEditTextInputNotBlank(sharesStr, stopLossStr, targetStr, capitalStr, stopDateStr))
                {
                    long shares = Long.parseLong(sharesStr.replace(",", ""));
                    BigDecimal stopLoss = new BigDecimal(stopLossStr.replace(",", ""));
                    BigDecimal target = new BigDecimal(targetStr.replace(",", ""));
                    long capital = Long.parseLong(capitalStr.replace(",", ""));

                    List<Tranche> trancheList = validateAndGetEachTranche(entryTranchesLayout, shares);
                    if(!trancheList.isEmpty())
                    {
                        long realTotalShares = getRealTotalShares(trancheList);
                        TrancheAggregate trancheAggregate = aggregatedTrancheList(trancheList, realTotalShares, shares);
                        String entryDateStr = entryDateEditText.getText().toString();

                        Date entryDate = null;
                        if(StringUtils.isNotBlank(entryDateStr))
                        {
                            entryDate = getFormattedDate(entryDateStr);
                        }

                        Date stopDate = getFormattedDate(stopDateStr);

                        if(areTradePlanInputsValid(shares, stopLoss, target, entryDate, stopDate, capital, trancheAggregate))
                        {
                            evaluateRiskRewardAndSave(realTotalShares, shares, stopLoss, target, entryDate, stopDate, capital, trancheList, trancheAggregate);
                        }
                    }
                }
            }
        });

        this.calculator = new DefaultCalculatorService();
        this.pseService = new FacadePSEPlannerService(this);

        initializeToolbarAndActionBar();
    }

    /**
     * Sets the on focus change listener for edit texts. Will hide keyboard on focus change.
     */
    private void setEditTextOnFocusChangeListener(EditText... editTexts)
    {
        for(EditText editText : editTexts)
        {
            editText.setOnFocusChangeListener(new EditTextOnFocusChangeHideKeyboard(this));
        }
    }

    /**
     * Sets the text change listener for edit texts. Will format the input (adds commas and round off decimal to 4 places).
     */
    private void setEditTextTextChangeListener(EditText... editTexts)
    {
        for(EditText editText : editTexts)
        {
            editText.addTextChangedListener(new EditTextOnTextChangeWrapper(editText,
                    new EditTextOnTextChangeAddComma(editText, ViewUtils.getEditTextMaxLength(editText.getFilters(), CLASS_NAME))));
        }
    }

    /**
     * Sets the EditText date picker listener.
     */
    private void setDateEditTextOnClickListener(final EditText editText, final boolean optional)
    {
        editText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Initialize a new date picker dialog fragment
                DialogFragment dialogFragment = DatePickerFragment.newInstance(editText.getId(), optional);

                // Show the date picker dialog fragment
                dialogFragment.show(SaveTradePlanActivity.this.getSupportFragmentManager(), DatePickerFragment.CLASS_NAME);
            }
        });
    }

    private boolean areAllEditTextInputNotBlank(String sharesStr, String stopLossStr, String targetStr, String capitalStr, String stopDateStr)
    {
        return isEditTextNotBlank(sharesStr, "shares", sharesEditText)
                && isEditTextNotBlank(stopLossStr, "stop loss", stopLossEditText)
                && isEditTextNotBlank(targetStr, "target", targetEditText)
                && isEditTextNotBlank(stopDateStr, "stop date", stopDateEditText)
                && isEditTextNotBlank(capitalStr, "capital", capitalEditText);
    }

    /**
     * Validates the edit text input. If invalid, show toast and shift focus to edit text.
     *
     * @param input    the edit text input
     * @param label    the edit text label to show in toast message
     * @param editText the edit text
     * @return true if valid else false
     */
    private boolean isEditTextNotBlank(String input, String label, EditText editText)
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
     * Gets the price-weight-status of each entry traches.
     *
     * @param entryTranchesLayout the entry tranches
     * @param totalShares the total shares used to compute the percent weight on each tranche shares.
     *
     * @return {@code List<Tranche} list of tranches or empty list if some inputs are missing
     */
    private List<Tranche> validateAndGetEachTranche(LinearLayout entryTranchesLayout, long totalShares)
    {
        int numOfTranches = entryTranchesLayout.getChildCount();
        List<Tranche> trancheList = new ArrayList<>(numOfTranches);
        for(int i = 0, trancheNum = 1; i < numOfTranches; i++, trancheNum++)
        {
            View entryTrancheContainer = entryTranchesLayout.getChildAt(i);
            EditText entryPrice = entryTrancheContainer.findViewById(R.id.edittext_entry_price);
            EditText trancheShares = entryTrancheContainer.findViewById(R.id.edittext_tranche_shares);
            CheckBox trancheStatus = entryTrancheContainer.findViewById(R.id.checkbox_status);

            String price = entryPrice.getText().toString();
            BigDecimal priceNum = new BigDecimal(price.replace(",", ""));
            String shares = trancheShares.getText().toString();
            long sharesNum = Long.parseLong(shares.replaceAll(",", ""));

            // Validate inputs if blank
            if(isEditTextNotBlank(price, "price at tranche " + trancheNum, entryPrice) &&
                    isEditTextNotBlank(shares, "share at tranche " + trancheNum, trancheShares) &&
                    isEntryPriceValidBoardLot(entryPrice, trancheNum, priceNum, sharesNum))
            {
                BigDecimal weight = getTrancheWeightFromShares(sharesNum, totalShares);
                Tranche tranche = new Tranche()
                        .setOrder(trancheNum)
                        .setExecuted(trancheStatus.isChecked())
                        .setPrice(priceNum)
                        .setShares(sharesNum)
                        .setWeight(weight);
                trancheList.add(tranche);
            }
            else
            {
                return Collections.emptyList();
            }
        }

        return trancheList;
    }

    private boolean isEntryPriceValidBoardLot(EditText entryPrice, int trancheNum, BigDecimal price, long shares)
    {
        if(!BoardLot.isValidBoardLot(price, shares))
        {
            Toast.makeText(this, getString(R.string.boardlot_invalid) + " at tranche " + trancheNum, Toast.LENGTH_SHORT).show();
            entryPrice.requestFocus();
            return false;
        }

        return true;
    }

    private BigDecimal getTrancheWeightFromShares(long shares, double totalShares)
    {
        BigDecimal weight = BigDecimal.valueOf(shares).divide(BigDecimal.valueOf(totalShares), 20, BigDecimal.ROUND_UP);
        return weight.multiply(BigDecimal.valueOf(100));
    }

    /**
     * Computes the real price and shares based on executed tranche, as well as the total price and shares of all tranches.
     *
     * @param trancheList     the tranche list
     * @param realTotalShares the real total shares based on executed tranche/s
     * @param totalShares     the total shares of the trade
     *
     * @return the aggregated tranche list
     */
    private TrancheAggregate aggregatedTrancheList(List<Tranche> trancheList, final long realTotalShares, final long totalShares)
    {
        BigDecimal realAveragePrice = BigDecimal.ZERO;
        BigDecimal averagePrice = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        boolean atLeastOneExecuted = false;
        for(Tranche entry : trancheList)
        {
            BigDecimal weight = entry.getWeight();
            BigDecimal weightPercent = weight.movePointLeft(2);
            totalWeight = totalWeight.add(weight);
            BigDecimal priceNum = entry.getPrice();
            averagePrice = averagePrice.add(priceNum.multiply(weightPercent));
            if(entry.isExecuted())
            {
                atLeastOneExecuted = true;
                BigDecimal realWeight = getTrancheWeightFromShares(entry.getShares(), realTotalShares);
                BigDecimal realWeightPercent = realWeight.movePointLeft(2);

                realAveragePrice = realAveragePrice.add(priceNum.multiply(realWeightPercent));
            }
        }

        TrancheAggregate trancheAggregate = new TrancheAggregate()
                .setAtLeastOneExecuted(atLeastOneExecuted)
                .setAveragePrice(averagePrice)
                .setTotalShares(totalShares)
                .setRealAveragePrice(realAveragePrice)
                .setRealTotalShares(realTotalShares)
                .setTotalWeight(totalWeight);

        LogManager.debug(CLASS_NAME, "getAveragePriceTotalWeight", "trancheAggregate = " + trancheAggregate);

        return trancheAggregate;
    }

    /**
     * Gets the total shares of all executed tranches.
     */
    private long getRealTotalShares(List<Tranche> trancheList)
    {
        long realTotalShares = 0;
        for(Tranche tranche : trancheList)
        {
            if(tranche.isExecuted())
            {
                realTotalShares += tranche.getShares();
            }
        }

        return realTotalShares;
    }

    private Date getFormattedDate(String dateStr)
    {
        Date date;
        try
        {
            date = DatePickerFragment.DATE_FORMATTER.parse(dateStr);
        }
        catch(ParseException e)
        {
            LogManager.error(CLASS_NAME, "onClick(evaluateRiskRewardAndSave button)", "Error parsing date, will use current date instead.", e);
            date = new Date();
        }

        return date;
    }

    /**
     * Validates all edit text inputs if not blank.
     * Validate if share and entry prices of the selected stock is a valid boardlot.
     * Validate if total weight is equal to 100.
     * Validate target, stop loss, date entry & stop, and capital.
     *
     * @param shares             the number of shares to allot for the trade
     * @param stopLoss           the stop loss of the trade
     * @param target             the target price of the trade
     * @param entryDate          the date where the first tranche is executed
     * @param stopDate           the time stop of the trade
     * @param capital            the capital to allot for the trade
     * @param trancheAggregate   the details of all tranches combined
     *
     * @return true if all inputs are valid, else false
     */
    private boolean areTradePlanInputsValid(long shares, BigDecimal stopLoss, BigDecimal target, Date entryDate, Date stopDate, long capital,
            TrancheAggregate trancheAggregate)
    {
        BigDecimal averagePrice = trancheAggregate.getAveragePrice();
        BigDecimal totalWeight = trancheAggregate.getTotalWeight();
        boolean atLeastOneExecuted = trancheAggregate.isAtLeastOneExecuted();

        boolean isWeightExceedsMaxWeight = totalWeight.intValue() != MAX_WEIGHT;
        if(isWeightExceedsMaxWeight)
        {
            Toast.makeText(this, getString(R.string.tranche_weight_invalid), Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isTotalBuyExceedsCapital = capital < (shares * averagePrice.doubleValue());
        if(isTotalBuyExceedsCapital)
        {
            Toast.makeText(this, getString(R.string.buy_greater_than_capital), Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isEntryDateGiven = entryDate != null;
        if(isEntryDateGiven)
        {
            boolean isEntryDateEqualOrAfterStopDate = entryDate.getTime() == stopDate.getTime() || entryDate.after(stopDate);
            if(isEntryDateEqualOrAfterStopDate)
            {
                Toast.makeText(this, getString(R.string.entry_greater_than_stop_date), Toast.LENGTH_SHORT).show();
                return false;
            }

            if(!atLeastOneExecuted)
            {
                Toast.makeText(this, getString(R.string.entry_date_given_no_tranche_executed), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else
        {
            if(atLeastOneExecuted)
            {
                Toast.makeText(this, getString(R.string.entry_date_not_given_tranche_executed), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        boolean isStopLossSameOrHigherThanAveragePrice = stopLoss.doubleValue() >= averagePrice.doubleValue();
        if(isStopLossSameOrHigherThanAveragePrice)
        {
            Toast.makeText(this, getString(R.string.stoploss_invalid, averagePrice), Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isTargetPriceSameOrLowerThanAveragePrice = target.doubleValue() <= averagePrice.doubleValue();
        if(isTargetPriceSameOrLowerThanAveragePrice)
        {
            Toast.makeText(this, getString(R.string.target_invalid, averagePrice), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void evaluateRiskRewardAndSave(long realTotalShares, long shares, BigDecimal stopLoss, BigDecimal target, Date entryDate, Date stopDate, long capital,
            List<Tranche> trancheList, TrancheAggregate trancheAggregate)
    {
        BigDecimal riskReward = calculator.getRiskRewardRatio(trancheAggregate.getAveragePrice(), target, stopLoss);

        TradeDto dto = getTradeToSave(realTotalShares, shares, stopLoss, target, capital, entryDate, stopDate, riskReward, trancheAggregate.getRealAveragePrice(), trancheList);
        LogManager.debug(CLASS_NAME, "evaluateRiskRewardAndSave", "tradeDto = " + dto);

        boolean isRiskRewardNotAttractive = riskReward.doubleValue() < 2;
        if(isRiskRewardNotAttractive)
        {
            createAndShowAlertDialog(dto);
        }
        else
        {
            saveTradePlan(dto);
            setActivityResultSaveClicked(dto);
        }

        LogManager.debug(CLASS_NAME, "onCreate(saveButton)", "Saved TradePlan: " + dto);
    }

    /**
     * Creates a TradeDto from the trade parameters.
     *
     * @param realTotalShares  the total shares based on executed tranche/s
     * @param shares           the total shares
     * @param stopLoss         the stop loss
     * @param target           the target price
     * @param capital          the total capital
     * @param entryDate        the entry date
     * @param stopDate         the time stop date
     * @param riskReward       the risk reward ratio
     * @param realAveragePrice the real average price of all tranches that is executed
     * @param trancheList      the list of all tranches
     * @return TradeDto
     */
    private TradeDto getTradeToSave(long realTotalShares, long shares, BigDecimal stopLoss, BigDecimal target, long capital, Date entryDate, Date stopDate, BigDecimal riskReward,
            BigDecimal realAveragePrice, List<Tranche> trancheList)
    {
        String symbol = getSelectedSymbol();

        BigDecimal averagePriceAfterBuy = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal currentPrice = getSelectedSymbolCurrentPrice();
        BigDecimal priceToBreakEven = BigDecimal.ZERO;
        BigDecimal lossToStopLoss = BigDecimal.ZERO;
        BigDecimal gainToTarget = BigDecimal.ZERO;
        BigDecimal gainLoss = BigDecimal.ZERO;
        BigDecimal gainLossPercent = BigDecimal.ZERO;
        BigDecimal percentCapital = BigDecimal.ZERO;

        if(!BigDecimal.ZERO.equals(realAveragePrice))
        {
            averagePriceAfterBuy = calculator.getAveragePriceAfterBuy(realAveragePrice);

            totalAmount = averagePriceAfterBuy.multiply(new BigDecimal(realTotalShares));
            priceToBreakEven = calculator.getPriceToBreakEven(realAveragePrice);

            BigDecimal stopLossTotalAmount = calculator.getSellNetAmount(stopLoss, realTotalShares);
            lossToStopLoss = stopLossTotalAmount.subtract(totalAmount);

            BigDecimal targetTotalAmount = calculator.getSellNetAmount(target, realTotalShares);
            gainToTarget = targetTotalAmount.subtract(totalAmount);

            gainLoss = calculator.getGainLossAmount(realAveragePrice, realTotalShares, currentPrice);
            gainLossPercent = calculator.getPercentGainLoss(realAveragePrice, realTotalShares, currentPrice);

            percentCapital = totalAmount.divide(new BigDecimal(capital), MathContext.DECIMAL64)
                    .multiply(ONE_HUNDRED).setScale(2, BigDecimal.ROUND_CEILING);
        }

        int holdingPeriod = 0;
        Date now = new Date();
        if(entryDate != null)
        {
            holdingPeriod = calculator.getDaysBetween(now, entryDate);
        }

        TradeDto tradeDto = new TradeDto()
                .setSymbol(symbol)
                .setCurrentPrice(currentPrice)
                .setAveragePrice(averagePriceAfterBuy)
                .setTotalAmount(totalAmount)
                .setTotalShares(shares)
                .setPriceToBreakEven(priceToBreakEven)
                .setStopLoss(stopLoss)
                .setLossToStopLoss(lossToStopLoss)
                .setTargetPrice(target)
                .setGainToTarget(gainToTarget)
                .setGainLoss(gainLoss)
                .setGainLossPercent(gainLossPercent)
                .setCapital(capital)
                .setPercentCapital(percentCapital)
                .setEntryDate(entryDate)
                .setStopDate(stopDate)
                .setDaysToStopDate(calculator.getDaysBetween(now, stopDate))
                .setHoldingPeriod(holdingPeriod)
                .setRiskReward(riskReward);

        Date datePlanned = getDatePlannedToSet();
        int daysSincePlanned = calculator.getDaysBetween(now, datePlanned);
        tradeDto.setDatePlanned(datePlanned)
                .setDaysSincePlanned(daysSincePlanned);

        List<TradeEntryDto> list = priceWeightStatusMapToTradeEntryList(symbol, shares, trancheList);
        tradeDto.setTradeEntries(list);

        return tradeDto;
    }

    protected List<TradeEntryDto> priceWeightStatusMapToTradeEntryList(String stock, long shares, List<Tranche> trancheList)
    {
        List<TradeEntryDto> tradeEntryDtos = new ArrayList<>(trancheList.size());

        for(Tranche tranche : trancheList)
        {
            BigDecimal weight = tranche.getWeight();
            double weightMultiplier = tranche.getWeight().divide(BigDecimal.valueOf(100), 20, BigDecimal.ROUND_UP).doubleValue();
            long partialShares = Math.round(shares * weightMultiplier);

            tradeEntryDtos.add(new TradeEntryDto(stock, tranche.getPrice(), partialShares, weight, tranche.isExecuted()));
        }

        return tradeEntryDtos;
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
                setActivityResultSaveClicked(dto);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });

        showDialogWithMessageInCenter(builder);
    }

    protected void showDialogWithMessageInCenter(AlertDialog.Builder builder)
    {
        AlertDialog dialog = builder.create();
        dialog.show();

        // Align message to center.
        TextView messageView = dialog.findViewById(android.R.id.message);
        if(messageView != null)
        {
            messageView.setGravity(Gravity.CENTER);
        }
    }

    private void initializeToolbarAndActionBar()
    {
        toolbar.setTitle(getToolbarTitle());
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Adds a new tranche.
     *
     * @param inflater            the LayoutInflater that creates the create_entry_tranche
     * @param entryTranchesLayout the layout containing the tranche list
     *                            
     * @return entryTrancheContainer the created entry tranche container
     */
    protected View addTranche(LayoutInflater inflater, LinearLayout entryTranchesLayout)
    {
        // Create initial tranche
        View entryTrancheContainer = inflater.inflate(R.layout.create_entry_tranche, null, false);
        setEntryTrancheViewsProperties(entryTranchesLayout, entryTrancheContainer);

        return entryTrancheContainer;
    }

    /**
     * Sets the entry tranche properties and listeners. See create_entry_tranche.xml for the views to set.
     *
     * @param entryTranchesLayout   the container/layout of the list of entryTrancheContainers
     * @param entryTrancheContainer the container/layout of the views to set
     */
    private void setEntryTrancheViewsProperties(final LinearLayout entryTranchesLayout, final View entryTrancheContainer)
    {
        // This serves as the index of the added view
        final int numOfEntryTranche = entryTranchesLayout.getChildCount();
        entryTrancheContainer.setTag(numOfEntryTranche);

        TextView labelTranche = entryTrancheContainer.findViewById(R.id.label_tranche);
        labelTranche.setText(getString(R.string.label_tranche, ViewUtils.getOrdinalNumber(numOfEntryTranche)));

        ImageView removeTranche = entryTrancheContainer.findViewById(R.id.imageview_remove_tranche);
        removeTranche.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Removes this entry tranche from the view container.
             */
            @Override
            public void onClick(View v)
            {
                int removedIndex = removeEntryTrancheContainer(entryTrancheContainer);
                updateAllEntryTrancheTagAndLabel(removedIndex);
            }
        });

        EditText entryPrice = entryTrancheContainer.findViewById(R.id.edittext_entry_price);

        EditText trancheShares = entryTrancheContainer.findViewById(R.id.edittext_tranche_shares);
        trancheShares.setText(R.string.default_value);

        setEditTextTextChangeListener(entryPrice, trancheShares);

        final CheckBox checkboxStatus = entryTrancheContainer.findViewById(R.id.checkbox_status);
        TextView labelStatus = entryTrancheContainer.findViewById(R.id.label_status);
        labelStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkboxStatus.toggle();
            }
        });

        entryTranchesLayout.addView(entryTrancheContainer);
    }

    private int removeEntryTrancheContainer(View entryTrancheContainer)
    {
        // We are sure of its type
        int index = (int) entryTrancheContainer.getTag();
        entryTranchesLayout.removeView(entryTrancheContainer);

        return index;
    }

    private void updateAllEntryTrancheTagAndLabel(int index)
    {
        // Gets the current child count
        int childCount = entryTranchesLayout.getChildCount();
        for(int i = index; i < childCount; i++)
        {
            // Update the succeeding entry tranche tag/index and title
            View nextEntryTrancheContainer = entryTranchesLayout.getChildAt(i);
            nextEntryTrancheContainer.setTag(i);
            TextView labelTranche = nextEntryTrancheContainer.findViewById(R.id.label_tranche);

            labelTranche.setText(getString(R.string.label_tranche, ViewUtils.getOrdinalNumber(i)));
            // TODO: update tranche weight?
        }
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
     * Sends the result back to MainActivity.
     */
    private void setActivityResultHome(int resultCode)
    {
        Intent data = new Intent();

        LogManager.debug(CLASS_NAME, "setActivityResultHome", "Result code: " + resultCode);

        if(resultCode == Activity.RESULT_OK)
        {
            setIntentExtraOnResultHome(data);
        }

        setResult(resultCode, data);
        finish();
    }

    /**
     * Sends the result back to MainActivity.
     */
    private void setActivityResultSaveClicked(TradeDto dto)
    {
        Intent data = new Intent();
        data.putExtra(DataKey.EXTRA_TRADE.toString(), dto);

        setIntentExtraOnResultSaveClicked(data);
        setResult(Activity.RESULT_OK, data);
        finish();

        LogManager.debug(CLASS_NAME, "setActivityResultSaveClicked", "TradeDto result: " + dto);
    }

    protected abstract int getToolbarTitle();

    protected abstract void setIntentExtraOnResultHome(Intent intent);

    protected abstract void setIntentExtraOnResultSaveClicked(Intent intent);

    protected abstract void saveTradePlan(TradeDto dto);

    protected abstract String getSelectedSymbol();

    protected abstract BigDecimal getSelectedSymbolCurrentPrice();

    protected abstract Date getDatePlannedToSet();
}