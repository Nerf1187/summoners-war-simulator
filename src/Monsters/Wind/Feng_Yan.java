package Monsters.Wind;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;


public class Feng_Yan extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    
    public Feng_Yan()
    {
        super("Feng Yan" + count, WIND, 10_215, 801, 659, 96, 15, 50, 15, 25);
        setRunes(MonsterRunes.getRunesFromFile("Feng_Yan1.csv", this));
        setAbilities();
        count++;
    }
    
    
    public Feng_Yan(String fileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.DEC_DEF, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(50);
        abilities.add(new Attack_Ability("Sequential Attack (1)", 1.3 * 1.25, 0, 3, "Attacks the enemy " +
                "3 times with each strike having a 30% chance to decrease the target's Defense for 2 turns.", ability1Debuffs, ability1DebuffChances,
                0, false, false));
        
        ArrayList<Buff> ability2Buffs = abilityBuffs(Buff.RECOVERY, 2);
        ArrayList<Integer> ability2BuffChances = abilityChances(100);
        abilities.add(new Heal_Ability("Calm Mind (2)", 0, 1, "Removes the harmful effects casted on yourself " +
                "and counterattacks for 2 turns when attacked. Recovers 15% of the HP of all allies in each turn for the next 2 turns.", ability2Buffs,
                ability2BuffChances, 3, true));
        
        //@Passive:Creation
        abilities.add(new Passive("Wind and Clouds", "Your Attack Bar increases by 20% whenever you are attacked. Your attacks will " +
                "inflict additional damage that is proportionate to your Defense whenever you attack."));
        
        abilities.add(new Leader_Skill(Stat.DEF, 0.44, ALL));
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        abilities.get(0).setDmgMultiplier(1.25 * (1.3 + (1.6 * getDef() * 1.35)) / getAtk());
        
        if (abilityNum == 2 && abilityIsValid(2))
        {
            cleanse();
            addAppliedBuff(Buff.COUNTER, 2, this);
        }
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        Team friendlyTeam = (game.getNextMonsTeam().size() > 0) ? game.getNextMonsTeam() : Auto_Play.getHighestAtkBar();
        if (abilityNum == 2)
        {
            for (Monster mon : friendlyTeam.getMonsters())
            {
                if (mon.equals(target))
                {
                    continue;
                }
                heal(mon, abilities.get(1));
            }
        }
        
        afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
    
    public void attacked(Monster attacker)
    {
        super.attacked(attacker);
        
        //@Passive
        if (!containsDebuff(Debuff.OBLIVION))
        {
            setAtkBar((int) getAtkBar() + 200);
        }
    }
}
