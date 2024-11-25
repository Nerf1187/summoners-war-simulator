package Runes;

import Errors.*;
import Monsters.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * This class used to create and apply runes for Monsters
 *
 * @author Anthony (Tony) Youssef
 */
public class Rune
{
    public static final int ATK = 1, ATKPERCENT = 2, DEF = 3, DEFPERCENT = 4, HP = 5, HPPERCENT = 6, SPD = 7, CRITRATE = 8, CRITDMG = 9, RES = 10, ACC = 11;
    public static final int ENERGY = 1, FATAL = 2, BLADE = 3, SWIFT = 4, FOCUS = 5, GUARD = 6, ENDURE = 7, SHIELD = 8, REVENGE = 9, WILL = 10, NEMESIS = 11;
    public static final int VAMPIRE = 12, DESTROY = 13, DESPAIR = 14, VIOLENT = 15, RAGE = 16, FIGHT = 17, DETERMINATION = 18, ENHANCE = 19, ACCURACY = 20,
            TOLERANCE = 21, SEAL = 24;
    public static final int ELEMENTARTIFACT = 22, TYPEARTIFACT = 23;
    private int type;
    private MainAttribute mainAttribute;
    private final ArrayList<SubAttribute> subAttributes;
    private final Monster monster;
    private final int monBaseMaxHp, monBaseAtk, monBaseDef;
    private boolean applied = false;
    
    /**
     * Creates a new Rune
     *
     * @param type          The rune type
     * @param mainAttribute The Main attribute
     * @param place         The rune's place (1,2, etc.)
     * @param subAttributes The list of subAttributes
     * @param monster       The Monster to apply the rune to
     */
    public Rune(int type, MainAttribute mainAttribute, int place, ArrayList<SubAttribute> subAttributes, Monster monster)
    {
        //Make sure the type and place are within the correct range
        if (type > SEAL || type < 1 || place > 8 || place < 1)
        {
            throw new IndexOutOfBoundsException("Type must be between 1 and 24 inclusive and place must be between 1 and 8 inclusive");
        }
        
        //Make sure artifacts have the correct placement
        if (type == ELEMENTARTIFACT && place != 7)
        {
            throw new ConflictingArguments("Place must equal 7 for an element artifact");
        }
        if (type == TYPEARTIFACT && place != 8)
        {
            throw new ConflictingArguments("Place must equal 8 for a type artifact");
        }
        
        this.type = type;
        this.mainAttribute = mainAttribute;
        this.subAttributes = subAttributes;
        this.monster = monster;
        monBaseMaxHp = monster.getBaseMaxHp();
        monBaseAtk = monster.getBaseAtk();
        monBaseDef = monster.getBaseDef();
    }
    
    /**
     * Creates a new blank rune
     */
    public Rune()
    {
        type = -1;
        mainAttribute = null;
        subAttributes = null;
        monster = null;
        monBaseAtk = -1;
        monBaseMaxHp = -1;
        monBaseDef = -1;
    }
    
    /**
     * Applies the rune attributes if it has not been done already, does nothing otherwise
     */
    public void apply()
    {
        //Do nothing if the rune was already applied
        if (applied)
        {
            return;
        }
        
        applied = true;
        
        //Apply the main attribute
        applyAttribute(mainAttribute);
        
        //Apply each sub-attribute
        for (SubAttribute sub : subAttributes)
        {
            applyAttribute(sub);
        }
    }
    
