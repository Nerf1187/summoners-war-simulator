package Game;

import Abilities.*;
import Monsters.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static Monsters.Monster.stringIsMonsterName;
import static Monsters.Monster.toProperName;

/**
 * This class runs multiple simulations such that every possible team combination fights against every other team.
 *
 * @author Anthony (Tony) Youssef
 */
public class Auto_Play extends Thread
{
    private static final Scanner scan = new Scanner(System.in);
    private static int team1Wins = 0, team2Wins = 0;
    private static Game game;
    private static boolean stunned = false, pause = false;
    private static Team highestAtkBar = new Team("", new ArrayList<>()), other = new Team("", new ArrayList<>());
    private static final HashMap<Team, ArrayList<Integer>> teamStats = new HashMap<>();
    private static final ArrayList<Team> bestTeams = new ArrayList<>();
    private static int numOfCompletedSimulations = 0;
    private static Instant start;
    
    //TODO: Add stopwatch to get estimated time remaining
    //private static Stopwatch stopWatch;
    
    /**
     * Runs the Auto_Play class
     */
    
    public static void main(String[] args)
    {
        Monster.setGame(game);
        Monster.setDatabase();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Instant end = Instant.now();
            long timeElapsed = Duration.between(start, end).toMillis();
            
            System.out.println("\n\nTotal number of simulations ran: " + numOfCompletedSimulations);
            System.out.println("Time elapsed: \t" + toReadableTime(timeElapsed));
            System.out.println("Final standings:");
            for (Team team : bestTeams)
            {
                for (Monster mon : team.getMonsters())
                {
                    System.out.printf("%s\t\t", mon.getName(true, false));
                }
                System.out.println("number of wins: " + teamStats.get(team).get(0) + " Number of losses: " + teamStats.get(team).get(1));
            }
            System.out.println("\n\n");
        }));
        
        Auto_Play thread = new Auto_Play();
        thread.start();
        
        for (int k = 0; k < 4; k++)
        {
            bestTeams.add(null);
        }
        
        Monster.setPrint(false);
        
        allPossibleTeams();
    }
    
    /**
     * Runs and pauses the program according to user input
     */
    public void run()
    {
        Scanner scan = new Scanner(System.in);
        while (true)
        {
            while (!pause)
            {
                scan.nextLine();
                pause = true;
            }
            while (pause)
            {
                Main.pause(5);
            }
        }
    }
    
    
    /**
     * Runs a simulated battle with the provided teams
     *
     * @param team1 The first team in the battle
     * @param team2 The second team in the battle
     * @param count The number of times the battle has been run
     * @return the winner if there is one, null otherwise
     */
    public static Team battle(Team team1, Team team2, int count)
    {
        //Create game
        game = new Game(team1, team2);
        Monster.setGame(game);
        int turnNumber = 1;
        HashMap<Monster, Integer> deadMons = new HashMap<>();
        
        while (!game.endGame())
        {
            //Increment attack bars until at least one is full
            if (!game.hasFullAtkBar())
            {
                game.increaseAtkBar();
                continue;
            }
            
            //Print game and find who is next
            //System.out.println("\n\nTURN NUMBER: " + turnNumber + "\n GAME NUMBER: " + (count + 1) + "\n" + game + "\n\n");
            highestAtkBar = game.getTeamWithHighestAtkBar();
            other = game.getOtherTeam();
            Monster next = highestAtkBar.MonsterWithHighestFullAtkBar();
            //System.out.println(next);
            
            //Activate before turn passives
            game.activateBeforeTurnPassives(next);
            
            game.applyStats(next);
            
            //If the monster's Hp falls below zero before turn
            if (next.getCurrentHp() <= 0)
            {
                next.kill();
            }
            
            //Print next mon
            //System.out.println("NEXT: " + next.shortToString(true) + "\n\n");
            
            //Check if stunned
            
            if (stunned)
            {
                stunned = false;
                next.decreaseStatCooldowns();
                next.setAtkBar(0);
                continue;
            }
            
            if (next.getCurrentHp() <= 0)
            {
                next.kill();
                if (next.isDead())
                {
                    next.setAtkBar(-999);
                    continue;
                }
            }
            
            //Ends game if a team is dead
            if (game.endGame())
            {
                break;
            }
            
            //Check for Provoke
            Provoke p = next.getProvoke();
            if (p != null)
            {
                //System.out.println(ConsoleColors.YELLOW + "Provoked!" + ConsoleColors.RESET);
                Monster caster = p.getCaster();
                next.nextTurn(caster, 1);
                continue;
            }
            
            //Checks if any Monster on the other team has Threat
            boolean threat = other.monHasThreat();
            
            //Get abilityNum and print
            int abilityNum = chooseAbilityNum(next, next.getAbilities(), true);
            //System.out.println("AbilityNum: " + (abilityNum + 1));
            
            //Get target and print
            chooseTargetAndApplyNextTurn(next, abilityNum, (next.getAbility(abilityNum).targetsEnemy() ? other : highestAtkBar), true);
            //System.out.println("TARGET: " + targetMon.shortToString(true) + "\n\n");
            
            //Apply nextTurn()
            //Main.applyNextTurn(next, targetMon, abilityNum + 1);
            for (Monster mon : other.getMonsters())
            {
                if (mon.isDead() && !deadMons.containsKey(mon))
                {
                    deadMons.put(mon, turnNumber);
                }
            }
            
            turnNumber++;
            if (turnNumber >= 100)
            {
                break;
            }
        }
        
        if (team1.deadTeam())
        {
            team2Wins++;
        }
        else if (team2.deadTeam())
        {
            team1Wins++;
        }
        
        //Change this number to run the battle multiple times
        if (count < 0)
        {
            game.reset();
            Main.setRuneEffectsAndNames(team1, team2);
            battle(team1, team2, count + 1);
        }
        
        if (team1Wins > team2Wins)
        {
            return team1;
        }
        if (team2Wins > team1Wins)
        {
            return team2;
        }
        return null;
    }
    
    /**
     * A recursive algorithm to choose the best ability for the Monster to use. Chooses support Ability if there is a Monster on the same Team with low
     * health or the Ability applies multiple buffs.
     *
     * @param next      The Monster to choose the Ability from
     * @param abilities The set of abilities that can be chosen
     * @param firstCall True if this call is the first time it is called (from outside the method)
     * @return the ability's number on the monster
     */
    public static int chooseAbilityNum(Monster next, ArrayList<Ability> abilities, boolean firstCall)
    {
        if (firstCall)
        {
            ArrayList<Integer> viableNums = next.getViableAbilityNumbers();
            ArrayList<Ability> modifiedAbilities = new ArrayList<>();
            for (int i = 0; i < abilities.size(); i++)
            {
                if (viableNums.contains(i) && abilities.get(i).getTurnsRemaining() <= 0 && !(abilities.get(i) instanceof Passive))
                {
                    modifiedAbilities.add(abilities.get(i));
                }
            }
            return chooseAbilityNum(next, modifiedAbilities, false);
        }
        int numOfLowHealthTeammates = 0;
        boolean teammateWithLowHealth = false;
        boolean multipleTeammatesWithLowHealth;
        for (Monster mon : highestAtkBar.getMonsters())
        {
            if (mon.equals(next))
            {
                continue;
            }
            if (mon.getHpRatio() <= 50.0)
            {
                numOfLowHealthTeammates++;
                teammateWithLowHealth = true;
            }
        }
        multipleTeammatesWithLowHealth = numOfLowHealthTeammates >= 2;
        
        //Choose a team support ability
        if (teammateWithLowHealth && next.hasTeamSupportAbility())
        {
            ArrayList<Heal_Ability> supportAbilities = new ArrayList<>();
            for (Ability ability : abilities)
            {
                if (ability instanceof Heal_Ability && next.getTeamSupportAbilities().contains((Heal_Ability) ability))
                {
                    supportAbilities.add((Heal_Ability) ability);
                }
            }
            Collections.reverse(supportAbilities);
            for (int i = supportAbilities.size() - 1; i >= 0; i--)
            {
                Heal_Ability ability = supportAbilities.get(i);
                if (ability.getTurnsRemaining() > 0)
                {
                    supportAbilities.remove(ability);
                    continue;
                }
                if (ability.targetsMultipleMonsters() && !multipleTeammatesWithLowHealth && supportAbilities.size() > 1)
                {
                    supportAbilities.remove(ability);
                }
            }
            Collections.reverse(supportAbilities);
            if (!supportAbilities.isEmpty())
            {
                Ability a = supportAbilities.get(supportAbilities.size() - 1);
                if (!next.abilityIsValid(a))
                {
                    ArrayList<Ability> modifiedAbilities = new ArrayList<>();
                    for (Ability ab : supportAbilities)
                    {
                        if (!ab.equals(a))
                        {
                            modifiedAbilities.add(ab);
                        }
                    }
                    return chooseAbilityNum(next, modifiedAbilities, false);
                }
                return next.getAbilities().indexOf(a) + 1;
            }
        }
        
        //Choose a self-support ability
        if (next.hasSelfSupportAbility())
        {
            ArrayList<Ability> selfAbilities = new ArrayList<>();
            for (Ability value : abilities)
            {
                if (next.getSelfSupportAbilities().contains(value))
                {
                    selfAbilities.add(value);
                }
            }
            for (int i = selfAbilities.size() - 1; i >= 0; i--)
            {
                Ability ability = selfAbilities.get(i);
                if (ability.getTurnsRemaining() > 0 || ability instanceof Passive)
                {
                    selfAbilities.remove(ability);
                }
            }
            
            if (!selfAbilities.isEmpty())
            {
                Ability a = selfAbilities.get(selfAbilities.size() - 1);
                if (!next.abilityIsValid(a))
                {
                    ArrayList<Ability> modifiedAbilities = new ArrayList<>();
                    for (Ability ab : selfAbilities)
                    {
                        if (!ab.equals(a))
                        {
                            modifiedAbilities.add(ab);
                        }
                    }
                    return chooseAbilityNum(next, modifiedAbilities, false);
                }
                return next.getAbilities().indexOf(a) + 1;
            }
        }
        
        //Choose heal ability with multiple buffs
        if (next.hasSupportAbilityWithMultipleBuffs())
        {
            ArrayList<Ability> supportAbilities = new ArrayList<>();
            for (Ability value : abilities)
            {
                if (next.getSupportAbilitiesWithMultipleBuffs().contains(value))
                {
                    supportAbilities.add(value);
                }
            }
            
            for (int i = supportAbilities.size() - 1; i >= 0; i--)
            {
                Ability ability = supportAbilities.get(i);
                if (ability.getTurnsRemaining() > 0 || ability instanceof Passive)
                {
                    supportAbilities.remove(ability);
                }
            }
            
            if (!supportAbilities.isEmpty())
            {
                Ability a = supportAbilities.get(supportAbilities.size() - 1);
                if (!next.abilityIsValid(a))
                {
                    ArrayList<Ability> modifiedAbilities = new ArrayList<>();
                    for (Ability ab : supportAbilities)
                    {
                        if (!ab.equals(a))
                        {
                            modifiedAbilities.add(ab);
                        }
                    }
                    return chooseAbilityNum(next, modifiedAbilities, false);
                }
                return next.getAbilities().indexOf(a) + 1;
            }
        }
        
        //Choose attack ability
        ArrayList<Ability> otherAbilities = new ArrayList<>();
        for (Ability value : abilities)
        {
            if (next.getAttackAbilities().contains(value))
            {
                otherAbilities.add(value);
            }
        }
        for (int i = otherAbilities.size() - 1; i >= 0; i--)
        {
            Ability ability = otherAbilities.get(i);
            if (ability.getTurnsRemaining() > 0 || ability instanceof Heal_Ability || ability instanceof Passive)
            {
                otherAbilities.remove(ability);
            }
        }
        
        if (!otherAbilities.isEmpty())
        {
            Ability a = otherAbilities.get(otherAbilities.size() - 1);
            if (!next.abilityIsValid(a))
            {
                ArrayList<Ability> modifiedAbilities = new ArrayList<>();
                for (Ability ab : otherAbilities)
                {
                    if (!ab.equals(a))
                    {
                        modifiedAbilities.add(ab);
                    }
                }
                return chooseAbilityNum(next, modifiedAbilities, false);
            }
            return next.getAbilities().indexOf(a) + 1;
        }
        
        //Base case, returns the Monsters basic ability
        return 1;
    }
    
    /**
     * A recursive algorithm to choose the best target to attack/heal and apply the {@link Monster#nextTurn(Monster, int)} function. The order of attack
     * significance: low health targets, advantageous attributes, neutral attributes, disadvantageous attributes, damage taken increasing debuffs.
     *
     * @param next             The attacking/healing Monster
     * @param abilityNum       The ability number to use
     * @param potentialTargets A temporary team of Monsters that contain the potential targets
     * @param firstCall        True if this call is the first time it is called (From outside the method)
     */
    public static void chooseTargetAndApplyNextTurn(Monster next, int abilityNum, Team potentialTargets, boolean firstCall)
    {
        if (firstCall)
        {
            ArrayList<Monster> modifiedTeam = new ArrayList<>();
            for (Monster mon : potentialTargets.getMonsters())
            {
                if (!mon.isDead())
                {
                    modifiedTeam.add(mon);
                }
            }
            chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
            return;
        }
        Team friendlyTeam = highestAtkBar;
        Ability chosenAbility = next.getAbility(abilityNum);
        
        //Ability is a support ability
        if (chosenAbility instanceof Heal_Ability)
        {
            Monster target = friendlyTeam.getLowestHpMon();
            if (!next.targetIsValid(next, target, chosenAbility.targetsEnemy()))
            {
                
                ArrayList<Monster> modifiedTeam = new ArrayList<>();
                for (Monster potentialTarget : potentialTargets.getMonsters())
                {
                    if (!potentialTarget.equals(target))
                    {
                        modifiedTeam.add(potentialTarget);
                    }
                }
                chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
                return;
            }
            if (!next.nextTurn(target, abilityNum))
            {
                ArrayList<Monster> modifiedTeam = new ArrayList<>();
                for (Monster potentialTarget : potentialTargets.getMonsters())
                {
                    if (!potentialTarget.equals(target))
                    {
                        modifiedTeam.add(potentialTarget);
                    }
                }
                chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
            }
            return;
        }
        
        //Ability targetsSelf
        if (chosenAbility.targetsSelf())
        {
            next.nextTurn(next, abilityNum);
            return;
        }
        
        ArrayList<Monster> advantageousElements = new ArrayList<>();
        ArrayList<Monster> neutralElements = new ArrayList<>();
        ArrayList<Monster> disadvantageousElements = new ArrayList<>();
        
        //Ability is an attack ability
        for (Monster target : potentialTargets.getMonsters())
        {
            if (!target.isDead() && target.getHpRatio() <= 20.0)
            {
                if (!next.targetIsValid(next, target, chosenAbility.targetsEnemy()))
                {
                    
                    ArrayList<Monster> modifiedTeam = new ArrayList<>();
                    for (Monster potentialTarget : potentialTargets.getMonsters())
                    {
                        if (!potentialTarget.equals(target))
                        {
                            modifiedTeam.add(potentialTarget);
                        }
                    }
                    chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
                    return;
                }
                if (!next.nextTurn(target, abilityNum))
                {
                    ArrayList<Monster> modifiedTeam = new ArrayList<>();
                    for (Monster potentialTarget : potentialTargets.getMonsters())
                    {
                        if (!potentialTarget.equals(target))
                        {
                            modifiedTeam.add(potentialTarget);
                        }
                    }
                    chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
                }
                return;
            }
            if (target.isDead())
            {
                continue;
            }
            if (Team.elementalRelationship(next.getElement(), target.getElement()).equals(ConsoleColors.GREEN_BACKGROUND))
            {
                advantageousElements.add(target);
            }
            else if (Team.elementalRelationship(next.getElement(), target.getElement()).equals(ConsoleColors.YELLOW_BACKGROUND))
            {
                neutralElements.add(target);
            }
            else
            {
                disadvantageousElements.add(target);
            }
        }
        
        if (!advantageousElements.isEmpty())
        {
            if (getNewTarget(next, abilityNum, potentialTargets, chosenAbility, advantageousElements))
            {
                return;
            }
        }
        else if (!neutralElements.isEmpty())
        {
            if (getNewTarget(next, abilityNum, potentialTargets, chosenAbility, neutralElements))
            {
                return;
            }
        }
        else if (!disadvantageousElements.isEmpty())
        {
            if (getNewTarget(next, abilityNum, potentialTargets, chosenAbility, disadvantageousElements))
            {
                return;
            }
        }
        
        for (Monster target : other.getMonsters())
        {
            if (next.targetIsValid(next, target, chosenAbility.targetsEnemy()))
            {
                if (!next.nextTurn(target, abilityNum))
                {
                    ArrayList<Monster> modifiedTeam = new ArrayList<>();
                    for (Monster potentialTarget : potentialTargets.getMonsters())
                    {
                        if (!potentialTarget.equals(target))
                        {
                            modifiedTeam.add(potentialTarget);
                        }
                    }
                    chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
                }
                return;
            }
        }
    }
    
    private static boolean getNewTarget(Monster next, int abilityNum, Team potentialTargets, Ability chosenAbility, ArrayList<Monster> allMons)
    {
        Monster target = getBestMonToAttack(allMons);
        if (!next.targetIsValid(next, target, chosenAbility.targetsEnemy()))
        {
            
            ArrayList<Monster> modifiedTeam = new ArrayList<>();
            for (Monster potentialTarget : potentialTargets.getMonsters())
            {
                if (!potentialTarget.equals(target))
                {
                    modifiedTeam.add(potentialTarget);
                }
            }
            chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
            return false;
        }
        if (!next.nextTurn(target, abilityNum))
        {
            ArrayList<Monster> modifiedTeam = new ArrayList<>();
            for (Monster potentialTarget : potentialTargets.getMonsters())
            {
                if (!potentialTarget.equals(target))
                {
                    modifiedTeam.add(potentialTarget);
                }
            }
            chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
        }
        return true;
    }
    
    /**
     * A method to find the Monster with the most damage taken increasing effects
     *
     * @param monsters The Monsters to search through
     * @return the Monster with the most damage taken increasing effects in the list
     */
    public static Monster getBestMonToAttack(ArrayList<Monster> monsters)
    {
        Monster target = null;
        for (Monster mon : monsters)
        {
            if (!mon.isDead())
            {
                target = mon;
                break;
            }
        }
        if (target == null)
        {
            return null;
        }
        double highestPoints = Double.MIN_VALUE;
        double currentPoints;
        for (Monster mon : monsters)
        {
            currentPoints = 0;
            if (mon.isDead())
            {
                continue;
            }
            ArrayList<Buff> targetBuffs = mon.getAppliedBuffs();
            for (Buff buff : targetBuffs)
            {
                switch (buff.getBuffNum())
                {
                    case Buff.CRIT_RESIST_UP, Buff.COUNTER, Buff.IMMUNITY, Buff.REFLECT, Buff.SHIELD -> currentPoints -= 1;
                    case Buff.DEF_UP -> currentPoints -= 2;
                    case Buff.ENDURE, Buff.SOUL_PROTECTION, Buff.DEFEND -> currentPoints -= 3;
                    case Buff.INVINCIBILITY -> currentPoints -= 4;
                }
            }
            ArrayList<Debuff> targetDebuffs = mon.getAppliedDebuffs();
            for (Debuff debuff : targetDebuffs)
            {
                if (debuff.getDebuffNum() == Debuff.DEC_DEF || debuff.getDebuffNum() == Debuff.BRAND)
                {
                    currentPoints += 2;
                }
            }
            currentPoints /= (target.getHpRatio() * 1.15);
            
            if (currentPoints > highestPoints)
            {
                target = mon;
                highestPoints = currentPoints;
            }
            else if (currentPoints == highestPoints)
            {
                target = (mon.getHpRatio() < target.getHpRatio()) ? mon : target;
            }
        }
        return target;
    }
    
    /**
     * @return the Team without the next Monster.
     */
    public static Team getOther()
    {
        return other;
    }
    
    /**
     * @return the Team containing the next Monster
     */
    public static Team getHighestAtkBar()
    {
        return highestAtkBar;
    }
    
    /**
     * @return the Game object currently in use
     */
    public static Game getGame()
    {
        return game;
    }
    
    /**
     * A method to create every possible Team combination (ignores leader skills to decrease the number of combinations) and simulates each of their
     * battles
     */
    public static void allPossibleTeams()
    {
        ArrayList<Monster> allMons = new ArrayList<>();
        
        for (String name : Monster.monsterNamesDatabase.keySet())
        {
            String element = Monster.monsterNamesDatabase.get(name);
            name = name.replaceAll(" ", "_");
            try
            {
                Class<?> c = Class.forName("Monsters." + element + "." + name);
                allMons.add((Monster) c.getDeclaredConstructor().newInstance());
            }
            catch (Throwable e)
            {
                throw new RuntimeException(e);
            }
        }
        
        ArrayList<ArrayList<Monster>> allPossibleTeamMonsters = generateCombinations(allMons, 4);
        ArrayList<Team> allPossibleTeams = new ArrayList<>();
        int count = 0;
        for (ArrayList<Monster> list : allPossibleTeamMonsters)
        {
            Team team = new Team("Team" + count, list);
            allPossibleTeams.add(team);
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(0);
            temp.add(0);
            teamStats.put(team, temp);
            count++;
        }
        Collections.shuffle(allPossibleTeams);
        Team winner;
        
        System.out.println("Simulations started");
        start = Instant.now();
        for (int i = 0; i < allPossibleTeams.size(); i++)
        {
            winner = allPossibleTeams.get(i);
            for (int j = i + 1; j < allPossibleTeams.size(); j++)
            {
                
                int numOfSimsLeft = totalNumOfSims(allPossibleTeams.size()) - numOfCompletedSimulations;
                if (pause)
                {
                    System.out.println("Current standings:");
                    for (Team team : bestTeams)
                    {
                        for (Monster mon : team.getMonsters())
                        {
                            System.out.printf("%s\t\t", mon.getName(true, false));
                        }
                        System.out.println("number of wins: " + teamStats.get(team).get(0) + "\tNumber of losses: " + teamStats.get(team).get(1));
                    }
                    System.out.println("Number of simulations left: " + Monster.numWithCommas(numOfSimsLeft));
                    System.out.println("Press enter to continue or type \"inspect\" to inspect a specific team");
                    String response = scan.nextLine();
                    if (response.equalsIgnoreCase("inspect"))
                    {
                        Team inspectTeam = findTeamFromMonsters(new ArrayList<>());
                        if (inspectTeam != null)
                        {
                            for (Monster mon : inspectTeam.getMonsters())
                            {
                                System.out.printf("%s\t\t", mon.getName(true, false));
                            }
                            System.out.println("number of wins: " + teamStats.get(inspectTeam).get(0) + "\tNumber of losses: " + teamStats.get(inspectTeam).get(1));
                            System.out.println("Press enter to continue");
                        }
                        scan.nextLine();
                    }
                    
                    pause = false;
                    System.out.println("Running");
                }
                numOfCompletedSimulations++;
                System.gc();
                Team contender = allPossibleTeams.get(j);
                
                //Reset team
                ArrayList<Monster> monsters = winner.getMonsters();
                for (int k = 0; k < monsters.size(); k++)
                {
                    try
                    {
                        monsters.set(k, monsters.get(k).getClass().getDeclaredConstructor().newInstance());
                    }
                    catch (Throwable e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                ArrayList<Monster> monsters1 = contender.getMonsters();
                for (int k = 0; k < monsters1.size(); k++)
                {
                    try
                    {
                        monsters1.set(k, monsters1.get(k).getClass().getDeclaredConstructor().newInstance());
                    }
                    catch (Throwable e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                Main.setRuneEffectsAndNames(winner, contender);
                battle(winner, contender, 0);
                Team loser = contender;
                if (!game.getWinningTeam().getName().equals("Temp"))
                {
                    Team temp = winner;
                    winner = game.getWinningTeam();
                    if (winner.equals(temp))
                    {
                        loser = contender;
                    }
                    else
                    {
                        loser = temp;
                    }
                }
                
                Team finalWinner = winner;
                teamStats.forEach((key, value) -> {
                    if (key.equals(finalWinner))
                    {
                        ArrayList<Integer> temp = new ArrayList<>(teamStats.get(key));
                        temp.set(0, temp.get(0) + 1);
                        teamStats.put(key, temp);
                    }
                });
                
                Team finalLoser = loser;
                teamStats.forEach((key, value) -> {
                    if (key.equals(finalLoser))
                    {
                        ArrayList<Integer> temp = new ArrayList<>(teamStats.get(key));
                        temp.set(1, temp.get(1) + 1);
                        teamStats.put(key, temp);
                    }
                });
                
                AtomicInteger highest = new AtomicInteger(0);
                AtomicInteger second = new AtomicInteger(0);
                AtomicInteger third = new AtomicInteger(0);
                AtomicInteger fourth = new AtomicInteger(0);
                
                teamStats.forEach((key, value) -> {
                    if (value.get(0) > highest.get())
                    {
                        highest.set(value.get(0));
                        bestTeams.set(0, key);
                    }
                    else if (value.get(0) > second.get())
                    {
                        second.set(value.get(0));
                        bestTeams.set(1, key);
                    }
                    else if (value.get(0) > third.get())
                    {
                        third.set(value.get(0));
                        bestTeams.set(2, key);
                    }
                    else if (value.get(0) > fourth.get())
                    {
                        fourth.set(value.get(0));
                        bestTeams.set(3, key);
                    }
                });
            }
        }
    }
    
    /**
     * Generates all possible Team combinations
     *
     * @param monsters A list of Monsters to generate combinations from
     * @param r        The length of each combination
     * @return a list of lists of Monsters to set up the Teams
     * @author ChatGPT
     */
    public static ArrayList<ArrayList<Monster>> generateCombinations(ArrayList<Monster> monsters, int r)
    {
        ArrayList<ArrayList<Monster>> result = new ArrayList<>();
        ArrayList<Monster> currentCombination = new ArrayList<>();
        for (int i = 0; i < r; i++)
        {
            currentCombination.add(null);
        }
        generateCombinationsUtil(monsters, r, 0, 0, currentCombination, result);
        return result;
    }
    
    /**
     * A helper method to generate each combination. This method should only be called from {@link Auto_Play#generateCombinations(ArrayList, int)}
     *
     * @param monsters           A list of Monsters to generate the combinations from
     * @param r                  The length of each combination
     * @param index              The place in the list Monsters to start. This should be 0 on the first call
     * @param depth              The current place in the current combination to add the next Monster. This should be 0 on the first call
     * @param currentCombination The current combination being built. This should be a new ArrayList with a size of r on the first call
     * @param result             All combinations built already. This should be a new ArrayList on the first call.
     * @author ChatGPT
     */
    private static void generateCombinationsUtil(ArrayList<Monster> monsters, int r, int index, int depth, ArrayList<Monster> currentCombination,
            ArrayList<ArrayList<Monster>> result)
    {
        if (depth == r)
        {
            result.add(new ArrayList<>(currentCombination));
            return;
        }
        
        for (int i = index; i < monsters.size(); i++)
        {
            try
            {
                currentCombination.set(depth, monsters.get(i));
            }
            catch (Throwable e)
            {
                throw new RuntimeException(e);
            }
            generateCombinationsUtil(monsters, r, i + 1, depth + 1, currentCombination, result);
        }
    }
    
    /**
     * @param numOfCombos The number of combinations being tested
     * @return the total number of different simulations that can be run (equivalent to numOfCombos!)
     */
    public static int totalNumOfSims(int numOfCombos)
    {
        if (numOfCombos == 0)
        {
            return 0;
        }
        return numOfCombos + totalNumOfSims(numOfCombos - 1);
    }
    
    /**
     * Asks for four Monsters from the user and finds the Team that has all four.
     *
     * @param pickedMons The Monsters already picked. The first call should pass a new ArrayList.
     * @return The Team from {@link Auto_Play#teamStats} which contains the four Monsters provided.
     */
    public static Team findTeamFromMonsters(ArrayList<Monster> pickedMons)
    {
        String inputMon;
        do
        {
            System.out.println("Enter Monster " + (pickedMons.size() + 1) + "'s name");
            inputMon = scan.nextLine();
        }
        while (!stringIsMonsterName(inputMon) || Team.teamHasMon(inputMon, pickedMons));
        
        try
        {
            inputMon = inputMon.replaceAll(" ", "_");
            inputMon = toProperName(inputMon);
            String temp = inputMon.replaceAll("_", " ");
            String element = Monster.monsterNamesDatabase.get(temp);
            String name = "Monsters." + element + "." + inputMon;
            Class<?> c = Class.forName(name);
            pickedMons.add((Monster) c.getDeclaredConstructor().newInstance());
        }
        catch (Exception ignored)
        {
        }
        if (pickedMons.size() < 4)
        {
            return findTeamFromMonsters(pickedMons);
        }
        else
        {
            ArrayList<Team> allTeams = new ArrayList<>(teamStats.keySet());
            
            for (int i = 0; i < 4; i++)
            {
                for (int j = allTeams.size() - 1; j >= 0; j--)
                {
                    Team currentTeam = allTeams.get(j);
                    if (!currentTeam.hasInstanceOf(pickedMons.get(i)))
                    {
                        allTeams.remove(currentTeam);
                    }
                }
            }
            
            try
            {
                return allTeams.get(0);
            }
            catch (IndexOutOfBoundsException e)
            {
                System.out.println("Oops! Team not found.");
                return null;
            }
        }
    }
    
    private static String toReadableTime(long milliseconds)
    {
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        
        while (milliseconds >= 1_000)
        {
            seconds++;
            milliseconds -= 1_000;
            
            if (seconds >= 60)
            {
                seconds -= 60;
                minutes++;
            }
            if (minutes >= 60)
            {
                minutes -= 60;
                hours++;
            }
            if (hours >= 24)
            {
                hours -= 24;
                days++;
            }
        }
        
        return String.format("%d days, %d hours, %d minutes, %d.%d seconds", days, hours, minutes, seconds, milliseconds);
    }
}
