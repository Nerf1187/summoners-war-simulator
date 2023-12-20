package Monsters.Fire;//Create nextTurn()

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;


public class Kaki extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
    private static int count = 1;
    
    
    public Kaki()
    {
        super("Kaki" + count, FIRE, 8_400, 604, 812, 115, 15, 50, 15, 0);
        setRunes(MonsterRunes.getRunesFromFile("Kaki2.csv", this));
        setAbilities();
        count++;
        
        //@Passive
        setDef((int) Math.ceil((getDef() + getAtk() * 0.2)));
    }
    
    
    public Kaki(String fileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.DEC_DEF, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(50);
        
        abilities.add(new Attack_Ability("Ghost Slash (1)", 1.3 * 3.5, 0.3, 2, "Attacks the enemy 2 times, recovering HP by 30% " +
                "of the inflicted damage and decreasing the target's Defense for 2 turns with a 50% chance each.", ability1Debuffs,
                ability1DebuffChances, 0,
                false, false));
        
        abilities.add(new Attack_Ability("Blade Slaughter (2)", 1.3 * 12.5, 0, 1, "Attacks all enemies and grants Endure effect " +
                "on yourself for 1 turn if an enemy gets defeated.", 3, false, false));
        
        //@Passive:Creation
        abilities.add(new Passive("Forestall", "Increases your Defense by 20% of your Attack Power when the battle begins, and inflicts additional " +
                "damage " +
                "proportional to your Defense when you attack on your turn. Critical Hits won’t occur when attacking the enemy"));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        int critRate = getCritRate();
        int atk = getAtk();
        
        //@Passive
        if (!containsDebuff(new Debuff(Debuff.OBLIVION, 1, 0)))
        {
            setCritRate(-9999);
            setAtk(atk + (int) (getDef() * 0.2));
        }
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            setCritRate(critRate);
            setAtk(atk);
            return false;
        }
        if (abilityNum == 2)
        {
            Team other = (game.getOtherTeam().size() > 0) ? game.getOtherTeam() : Auto_Play.getOther();
            for (int i = 0; i < other.size(); i++)
            {
                Monster m = other.get(i);
                if (!m.equals(target) && !m.isDead())
                {
                    attack(m, abilities.get(1), false);
                    if (m.isDead() || target.isDead())
                    {
                        addAppliedBuff(Buff.ENDURE, 2, this);
                    }
                }
            }
        }
        
        super.afterTurnProtocol((abilityNum == 1) ? target : (game.getOtherTeam().size() > 0) ? game.getOtherTeam() : Auto_Play.getOther(), true);
        setCritRate(critRate);
        setAtk(atk);
        return true;
    }
}
