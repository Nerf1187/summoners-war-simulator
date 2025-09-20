package Monsters.Water;

import Abilities.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Game.*;
import Monsters.*;
import Util.Util.*;
import java.util.*;

/**
 * Water Neostone Fighter
 */
public class Ryan extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Ryan()
    {
        this("Ryan1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Ryan(String runeFileName)
    {
        super("Ryan" + count, Element.WATER, 10_380, 549, 736, 107, 15, 50, 15, 0);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        
        ArrayList<Buff> ability1Buffs = MONSTERS.abilityBuffs(BuffEffect.ATK_UP.getNum(), 3);
        ArrayList<Integer> ability1BuffChances = MONSTERS.abilityChances(100);
        Ability a1 = new Attack_Ability("Combat Knife (1)", 1.35 * 1.9, 0, 2, "Swings a dagger to attack the enemy 2 times and " +
                                                                              "subsequently increases your Attack Power for 2 turns.", ability1Buffs, ability1BuffChances, 0, false, false, false, 0);
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(DebuffEffect.CONTINUOUS_DMG.getNum(), 2, 0);
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(100);
        Ability a2 = new Attack_Ability("Sharp Strike (2)", 1.3 * 5.6, 0, 1, "Attacks an enemy and inflicts Continuous Damage" +
                                                                             " for 2 turns. This attack will also decrease the enemy's Defense for 2 turns if it lands as a Critical Hit. Instantly gains another " +
                                                                             "turn if the target has 50% or less HP after the attack.", ability2Debuffs, ability2DebuffChances, 3, false, false, false);
        
        ArrayList<Debuff> ability3Debuffs = abilityDebuffs(DebuffEffect.UNRECOVERABLE.getNum(), 1, 0);
        ArrayList<Integer> ability3DebuffChances = MONSTERS.abilityChances(100);
        //12% increase in dmg in the 8.7.7 update
        Ability a3 = new Attack_Ability("Dagger Dash (3)", 1.25 * 3.2 * 1.12, 0, 3, "Attacks the enemy 3 times with attacks that always lands as Critical Hits to disturb " +
                                                                                    "HP recovery for 2 turns each. Increases your Attack Bar by the proportion of the enemy's HP lost from your attack", ability3Debuffs,
                ability3DebuffChances, 3, false, false, false);
        
        super.setAbilities(a1, a2, a3);
    }
    
    @Override
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //Save crit rate
        int critRate = getCritRate();
        
        //Force ability 3 to crit
        if (abilityNum == 3)
        {
            setCritRate(999_999);
        }
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            setCritRate(critRate);
            return false;
        }
        switch (abilityNum)
        {
            case 2 ->
            {
                //Decrease target's defense if they were crit
                if (target.wasCrit())
                {
                    target.addAppliedDebuff(DebuffEffect.DEC_DEF, 2, 200, this);
                }
                //Gain another turn if the target is under half health
                if (target.getHpRatio() <= 50)
                {
                    setAtkBar(2_000);
                    if (isPrint())
                    {
                        System.out.println(ConsoleColor.GREEN + "Extra Turn!" + ConsoleColor.RESET);
                    }
                }
            }
            case 3 ->
            {
                //Increase attack bar based on how much damage was dealt
                double dmgPercent = getDmgDealtThisTurn() / target.getMaxHp() * 100;
                setAtkBar((int) (getAtkBar() + dmgPercent * 10));
            }
        }
        //Reset crit rate
        setCritRate(critRate);
        super.afterTurnProtocol(target, true);
        return true;
    }
}
