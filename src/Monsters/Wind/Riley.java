package Monsters.Wind;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.*;
import java.util.*;


public class Riley extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
    private static int count = 1;
    Stat totemCount = new Stat(999_999);
    
    
    public Riley()
    {
        super("Riley" + count, WIND, 11_850, 714, 637, 97, 15, 50, 40, 0);
        setRunes(MonsterRunes.getRunesFromFile("Riley1.csv", this));
        setAbilities();
        count++;
        totemCount.setStatNum(Stat.TOTEM);
        addOtherStat(totemCount);
    }
    
    
    public Riley(String fileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    private void setAbilities()
    {
        
        abilities.add(new Attack_Ability("Totem Magic (1)", 1.2 * 3.9, 0, 1, "Attacks the enemy target and recovers" +
                " the HP of an ally with the lowest HP ratio by 15%.", 0, false, false));
        
        ArrayList<Buff> ability2Buffs = abilityBuffs(Buff.IMMUNITY, 1);
        ArrayList<Integer> ability2BuffChances = abilityChances(100);
        abilities.add(new Heal_Ability("Dance with the Elemental (2)", 0, 1, "Grants Immunity on all allies for 1 turn.", ability2Buffs,
                ability2BuffChances, 3, true));
        
        ArrayList<Buff> ability3Buffs = abilityBuffs(Buff.ATK_UP, 2, Buff.REMOVE_DEBUFF, 0);
        ArrayList<Integer> ability3BuffChances = abilityChances(100, 100);
        abilities.add(new Heal_Ability("Valiant Boar Soul (3)", 1.15 * 0.3, 1, "Removes 1 harmful effect of all allies," +
                " recovers the HP by 30% and increases the Attack Power for 2 turns.", ability3Buffs, ability3BuffChances, 3, true));
        
        ArrayList<Buff> ability4Buffs = abilityBuffs(Buff.ATK_UP, 2, Buff.IMMUNITY, 1);
        ArrayList<Integer> ability4BuffChances = abilityChances(100, 100);
        abilities.add(new Heal_Ability("Grassland Tribe Totem (4)", 1.2 * 0.2, 1, "Stacks 1 totem whenever you're attacked " +
                "by a target during the enemy's turn or use a skill during your turn. Becomes able to use the [Grassland Tribe Totem] skill when you " +
                "stack 3 totems. Recovers the " +
                "HP of all allies by 20%, increases the Attack Power for 2 turns and grants Immunity for 1 turn when you use the skill.", ability4Buffs,
                ability4BuffChances,
                0, true));
        
        super.setAbilities(abilities);
    }
    
    public boolean abilityIsValid(int abilityNum)
    {
        if (abilityNum == 4)
        {
            return totemCount.getNumOfSpecialEffects() >= 3;
        }
        return super.abilityIsValid(abilityNum);
    }
    
    public boolean abilityIsValid(Ability ability)
    {
        if (ability.equals(abilities.get(3)))
        {
            return totemCount.getNumOfSpecialEffects() >= 3;
        }
        return super.abilityIsValid(ability);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        if (totemCount.getNumOfSpecialEffects() < 3 && abilityNum == 4)
        {
            System.out.println("Ability not ready yet");
            return false;
        }
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        Team team = (game.getNextMonsTeam().size() > 0) ? game.getNextMonsTeam() : Auto_Play.getHighestAtkBar();
        switch (abilityNum)
        {
            case 1 ->
            {
                double lowestRatio = getHpRatio();
                ArrayList<Monster> lowestRatioMons = new ArrayList<>();
                lowestRatioMons.add(this);
                for (int i = 0; i < team.size(); i++)
                {
                    Monster monster = team.get(i);
                    if (monster.getHpRatio() < lowestRatio && !monster.isDead())
                    {
                        lowestRatioMons.clear();
                        lowestRatioMons.add(monster);
                        lowestRatio = monster.getHpRatio();
                    }
                    if (monster.getHpRatio() == lowestRatio)
                    {
                        lowestRatioMons.add(monster);
                    }
                }
                heal(lowestRatioMons.get(new Random().nextInt(lowestRatioMons.size())), new Heal_Ability("", 1.15 * 0.15, 1, "",
                        0, false));
            }
            case 2 ->
            {
                for (int i = 0; i < team.size(); i++)
                {
                    Monster m = team.get(i);
                    if (!m.equals(target))
                    {
                        heal(m, abilities.get(1));
                    }
                }
            }
            case 3 ->
            {
                for (int i = 0; i < team.size(); i++)
                {
                    Monster m = team.get(i);
                    if (!m.equals(target))
                    {
                        heal(m, abilities.get(2));
                    }
                }
            }
            case 4 ->
            {
                for (int i = 0; i < team.size(); i++)
                {
                    Monster m = team.get(i);
                    if (!m.equals(target))
                    {
                        heal(m, abilities.get(3));
                    }
                }
                totemCount.setNumOfSpecialEffects(0);
            }
        }
        
        afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
    
    public void afterTurnProtocol(Object o, boolean attack)
    {
        super.afterTurnProtocol(o, attack);
        totemCount.setNumOfSpecialEffects(totemCount.getNumOfSpecialEffects() + 1);
    }
    
    public void attacked(Monster attacker)
    {
        if (totemCount.getNumOfSpecialEffects() < 3)
        {
            totemCount.setNumOfSpecialEffects(totemCount.getNumOfSpecialEffects() + 1);
        }
        super.attacked(attacker);
    }
}
