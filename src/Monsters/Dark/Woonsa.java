package Monsters.Dark;

import Abilities.*;
import Monsters.*;
import Runes.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Dark Pioneer
 */
public class Woonsa extends Monster
{
    private static int count = 1;
    private int currentAbilityNum = -1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Woonsa()
    {
        this("Woonsa1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Woonsa(String runeFileName)
    {
        super("Woonsa" + count, Element.DARK, 12_015, 637, 703, 118, 15, 50, 15, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.SLEEP.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(25);
        Ability a1 = new Attack_Ability("Yin Yang Attack (1)", 1.3 * (3.6 + (0.09 * getMaxHp()) / getAtk()), 0, 1, "Attacks the enemy with the power of Yin and Yang and puts the enemy to sleep for 2 turns with a 25% chance. The damage " +
                                                                                                                    "increases according to your MAX HP.", ability1Debuffs, ability1DebuffChances, 0, false, false, false);
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(DebuffEffect.REMOVE_BENEFICIAL_EFFECT.getNum(), 0, 0, DebuffEffect.BLOCK_BENEFICIAL_EFFECTS.getNum(), 2, 0);
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(75, 75);
        Ability a2 = new Attack_Ability("North Star's Wisdom (2)", 3 * 1.25, 0, 2, "Attacks the enemy 2 times with each attack having a 75% chance to remove a beneficial effect at the enemy and a 75% chance to block the beneficial effects " +
                                                                                    "granted on the target for 2 turns. This attack won't land as Glancing Hits.", ability2Debuffs, ability2DebuffChances, 3, false, false, false);
        
        
        Ability a3 = new Attack_Ability("Inhale Magic (3)", 0 /*Damage will be calculated on attack*/, 1, 1,
                "Steals the HP of all enemies by 15% of your MAX HP and steals all beneficial effects on all enemies. Increases the Attack Bar of all allies by 20% if you successfully steal beneficial effects.", 5, false, false, true);
        
        Ability a4 = new Leader_Skill(RuneAttribute.DEF, 0.33, Element.ALL);
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //Get number of buffs before turn for ability 3
        int numOfBuffs = getAppliedBuffs().size();
        currentAbilityNum = abilityNum;
        boolean b = super.nextTurn(target, abilityNum);
        
        //Make sure ability 2 does not glance
        if (abilityNum == 2)
        {
            setAbilityGlancingRateChange(-999_999);
        }
        if (!b)
        {
            currentAbilityNum = -1;
            return false;
        }
        
        if (abilityNum == 3)
        {
            //Steal buffs and increase the attack bar if buffs were stolen
            applyToTeam(game.getOtherTeam(), this::stealAllBuffs);
            if (numOfBuffs < getAppliedBuffs().size())
            {
                applyToTeam(game.getNextMonsTeam(), m -> m.increaseAtkBarByPercent(20));
            }
        }
        //Reset glancing rate change
        setAbilityGlancingRateChange(0);
        afterTurnProtocol(target, true);
        return true;
    }
    
    public double dmgIncProtocol(double num)
    {
        //Set damage to equal 15% of overall health for ability 3
        if (currentAbilityNum == 3)
        {
            return getMaxHp() * 0.15;
        }
        return num;
    }
    
    public Monster copy()
    {
        Woonsa save = (Woonsa) super.copy();
        save.currentAbilityNum = this.currentAbilityNum;
        return save;
    }
    
    public void paste(Monster save)
    {
        if (!(save instanceof Woonsa))
        {
            return;
        }
        
        super.paste(save);
        this.currentAbilityNum = ((Woonsa) save).currentAbilityNum;
    }
}
