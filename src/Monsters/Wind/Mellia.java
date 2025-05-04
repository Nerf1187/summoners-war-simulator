package Monsters.Wind;

import Abilities.*;
import Monsters.*;
import Runes.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Wind Dryad
 */
public class Mellia extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Mellia()
    {
        this("Mellia1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Mellia(String runeFileName)
    {
        super("Mellia" + count, Element.WIND, 10_710, 648, 615, 104, 15, 50, 15, 0);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        Ability a1 = new Attack_Ability("Flower growing (1)", 1.2 * 3.8, 0, 1, "Attacks an enemy and absorbs the Attack Bar " +
                                                                                "by 15% with a 70% chance", 0, false, false, false);
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(DebuffEffect.DEC_ATK_SPD.getNum(), 2, 0);
        ability2Debuffs.add(new DecAtkBar(50));
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(75, 100);
        Ability a2 = new Attack_Ability("Earth friends (2)", 1.2 * 3.5, 0, 1, "Attacks all enemies to decrease their Attack " +
                                                                               "Speed for 2 turns with a 75% chance and their Attack Bar by 50%.", ability2Debuffs, ability2DebuffChances, 3, false, false, true);
        
        //@Passive:Creation
        Ability a3 = new Passive("Thorn Tree", "Inflicts Continuous Damage on the target for 2 turns with every attack. If you attack an enemy who " +
                                                "already has Continuous Damage, additionally inflicts Continuous Damage for 1 turn.");
        
        Ability a4 = new Leader_Skill(RuneAttribute.HP, 0.33, Element.ALL);
        
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        if (abilityNum == 1)
        {
            //Steal attack bar
            int random = new Random().nextInt(101);
            if (random <= 70)
            {
                target.setAtkBar((int) (target.getAtkBar() - 150));
                setAtkBar((int) (getAtkBar() + 150));
            }
            
            //Attempt to apply passive
            applyPassive(target);
        }
        if (abilityNum == 2)
        {
            //Attempt to apply passive to the enemy team
            applyToTeam( game.getOtherTeam(), this::applyPassive);
        }
        
        super.afterTurnProtocol((abilityNum == 1) ? target : game.getOtherTeam(), true);
        return true;
    }
    
    /**
     * Applies Mellia's passive ability
     *
     * @param target The target to try and apply the passive to
     */
    private void applyPassive(Monster target)
    {
        //@Passive
        //Apply DOT to the target.
        //Apply 2 if they already have DOT
        if (this.passiveCanActivate())
        {
            if (target.containsDebuff(DebuffEffect.CONTINUOUS_DMG))
            {
                target.addAppliedDebuff(DebuffEffect.CONTINUOUS_DMG, 100, 1, this);
            }
            target.addAppliedDebuff(DebuffEffect.CONTINUOUS_DMG, 100, 1, this);
        }
    }
}
