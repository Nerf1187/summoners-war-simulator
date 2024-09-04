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
     * @param name                    the name of the monster
     * @param healingPercent          the percent of the max hp to heal (ex. 0.25)
     * @param numOfActivations        the amount of times the ability activates when called
     * @param description             the description to print
     * @param debuffs                 the debuffs to apply to the target when called
     * @param debuffChances           the chance that each debuff will apply
     * @param buffs                   the buffs to apply to self when called
     * @param buffChances             the chance that each buff will apply
     * @param cooldown                the cooldown of the ability
     * @param targetsMultipleMonsters whether the Ability targets multiple Monsters
     * @throws BadArgumentLength if debuffs and debuffChances or buffs and buffChances are different lengths
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
     * @param name                    the name of the monster
     * @param healingPercent          the percent of the max hp to heal (ex. 0.25)
     * @param numOfActivations        the amount of times the ability activates when called
     * @param description             the description to print
     * @param buffs                   the buffs to apply to self when called
     * @param buffsChance             the chances that each buff will apply
     * @param cooldown                the cooldown of the ability
     * @param targetsMultipleMonsters whether the Ability targets multiple Monsters
     * @throws BadArgumentLength if buffs and buffChances are different lengths
     */
    //No debuffs
    public Heal_Ability(String name, double healingPercent, int numOfActivations, String description, ArrayList<Buff> buffs,
            ArrayList<Integer> buffsChance, int cooldown, boolean targetsMultipleMonsters)
    {
        super(name, 0, healingPercent, numOfActivations, description, buffs, buffsChance, cooldown, false, false, false, false, false, targetsMultipleMonsters, 0);
    }
    
    /**
     * Constructs a new Heal Ability with no buffs or debuffs
     *
     * @param name                    the name of the monster
     * @param healingPercent          the percent of the max hp to heal (ex. 0.25)
     * @param numOfActivations        the amount of times the ability activates when called
     * @param description             the description to print
     * @param cooldown                the cooldown of the ability
     * @param targetsMultipleMonsters whether the Ability targets multiple Monsters
     */
    //No buffs or debuffs
    public Heal_Ability(String name, double healingPercent, int numOfActivations, String description, int cooldown, boolean targetsMultipleMonsters)
    {
        super(name, 0, healingPercent, numOfActivations, description, cooldown, false, false, false, false, false, targetsMultipleMonsters);
    }
}