package Monsters.Water;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

/**
 * Water Archangel
 */
public class Ariel extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Ariel()
    {
        this("Ariel1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Ariel(String runeFileName)
    {
        super("Ariel" + count, WATER, 11_850, 747, 604, 100, 15, 50, 40, 0);
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.REMOVE_BENEFICIAL_EFFECT, 0, 1);
        ArrayList<Integer> ability1DebuffChances = abilityChances(75);
        abilities.add(new Attack_Ability("Heavenly Sword (1)", 1.3 * (1.9 + (getDef() * 3.0) / getAtk()), 0, 1,
                "Attacks with a heavenly sword, removing a beneficial effect on the enemy with a 75% chance. The damage increases according to your " +
                "Defense.", ability1Debuffs, ability1DebuffChances, 0, false, false, false));
        
        ArrayList<Buff> ability2Buffs = abilityBuffs(Buff.DEF_UP, 2);
        ArrayList<Integer> ability2BuffChances = abilityChances(100);
        abilities.add(new Heal_Ability("Archangel's Blessing (2)", 0.35, 1, "Recovers an ally's HP by 35% and increases the " +
                                                                            "Defense for 2 turns.", ability2Buffs, ability2BuffChances, 2, false));
        
        ArrayList<Buff> ability3Buffs = new ArrayList<>();
        ability3Buffs.add(new IncAtkBar(20));
        ArrayList<Integer> ability3BuffChances = abilityChances(100);
        abilities.add(new Heal_Ability("Holy Water (3)", 0.5 * 1.2, 1, "Removes all harmful effects granted on all allies and " +
                                                                       "recovers their HP by 50% each. In addition, increases the Attack Bar of all allies by 20% each and recovers 15% HP each turn for 3 " +
                                                                       "turns by the number of harmful effects removed on the allies who had their harmful effects removed.", ability3Buffs, ability3BuffChances, 4, true));
        
        abilities.add(new Leader_Skill(Stat.HP, 0.5, WATER));
        
        super.setAbilities(abilities);
    }
    
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        //Apply recovery for each debuff cleansed
        if (abilityNum == 3)
        {
            applyToTeam(game.getNextMonsTeam(), m -> {
                for (int i = 0; i < m.cleanse(); i++)
                {
                    m.addAppliedBuff(Buff.RECOVERY, 3, this);
                }
            });
        }
        super.afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
}