package Monsters.Light;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * Light Oracle
 */
public class Laima extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Laima()
    {
        this("Laima1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Laima(String runeFileName)
    {
        super("Laima" + count, LIGHT, 12_015, 626, 714, 100, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        abilities.add(new Attack_Ability("Passing Time (1)", 1.3 * 1.30, 0, 3, "Attacks the enemy 3 times and recovers 10% of the Attack Bar each time the attack lands as a Critical Hit.", 0, false, false, false));
        
        abilities.add(new Attack_Ability("Predicted Future (2)", 3.0 * 1.3, 0, 1, "Attacks all enemies to remove beneficial effects granted on them with a 100% chance. Decreases their Attack Speed for 2 turns if you successfully remove beneficial effects.", 3, false, false, true));
        
        ArrayList<Buff> ability3Buffs = abilityBuffs(Buff.CLEANSE, 0, Buff.INVINCIBILITY, 1, Buff.IMMUNITY, 2, Buff.RECOVERY, 3);
        ArrayList<Integer> ability3BuffChances = abilityChances(100, 100, 100, 100);
        abilities.add(new Heal_Ability("Start and End (3)", 0 * 1.0, 1, "Removes the harmful effects on all allies and grants Invincibility for 1 turn and Immunity for 2 turns. Recovers their HP by 15% each for 3 turns afterwards.", ability3Buffs, ability3BuffChances, 4, true));
        
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
            //Attempt to strip each enemy target and decrease their speed
            applyToTeam(game.getOtherTeam(), (mon -> {
                if (resistanceCheck(mon) && mon.strip() > 0)
                {
                    mon.addAppliedDebuff(Debuff.DEC_ATK_SPD, 100, 2, this);
                }
            }));
        }
        
        super.afterTurnProtocol((abilityNum == 2) ? game.getOtherTeam() : target, abilityNum != 3);
        return true;
    }
    
    public void selfAfterHitProtocol(Monster target, int abilityNum)
    {
        //Check if the ability should remove beneficial effects for each enemy and apply the relevant DoT
        if (abilityNum == 1 && target.wasCrit())
        {
            this.increaseAtkBarByPercent(10);
        }
        
        super.selfAfterHitProtocol(target, abilityNum);
    }
}