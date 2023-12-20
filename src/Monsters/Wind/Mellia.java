package Monsters.Wind;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;


public class Mellia extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
    private static int count = 1;
    
    
    public Mellia()
    {
        super("Mellia" + count, WIND, 10_710, 648, 615, 104, 15, 50, 15, 0);
        setRunes(MonsterRunes.getRunesFromFile("Mellia1.csv", this));
        setAbilities();
        count++;
    }
    
    
    public Mellia(String fileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    
    public void setAbilities()
    {
        
        abilities.add(new Attack_Ability("Flower growing (1)", 1.2 * 3.8, 0, 1, "Attacks an enemy and absorbs the Attack Bar " +
                "by 15% with a 70% chance", 0, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.DEC_ATK_SPD, 2, 0);
        ability2Debuffs.add(new DecAtkBar(50));
        ArrayList<Integer> ability2DebuffChances = abilityChances(75, 100);
        abilities.add(new Attack_Ability("Earth friends (2)", 1.2 * 3.5, 0, 1, "Attacks all enemies to decrease their Attack " +
                "Speed for 2 turns with a 75% chance and their Attack Bar by 50%.", ability2Debuffs, ability2DebuffChances, 3, false, false));
        
        //@Passive:Creation
        abilities.add(new Passive("Thorn Tree", "Inflicts Continuous Damage on the target for 2 turns with every attack. If you attack an enemy who " +
                "already has " +
                "Continuous Damage, additionally inflicts Continuous Damage for 1 turn."));
        
        abilities.add(new Leader_Skill(Stat.HP, 0.33, ALL));
        
        super.setAbilities(abilities);
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
            int random = new Random().nextInt(101);
            if (random <= 70)
            {
                target.setAtkBar((int) (target.getAtkBar() - 150));
                setAtkBar((int) (getAtkBar() + 150));
            }
            
            //@Passive
            if (!containsDebuff(new Debuff(Debuff.OBLIVION, 1, 0)))
            {
                if (target.containsDebuff(new Debuff(Debuff.CONTINUOUS_DMG, 1, 0)))
                {
                    target.addAppliedDebuff(new Debuff(Debuff.CONTINUOUS_DMG, 1, 0), 100, this);
                    target.addAppliedDebuff(new Debuff(Debuff.CONTINUOUS_DMG, 1, 0), 100, this);
                }
                else
                {
                    target.addAppliedDebuff(new Debuff(Debuff.CONTINUOUS_DMG, 1, 0), 100, this);
                }
            }
        }
        if (abilityNum == 2)
        {
            Team other = (game.getOtherTeam().size() > 0) ? game.getOtherTeam() : Auto_Play.getOther();
            for (int i = 0; i < other.size(); i++)
            {
                Monster m = other.get(i);
                if (!m.equals(target))
                {
                    attack(m, abilities.get(1), false);
                }
                
                //@Passive
                if (!containsDebuff(new Debuff(Debuff.OBLIVION, 1, 0)))
                {
                    if (target.containsDebuff(new Debuff(Debuff.CONTINUOUS_DMG, 1, 0)))
                    {
                        target.addAppliedDebuff(new Debuff(Debuff.CONTINUOUS_DMG, 1, 0), 100, this);
                        target.addAppliedDebuff(new Debuff(Debuff.CONTINUOUS_DMG, 1, 0), 100, this);
                    }
                    else
                    {
                        target.addAppliedDebuff(new Debuff(Debuff.CONTINUOUS_DMG, 1, 0), 100, this);
                    }
                }
            }
        }
        
        super.afterTurnProtocol((abilityNum == 1) ? target : (game.getOtherTeam().size() > 0) ? game.getOtherTeam() : Auto_Play.getOther(), true);
        return true;
    }
}
