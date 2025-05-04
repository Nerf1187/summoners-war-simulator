package Monsters.Wind;

import Abilities.*;
import Effects.Debuffs.*;
import Monsters.*;
import Util.Util.*;
import java.util.*;

/**
 * Wind Joker
 */
public class Lushen extends Monster
{
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
        super("Lushen" + count, Element.WIND, 9_225, 461, 900, 103, 15, 50, 15, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.UNRECOVERABLE.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(100);
        Ability a1 = new Attack_Ability("Flying Cards (1)", 1.2 * 3.6, 0, 1, "Throws a sharp card to attack and disturbs the " +
                                                                              "enemy's HP recovery for 2 turns with a 90% chance.", ability1Debuffs, ability1DebuffChances, 0, false, false, false);
        
        Ability a2 = new Attack_Ability("Surprise Box (2)", 1.25 * 2.4, 0, 1, "Summons a surprise box that inflicts damage" +
                                                                               " and grants 1 random weakening effect among Stun, Glancing Hit Rate Increase, and Attack Speed Decrease to all enemies.", 3, false,
                false, true);
        
        Ability a3 = new Attack_Ability("Amputation Magic (3)", 1.3 * 0.68, 0, 3, "Throws a number of cards and inflicts " +
                                                                                   "damage to all enemies, ignoring their Defense.", 4, true, false, true);
        
        super.setAbilities(a1, a2, a3);
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
                m.addAppliedDebuff(new Debuff((switch (new Random().nextInt(3)) {
                    case 0 -> DebuffEffect.STUN;
                    case 1 -> DebuffEffect.GLANCING_HIT_UP;
                    default -> DebuffEffect.DEC_ATK_SPD;
                }), 1, 0), 100, this);
            });
        }
        
        super.afterTurnProtocol((abilityNum == 1) ? target : game.getOtherTeam(), true);
        return true;
    }
}
