package Monsters.Water;

import Abilities.*;
import Effects.Debuffs.*;
import Monsters.*;
import Util.Util.*;
import java.util.*;

/**
 * Water Onimusha
 */
public class Suiki extends Monster
{
    private static int count = 1;
    
    private double atkIncrease = 1;
    private final int originalAtk;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Suiki()
    {
        this("Suiki1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Suiki(String runeFileName)
    {
        super("Suiki" + count, Element.WATER, 9_885, 582, 736, 100, 15, 50, 40, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
        originalAtk = getAtk();
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.DEC_DEF.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(50);
        Ability a1 = new Attack_Ability("Ghost Slash (1)", 3.5 * 1.30, 30, 2, "Attacks the enemy 2 times, recovering HP by 30% of the inflicted damage and decreasing the target's Defense for 2 turns with a 50% chance each.", ability1Debuffs,
                ability1DebuffChances, 0, false, false, false);
        
        Ability a2 = new Attack_Ability("Ghost Wild Dance (2)", 6.2 * 1.30, 30, 2, "Attacks all enemies 2 times and recovers HP by 30% of the inflicted damage.", 3, false, false, true);
        
        //@Passive:Creation
        Ability a3 = new Passive("Undergo Hardship", "Increases your Attack Power by 10% each, up to 250%, whenever Monsters except yourself get a turn. Critical Hits won’t occur when attacking the enemy.");
        
        super.setAbilities(a1, a2, a3);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        int critRate = this.getCritRate();
        
        //@Passive
        //Make sure Suiki does not crit
        if (this.passiveCanActivate())
        {
            setCritRate(-999_999);
        }
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            this.setCritRate(critRate);
            return false;
        }
        
        super.afterTurnProtocol((abilityNum == 2) ? game.getOtherTeam() : target, true);
        this.setCritRate(critRate);
        return true;
    }
    
    public void beforeTurnProtocol(Monster nextMon, boolean self, boolean enemyTurn, boolean hasOblivion)
    {
        super.beforeTurnProtocol(nextMon, self, enemyTurn, hasOblivion);
        if (this.isDead())
        {
            return;
        }
        
        if (!self && atkIncrease < 3.5)
        {
            atkIncrease += 0.1;
            
            this.setAtk(originalAtk * atkIncrease);
            
            if (isPrint())
            {
                System.out.printf("Undergo Hardship (%s) +%d\n", this.getName(true, false), (int) ((atkIncrease - 1) / 0.1));
            }
        }
    }
}