    /**
     * Applies the given rune attribute
     *
     * @param attribute The attribute to apply
     */
    private void applyAttribute(MainAttribute attribute)
    {
        switch (attribute.getNum())
        {
            case ATK -> monster.setTempAtk(monster.getTempAtk() + attribute.getAmount());
            case ATKPERCENT -> monster.setTempAtk((monBaseAtk * (1.0 * attribute.getAmount() / 100) + monster.getTempAtk()));
            case DEF -> monster.setTempDef(monster.getTempDef() + attribute.getAmount());
            case DEFPERCENT -> monster.setTempDef((monBaseDef * (1.0 * attribute.getAmount() / 100) + monster.getTempDef()));
            case HP -> monster.setTempMaxHp(monster.getTempMaxHp() + attribute.getAmount());
            case HPPERCENT -> monster.setTempMaxHp((monBaseMaxHp * (1.0 * attribute.getAmount() / 100) + monster.getTempMaxHp()));
            case SPD -> monster.setSpd(monster.getSpd() + attribute.getAmount());
            case CRITRATE -> monster.setCritRate(monster.getCritRate() + attribute.getAmount());
            case CRITDMG -> monster.setCritDmg(monster.getCritDmg() + attribute.getAmount());
            case RES -> monster.setResistance(monster.getResistance() + attribute.getAmount());
            case ACC -> monster.setAccuracy(monster.getAccuracy() + attribute.getAmount());
        }
    }
    
    /**
     * Gets the rune's type
     *
     * @return The rune's type
     */
    public int getType()
    {
        return type;
    }
    
    /**
     * Sets the rune type to a new value
     *
     * @param type The new value
     * @return True if the input was valid and the type was successfully set, false otherwise
     */
    public boolean setType(int type)
    {
        if (type < 1 || type > SEAL)
        {
            return false;
        }
        this.type = type;
        return true;
    }
    
    /**
     * Formats the rune into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        String s = numToType(type);
        //Get the main attribute followed by ach sub-attribute on separate lines
        s += "\tMain Attribute:\n\t\t";
        s += mainAttribute + "\n\tSub Attributes:\n";
        for (SubAttribute sub : subAttributes)
        {
            s += "\t\t" + sub + "\n";
        }
        return s;
    }
    
    /**
     * Formats the rune type number into a String
     *
     * @param num The number to convert
     * @return The rune type as a String
     */
    public static String numToType(int num)
    {
        try
        {
            //Read the rune key and get the name of the requested set
            Scanner read = new Scanner(new File("Rune key.csv"));
            while (read.hasNextLine())
            {
                String[] line = read.nextLine().split(",");
                if ((line[0].equals(num + "")))
                {
                    return line[1];
                }
            }
        }
        catch (Exception ignored)
        {
        }
        if (num == ELEMENTARTIFACT)
        {
            return "Element artifact";
        }
        if (num == TYPEARTIFACT)
        {
            return "Type artifact";
        }
        return "";
    }
    
    /**
     * Converts a given variable name into an attribute or set number (Help from StackOverflow)
     *
     * @param str A String containing the variable name
     * @return The attribute number with the given variable name if possible, -1 otherwise
     */
    public static int stringToNum(String str)
    {
        //Convert the name to uppercase
        str = str.toUpperCase();
        int returnVal = -1;
        try
        {
            //Get the number equivalent of the String
            Field f = Rune.class.getField(str);
            Class<?> type = f.getType();
            if (type.toString().equals("int"))
            {
                returnVal = f.getInt(new Rune());
            }
        }
        //The given String is not a variable name
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            System.out.println(str + " is not a rune set type or property");
            return -1;
        }
        
        return returnVal;
    }
    
    /**
     * Gets the rune's main attribute
     *
     * @return The rune's main attribute
     */
    public MainAttribute getMainAttribute()
    {
        return mainAttribute;
    }
    
    /**
     * Sets the main attribute for the rune. This method should not be called outside of rune editing classes
     *
     * @param newAttribute The new attribute for the Rune
     */
    public void setMainAttribute(MainAttribute newAttribute)
    {
        mainAttribute = newAttribute;
    }
    
    /**
     * Gets the rune's sub attributes
     *
     * @return The rune's sub attributes
     */
    public ArrayList<SubAttribute> getSubAttributes()
    {
        return subAttributes;
    }
}