package Abilities;

import Errors.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * The subclass for all offensive abilities. This class defaults Ability booleans to (targetsEnemy = true; isPassive = false; targetsSelf = false)
 *
 * @author Anthony (Tony) Youssef
 */
public class Attack_Ability extends Ability
{
    /**
     * Constructs a new Attack Ability.
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
     * @param ignoresDefense      whether this ability ignores the targets defense
     * @param ignoresDmgReduction whether this ability ignores damage reduction effects
     * @throws BadArgumentLength if debuffs and debuffChances or buffs and buffChances are different lengths
     */
    public Attack_Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Debuff> debuffs,
            ArrayList<Integer> debuffChances, ArrayList<Buff> buffs, ArrayList<Integer> buffChances, int cooldown, boolean ignoresDefense,
            boolean ignoresDmgReduction)
    {
        super(name, multiplier, healingPercent, numOfActivations, description, debuffs, debuffChances, buffs, buffChances, cooldown, true, false,
                ignoresDefense, false, ignoresDmgReduction);
    }
    
    /**
     * Constructs a new Attack Ability with no buffs
     *
     * @param name                the name of the monster
     * @param multiplier          the attack multiplier (ex. 2.5)
     * @param healingPercent      the percent of the max hp to heal (ex. 0.25)
     * @param numOfActivations    the amount of times the ability activates when called
     * @param description         the description to print
     * @param debuffs             the debuffs to apply to the target when called
     * @param debuffChances       the chance that each debuff will apply
     * @param cooldown            the cooldown of the ability
     * @param ignoresDefense      whether this ability ignores the targets defense
     * @param ignoresDmgReduction whether this ability ignores damage reduction effects
     * @throws BadArgumentLength if debuffs and debuffChances are different lengths
     */
    public Attack_Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Debuff> debuffs,
            ArrayList<Integer> debuffChances, int cooldown, boolean ignoresDefense, boolean ignoresDmgReduction)
    {
        super(name, multiplier, healingPercent, numOfActivations, description, new ArrayList<>(), new ArrayList<>(), debuffs, debuffChances, cooldown,
                true, false, ignoresDefense, false, ignoresDmgReduction, 0);
    }
    
    /**
     * Constructs a new Attack Ability with no debuffs
     *
     * @param name                the name of the monster
     * @param multiplier          the attack multiplier (ex. 2.5)
     * @param healingPercent      the percent of the max hp to heal (ex. 0.25)
     * @param numOfActivations    the amount of times the ability activates when called
     * @param description         the description to print
     * @param buffs               the buffs to apply to self when called
     * @param buffChances         the chance that each buff will apply
     * @param cooldown            the cooldown of the ability
     * @param ignoresDefense      whether this ability ignores the targets defense
     * @param ignoresDmgReduction whether this ability ignores damage reduction effects
     * @param ignore              an int to distinguish this constructor from another
     * @throws BadArgumentLength if buffs and buffChances are different lengths
     */
    public Attack_Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Buff> buffs,
            ArrayList<Integer> buffChances, int cooldown, boolean ignoresDefense, boolean ignoresDmgReduction, int ignore)
    {
        super(name, multiplier, healingPercent, numOfActivations, description, new ArrayList<>(), new ArrayList<>(), buffs, buffChances, cooldown, true,
                false, ignoresDefense, false, ignoresDmgReduction);
    }
    
    /**
     * Constructs a new Attack Ability
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
     * @param ignoresDefense      whether this ability ignores the targets defense
     * @param ignoresDmgReduction whether this ability ignores damage reduction effects
     * @param ignore              an int to distinguish this constructor from another
     * @throws BadArgumentLength if debuffs and debuffChances or buffs and buffChances are different lengths
     */
    //Reverse buffs and debuffs in header
    public Attack_Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Buff> buffs,
            ArrayList<Integer> buffChances, ArrayList<Debuff> debuffs, ArrayList<Integer> debuffChances, int cooldown, boolean ignoresDefense,
            boolean ignoresDmgReduction, int ignore)
    {
        super(name, multiplier, healingPercent, numOfActivations, description, debuffs, debuffChances, buffs, buffChances, cooldown, true, false,
                ignoresDefense, false, ignoresDmgReduction);
    }
    
    /**
     * Constructs a new Attack Ability with no buffs or debuffs
     *
     * @param name                the name of the monster
     * @param multiplier          the attack multiplier (ex. 2.5)
     * @param healingPercent      the percent of the max hp to heal (ex. 0.25)
     * @param numOfActivations    the amount of times the ability activates when called
     * @param description         the description to print
     * @param cooldown            the cooldown of the ability
     * @param ignoresDefense      whether this ability ignores the targets defense
     * @param ignoresDmgReduction whether this ability ignores damage reduction effects
     */
    //No buffs or debuffs
    public Attack_Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, int cooldown,
            boolean ignoresDefense, boolean ignoresDmgReduction)
    {
        super(name, multiplier, healingPercent, numOfActivations, description, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), cooldown, true, false, ignoresDefense, false, ignoresDmgReduction);
    }
}
