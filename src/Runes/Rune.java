package Runes;

import Errors.*;
import Monsters.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author Anthony (Tony) Youssef
 * This class used to create and apply runes for Monsters
 */
public class Rune
{
    public static final int ATK = 1, ATKPERCENT = 2, DEF = 3, DEFPERCENT = 4, HP = 5, HPPERCENT = 6, SPD = 7, CRITRATE = 8, CRITDMG = 9, RES = 10, ACC = 11;
    public static final int ENERGY = 1, FATAL = 2, BLADE = 3, SWIFT = 4, FOCUS = 5, GUARD = 6, ENDURE = 7, SHIELD = 8, REVENGE = 9, WILL = 10, NEMESIS = 11;
    public static final int VAMPIRE = 12, DESTROY = 13, DESPAIR = 14, VIOLENT = 15, RAGE = 16, FIGHT = 17, DETERMINATION = 18, ENHANCE = 19, ACCURACY = 20,
            TOLERANCE = 21, SEAL = 24;
    public static final int ELEMENTARTIFACT = 22, TYPEARTIFACT = 23;
    private final int type;
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
        if (type > TYPEARTIFACT || place > 8)
        {
            throw new IndexOutOfBoundsException("Type must be less than 25 and place must be less than 9");
        }
        if (type == ELEMENTARTIFACT && place != 7)
        {
            throw new ConflictingArguments("Place must equal 7 for an element artifact");
        }
        if (type == TYPEARTIFACT && place != 8)
        {
            throw new ConflictingArguments("Place must be 8 for a type artifact");
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
        if (applied)
        {
            return;
        }
        
        applied = true;
        switch (mainAttribute.getNum())
        {
            case ATK -> monster.setTempAtk(monster.getTempAtk() + mainAttribute.getAmount());
            case ATKPERCENT -> monster.setTempAtk((monBaseAtk * (1.0 * mainAttribute.getAmount() / 100) + monster.getTempAtk()));
            case DEF -> monster.setTempDef(monster.getTempDef() + mainAttribute.getAmount());
            case DEFPERCENT -> monster.setTempDef((monBaseDef * (1.0 * mainAttribute.getAmount() / 100) + monster.getTempDef()));
            case HP -> monster.setTempMaxHp(monster.getTempMaxHp() + mainAttribute.getAmount());
            case HPPERCENT -> monster.setTempMaxHp((monBaseMaxHp * (1.0 * mainAttribute.getAmount() / 100) + monster.getTempMaxHp()));
            case SPD -> monster.setSpd(monster.getSpd() + mainAttribute.getAmount());
            case CRITRATE -> monster.setCritRate(monster.getCritRate() + mainAttribute.getAmount());
            case CRITDMG -> monster.setCritDmg(monster.getCritDmg() + mainAttribute.getAmount());
            case RES -> monster.setResistance(monster.getResistance() + mainAttribute.getAmount());
            case ACC -> monster.setAccuracy(monster.getAccuracy() + mainAttribute.getAmount());
        }
        
        for (SubAttribute sub : subAttributes)
        {
            switch (sub.getNum())
            {
                case ATK -> monster.setTempAtk(monster.getTempAtk() + sub.getAmount());
                case ATKPERCENT -> monster.setTempAtk((monBaseAtk * (1.0 * sub.getAmount() / 100) + monster.getTempAtk()));
                case DEF -> monster.setTempDef(monster.getTempDef() + sub.getAmount());
                case DEFPERCENT -> monster.setTempDef((monBaseDef * (1.0 * sub.getAmount() / 100) + monster.getTempDef()));
                case HP -> monster.setTempMaxHp(monster.getTempMaxHp() + sub.getAmount());
                case HPPERCENT -> monster.setTempMaxHp((monBaseMaxHp * (1.0 * sub.getAmount() / 100) + monster.getTempMaxHp()));
                case SPD -> monster.setSpd(monster.getSpd() + sub.getAmount());
                case CRITRATE -> monster.setCritRate(monster.getCritRate() + sub.getAmount());
                case CRITDMG -> monster.setCritDmg(monster.getCritDmg() + sub.getAmount());
                case RES -> monster.setResistance(monster.getResistance() + sub.getAmount());
                case ACC -> monster.setAccuracy(monster.getAccuracy() + sub.getAmount());
            }
        }
    }
    
    /**
     * @return The rune's type
     */
    public int getType()
    {
        return type;
    }
    
    /**
     * Formats the rune into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        String s = numToType(type);
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
            Scanner read = new Scanner(Objects.requireNonNull(Rune.class.getResourceAsStream("Rune key.csv")));
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
     * Converts a given variable name into an attribute number
     *
     * @param str a String containing the variable name
     * @return The attribute number with the given variable name if possible, -1 otherwise
     */
    public static int stringToNum(String str)
    {
        str = str.toUpperCase();
        int returnVal = -1;
        try
        {
            Field f = Rune.class.getField(str);
            Class type = f.getType();
            if (type.toString().equals("int"))
            {
                returnVal = f.getInt(new Rune());
            }
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            System.err.println(e);
            return -1;
        }
        
        return returnVal;
    }
    
    /**
     * @return The rune's main attribute
     */
    public MainAttribute getMainAttribute()
    {
        return mainAttribute;
    }
    
    /**
     * Sets the main attribute for the rune. This method should not be called outside of rune editing classes
     * @param newAttribute The new attribute for the Rune
     */
    public void setMainAttribute(MainAttribute newAttribute)
    {
        mainAttribute = newAttribute;
    }
    
    /**
     * @return the rune's sub attributes
     */
    public ArrayList<SubAttribute> getSubAttributes()
    {
        return subAttributes;
    }
}