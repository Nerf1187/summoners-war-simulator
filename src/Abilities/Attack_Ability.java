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
     * @param ignoresDefense      Whether this ability ignores the target's defense
     * @param ignoresDmgReduction Whether this ability ignores damage reduction effects
     * @param hitsTeam            Whether this ability targets everyone on the enemy team
     * @throws BadArgumentLength If debuffs and debuffChances or buffs and buffChances are different lengths
     */
    public Attack_Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Debuff> debuffs,
            ArrayList<Integer> debuffChances, ArrayList<Buff> buffs, ArrayList<Integer> buffChances, int cooldown, boolean ignoresDefense,
            boolean ignoresDmgReduction, boolean hitsTeam)
    {
        super(name, multiplier, healingPercent, numOfActivations, description, debuffs, debuffChances, buffs, buffChances, cooldown, true, false,
                ignoresDefense, false, ignoresDmgReduction, hitsTeam);
    }
    
    /**
     * Constructs a new Attack Ability with no buffs
     *
     * @param name                The name of the monster
     * @param multiplier          The attack multiplier (ex. 2.5 for 250%)
     * @param healingPercent      The percentage of the max hp to heal (0-100)
     * @param numOfActivations    The amount of times the ability activates when called
     * @param description         The description to print
     * @param debuffs             The debuffs to apply to the target when called
     * @param debuffChances       The chance that each debuff will apply
     * @param cooldown            The cooldown of the ability
     * @param ignoresDefense      Whether this ability ignores the target's defense
     * @param ignoresDmgReduction Whether this ability ignores damage reduction effects
     * @param hitsTeam            Whether this ability targets everyone on the enemy team
     * @throws BadArgumentLength If debuffs and debuffChances are different lengths
     */
    public Attack_Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Debuff> debuffs,
            ArrayList<Integer> debuffChances, int cooldown, boolean ignoresDefense, boolean ignoresDmgReduction, boolean hitsTeam)
    {
        super(name, multiplier, healingPercent, numOfActivations, description, new ArrayList<>(), new ArrayList<>(), debuffs, debuffChances, cooldown,
                true, false, ignoresDefense, false, ignoresDmgReduction, hitsTeam, 0);
    }
    
    /**
     * Constructs a new Attack Ability with no debuffs
     *
     * @param name                The name of the monster
     * @param multiplier          The attack multiplier (ex. 2.5 for 250%)
     * @param healingPercent      The percentage of the max hp to heal (0-100)
     * @param numOfActivations    The amount of times the ability activates when called
     * @param description         The description to print
     * @param buffs               The buffs to apply to self when called
     * @param buffChances         The chance that each buff will apply
     * @param cooldown            The cooldown of the ability
     * @param ignoresDefense      Whether this ability ignores the target's defense
     * @param ignoresDmgReduction Whether this ability ignores damage reduction effects
     * @param hitsTeam            Whether this ability targets everyone on the enemy team
     * @param ignore              An int to distinguish this constructor from another
     * @throws BadArgumentLength If buffs and buffChances are different lengths
     */
    public Attack_Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Buff> buffs,
            ArrayList<Integer> buffChances, int cooldown, boolean ignoresDefense, boolean ignoresDmgReduction, boolean hitsTeam, int ignore)
    {
        super(name, multiplier, healingPercent, numOfActivations, description, new ArrayList<>(), new ArrayList<>(), buffs, buffChances, cooldown, true,
                false, ignoresDefense, false, ignoresDmgReduction, hitsTeam);
    }
    
    /**
     * Constructs a new Attack Ability
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
     * @param ignoresDefense      Whether this ability ignores the target's defense
     * @param ignoresDmgReduction Whether this ability ignores damage reduction effects
     * @param hitsTeam            Whether this ability targets everyone on the enemy team
     * @param ignore              An int to distinguish this constructor from another
     * @throws BadArgumentLength If debuffs and debuffChances or buffs and buffChances are different lengths
     */
    public Attack_Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, ArrayList<Buff> buffs,
            ArrayList<Integer> buffChances, ArrayList<Debuff> debuffs, ArrayList<Integer> debuffChances, int cooldown, boolean ignoresDefense,
            boolean ignoresDmgReduction, boolean hitsTeam, int ignore)
    {
        super(name, multiplier, healingPercent, numOfActivations, description, debuffs, debuffChances, buffs, buffChances, cooldown, true, false,
                ignoresDefense, false, ignoresDmgReduction, hitsTeam);
    }
    
    /**
     * Constructs a new Attack Ability with no buffs or debuffs
     *
     * @param name                The name of the monster
     * @param multiplier          The attack multiplier (ex. 2.5 for 250%)
     * @param healingPercent      The percentage of the max hp to heal (0-100)
     * @param numOfActivations    The amount of times the ability activates when called
     * @param description         The description to print
     * @param cooldown            The cooldown of the ability
     * @param ignoresDefense      Whether this ability ignores the target's defense
     * @param ignoresDmgReduction Whether this ability ignores damage reduction effects
     * @param hitsTeam            Whether this ability targets everyone on the enemy team
     */
    public Attack_Ability(String name, double multiplier, double healingPercent, int numOfActivations, String description, int cooldown,
            boolean ignoresDefense, boolean ignoresDmgReduction, boolean hitsTeam)
    {
        super(name, multiplier, healingPercent, numOfActivations, description, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), cooldown, true, false, ignoresDefense, false, ignoresDmgReduction, hitsTeam);
    }
}
