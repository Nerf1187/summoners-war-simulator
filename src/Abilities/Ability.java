package Abilities;

import Errors.*;
import Game.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * The parent class for all abilities. Should only be constructed in subclasses or when the ability only targets the monster who has it (ex. Xiao Ling's
 * second skill) so the Game.Auto_Play algorithm functions properly.
 *
 * @author Anthony (Tony) Youssef
 */
public class Ability
{
    private double dmg;
    private final double healingPercent;
    private final int maxCooldown, numOfActivations;
    private int turnsRemaining = 0;
    protected String description;
    protected final String name;
    private final ArrayList<Debuff> debuffs;
    private final ArrayList<Buff> buffs;
    private final ArrayList<Integer> debuffsChance, buffsChance;
    private final boolean targetEnemy, passive, ignoreDef, ignoreDmgReduction, targetsSelf;
    
    //base constructor
    
    /**
     * Constructs a new Ability
     *
     * @param name                the name of the monster
     * @param multiplier          the attack multiplier (ex. 2.5)
     * @param healingPercent      the percent of the max hp to heal (ex. 0.25)
     * @param numOfActivations    the amount of times the ability activates when called
     * @param description         the description to print
     * @param debuffs             the debuffs to apply to the target when called
     * @param debuffChances       the chance that each debuff will apply
     * @param buffs               the buffs to apply to self when called
     * @param buffChances         the chance that each buff will apply
     * @param cooldown            the cooldown of the ability
     * @param targetsEnemy        whether the ability targets the enemy
     * @param isPassive           whether this ability is a passive
     * @param ignoresDefense      whether this ability ignores the targets defense
     * @param targetsSelf         whether this ability targets self
     * @param ignoresDmgReduction whether this ability ignores damage reduction effects
     * @throws ConflictingArguments if targetsEnemy and targetsSelf are both true
     * @throws BadArgumentLength    if debuffs and debuffChances or buffs and buffChances are different lengths
     */
    public Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Debuff> debuffs,
            ArrayList<Integer> debuffChances, ArrayList<Buff> buffs, ArrayList<Integer> buffChances, int cooldown, boolean targetsEnemy,
            boolean isPassive, boolean ignoresDefense, boolean targetsSelf, boolean ignoresDmgReduction)
    {
        if (targetsEnemy && targetsSelf)
        {
            throw new ConflictingArguments(name + " can not target enemy and self");
        }
        if (debuffs.size() != debuffChances.size())
        {
            throw new BadArgumentLength("Debuff size and debuffs chance size not the same");
        }
        if (buffs.size() != buffChances.size())
        {
            throw new BadArgumentLength("Buff size and buffs chance size not the same");
        }
        
        this.dmg = multiplier;
        this.healingPercent = healingPercent;
        this.description = description;
        if (!debuffs.isEmpty())
        {
            this.debuffs = debuffs;
            this.debuffsChance = debuffChances;
        }
        else
        {
            this.debuffs = new ArrayList<>();
            this.debuffsChance = new ArrayList<>();
        }
        if (!buffs.isEmpty())
        {
            this.buffs = buffs;
            this.buffsChance = buffChances;
        }
        else
        {
            this.buffs = new ArrayList<>();
            this.buffsChance = new ArrayList<>();
        }
        this.maxCooldown = (cooldown == 0) ? cooldown : cooldown + 1;
        this.name = name;
        this.targetEnemy = targetsEnemy;
        passive = isPassive;
        ignoreDef = ignoresDefense;
        this.ignoreDmgReduction = ignoresDmgReduction;
        this.targetsSelf = targetsSelf;
        this.numOfActivations = numOfActivations;
        descriptionWithLineBreaks();
    }
    
    /**
     * Constructs a new Ability with no debuffs
     *
     * @param name                the name of the monster
     * @param multiplier          the attack multiplier (ex. 2.5)
     * @param healingPercent      the percent of the max hp to heal (ex. 0.25)
     * @param numOfActivations    the amount of times the ability activates when called
     * @param description         the description to print
     * @param buffs               the buffs to apply to self when called
     * @param buffChances         the chance that each buff will apply
     * @param cooldown            the cooldown of the ability
     * @param targetsEnemy        whether the ability targets the enemy
     * @param isPassive           whether this ability is a passive
     * @param ignoresDefense      whether this ability ignores the targets defense
     * @param targetsSelf         whether this ability targets self
     * @param ignoresDmgReduction whether this ability ignores damage reduction effects
     * @param ignore              an int to distinguish this constructor from another
     * @throws ConflictingArguments if targetsEnemy and targetsSelf are true
     * @throws BadArgumentLength    if buffs and buffChances are different lengths
     */
    public Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Buff> buffs,
            ArrayList<Integer> buffChances, int cooldown, boolean targetsEnemy, boolean isPassive, boolean ignoresDefense, boolean targetsSelf,
            boolean ignoresDmgReduction, int ignore)
    {
        this(name, multiplier, healingPercent, numOfActivations, description, new ArrayList<>(), new ArrayList<>(), buffs, buffChances, cooldown,
                targetsEnemy, isPassive, ignoresDefense, targetsSelf, ignoresDmgReduction);
    }
    
    /**
     * Constructs a new Ability with no buffs
     *
     * @param name                the name of the monster
     * @param multiplier          the attack multiplier (ex. 2.5)
     * @param healingPercent      the percent of the max hp to heal (ex. 0.25)
     * @param numOfActivations    the amount of times the ability activates when called
     * @param description         the description to print
     * @param debuffs             the debuffs to apply to the target when called
     * @param debuffChances       the chance that each debuff will apply
     * @param cooldown            the cooldown of the ability
     * @param targetsEnemy        whether the ability targets the enemy
     * @param isPassive           whether this ability is a passive
     * @param ignoresDefense      whether this ability ignores the targets defense
     * @param targetsSelf         whether this ability targets self
     * @param ignoresDmgReduction whether this ability ignores damage reduction effects
     * @throws ConflictingArguments if targetsEnemy and targetsSelf are true
     * @throws BadArgumentLength    if debuffs and debuffChances are different lengths
     */
    public Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Debuff> debuffs,
            ArrayList<Integer> debuffChances, int cooldown, boolean targetsEnemy, boolean isPassive, boolean ignoresDefense, boolean targetsSelf,
            boolean ignoresDmgReduction)
    {
        this(name, multiplier, healingPercent, numOfActivations, description, debuffs, debuffChances, new ArrayList<>(), new ArrayList<>(), cooldown,
                targetsEnemy, isPassive, ignoresDefense, targetsSelf, ignoresDmgReduction);
    }
    
    /**
     * Constructs a new Ability
     *
     * @param name                the name of the monster
     * @param multiplier          the attack multiplier (ex. 2.5)
     * @param healingPercent      the percent of the max hp to heal (ex. 0.25)
     * @param numOfActivations    the amount of times the ability activates when called
     * @param description         the description to print
     * @param buffs               the buffs to apply to self when called
     * @param buffChances         the chance that each buff will apply
     * @param debuffs             the debuffs to apply to the target when called
     * @param debuffChances       the chance that each debuff will apply
     * @param cooldown            the cooldown of the ability
     * @param targetsEnemy        whether the ability targets the enemy
     * @param isPassive           whether this ability is a passive
     * @param ignoresDefense      whether this ability ignores the targets defense
     * @param targetsSelf         whether this ability targets self
     * @param ignoresDmgReduction whether this ability ignores damage reduction effects
     * @param ignore              an int to distinguish this constructor from another
     * @throws ConflictingArguments if targetsEnemy and targetsSelf are true
     * @throws BadArgumentLength    if debuffs and debuffChances or buffs and buffChances are not the same length
     */
    public Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Buff> buffs,
            ArrayList<Integer> buffChances, ArrayList<Debuff> debuffs, ArrayList<Integer> debuffChances, int cooldown, boolean targetsEnemy,
            boolean isPassive, boolean ignoresDefense, boolean targetsSelf, boolean ignoresDmgReduction, int ignore)
    {
        this(name, multiplier, healingPercent, numOfActivations, description, debuffs, debuffChances, buffs, buffChances, cooldown, targetsEnemy,
                isPassive, ignoresDefense, targetsSelf, ignoresDmgReduction);
    }
    
    /**
     * Constructs a new Ability with no buffs or debuffs
     *
     * @param name                the name of the monster
     * @param multiplier          the attack multiplier (ex. 2.5)
     * @param healingPercent      the percent of the max hp to heal (ex. 0.25)
     * @param numOfActivations    the amount of times the ability activates when called
     * @param description         the description to print
     * @param cooldown            the cooldown of the ability
     * @param targetsEnemy        whether the ability targets the enemy
     * @param isPassive           whether this ability is a passive
     * @param ignoresDefense      whether this ability ignores the targets defense
     * @param targetsSelf         whether this ability targets self
     * @param ignoresDmgReduction whether this ability ignores damage reduction effects
     * @throws ConflictingArguments if targetsEnemy and targetsSelf are true
     */
    public Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, int cooldown, boolean targetsEnemy,
            boolean isPassive, boolean ignoresDefense, boolean targetsSelf, boolean ignoresDmgReduction)
    {
        this(name, multiplier, healingPercent, numOfActivations, description, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
                , cooldown, targetsEnemy, isPassive, ignoresDefense, targetsSelf, ignoresDmgReduction);
    }
    
    /**
     * Constructs an empty Ability used specifically for the {@link Leader_Skill} class
     */
    protected Ability()
    {
        this("", 0, 0, 0, "", 0, false, false, false, false, false);
    }
    
    /**
     * Formats the Ability description with line breaks
     */
    private void descriptionWithLineBreaks()
    {
        String s = "";
        boolean breakLine = false;
        for (int i = 0; i < description.length(); i++)
        {
            s += description.charAt(i);
            if (i != 0 && i % 80 == 0)
            {
                breakLine = true;
            }
            if (breakLine && description.charAt(i) == ' ')
            {
                s += "\n\t\t\t";
                breakLine = false;
            }
        }
        description = s;
    }
    
    /**
     * @return the damage multiplier
     */
    public double getDmgMultiplier()
    {
        return dmg;
    }
    
    /**
     * @return the healing multiplier
     */
    public double getHealingPercent()
    {
        return healingPercent;
    }
    
    /**
     * @return whether this Ability ignores the targets defense
     */
    public boolean ignoresDefense()
    {
        return ignoreDef;
    }
    
    /**
     * @return whether this Ability targets the enemy
     */
    public boolean targetsEnemy()
    {
        return targetEnemy;
    }
    
    /**
     * @return the buffs for this Ability
     */
    public ArrayList<Buff> getBuffs()
    {
        return buffs;
    }
    
    /**
     * @return the buff chances for this Ability
     */
    public ArrayList<Integer> getBuffsChance()
    {
        return buffsChance;
    }
    
    /**
     * @return the debuffs for this Ability
     */
    public ArrayList<Debuff> getDebuffs()
    {
        return debuffs;
    }
    
    /**
     * Adds a new debuff to the Ability
     *
     * @param debuff the debuff to add
     * @param chance the chance that it is applied
     */
    public void addDebuff(Debuff debuff, int chance)
    {
        debuffs.add(debuff);
        debuffsChance.add(chance);
    }
    
    /**
     * @return the debuff chances for this Ability
     */
    public ArrayList<Integer> getDebuffsChance()
    {
        return debuffsChance;
    }
    
    /**
     * @return whether the Ability is passive
     */
    public boolean isPassive()
    {
        return passive;
    }
    
    /**
     * Returns how many turns are left before the Ability can be used again
     *
     * @return how many turns are left
     */
    public int getTurnsRemaining()
    {
        return turnsRemaining;
    }
    
    /**
     * @return whether the Ability targets the self
     */
    public boolean targetsSelf()
    {
        return targetsSelf;
    }
    
    /**
     * Decreases the cooldown by 1 if {@link Ability#getTurnsRemaining()} is greater than 0
     */
    public void decCooldown()
    {
        turnsRemaining = Math.max(0, turnsRemaining - 1);
    }
    
    /**
     * Sets the cooldown to the Ability's max
     */
    public void setToMaxCooldown()
    {
        turnsRemaining = maxCooldown;
    }
    
    /**
     * Sets the cooldown to a given number
     *
     * @param num the number to set the cooldown to
     */
    public void setToNumTurns(int num)
    {
        turnsRemaining = num;
    }
    
    public String toString()
    {
        String cooldown = (this.turnsRemaining != 0) ?
                ConsoleColors.BLUE + "Reusable in " + this.turnsRemaining + " turns " + ConsoleColors.BLACK + ConsoleColors.WHITE_BACKGROUND : "";
        String reusableIn = (this.turnsRemaining == 0 && maxCooldown != 0) ? ConsoleColors.BLUE + " (Cooldown: " + (maxCooldown - 1) + " turns)" : "";
        
        return (!passive) ? name + ": " + cooldown + description + reusableIn + ConsoleColors.RESET :
                name + " (Passive): " + cooldown + description + reusableIn + ConsoleColors.RESET;
    }
    
    /**
     * Formats the colors for {@link Ability#toString()}
     *
     * @param hasSilence  whether the Monster has the silence debuff
     * @param hasOblivion whether the Monster has the Oblivion debuff
     * @return the formatted String
     */
    public String toString(boolean hasSilence, boolean hasOblivion)
    {
        String s = "";
        if (hasSilence)
        {
            if (maxCooldown != 0)
            {
                String cooldown = (this.turnsRemaining != 0) ? "Reusable in " + this.turnsRemaining + " turns, " : "";
                String reusableIn = (this.turnsRemaining == 0) ? ConsoleColors.BLUE + " (Cooldown: " + maxCooldown + " turns)" : "";
                s += (!passive) ?
                        ConsoleColors.BLACK + ConsoleColors.WHITE_BACKGROUND + name + ": " + cooldown + description + reusableIn + ConsoleColors.RESET :
                        ConsoleColors.BLACK + ConsoleColors.WHITE_BACKGROUND + name + " (Passive): " + cooldown + description + reusableIn + ConsoleColors.RESET;
            }
        }
        if (hasOblivion)
        {
            if (passive)
            {
                String cooldown = (this.turnsRemaining != 0) ? "Reusable in " + this.turnsRemaining + " turns, " : "";
                String reusableIn = (this.turnsRemaining == 0 && this.maxCooldown != 0) ? ConsoleColors.BLUE + " (Cooldown: " + maxCooldown + " turns)"
                        : "";
                s += ConsoleColors.BLACK + ConsoleColors.WHITE_BACKGROUND + name + " (Passive): " + cooldown + description + reusableIn + ConsoleColors.RESET;
            }
        }
        return (s.isEmpty()) ? toString() : s;
    }
    
    /**
     * Returns whether the Ability can be used this turn
     *
     * @param hasSilence whether the Monster has the silence debuff
     * @return whether the Ability can be used
     */
    public boolean isViableAbility(boolean hasSilence)
    {
        if (hasSilence)
        {
            return maxCooldown == 0;
        }
        return !passive && (turnsRemaining == 0);
    }
    
    /**
     * @return the number of activations for the Ability
     */
    public int getNumOfActivations()
    {
        return numOfActivations;
    }
    
    /**
     * Sets the damage multiplier
     *
     * @param num the double to set the multiplier to
     */
    public void setDmgMultiplier(double num)
    {
        dmg = Math.max(0, num);
    }
    
    /**
     * Compares this Ability to another.
     *
     * @param ability the Ability to compare
     * @return true if the given Ability's name equals this Ability's name
     */
    public boolean equals(Ability ability)
    {
        return ability.name.equals(name);
    }
    
    /**
     * @return whether the Ability ignores damage reduction effects
     */
    public boolean ignoresDmgReduction()
    {
        return ignoreDmgReduction;
    }
}
