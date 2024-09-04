package Monsters.Fire;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;


public class Trevor extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
    private static int count = 1;
    
    
    public Trevor()
    {
        super("Trevor" + count, FIRE, 8_895, 659, 725, 107, 15, 50, 15, 0);
        setRunes(MonsterRunes.getRunesFromFile("Trevor1.csv", this));
        setAbilities();
        count++;
    }
    
    
    public Trevor(String fileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    private void setAbilities()
    {
        
        ArrayList<Buff> ability1Buffs = abilityBuffs(Buff.ATK_UP, 2);
        ArrayList<Integer> ability1BuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Combat Knife (1)", 1.35 * 1.9, 0, 2, "Swings a dagger to attack the enemy 2 times and " +
                "subsequently increases your Attack Power for 2 turns.", ability1Buffs, ability1BuffChances, 0, false, false, 0, false));
        
        abilities.add(new Attack_Ability("Relentless Strike (2)", 1.3 * 5.7, 0.3, 1, "Increases the Critical Rate for 2 turns " +
                "and instantly attacks an enemy with a powerful strike. In addition, recovers your HP by 30% of the inflicted damage.", 3, false,
                false, false));
        
        //@Passive:Creation
        abilities.add(new Passive("Brawler's Will", "As your HP decreases, the damage inflicted to the enemy will increase and the damage you receive " +
                "will decrease. The damage you receive will decrease by 20% additionally if you are under harmful effects."));
        
        super.setAbilities(abilities);
        
        abilities.add(new Leader_Skill(Stat.ATK, 0.33, ALL));
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        if (abilityNum == 2 && abilities.get(1).getTurnsRemaining() == 0)
        {
            this.addAppliedBuff(Buff.CRIT_RATE_UP, 2, this);
        }
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        super.afterTurnProtocol(target, true);
        return true;
    }
    
    public double dmgIncProtocol(double num)
    {
        if (containsDebuff(Debuff.OBLIVION))
        {
            return num;
        }
        
        //@Passive
        double ratio = (1.0 * this.getCurrentHp() / this.getMaxHp());
        return num + (num * ((1 - ratio) * 2));
    }
    
    public double dmgReductionProtocol(double num, boolean self)
    {
        if (!self)
        {
            return num;
        }
        
        if (this.containsDebuff(Debuff.OBLIVION))
        {
            return num;
        }
        
        //@Passive
        double ratio = (1.0 * this.getCurrentHp() / this.getMaxHp());
        num -= (num * ((1 - ratio) * 0.75));
        
        return (!this.getAppliedDebuffs().isEmpty()) ? num * 0.8 : num;
    }
}
