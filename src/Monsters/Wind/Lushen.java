package Monsters.Wind;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * Wind Joker
 */
public class Lushen extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Lushen()
    {
        this("Lushen1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Lushen(String runeFileName)
    {
        super("Lushen" + count, WIND, 9_225, 461, 900, 103, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.UNRECOVERABLE, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Flying Cards (1)", 1.2 * 3.6, 0, 1, "Throws a sharp card to attack and disturbs the " +
                                                                              "enemy's HP recovery for 2 turns with a 90% chance.", ability1Debuffs, ability1DebuffChances, 0, false, false, false));
        
        abilities.add(new Attack_Ability("Surprise Box (2)", 1.25 * 2.4, 0, 1, "Summons a surprise box that inflicts damage" +
                                                                               " and grants 1 random weakening effect among Stun, Glancing Hit Rate Increase, and Attack Speed Decrease to all enemies.", 3, false,
                false, true));
        
        abilities.add(new Attack_Ability("Amputation Magic (3)", 1.3 * 0.68, 0, 3, "Throws a number of cards and inflicts " +
                                                                                   "damage to all enemies, ignoring their Defense.", 4, true, false, true));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        //Apply random debuff
        if (abilityNum == 2)
        {
            applyToTeam(game.getOtherTeam(), m -> {
                int random = new Random().nextInt(3);
                switch (random)
                {
                    case 0 -> m.addAppliedDebuff(new Debuff(Debuff.STUN, 1, 0), 100, this);
                    case 1 -> m.addAppliedDebuff(new Debuff(Debuff.GLANCING_HIT_UP, 1, 0), 100, this);
                    case 2 -> m.addAppliedDebuff(new Debuff(Debuff.DEC_ATK_SPD, 1, 0), 100, this);
                }
            });
        }
        
        super.afterTurnProtocol((abilityNum == 1) ? target : game.getOtherTeam(), true);
        return true;
    }
}