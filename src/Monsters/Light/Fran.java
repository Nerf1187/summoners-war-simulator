package Monsters.Light;

import Abilities.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Monsters.*;
import Runes.*;
import Util.Util.*;
import java.util.*;

/**
 * Light Fairy Queen
 */
public class Fran extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Fran()
    {
        this("Fran1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Fran(String runeFileName)
    {
        super("Fran" + count, Element.LIGHT, 10_215, 461, 670, 103, 15, 50, 15, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.DEC_ATK.getNum(), 2, 0);
        ability1Debuffs.add(new DecAtkBar(10));
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(40, 40);
        Ability a1 = new Attack_Ability("Light Hurricane (1)", 1.2 * 1.30, 0, 3, "Attacks the enemy 3 times with a whirling storm of light. Each attack has a 20% chance to weaken the Attack Power for 2 turns and decrease the Attack Bar by 10%.", ability1Debuffs, ability1DebuffChances, 0, false, false, false);
        
        ArrayList<Buff> ability2Buffs = MONSTERS.abilityBuffs(BuffEffect.CLEANSE.getNum(), 0);
        ArrayList<Integer> ability2BuffChances = MONSTERS.abilityChances(100);
        Heal_Ability a2 = new Heal_Ability("Purify (2)", 4 * 1.30, 1, "Removes all harmful effects on the target ally and recovers its HP. The recovery amount is proportionate to the Attack Power.", ability2Buffs, ability2BuffChances, 2, false);
        a2.setHealingMultiplierStat(RuneAttribute.ATK);
        
        ArrayList<Buff> ability3Buffs = MONSTERS.abilityBuffs(BuffEffect.ATK_UP.getNum(), 2, BuffEffect.IMMUNITY.getNum(), 2);
        ArrayList<Integer> ability3BuffChances = MONSTERS.abilityChances(100, 100);
        Heal_Ability a3 = new Heal_Ability("Fairy's Blessing (3)", 3 * 1.25, 1, "Recovers the HP of all allies, increases the Attack Power and grants immunity for 2 turns. The recovery amount is proportionate to the Attack Power.", ability3Buffs,
                ability3BuffChances, 4, true);
        a3.setHealingMultiplierStat(RuneAttribute.ATK);
        
        Ability a4 = new Leader_Skill(RuneAttribute.SPD, 10, Element.ALL);
        
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        super.afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
}