
package Monsters.Dark;

import Abilities.*;
import Effects.Debuffs.*;
import Monsters.*;
import Util.Util.*;
import java.util.*;

/**
 * Dark Black Tea Bunny
 */
public class Lavender extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Lavender()
    {
        this("Lavender1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Lavender(String runeFileName)
    {
        super("Lavender" + count, Element.DARK, 9_225, 560, 801, 99, 30, 50, 15, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.BLOCK_BENEFICIAL_EFFECTS.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(50);
        Ability a1 = new Attack_Ability("Magic Kettle (1)", 3.8 * 1.30, 0, 1, "Attacks the enemy to prevent from receiving beneficial effects for 2 turns with a 50% chance.", ability1Debuffs, ability1DebuffChances, 0, false, false, false);
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs();
        ability2Debuffs.add(new DecAtkBar(25));
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(100);
        Ability a2 = new Attack_Ability("Spinning Tea Spoon (2)", 3.8 * 1.30, 0, 3, "Attacks all enemies 3 times to reduce their Attack Bar by 25% each, and attacks them once more to stun them for 1 turn with a 30% chance. If the enemy's Attack " +
                                                                                    "Bar is 0, you are guaranteed to stun the target.", ability2Debuffs, ability2DebuffChances, 3, false, false, true);
        
        Ability a3 = new Attack_Ability("Midnight Teatime (3)", 1.3 * 1.20, 0, 3, "Evens out the ratio of all enemies' HP and Attack Bar except the boss and then attacks them 3 times.", 4, false, false, true);
        
        super.setAbilities(a1, a2, a3);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        if (abilityNum == 3 && turnIsValid(target, abilityNum))
        {
            double avgHpRatio = 0;
            double avgAtkBar = 0;
            for (Monster m : game.getOtherTeam())
            {
                if (m.isDead())
                {
                    continue;
                }
                avgHpRatio += m.getHpRatio();
                avgAtkBar += m.getAtkBar();
            }
            
            avgHpRatio /= game.getOtherTeam().numOfLivingMons();
            avgAtkBar /= game.getOtherTeam().numOfLivingMons();
            
            for (Monster m : game.getOtherTeam())
            {
                if (m.isDead())
                {
                    continue;
                }
                m.setHpRatio(avgHpRatio);
                m.setAtkBar(avgAtkBar);
            }
        }
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        if (abilityNum == 2)
        {
            Ability a = getAbilities().get(1);
            this.attack(target, new Attack_Ability("", a.getDmgMultiplier(), a.getHealingPercent(), 1, "", abilityDebuffs(DebuffEffect.STUN.getNum(), 1, 0), MONSTERS.abilityChances((target.getAtkBar() == 0) ? 100 : 30), 0,
                    a.ignoresDefense(), a.ignoresDmgReduction(), a.targetsAllTeam()));
        }
        
        super.afterTurnProtocol((abilityNum == 1) ? target : game.getOtherTeam(), true);
        return true;
    }
}