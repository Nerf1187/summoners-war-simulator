package Monsters.Wind;

import Abilities.*;
import Monsters.*;
import Runes.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Wind Weapon Master
 */
public class Dominic extends Monster
{
    private Ability passiveAbility;
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Dominic()
    {
        this("Dominic1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Dominic(String runeFileName)
    {
        super("Dominic" + count, Element.WIND, 10_875, 615, 801, 102, 30, 50, 15, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.UNRECOVERABLE.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(50);
        Ability a1 = new Attack_Ability("Glaive Slash (1)", 1.9 * 1.25, 0, 2, "Attacks the enemy 2 times to disturb the " +
                                                                               "target's HP recovery for 2 turns with a 30% chance each.", ability1Debuffs, ability1DebuffChances, 0, false, false, false);
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(DebuffEffect.BRAND.getNum(), 2, 0);
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(100);
        //Only activates once since only the first hit applies Brand
        Ability a2 = new Attack_Ability("Weakness Shot (2)", 0 /*Calculated on nextTurn()*/, 0, 1, "Attacks the enemy to leave a Branding effect for 2 turns and attacks 3 more times. The damage increases as the enemy's HP condition is bad.", ability2Debuffs,
                ability2DebuffChances, 3, false, false, false);
        
        
        //@Passive:Creation
        Ability a3 = new Passive("Improvisation", "Attacks additionally, dealing damage equal to 100% of your Attack Power when attacking an enemy on " +
                                                   "your turn. This attack increases the damage by 100% if your HP exceeds 50% and recovers the HP by the damage dealt if it's 50% or below.");
        
        //@Passive:Attack ability
        passiveAbility = new Attack_Ability("", 2.0 * 1.2, 0, 1, "", 0, false, false, false);
        
        Ability a4 = new Leader_Skill(RuneAttribute.HP, 0.33, Element.ALL);
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //Increase ability 2 damage based on missing HP
        abilities.get(1).setDmgMultiplier((3.1 - 1.2 * (target.getHpRatio() / 100)) * 1.35);
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        if (abilityNum == 2)
        {
            //Attack 3 more times without trying to apply Brand
            //This must be a loop as the dmg multiplier will change for each hit
            for (int i = 0; i < 3; i++)
            {
                attack(target, new Attack_Ability("", (3.1 - 1.2 * (target.getHpRatio() / 100)) * 1.35, 0, 1, "", 0,
                        false, false, false));
            }
        }
        
        //@Passive
        //Attack with the extra hit
        if (this.passiveCanActivate())
        {
            //Reset damage multiplier
            passiveAbility.setDmgMultiplier(2.0);
            passiveAbility.setHealingPercent(0);
            //Double the damage when over half HP
            if (getHpRatio() > 50.0)
            {
                passiveAbility.setDmgMultiplier(4.0);
            }
            else //Life steal by 100% of passive attack's dmg
            {
                passiveAbility.setHealingPercent(100);
            }
            attack(target, passiveAbility, false, true);
        }
        
        afterTurnProtocol(target, true);
        return true;
    }
}
