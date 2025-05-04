package Monsters.Dark;

import Abilities.*;
import Monsters.*;
import Effects.Buffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Dark Epikion Priest
 */
public class Rasheed extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Rasheed()
    {
        this("Rasheed1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Rasheed(String runeFileName)
    {
        super("Rasheed" + count, Element.DARK, 10_875, 549, 538, 96, 15, 50, 40, 0);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        Ability a1 = new Attack_Ability("Absorb Mana (1)", 1.35 * (1.8 + (0.12 * getMaxHp()) / getAtk()), 0.5, 1, "Attacks the enemy and recovers the HP by 50% of the damage dealt. This attack will deal more damage according to your MAX HP.", 0,
                false, false, false);
        
        ArrayList<Buff> ability2Buffs = MONSTERS.abilityBuffs(BuffEffect.EXTEND_BUFF.getNum(), 0, BuffEffect.SHORTEN_DEBUFF.getNum(), 0);
        ArrayList<Integer> ability2BuffChances = MONSTERS.abilityChances(100, 100);
        Ability a2 = new Heal_Ability("Block Flow (2)", 0 /*Calculated at nextTurn()*/, 1,
                "Extends the time of the beneficial effects and shortens the time of the harmful effects granted on all allies, and recovers their HP by 10%. The recovery amount increases by 10% per harmful effect or beneficial effect " +
                "granted on the allies.", ability2Buffs, ability2BuffChances, 3, true);
        
        Ability a3 = new Attack_Ability("Soul Control (3)", 1.2 * ((0.18 * getMaxHp()) / getAtk()), 0, 3,
                "Attacks the enemy target 3 times to inflict great damage. This attack will deal more damage according to your MAX HP. Also, if the enemy dies, creates a shield by the target's MAX HP on all allies for 2 turns. The amount of shield" +
                " created cannot exceed twice your MAX HP.", 3, false, false, false);
        
        super.setAbilities(a1, a2, a3);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        switch (abilityNum)
        {
            //Increase healing done for each buff and debuff on the Monster
            case 2 -> applyToTeam(game.getNextMonsTeam(), m -> {
                double healAmt = (m.getAppliedDebuffs().size() + m.getAppliedBuffs().size() + 1.0) / 10;
                heal(m, new Heal_Ability("", healAmt * 1.3, 1, "", 0, false));
            });
            case 3 ->
            {
                //Add shield to the allied team if the target is dead
                if (target.isDead())
                {
                    applyToTeam(game.getNextMonsTeam(), m -> m.addAppliedBuff(new Shield(Math.min(target.getMaxHp(), this.getMaxHp() * 2), 2), this));
                }
            }
        }
        afterTurnProtocol((abilityNum == 2) ? game.getNextMonsTeam() : target, false);
        return true;
    }
}
