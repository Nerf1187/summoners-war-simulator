package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Util.Util.RUNES;
import java.util.*;

/**
 * Fire Dragon Knight
 */
public class Laika extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Laika()
    {
        this("Laika1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Laika(String runeFileName)
    {
        super("Laika" + count, Element.FIRE, 11_040, 571, 834, 100, 15, 50, 15, 0);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        Ability a1 = new Attack_Ability("Dragon's Might (1)", 4.3 * 1.3, 0.5, 1, "Attacks the enemy, inflicting Continuous " + "Damage for 2 turns if the attack lands as a critical hit. Also recovers HP by 50% of the inflicted damage.", 0, false,
                false, false);
        
        Ability a2 = new Attack_Ability("Justice (2)", 0 /*calculated at nextTurn()*/, 0, 1,
                "Channels burning rage to inflict damage that ignores all damage reduction effects to the enemy. The damage increases according to the number of dead allies.", 2, false, true, false);
        
        //@Passive:Creation
        Ability a3 = new Passive("Noble Blood", "Your attacks won't land as Glancing Hits and the inflicted damage of one attack won't exceed 24% of the MAX HP. Additionally, counterattacks the attacker with a 50% chance when you're attacked.", 0);
        
        Ability a4 = new Leader_Skill(RuneAttribute.DEF, 0.5, Element.FIRE);
        
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //Reset glancing rate
        setAbilityGlancingRateChange(0);
        
        //@Passive
        //Prevent Laika from landing a glancing hit
        if (this.passiveCanActivate())
        {
            setAbilityGlancingRateChange(-999_999);
        }
        
        //Get number of living Monsters
        Team friendlyTeam = game.getNextMonsTeam();
        int numOfAliveMons = friendlyTeam.numOfLivingMons();
        //Increase attack based on the number of dead allies
        abilities.get(1).setDmgMultiplier((13.5 - 5.5 * (1.0 * numOfAliveMons / friendlyTeam.size())) * 1.3);
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        if (target.wasCrit())
        {
            //Apply continuous damage if the attack landed as a crit
            if (abilityNum == 1)
            {
                target.addAppliedDebuff(DebuffEffect.CONTINUOUS_DMG, 100, 2, this);
            }
        }
        
        super.afterTurnProtocol(target, !Game.canCounter(), true);
        return true;
    }
    
    public double dmgReductionProtocol(double num, boolean self)
    {
        //@Passive
        //Prevent damage from being over 24% of the max HP
        if (!self || !this.passiveCanActivate())
        {
            return num;
        }
        return Math.min(getMaxHp() * 0.24, num);
    }
    
    public void attacked(Monster attacker)
    {
        //@Passive
        //Counter with a 50% chance if the attack was a crit
        if (new Random().nextInt(101) < 50 && this.passiveCanActivate())
        {
            addAppliedBuff(BuffEffect.COUNTER, 0, this);
        }
        super.attacked(attacker);
    }
}
