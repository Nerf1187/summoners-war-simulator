package Monsters.Light;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import java.util.*;


public class Figaro extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    
    public Figaro()
    {
        super("Figaro" + count, LIGHT, 11_700, 494, 703, 103, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile("Figaro1.csv", this));
        setAbilities();
        count++;
    }
    
    
    public Figaro(String fileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.UNRECOVERABLE, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Flying Cards (1)", 3.6 * 1.2, 0, 1, "Throws a sharp card to " +
                "attack and disturbs the enemy's HP recovery for 2 turns with a 70% chance.", ability1Debuffs, ability1DebuffChances, 0,
                false, false, false));
        
        abilities.add(new Attack_Ability("Surprise Box (2)", 1.25 * 2.4, 0, 1, "Summons a surprise box that inflicts damage" +
                " and grants 1 random weakening effect among Stun, Glancing Hit Rate Increase, and Attack Speed Decrease to all enemies.", 3, false,
                false, true));
        
        //@Passive:Creation
        abilities.add(new Passive("Camouflage", "Removes 1 beneficial effect from the targeted enemy, and installs a bomb for 2 turns with " +
                "a 25% chance every time you perform an attack. In addition, cancels incoming damage with a 25% chance."));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        if (abilityNum == 2)
        {
            applyToTeam(game.getOtherTeam(), m -> {
                int random = new Random().nextInt(3);
                switch (random)
                {
                    case 0 -> m.addAppliedDebuff(Debuff.STUN, 100, 1, this);
                    case 1 -> m.addAppliedDebuff(Debuff.GLANCING_HIT_UP, 100, 1, this);
                    case 2 -> m.addAppliedDebuff(Debuff.DEC_ATK_SPD, 100, 1, this);
                }
            });
        }
        
        //@Passive
        if (new Random().nextInt(101) <= 25 && !containsDebuff(Debuff.OBLIVION))
        {
            applyToTeam(game.getOtherTeam(), m -> {
                m.removeRandomBuff();
                m.addAppliedDebuff(Debuff.BOMB, 100, 2, this);
            });
        }
        
        super.afterTurnProtocol((abilityNum == 1) ? target : game.getOtherTeam(), true);
        return true;
    }
    
    public double dmgReductionProtocol(double num, boolean self)
    {
        if (!self)
        {
            return num;
        }
        //@Passive
        return ((new Random().nextInt(101)) <= 25 && !containsDebuff(Debuff.OBLIVION)) ? 0 : num;
    }
}
