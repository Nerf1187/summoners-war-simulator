package Monsters.Dark;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.*;
import Stats.Debuffs.*;
import java.util.*;

public class Woonsa extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    private int currentAbilityNum = -1;
    
    public Woonsa()
    {
        super("Woonsa" + count, DARK, 12_015, 637, 703, 118, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile("Woonsa1.csv", this));
        setAbilities();
        count++;
    }
    
    public Woonsa(String runeFileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.SLEEP, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(25);
        abilities.add(new Attack_Ability("Yin Yang Attack (1)", 1.3 * (3.6 + (0.09 * getMaxHp()) / getAtk()), 0, 1, "Attacks the enemy with the power of Yin and Yang and puts the enemy to sleep for 2 turns with a 25% chance. The damage " +
                "increases according to your MAX HP.", ability1Debuffs, ability1DebuffChances, 0, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.REMOVE_BENEFICIAL_EFFECT, 0, 0, Debuff.BLOCK_BENEFICIAL_EFFECTS, 2, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(75, 75);
        abilities.add(new Attack_Ability("North Star's Wisdom (2)", 3 * 1.25, 0, 2, "Attacks the enemy 2 times with each attack having a 75% chance to remove a beneficial effect at the enemy and a 75% chance to block the beneficial effects " +
                "granted on the target for 2 turns. This attack won't land as Glancing Hits.", ability2Debuffs, ability2DebuffChances, 3, false, false));
        
        /*Damage will be calculated on attack*/
        abilities.add(new Attack_Ability("Inhale Magic (3)", 0, 1, 1, "Steals the HP of all enemies by 15% of your MAX HP and steals all beneficial effects on all enemies. Increases the Attack Bar of all allies by 20% if you successfully " +
                "steal beneficial effects.", 5, false, false));
        
        abilities.add(new Leader_Skill(Stat.DEF, 0.33, Monster.ALL));
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        int numOfBuffs = getAppliedBuffs().size();
        currentAbilityNum = abilityNum;
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            currentAbilityNum = -1;
            return false;
        }
        
        if (abilityNum == 2)
        {
            setAbilityGlancingRateChange(Integer.MIN_VALUE);
        }
        else
        {
            setAbilityGlancingRateChange(0);
            if (abilityNum == 3)
            {
                Team other = (game.getOtherTeam().size() > 0) ? game.getOtherTeam() : Auto_Play.getOther();
                for (int i = 0; i < other.size(); i++)
                {
                    Monster m = other.get(i);
                    if (!m.equals(target) && !m.isDead())
                    {
                        attack(m, abilities.get(2));
                        for (int j = m.getAppliedBuffs().size() - 1; j >= 0; j--)
                        {
                            stealBuff(target);
                        }
                    }
                }
                if (numOfBuffs < getAppliedBuffs().size())
                {
                    Team team = (game.getTeamWithHighestAtkBar().size() > 0) ? game.getTeamWithHighestAtkBar() : Auto_Play.getHighestAtkBar();
                    for (int i = 0; i < team.size(); i++)
                    {
                        Monster m = team.get(i);
                        m.increaseAtkBarByPercent(20);
                    }
                }
            }
        }
        afterTurnProtocol(target, true);
        return true;
    }
    
    public double dmgIncProtocol(double num)
    {
        if (currentAbilityNum == 3)
        {
            return getMaxHp() * 0.15;
        }
        return num;
    }
}
