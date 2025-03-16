package Monsters.Water;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

/**
 * Water Oracle
 */
public class Praha extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
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
        super("Praha" + count, WATER, 11_040, 714, 692, 100, 15, 50, 15, 25);
        super.setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        abilities.add(new Attack_Ability("Passing Time (1)", 1.3 * 1.30, 0, 3, "Attacks the enemy 3 times and recovers 10% of the Attack Bar each time the attack lands as a Critical Hit.", 0, false, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.NULL, 1, 1);
        ArrayList<Integer> ability2DebuffChances = abilityChances(90);
        abilities.add(new Attack_Ability("Predicted Future (2)", 3.0 * 1.30, 0, 1, "Attacks all enemies with an 90% chance to remove beneficial effects, inflicting Continuous Damage equal to the number of removed beneficial effects for 2 turns.",
                ability2Debuffs, ability2DebuffChances, 3, false, false, true));
        
        ArrayList<Buff> ability3Buffs = abilityBuffs(Buff.ATK_SPD_UP, 2);
        ArrayList<Integer> ability3BuffChances = abilityChances(100);
        abilities.add(new Heal_Ability("Daydream (3)", 50 * 1.2, 1, "Recovers the HP of all allies by 50% by putting yourself to an irresistible sleep for 1 turn. In addition, puts all enemies to sleep for 1 turn with a 100% chance and increases " +
                                                                    "Attack Speed of all allies for 2 turns.", ability3Buffs, ability3BuffChances, 4, true));
        
        abilities.add(new Leader_Skill(Stat.DEF, 41, ALL));
        
        super.setAbilities(abilities);
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
                if (mon.containsDebuff(Debuff.NULL))
                {
                    mon.removeDebuff(Debuff.NULL);
                    int buffs = mon.strip();
                    for (int i = 0; i < buffs; i++)
                    {
                        mon.addGuaranteedAppliedDebuff(Debuff.CONTINUOUS_DMG, 2, this);
                    }
                }
            }));
        }
        
        if (abilityNum == 3)
        {
            //Put self to sleep and attempt to put every enemy to sleep
            this.addGuaranteedAppliedDebuff(Debuff.SLEEP, 1, this);
            applyToTeam(game.getOtherTeam(), (mon -> {
                mon.addAppliedDebuff(Debuff.SLEEP, 100, 1, this);
            }));
        }
        
        super.afterTurnProtocol((abilityNum == 2) ? game.getOtherTeam() : target, abilityNum != 3);
        return true;
    }
    
    public void selfAfterHitProtocol(Monster target, int abilityNum)
    {
        //Increase attack bar if ability 1 was used and the hit crit the enemy
        if (abilityNum == 1 && target.wasCrit())
        {
            this.increaseAtkBarByPercent(10);
        }
        
        super.selfAfterHitProtocol(target, abilityNum);
    }
}