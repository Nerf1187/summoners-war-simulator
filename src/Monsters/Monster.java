package Monsters;

import Abilities.*;
import Errors.*;
import Game.*;
import Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

import static Game.Main.scan;

/**
 * The parent class for all Monsters. This class contains all methods used by Monsters.
 *
 * @author Anthony (Tony) Youssef
 */

public class Monster
{
    //Contains the name and element of every monster
    public static HashMap<String, String> monsterNamesDatabase = new HashMap<>();
    
    private static boolean print = true;
    public static final int FIRE = 0, WATER = 1, WIND = 2, LIGHT = 3, DARK = 4, ALL = 5;
    private String name;
    private int currentHp, maxHp, destroyedHp = 0, def, atk, spd, critRate, critDmg, resistance, accuracy;
    private final int element;
    protected static Game game = null;
    
    /**
     * Used when applying runes
     */
    private double tempMaxHp, tempDef, tempAtk;
    /**
     * Used to apply buffs/debuffs
     */
    private int extraAtk = 0, extraDef = 0, extraCritRate = 0, extraSpd = 0, shield = 0;
    
    /**
     * Monster's base stats (unchanging)
     */
    private final int baseMaxHp, baseAtk, baseDef, baseSpd, baseCritRate, baseCritDmg, baseRes, baseAcc;
    private int extraGlancingRate, lessAtk, lessDef, lessAtkSpd;
    private int abilityGlancingRateChange;
    private int nomOfViolentRuneProcs = 0;
    private double dmgDealtThisTurn;
    private double atkBar = 0;
    private boolean dead = false, crit = false, glancing = false, wasCrit = false;
    private ArrayList<Buff> appliedBuffs = new ArrayList<>();
    private ArrayList<Debuff> appliedDebuffs = new ArrayList<>();
    private ArrayList<Stat> otherStats = new ArrayList<>();
    private ArrayList<Ability> abilities;
    private ArrayList<Rune> runes = new ArrayList<>();
    
    /**
     * Creates a new Monster
     *
     * @param name       The name of the Monster
     * @param element    The element of the Monster. FIRE = 0, WATER = 1. WIND = 2, LIGHT = 3, DARK = 4
     * @param hp         The base health of the Monster
     * @param def        The base defense of the Monster
     * @param attack     The base attack power of the Monster
     * @param speed      The base attack speed of the Monster
     * @param critRate   The base critical hit rate of the Monster (as a percent)
     * @param critDmg    The base critical damage amount of the Monster (as a percent)
     * @param resistance The base resistance of the Monster (as a percent)
     * @param accuracy   The base accuracy of the Monster (as a percent)
     */
    public Monster(String name, int element, int hp, int def, int attack, int speed, int critRate, int critDmg, int resistance, int accuracy)
    {
        this.name = name;
        this.element = element;
        this.maxHp = hp;
        currentHp = maxHp;
        this.def = def;
        this.atk = attack;
        this.spd = speed;
        this.critRate = critRate;
        this.critDmg = critDmg;
        this.resistance = resistance;
        this.accuracy = accuracy;
        baseAtk = atk;
        baseDef = def;
        baseSpd = spd;
        baseCritRate = critRate;
        baseCritDmg = critDmg;
        baseRes = resistance;
        baseAcc = accuracy;
        baseMaxHp = maxHp;
        tempMaxHp = maxHp;
        tempAtk = atk;
        tempDef = def;
    }
    
    /**
     * Creates a new blank Monster with the specified element
     *
     * @param element The element of the Monster
     */
    public Monster(int element)
    {
        this("", element, 0, 0, 0, 0, 15, 50, 15, 0);
    }
    
    /**
     * Creates a blank Monster
     */
    public Monster()
    {
        this(0);
    }
    
    /**
     * Checks whether the program is currently set to print to console
     *
     * @return true if printing to the console is activated
     */
    public static boolean isPrint()
    {
        return print;
    }
    
    /**
     * Sets whether printing the console is activated (on by default
     *
     * @param print true for on, false for off
     */
    public static void setPrint(boolean print)
    {
        Monster.print = print;
    }
    
    /**
     * Sets the abilities for the Monsters
     *
     * @param abilities The abilities to set
     */
    public void setAbilities(ArrayList<Ability> abilities)
    {
        this.abilities = abilities;
        if (numOfSets(Rune.DESPAIR) == 1)
        {
            for (Ability ability : abilities)
            {
                ability.addDebuff(new Debuff(Debuff.STUN, 1, 0), 25);
            }
        }
    }
    
    /**
     * @return The Monster's abilities
     */
    public ArrayList<Ability> getAbilities()
    {
        return abilities;
    }
    
    /**
     * Sets the rune set to the provided List
     *
     * @param runes The Monster's rune set
     */
    public void setRunes(ArrayList<Rune> runes)
    {
        tempMaxHp = baseMaxHp;
        tempDef = baseDef;
        tempAtk = baseAtk;
        spd = baseSpd;
        critRate = baseCritRate;
        critDmg = baseCritDmg;
        resistance = baseRes;
        accuracy = baseAcc;
        
        this.runes = runes;
        for (Rune rune : runes)
        {
            rune.apply();
        }
        tempMaxHp = Math.ceil(tempMaxHp);
        tempDef = Math.ceil(tempDef);
        tempAtk = Math.ceil(tempAtk);
        applyRuneSetEffectsForBeginningOfGame();
        maxHp = (int) Math.ceil(tempMaxHp);
        def = (int) Math.ceil(tempDef);
        atk = (int) Math.ceil(tempAtk);
        currentHp = maxHp;
    }
    
    /**
     * @return this Monster's runes
     */
    public ArrayList<Rune> getRunes()
    {
        return runes;
    }
    
    /**
     * Change the Monster's name
     *
     * @param name The new name
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Change the Monster's resistance
     *
     * @param resistance The new resistance
     */
    public void setResistance(double resistance)
    {
        this.resistance = (int) Math.ceil(resistance);
    }
    
    /**
     * @return The Monster's current resistance
     */
    public int getResistance()
    {
        return resistance;
    }
    
    /**
     * Change the Monster's accuracy
     *
     * @param accuracy The new accuracy
     */
    public void setAccuracy(double accuracy)
    {
        this.accuracy = (int) Math.ceil(accuracy);
    }
    
    /**
     * @return The Monster's current accuracy
     */
    public int getAccuracy()
    {
        return accuracy;
    }
    
    /**
     * @return The Monster's current attack stat (Does not include buffs/debuffs)
     */
    public int getAtk()
    {
        return atk;
    }
    
    /**
     * @return The Monster's base attack
     */
    public int getBaseAtk()
    {
        return baseAtk;
    }
    
    /**
     * @return The Monster's current defense stat (Does not include buffs/debuffs)
     */
    public int getDef()
    {
        return def;
    }
    
    /**
     * @return The Monster's base defense
     */
    public int getBaseDef()
    {
        return baseDef;
    }
    
    /**
     * Change the Monster's defense
     *
     * @param def The Monster's new defense
     */
    public void setDef(double def)
    {
        this.def = (int) Math.ceil(def);
    }
    
    /**
     * Set the Monster's temporary max health
     *
     * @param hp the Monster's new temporary max health
     */
    public void setTempMaxHp(double hp)
    {
        tempMaxHp = hp;
    }
    
    /**
     * Set the Monster's temporary attack
     *
     * @param tempAtk the Monster's new temporary attack
     */
    public void setTempAtk(double tempAtk)
    {
        this.tempAtk = tempAtk;
    }
    
    /**
     * Set the Monster's temporary defense
     *
     * @param tempDef the Monster's new defense
     */
    public void setTempDef(double tempDef)
    {
        this.tempDef = tempDef;
    }
    
    /**
     * @return the Monster's temporary attack
     */
    public double getTempAtk()
    {
        return tempAtk;
    }
    
    /**
     * @return the Monster's temporary defense
     */
    public double getTempDef()
    {
        return tempDef;
    }
    
    /**
     * @return the Monster's temporary max health
     */
    public double getTempMaxHp()
    {
        return tempMaxHp;
    }
    
    /**
     * @return the Monster's element
     */
    public int getElement()
    {
        return element;
    }
    
    /**
     * @return the Monster's current health
     */
    public int getCurrentHp()
    {
        return currentHp;
    }
    
    /**
     * @return the Monster's maximum health
     */
    public int getMaxHp()
    {
        return maxHp;
    }
    
    /**
     * @return the Monster's base maximum health
     */
    public int getBaseMaxHp()
    {
        return baseMaxHp;
    }
    
    /**
     * @return the Monster's current speed stat
     */
    public int getSpd()
    {
        return spd;
    }
    
    /**
     * @return the Monster's base speed
     */
    public int getBaseSpd()
    {
        return baseSpd;
    }
    
    /**
     * @return all current buffs on the Monster
     */
    public ArrayList<Buff> getAppliedBuffs()
    {
        return appliedBuffs;
    }
    
    /**
     * @return all current debuffs on the Monster
     */
    public ArrayList<Debuff> getAppliedDebuffs()
    {
        return appliedDebuffs;
    }
    
    /**
     * @return the Monster's current critical hit rate
     */
    public int getCritRate()
    {
        return critRate;
    }
    
    /**
     * Changes the Monster's critical hit rate
     *
     * @param critRate The Monster's new critical hit rate
     */
    public void setCritRate(double critRate)
    {
        this.critRate = (int) Math.ceil(critRate);
    }
    
    /**
     * Changes the Monster's critical damage
     *
     * @param critDmg The Monster's new critical damage
     */
    public void setCritDmg(double critDmg)
    {
        this.critDmg = (int) Math.ceil(critDmg);
    }
    
    /**
     * @return the Monster's current critical damage
     */
    public int getCritDmg()
    {
        return critDmg;
    }
    
    /**
     * Change the Monster's current health
     *
     * @param currentHp The Monster's new health
     */
    public void setCurrentHp(int currentHp)
    {
        this.currentHp = Math.min(currentHp, maxHp - destroyedHp);
    }
    
    /**
     * Change the amount of destroyed health
     *
     * @param amount The new amount of destroyed health
     */
    public void destroyHp(int amount)
    {
        destroyedHp = (destroyedHp + amount > maxHp * 0.6) ? (int) (maxHp * 0.6) : destroyedHp + amount;
    }
    
