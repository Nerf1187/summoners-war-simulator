package Monsters;

import Game.*;
import Util.Util.*;

/**
 * Represents an elemental type with associated font color and relationship behavior.
 * Each element can have relationships with other elements such as advantageous, neutral, or disadvantageous.
 */
public enum Element
{
    FIRE(ConsoleColor.RED_BOLD_BRIGHT),
    WATER(ConsoleColor.BLUE_BOLD_BRIGHT),
    WIND(ConsoleColor.YELLOW_BOLD_BRIGHT),
    LIGHT(ConsoleColor.WHITE_BOLD_BRIGHT),
    DARK(ConsoleColor.PURPLE_BOLD_BRIGHT),
    ALL(ConsoleColor.RESET);
    
    private final ConsoleColor fontColor;
    
    Element(ConsoleColor color)
    {
        this.fontColor = color;
    }
    
    public ConsoleColor getFontColor()
    {
        return fontColor;
    }
    
    public String toString()
    {
        if (this == ALL)
        {
            return null;
        }
        
        return STRINGS.toTitleCase(super.toString());
    }
    
    /**
     * Finds the elemental relationship between this and another Element. (Assumes Elements are from different Teams)
     *
     * @param other The element to compare to.
     * @return The color of the relationship (green background for advantageous; yellow background for neutral; red background for disadvantageous)
     */
    public ConsoleColor relationWith(Element other)
    {
        //Advantageous
        if ((this == FIRE && other == WIND) || (this == WATER && other == FIRE) || (this == WIND && other == WATER) || (this == LIGHT && other == DARK) || (this == DARK && other == LIGHT))
        {
            return ConsoleColor.GREEN_BACKGROUND;
        }
        //Neutral
        if (this == other || this == LIGHT || this == DARK || this == ALL || other == LIGHT || other == DARK || other == ALL)
        {
            return ConsoleColor.YELLOW_BACKGROUND;
        }
        //Disadvantageous
        return ConsoleColor.RED_BACKGROUND;
    }
}
