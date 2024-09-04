package Stats;

import Game.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * @author Anthony (Tony) Youssef
 * The parent class for all Buffs and Debuffs
 */

public class Stat
{
    public static int NULL = -1, BERSERK = 0, TOTEM = 1, MAGIC_SPHERE = 2, MACARON_SHIELD = 3;
    //Rune sub-stats
    public static final int ATK = 1, ATKPERCENT = 2, DEF = 3, DEFPERCENT = 4, HP = 5, HPPERCENT = 6, SPD = 7, CRITRATE = 8, CRITDMG = 9, RES = 10, ACC = 11;
    
    private int numTurns;
    private int otherStatNum = -1;
    private int numOfSpecialEffects = 0;
    
    /**
     * Creates a new Stat (Not a buff or debuff)
     *
     * @param numTurns The number of turns to apply
     */
    public Stat(int numTurns)
    {
        this.numTurns = numTurns;
    }
    
    /**
     * Sets the Stat number (Not buff or debuff)
     *
     * @param num The number to set
     */
    public void setStatNum(int num)
    {
        otherStatNum = num;
    }
    
    /**
     * @return The Stats number
     */
    public int getStatNum()
    {
        return otherStatNum;
    }
    
    /**
     * Decreases the turns remaining by the specified amount
     *
     * @param turns The number of turns to decrease
     */
    public void decreaseTurn(int turns)
    {
        numTurns -= turns;
    }
    
    /**
     * Decrease the turns remaining by one
     */
    public void decreaseTurn()
    {
        numTurns -= 1;
    }
    
    /**
     * @return The number of turns remaining for the Stat
     */
    public int getNumTurns()
    {
        return numTurns;
    }
    
    /**
     * Sets the number of turns remaining to a given value
     *
     * @param numTurns The new number of turns remaining
     */
    public void setNumTurns(int numTurns)
    {
        this.numTurns = numTurns;
    }
    
    /**
     * Compares two Stats
     *
     * @param stat The other Stat to compare to
     * @return True if both Stats have the same stat number, false otherwise
     */
    public boolean equals(Stat stat)
    {
        return (stat.otherStatNum == otherStatNum) && (stat.otherStatNum != -1);
    }
    
    /**
     * Formats the Stat into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        String s = "";
        switch (otherStatNum)
        {
            case 0 -> s = "Berserk (" + getNumTurns() + " turns remaining)";
            case 1 -> s = "Totems (" + numOfSpecialEffects + ")";
            case 2 -> s = "Magic Sphere (" + numOfSpecialEffects + ")";
            case 3 -> s = "Macaron Shield";
        }
        return s;
    }
    
    /**
     * @return The number of special effects this Stat has
     */
    public int getNumOfSpecialEffects()
    {
        return numOfSpecialEffects;
    }
    
    /**
     * Sets the number of special effects this Stat has
     *
     * @param numOfSpecialEffects The number to set
     */
    public void setNumOfSpecialEffects(int numOfSpecialEffects)
    {
        this.numOfSpecialEffects = numOfSpecialEffects;
    }
    
    /**
     * Prints all buffs and debuffs and their descriptions
     */
    public static void printStatDescriptions()
    {
        try
        {
            System.out.printf("%sBuffs:%s%s%sDebuffs:%s%n%n", ConsoleColors.BLUE, ConsoleColors.RESET, statSpacing("Buffs:".length()), ConsoleColors.RED
                    , ConsoleColors.RESET);
            Scanner readBuffs = new Scanner(Objects.requireNonNull(Buff.class.getResourceAsStream("Buff key.csv")));
            Scanner readDebuffs = new Scanner(Objects.requireNonNull(Debuff.class.getResourceAsStream("Debuff key.csv")));
            String buffName;
            String buffDescription;
            String debuffName;
            String debuffDescription;
            while (readBuffs.hasNextLine() || readDebuffs.hasNextLine())
            {
                //Get next buff to print
                String[] buff = readBuffs.nextLine().split(",");
                for (int i = 0; i < buff.length; i++)
                {
                    buff[i] = buff[i].replaceAll(";", ",");
                }
                
                try
                {
                    //Get next debuff to print
                    String[] debuff = readDebuffs.nextLine().split(",");
                    for (int i = 0; i < debuff.length; i++)
                    {
                        debuff[i] = debuff[i].replaceAll(";", ",");
                    }
                    //Get each part of the buff and debuff
                    buffName = buff[1];
                    int buffNameHolder = buffName.length();
                    buffDescription = buff[3];
                    debuffName = debuff[1];
                    debuffDescription = debuff[3];
                    int lastLineBreak = 0;
                    
                    //Print something else if there is no description
                    if (debuffDescription.equals("null"))
                    {
                        throw new NoSuchElementException();
                    }
                    
                    //Print part of the buff description on the next line if it is too long
                    if (buffDescription.length() > 95)
                    {
                        String s = "";
                        int count = 0;
                        boolean breakLine = false;
                        for (int j = 0; j < buffDescription.length(); j++)
                        {
                            if (j != 0 && j % 70 == 0)
                            {
                                breakLine = true;
                            }
                            if (breakLine && buffDescription.charAt(j) == ' ')
                            {
                                for (int k = 0; k < buffName.length() + 1; k++)
                                {
                                    s += " ";
                                }
                                
                                
                                breakLine = false;
                                lastLineBreak = count;
                            }
                            count++;
                        }
                        System.out.printf("%s%s:%s %s%s%s%s:%s %s%s\n%s%s%s\n\n", ConsoleColors.BLUE, buffName, ConsoleColors.CYAN,
                                buffDescription.substring(0, lastLineBreak), ConsoleColors.RED,
                                statSpacing(buffName.length() + 2 + buffDescription.substring(0, lastLineBreak).length()), debuffName,
                                ConsoleColors.YELLOW, debuffDescription, ConsoleColors.CYAN, s, buffDescription.substring(lastLineBreak),
                                ConsoleColors.RESET);
                    }
                    //Print the stat descriptions normally
                    else
                    {
                        System.out.printf("%s%s:%s %s%s%s%s:%s %s%s%n%n", ConsoleColors.BLUE, buffName, ConsoleColors.CYAN, buffDescription,
                                ConsoleColors.RED, statSpacing(buffNameHolder + 2 + buffDescription.substring(lastLineBreak).length()), debuffName,
                                ConsoleColors.YELLOW, debuffDescription, ConsoleColors.RESET);
                    }
                }
                catch (NoSuchElementException e)
                {
                    buffName = buff[1];
                    buffDescription = buff[3];
                    if (!buffDescription.equals("null"))
                    {
                        System.out.printf("%s%s:%s %s%s%n%n", ConsoleColors.BLUE, buffName, ConsoleColors.CYAN, buffDescription, ConsoleColors.RESET);
                    }
                }
            }
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Formats the spacing needed to print the stats correctly
     *
     * @param length the length of the String already printed
     * @return the correct number of spaces to print
     */
    private static String statSpacing(int length)
    {
        String spaces = "";
        
        for (int i = 0; i < 100 - length; i++)
        {
            spaces += " ";
        }
        return spaces;
    }
}
