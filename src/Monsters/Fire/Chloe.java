package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.*;
import java.util.*;


public class Chloe extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    public Chloe()
    {
        super("Chloe" + count, FIRE, 11_700, 648, 549, 111, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile("Chloe1.csv", this));
        setAbilities();
        count++;
    }
    
    public Chloe(String runeFileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        abilities.add(new Attack_Ability("Absorb Mana (1)", 1.35 * (1.8 + (0.12 * getMaxHp()) / getAtk()), 0.5, 1, "Attacks the " +
                "enemy and recovers the HP by 50% of the damage dealt. This attack will deal more damage according to your MAX HP.", 0, false,
                false));
        
        ArrayList<Buff> ability2Buffs = abilityBuffs(Buff.REMOVE_DEBUFF, 0);
        ArrayList<Integer> ability2BuffChances = abilityChances(100);
        abilities.add(new Heal_Ability("Fill (2)", 1.3 * 0.2, 1, "Removes 1 harmful effect from all allies " +
                "(excluding yourself) and recovers the HP of all allies (including yourself) by 20% each.", ability2Buffs, ability2BuffChances, 3, true));
        
        ArrayList<Buff> ability3Buffs = abilityBuffs(Buff.INVINCIBILITY, 1, Buff.IMMUNITY, 3);
        ArrayList<Integer> ability3BuffChances = abilityChances(100, 100);
        abilities.add(new Heal_Ability("Fanatic (3)", 0, 1, "All allies are invincible for 1 turn and gain Immunity for 3 turns.",
                ability3Buffs, ability3BuffChances, 5, true));
        
        abilities.add(new Leader_Skill(Stat.SPD, 0.2, ALL));
        
        super.setAbilities(abilities);
    }
    
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        switch (abilityNum)
        {
            case 2 ->
            {
                Team next = (game.getNextMonsTeam().size() > 0) ? game.getNextMonsTeam() : Auto_Play.getHighestAtkBar();
                for (int i = 0; i < next.size(); i++)
                {
                    Monster m = next.get(i);
                    
                    if (!m.equals(this) && !m.equals(target) && !m.isDead())
                    {
                        heal(m, abilities.get(1));
                    }
                    else if (m.equals(this))
                    {
                        heal(m, new Heal_Ability("", 1.3 * 1.2, 1, "", 0, false));
                    }
                }
            }
            case 3 ->
            {
                Team next = (game.getNextMonsTeam().size() > 0) ? game.getNextMonsTeam() : Auto_Play.getHighestAtkBar();
                for (int i = 0; i < next.size(); i++)
                {
                    if (next.get(i).equals(target) || next.get(i).isDead())
                    {
                        continue;
                    }
                    heal(next.get(i), abilities.get(2));
                }
            }
        }
        afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
}
