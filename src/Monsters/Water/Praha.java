package Monsters.Water;

import Abilities.*;
import Monsters.*;
import Runes.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Water Oracle
 */
public class Praha extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Praha()
    {
        this("Praha1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Praha(String runeFileName)
    {
        super("Praha" + count, Element.WATER, 11_040, 714, 692, 100, 15, 50, 15, 25);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        Ability a1 = new Attack_Ability("Passing Time (1)", 1.3 * 1.30, 0, 3, "Attacks the enemy 3 times and recovers 10% of the Attack Bar each time the attack lands as a Critical Hit.", 0, false, false, false);
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(DebuffEffect.NULL.getNum(), 1, 1);
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(90);
        Ability a2 = new Attack_Ability("Predicted Future (2)", 3.0 * 1.30, 0, 1, "Attacks all enemies with an 90% chance to remove beneficial effects, inflicting Continuous Damage equal to the number of removed beneficial effects for 2 turns.",
                ability2Debuffs, ability2DebuffChances, 3, false, false, true);
        
        ArrayList<Buff> ability3Buffs = MONSTERS.abilityBuffs(BuffEffect.ATK_SPD_UP.getNum(), 2);
        ArrayList<Integer> ability3BuffChances = MONSTERS.abilityChances(100);
        Ability a3 = new Heal_Ability("Daydream (3)", 50 * 1.2, 1, "Recovers the HP of all allies by 50% by putting yourself to an irresistible sleep for 1 turn. In addition, puts all enemies to sleep for 1 turn with a 100% chance and increases " +
                                                                    "Attack Speed of all allies for 2 turns.", ability3Buffs, ability3BuffChances, 4, true);
        
        Ability a4 = new Leader_Skill(RuneAttribute.DEF, 41, Element.ALL);
        
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        if (abilityNum == 2)
        {
            //Check if the ability should remove beneficial effects for each enemy and apply the relevant DoT
            applyToTeam(game.getOtherTeam(), (mon -> {
                if (mon.containsDebuff(DebuffEffect.NULL))
                {
                    mon.removeAllOf(DebuffEffect.NULL);
                    int buffs = mon.strip(this);
                    for (int i = 0; i < buffs; i++)
                    {
                        mon.addGuaranteedAppliedDebuff(DebuffEffect.CONTINUOUS_DMG, 2, this);
                    }
                }
            }));
        }
        
        if (abilityNum == 3)
        {
            //Put self to sleep and attempt to put every enemy to sleep
            this.addGuaranteedAppliedDebuff(DebuffEffect.SLEEP, 1, this);
            applyToTeam(game.getOtherTeam(), (mon -> {
                mon.addAppliedDebuff(DebuffEffect.SLEEP, 100, 1, this);
            }));
        }
        
        super.afterTurnProtocol((abilityNum == 2) ? game.getOtherTeam() : target, abilityNum != 3);
        return true;
    }
    
    public void attackerAfterHitProtocol(Monster target, int abilityNum, int count)
    {
        //Increase attack bar if ability 1 was used and the hit crit the enemy
        if (abilityNum == 1 && target.wasCrit())
        {
            this.increaseAtkBarByPercent(10);
        }
        
        super.attackerAfterHitProtocol(target, abilityNum, count);
    }
}
