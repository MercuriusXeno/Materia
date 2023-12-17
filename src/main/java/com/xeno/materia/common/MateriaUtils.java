package com.xeno.materia.common;

public class MateriaUtils
{
    public static String getAmountForDisplay(long amount)
    {
        // blubberbub's movie magic, it's a long but we're putting a decimal place in it so it resembles currency
        // the floating point is a fake, this is an integer through and through, but it tricks our lizard brains
        // into thinking about stuff like money, and I'm hoping that somehow helps users process/use the mod more easily.
        var longAsString = Long.toString(amount);
        if (longAsString.length() < 2)
        {
            return String.format("0.0%s", longAsString);
        } else if (longAsString.length() < 3)
        {
            return String.format("0.%s", longAsString);
        } else
        {
            return String.format("%s.%s", longAsString.substring(0, longAsString.length() - 2), longAsString.substring(longAsString.length() - 2));
        }
    }
}
