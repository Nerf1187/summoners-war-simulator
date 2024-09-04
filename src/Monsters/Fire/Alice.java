package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

public class Alice extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    private final int maxSpd;
    private int currentSpd;
    private boolean dmgReductionActive = false;
    
    public Alice()
    {
        super("Alice" + count, FIRE, 9_555, 900, 604, 101, 15, 50, 40, 0);
        maxSpd = getSpd();
        super.setRunes(MonsterRunes.getRunesFromFile("Alice1.csv", this));
        setAbilities();
        currentSpd = this.getSpd();
        count++;
    }
    
    public Alice(String runeFileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.DEC_DEF, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(70);
        abilities.add(new Attack_Ability("Macaron Boomerang (1)", 1.25 * (1.8 + (2.7 * getDef()) / getAtk()), 0, 1, "Attacks the enemy to decrease Defense for 2 turns with a 40% chance. This attack will deal more damage according to your Defense."
                , ability1Debuffs, ability1DebuffChances, 0, false, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.STUN, 1, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Crash! Macaron (2)", 1.25 * (2 + (3.2 * getDef()) / getAtk()), 0, 1,
                "Escorts the ally with the lowest HP for 3 turns, then charges towards the enemy target. Deals damage that increases according to your Defense on the attacked target and stuns for 1 turn.", ability2Debuffs,
                ability2DebuffChances, 3, false, false, false));
        
        ArrayList<Buff> ability3Buffs = abilityBuffs(Buff.DEF_UP, 1);
        ArrayList<Integer> ability3BuffChances = abilityChances(100);
        abilities.add(new Ability("Macaron Shield (3)", 0, 0, 1,
                "Decreases the damage that your allies take by 200% of your Defense until the next turn starts. You will gain immunity to inability effects and " +
                        "have your Attack Speed decreased by 30%. In addition, increases the Defense of all allies for 1 turn.", ability3Buffs, ability3BuffChances, 3, false, false, false, true, false, false, 0));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        setDmgReductionActive(false);
        for (Monster mon : game.getNextMonsTeam().getMonsters())
        {
            mon.removeOtherStat(Stat.MACARON_SHIELD);
        }
        
        setCurrentSpd(getMaxSpd());
        switch (abilityNum)
        {
            case 2:
            {
                int minHP = -1;
                int minIndex = -1;
                
                Team next = game.getNextMonsTeam();
                for (int i = 0; i < next.size(); i++)
                {
                    Monster m = next.get(i);
                    if (m.getCurrentHp() < minHP)
                    {
                        minHP = m.getCurrentHp();
                        minIndex = i;
                    }
                }
                Monster m = next.get(minIndex);
                m.addAppliedBuff(Buff.DEFEND, 3, this);
                break;
            }
            case 3:
            {
                setDmgReductionActive(true);
                setCurrentSpd(100);
                
                applyToTeam(game.getNextMonsTeam(), m -> {
                    Stat s = new Stat(Integer.MAX_VALUE);
                    s.setStatNum(Stat.MACARON_SHIELD);
                    m.addOtherStat(s);
                });
            }
        }
        afterTurnProtocol(target, abilityNum != 3);
        return true;
    }
    
    /**
     * Checks if Macaron Shield is active
     * @return True if Macaron Shield is active, false otherwise
     */
    public boolean dmgReductionIsActive()
    {
        return dmgReductionActive;
    }
    
    /**
     * Sets the Macaron Shield status
     * @param dmgReductionActive The new status of Macaron Shield
     */
    public void setDmgReductionActive(boolean dmgReductionActive)
    {
        this.dmgReductionActive = dmgReductionActive;
    }
    
    /**
     * Sets the current speed of the Monster
     * @param currentSpd The new current speed
     */
    public void setCurrentSpd(int currentSpd)
    {
        this.currentSpd = currentSpd;
    }
    
    public void increaseAtkBar()
    {
        if (!this.isDead())
        {
            increaseAtkBar((currentSpd + extraSpd - lessAtkSpd) * 0.2);
        }
    }
    
    /**
     * Gets the max speed for the Monster
     * @return The max speed for the Monster
     */
    public int getMaxSpd()
    {
        return maxSpd;
    }
    
    public double dmgReductionProtocol(double dmg, boolean self)
    {
        if (dmgReductionIsActive())
        {
            if (isPrint())
            {
                System.out.println(ConsoleColors.GREEN + "Macaron Shield" + ConsoleColors.RESET);
            }
            return dmg / (getDef() * 2);
        }
        return dmg;
    }
    
    public void afterTurnProtocol(Object o, boolean attack)
    {
        super.afterTurnProtocol(o, attack);
        removeDebuff(Debuff.STUN);
        removeDebuff(Debuff.FREEZE);
        removeDebuff(Debuff.SLEEP);
    }
}