    /**
     * Adds a new buff to the Monster
     *
     * @param num    The buff number to add
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    public void addAppliedBuff(int num, int turns, Monster caster)
    {
        if (caster.equals(this) && turns > 0)
        {
            turns++;
        }
        if (num == Buff.SHIELD)
        {
            throw new RuntimeException("Please use the other addAppliedBuff method for shields");
        }
        
        if (containsBuff(num))
        {
            //Continuous healing can stack with itself, no other buff can.
            if (num != Buff.RECOVERY && num != Buff.THREAT && num != Buff.DEFEND)
            {
                if (appliedBuffs.get(getBuffIndex(new Buff(num, 1))).getNumTurns() < turns)
                {
                    removeBuff(new Buff(num, 1));
                    appliedBuffs.add(new Buff(num, turns));
                }
            }
            else if (num == Buff.RECOVERY)
            {
                appliedBuffs.add(new Buff(Buff.RECOVERY, turns));
            }
        }
        else if (num == Buff.THREAT)
        {
            appliedBuffs.add(new Threat(turns));
        }
        else if (num == Buff.DEFEND)
        {
            appliedBuffs.add(new Defend(turns, caster));
        }
        else
        {
            appliedBuffs.add(new Buff(num, turns));
        }
    }
    
    /**
     * Adds a new buff to the Monster
     *
     * @param num    The buff number to add
     * @param chance The chance it will apply
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    private void addAppliedBuff(int num, int chance, int turns, Monster caster)
    {
        int random = new Random().nextInt(101);
        if (random <= chance)
        {
            addAppliedBuff(num, turns, caster);
        }
    }
    
    /**
     * Adds a new buff to the Monster
     *
     * @param buff   The buff to add
     * @param caster The Monster who gave it
     */
    public void addAppliedBuff(Buff buff, Monster caster)
    {
        if (buff instanceof Shield possibleShield)
        {
            if (possibleShield.getAmount() == 0)
            {
                return;
            }
            if (hasShield())
            {
                if (getShield().getAmount() > possibleShield.getAmount())
                {
                    return;
                }
                else if (getShield().getAmount() == possibleShield.getAmount())
                {
                    if (getShield().getNumTurns() > possibleShield.getNumTurns())
                    {
                        return;
                    }
                }
                else
                {
                    appliedBuffs.remove(getShield());
                    shield = 0;
                }
            }
            appliedBuffs.add(possibleShield);
        }
        else
        {
            addAppliedBuff(buff.getBuffNum(), buff.getNumTurns(), caster);
        }
    }
    
    /**
     * Adds a new buff to the Monster
     *
     * @param buff   The buff to add
     * @param chance The chance it will apply
     * @param caster The Monster who gave it
     */
    public void addAppliedBuff(Buff buff, int chance, Monster caster)
    {
        int random = new Random().nextInt(101);
        if (random <= chance)
        {
            addAppliedBuff(buff, caster);
        }
    }
    
