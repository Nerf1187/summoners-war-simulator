package Monsters.Water;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;


public class Verad extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
    private static int count = 1;
    
    
    public Verad()
    {
        super("Verad" + count, WATER, 11_535, 801, 571, 98, 15, 50, 15, 25);
        setRunes(MonsterRunes.getRunesFromFile("Verad1.csv", this));
        setAbilities();
        count++;
    }
    
    
    public Verad(String fileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    private void setAbilities()
    {
        
        abilities.add(new Attack_Ability("Ice Bolt (1)", 1.3 * (4.6 + ((getDef() * 2.9)) / getAtk()), 0, 1, "Shoots an Ice " +
                "arrow at the enemy. This attack will deal more damage according to your Defense.", 0, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.NULL, 0, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Snowstorm (2)", 1.2 * (3.6 + ((getDef() * 2.7)) / getAtk()), 0, 1, "Attacks all" +
                " enemies with a storm of ice, dealing damage proportionate to your Defense and freezing them for 1 turn with a 80% chance.",
                ability2Debuffs, ability2DebuffChances,
                3, false, false));
        
        //Null debuff is a placeholder to check if target gets frozen for a second turn
        ArrayList<Debuff> ability3Debuffs = abilityDebuffs(Debuff.NULL, 0, 0);
        ability3Debuffs.add(new DecAtkBar(2_000));
        ArrayList<Integer> ability3DebuffChances = abilityChances(100, 100);
        abilities.add(new Attack_Ability("Absolute Zero (3)", 1.3 * (5.0 + ((getDef() * 2.7)) / getAtk()), 0, 1, "Attacks " +
                "all enemies with a gust of freezing wind, dealing damage proportionate to your Defense and setting the Attack Bar to 0. Additionally, " +
                "the enemies are frozen for" +
                " 1 turn.", ability3Debuffs, ability3DebuffChances, 4, false, false));
        
        abilities.add(new Leader_Skill(Stat.HP, 0.33, ALL));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean isFrozenBeforeAttackByVerad = target.containsDebuff(Debuff.FREEZE) &&
                target.getAppliedDebuffs().get(target.getDebuffIndex(new Debuff(Debuff.FREEZE, 1, 0))).getCaster() instanceof Verad;
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        boolean isFrozenAfterAttack = target.containsDebuff(Debuff.NULL);
        if (isFrozenAfterAttack)
        {
            target.removeDebuff(Debuff.NULL);
        }
        
        switch (abilityNum)
        {
            case 2 ->
            {
                if (isFrozenAfterAttack)
                {
                    target.addGuaranteedAppliedDebuff(new Debuff(Debuff.FREEZE, (isFrozenBeforeAttackByVerad) ? 2 : 1, 0), this);
                }
                Team other = (game.getOtherTeam().size() > 0) ? game.getOtherTeam() : Auto_Play.getOther();
                for (int i = 0; i < other.size(); i++)
                {
                    Monster m = other.get(i);
                    
                    boolean mIsFrozenBeforeAttackByVerad = m.containsDebuff(Debuff.FREEZE) &&
                            m.getAppliedDebuffs().get(m.getDebuffIndex(new Debuff(Debuff.FREEZE, 1, 0))).getCaster() instanceof Verad;
                    
                    if (!m.equals(target))
                    {
                        attack(m, abilities.get(1), false);
                        boolean mIsFrozenAfterAttack = m.containsDebuff(Debuff.NULL);
                        if (mIsFrozenAfterAttack)
                        {
                            m.removeDebuff(Debuff.NULL);
                            m.addGuaranteedAppliedDebuff(new Debuff(Debuff.FREEZE, (mIsFrozenBeforeAttackByVerad) ? 2 : 1, 0), this);
                        }
                    }
                }
            }
            case 3 ->
            {
                if (isFrozenAfterAttack)
                {
                    target.addGuaranteedAppliedDebuff(new Debuff(Debuff.FREEZE, (isFrozenBeforeAttackByVerad) ? 2 : 1, 0), this);
                }
                
                Team other = (game.getOtherTeam().size() > 0) ? game.getOtherTeam() : Auto_Play.getOther();
                for (int i = 0; i < other.size(); i++)
                {
                    Monster m = other.get(i);
                    boolean mIsFrozenBeforeAttackByVerad = m.containsDebuff(Debuff.FREEZE) &&
                            m.getAppliedDebuffs().get(m.getDebuffIndex(new Debuff(Debuff.FREEZE, 1, 0))).getCaster() instanceof Verad;
                    
                    if (!m.equals(target))
                    {
                        attack(m, abilities.get(2), false);
                        boolean mIsFrozenAfterAttack = m.containsDebuff(Debuff.NULL);
                        if (mIsFrozenAfterAttack)
                        {
                            m.removeDebuff(Debuff.NULL);
                            m.addGuaranteedAppliedDebuff(new Debuff(Debuff.FREEZE, (mIsFrozenBeforeAttackByVerad) ? 2 : 1, 0), this);
                        }
                    }
                }
            }
        }
        super.afterTurnProtocol((abilityNum == 1) ? target : (game.getOtherTeam().size() > 0) ? game.getOtherTeam() : Auto_Play.getOther(), true);
        return true;
    }
}
