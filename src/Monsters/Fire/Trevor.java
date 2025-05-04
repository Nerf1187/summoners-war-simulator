package Monsters.Fire;

import Abilities.*;
import Monsters.*;
import Runes.*;
import Effects.Buffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Fire Neostone Fighter
 */
public class Trevor extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Trevor()
    {
        this("Trevor1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Trevor(String runeFileName)
    {
        super("Trevor" + count, Element.FIRE, 10_050, 527, 780, 107, 15, 50, 15, 0);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        
        ArrayList<Buff> ability1Buffs = MONSTERS.abilityBuffs(BuffEffect.ATK_UP.getNum(), 2);
        ArrayList<Integer> ability1BuffChances = MONSTERS.abilityChances(100);
        Ability a1 = new Attack_Ability("Combat Knife (1)", 1.35 * 1.9, 0, 2, "Swings a dagger to attack the enemy 2 times and " +
                                                                               "subsequently increases your Attack Power for 2 turns.", ability1Buffs, ability1BuffChances, 0, false, false, false, 0);
        
        Ability a2 = new Attack_Ability("Relentless Strike (2)", 1.3 * 5.7, 0.3, 1, "Increases the Critical Rate for 2 turns " +
                                                                                     "and instantly attacks an enemy with a powerful strike. In addition, recovers your HP by 30% of the inflicted damage. This attack won't land as a Glancing Hit", 3,
                false,
                false, false);
        
        //@Passive:Creation
        Ability a3 = new Passive("Brawler's Will", "As your HP decreases, the damage inflicted to the enemy will increase and the damage you receive " +
                                                    "will decrease. The damage you receive will decrease by 20% additionally if you are under harmful effects.");
        
        
        Ability a4 = new Leader_Skill(RuneAttribute.ATK, 0.33, Element.ALL);
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //Apply crit rate before turn
        if (abilityNum == 2 && abilities.get(1).getTurnsRemaining() == 0)
        {
            this.addAppliedBuff(BuffEffect.CRIT_RATE_UP, 2, this);
            this.setAbilityGlancingRateChange(-999_999);
        }
        
        boolean b = super.nextTurn(target, abilityNum);
        this.setAbilityGlancingRateChange(0);
        if (!b)
        {
            return false;
        }
        super.afterTurnProtocol(target, true);
        return true;
    }
    
    public double dmgIncProtocol(double num)
    {
        if (!this.passiveCanActivate())
        {
            return num;
        }
        
        //@Passive
        //Increase damage based on current HP
        double ratio = (1.0 * this.getCurrentHp() / this.getMaxHp());
        return num + (num * ((1 - ratio) * 2));
    }
    
    public double dmgReductionProtocol(double num, boolean self)
    {
        if (!self || !this.passiveCanActivate())
        {
            return num;
        }
        
        //@Passive
        //Reduce damage based on current HP and whether Trevor has any debuffs
        double ratio = (1.0 * this.getCurrentHp() / this.getMaxHp());
        num -= (num * ((1 - ratio) * 0.75));
        
        return (!this.getAppliedDebuffs().isEmpty()) ? num * 0.8 : num;
    }
}
