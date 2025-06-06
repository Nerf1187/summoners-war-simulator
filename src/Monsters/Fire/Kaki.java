package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;


/**
 * Fire Onimusha
 */
public class Kaki extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Kaki()
    {
        this("Kaki1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Kaki(String runeFileName)
    {
        super("Kaki" + count, Element.FIRE, 8_400, 604, 812, 115, 15, 50, 15, 0);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
        
        //@Passive
        //Increase attack defense on attack
        setDef((int) Math.ceil((getDef() + getAtk() * 0.2)));
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.DEC_DEF.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(50);
        
        Ability a1 = new Attack_Ability("Ghost Slash (1)", 1.3 * 3.5, 0.3, 2, "Attacks the enemy 2 times, recovering HP by 30% " +
                                                                               "of the inflicted damage and decreasing the target's Defense for 2 turns with a 50% chance each.", ability1Debuffs,
                ability1DebuffChances, 0, false, false, false);
        
        Ability a2 = new Attack_Ability("Blade Slaughter (2)", 1.3 * 12.5, 0, 1, "Attacks all enemies and grants Endure effect " +
                                                                                  "on yourself for 1 turn if an enemy gets defeated.", 3, false, false, true);
        
        //@Passive:Creation
        Ability a3 = new Passive("Forestall", "Increases your Defense by 20% of your Attack Power when the battle begins, and inflicts additional " +
                                               "damage proportional to your Defense when you attack on your turn. Critical Hits won’t occur when attacking the enemy");
        
        super.setAbilities(a1, a2, a3);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        int critRate = getCritRate();
        int atk = getAtk();
        
        //@Passive
        //Make sure Kaki does not crit
        //Increase damage done based on defense
        if (this.passiveCanActivate())
        {
            setCritRate(-999_999);
            setAtk(atk + (int) (getDef() * 0.2));
        }
        
        Team other = game.getOtherTeam();
        int numDeadBefore = other.numDead();
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            setCritRate(critRate);
            setAtk(atk);
            return false;
        }
        if (abilityNum == 2)
        {
            //Add Endure effect if the ability killed an enemy
            int numDeadAfter = other.numDead();
            
            if (numDeadAfter > numDeadBefore && !this.containsDebuff(DebuffEffect.BLOCK_BENEFICIAL_EFFECTS))
            {
                this.addAppliedBuff(BuffEffect.ENDURE, 1, this);
            }
        }
        
        super.afterTurnProtocol((abilityNum == 1) ? target : game.getOtherTeam(), true);
        //Reset crit rate and attack
        setCritRate(critRate);
        setAtk(atk);
        return true;
    }
}
