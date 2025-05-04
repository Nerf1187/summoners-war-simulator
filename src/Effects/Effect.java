package Effects;

import static Effects.OtherEffect.*;

/**
 * The parent class for all Buffs and Debuffs
 *
 * @author Anthony (Tony) Youssef
 */
public class Effect
{
    private int numTurns;
    private OtherEffect otherEffect = NULL;
    private int numOfSpecialEffects = 0;
    
    /**
     * Creates a new Effect (Not a buff or debuff)
     *
     * @param numTurns The number of turns to apply
     */
    public Effect(int numTurns)
    {
        this.numTurns = numTurns;
    }
    
    /**
     * Sets the Effect number (Not buff or debuff)
     *
     * @param num The number to set
     */
    public void setEffect(OtherEffect num)
    {
        otherEffect = num;
    }
    
    /**
     * Gets the Effects number
     *
     * @return The Effects number
     */
    public OtherEffect getEffect()
    {
        return otherEffect;
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
     * Gets the number of turns remaining for the Effect
     *
     * @return The number of turns remaining for the Effect
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
     * Compares two Effects
     *
     * @param effect The other Effect to compare
     * @return True if both Effects have the same effect number and the effect is not {@link OtherEffect#NULL}, false otherwise
     */
    public boolean equals(Effect effect)
    {
        return (effect.otherEffect == this.otherEffect) && (effect.otherEffect != NULL);
    }
    
    /**
     * Formats the Effect into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        return switch (otherEffect)
        {
            case BERSERK, THUNDERER -> "%s (%d turns remaining)".formatted(otherEffect, getNumTurns());
            case TOTEM, MAGIC_SPHERE -> "%s (%d)".formatted(otherEffect, numOfSpecialEffects);
            default -> "";
        };
    }
    
    /**
     * Gets the number of special effects this Effect has
     *
     * @return The number of special effects this Effect has
     */
    public int getNumOfSpecialEffects()
    {
        return numOfSpecialEffects;
    }
    
    /**
     * Sets the number of special effects this Effect has
     *
     * @param numOfSpecialEffects The number to set
     */
    public void setNumOfSpecialEffects(int numOfSpecialEffects)
    {
        this.numOfSpecialEffects = numOfSpecialEffects;
    }
}