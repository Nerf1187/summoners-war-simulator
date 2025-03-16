package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

/**
 * Fire Macaron Guard
 */
public class Alice extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    private final int maxSpd;
    private int currentSpd;
    private boolean dmgReductionActive = false;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Alice()
    {
        this("Alice1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Alice(String runeFileName)
    {
        super("Alice" + count, FIRE, 9_555, 900, 604, 101, 15, 50, 40, 0);
        maxSpd = getSpd();
        super.setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
        setAbilities();
        currentSpd = this.getSpd();
        count++;
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
                "Increases the Defense of all allies for 1 turn and decreases the damage that your allies take by 200% of your Defense until the next turn starts. You will gain immunity to inability effects and have your Attack Speed at 100. you " +
                "are not affected by the effects that change Attack Speed during this state.", ability3Buffs, ability3BuffChances, 3, false, false, false, true, false, false, 0));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        //Remove damage reduction
        setDmgReductionActive(false);
        setCurrentSpd(maxSpd);
        this.removeOtherStat(Stat.MACARON_SHIELD);
        
        //Reset speed
        setCurrentSpd(getMaxSpd());
        
        switch (abilityNum)
        {
            case 2 ->
            {
                //Apply Defend to the lowest HP ally
                Team next = game.getNextMonsTeam();
                Monster m = next.getMonsterWithLowestHpRatio();
                m.addAppliedBuff(Buff.DEFEND, 3, this);
            }
            case 3 ->
            {
                //Activate damage reduction
                setDmgReductionActive(true);
                //Change speed
                setCurrentSpd(100);
                
                //Add Macaron shield to other effects
                Stat s = new Stat(Integer.MAX_VALUE);
                s.setStatNum(Stat.MACARON_SHIELD);
                this.addOtherStat(s);
            }
        }
        afterTurnProtocol(target, abilityNum != 3);
        return true;
    }
    
    /**
     * Checks if Macaron Shield is active
     *
     * @return True if Macaron Shield is active, false otherwise
     */
    public boolean dmgReductionIsActive()
    {
        return dmgReductionActive;
    }
    
    /**
     * Sets the Macaron Shield status
     *
     * @param dmgReductionActive The new status of Macaron Shield
     */
    public void setDmgReductionActive(boolean dmgReductionActive)
    {
        this.dmgReductionActive = dmgReductionActive;
    }
    
    /**
     * Sets the current speed of the Monster
     *
     * @param currentSpd The new current speed
     */
    public void setCurrentSpd(int currentSpd)
    {
        this.currentSpd = currentSpd;
    }
    
    public void increaseAtkBar()
    {
        //Use current speed instead of parent speed
        if (!this.isDead())
        {
            increaseAtkBar(currentSpd * 0.07);
        }
    }
    
    /**
     * Gets the max speed for the Monster
     *
     * @return The max speed for the Monster
     */
    public int getMaxSpd()
    {
        return maxSpd;
    }
    
    public double dmgReductionProtocol(double dmg, boolean self)
    {
        //Reduce damage if Macaron shield is active
        if (dmgReductionIsActive())
        {
            if (isPrint())
            {
                System.out.printf("%sMacaron Shield%s%n", ConsoleColors.GREEN, ConsoleColors.RESET);
            }
            return dmg / (getDef() * 2);
        }
        return dmg;
    }
    
    public void afterTurnProtocol(Object o, boolean attack)
    {
        super.afterTurnProtocol(o, attack);
        //Remove stun effects if Macaron shield is active
        if (dmgReductionIsActive())
        {
            removeDebuff(Debuff.STUN);
            removeDebuff(Debuff.FREEZE);
            removeDebuff(Debuff.SLEEP);
        }
    }
    
    public void reset()
    {
        //Remove Macaron shield when resetting
        dmgReductionActive = false;
        this.removeOtherStat(Stat.MACARON_SHIELD);
        super.reset();
    }
    
    public void kill()
    {
        super.kill();
        if (this.isDead())
        {
            dmgReductionActive = false;
            this.removeOtherStat(Stat.MACARON_SHIELD);
        }
    }
    
    public Monster copy()
    {
        Alice save = (Alice) super.copy();
        save.dmgReductionActive = this.dmgReductionActive;
        save.currentSpd = this.currentSpd;
        return save;
    }
    
    public void paste(Monster save)
    {
        if (!(save instanceof Alice))
        {
            return;
        }
        
        super.paste(save);
        this.dmgReductionActive = ((Alice) save).dmgReductionActive;
        this.currentSpd = ((Alice) save).currentSpd;
    }
}