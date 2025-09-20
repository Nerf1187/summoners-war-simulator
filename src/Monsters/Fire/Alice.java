package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Effects.*;
import Util.Util.*;
import java.util.*;

import static Util.Util.CONSOLE_INTERFACE.OUTPUT.printfWithColor;

/**
 * Fire Macaron Guard
 */
public class Alice extends Monster
{
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
        super("Alice" + count, Element.FIRE, 9_555, 900, 604, 101, 15, 50, 40, 0);
        maxSpd = getSpd();
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        currentSpd = this.getSpd();
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.DEC_DEF.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(70);
        Ability a1 = new Attack_Ability("Macaron Boomerang (1)", 1.25 * (1.8 + (2.7 * getDef()) / getAtk()), 0, 1, "Attacks the enemy to decrease Defense for 2 turns with a 40% chance. This attack will deal more damage according to your Defense."
                , ability1Debuffs, ability1DebuffChances, 0, false, false, false);
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(DebuffEffect.STUN.getNum(), 1, 0);
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(100);
        Ability a2 = new Attack_Ability("Crash! Macaron (2)", 1.25 * (2 + (3.2 * getDef()) / getAtk()), 0, 1,
                "Escorts the ally with the lowest HP for 3 turns, then charges towards the enemy target. Deals damage that increases according to your Defense on the attacked target and stuns for 1 turn.", ability2Debuffs,
                ability2DebuffChances, 3, false, false, false);
        
        ArrayList<Buff> ability3Buffs = MONSTERS.abilityBuffs(BuffEffect.DEF_UP.getNum(), 2);
        ArrayList<Integer> ability3BuffChances = MONSTERS.abilityChances(100);
        Ability a3 = new Ability("Macaron Shield (3)", 0, 0, 1,
                "Increases the Defense of all allies for 1 turn and decreases the damage that your allies take by 200% of your Defense until the next turn starts. You will gain immunity to inability effects and have your Attack Speed at 100. you " +
                "are not affected by the effects that change Attack Speed during this state.", ability3Buffs, ability3BuffChances, 3, false, false, false, true, false, false, 0);
        
        super.setAbilities(a1, a2, a3);
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
        this.removeAllOf(OtherEffect.MACARON_SHIELD);
        
        //Reset speed
        setCurrentSpd(getMaxSpd());
        
        switch (abilityNum)
        {
            case 2 ->
            {
                //Apply Defend to the lowest HP ally
                Team next = game.getNextMonsTeam();
                Monster m = next.getMonsterWithLowestHpRatio();
                m.addAppliedBuff(BuffEffect.DEFEND, 3, this);
            }
            case 3 ->
            {
                //Activate damage reduction
                setDmgReductionActive(true);
                //Change speed
                setCurrentSpd(100);
                
                //Add Macaron shield to other effects
                Effect s = new Effect(Integer.MAX_VALUE);
                s.setEffect(OtherEffect.MACARON_SHIELD);
                this.addOtherEffect(s);
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
                printfWithColor("Macaron Shield%n", ConsoleColor.GREEN);
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
            removeAllOf(DebuffEffect.STUN);
            removeAllOf(DebuffEffect.FREEZE);
            removeAllOf(DebuffEffect.SLEEP);
        }
    }
    
    public void reset()
    {
        //Remove Macaron shield when resetting
        dmgReductionActive = false;
        this.removeAllOf(OtherEffect.MACARON_SHIELD);
        super.reset();
    }
    
    public void kill()
    {
        super.kill();
        if (this.isDead())
        {
            dmgReductionActive = false;
            this.removeAllOf(OtherEffect.MACARON_SHIELD);
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
