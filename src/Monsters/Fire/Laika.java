package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;


public class Laika extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    
    public Laika()
    {
        super("Laika" + count, FIRE, 11040, 571, 834, 100_0, 15, 50, 15, 0);
        setRunes(MonsterRunes.getRunesFromFile("Laika1.csv", this));
        setAbilities();
        count++;
    }
    
    
    public Laika(String fileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    private void setAbilities()
    {
        
        abilities.add(new Attack_Ability("Dragon's Might (1)", 4.3 * 1.3, 0.5, 1, "Attacks the enemy, inflicting Continuous " +
                "Damage for 2 turns if the attack lands as a critical hit. Also recovers HP by 50% of the inflicted damage.", 0, false, false));
        
        abilities.add(new Attack_Ability("Justice (2)", 0 /*calculated at nextTurn()*/, 0, 1, "Channels burning rage to inflict damage " +
                "that ignores all damage reduction effects to the enemy. The damage increases according to the number of dead allies.", 2, false,
                true));
        
        //@Passive:Creation
        abilities.add(new Passive("Noble Blood", "Your attacks won't land as Glancing Hits and the inflicted damage of one attack won't exceed 24% of " +
                "the MAX HP. " +
                "Additionally, counterattacks the attacker with a 50% chance when you're attacked.", 0));
        
        
        abilities.add(new Leader_Skill(Stat.DEF, 0.5, FIRE));
        
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        setAbilityGlancingRateChange(0);
        
        //@Passive
        if (!containsDebuff(Debuff.OBLIVION))
        {
            setAbilityGlancingRateChange(-999_999);
        }
        
        Team friendlyTeam = (game.getNextMonsTeam().size() > 0) ? game.getNextMonsTeam() : Auto_Play.getHighestAtkBar();
        int numOfAliveMons = 0;
        for (Monster mon : friendlyTeam.getMonsters())
        {
            if (!mon.isDead())
            {
                numOfAliveMons++;
            }
        }
        abilities.get(1).setDmgMultiplier((13.5 - 5.5 * (1.0 * numOfAliveMons / friendlyTeam.size())) * 1.3);
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        if (target.wasCrit())
        {
            if (abilityNum == 1)
            {
                target.addAppliedDebuff(Debuff.CONTINUOUS_DMG, 100, 2, this);
            }
        }
        
        super.afterTurnProtocol(target, !Game.canCounter(), true);
        return true;
    }
    
    public double dmgIncProtocol(double num)
    {
        return Math.min(num, getMaxHp() * 0.24);
    }
    
    public void attacked(Monster attacker)
    {
        
        int random = new Random().nextInt(101);
        
        //@Passive
        if (random < 50)
        {
            addAppliedBuff(Buff.COUNTER, 0, this);
        }
        super.attacked(attacker);
    }
}
