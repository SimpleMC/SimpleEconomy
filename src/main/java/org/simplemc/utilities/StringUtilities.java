package org.simplemc.utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class StringUtilities
{
    /**
     * Formats a double to 2dp
     * Example: 10.00000 -> 10.00
     * @param d The double to format
     * @return Formatted double as a string.
     */
    public static String formatDouble(Double d)
    {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(d);
    }
}
