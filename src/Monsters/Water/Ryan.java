package Monsters.Water;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;


public class Ryan extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    
    public Ryan()
    {
        super("Ryan" + count, WATER, 10_380, 549, 736, 107, 15, 50, 15, 0);
        setRunes(MonsterRunes.getRunesFromFile("Ryan1.csv", this));
        setAbilities();
        count++;
    }
    
    
    public Ryan(String fileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    private void setAbilities()
    {
        
        ArrayList<Buff> ability1Buffs = abilityBuffs(Buff.ATK_UP, 3);
        ArrayList<Integer> ability1BuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Combat Knife (1)", 1.35 * 1.9, 0, 2, "Swings a dagger to attack the enemy 2 times and " +
                "subsequently increases your Attack Power for 2 turns.", ability1Buffs, ability1BuffChances, 0, false, false, 0));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.CONTINUOUS_DMG, 2, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Sharp Strike (2)", 1.3 * 5.6, 0, 1, "Attacks an enemy and inflicts Continuous Damage" +
                " for 2 turns. This attack will also decrease the enemy's Defense for 2 turns if it lands as a Critical Hit. Instantly gains another " +
                "turn if the target has 50%" +
                " or less HP after the attack.", ability2Debuffs, ability2DebuffChances, 3, false, false));
        
        ArrayList<Debuff> ability3Debuffs = abilityDebuffs(Debuff.CONTINUOUS_DMG, 1, 0);
        ArrayList<Integer> ability3DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Dagger Dash (3)", 1.25 * 3.2, 0, 3, "Attacks the enemy with an attack that is " +
                "guaranteed to land as a Critical Hit 3 times, each attack inflicting Continuous Damage for 1 turn. Increases your Attack Bar " +
                "proportionate to the enemy's lost" +
                " HP from your attack.", ability3Debuffs, ability3DebuffChances, 3, false, false));
        
        super.setAbilities(abilities);
    }
    
    @Override
    public boolean nextTurn(Monster target, int abilityNum)
    {
        int critRate = getCritRate();
        if (abilityNum == 3)
        {
            setCritRate(999);
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
                if (target.wasCrit())
                {
                    target.addAppliedDebuff(new Debuff(Debuff.DEC_DEF, 2, 0), 100, this);
                }
                if (target.getHpRatio() <= 50)
                {
                    setAtkBar(2_000);
                    if (isPrint())
                    {
                        System.out.println(ConsoleColors.GREEN + "Extra Turn!" + ConsoleColors.RESET);
                    }
                }
            }
            case 3 ->
            {
                double dmgPercent = getDmgDealtThisTurn() / target.getMaxHp() * 100;
                setAtkBar((int) (getAtkBar() + dmgPercent * 10));
            }
        }
        super.afterTurnProtocol(target, true);
        setCritRate(critRate);
        return true;
    }
}
