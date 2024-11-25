package Abilities;

import Errors.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * The subclass for all defensive abilities. Defaults Ability values to (multiplier = 0; (all booleans) = false
 */
public class Heal_Ability extends Ability
{
    /**
     * Constructs a new Heal ability
     *
     * @param name                    The name of the monster
     * @param healingPercent          The percentage of the max hp to heal (0-100)
     * @param numOfActivations        The amount of times the ability activates when called
     * @param description             The description to print
     * @param debuffs                 The debuffs to apply to the target when called
     * @param debuffChances           The chance that each debuff will apply
     * @param buffs                   The buffs to apply to self when called
     * @param buffChances             The chance that each buff will apply
     * @param cooldown                The cooldown of the ability
     * @param targetsMultipleMonsters Whether the Ability targets multiple Monsters
     * @throws BadArgumentLength If debuffs and debuffChances or buffs and buffChances are different lengths
     */
    public Heal_Ability(String name, double healingPercent, int numOfActivations, String description, ArrayList<Buff> buffs,
            ArrayList<Integer> buffChances, ArrayList<Debuff> debuffs, ArrayList<Integer> debuffChances, int cooldown, boolean targetsMultipleMonsters)
    {
        super(name, 0, healingPercent, numOfActivations, description, buffs, buffChances, debuffs, debuffChances, cooldown, false, false, false, false,
                false, targetsMultipleMonsters, 0);
    }
    
    /**
     * Constructs a new Heal Ability with no debuffs
     *
     * @param name                    The name of the monster
     * @param healingPercent          The percentage of the max hp to heal (0-100)
     * @param numOfActivations        The amount of times the ability activates when called
     * @param description             The description to print
     * @param buffs                   The buffs to apply to self when called
     * @param buffsChance             The chances that each buff will apply
     * @param cooldown                The cooldown of the ability
     * @param targetsMultipleMonsters Whether the Ability targets multiple Monsters
     * @throws BadArgumentLength If buffs and buffChances are different lengths
     */
    public Heal_Ability(String name, double healingPercent, int numOfActivations, String description, ArrayList<Buff> buffs,
            ArrayList<Integer> buffsChance, int cooldown, boolean targetsMultipleMonsters)
    {
        super(name, 0, healingPercent, numOfActivations, description, buffs, buffsChance, cooldown, false, false, false, false, false, targetsMultipleMonsters, 0);
    }
    
    /**
     * Constructs a new Heal Ability with no buffs or debuffs
     *
     * @param name                    The name of the monster
     * @param healingPercent          The percentage of the max hp to heal (0-100)
     * @param numOfActivations        The amount of times the ability activates when called
     * @param description             The description to print
     * @param cooldown                The cooldown of the ability
     * @param targetsMultipleMonsters Whether the Ability targets multiple Monsters
     */
    public Heal_Ability(String name, double healingPercent, int numOfActivations, String description, int cooldown, boolean targetsMultipleMonsters)
    {
        super(name, 0, healingPercent, numOfActivations, description, cooldown, false, false, false, false, false, targetsMultipleMonsters);
    }
}