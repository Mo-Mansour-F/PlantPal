package com.mmf.plantpal.utilteis;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

public class MoneyFormatter {


    private static final NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

    public static String fromNumberToMoneyFormat(float number) {
        return getDecimalFormat().format(number);
    }

    public static String fromNumberToMoneyFormat(double number) {
        return getDecimalFormat().format(number);
    }

    private static DecimalFormat getDecimalFormat(){
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        DecimalFormat decimalFormat = (DecimalFormat) currencyFormatter;
        decimalFormat.setCurrency(Currency.getInstance(Locale.US));
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(0);
        return decimalFormat;

//        return String.format(Locale.US, "%,.1f$", number);
//        return numberFormat.format(number) + "$";
    }


    public static Number fromMoneyFormatToString(String money) {
        try {
            return numberFormat.parse(money);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}




