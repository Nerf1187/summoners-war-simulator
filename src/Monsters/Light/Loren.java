package Monsters.Light;

import Abilities.*;
import Monsters.*;
import Runes.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Light Cowgirl
 */
public class Loren extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Loren()
    {
        this("Loren1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Loren(String runeFileName)
    {
        super("Loren" + count, Element.LIGHT, 9_225, 516, 681, 101, 15, 50, 15, 25);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.DEC_ATK_SPD.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(50);
        Ability a1 = new Attack_Ability("Cross Fire (1)", 1.25 * 1.25, 0, 3, "Shoots 3 projectiles, with each projectile having a 30% chance to slow the Attack Speed for 2 turns.", ability1Debuffs,
                ability1DebuffChances, 0, false, false, false);
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(DebuffEffect.REMOVE_BENEFICIAL_EFFECT.getNum(), 1, 1, DebuffEffect.UNRECOVERABLE.getNum(), 2, 0);
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(75, 100);
        Ability a2 = new Attack_Ability("Silver-Plated Bullet (2)", 3.0 * 1.25, 0, 2, "Attacks the enemy 2 times with holy bullets. Each attack has a 60% chance to remove one beneficial effect and disturb the HP recovery for 2 turns.",
                ability2Debuffs, ability2DebuffChances, 3, false, false, false);
        
        //@Passive: Creation
        Ability a3 = new Passive("Chaser", "Decreases the enemy's Attack Bar by 20% with each attack and weakens the enemy's defense for 1 turn with a 75% chance.");
        
        Ability a4 = new Leader_Skill(RuneAttribute.ATK, 0.18, Element.ALL);
        
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        super.afterTurnProtocol(target, true);
        return true;
    }
    
    public void attackerAfterHitProtocol(Monster target, int abilityNum, int count)
    {
        //@Passive
        //Decrease the target's attack bar and apply Defense down
        if (this.passiveCanActivate())
        {
            target.addAppliedDebuff(new DecAtkBar(20), 100, this);
            target.addAppliedDebuff(DebuffEffect.DEC_DEF, 75, 1, this);
        }
        
        super.attackerAfterHitProtocol(target, abilityNum, count);
    }
}
