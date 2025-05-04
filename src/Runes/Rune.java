package Runes;

import Errors.*;
import Monsters.*;
import java.io.*;
import java.util.*;

import static Runes.RuneType.*;

/**
 * This class used to create and apply runes for Monsters
 *
 * @author Anthony (Tony) Youssef
 */
public class Rune
{
    private RuneType type;
    private MainAttribute mainAttribute;
    private final ArrayList<SubAttribute> subAttributes;
    private final Monster monster;
    private final int monBaseMaxHp, monBaseAtk, monBaseDef;
    private boolean applied = false;
    private static final BufferedInputStream runeKey = new BufferedInputStream(Objects.requireNonNull(Rune.class.getResourceAsStream("Rune key.csv")));
    
    /**
     * Creates a new Rune
     *
     * @param type          The rune type
     * @param mainAttribute The Main attribute
     * @param place         The rune's place (1-6 for runes, 7 or 8 for element and type artifacts respectively)
     * @param subAttributes The list of subAttributes
     * @param monster       The Monster to apply the rune to
     */
    public Rune(RuneType type, MainAttribute mainAttribute, int place, ArrayList<SubAttribute> subAttributes, Monster monster)
    {
        //Make sure the type and place are within the correct range
        if (place > 8 || place < 1)
        {
            throw new IndexOutOfBoundsException("Place must be between 1 and 8 inclusive");
        }
        
        //Make sure artifacts have the correct placement
        if (type == ELEMENT_ARTIFACT && place != 7)
        {
            throw new ConflictingArguments("Place must equal 7 for an element artifact");
        }
        if (type == TYPE_ARTIFACT && place != 8)
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
        type = RuneType.NONE;
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
        switch (attribute.getAttribute())
        {
            case ATK -> monster.setTempAtk(monster.getTempAtk() + attribute.getAmount());
            case ATK_PERCENT -> monster.setTempAtk((monBaseAtk * (1.0 * attribute.getAmount() / 100) + monster.getTempAtk()));
            case DEF -> monster.setTempDef(monster.getTempDef() + attribute.getAmount());
            case DEF_PERCENT -> monster.setTempDef((monBaseDef * (1.0 * attribute.getAmount() / 100) + monster.getTempDef()));
            case HP -> monster.setTempMaxHp(monster.getTempMaxHp() + attribute.getAmount());
            case HP_PERCENT -> monster.setTempMaxHp((monBaseMaxHp * (1.0 * attribute.getAmount() / 100) + monster.getTempMaxHp()));
            case SPD -> monster.setSpd(monster.getSpd() + attribute.getAmount());
            case CRIT_RATE -> monster.setCritRate(monster.getCritRate() + attribute.getAmount());
            case CRIT_DMG -> monster.setCritDmg(monster.getCritDmg() + attribute.getAmount());
            case RES -> monster.setResistance(monster.getResistance() + attribute.getAmount());
            case ACC -> monster.setAccuracy(monster.getAccuracy() + attribute.getAmount());
        }
    }
    
    /**
     * Retrieves the type of the rune.
     *
     * @return The rune type, represented as a {@code RuneType} object.
     */
    public RuneType getType()
    {
        return type;
    }
    
    /**
     * Sets the rune type to a new value
     *
     * @param type The new value
     */
    public void setType(RuneType type)
    {
        this.type = type;
    }
    
    /**
     * Formats the rune into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        StringBuilder s = new StringBuilder(type.toString());
        //Get the main attribute followed by ach sub-attribute on separate lines
        s.append("\tMain Attribute:\n\t\t");
        s.append(mainAttribute).append("\n\tSub Attributes:\n");
        for (SubAttribute sub : subAttributes)
        {
            s.append("\t\t").append(sub).append("\n");
        }
        return s.toString();
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