package Monsters.Light;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

//2A

public class Xiao_Ling extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
    private static int count = 1;
    
    
    public Xiao_Ling()
    {
        super("Xiao Ling" + count, LIGHT, 12_180, 527, 692, 104, 15, 50, 40, 0);
        setRunes(MonsterRunes.getRunesFromFile("Xiao_Ling1.csv", this));
        setAbilities();
        count++;
    }
    
    
    public Xiao_Ling(String fileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.STUN, 1, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Energy Punch (1)", 1.2 * (2.0 + ((0.2 * getMaxHp())) / getAtk()), 0, 1, "Attacks " +
                "with a spinning punch and stuns the enemy for 1 turn with a 50% chance. The damage of this attack increases according to your MAX HP.",
                ability1Debuffs,
                ability1DebuffChances, 0, false, false));
        
        ArrayList<Buff> ability2Buffs = abilityBuffs(Buff.CRIT_RATE_UP, 2, Buff.COUNTER, 2);
        ArrayList<Integer> ability2BuffChances = abilityChances(100, 100);
        abilities.add(new Ability("Counterattack (2)", 0, 0, 1, "Increases the critical rate for 2 turns and counterattacks " +
                "when attacked. The Attack Bar is increased by 70%, and the damage you receive will be reduced by half when you get attacked while this " +
                "skill is on cooldown.",
                new ArrayList<>(), new ArrayList<>(), ability2Buffs, ability2BuffChances, 3, false, false, false, true, false));
        
        //@Passive:Creation
        abilities.add(new Passive("Lonely Fight", "Absorbs the Attack Bar by 50% if you attack a monster with same or lower HP status compared to yours." +
                " Recovers " +
                "your HP by 50% of the damage dealt if you attack a monster that has better HP status than yours."));
        
        abilities.add(new Leader_Skill(Stat.ATK, 0.21, ALL));
        
        super.setAbilities(abilities);
    }
    
    @Override
    public boolean nextTurn(Monster target, int abilityNum)
    {
        if (!target.equals(this) && abilityNum == 2)
        {
            System.out.println("Ability can only be used on self!");
            return false;
        }
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        //@Passive
        if (!containsDebuff(new Debuff(Debuff.OBLIVION, 1, 0)))
        {
            if (target.getHpRatio() <= this.getHpRatio())
            {
                setAtkBar((int) (target.getAtkBar() * 0.5));
                target.setAtkBar((int) (target.getAtkBar() * 0.5));
            }
            else
            {
                setCurrentHp(getCurrentHp() + (int) (0.5 * getDmgDealtThisTurn()));
            }
        }
        
        if (abilityNum == 2)
        {
            setAtkBar((int) (getAtkBar() + 700));
        }
        
        
        super.afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
    
    public double dmgReductionProtocol(double num)
    {
        return (abilities.get(1).getTurnsRemaining() == 0) ? num : num / 2;
    }
}
