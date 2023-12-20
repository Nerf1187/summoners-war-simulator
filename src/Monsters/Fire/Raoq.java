package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import java.util.*;


public class Raoq extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    
    public Raoq()
    {
        super("Raoq" + count, FIRE, 9_720, 582, 801, 108, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile("Raoq1.csv", this));
        setAbilities();
        count++;
    }
    
    
    public Raoq(String fileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.DEC_DEF, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(80);
        abilities.add(new Attack_Ability("Scratch (1)", 4.5 * 1.2, 0, 1, "Attacks with giant claws to weaken the enemy's defense for 2 turns with a 70% " +
                "chance and attacks consecutively with a 30% chance.", ability1Debuffs, ability1DebuffChances, 0, false, false));
        
        abilities.add(new Attack_Ability("Team Up (2)", 4.5 * 1.2, 0, 1, "Teams up with two other allies to attack an enemy.", 3, false, false));
        
        //@Passive:Creation
        abilities.add(new Passive("Annihilate", "If you kill the enemy, you will get an extra turn instantly and your skill cooldown time will decrease " +
                "by 1 turn."));
        
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
            do
            {
                attack(target, abilities.get(0));
            }
            while (new Random().nextInt(101) < 30);
        }
        
        if (abilityNum == 2)
        {
            Team friendlyTeam = (game.getNextMonsTeam().size() > 0) ? game.getNextMonsTeam() : Auto_Play.getHighestAtkBar();
            Monster attackingMon1 = null;
            do
            {
                if (friendlyTeam.numOfAliveMons() == 1)
                {
                    break;
                }
                attackingMon1 = friendlyTeam.getRandomMon();
            }
            while (attackingMon1 instanceof Raoq && friendlyTeam.numOfAliveMons() > 1);
            
            Monster attackingMon2 = null;
            do
            {
                if (attackingMon1 == null || friendlyTeam.numOfAliveMons() == 2)
                {
                    break;
                }
                attackingMon2 = friendlyTeam.getRandomMon();
            }
            while (attackingMon2 instanceof Raoq || attackingMon2.equals(attackingMon1) && (attackingMon1 != null && friendlyTeam.numOfAliveMons() > 2));
            
            if (attackingMon1 != null)
            {
                attackingMon1.attack(target, attackingMon1.getAbility(1));
            }
            if (attackingMon2 != null)
            {
                attackingMon2.attack(target, attackingMon2.getAbility(1));
            }
        }
        
        if (target.isDead())
        {
            if (isPrint())
            {
                System.out.println(ConsoleColors.GREEN + "Extra turn!" + ConsoleColors.RESET);
            }
            setAtkBar(2_000);
            abilities.get(2).decCooldown();
        }
        super.afterTurnProtocol(target, true);
        return true;
    }
}
