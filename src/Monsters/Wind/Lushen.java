package Monsters.Wind;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import java.util.*;


public class Lushen extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
    private static int count = 1;
    
    
    public Lushen()
    {
        super("Lushen" + count, WIND, 9_225, 461, 900, 103, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile("Lushen1.csv", this));
        setAbilities();
        count++;
    }
    
    
    public Lushen(String fileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.UNRECOVERABLE, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Flying Cards (1)", 1.2 * 3.6, 0, 1, "Throws a sharp card to attack and disturbs the " +
                "enemy's HP recovery for 2 turns with a 90% chance.", ability1Debuffs, ability1DebuffChances, 0, false, false));
        
        abilities.add(new Attack_Ability("Surprise Box (2)", 1.25 * 2.4, 0, 1, "Summons a surprise box that inflicts damage" +
                " and grants 1 random weakening effect among Stun, Glancing Hit Rate Increase, and Attack Speed Decrease to all enemies.", 3, false,
                false));
        
        abilities.add(new Attack_Ability("Amputation Magic (3)", 1.3 * 0.68, 0, 3, "Throws a number of cards and inflicts " +
                "damage to all enemies, ignoring their Defense.", 4, true, false));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        Team other = (game.getOtherTeam().size() > 0) ? game.getOtherTeam() : Auto_Play.getOther();
        switch (abilityNum)
        {
            case 2 ->
            {
                for (int i = 0; i < other.size(); i++)
                {
                    Monster m = other.get(i);
                    if (!m.equals(target))
                    {
                        attack(m, abilities.get(1));
                    }
                    int random = new Random().nextInt(3);
                    switch (random)
                    {
                        case 0 -> m.addAppliedDebuff(new Debuff(Debuff.STUN, 1, 0), 100, this);
                        case 1 -> m.addAppliedDebuff(new Debuff(Debuff.GLANCING_HIT_UP, 1, 0), 100, this);
                        case 2 -> m.addAppliedDebuff(new Debuff(Debuff.DEC_ATK_SPD, 1, 0), 100, this);
                    }
                }
            }
            case 3 ->
            {
                for (int i = 0; i < other.size(); i++)
                {
                    Monster m = other.get(i);
                    if (m.equals(target))
                    {
                        continue;
                    }
                    attack(m, abilities.get(2));
                }
            }
        }
        
        super.afterTurnProtocol((abilityNum == 1) ? target : (game.getOtherTeam().size() > 0) ? game.getOtherTeam() : Auto_Play.getOther(), true);
        return true;
    }
}