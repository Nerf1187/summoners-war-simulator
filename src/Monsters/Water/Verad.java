package Monsters.Water;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Water Dragon
 */
public class Verad extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Verad()
    {
        this("Verad1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Verad(String runeFileName)
    {
        super("Verad" + count, Element.WATER, 11_535, 801, 571, 98, 15, 50, 15, 25);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        Ability a1 = new Attack_Ability("Ice Bolt (1)", 1.3 * (4.6 + ((getDef() * 2.9)) / getAtk()), 0, 1, "Shoots an Ice " +
                                                                                                            "arrow at the enemy. This attack will deal more damage according to your Defense.", 0, false, false, false);
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(DebuffEffect.NULL.getNum(), 1, 0);
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(100);
        Ability a2 = new Attack_Ability("Snowstorm (2)", 1.2 * (3.6 + ((getDef() * 2.7)) / getAtk()), 0, 1, "Attacks all" +
                                                                                                             " enemies with a storm of ice, dealing damage proportionate to your Defense and freezing them for 1 turn with a 80% chance.",
                ability2Debuffs, ability2DebuffChances, 3, false, false, true);
        
        //Null debuff is a placeholder to check if target gets frozen for a second turn
        ArrayList<Debuff> ability3Debuffs = abilityDebuffs(DebuffEffect.NULL.getNum(), 1, 0);
        ability3Debuffs.add(new DecAtkBar(100));
        ArrayList<Integer> ability3DebuffChances = MONSTERS.abilityChances(100, 100);
        Ability a3 = new Attack_Ability("Absolute Zero (3)", 1.3 * (5.0 + ((getDef() * 2.7)) / getAtk()), 0, 1, "Attacks " +
                                                                                                                 "all enemies with a gust of freezing wind, dealing damage proportionate to your Defense and setting the Attack Bar to 0. Additionally, " +
                                                                                                                 "the enemies are frozen for 1 turn.", ability3Debuffs, ability3DebuffChances, 4, false, false, true);
        
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
        super.afterTurnProtocol((abilityNum == 1) ? target : game.getOtherTeam(), true);
        return true;
    }
    
    public void attackTeam(Team targetTeam, Ability ability)
    {
        for (Monster target : targetTeam.getMonsters())
        {
            //Check if the target has a Freeze debuff cast by Verad
            boolean isFrozenBeforeAttackByVerad = target.containsDebuff(DebuffEffect.FREEZE) && target.getAppliedDebuffs().get(target.getDebuffIndex(DebuffEffect.FREEZE)).getCaster() instanceof Verad;
            attack(target, ability);
            //Check if Verad froze the target again
            boolean isFrozenAfterAttack = target.containsDebuff(DebuffEffect.NULL);
            //Remove temporary debuff and re-apply Freeze
            if (isFrozenAfterAttack)
            {
                target.removeDebuff(DebuffEffect.NULL);
                target.addGuaranteedAppliedDebuff(DebuffEffect.FREEZE, (isFrozenBeforeAttackByVerad) ? 2 : 1, this);
            }
        }
    }
}
