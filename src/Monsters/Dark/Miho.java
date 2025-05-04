package Monsters.Dark;

import Abilities.*;
import Game.*;
import Monsters.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Dark Martial Cat (2A)
 */
public class Miho extends Monster
{
    private static int count = 1;
    private boolean gotCritThisTurn = false;
    private final int critRate;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Miho()
    {
        this("Miho1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Miho(String runeFileName)
    {
        super("Miho" + count, Element.DARK, 10_050, 560, 801, 119, 15, 50, 15, 0);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        critRate = getCritRate();
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.STUN.getNum(), 1, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(50);
        Ability a1 = (new Attack_Ability("Energy Punch (1)", 1.2 * 4.45, 0.2, 1, "Attacks with a spinning punch and stuns the " +
                                                                                 "enemy for 1 turn with a 50% chance and recovers HP by 20% of inflicted damage.", ability1Debuffs, ability1DebuffChances, 0, false, false, false));
        
        ArrayList<Debuff> ability2Debuffs = new ArrayList<>();
        ability2Debuffs.add(new DecAtkBar(25));
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(100);
        Ability a2 = (new Attack_Ability("Chain Attack(2)", 1.2 * 3.9, 0, 2, "Launches 2 consecutive attacks on an enemy, " +
                                                                             "inflicting damage and decreasing the enemy's Attack Bar by 25% with each attack.", ability2Debuffs, ability2DebuffChances, 3, false, false, false));
        
        //@Passive:Creation
        Ability a3 = (new Passive("Eye For An Eye", "Increases your Attack Bar by 30% and counterattacks the attacker with a critical hit when you are attacked with a critical hit. You won't get defeated with critical hit attacks."));
        
        super.setAbilities(a1, a2, a3);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        super.afterTurnProtocol(target, !Game.canCounter(), true);
        //Reset crit rate
        setCritRate(critRate);
        return true;
    }
    
    public void attacked(Monster attacker)
    {
        super.attacked(attacker);
        
        //@Passive
        //Counter if hit by a crit
        if (gotCritThisTurn && this.passiveCanActivate() && Game.canCounter())
        {
            increaseAtkBarByPercent(40);
            setCritRate(10_000);
            addAppliedBuff(BuffEffect.COUNTER, 0, this);
        }
    }
    
    public void targetAfterHitProtocol(Monster attacker)
    {
        //@Passive
        //Make sure Miho does not die from a crit attack
        gotCritThisTurn = wasCrit() || gotCritThisTurn;
        if (wasCrit() && this.passiveCanActivate() && (getCurrentHp() <= 0 || this.isDead()))
        {
            setCurrentHp(1);
            setDead(false);
        }
    }
}