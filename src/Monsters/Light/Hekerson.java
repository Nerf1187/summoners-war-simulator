package Monsters.Light;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * Light Poison Master
 */
public class Hekerson extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Hekerson()
    {
        this("Hekerson1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Hekerson(String runeFileName)
    {
        super("Hekerson" + count, LIGHT, 10_710, 560, 703, 101, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.STUN, 1, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(50);
        abilities.add(new Attack_Ability("Concussive Shot (1)", 4.1 * 1.2, 0, 1, "Attacks the enemy and " +
                                                                                 "Stuns the target for 1 turn with a 50% chance.", ability1Debuffs, ability1DebuffChances, 0, false, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.STRIP, 0, 1, Debuff.CONTINUOUS_DMG, 2, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(100, 100);
        abilities.add(new Attack_Ability("Poison Shot Fire (2)", 5.4 * 1.25, 0, 1, "Attacks the enemy " +
                                                                                   "and removes all beneficial effects granted on the target with a 75% chance and inflicts Continuous Damage for 2 turns with a 75% chance.",
                ability2Debuffs, ability2DebuffChances, 3, false, false, false));
        
        ArrayList<Debuff> ability3Debuffs = abilityDebuffs(Debuff.BLOCK_BENEFICIAL_EFFECTS, 2, 0);
        ArrayList<Integer> ability3DebuffChances = abilityChances(60);
        abilities.add(new Attack_Ability("Ruthless Bombing (3)", 1.6 * 1.15, 0, 3, "Attacks all enemies 3" +
                                                                                   " times, each attack having a 50% chance to block beneficial effects to be granted on them for 2 turns. Also, destroys the enemy's MAX " +
                                                                                   "HP by 50% of the damage dealt.", ability3Debuffs, ability3DebuffChances, 4, false, false, true));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        if (abilityNum == 3)
        {
            //Destroy HP
            applyToTeam(game.getOtherTeam(), m -> m.destroyHp((int) (getDmgDealtThisTurn() * 0.5)));
        }
        
        super.afterTurnProtocol((abilityNum == 1) ? target : game.getOtherTeam(), true);
        return true;
    }
}