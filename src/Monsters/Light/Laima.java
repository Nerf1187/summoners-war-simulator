package Monsters.Light;

import Abilities.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Monsters.*;
import Util.Util.*;
import java.util.*;

/**
 * Light Oracle
 */
public class Laima extends Monster
{
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
        super("Laima" + count, Element.LIGHT, 12_015, 626, 714, 100, 15, 50, 15, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        Ability a1 = new Attack_Ability("Passing Time (1)", 1.3 * 1.30, 0, 3, "Attacks the enemy 3 times and recovers 10% of the Attack Bar each time the attack lands as a Critical Hit.", 0, false, false, false);
        
        Ability a2 = new Attack_Ability("Predicted Future (2)", 3.0 * 1.3, 0, 1, "Attacks all enemies to remove beneficial effects granted on them with a 100% chance. Decreases their Attack Speed for 2 turns if you successfully remove beneficial effects.", 3, false, false, true);
        
        ArrayList<Buff> ability3Buffs = MONSTERS.abilityBuffs(BuffEffect.CLEANSE.getNum(), 0, BuffEffect.INVINCIBILITY.getNum(), 1, BuffEffect.IMMUNITY.getNum(), 2, BuffEffect.RECOVERY.getNum(), 3);
        ArrayList<Integer> ability3BuffChances = MONSTERS.abilityChances(100, 100, 100, 100);
        Ability a3 = new Heal_Ability("Start and End (3)", 0 * 1.0, 1, "Removes the harmful effects on all allies and grants Invincibility for 1 turn and Immunity for 2 turns. Recovers their HP by 15% each for 3 turns afterwards.", ability3Buffs, ability3BuffChances, 4, true);
        
        super.setAbilities(a1, a2, a3);
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
            applyToTeam(game.getOtherTeam(), m -> {
                if (resistanceCheck(m) && m.strip(this) > 0)
                {
                    m.addAppliedDebuff(DebuffEffect.DEC_ATK_SPD, 100, 2, this);
                }
            });
        }
        
        super.afterTurnProtocol((abilityNum == 2) ? game.getOtherTeam() : target, abilityNum != 3);
        return true;
    }
    
    public void attackerAfterHitProtocol(Monster target, int abilityNum, int count)
    {
        //Check if the ability should remove beneficial effects for each enemy and apply the relevant DoT
        if (abilityNum == 1 && target.wasCrit())
        {
            this.increaseAtkBarByPercent(10);
        }
        
        super.attackerAfterHitProtocol(target, abilityNum, count);
    }
}
