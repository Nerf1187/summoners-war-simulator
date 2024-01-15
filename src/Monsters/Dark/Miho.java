package Monsters.Dark;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;

//2A

public class Miho extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    private boolean gotCritThisTurn = false;
    private final int critRate;
    
    
    public Miho()
    {
        super("Miho" + count, DARK, 10_050, 560, 801, 119, 15, 50, 15, 0);
        setRunes(MonsterRunes.getRunesFromFile("Miho1.csv", this));
        critRate = getCritRate();
        setAbilities();
        count++;
    }
    
    
    public Miho(String runeFileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.STUN, 1, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(50);
        abilities.add(new Attack_Ability("Energy Punch (1)", 1.2 * 4.45, 0.2, 1, "Attacks with a spinning punch and stuns the " +
                "enemy for 1 turn with a 50% chance and recovers HP by 20% of inflicted damage.", ability1Debuffs, ability1DebuffChances, 0, false,
                false));
        
        ArrayList<Debuff> ability2Debuffs = new ArrayList<>();
        ability2Debuffs.add(new DecAtkBar(25));
        ArrayList<Integer> ability2DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Chain Attack(2)", 1.2 * 3.9, 0, 2, "Launches 2 consecutive attacks on an enemy, " +
                "inflicting damage and decreasing the enemy's Attack Bar by 25% with each attack.", ability2Debuffs, ability2DebuffChances, 3, false,
                false));
        
        //@Passive:Creation
        abilities.add(new Passive("Eye For An Eye", "Increases your Attack Bar by 30% and counterattacks the attacker with a critical hit when you are " +
                "attacked " +
                "with a critical hit. You won't get defeated with critical hit attacks."));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        super.afterTurnProtocol(target, !Game.canCounter(), true);
        setCritRate(critRate);
        return true;
    }
    
    public void attacked(Monster attacker)
    {
        super.attacked(attacker);
        
        //@Passive
        if (gotCritThisTurn && !containsDebuff(Debuff.OBLIVION) && Game.canCounter())
        {
            increaseAtkBarByPercent(40);
            setCritRate(10_000);
            addAppliedBuff(Buff.COUNTER, 0, this);
        }
    }
    
    public void afterHitProtocol(Monster attacker)
    {
        //@Passive
        gotCritThisTurn = wasCrit() || gotCritThisTurn;
        if (wasCrit() && !containsDebuff(Debuff.OBLIVION) && getCurrentHp() <= 0)
        {
            setCurrentHp(1);
        }
    }
}
