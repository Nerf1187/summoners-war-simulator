package Monsters.Water;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;


public class Ariel extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    public Ariel()
    {
        super("Ariel" + count, WATER, 11_850, 747, 604, 100, 15, 50, 40, 0);
        setRunes(MonsterRunes.getRunesFromFile("Ariel1.csv", this));
        setAbilities();
        count++;
    }
    
    public Ariel(String runeFileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.REMOVE_BENEFICIAL_EFFECT, 0, 1);
        ArrayList<Integer> ability1DebuffChances = abilityChances(75);
        abilities.add(new Attack_Ability("Heavenly Sword (1)", 1.3 * (2.0 + ((getDef() * 1.05 * 2.95)) / (getAtk() * 1.15)), 0, 1,
                "Attacks with a heavenly sword, removing a beneficial effect on the enemy with a 75% chance. The damage increases according to your " +
                        "Defense.",
                ability1Debuffs, ability1DebuffChances, 0, false, false));
        
        ArrayList<Buff> ability2Buffs = abilityBuffs(Buff.DEF_UP, 2);
        ArrayList<Integer> ability2BuffChances = abilityChances(100);
        abilities.add(new Heal_Ability("Archangel's Blessing (2)", 0.35, 1, "Recovers an ally's HP by 35% and increases the " +
                "Defense for 2 turns.", ability2Buffs, ability2BuffChances, 2, false));
        
        abilities.add(new Heal_Ability("Holy Water (3)", 0.5 * 1.2, 1, "Removes all harmful effects granted on all allies and " +
                "recovers their HP by 50% each. In addition, increases the Attack Bar of all allies by 20% each and recovers 15% HP each turn for 3 " +
                "turns by the number of harmful" +
                " effects removed on the allies who had their harmful effects removed.", 4, true));
        
        abilities.add(new Leader_Skill(Stat.HP, 0.5, WATER));
        
        super.setAbilities(abilities);
    }
    
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        if (abilityNum == 3 && abilities.get(2).getTurnsRemaining() == 0)
        {
            Team team = (game.getNextMonsTeam().size() > 0) ? game.getNextMonsTeam() : Auto_Play.getHighestAtkBar();
            for (int i = 0; i < team.size(); i++)
            {
                Monster m = team.get(i);
                m.cleanse();
            }
        }
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        if (abilityNum == 3)
        {
            Team team = (game.getNextMonsTeam().size() > 0) ? game.getNextMonsTeam() : Auto_Play.getHighestAtkBar();
            for (int i = 0; i < team.size(); i++)
            {
                Monster m = team.get(i);
                if (!m.equals(target))
                {
                    heal(m, abilities.get(2));
                }
                int cleansed = m.cleanse();
                for (int j = 0; j < cleansed; j++)
                {
                    if (m.equals(this))
                    {
                        m.addAppliedBuff(Buff.RECOVERY, 4, this);
                    }
                    else
                    {
                        m.addAppliedBuff(Buff.RECOVERY, 3, this);
                    }
                }
                m.increaseAtkBarByPercent(20);
            }
        }
        super.afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
}
