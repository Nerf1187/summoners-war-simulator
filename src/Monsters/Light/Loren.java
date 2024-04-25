package Monsters.Light;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.*;
import Stats.Debuffs.*;
import java.util.*;

public class Loren extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    public Loren()
    {
        super("Loren" + count, LIGHT, 9_225, 516, 681, 101, 15, 50, 15, 25);
        super.setRunes(MonsterRunes.getRunesFromFile("Loren1.csv", this));
        setAbilities();
        count++;
    }
    
    public Loren(String fileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(fileName, this));
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.DEC_ATK_SPD, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(50);
        abilities.add(new Attack_Ability("Cross Fire (1)", 1.25 * 1.25, 0, 3, "Shoots 3 projectiles, with each projectile having a 30% chance to slow the Attack Speed for 2 turns.", ability1Debuffs,
                ability1DebuffChances, 0, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.REMOVE_BENEFICIAL_EFFECT, 1, 1, Debuff.UNRECOVERABLE, 2, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(75, 100);
        abilities.add(new Attack_Ability("Silver-Plated Bullet (2)", 3.0 * 1.25, 0, 2, "Attacks the enemy 2 times with holy bullets. Each attack has a 60% chance to remove one beneficial effect and disturb the HP recovery for 2 turns.",
                ability2Debuffs, ability2DebuffChances, 3, false, false));
        
        //@Passive: Creation
        abilities.add(new Passive("Chaser", "Decreases the enemy's Attack Bar by 20% with each attack and weakens the enemy's defense for 1 turn with a 75% chance."));
        
        abilities.add(new Leader_Skill(Stat.ATK, 0.18, Monster.ALL));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        super.afterTurnProtocol(target, true);
        return true;
    }
    
    public void selfAfterHitProtocol(Monster target)
    {
        //@Passive
        if (!containsDebuff(Debuff.OBLIVION))
        {
            target.addAppliedDebuff(new DecAtkBar(20), 100, this);
            target.addAppliedDebuff(Debuff.DEC_DEF, 75, 1, this);
        }
        
        super.selfAfterHitProtocol(target);
    }
}