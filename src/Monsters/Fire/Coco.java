package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

/**
 * Fire Mage
 */
public class Coco extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    Stat numOfMagicSpheres = new Stat(999_999);
    
    Ability magicSphereAttack = new Attack_Ability("", 1.5, 0, 1, "", 0, false, false, false);
    
    /**
     * Creates the Monster with the default rune set
     */
    public Coco()
    {
        this("Coco1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Coco(String runeFileName)
    {
        super("Coco" + count, FIRE, 10_710, 615, 812, 101, 15, 50, 40, 0);
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
        setAbilities();
        //Magic Spheres count
        numOfMagicSpheres.setStatNum(Stat.MAGIC_SPHERE);
        numOfMagicSpheres.setNumOfSpecialEffects(0);
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.DEC_ATK, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(65);
        abilities.add(new Attack_Ability("Magical Eye (1)", 1.9 * 1.15, 0, 2, "Attacks the enemy 2 times " +
                                                                              "and decreases the Attack Power for 2 turns with a 35% chance with each attack.", ability1Debuffs, ability1DebuffChances, 0, false, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.BRAND, 2, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(80);
        abilities.add(new Attack_Ability("Night Crow (2)", 2.3 * 1.25, 0, 3, "Attacks the enemy 3 times " +
                                                                             "and leaves a Branding effect for 2 turns with a 75% chance each. The cooldown time of [Chaos Magic Circle] will be decreased by 1 turn " +
                                                                             "each whenever you attack the target under harmful effects.", ability2Debuffs, ability2DebuffChances, 3, false, false, false));
        
        abilities.add(new Ability("Chaos Magic Circle (3)", 0, 0, 0, "Summons 5 magic spheres, which " +
                                                                     "each can inflict damage equal to 150% of your Attack Power. If you get attacked from an enemy while you still have magic spheres, uses " +
                                                                     "1 magic sphere to counterattack and increases your Attack Bar by 50%. When you attack on your turn, all remaining spheres will attack the" +
                                                                     " enemy target together. Recovers your HP by 10% for every sphere that counterattacks", 5, false, false, false, true, false, false));
        
        abilities.add(new Leader_Skill(Stat.ATK, 0.44, ALL));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        //use up magic spheres on attack
        for (int i = 0; i < numOfMagicSpheres.getNumOfSpecialEffects(); i++)
        {
            attack(target, magicSphereAttack);
        }
        numOfMagicSpheres.setNumOfSpecialEffects(0);
        
        //Add magic spheres
        if (abilityNum == 3)
        {
            numOfMagicSpheres.setNumOfSpecialEffects(5);
        }
        afterTurnProtocol(target, abilityNum != 3);
        return true;
    }
    
    public void attacked(Monster attacker)
    {
        if (numOfMagicSpheres.getNumOfSpecialEffects() <= 0 || !Game.canCounter())
        {
            return;
        }
        //Counter with a magic sphere when attacked
        Game.setCanCounter(false);
        attack(attacker, magicSphereAttack, true);
        //Heal self for counterattack
        if (canHeal())
        {
            setCurrentHp((int) Math.ceil((getCurrentHp() + getMaxHp() * 0.1)));
        }
        numOfMagicSpheres.setNumOfSpecialEffects(numOfMagicSpheres.getNumOfSpecialEffects() - 1);
        super.attacked(attacker);
    }
    
    public void attack(Monster target, Ability ability, boolean isCounter)
    {
        //Decrease ability 3 cooldown if the enemy has a debuff
        if (abilities.indexOf(ability) == 1)
        {
            if (!target.getAppliedDebuffs().isEmpty())
            {
                for (int i = 0; i < 3; i++)
                {
                    abilities.get(2).decCooldown();
                }
            }
        }
        super.attack(target, ability, isCounter);
    }
    
    public void reset()
    {
        //Remove magic spheres on reset
        numOfMagicSpheres.setNumOfSpecialEffects(0);
        numOfMagicSpheres.setNumTurns(999_999);
        super.reset();
    }
    
    public void kill()
    {
        super.kill();
        if (this.isDead())
        {
            numOfMagicSpheres.setNumOfSpecialEffects(0);
            numOfMagicSpheres.setNumTurns(999_999);
        }
    }
    
    public Monster copy()
    {
        Coco save = (Coco) super.copy();
        Stat s = new Stat(this.numOfMagicSpheres.getNumTurns());
        
        s.setStatNum(Stat.MAGIC_SPHERE);
        s.setNumOfSpecialEffects(this.numOfMagicSpheres.getNumOfSpecialEffects());
        
        save.numOfMagicSpheres = s;
        return save;
    }
    
    public void paste(Monster save)
    {
        if (!(save instanceof Coco))
        {
            return;
        }
        
        super.paste(save);
        this.numOfMagicSpheres = ((Coco) save).numOfMagicSpheres;
    }
}