    /**
     * Adds a new debuff to the Monster (Assumes debuff does not go through immunity)
     *
     * @param num    The debuff number to add
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    private void addAppliedDebuff(int num, int turns, Monster caster)
    {
        int resRate = new Random().nextInt(101);
        if (resRate <= Math.max(15, Math.min(resistance, 100) - Math.min(accuracy, 100)))
        {
            if (print)
            {
                System.out.println("Resisted!");
            }
            return;
        }
        if (containsDebuff(new Debuff(num, 1, 0)))
        {
            if (num != Debuff.BOMB && num != Debuff.CONTINUOUS_DMG && num != Debuff.PROVOKE)
            {
                if (appliedDebuffs.get(getDebuffIndex(new Debuff(num, 1, 0))).getNumTurns() < turns)
                {
                    removeDebuff(num);
                    appliedDebuffs.add(new Debuff(num, turns, 0));
                }
            }
            else
            {
                if (num == Debuff.BOMB)
                {
                    if (countDebuff(new Debuff(Debuff.BOMB, 1, 0)) < 2)
                    {
                        appliedDebuffs.add(new Debuff(Debuff.BOMB, turns, 0));
                    }
                }
                else
                {
                    
                    if (countDebuff(new Debuff(Debuff.CONTINUOUS_DMG, 1, 0)) < 6)
                    {
                        appliedDebuffs.add(new Debuff(
                                Debuff.CONTINUOUS_DMG, turns, 0));
                    }
                }
            }
        }
        else if (num == Debuff.PROVOKE)
        {
            appliedDebuffs.add(new Provoke(turns, caster));
        }
        /*else if (num == Debuff.DEC_ATK_BAR)
        {
            appliedDebuffs.add(new DecAtkBar())
        }*/
        else
        {
            appliedDebuffs.add(new Debuff(num, turns, 0));
        }
    }
    
    /**
     * Adds a new debuff to the Monster that ignores resistance
     *
     * @param debuff The debuff to add
     * @param caster The Monster who gave it
     */
    public void addGuaranteedAppliedDebuff(Debuff debuff, Monster caster)
    {
        debuff.setCaster(caster);
        if (containsBuff(new Buff(Buff.IMMUNITY, 1)) && !debuff.goesThroughImmunity())
        {
            return;
        }
        int num = debuff.getDebuffNum();
        int turns = debuff.getNumTurns();
        if (containsDebuff(new Debuff(num, 1, 0)))
        {
            if (num != Debuff.BOMB && num != Debuff.CONTINUOUS_DMG && num != Debuff.PROVOKE)
            {
                if (appliedDebuffs.get(getDebuffIndex(new Debuff(num, 1, 0))).getNumTurns() < turns)
                {
                    removeDebuff(num);
                    appliedDebuffs.add(debuff);
                }
            }
            else
            {
                if (num == Debuff.BOMB)
                {
                    if (countDebuff(new Debuff(Debuff.BOMB, 1, 0)) < 2)
                    {
                        appliedDebuffs.add(debuff);
                    }
                }
                else
                {
                    
                    if (countDebuff(new Debuff(Debuff.CONTINUOUS_DMG, 1, 0)) < 10)
                    {
                        appliedDebuffs.add(debuff);
                    }
                }
            }
        }
        else if (num == Debuff.PROVOKE)
        {
            appliedDebuffs.add(new Provoke(turns, caster));
        }
        else
        {
            appliedDebuffs.add(debuff);
        }
    }
    
    /**
     * Adds a new debuff to the Monster (Assumes debuff does not go through immunity
     *
     * @param num    The debuff number to add
     * @param chance The chance the debuff will be applied
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    public void addAppliedDebuff(int num, int chance, int turns, Monster caster)
    {
        if (containsBuff(new Buff(Buff.IMMUNITY, 1)))
        {
            if (print)
            {
                System.out.println(ConsoleColors.GREEN + "Immunity!" + ConsoleColors.RESET);
            }
            return;
        }
        int random = new Random().nextInt(101);
        if (random <= chance)
        {
            addAppliedDebuff(num, turns, caster);
        }
    }
    
    /**
     * Adds a new debuff to the Monster
     *
     * @param debuff The debuff to add
     * @param caster The Monster who gave it
     */
    public void addAppliedDebuff(Debuff debuff, Monster caster)
    {
        if (debuff instanceof DecAtkBar)
        {
            appliedDebuffs.add(debuff);
            return;
        }
        addAppliedDebuff(debuff.getDebuffNum(), debuff.getNumTurns(), caster);
    }
    
    /**
     * Adds a new debuff to the Monster
     *
     * @param debuff The debuff to add
     * @param chance The chance it will apply
     * @param caster The Monster who gave it
     */
    public void addAppliedDebuff(Debuff debuff, int chance, Monster caster)
    {
        if (containsBuff(new Buff(Buff.IMMUNITY, 1)) && !debuff.goesThroughImmunity())
        {
            if (print)
            {
                System.out.println(ConsoleColors.GREEN + "Immunity!" + ConsoleColors.RESET);
            }
            return;
        }
        int random = new Random().nextInt(101);
        if (random <= chance)
        {
            addAppliedDebuff(debuff, caster);
        }
    }
    
    /**
     * Removes all instances of a debuff
     *
     * @param debuff The debuff to remove
     */
    public void removeDebuff(Debuff debuff)
    {
        for (int i = appliedDebuffs.size() - 1; i >= 0; i--)
        {
            if (appliedDebuffs.get(i).equals(debuff))
            {
                appliedDebuffs.remove(i);
            }
        }
    }
    
    /**
     * Removes all instances of a debuff
     *
     * @param debuff The number of the debuff to remove
     */
    public void removeDebuff(int debuff)
    {
        removeDebuff(new Debuff(debuff));
    }
    
    /**
     * Removes all instances of a buff
     *
     * @param buff The buff to remove
     */
    public void removeBuff(Buff buff)
    {
        for (int i = appliedBuffs.size() - 1; i >= 0; i--)
        {
            if (appliedBuffs.get(i).equals(buff))
            {
                appliedBuffs.remove(i);
            }
        }
    }
    
    /**
     * Removes all instances of a buff
     *
     * @param num The buff number to remove
     */
    public void removeBuff(int num)
    {
        removeBuff(new Buff(num));
    }
    
    /**
     * Finds the index of the given buff
     *
     * @param buff The buff to find
     * @return the index of the provided buff
     */
    public int getBuffIndex(Buff buff)
    {
        for (int i = 0; i < appliedBuffs.size(); i++)
        {
            Buff b = appliedBuffs.get(i);
            if (buff.equals(b))
            {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Finds the index of the given debuff
     *
     * @param debuff The debuff to find
     * @return the index of the provided debuff
     */
    public int getDebuffIndex(Debuff debuff)
    {
        for (int i = 0; i < appliedDebuffs.size(); i++)
        {
            if (appliedDebuffs.get(i).equals(debuff))
            {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Counts the number of instances of the given buff are on the Monster
     *
     * @param buff The buff to count
     * @return The number of instances of the given buff
     */
    public int countBuff(Buff buff)
    {
        int count = 0;
        for (Buff b : appliedBuffs)
        {
            if (b.equals(buff))
            {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Counts the number of instances of the given buff are on the Monster
     *
     * @param buff The number of the buff to count
     * @return The number of instances of the given buff
     */
    public int countBuff(int buff)
    {
        return countBuff(new Buff(buff, 1));
    }
    
    /**
     * Counts the number of instances of the given debuff are on the Monster
     *
     * @param debuff The debuff to count
     * @return The number of instances of the given debuff
     */
    public int countDebuff(Debuff debuff)
    {
        int count = 0;
        for (Debuff d : appliedDebuffs)
        {
            if (d.equals(debuff))
            {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Counts the number of instances of the given debuff are on the Monster
     *
     * @param debuff The number of the debuff to count
     * @return The number of instances of the given debuff
     */
    public int countDebuff(int debuff)
    {
        return countDebuff(new Debuff(debuff, 1, 0));
    }
    
    /**
     * Checks if the Monster is dead
     *
     * @return true if the Monster has 0 health, false otherwise
     */
    public boolean isDead()
    {
        return dead;
    }
    
    /**
     * Marks the Monster as alive (false) or dead (true)
     *
     * @param isDead The new dead state of the Monster
     */
    public void setDead(boolean isDead)
    {
        dead = isDead;
    }
    
    /**
     * Calculates the base damage for the Monster to deal on this turn
     *
     * @param multiplier The ability multiplier
     * @param target     The Monster being attacked
     * @return the base damage to deal for this turn
     */
    public int calculateBaseDamage(double multiplier, Monster target)
    {
        double dmg = ((atk + extraAtk - lessAtk) * multiplier);
        int critRate = (target.containsBuff(new Buff(Buff.CRIT_RESIST_UP, 1))) ? (int) (this.critRate * 0.7) : this.critRate;
        String elementRelation = Team.elementalRelationship(element, target.element);
        int critChanceChange = 0;
        if (elementRelation.equals(ConsoleColors.GREEN_BACKGROUND))
        {
            critChanceChange = 15;
        }
        else if (elementRelation.equals(ConsoleColors.RED_BACKGROUND))
        {
            critChanceChange = -20;
        }
        //Crit
        if (new Random().nextInt(101) <= (critRate + extraCritRate - extraGlancingRate + critChanceChange))
        {
            dmg *= (1 + (1.0 * (this.critDmg) / 100));
            if (elementRelation.equals(ConsoleColors.GREEN_BACKGROUND))
            {
                dmg *= 1.17;
            }
            else if (elementRelation.equals(ConsoleColors.RED_BACKGROUND))
            {
                dmg *= 0.83;
            }
            crit = true;
        }
        //Glancing
        else if (new Random().nextInt(101) <= (30 + extraGlancingRate - critChanceChange + abilityGlancingRateChange))
        {
            dmg *= 0.7;
            if (elementRelation.equals(ConsoleColors.GREEN_BACKGROUND))
            {
                dmg *= 1.15;
            }
            else if (elementRelation.equals(ConsoleColors.RED_BACKGROUND))
            {
                dmg *= 0.85;
            }
            glancing = true;
        }
        return Math.max(50, new Random().nextInt((int) dmg - 50, (int) dmg + 50));
    }
    
    /**
     * Calculates the base damage reduction for the Monster
     *
     * @return The base damage reduction
     */
    public double calculateBaseDmgReduction()
    {
        return (1000 / (1140 + 3.5 * (def + extraDef - lessDef)));
    }
    
    /**
     * Deals an amount of damage to the Monster
     *
     * @param dmg                 The amount of damage to deal
     * @param ignoresDmgReduction True if the damage should ignore damage reduction effects (such as shields)
     */
    public void dealDmg(double dmg, boolean ignoresDmgReduction)
    {
        if (!ignoresDmgReduction)
        {
            shield -= (int) Math.ceil(dmg);
        }
        else
        {
            currentHp -= (int) Math.ceil(dmg);
        }
        if (shield < 0)
        {
            removeBuff(Buff.SHIELD);
            currentHp += shield;
            shield = 0;
        }
    }
    
    /**
     * Increases the attack bar by the default amount (1/5 of the Monster's attack speed) if the Monster is alive, does nothing otherwise
     */
    public void increaseAtkBar()
    {
        if (dead)
        {
            atkBar = 0;
            return;
        }
        atkBar += (spd + extraSpd - lessAtkSpd) * 0.2;
    }
    
    /**
     * Checks if the Monster has a full attack bar
     *
     * @return true if the Monster has a full attack bar, false otherwise
     */
    public boolean hasFullAtkBar()
    {
        return atkBar >= 1000;
    }
    
    /**
     * @return The current attack bar value
     */
    public double getAtkBar()
    {
        return atkBar;
    }
    
    /**
     * Increases the attack bar by a given amount
     *
     * @param num the amount to increase the attack bar
     */
    public void increaseAtkBar(int num)
    {
        atkBar += num;
    }
    
    /**
     * Set the attack bar to a given amount
     *
     * @param num the amount to set the attack bar to
     */
    public void setAtkBar(int num)
    {
        atkBar = Math.max(num, 0);
    }
    
    /**
     * Gives the name of the Monster with its associated number and element if desired
     *
     * @param withElement true if the elemental color is included
     * @param withNumber  true if the Monster's number is included
     * @return the Monster's name with its element and color if wanted
     */
    public String getName(boolean withElement, boolean withNumber)
    {
        if (!withElement)
        {
            if (withNumber)
            {
                return name;
            }
            String returnName = "";
            for (char c : name.toCharArray())
            {
                if (!(c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0' || c == '(' || c == ')'))
                {
                    returnName += c;
                }
            }
            return returnName;
        }
        String elementColor = "";
        switch (element)
        {
            case 0 -> elementColor = ConsoleColors.RED_BOLD_BRIGHT;
            case 1 -> elementColor = ConsoleColors.BLUE_BOLD_BRIGHT;
            case 2 -> elementColor = ConsoleColors.YELLOW_BOLD_BRIGHT;
            case 3 -> elementColor = ConsoleColors.WHITE_BOLD_BRIGHT;
            case 4 -> elementColor = ConsoleColors.PURPLE_BOLD_BRIGHT;
        }
        if (withNumber)
        {
            return elementColor + name + ConsoleColors.RESET;
        }
        String returnName = "";
        for (char c : name.toCharArray())
        {
            if (!(c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0' || c == '(' || c == ')'))
            {
                returnName += c;
            }
        }
        return elementColor + returnName + ConsoleColors.RESET;
    }
    
    /**
     * Checks if the given ability number is valid to use
     *
     * @param abilityNum The ability number to check
     * @return true if the ability associated with the given number can be used on this turn, false otherwise
     */
    public boolean abilityIsValid(int abilityNum)
    {
        if (abilities.get(abilityNum - 1).isPassive())
        {
            return false;
        }
        return abilities.get(abilityNum - 1).getTurnsRemaining() <= 0;
    }
    
    /**
     * Checks if the given ability is valid to use
     *
     * @param ability The ability to check
     * @return true if the given ability can be used on this turn, false otherwise
     */
    public boolean abilityIsValid(Ability ability)
    {
        if (ability.isPassive())
        {
            return false;
        }
        return ability.getTurnsRemaining() <= 0;
    }
    
    /**
     * Checks if the given target can be attacked/healed
     *
     * @param next          The Monster whose turn it is
     * @param target        The targeted Monster to check
     * @param enemyIsTarget true if target and next are on opposite teams, false otherwise
     * @return true if the given target can be attacked/healed
     */
    public boolean targetIsValid(Monster next, Monster target, boolean enemyIsTarget)
    {
        Provoke p = next.getProvoke();
        if (p != null && enemyIsTarget)
        {
            return p.getCaster().equals(target);
        }
        if (target.isDead())
        {
            return false;
        }
        for (Monster other : Auto_Play.getOther().getMonsters())
        {
            if (other.hasThreat() && enemyIsTarget)
            {
                if (target.equals(other))
                {
                    return true;
                }
            }
        }
        return true;
    }
    
    /**
     * Starts the Monster's turn
     *
     * @param target     The targeted Monster
     * @param abilityNum The ability number to use
     * @return true if the turn completed successfully, false otherwise
     */
    public boolean nextTurn(Monster target, int abilityNum)
    {
        extraAtk = 0;
        extraDef = 0;
        extraCritRate = 0;
        extraSpd = 0;
        shield = 0;
        if (target.equals(this) && abilities.get(abilityNum - 1).targetsEnemy())
        {
            return false;
        }
        if (target.isDead())
        {
            return false;
        }
        if (abilities.get(abilityNum - 1).isPassive())
        {
            return false;
        }
        if (abilities.get(abilityNum - 1).targetsEnemy())
        {
            switch (abilityNum)
            {
                case 1 ->
                {
                    atkBar = 0;
                    attack(target, abilities.get(0), false);
                }
                case 2 ->
                {
                    if (abilities.get(1).getTurnsRemaining() == 0)
                    {
                        atkBar = 0;
                        attack(target, abilities.get(1), false);
                        abilities.get(1).setToMaxCooldown();
                    }
                    else
                    {
                        if (print)
                        {
                            System.out.printf("Ability on cooldown (%d turns remaining)\n", abilities.get(1).getTurnsRemaining());
                        }
                        return false;
                    }
                }
                case 3 ->
                {
                    if (abilities.get(2).getTurnsRemaining() == 0)
                    {
                        atkBar = 0;
                        attack(target, abilities.get(2), false);
                        abilities.get(2).setToMaxCooldown();
                    }
                    else
                    {
                        if (print)
                        {
                            System.out.printf("Ability on cooldown (%d turns remaining)%n", abilities.get(2).getTurnsRemaining());
                        }
                        return false;
                    }
                }
                case 4 ->
                {
                    if (abilities.get(3).getTurnsRemaining() == 0)
                    {
                        atkBar = 0;
                        attack(target, abilities.get(2), false);
                        abilities.get(3).setToMaxCooldown();
                    }
                    else
                    {
                        if (print)
                        {
                            System.out.printf("Ability on cooldown (%d turns remaining)%n", abilities.get(3).getTurnsRemaining());
                        }
                        return false;
                    }
                }
            }
        }
        else
        {
            switch (abilityNum)
            {
                case 1 ->
                {
                    atkBar = 0;
                    heal(target, abilities.get(0));
                }
                case 2 ->
                {
                    if (abilities.get(1).getTurnsRemaining() == 0)
                    {
                        atkBar = 0;
                        heal(target, abilities.get(1));
                        abilities.get(1).setToMaxCooldown();
                    }
                    else
                    {
                        if (print)
                        {
                            System.out.printf("Ability on cooldown (%d turns remaining)%n", abilities.get(1).getTurnsRemaining());
                        }
                        return false;
                    }
                }
                case 3 ->
                {
                    if (abilities.get(2).getTurnsRemaining() == 0)
                    {
                        atkBar = 0;
                        heal(target, abilities.get(2));
                        abilities.get(2).setToMaxCooldown();
                    }
                    else
                    {
                        if (print)
                        {
                            System.out.printf("Ability on cooldown (%d turns remaining)%n", abilities.get(2).getTurnsRemaining());
                        }
                        return false;
                    }
                }
                case 4 ->
                {
                    if (abilities.get(3).getTurnsRemaining() == 0)
                    {
                        atkBar = 0;
                        heal(target, abilities.get(3));
                        abilities.get(3).setToMaxCooldown();
                    }
                    else
                    {
                        if (print)
                        {
                            System.out.printf("Ability on cooldown (%d turns remaining)%n", abilities.get(3).getTurnsRemaining());
                        }
                        return false;
                    }
                }
            }
        }
        
        //Cleanse
        if (containsBuff(Buff.CLEANSE))
        {
            cleanse();
        }
        
        //Apply shield
        if (hasShield() && shield == 0)
        {
            shield = getShield().getAmount();
        }
        
        //Reduce ability and stat cooldowns and call attacked() function
        return true;
    }
    
    /**
     * The Monster's basic attack command. This method will execute all functions that are needed during the Monster's turn
     *
     * @param target    The target Monster to attack
     * @param ability   The ability to attack with
     * @param isCounter True if this attack is a counter, false otherwise
     * @param count     The number of times this method has been called this turn
     */
    private void attack(Monster target, Ability ability, boolean isCounter, int count)
    {
        if (print)
        {
            System.out.println("\n");
        }
        //Make sure booleans do not conflict with each other
        if (Game.canCounter() && isCounter)
        {
            throw new ConflictingArguments(getName(true, true) + " canCounter and isCounter can " + "not both be true");
        }
        
        //Calculate and deal damage
        applyBeginningOfTurnBuffs();
        applyBeginningOfTurnDebuffs();
        if (currentHp <= 0)
        {
            currentHp = 0;
            dead = true;
            crit = false;
            glancing = false;
            return;
        }
        if (isStunned())
        {
            return;
        }
        double finalDmg = calculateBaseDamage(ability.getDmgMultiplier(), target);
        
        //Multiplies dmg by dmg reduction
        finalDmg *= (ability.ignoresDefense()) ? new Monster(target.element).calculateBaseDmgReduction() : target.calculateBaseDmgReduction();
        
        String ignoreDef = (ability.ignoresDefense()) ? ConsoleColors.RED + "Ignore Defense! " + ConsoleColors.RESET : " ";
        
        //Branding
        if (target.containsDebuff(new Debuff(Debuff.BRAND, 1, 0)))
        {
            finalDmg *= 1.25;
        }
        
        //Invincibility
        if (!ability.ignoresDmgReduction() && target.containsBuff(new Buff(Buff.INVINCIBILITY, 1)))
        {
            finalDmg = 0;
            if (print)
            {
                System.out.print(target.getName(true, true) + " has invincibility!\n");
            }
        }
        
        //Reduces damage if the method call is a counter
        if (isCounter)
        {
            finalDmg *= 0.7;
        }
        
        //Accounts for reflected damage
        if (target.containsBuff(Buff.REFLECT))
        {
            dealDmg(finalDmg * 0.3, false);
            if (print)
            {
                System.out.printf("%s%d damage reflected.%s", ConsoleColors.PURPLE, (int) (finalDmg * 0.3), ConsoleColors.RESET);
            }
            finalDmg *= 0.7;
        }
        
        //Checks for Defend buff
        if (!ability.ignoresDmgReduction() && target.hasDefend())
        {
            target = target.getDefend().getCaster();
            target.addAppliedBuff(Buff.COUNTER, 0, target);
            finalDmg /= 2;
        }
        
        //Reduces damage if target has Threat
        if (target.hasThreat())
        {
            finalDmg *= 0.85;
        }
        
        //Deal damage to target
        
        
        //Remove sleep if needed
        ArrayList<Debuff> TargetDebuffsList = target.appliedDebuffs;
        for (int i = TargetDebuffsList.size() - 1; i >= 0; i--)
        {
            Debuff debuff = TargetDebuffsList.get(i);
            if (debuff.getDebuffNum() == Debuff.SLEEP)
            {
                appliedDebuffs.remove(i);
            }
        }
        
        if (!ability.ignoresDmgReduction())
        {
            finalDmg = target.dmgReductionProtocol(finalDmg);
        }
        finalDmg = dmgIncProtocol(finalDmg);
        
        target.dealDmg(finalDmg, ability.ignoresDmgReduction());
        dmgDealtThisTurn += finalDmg;
        
        //Print damage dealt
        if (crit)
        {
            if (print)
            {
                System.out.print(ConsoleColors.GREEN + "Critical hit! " + ConsoleColors.RESET);
            }
        }
        if (glancing)
        {
            if (print)
            {
                System.out.print(ConsoleColors.YELLOW + "Glancing hit! " + ConsoleColors.RESET);
            }
        }
        
        if (print)
        {
            System.out.printf("%s%s%s dealt %s damage to %s.\n%s", ignoreDef, getName(true, true), ConsoleColors.PURPLE,
                    numWithCommas((int) finalDmg), target.getName(true, true), ConsoleColors.RESET);
        }
        
        target.wasCrit = crit;
        
        crit = false;
        glancing = false;
        
        //Vampire
        if (containsBuff(new Buff(Buff.VAMPIRE, 1)) && !containsDebuff(new Debuff(Debuff.UNRECOVERABLE, 1, 0)))
        {
            setCurrentHp((int) (currentHp + (0.2 * finalDmg)));
        }
        
        
        //Apply buffs to self if Monster does not have beneficial effect block debuff and attack was not glancing
        if (!containsDebuff(new Debuff(Debuff.BLOCK_BENEFICIAL_EFFECTS, 1, 0)) && !glancing)
        {
            ArrayList<Buff> buffs = ability.getBuffs();
            ArrayList<Integer> buffsChance = ability.getBuffsChance();
            for (int i = 0; i < buffs.size(); i++)
            {
                addAppliedBuff(buffs.get(i), buffsChance.get(i), this);
            }
        }
        
        //Apply debuffs to target if Monster does not have immunity
        int increasedChance = 0;
        if (crit)
        {
            increasedChance = 20;
        }
        else if (glancing)
        {
            increasedChance = -1_000;
        }
        ArrayList<Debuff> debuffs = ability.getDebuffs();
        ArrayList<Integer> debuffsChance = ability.getDebuffsChance();
        for (int i = 0; i < debuffs.size(); i++)
        {
            target.addAppliedDebuff(debuffs.get(i), debuffsChance.get(i) + increasedChance, this);
        }
        if (print)
        {
            System.out.println("\n");
        }
        
        //Decrease Atk Bar
        if (target.hasDecAtkBar())
        {
            DecAtkBar dec = target.getDecAtkBar();
            target.setAtkBar((int) (target.getAtkBar() - dec.getAmount() * 10));
            target.removeDebuff(dec);
        }
        
        //Buff steal
        if (containsBuff(new Buff(Buff.BUFF_STEAL, 1)))
        {
            int resRate = new Random().nextInt(101);
            if (resRate <= Math.max(15, Math.min(resistance, 100) - Math.min(accuracy, 100)))
            {
                if (print)
                {
                    System.out.println("Resisted!");
                }
                else
                {
                    Buff stolen = target.removeRandomBuff();
                    if (stolen.getBuffNum() == Buff.DEFEND)
                    {
                        stolen = new Buff(Buff.NULL, 1);
                    }
                    
                    if (!stolen.equals(new Buff(Buff.NULL, 1)))
                    {
                        addAppliedBuff(stolen.getBuffNum(), stolen.getNumTurns() + 1, this);
                    }
                    else
                    {
                        target.atkBar = Math.max(0, target.atkBar - 500);
                        atkBar += 500;
                    }
                }
            }
        }
        
        //Remove beneficial effect
        if (target.containsDebuff(new Debuff(Debuff.REMOVE_BENEFICIAL_EFFECT, 1, 0)))
        {
            target.removeRandomBuff();
            target.removeDebuff(Debuff.REMOVE_BENEFICIAL_EFFECT);
        }
        
        //Strip
        if (target.containsDebuff(Debuff.STRIP))
        {
            target.extraAtk = 0;
            target.extraDef = 0;
            target.extraCritRate = 0;
            target.extraSpd = 0;
            target.shield = 0;
            target.appliedBuffs = new ArrayList<>();
        }
        
        //Remove harmful effect
        if (containsBuff(new Buff(Buff.REMOVE_DEBUFF, 1)))
        {
            removeRandomDebuff();
            removeBuff(Buff.REMOVE_DEBUFF);
        }
        
        target.afterHitProtocol(this);
        
        //Check if target is now dead
        if (target.getCurrentHp() <= 0)
        {
            target.kill();
            return;
        }
        
        //Vampire Rune
        if (!containsDebuff(new Debuff(Debuff.UNRECOVERABLE, 1, 0)))
        {
            setCurrentHp((int) (currentHp + (finalDmg * 0.35 * numOfSets(Rune.VAMPIRE))));
        }
        
        //Destroy Rune
        if (numOfSets(Rune.DESTROY) > 0)
        {
            int percentToDestroy = 0;
            for (int i = 0; i < numOfSets(Rune.DESTROY); i++)
            {
                percentToDestroy += 4;
            }
            double amountToDestroy = 0.3 * finalDmg;
            if (amountToDestroy > maxHp * (1.0 * percentToDestroy / 100))
            {
                amountToDestroy = maxHp * (1.0 * percentToDestroy / 100);
            }
            destroyHp((int) amountToDestroy);
        }
        
        //Nemesis Rune
        if (target.numOfSets(Rune.NEMESIS) > 0)
        {
            if (finalDmg >= 0.07 * target.maxHp)
            {
                target.atkBar += 10 * (4 * target.numOfSets(Rune.NEMESIS));
            }
        }
        
        if (count < ability.getNumOfActivations())
        {
            attack(target, ability, isCounter, count + 1);
        }
        
        if (isCounter)
        {
            afterTurnProtocol(target, true, true);
        }
    }
    
    /**
     * The Monster's basic attack command. This method will execute all functions that are necessary during the Monster's turn
     *
     * @param target    The target Monster to attack
     * @param ability   The ability to attack with
     * @param isCounter True if this attack is a counter, false otherwise
     */
    public void attack(Monster target, Ability ability, boolean isCounter)
    {
        attack(target, ability, isCounter, 1);
        
        //heals self if the ability heals based off damage done
        setCurrentHp(currentHp + (int) (dmgDealtThisTurn * ability.getHealingPercent()));
    }
    
    /**
     * The Monster's basic attack command. This method assumes the attack is not a counter and will execute all functions that are necessary during the
     * Monster's turn
     *
     * @param target  The target Monster to attack
     * @param ability The ability to attack with
     */
    public void attack(Monster target, Ability ability)
    {
        attack(target, ability, false);
    }
    
    /**
     * The Monster's basic heal command. This method will execute all functions that are necessary during the Monster's turn
     *
     * @param target  The target Monster to heal
     * @param ability The ability to heal with
     * @param count   The number of times this method has been called this turn
     */
    private void heal(Monster target, Ability ability, int count)
    {
        for (Buff buff : ability.getBuffs())
        {
            if (buff.getBuffNum() == Buff.REMOVE_DEBUFF)
            {
                target.removeRandomDebuff();
                target.removeBuff(Buff.REMOVE_DEBUFF);
            }
            if (buff.getBuffNum() == Buff.CLEANSE)
            {
                target.cleanse();
                target.removeBuff(Buff.CLEANSE);
            }
        }
        
        if (target.isDead())
        {
            return;
        }
        
        //Heals if target does not have unrecoverable and is not dead
        double healAmount = 0;
        if (!target.containsDebuff(new Debuff(Debuff.UNRECOVERABLE, 1, 0)) && !target.dead)
        {
            healAmount = ability.getHealingPercent() * target.maxHp;
            target.currentHp += (int) healAmount;
            if (target.currentHp > target.maxHp)
            {
                target.currentHp = target.maxHp;
            }
        }
        
        
        if (print && healAmount > 0)
        {
            System.out.printf("%sHealed %s%s for %d health.%s\n", ConsoleColors.GREEN, target.getName(true, true),
                    ConsoleColors.GREEN, (int) healAmount, ConsoleColors.RESET);
        }
        
        
        //Apply buffs to target if target does not have beneficial effect blocker
        if (!containsDebuff(new Debuff(Debuff.BLOCK_BENEFICIAL_EFFECTS, 1, 0)) && !target.dead)
        {
            ArrayList<Buff> buffs = ability.getBuffs();
            ArrayList<Integer> buffsChance = ability.getBuffsChance();
            for (int i = 0; i < buffs.size(); i++)
            {
                if (buffs.get(i).getBuffNum() != Buff.CLEANSE && buffs.get(i).getBuffNum() != Buff.REMOVE_DEBUFF)
                {
                    target.addAppliedBuff(buffs.get(i), buffsChance.get(i), this);
                }
            }
        }
        
        if (count < ability.getNumOfActivations())
        {
            heal(target, ability, count + 1);
        }
    }
    
    /**
     * The Monster's basic heal command. This method will execute all functions that are necessary during the Monster's turn
     *
     * @param target  The target Monster to heal
     * @param ability The ability to heal with
     */
    public void heal(Monster target, Ability ability)
    {
        heal(target, ability, 1);
    }
    
    /**
     * This method will activate all after-attack commands, including counters and rune effects
     *
     * @param target The target that was attacked
     */
    private void afterAttackProtocol(Monster target)
    {
        target.attacked(this);
        
        for (int i = target.appliedBuffs.size() - 1; i >= 0; i--)
        {
            Buff buff = target.appliedBuffs.get(i);
            if (buff.getNumTurns() <= 0)
            {
                target.removeBuff(buff);
            }
        }
        
        for (int i = target.appliedDebuffs.size() - 1; i >= 0; i--)
        {
            Debuff debuff = target.appliedDebuffs.get(i);
            if (debuff.getNumTurns() <= 0)
            {
                target.removeDebuff(debuff);
            }
        }
        
        for (int i = target.otherStats.size() - 1; i >= 0; i--)
        {
            Stat stat = target.otherStats.get(i);
            if (stat.getNumTurns() <= 0)
            {
                target.removeOtherStat(stat);
            }
        }
    }
    
    /**
     * This method will activate all after-turn commands, including counters and rune effects
     *
     * @param targetMons The Monster(s) that were targeted
     * @param attack     True if this turn was an attack turn, false otherwise
     */
    private void afterTurnProtocol(ArrayList<Monster> targetMons, boolean attack)
    {
        if (attack)
        {
            for (Monster target : targetMons)
            {
                afterAttackProtocol(target);
            }
        }
    }
    
    /**
     * This method will activate all after-turn commands, including counters and rune effects
     *
     * @param target The Monster that was targeted
     * @param attack True if this turn was an attack turn, false otherwise
     */
    private void afterTurnProtocol(Monster target, boolean attack)
    {
        if (attack)
        {
            afterAttackProtocol(target);
        }
    }
    
    /**
     * This method will activate all after-turn commands, including counters and rune effects
     *
     * @param o         The Monster(s) that were targeted. This object should be an ArrayList<Monster>, a Team, or a Monster
     * @param isCounter True if this turn was a counter, false otherwise
     * @param attack    True if this turn was an attack turn, false otherwise
     * @throws ConflictingArguments if Object o is not an ArrayList, a Team, or a Monster
     */
    public void afterTurnProtocol(Object o, boolean isCounter, boolean attack)
    {
        if (!(o instanceof Team || o instanceof ArrayList<?> || o instanceof Monster))
        {
            throw new ConflictingArguments("Object must be an ArrayList, a Team, or a Monster");
        }
        
        if (o instanceof Team)
        {
            afterTurnProtocol(((Team) o).getMonsters(), attack);
        }
        
        else if (o instanceof ArrayList<?>)
        {
            afterTurnProtocol((ArrayList<Monster>) o, attack);
        }
        
        else
        {
            afterTurnProtocol((Monster) o, attack);
        }
        
        if (!isCounter)
        {
            decreaseStatCooldowns();
            if (!isStunned())
            {
                decreaseAbilityCooldowns();
            }
        }
        
        dmgDealtThisTurn = 0;
        
        //Violent Rune
        if (numOfSets(Rune.VIOLENT) > 0 && Game.canCounter())
        {
            Game.setCanCounter(false);
            int random = new Random().nextInt(101);
            double extraTurnChance = 22;
            for (int i = 0; i < nomOfViolentRuneProcs; i++)
            {
                extraTurnChance *= 0.55;
            }
            if (random <= extraTurnChance)
            {
                this.atkBar = 2000;
                if (print)
                {
                    System.out.println(ConsoleColors.GREEN + "Extra Turn!" + ConsoleColors.RESET);
                }
            }
            else
            {
                nomOfViolentRuneProcs = 0;
            }
        }
        Game.setCanCounter(true);
    }
    
    /**
     * This method assumes this turn was not a counter and will activate all after-turn commands, including counters and rune effects
     *
     * @param o      The Monster(s) that were targeted. This object should be an ArrayList<Monster>, a Team, or a Monster
     * @param attack True if this turn was an attack turn, false otherwise
     * @throws ConflictingArguments if Object o is not an ArrayList, a Team, or a Monster
     */
    public void afterTurnProtocol(Object o, boolean attack)
    {
        afterTurnProtocol(o, false, attack);
    }
    
    /**
     * Returns the Monster's name and abilities in a readable form
     *
     * @return The formatted String
     */
    @Override
    public String toString()
    {
        String s = "";
        s += getName(true, true) + ":\n";
        for (Ability ability : abilities)
        {
            if (ability instanceof Leader_Skill)
            {
                continue;
            }
            s += "\t " + ability.toString(containsDebuff(new Debuff(Debuff.SILENCE, 1, 0)), containsDebuff(new Debuff(Debuff.OBLIVION, 1, 0))) + "\n\n";
        }
        return s;
    }
    
    /**
     * <pre>
     * Formats the Monster into a shorter readable String. Format:
     * name (hp, attack bar)
     *      Buffs
     *      Debuffs
     *      Other Effects (place in team) </pre>
     *
     * @param withElement true if the method should return the Monster's name with its element, false otherwise
     * @return The shorter formatted String
     */
    public String shortToString(boolean withElement)
    {
        String name = withElement ? getName(true, true) : this.name;
        String healthPercent = String.valueOf((1.0 * currentHp / maxHp) * 100);
        String atkBarPercent = String.valueOf(atkBar / 10);
        
        return name + ConsoleColors.GREEN + " (Hp = " + healthPercent.substring(0, healthPercent.indexOf(".") + 2) + "%" + ConsoleColors.CYAN +
                ", Attack Bar = " + (atkBarPercent.substring(0, atkBarPercent.indexOf(".") + 2)) + "%)" + ConsoleColors.BLUE + "\n\t\tBuffs: " +
                appliedBuffs + ConsoleColors.RED + "\n\t\tDebuffs: " + appliedDebuffs + ConsoleColors.PURPLE + "\n\t\tOther Effects: " + otherStats
                + ConsoleColors.RESET;
    }
    
    /**
     * Applies all buffs on the Monster that are relevant to the turn
     */
    private void applyBeginningOfTurnBuffs()
    {
        for (Buff buff : appliedBuffs)
        {
            switch (buff.getBuffNum())
            {
                case Buff.ATK_UP -> extraAtk = (int) (atk * 0.5);
                case Buff.DEF_UP -> extraDef = (int) (def * 0.7);
                case Buff.CRIT_RATE_UP -> extraCritRate = (int) (critRate * 0.3);
                case Buff.ATK_SPD_UP -> extraSpd = (int) (spd * 0.3);
            }
        }
    }
    
    /**
     * Applies all debuffs on the Monster that are relevant to the turn
     */
    private void applyBeginningOfTurnDebuffs()
    {
        for (Debuff debuff : appliedDebuffs)
        {
            switch (debuff.getDebuffNum())
            {
                case Debuff.GLANCING_HIT_UP -> extraGlancingRate = 50;
                case Debuff.DEC_ATK -> lessAtk = (int) (atk * 0.5);
                case Debuff.DEC_DEF -> lessDef = (int) (def * 0.7);
                case Debuff.DEC_ATK_SPD -> lessAtkSpd = (int) (spd * 0.3);
            }
        }
    }
    
    /**
     * Decreases the time remaining on all buffs and debuffs by one turn each (Except Threat)
     */
    public void decreaseStatCooldowns()
    {
        for (int i = appliedBuffs.size() - 1; i >= 0; i--)
        {
            Buff buff = appliedBuffs.get(i);
            buff.decreaseTurn();
            if (buff.getNumTurns() <= 0)
            {
                switch (buff.getBuffNum())
                {
                    case Buff.ATK_UP -> extraAtk = 0;
                    case Buff.DEF_UP -> extraDef = 0;
                    case Buff.CRIT_RATE_UP -> extraCritRate = 0;
                    case Buff.ATK_SPD_UP -> extraSpd = 0;
                    case Buff.SHIELD -> shield = 0;
                }
                appliedBuffs.remove(i);
            }
        }
        
        for (int i = appliedDebuffs.size() - 1; i >= 0; i--)
        {
            Debuff debuff = appliedDebuffs.get(i);
            debuff.decreaseTurn();
            if (debuff.getNumTurns() <= 0)
            {
                switch (debuff.getDebuffNum())
                {
                    case Debuff.GLANCING_HIT_UP -> extraGlancingRate = 0;
                    case Debuff.DEC_ATK -> lessAtk = 0;
                    case Debuff.DEC_DEF -> lessDef = 0;
                    case Debuff.DEC_ATK_SPD -> lessAtkSpd = 0;
                }
                appliedDebuffs.remove(i);
            }
        }
    }
    
    /**
     * Decreases the cooldown for all abilities by one turn each if applicable
     */
    public void decreaseAbilityCooldowns()
    {
        for (Ability ability : abilities)
        {
            ability.decCooldown();
        }
    }
    
    /**
     * Checks if the Monster has the given Debuff
     *
     * @param debuff The debuff to look for
     * @return True if the Monster has the given Debuff, false otherwise
     */
    public boolean containsDebuff(Debuff debuff)
    {
        for (Debuff d : appliedDebuffs)
        {
            if (d.equals(debuff))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has the Debuff associated with the provided number
     *
     * @param num The Debuff number to look for
     * @return True if the Monster has the Debuff associated with the provided number, false otherwise
     */
    public boolean containsDebuff(int num)
    {
        return containsDebuff(new Debuff(num));
    }
    
    /**
     * Checks if the Monster has the given Buff
     *
     * @param buff The buff to look for
     * @return True if the Monster has the given Buff, false otherwise
     */
    public boolean containsBuff(Buff buff)
    {
        for (Buff b : appliedBuffs)
        {
            if (b.equals(buff))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has the Debuff associated with the provided number
     *
     * @param num The Debuff number to look for
     * @return True if the Monster has the Debuff associated with the provided number, false otherwise
     */
    public boolean containsBuff(int num)
    {
        return containsBuff(new Buff(num));
    }
    
    /**
     * Checks if the Monster has the given Stat (Not buff or debuff)
     *
     * @param stat The Stat to look for
     * @return True if the Monster has the provided Stat
     */
    public boolean containsOtherStat(Stat stat)
    {
        for (Stat s : otherStats)
        {
            if (s.equals(stat))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has the Stat (Not buff or debuff) associated with the given number
     *
     * @param stat The Stat number to look for
     * @return True if the Monster has the Stat associated with the given number
     */
    public boolean containsOtherStat(int stat)
    {
        Stat s = new Stat(1);
        s.setStatNum(stat);
        return containsOtherStat(s);
    }
    
    /**
     * Removes a random Buff if there is at least one Buff on the Monster, does nothing otherwise
     *
     * @return The buff that was removed
     */
    public Buff removeRandomBuff()
    {
        if (appliedBuffs.isEmpty())
        {
            return new Buff(Buff.NULL, 0);
        }
        int rand = new Random().nextInt(appliedBuffs.size());
        return appliedBuffs.remove(rand);
    }
    
    /**
     * Removes a random Debuff if there is at least one Debuff on the Monster, does nothing otherwise
     *
     * @return The debuff that was removed
     */
    public Debuff removeRandomDebuff()
    {
        if (appliedDebuffs.isEmpty())
        {
            return new Debuff(-1, 0, 0);
        }
        int rand = new Random().nextInt(appliedDebuffs.size());
        return appliedDebuffs.remove(rand);
    }
    
    /**
     * Adds a Stat (not buff or debuff) to the Monster
     *
     * @param stat The Stat to add
     */
    public void addOtherStat(Stat stat)
    {
        otherStats.add(stat);
    }
    
    /**
     * @return How much damage this Monster has dealt this turn
     */
    public double getDmgDealtThisTurn()
    {
        return dmgDealtThisTurn;
    }
    
    /**
     * @return All Stats (not buff or debuffs) on the Monster
     */
    public ArrayList<Stat> getOtherStats()
    {
        return otherStats;
    }
    
    /**
     * Removes all instances of the provided Stat
     *
     * @param stat The Stat to look for
     */
    public void removeOtherStat(Stat stat)
    {
        for (int i = otherStats.size() - 1; i >= 0; i--)
        {
            if (otherStats.get(i).equals(stat))
            {
                otherStats.remove(i);
            }
        }
    }
    
    /**
     * Sets the Monster's max Hp
     *
     * @param maxHp The Monster's new max Hp
     */
    public void setMaxHp(double maxHp)
    {
        this.maxHp = (int) Math.ceil(maxHp);
    }
    
    /**
     * Sets the Monster's attack power
     *
     * @param atk The Monster's new attack power
     */
    public void setAtk(double atk)
    {
        this.atk = (int) Math.ceil(atk);
    }
    
    /**
     * Sets the Monster's attack speed
     *
     * @param spd The Monster's new attack speed
     */
    public void setSpd(double spd)
    {
        this.spd = (int) Math.ceil(spd);
    }
    
    /**
     * Checks if the Monster is stunned on this turn. (If the Monster is slept, frozen, or stunned)
     *
     * @return True if the Monster is stunned, false otherwise
     */
    public boolean isStunned()
    {
        return containsDebuff(Debuff.SLEEP) || containsDebuff(Debuff.FREEZE) || containsDebuff(Debuff.STUN);
    }
    
    /**
     * Removes all debuffs on the Monster
     *
     * @return The number of debuffs removed
     */
    public int cleanse()
    {
        int size = appliedDebuffs.size();
        appliedDebuffs = new ArrayList<>();
        return size;
    }
    
    /**
     * Compares to Monsters
     *
     * @param mon The Monster to compare
     * @return True if the two Monster's names are equal
     */
    public boolean equals(Monster mon)
    {
        return name.equals(mon.name);
    }
    
    /**
     * Returns the Ability associated with the given number if there is one
     *
     * @param num the Ability's number
     * @return The Ability associated with the given number if there is one, returns null otherwise
     */
    public Ability getAbility(int num)
    {
        if (num > abilities.size() || num < 1)
        {
            return null;
        }
        return abilities.get(num - 1);
    }
    
    /**
     * Checks if the Monster has no health left and does not have any Buffs that prevent death. If so, kills the Monster.
     */
    public void kill()
    {
        if (dead || currentHp > 0)
        {
            return;
        }
        
        //Endure
        if (containsBuff(new Buff(Buff.ENDURE, 1)))
        {
            currentHp = 1;
        }
        
        //Protect soul
        else if (containsBuff(new Buff(Buff.SOUL_PROTECTION, 1)))
        {
            currentHp = (int) (maxHp * 0.3);
            removeBuff(new Buff(13, 1));
        }
        else
        {
            currentHp = 0;
            while (!appliedBuffs.isEmpty() || !appliedDebuffs.isEmpty())
            {
                decreaseStatCooldowns();
            }
            atkBar = 0;
            dead = true;
            if (print)
            {
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + name + " died!\n" + ConsoleColors.RESET);
            }
            Team other = game.getOtherTeam();
            for (int i = 0; i < other.size(); i++)
            {
                Monster m = other.get(i);
                if (m.getProvoke() != null)
                {
                    Provoke p = m.getProvoke();
                    if (p.getCaster().equals(this))
                    {
                        m.removeDebuff(p);
                    }
                }
            }
        }
    }
    
    /**
     * Formats a set of numbers into an ArrayList of Buffs
     *
     * @param args The numbers to format. Format: Buff number, number of turns, repeat as needed
     * @return The ArrayList of Buffs specified by the varargs
     */
    public static ArrayList<Buff> abilityBuffs(int... args)
    {
        if (args.length % 2 != 0)
        {
            throw new BadArgumentLength("Bad argument length: " + args.length);
        }
        
        ArrayList<Buff> buffs = new ArrayList<>();
        for (int i = 0; i < args.length; i += 2)
        {
            buffs.add(new Buff(args[i], args[i + 1]));
        }
        return buffs;
    }
    
    /**
     * Formats a set of numbers into an ArrayList of Debuffs
     *
     * @param args The numbers to format. Format: Debuff number, number of turns, whether it goes through immunity (0 for false, 1 for true), repeat as
     *             needed
     * @return The ArrayList of Debuffs specified by the varargs
     */
    public static ArrayList<Debuff> abilityDebuffs(int... args)
    {
        if (args.length % 3 != 0)
        {
            throw new BadArgumentLength("Bad argument length: " + args.length);
        }
        
        ArrayList<Debuff> debuffs = new ArrayList<>();
        for (int i = 0; i < args.length; i += 3)
        {
            debuffs.add(new Debuff(args[i], args[i + 1], args[i + 2]));
        }
        return debuffs;
    }
    
    /**
     * Formats a set of numbers into an ArrayList of Integers
     *
     * @param args The numbers to enter
     * @return The ArrayList of Integers
     */
    public static ArrayList<Integer> abilityChances(int... args)
    {
        ArrayList<Integer> chances = new ArrayList<>();
        for (int i : args)
        {
            chances.add(i);
        }
        return chances;
    }
    
    /**
     * Checks if the Ability associated with the provided number is passive
     *
     * @param abilityNum The Ability number
     * @return True if the Ability associated with the provided number is passive, false otherwise
     */
    public boolean abilityIsPassive(int abilityNum)
    {
        return abilities.get(abilityNum - 1).isPassive();
    }
    
    /**
     * Calculates the current health ratio of the Monster
     *
     * @return The ratio of current health to max health of the Monster (current hp / max hp)
     */
    public double getHpRatio()
    {
        String temp = 100.0 * currentHp / maxHp + "";
        String s;
        int place = 4;
        if (temp.length() > 4 && !temp.equals("100.0"))
        {
            s = temp.substring(0, 4);
        }
        else
        {
            s = temp;
        }
        place++;
        while (!Team.stringHasNumOtherThanZero(s) && place < temp.length())
        {
            s = temp.substring(0, place);
            place++;
        }
        return Double.parseDouble(s);
    }
    
    /**
     * Activates passive abilities that are triggered when attacked (ex. Miho)
     *
     * @param attacker The attacking Monster
     */
    public void attacked(Monster attacker)
    {
        if (hasThreat())
        {
            for (Buff b : appliedBuffs)
            {
                if (b instanceof Threat)
                {
                    b.decreaseTurn(1);
                }
            }
        }
        
        //Counter
        if (containsBuff(Buff.COUNTER) && !isDead() && Game.canCounter() && !isStunned())
        {
            Game.setCanCounter(false);
            if (print)
            {
                System.out.println("Counter!");
            }
            counter(attacker);
        }
        
        //Revenge Rune
        if (numOfSets(Rune.REVENGE) > 0 && Game.canCounter() && !isStunned() && !isStunned())
        {
            Game.setCanCounter(false);
            int random = new Random().nextInt(101);
            if (random <= 15 * numOfSets(Rune.REVENGE))
            {
                if (print)
                {
                    System.out.println("Counter! (Revenge Rune)");
                }
                counter(attacker);
            }
        }
    }
    
    /**
     * Counters the attacking Monster
     *
     * @param attacker The Monster who attacked
     */
    private void counter(Monster attacker)
    {
        ArrayList<Buff> targetBuffs = new ArrayList<>(appliedBuffs);
        ArrayList<Debuff> targetDebuffs = new ArrayList<>(appliedDebuffs);
        ArrayList<Stat> targetOtherEffects = new ArrayList<>(otherStats);
        ArrayList<Integer> targetAbilityCooldowns = new ArrayList<>();
        double currentAtkBar = atkBar;
        
        for (Ability ability : abilities)
        {
            targetAbilityCooldowns.add(ability.getTurnsRemaining());
        }
        
        nextTurn(attacker, 1);
        
        appliedBuffs = targetBuffs;
        appliedDebuffs = targetDebuffs;
        otherStats = targetOtherEffects;
        ArrayList<Ability> abilityArrayList = abilities;
        for (int i = 0; i < abilityArrayList.size(); i++)
        {
            abilityArrayList.get(i).setToNumTurns(targetAbilityCooldowns.get(i));
        }
        
        for (Buff b : targetBuffs)
        {
            b.decreaseTurn(-1);
        }
        
        for (Debuff d : targetDebuffs)
        {
            d.decreaseTurn(-1);
        }
        
        for (Stat s : targetOtherEffects)
        {
            s.decreaseTurn(-1);
        }
        
        atkBar = currentAtkBar;
    }
    
    /**
     * @return The Provoke debuff on a Monster if there is one
     */
    public Provoke getProvoke()
    {
        Team other = game.getOtherTeam();
        for (Debuff debuff : appliedDebuffs)
        {
            for (int i = 0; i < other.size(); i++)
            {
                if (debuff instanceof Provoke)
                {
                    return ((Provoke) debuff);
                }
            }
        }
        return null;
    }
    
    /**
     * @return True if Monster has a Threat buff
     */
    public boolean hasThreat()
    {
        for (Buff b : appliedBuffs)
        {
            if (b instanceof Threat)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return True if the Monster has a Defend buff
     */
    private boolean hasDefend()
    {
        for (Buff b : appliedBuffs)
        {
            if (b instanceof Defend)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return True if the Monster has a shield
     */
    private boolean hasShield()
    {
        for (Buff buff : appliedBuffs)
        {
            if (buff instanceof Shield)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return The Shield buff on the Monster if there is one
     */
    private Shield getShield()
    {
        for (Buff b : appliedBuffs)
        {
            if (b instanceof Shield)
            {
                return (Shield) b;
            }
        }
        return new Shield(0, 0);
    }
    
    /**
     * @return True if the Monster has a DecAtkBar debuff
     */
    private boolean hasDecAtkBar()
    {
        for (Debuff d : appliedDebuffs)
        {
            if (d instanceof DecAtkBar)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return The DecAtkBar on the Monster if it has one
     */
    private DecAtkBar getDecAtkBar()
    {
        for (Debuff d : appliedDebuffs)
        {
            if (d instanceof DecAtkBar)
            {
                return ((DecAtkBar) d);
            }
        }
        return new DecAtkBar(0);
    }
    
    /**
     * @return The Defend buff on the Monster if there is one
     */
    private Defend getDefend()
    {
        for (Buff b : appliedBuffs)
        {
            if (b instanceof Defend)
            {
                return ((Defend) b);
            }
        }
        return new Defend(1, this);
    }
    
    /**
     * @return A list of viable ability numbers
     */
    public ArrayList<Integer> getViableAbilityNumbers()
    {
        ArrayList<Integer> returnArray = new ArrayList<>();
        for (int i = 0; i < abilities.size(); i++)
        {
            Ability ability = abilities.get(i);
            if (ability.isViableAbility(containsDebuff(Debuff.SILENCE)))
            {
                returnArray.add(i + 1);
            }
        }
        return returnArray;
    }
    
    /**
     * @return True if the Monster was hit with a crit this turn
     */
    public boolean wasCrit()
    {
        return wasCrit;
    }
    
    /**
     * Prints the Monsters stats followed by its abilities and runes
     */
    public void printWithStats()
    {
        
        if (print)
        {
            System.out.printf("%s: %sHp: %s %s+%s; %sAttack: %s %s+%s; %sDefense: %s %s+%s; %sSpeed: %d %s+%d; %sCrit rate: %d%%; %sCrit damage: %d%%; " +
                            "%sResistance: %d%%; Accuracy: %s%d%%%s\n\n", getName(true, false), ConsoleColors.GREEN,
                    numWithCommas(baseMaxHp), ConsoleColors.GREEN_BOLD_BRIGHT, numWithCommas(maxHp - baseMaxHp), ConsoleColors.RED, numWithCommas(baseAtk),
                    ConsoleColors.RED_BOLD_BRIGHT, numWithCommas(atk - baseAtk), ConsoleColors.YELLOW, numWithCommas(baseDef),
                    ConsoleColors.YELLOW_BOLD_BRIGHT, numWithCommas(def - baseDef), ConsoleColors.CYAN, baseSpd, ConsoleColors.CYAN_BOLD_BRIGHT,
                    spd - baseSpd, ConsoleColors.BLUE, critRate, ConsoleColors.PURPLE, critDmg, ConsoleColors.RESET, Math.min(resistance, 100),
                    (accuracy >= 100) ? ConsoleColors.RED : "", Math.min(accuracy, 100), ConsoleColors.RESET);
        }
        for (Ability ability : abilities)
        {
            if (print)
            {
                System.out.println("\t " + ability.toString(containsDebuff(new Debuff(Debuff.SILENCE, 1, 0)),
                        containsDebuff(Debuff.OBLIVION))
                        + "\n\n");
            }
        }
        
        
        if (runes != null)
        {
            if (print)
            {
                System.out.println(ConsoleColors.PURPLE_BOLD_BRIGHT + "Rune sets:");
            }
            ArrayList<Integer> runeTypes = new ArrayList<>();
            for (Rune rune : runes)
            {
                runeTypes.add(rune.getType());
            }
            ArrayList<String> types = new ArrayList<>();
            while (!runeTypes.isEmpty())
            {
                int integer = runeTypes.get(runeTypes.size() - 1);
                for (int i = runeTypes.size() - 1; i >= 0; i--)
                {
                    if (runeTypes.get(i) == integer)
                    {
                        runeTypes.remove(runeTypes.get(i));
                    }
                }
                types.add(Rune.numToType(integer) + " x" + numOfSets(integer));
            }
            Collections.reverse(types);
            for (String string : types)
            {
                if (print)
                {
                    System.out.print("\t" + string + "\t");
                }
            }
        }
        
        if (print)
        {
            System.out.println(ConsoleColors.RESET + "\n");
        }
    }
    
    /**
     * Adds commas to the given number
     *
     * @param num The number to add commas to
     * @return The number with commas
     */
    public static String numWithCommas(int num)
    {
        String s = num + "";
        int count = s.length() - 1;
        String newString = "";
        for (int i = 0; i < s.length(); i++)
        {
            newString += s.charAt(i);
            if (count % 3 == 0 && count != 0)
            {
                newString += ",";
            }
            count--;
        }
        return newString;
    }
    
    /**
     * Formats a list of numbers into an ArrayList of SubAttributes
     *
     * @param args The number to format. Format: attribute number, amount
     * @return The ArrayList of SubAttributes
     */
    public static ArrayList<SubAttribute> runeSubs(int... args)
    {
        ArrayList<SubAttribute> subs = new ArrayList<>();
        if (args.length % 2 == 1)
        {
            throw new BadArgumentLength("Var args must be even");
        }
        for (int i = 0; i < args.length - 1; i += 2)
        {
            subs.add(new SubAttribute(args[i], args[i + 1]));
        }
        return subs;
    }
    
    /**
     * Applies all stat rune effects that are not team-based
     */
    private void applyRuneSetEffectsForBeginningOfGame()
    {
        tempMaxHp += baseMaxHp * (0.15 * numOfSets(Rune.ENERGY));
        tempAtk += baseAtk * (0.35 * numOfSets(Rune.FATAL));
        critRate += (12 * numOfSets(Rune.BLADE));
        spd += (int) (baseSpd * (0.25 * numOfSets(Rune.SWIFT)) + 0.99);
        accuracy += (20 * numOfSets(Rune.FOCUS));
        tempDef += 1.0 * baseDef * (0.15 * numOfSets(Rune.GUARD));
        resistance += (20 * numOfSets(Rune.ENDURE));
        if (numOfSets(Rune.WILL) > 0)
        {
            addAppliedBuff(Buff.IMMUNITY, numOfSets(Rune.WILL), new Monster());
        }
        critDmg += (40 * numOfSets(Rune.RAGE));
    }
    
    /**
     * Counts the number of runes for the given type number
     *
     * @param type The rune number to check
     * @return The number of runes for the number
     */
    private int countNumOfEffectRunes(int type)
    {
        if (runes == null)
        {
            return 0;
        }
        int count = 0;
        for (Rune rune : runes)
        {
            count += (type == rune.getType()) ? 1 : 0;
        }
        return count;
    }
    
    /**
     * Counts the number of set effects for the type number
     *
     * @param type The rune number to check
     * @return The number of set effects for the number
     */
    public int numOfSets(int type)
    {
        
        if (countNumOfEffectRunes(type) >= 4 && (type == Rune.FATAL || type == Rune.SWIFT || type == Rune.VAMPIRE || type == Rune.DESPAIR ||
                type == Rune.VIOLENT ||
                type == Rune.RAGE))
        
        {
            return 1;
        }
        if (type == Rune.ELEMENTARTIFACT || type == Rune.TYPEARTIFACT)
        {
            return countNumOfEffectRunes(type);
        }
        
        return countNumOfEffectRunes(type) / 2;
    }
    
    /**
     * Activates all passives triggered before a Monster's turn
     *
     * @param nextMon     The Monster whose turn it is
     * @param self        True if this.equals(nextMon)
     * @param enemyTurn   True if nextMon is on the opposing Team
     * @param hasOblivion True if the Monster has the Oblivion debuff
     */
    public void beforeTurnProtocol(Monster nextMon, boolean self, boolean enemyTurn, boolean hasOblivion)
    {
        if (self && enemyTurn)
        {
            throw new ConflictingArguments("self and enemyTurn cannot both be true");
        }
    }
    
    /**
     * Is overridden in certain Monsters to reduce their damage taken
     *
     * @param num The current damage to take
     * @return The updated damage to take
     */
    public double dmgReductionProtocol(double num)
    {
        return num;
    }
    
    /**
     * Is overridden in certain Monsters to increase the damage they deal
     *
     * @param num The current damage to deal
     * @return The updated damage to deal
     */
    public double dmgIncProtocol(double num)
    {
        return num;
    }
    
    /**
     * Overridden in certain Monsters to activate the passive abilities that are triggered after they are hit
     *
     * @param attacker The attacking Monster
     */
    public void afterHitProtocol(Monster attacker)
    {
    }
    
    /**
     * Sets the altered glancing rate as determined by an ability
     *
     * @param abilityGlancingRateChange The new value
     */
    public void setAbilityGlancingRateChange(int abilityGlancingRateChange)
    {
        this.abilityGlancingRateChange = abilityGlancingRateChange;
    }
    
    /**
     * Checks if the Monster has a support ability that targets multiple Monsters
     *
     * @return True if the Monster has a support ability that targets multiple Monsters, false otherwise
     */
    public boolean hasTeamSupportAbility()
    {
        for (Ability ability : abilities)
        {
            if (!ability.targetsEnemy() && !ability.targetsSelf())
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return The Monster's Team support abilities if it has one
     */
    public ArrayList<Heal_Ability> getTeamSupportAbilities()
    {
        ArrayList<Heal_Ability> returnList = new ArrayList<>();
        for (Ability ability : abilities)
        {
            if (ability instanceof Heal_Ability)
            {
                returnList.add((Heal_Ability) ability);
            }
        }
        return returnList;
    }
    
    /**
     * Checks if the Monster has an Ability that targets itself
     *
     * @return True if the Monster has an Ability that targets itself, false otherwise
     */
    public boolean hasSelfSupportAbility()
    {
        for (Ability ability : abilities)
        {
            if (ability.targetsSelf())
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return The Monster's abilities that target itself
     */
    public ArrayList<Ability> getSelfSupportAbilities()
    {
        ArrayList<Ability> returnList = new ArrayList<>();
        for (Ability ability : abilities)
        {
            if (ability.targetsSelf() && !(ability instanceof Passive))
            {
                returnList.add(ability);
            }
        }
        return returnList;
    }
    
    /**
     * Checks if the Monster has a support ability that applies multiple buffs
     *
     * @return True if the Monster has a support ability that applies multiple buffs, false otherwise
     */
    public boolean hasSupportAbilityWithMultipleBuffs()
    {
        for (Ability ability : abilities)
        {
            if (ability.getBuffs().size() >= 2)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return The Monster's support abilities that apply multiple buffs
     */
    public ArrayList<Ability> getSupportAbilitiesWithMultipleBuffs()
    {
        ArrayList<Ability> returnList = new ArrayList<>();
        for (Ability ability : abilities)
        {
            if (ability.getBuffs().size() >= 2)
            {
                returnList.add(ability);
            }
        }
        return returnList;
    }
    
    /**
     * @return The Monster's attack abilities
     */
    public ArrayList<Ability> getAttackAbilities()
    {
        ArrayList<Ability> returnList = new ArrayList<>();
        for (Ability ability : abilities)
        {
            if (ability.targetsEnemy())
            {
                returnList.add(ability);
            }
        }
        return returnList;
    }
    
    /**
     * @return The number of debuffs that increase the Monster's damage taken (brand/decrease defense)
     */
    public int getNumOfDmgTakenIncreasingDebuffs()
    {
        return countDebuff(Debuff.BRAND) + countDebuff(Debuff.DEC_DEF);
    }
    
    /**
     * Resets the Monster to its state after applying runes
     */
    public void reset()
    {
        while (!appliedBuffs.isEmpty() || !appliedDebuffs.isEmpty())
        {
            decreaseStatCooldowns();
        }
        otherStats = new ArrayList<>();
        
        for (Ability ability : abilities)
        {
            ability.setToNumTurns(0);
        }
        destroyedHp = 0;
        currentHp = maxHp;
        atkBar = 0;
        dead = false;
    }
    
    /**
     * @return True if the Monster has a leader skill
     */
    public boolean hasLeaderSkill()
    {
        for (Ability ability : abilities)
        {
            if (ability instanceof Leader_Skill)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Applies the Monster's leader skill if it has one, does nothing otherwise
     *
     * @param applyToTeam The Team to apply the skill to
     */
    public void applyLeaderSkill(Team applyToTeam)
    {
        if (!hasLeaderSkill())
        {
            return;
        }
        for (Ability ability : abilities)
        {
            if (ability instanceof Leader_Skill skill)
            {
                skill.apply(applyToTeam);
            }
        }
    }
    
    /**
     * Sets the current Game in use
     *
     * @param g The new Game
     */
    public static void setGame(Game g)
    {
        game = g;
    }
    
    /**
     * Gets a Monster name from the user and prints a detailed description of the Monster
     */
    public static void inspect()
    {
        String inputInspect;
        System.out.println("Which monster do you want to inspect?");
        do
        {
            inputInspect = scan.nextLine();
        }
        while (!stringIsMonsterName(inputInspect));
        int runeSetNum = Main.getRuneSetNum();
        try
        {
            inputInspect = inputInspect.replaceAll(" ", "_");
            inputInspect = toProperName(inputInspect);
            String element = monsterNamesDatabase.get(inputInspect.replaceAll("_", " "));
            String name = "Monsters." + element + "." + inputInspect;
            Class<?> c = Class.forName(name);
            if (runeSetNum == -1)
            {
                ((Monster) c.getDeclaredConstructor().newInstance()).printWithStats();
            }
            else
            {
                try
                {
                    ((Monster) c.getDeclaredConstructor(String.class).newInstance(inputInspect + runeSetNum + ".csv")).printWithStats();
                }
                catch (NoSuchMethodException ignored)
                {
                }
            }
            if (runeSetNum != -1)
            {
                scan.nextLine();
            }
        }
        catch (NoSuchMethodException e)
        {
            System.out.println("Error, this monster does not have a constructor to assign a custom rune set");
            inspect();
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Oops! Rune set not found.");
            inspect();
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Searches the provided ArrayList for a Monster whose name matches the provided String
     *
     * @param s    The name of the Monster to look for
     * @param mons The list of Monsters to look in
     * @return true if the ArrayList contains a Monster whose name equals the provided String, false otherwise.
     */
    public static boolean stringIsMonsterName(String s, ArrayList<Monster> mons)
    {
        for (Monster mon : mons)
        {
            if (mon.getName(false, false).equalsIgnoreCase(s))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Searches the Monster database for a name that matches the provided String. This is the same as calling
     * {@link Monster#stringIsMonsterName(String, ArrayList)} with {@link Monster#monsterNamesDatabase}
     *
     * @param s The name to look for
     * @return true if the name is in the monster database
     */
    public static boolean stringIsMonsterName(String s)
    {
        if (monsterNamesDatabase.isEmpty())
        {
            setDatabase();
        }
        
        for (String string : monsterNamesDatabase.keySet())
        {
            if (string.equalsIgnoreCase(s))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Formats the given String into a String readable by the program
     *
     * @param name the String to format
     * @return the formatted String
     */
    public static String toProperName(String name)
    {
        String returnName = "";
        name = name.replaceAll(" ", "_");
        for (int i = 0; i < name.length(); i++)
        {
            String character = String.valueOf(name.charAt(i));
            if (i == 0)
            {
                returnName += character.toUpperCase();
            }
            else if (name.charAt(i - 1) == '_')
            {
                returnName += character.toUpperCase();
            }
            else
            {
                returnName += character;
            }
        }
        return returnName;
    }
    
    /**
     * Sets {@link Monster#monsterNamesDatabase}
     */
    public static void setDatabase()
    {
        Scanner read = new Scanner(Objects.requireNonNull(Monster.class.getResourceAsStream("Monster database.csv")));
        while (read.hasNextLine())
        {
            String[] monAndElement = read.nextLine().split(",");
            monsterNamesDatabase.put(monAndElement[0], monAndElement[1]);
        }
        read.close();
        Main.sort(monsterNamesDatabase);
    }
}