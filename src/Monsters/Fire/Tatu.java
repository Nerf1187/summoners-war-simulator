package Monsters.Fire;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * Fire Pixie (2A)
 */
public class Tatu extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Tatu()
    {
        super("Tatu" + count, FIRE, 8_730, 626, 823, 111, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile("Tatu1.csv", this));
        setAbilities();
        count++;
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Tatu(String runeFileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.GLANCING_HIT_UP, 2, 0, Debuff.CONTINUOUS_DMG, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(85, 85);
        abilities.add(new Attack_Ability("Spirit Ball (1)", 4.4 * 1.1, 0, 1, "Attacks the enemy with an Energy ball. The attack has a 85% chance of increasing the enemy's chance to land a Glancing Hit and inflicting Continuous Damage for 2 turns " +
                                                                             "each.", ability1Debuffs, ability1DebuffChances, 0, false, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.CONTINUOUS_DMG, 3, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(100);
        
        abilities.add(new Attack_Ability("Ignite (2)", 5.6 * 1.25, 0, 1, "Attacks the enemy with a burning flame, inflicting Continuous Damage for 3 turns.", ability2Debuffs, ability2DebuffChances, 2, false, false, false));
        
        abilities.add(new Attack_Ability("Incinerate (3)", 4.2 * 1.25, 0, 1, "Attacks all enemies to inflict damage. In addition, blows up the Continuous Damage granted on each target to inflict damage that's equivalent to the target's Continuous " +
                                                                             "Damage.", 3, false, false, true));
        
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
            //Explode continuous damage on the enemy team
            applyToTeam(game.getOtherTeam(), m -> {
                while (m.containsDebuff(Debuff.CONTINUOUS_DMG))
                {
                    Debuff cont = m.getAppliedDebuffs().remove(m.getDebuffIndex(Debuff.CONTINUOUS_DMG));
                    for (int i = 0; i < cont.getNumTurns(); i++)
                    {
                        m.applyContinuousDmg();
                    }
                }
            });
        }
        
        afterTurnProtocol((abilityNum == 3) ? game.getOtherTeam() : target, true);
        return true;
    }
}