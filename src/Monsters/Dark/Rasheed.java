package Monsters.Dark;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import java.util.*;

public class Rasheed extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    public Rasheed()
    {
        super("Rasheed" + count, DARK, 10_875, 549, 538, 96, 15, 50, 40, 0);
        setRunes(MonsterRunes.getRunesFromFile("Rasheed1.csv", this));
        setAbilities();
        count++;
    }
    
    public Rasheed(String runeFileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        abilities.add(new Attack_Ability("Absorb Mana (1)", 1.35 * (1.8 + (0.12 * getMaxHp()) / getAtk()), 0.5, 1, "Attacks the enemy and recovers the HP by 50% of the damage dealt. This attack will deal more damage according to your MAX HP.", 0,
                false, false));
        
        ArrayList<Buff> ability2Buffs = abilityBuffs(Buff.EXTEND_BUFF, 0, Buff.SHORTEN_DEBUFF, 0);
        ArrayList<Integer> ability2BuffChances = abilityChances(100, 100);
        abilities.add(new Heal_Ability("Block Flow (2)", 0 /*Calculated at nextTurn()*/, 1,
                "Extends the time of the beneficial effects and shortens the time of the harmful effects granted on all allies, and recovers their HP by 10%. The recovery amount increases by 10% per harmful effect or beneficial effect " +
                        "granted" + " on the allies.", ability2Buffs, ability2BuffChances, 3, true));
        
        abilities.add(new Attack_Ability("Soul Control (3)", 1.2 * ((0.18 * getMaxHp()) / getAtk()), 0, 3,
                "Attacks the enemy target 3 times to inflict great damage. This attack will deal more damage according to your MAX HP. Also, if the enemy dies, creates a shield by the target's MAX HP on all allies for 2 turns. The amount of shield created cannot exceed twice your MAX HP.", 3, false, false));
        super.setAbilities(abilities);
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
            case 2 ->
            {
                Team next = game.getNextMonsTeam();
                for (int i = 0; i < next.size(); i++)
                {
                    Monster m = next.get(i);
                    double healAmt = 1.0 * (m.getAppliedDebuffs().size() + m.getAppliedBuffs().size() + 1) / 10;
                    heal(m, new Heal_Ability("", healAmt * 1.3, 1, "", 0, false));
                }
            }
            case 3 ->
            {
                if (target.isDead())
                {
                    Team next = game.getNextMonsTeam();
                    for (int i = 0; i < next.size(); i++)
                    {
                        Monster m = next.get(i);
                        m.addAppliedBuff(new Shield(Math.min(target.getMaxHp(), this.getMaxHp() * 2), (m.equals(this)) ? 3 : 2), this);
                    }
                }
            }
        }
        afterTurnProtocol((abilityNum == 2) ? game.getNextMonsTeam() : target, false);
        return true;
    }
}
