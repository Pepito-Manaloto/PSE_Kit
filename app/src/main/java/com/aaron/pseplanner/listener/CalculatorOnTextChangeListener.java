package com.aaron.pseplanner.listener;

import android.widget.EditText;

import com.aaron.pseplanner.fragment.CalculatorFragment;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.StockService;
import com.aaron.pseplanner.service.implementation.CalculatorServiceImpl;
import com.aaron.pseplanner.service.implementation.StockServiceImpl;

/**
 * Created by aaron.asuncion on 12/29/2016.
 * Additional logic of Calculator on top of {@link EditTextOnTextChangeAddComma}.
 * TODO: TIGHT COUPLING, REFACTOR.
 */
public class CalculatorOnTextChangeListener extends EditTextOnTextChangeAddComma
{
    private CalculatorFragment view;
    private CalculatorService calculatorService;
    private StockService stockService;

    public CalculatorOnTextChangeListener(EditText editText, int maxIntegerDigits, CalculatorFragment view)
    {
        super(editText, maxIntegerDigits);
        this.view = view;
        this.calculatorService = new CalculatorServiceImpl();
        this.stockService = new StockServiceImpl();
    }

    /**
     * Invokes the CalculatorFragment's calculate method after adding commas to the EditText input.
     * {@link CalculatorFragment#calculate(CalculatorService, StockService)}
     */
    @Override
    protected void afterAddingComma()
    {
        view.calculate(this.calculatorService, this.stockService);
    }
}
