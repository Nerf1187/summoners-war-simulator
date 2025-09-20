package Abilities;

import Errors.*;
import Game.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Effects.*;
import java.util.*;

/**
 * The parent class for all abilities. Should only be constructed in subclasses or when the ability only targets the monster who has it (ex. Xiao Ling's
 * second skill) so the Game.Auto_Play algorithm functions properly.
 *
 * @author Anthony (Tony) Youssef
 */
public class Ability
{
    private double atkMultiplier, healingPercent;
    private final int maxCooldown;
    private int numOfActivations;
    private int turnsRemaining = 0, numOfBeneficialEffectsToRemoveOverride = -1;
    protected String description;
    protected final String name;
    private final ArrayList<Debuff> debuffs;
    private final ArrayList<Buff> buffs;
    private final ArrayList<Integer> debuffsChance, buffsChance;
    private final boolean targetEnemy, passive, ignoreDef, ignoreDmgReduction, targetsSelf, targetsAllTeam;
    
    //base constructor
    
    /**
     * Constructs a new Ability
     *
     * @param name                The name of the monster
     * @param multiplier          The attack multiplier (ex. 2.5 for 250%)
     * @param healingPercent      The percentage of the max hp to heal (0-100)
     * @param numOfActivations    The amount of times the ability activates when called
     * @param description         The description to print
     * @param debuffs             The debuffs to apply to the target when called
     * @param debuffChances       The chance that each debuff will apply
     * @param buffs               The buffs to apply to self when called
     * @param buffChances         The chance that each buff will apply
     * @param cooldown            The cooldown of the ability
     * @param targetsEnemy        Whether the ability targets the enemy
     * @param isPassive           Whether this ability is a passive
     * @param ignoresDefense      Whether this ability ignores the target's defense
     * @param targetsSelf         Whether this ability targets self
     * @param ignoresDmgReduction Whether this ability ignores damage reduction effects
     * @param targetsAllTeam      True if the ability targets the entire team, false otherwise
     * @throws ConflictingArguments If targetsEnemy and targetsSelf are both true
     * @throws InvalidArgumentLength    If debuffs and debuffChances or buffs and buffChances are different lengths
     */
    public Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Debuff> debuffs,
            ArrayList<Integer> debuffChances, ArrayList<Buff> buffs, ArrayList<Integer> buffChances, int cooldown, boolean targetsEnemy,
            boolean isPassive, boolean ignoresDefense, boolean targetsSelf, boolean ignoresDmgReduction, boolean targetsAllTeam)
    {
        //Check arguments are valid
        if (targetsEnemy && targetsSelf)
        {
            throw new ConflictingArguments(name + " can not target enemy and self");
        }
        if (debuffs.size() != debuffChances.size())
        {
            throw new InvalidArgumentLength("Debuff size and debuffs chance size not the same");
        }
        if (buffs.size() != buffChances.size())
        {
            throw new InvalidArgumentLength("Buff size and buffs chance size not the same");
        }
        
        //Set values
        this.atkMultiplier = multiplier;
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
        this.targetsAllTeam = targetsAllTeam;
        //Set description
        descriptionWithLineBreaks();
    }
    
    /**
     * Constructs a new Ability with no debuffs
     *
     * @param name                The name of the monster
     * @param multiplier          The attack multiplier (ex. 2.5 for 250%)
     * @param healingPercent      The percentage of the max hp to heal (0-100)
     * @param numOfActivations    The amount of times the ability activates when called
     * @param description         The description to print
     * @param buffs               The buffs to apply to self when called
     * @param buffChances         The chance that each buff will apply
     * @param cooldown            The cooldown of the ability
     * @param targetsEnemy        Whether the ability targets the enemy
     * @param isPassive           Whether this ability is a passive
     * @param targetsAllTeam      True if the ability targets the entire team, false otherwise
     * @param ignoresDefense      Whether this ability ignores the target's defense
     * @param targetsSelf         Whether this ability targets self
     * @param ignoresDmgReduction Whether this ability ignores damage reduction effects
     * @param ignore              An int to distinguish this constructor from another
     * @throws ConflictingArguments If targetsEnemy and targetsSelf are true
     * @throws InvalidArgumentLength    If buffs and buffChances are different lengths
     */
    public Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Buff> buffs,
            ArrayList<Integer> buffChances, int cooldown, boolean targetsEnemy, boolean isPassive, boolean ignoresDefense, boolean targetsSelf,
            boolean ignoresDmgReduction, boolean targetsAllTeam, int ignore)
    {
        this(name, multiplier, healingPercent, numOfActivations, description, new ArrayList<>(), new ArrayList<>(), buffs, buffChances, cooldown,
                targetsEnemy, isPassive, ignoresDefense, targetsSelf, ignoresDmgReduction, targetsAllTeam);
    }
    
    /**
     * Constructs a new Ability with no buffs
     *
     * @param name                The name of the monster
     * @param multiplier          The attack multiplier (ex. 2.5 for 250%)
     * @param healingPercent      The percentage of the max hp to heal (0-100)
     * @param numOfActivations    The amount of times the ability activates when called
     * @param description         The description to print
     * @param debuffs             The debuffs to apply to the target when called
     * @param debuffChances       The chance that each debuff will apply
     * @param cooldown            The cooldown of the ability
     * @param targetsEnemy        Whether the ability targets the enemy
     * @param isPassive           Whether this ability is a passive
     * @param ignoresDefense      Whether this ability ignores the target's defense
     * @param targetsSelf         Whether this ability targets self
     * @param ignoresDmgReduction Whether this ability ignores damage reduction effects
     * @param targetsAllTeam      True if the ability targets the entire team, false otherwise
     * @throws ConflictingArguments If targetsEnemy and targetsSelf are true
     * @throws InvalidArgumentLength    If debuffs and debuffChances are different lengths
     */
    public Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Debuff> debuffs,
            ArrayList<Integer> debuffChances, int cooldown, boolean targetsEnemy, boolean isPassive, boolean ignoresDefense, boolean targetsSelf,
            boolean ignoresDmgReduction, boolean targetsAllTeam)
    {
        this(name, multiplier, healingPercent, numOfActivations, description, debuffs, debuffChances, new ArrayList<>(), new ArrayList<>(), cooldown,
                targetsEnemy, isPassive, ignoresDefense, targetsSelf, ignoresDmgReduction, targetsAllTeam);
    }
    
    /**
     * Constructs a new Ability
     *
     * @param name                The name of the monster
     * @param multiplier          The attack multiplier (ex. 2.5 for 250%)
     * @param healingPercent      The percentage of the max hp to heal (0-100)
     * @param numOfActivations    The amount of times the ability activates when called
     * @param description         The description to print
     * @param buffs               The buffs to apply to self when called
     * @param buffChances         The chance that each buff will apply
     * @param debuffs             The debuffs to apply to the target when called
     * @param debuffChances       The chance that each debuff will apply
     * @param cooldown            The cooldown of the ability
     * @param targetsEnemy        Whether the ability targets the enemy
     * @param isPassive           Whether this ability is a passive
     * @param targetsAllTeam      True if the ability targets the entire team, false otherwise
     * @param ignoresDefense      Whether this ability ignores the target's defense
     * @param targetsSelf         Whether this ability targets self
     * @param ignoresDmgReduction Whether this ability ignores damage reduction effects
     * @param ignore              An int to distinguish this constructor from another
     * @throws ConflictingArguments If targetsEnemy and targetsSelf are true
     * @throws InvalidArgumentLength    if debuffs and debuffChances or buffs and buffChances are different lengths
     */
    public Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Buff> buffs,
            ArrayList<Integer> buffChances, ArrayList<Debuff> debuffs, ArrayList<Integer> debuffChances, int cooldown, boolean targetsEnemy,
            boolean isPassive, boolean ignoresDefense, boolean targetsSelf, boolean ignoresDmgReduction, boolean targetsAllTeam, int ignore)
    {
        this(name, multiplier, healingPercent, numOfActivations, description, debuffs, debuffChances, buffs, buffChances, cooldown, targetsEnemy,
                isPassive, ignoresDefense, targetsSelf, ignoresDmgReduction, targetsAllTeam);
    }
    
    /**
     * Constructs a new Ability with no buffs or debuffs
     *
     * @param name                The name of the monster
     * @param multiplier          The attack multiplier (ex. 2.5 for 250%)
     * @param healingPercent      The percentage of the max hp to heal (0-100)
     * @param numOfActivations    The amount of times the ability activates when called
     * @param description         The description to print
     * @param cooldown            The cooldown of the ability
     * @param targetsEnemy        Whether the ability targets the enemy
     * @param isPassive           Whether this ability is a passive
     * @param ignoresDefense      Whether this ability ignores the target's defense
     * @param targetsSelf         Whether this ability targets self
     * @param ignoresDmgReduction Whether this ability ignores damage reduction effects
     * @param targetsAllTeam      True if the ability targets the entire team, false otherwise
     * @throws ConflictingArguments If targetsEnemy and targetsSelf are true
     */
    public Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, int cooldown, boolean targetsEnemy,
            boolean isPassive, boolean ignoresDefense, boolean targetsSelf, boolean ignoresDmgReduction, boolean targetsAllTeam)
    {
        this(name, multiplier, healingPercent, numOfActivations, description, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
                , cooldown, targetsEnemy, isPassive, ignoresDefense, targetsSelf, ignoresDmgReduction, targetsAllTeam);
    }
    
    /**
     * Constructs an empty Ability used specifically for the {@link Leader_Skill} class
     */
    protected Ability()
    {
        this("", 0, 0, 0, "", 0, false, false, false, false, false, false);
    }
    
    /**
     * Formats the Ability description with line breaks
     */
    private void descriptionWithLineBreaks()
    {
        int offset = 0;
        StringBuilder sb = new StringBuilder(description);
        boolean breakLine = false;
        //Create line breaks at the closest space character after 80 characters.
        for (int i = 80; i < description.length(); i++)
        {
            //Flag new line break after 80 characters
            if (i % 80 == 0)
            {
                breakLine = true;
            }
            //Line break at next space if flagged
            if (breakLine && description.charAt(i) == ' ')
            {
                sb.insert(i + offset, "\n\t\t\t");
                offset += "\n\t\t\t".length();
                i += 80;
                breakLine = false;
            }
        }
        description = sb.toString();
    }
    
    /**
     * Gets the damage multiplier
     *
     * @return The damage multiplier
     */
    public double getDmgMultiplier()
    {
        return atkMultiplier;
    }
    
    /**
     * Gets the healing multiplier
     *
     * @return The healing multiplier
     */
    public double getHealingPercent()
    {
        return healingPercent;
    }
    
    /**
     * Sets the healing multiplier
     *
     * @param healingPercent The new healing multiplier
     */
    public void setHealingPercent(double healingPercent)
    {
        this.healingPercent = healingPercent;
    }
    
    /**
     * Sets the description
     * @param description The new description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    /**
     * @return True if the ability ignores the target's defense, false otherwise
     */
    public boolean ignoresDefense()
    {
        return ignoreDef;
    }
    
    /**
     * @return True if the ability targets the enemy, false otherwise
     */
    public boolean targetsEnemy()
    {
        return targetEnemy;
    }
    
    /**
     * Gets the buffs for this Ability
     *
     * @return The buffs for this Ability
     */
    public ArrayList<Buff> getBuffs()
    {
        return buffs;
    }
    
    /**
     * Gets the buff chances for this Ability
     *
     * @return The buff chances for this Ability
     */
    public ArrayList<Integer> getBuffsChance()
    {
        return buffsChance;
    }
    
    /**
     * Gets the debuffs for this Ability
     *
     * @return The debuffs for this Ability
     */
    public ArrayList<Debuff> getDebuffs()
    {
        return debuffs;
    }
    
    /**
     * Gets the debuff chances for this Ability
     *
     * @return The debuff chances for this Ability
     */
    public ArrayList<Integer> getDebuffsChance()
    {
        return debuffsChance;
    }
    
    /**
     * @return True if the Ability is passive, false otherwise
     */
    public boolean isPassive()
    {
        return passive;
    }
    
    /**
     * Returns how many turns are left before the ability can be used again
     *
     * @return How many turns before the ability can be used again
     */
    public int getTurnsRemaining()
    {
        return turnsRemaining;
    }
    
    /**
     * @return True if the Ability targets the self, false otherwise
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
     * Gets the ability's max cooldown
     *
     * @return The ability's max cooldown
     */
    public int getMaxCooldown()
    {
        return maxCooldown;
    }
    
    /**
     * Sets the cooldown to a given number
     *
     * @param num The number to set the cooldown to
     */
    public void setToNumTurns(int num)
    {
        turnsRemaining = num;
    }
    
    /**
     * Formats the ability into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        String cooldown = (this.turnsRemaining != 0) ?
                ConsoleColor.BLUE + "Reusable in " + this.turnsRemaining + " turns " + ConsoleColor.BLACK + ConsoleColor.WHITE_BACKGROUND : "";
        String reusableIn = (this.turnsRemaining == 0 && maxCooldown != 0) ? ConsoleColor.BLUE + " (Cooldown: " + (maxCooldown - 1) + " turns)" : "";
        
        return (!passive) ? name + ": " + cooldown + description + reusableIn + ConsoleColor.RESET :
                name + " (Passive): " + cooldown + description + reusableIn + ConsoleColor.RESET;
    }
    
    /**
     * Formats the colors for {@link Ability#toString()}
     *
     * @param hasSilence  Whether the Monster has the Silence debuff
     * @param hasOblivion Whether the Monster has the Oblivion debuff
     *
     * @return The formatted String
     */
    public String toString(boolean hasSilence, boolean hasOblivion)
    {
        String s = "";
        if (hasSilence)
        {
            if (maxCooldown != 0)
            {
                //Gray out ability if it has a cooldown and the Monster is silenced
                String cooldown = (this.turnsRemaining != 0) ? "Reusable in " + this.turnsRemaining + " turns, " : "";
                String reusableIn = (this.turnsRemaining == 0) ? ConsoleColor.BLUE + " (Cooldown: " + maxCooldown + " turns)" : "";
                s += (!passive) ?
                        "" + ConsoleColor.BLACK + ConsoleColor.WHITE_BACKGROUND + name + ": " + cooldown + description + reusableIn + ConsoleColor.RESET :
                        "" + ConsoleColor.BLACK + ConsoleColor.WHITE_BACKGROUND + name + " (Passive): " + cooldown + description + reusableIn + ConsoleColor.RESET;
            }
        }
        if (hasOblivion)
        {
            if (passive)
            {
                //Gray out ability if it is a passive and the Monster has Oblivion
                String cooldown = (this.turnsRemaining != 0) ? "Reusable in " + this.turnsRemaining + " turns, " : "";
                String reusableIn = (this.turnsRemaining == 0 && this.maxCooldown != 0) ? ConsoleColor.BLUE + " (Cooldown: " + maxCooldown + " turns)"
                        : "";
                s += "" + ConsoleColor.BLACK + ConsoleColor.WHITE_BACKGROUND + name + " (Passive): " + cooldown + description + reusableIn + ConsoleColor.RESET;
            }
        }
        return (s.isEmpty()) ? this.toString() : s;
    }
    
    /**
     * Checks if the Ability can be used this turn
     *
     * @param hasSilence Whether the Monster has the silence debuff
     *
     * @return Whether the Ability can be used
     */
    public boolean isViableAbility(boolean hasSilence)
    {
        if (hasSilence)
        {
            return maxCooldown == 0;
        }
        return !passive && !(this instanceof Leader_Skill) && (turnsRemaining == 0);
    }
    
    /**
     * Gets the number of activations for the Ability
     *
     * @return The number of activations for the Ability
     */
    public int getNumOfActivations()
    {
        return numOfActivations;
    }
    
    /**
     * Sets the number of activations to the specified value.
     *
     * @param num the number of activations to be set
     */
    public void setNumOfActivations(int num)
    {
        numOfActivations = num;
    }
    
    /**
     * Sets the damage multiplier
     *
     * @param num The new damage multiplier
     */
    public void setDmgMultiplier(double num)
    {
        atkMultiplier = Math.max(0, num);
    }
    
    /**
     * Compares this Ability to another.
     *
     * @param ability The Ability to compare
     *
     * @return True if the two abilities' names are the same, false otherwise
     */
    public boolean equals(Ability ability)
    {
        return ability.name.equals(name);
    }
    
    /**
     * @return True if the Ability ignores damage reduction effects, false otherwise
     */
    public boolean ignoresDmgReduction()
    {
        return ignoreDmgReduction;
    }
    
    /**
     * Checks if the ability targets the entire team
     *
     * @return True if the ability targets the entire team, false otherwise
     */
    public boolean targetsAllTeam()
    {
        return targetsAllTeam;
    }
    
    /**
     * Checks if the ability can apply the given buff
     *
     * @param buffNum The number of the buff to check for
     *
     * @return True if the ability can apply the buff, false otherwise
     */
    public boolean canApplyBuff(BuffEffect buffNum)
    {
        return this.canApplyStat(new Buff(buffNum));
    }
    
    /**
     * Checks if the ability can apply the given debuff
     *
     * @param debuffNum The number of the debuff to check for
     *
     * @return True if the ability can apply the debuff, false otherwise
     */
    public boolean canApplyDebuff(DebuffEffect debuffNum)
    {
        return this.canApplyStat(new Debuff(debuffNum));
    }
    
    /**
     * Checks if the ability can apply the given buff or debuff
     *
     * @param s The buff or debuff to check for
     *
     * @return True if the ability can apply the buff or debuff, false otherwise
     */
    public boolean canApplyStat(Effect s)
    {
        return switch (s)
        {
            case Buff b ->
            {
                //Search the buffs array for the buff
                for (Buff potentialBuff : buffs)
                {
                    if (potentialBuff.equals(b))
                    {
                        yield true;
                    }
                }
                yield false;
            }
            case Debuff d ->
            {
                //Search the debuffs array for the debuff
                for (Debuff potentialDebuff : debuffs)
                {
                    if (potentialDebuff.equals(d))
                    {
                        yield true;
                    }
                }
                yield false;
            }
            default -> false;
        };
    }
    
    /**
     * Counts approximately how many buffs the ability can remove with one call
     *
     * @return A number representing an approximation of how many debuffs the ability can remove (0-11)
     */
    public int getNumOfBeneficialEffectsToRemove()
    {
        if (this.numOfBeneficialEffectsToRemoveOverride != -1)
        {
            return this.numOfBeneficialEffectsToRemoveOverride;
        }
        
        int sum = 0;
        if (canApplyDebuff(DebuffEffect.STRIP))
        {
            sum += 5;
        }
        if (canApplyBuff(BuffEffect.BUFF_STEAL))
        {
            sum += 3;
        }
        if (canApplyDebuff(DebuffEffect.REMOVE_BENEFICIAL_EFFECT))
        {
            sum += 2;
        }
        if (canApplyDebuff(DebuffEffect.SHORTEN_BUFFS))
        {
            sum += 1;
        }
        return sum * this.numOfActivations;
    }
    
    /**
     * Set an override value for {@link Ability#getNumOfBeneficialEffectsToRemove()}. If the override is set, the linked method will return the override, otherwise it will continue calculating as normal.
     * Use {@link Ability#resetBeneficialEffectRemoversOverride()} to reset and remove the override
     * @param debuffNums The debuff numbers to use to calculate the override
     */
    public void addBeneficialEffectRemoversOverride(DebuffEffect... debuffNums)
    {
        int sum = 0;
        for (DebuffEffect debuffNum : debuffNums)
        {
            sum += switch (debuffNum)
            {
                case DebuffEffect.STRIP -> 5;
                case DebuffEffect.REMOVE_BENEFICIAL_EFFECT -> 2;
                case DebuffEffect.SHORTEN_BUFFS -> 1;
                default -> 0;
            };
        }
        this.numOfBeneficialEffectsToRemoveOverride = sum * this.numOfActivations;
    }
    
    /**
     * Updates the override value for the number of beneficial effects to remove by summing
     * up the contributions of specified buff effects and multiplying by the number of activations.
     *
     * @param buffNums An array of {@link BuffEffect} representing the types of buff effects
     *                 to consider when calculating the override value.
     */
    public void addBeneficialEffectRemoversOverride(BuffEffect... buffNums)
    {
        int sum = 0;
        for (BuffEffect buffEffect : buffNums)
        {
            sum += switch (buffEffect)
            {
                case BuffEffect.BUFF_STEAL -> 3;
                default -> 0;
            };
        }
        this.numOfBeneficialEffectsToRemoveOverride = sum * this.numOfActivations;
    }
    
    /**
     * Resets the override value
     */
    public void resetBeneficialEffectRemoversOverride()
    {
        this.numOfBeneficialEffectsToRemoveOverride = -1;
    }
}
