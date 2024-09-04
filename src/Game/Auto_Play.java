package Game;

import Abilities.*;
import Monsters.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.io.*;
import java.util.*;

import static Monsters.Monster.*;

/**
 * This class runs multiple simulations such that every possible team combination fights against every other team.
 *
 * @author Anthony (Tony) Youssef
 */
public class Auto_Play extends Thread
{
    private static final Scanner scan = new Scanner(System.in);
    private static Game game;
    private static boolean stunned = false, pause = false;
    private static Team highestAtkBar = new Team("", new ArrayList<>()), other = new Team("", new ArrayList<>());
    private static ArrayList<Team> teamStats = new ArrayList<>();
    private static final ArrayList<Team> bestTeams = new ArrayList<>();
    private static final ArrayList<String> monsterKeys = new ArrayList<>();
    private static long numOfCompletedSimulations = 0, totalSims = 0;
    private static boolean simsCalculationError = false;
    private static final StopWatch totalRunningTime = new StopWatch(false), battleTime = new StopWatch(false);
    private static boolean whitelisted = false;
    
    /**
     * Runs the Auto_Play class
     */
    public static void main(String[] args)
    {
        Monster.setGame(game);
        Monster.setDatabase();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\nTotal number of simulations ran: " + numWithCommas(numOfCompletedSimulations));
            totalRunningTime.pause();
            updateBestTeams();
            System.out.println("Total time elapsed: \t" + toReadableTime(totalRunningTime.getElapsedTime()));
            System.out.println("Time elapsed during battle: " + toReadableTime(battleTime.getElapsedTime()));
            System.out.println("Final standings:");
            for (Team team : bestTeams)
            {
                for (Monster mon : team.getMonsters())
                {
                    System.out.printf("%s\t\t", mon.getName(true, false));
                }
                System.out.printf("Number of wins: %s Number of losses: %s\n", numWithCommas(team.getWins()), numWithCommas(team.getLosses()));
            }
            System.out.println("\n\n");
            if (numOfCompletedSimulations >= 500_000 || numOfCompletedSimulations >= totalSims * 0.5 || battleTime.getElapsedTime() >= 3.6e12)
            {
                String fileName = exportResults();
                
                if (fileName != null)
                {
                    System.out.printf("Results Exported to \"%s\"\n", fileName);
                }
            }
        }));
        
        for (int i = 0; i < 4; i++)
        {
            bestTeams.add(null);
        }
        
        Monster.setPrint(false);
        
        allPossibleTeams();
        
        System.out.println("Simulations complete");
        postRunOptions(teamStats);
        
        System.exit(0);
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
     * @return the Game object that was used in the battle
     */
    public static Game battle(Team team1, Team team2)
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
            highestAtkBar = game.getTeamWithHighestAtkBar();
            other = game.getOtherTeam();
            Monster next = highestAtkBar.MonsterWithHighestFullAtkBar();
            
            //Activate before turn passives
            game.activateBeforeTurnPassives(next);
            
            game.applyStats(next);
            
            //If the monster's Hp falls below zero before turn
            if (next.getCurrentHp() <= 0)
            {
                next.kill();
            }
            
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
                Monster caster = p.getCaster();
                next.nextTurn(caster, 1);
                continue;
            }
            
            //Checks if any Monster on the other team has Threat
            boolean threat = other.monHasThreat();
            
            //Get abilityNum and print
            int abilityNum = next.chooseAbilityNum(next, game.getTeamWithHighestAtkBar(), next.getAbilities(), true);
            
            //Get target and print
            if (threat && !next.getAbility(abilityNum).targetsEnemy())
            {
                Monster target = other.getMonWithThreat();
                next.nextTurn(target, abilityNum);
            }
            chooseTargetAndApplyNextTurn(next, abilityNum, (next.getAbility(abilityNum).targetsEnemy() ? other : highestAtkBar), true);
            
            //Apply nextTurn()
            for (Monster mon : other.getMonsters())
            {
                if (mon.isDead() && !deadMons.containsKey(mon))
                {
                    deadMons.put(mon, turnNumber);
                }
            }
            
            turnNumber++;
            if (turnNumber >= 150)
            {
                break;
            }
        }
        
        return game;
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
        if (potentialTargets.size() == 0)
        {
            return;
        }
        Team friendlyTeam = highestAtkBar;
        Ability chosenAbility = next.getAbility(abilityNum);
        
        //Ability is a support ability
        if (chosenAbility instanceof Heal_Ability)
        {
            Monster target = friendlyTeam.getLowestHpMon();
            verifyTarget(next, chosenAbility, target, abilityNum, potentialTargets);
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
                verifyTarget(next, chosenAbility, target, abilityNum, potentialTargets);
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
                    try
                    {
                        chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
                    }
                    catch (StackOverflowError e)
                    {
                        System.out.println(next);
                        System.out.println(potentialTargets);
                    }
                }
                return;
            }
        }
    }
    
    private static void verifyTarget(Monster next, Ability chosenAbility, Monster target, int abilityNum, Team potentialTargets)
    {
        if (!next.targetIsValid(next, target, chosenAbility.targetsEnemy()))
        {
            
            removeInvalidTarget(next, abilityNum, potentialTargets, target);
            return;
        }
        if (!next.nextTurn(target, abilityNum))
        {
            removeInvalidTarget(next, abilityNum, potentialTargets, target);
        }
    }
    
    private static boolean getNewTarget(Monster next, int abilityNum, Team potentialTargets, Ability chosenAbility, ArrayList<Monster> allMons)
    {
        Monster target = getBestMonToAttack(allMons);
        if (!next.targetIsValid(next, target, chosenAbility.targetsEnemy()))
        {
            removeInvalidTarget(next, abilityNum, potentialTargets, target);
            return false;
        }
        if (!next.nextTurn(target, abilityNum))
        {
            removeInvalidTarget(next, abilityNum, potentialTargets, target);
        }
        return true;
    }
    
    private static void removeInvalidTarget(Monster next, int abilityNum, Team potentialTargets, Monster target)
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
        ArrayList<Monster> allMons = filterMonsters(Monster.getMonstersFromDatabase(), true);
        for (Monster allMon : allMons)
        {
            monsterKeys.add(allMon.getName(false, false));
        }
        ArrayList<ArrayList<Monster>> allPossibleTeamMonsters = generateCombinations(allMons, 4);
        ArrayList<Team> allPossibleTeams = new ArrayList<>();
        for (ArrayList<Monster> list : allPossibleTeamMonsters)
        {
            allPossibleTeams.add(new Team("temp", list));
        }
        Collections.shuffle(allPossibleTeamMonsters);
        totalSims = totalNumOfSims(allPossibleTeamMonsters.size());
        teamStats = allPossibleTeams;
        
        System.out.println("Simulations started");
        
        Auto_Play thread = new Auto_Play();
        thread.start();
        
        totalRunningTime.play();
        //Run all simulations
        for (int i = 0; i < allPossibleTeamMonsters.size(); i++)
        {
            final Team winner = allPossibleTeams.get(i);
            for (int j = i + 1; j < allPossibleTeamMonsters.size(); j++)
            {
                if (pause && numOfCompletedSimulations != 0)
                {
                    totalRunningTime.pause();
                    updateBestTeams();
                    
                    if (totalSims < 0 || simsCalculationError)
                    {
                        totalSims = totalNumOfSims(allPossibleTeams.size() - i);
                    }
                    if (simsCalculationError)
                    {
                        totalSims *= -1;
                    }
                    simsCalculationError = false;
                    long numOfSimsLeft = Math.abs(totalSims) - numOfCompletedSimulations;
                    String end = (totalSims < 0) ? "+" : "";
                    System.out.println("Number of simulations left: " + Monster.numWithCommas(numOfSimsLeft) + end);
                    long nanosecondsPerSim = totalRunningTime.getElapsedTime() / numOfCompletedSimulations;
                    long timeRemaining = nanosecondsPerSim * numOfSimsLeft;
                    System.out.println("Total elapsed time: " + toReadableTime(totalRunningTime.getElapsedTime()));
                    System.out.println("Time elapsed during battle: " + toReadableTime(battleTime.getElapsedTime()));
                    System.out.println("Estimated time remaining: " + toReadableTime(timeRemaining) + end);
                    System.out.println();
                    postRunOptions(teamStats);
                    
                    pause = false;
                    System.out.println("Running");
                    totalRunningTime.play();
                }
                numOfCompletedSimulations++;
                final Team contender = allPossibleTeams.get(j);
                
                //Reset teams
                resetTeam(winner.getMonsters());
                resetTeam(contender.getMonsters());
                Main.setRuneEffectsAndNames(winner, contender);
                battleTime.play();
                battle(winner, contender);
                battleTime.pause();
                
                //Update teamStats if a team won
                if (!game.getWinningTeam().getName().equals("None"))
                {
                    game.getWinningTeam().increaseWins();
                    game.getLosingTeam().increaseLosses();
                }
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
    private static void generateCombinationsUtil(ArrayList<Monster> monsters, int r, int index, int depth, ArrayList<Monster> currentCombination, ArrayList<ArrayList<Monster>> result)
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
    public static long totalNumOfSims(int numOfCombos)
    {
        if (numOfCombos == 0)
        {
            return 0;
        }
        try
        {
            return numOfCombos + totalNumOfSims(numOfCombos - 1);
        }
        catch (StackOverflowError e)
        {
            simsCalculationError = true;
            return numOfCombos;
        }
    }
    
    /**
     * Asks for four Monsters from the user and finds the Team that has all four.
     *
     * @param pickedMons The Monsters already picked. The first call should pass an empty ArrayList.
     * @param teams      The teams in use
     * @return The Team from {@link Auto_Play#teamStats} which contains the four Monsters provided.
     */
    public static Team findTeamFromMonsters(ArrayList<Monster> pickedMons, ArrayList<Team> teams)
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
            return findTeamFromMonsters(pickedMons, teams);
        }
        else
        {
            ArrayList<Team> allTeams = new ArrayList<>(teams);
            
            for (int i = allTeams.size() - 1; i >= 0; i--)
            {
                Team team = allTeams.get(i);
                for (int j = 0; j < 4; j++)
                {
                    if (!team.hasInstanceOf(pickedMons.get(j)))
                    {
                        allTeams.remove(team);
                        break;
                    }
                }
            }
            
            try
            {
                return allTeams.getFirst();
            }
            catch (IndexOutOfBoundsException | NoSuchElementException e)
            {
                System.out.println("Oops! Team not found.");
                return null;
            }
        }
    }
    
    /**
     * Converts a time in nanoseconds to readable time
     *
     * @param nanoseconds Number to convert
     * @return A String conveying the time in a readable format
     */
    private static String toReadableTime(long nanoseconds)
    {
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        
        while (nanoseconds >= 1e9)
        {
            seconds++;
            nanoseconds -= (long) 1e9;
            
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
        String returnString = "";
        if (days > 0)
        {
            returnString += days + " day" + (days == 1 ? "" : "s") + ", ";
        }
        if (hours > 0)
        {
            returnString += hours + " hours" + (hours == 1 ? "" : "s") + "" + ", ";
        }
        if (minutes > 0)
        {
            returnString += minutes + " minutes" + (minutes == 1 ? "" : "s") + ", ";
        }
        return returnString + seconds + "." + nanoseconds + " seconds";
    }
    
    /**
     * Replaces each Monster on a given team with a new instance of the same Monster
     *
     * @param team The team to replace Monsters on
     */
    public static void resetTeam(ArrayList<Monster> team)
    {
        for (int i = 0; i < team.size(); i++)
        {
            try
            {
                team.set(i, team.get(i).getClass().getDeclaredConstructor().newInstance());
            }
            catch (Throwable e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    /**
     * Updates the current best teams (sorts by number of wins)
     */
    private static void updateBestTeams()
    {
        int highest = 0;
        int second = 0;
        int third = 0;
        int fourth = 0;
        
        for (Team team : teamStats)
        {
            if (team.getWins() > highest)
            {
                highest = team.getWins();
                bestTeams.set(0, team);
            }
            else if (team.getWins() > second)
            {
                second = team.getWins();
                bestTeams.set(1, team);
            }
            else if (team.getWins() > third)
            {
                third = team.getWins();
                bestTeams.set(2, team);
            }
            else if (team.getWins() > fourth)
            {
                fourth = team.getWins();
                bestTeams.set(3, team);
            }
        }
    }
    
    /**
     * Allows the user to look at the teams in more detail after every run is done
     *
     * @param teams A list of the teams to use.
     */
    public static void postRunOptions(ArrayList<Team> teams)
    {
        final ArrayList<Team> finalTeams = new ArrayList<>(teams);
        String sortOption = "wins";
        teams = sortTeams(teams, true, "wins");
        while (true)
        {
            if (teams.isEmpty())
            {
                teams = new ArrayList<>(finalTeams);
            }
            System.out.println("Type \"exit\" to exit, \"inspect\" to inspect a specific team. Type \"order\" to change how the teams are ordered. Enter a number to get the team at that index (start with \"-\" to start counting from " +
                    "the end " + "of the list). To get a range of Teams, use the format \"# - #\" replacing # with a number");
            String input = scan.nextLine();
            try
            {
                boolean neg = input.contains("-");
                int index = Math.abs(Integer.parseInt(input));
                
                if (neg)
                {
                    Collections.reverse(teams);
                }
                
                Team team = teams.get(index);
                
                if (neg)
                {
                    Collections.reverse(teams);
                }
                
                printSingleTeamStats(team);
            }
            catch (IndexOutOfBoundsException e)
            {
                System.out.println("Index out of bounds, please enter a number between 0 and " + numWithCommas(teams.size() - 1) + " inclusive");
            }
            catch (NumberFormatException e)
            {
                //Find a specific team
                if (input.equalsIgnoreCase("inspect"))
                {
                    Team inspectTeam = findTeamFromMonsters(new ArrayList<>(), teams);
                    if (inspectTeam != null)
                    {
                        for (Monster mon : inspectTeam.getMonsters())
                        {
                            System.out.printf("%s\t\t", mon.getName(true, false));
                        }
                        System.out.println("number of wins: " + numWithCommas(inspectTeam.getWins()) + "\tNumber of losses: " + numWithCommas(inspectTeam.getLosses()));
                        System.out.println("Press enter to continue");
                    }
                    scan.nextLine();
                }
                //Order the teams
                else if (input.equalsIgnoreCase("order"))
                {
                    boolean reversed = false;
                    boolean exit = false;
                    while (true)
                    {
                        System.out.println("Type \"wins\" to sort by wins, \"losses\" to sort by losses, \"ratio\" to sort by win/loss ratio, or \"back\" to go back");
                        String sortString = scan.nextLine();
                        if (sortString.equalsIgnoreCase("wins"))
                        {
                            sortOption = "wins";
                            break;
                        }
                        else if (sortString.equalsIgnoreCase("losses"))
                        {
                            sortOption = "losses";
                            break;
                        }
                        else if (sortString.equalsIgnoreCase("ratio"))
                        {
                            sortOption = "ratio";
                            break;
                        }
                        else if (sortString.equalsIgnoreCase("back"))
                        {
                            exit = true;
                            break;
                        }
                        else
                        {
                            System.out.println("Please enter a valid response");
                        }
                    }
                    if (exit)
                    {
                        continue;
                    }
                    while (true)
                    {
                        System.out.println("Type \"normal\" to sort highest to lowest, \"reversed\" to sort lowest to highest, or \"back\" to go back");
                        String reverseInput = scan.nextLine();
                        if (reverseInput.equalsIgnoreCase("normal"))
                        {
                            break;
                        }
                        else if (reverseInput.equalsIgnoreCase("reversed"))
                        {
                            reversed = true;
                            break;
                        }
                        else if (reverseInput.equalsIgnoreCase("back"))
                        {
                            exit = true;
                            break;
                        }
                        else
                        {
                            System.out.println("Please enter a valid input");
                        }
                    }
                    if (exit)
                    {
                        continue;
                    }
                    
                    teams = sortTeams(teams, !reversed, sortOption);
                }
                //Filter the results
                else if (input.equalsIgnoreCase("filter"))
                {
                    teams = new ArrayList<>(finalTeams);
                    teams = filterTeams(teams);
                }
                //Exit options
                else if (input.equalsIgnoreCase("exit"))
                {
                    break;
                }
                //GetRange
                else if (input.contains("-"))
                {
                    //Remove whitespace
                    input = input.replaceAll(" ", "");
                    try
                    {
                        //Check if first number is negative and remove first dash if it is to properly parse number
                        boolean firstNeg = input.startsWith("-");
                        if (firstNeg)
                        {
                            input = input.substring(1);
                        }
                        //Get range
                        int firstNum = Integer.parseInt(input.substring(0, input.indexOf("-")));
                        int secondNum = Integer.parseInt(input.substring(input.indexOf("-") + 1));
                        
                        //Make first number negative if needed
                        if (firstNeg)
                        {
                            firstNum *= -1;
                        }
                        if (secondNum > teams.size())
                        {
                            System.out.println("Please enter a valid range");
                            continue;
                        }
                        
                        //Start is +
                        if (firstNum >= 0)
                        {
                            //Get end value if input is negative
                            if (secondNum < 0)
                            {
                                secondNum = teams.size() + secondNum - 1;
                            }
                            
                            if (firstNum > secondNum)
                            {
                                System.out.println("Please enter a valid range");
                                continue;
                            }
                            
                            for (int i = firstNum; i < secondNum; i++)
                            {
                                printSingleTeamStats(teams.get(i));
                            }
                        }
                        //start is -
                        else
                        {
                            if (secondNum > 0)
                            {
                                System.out.println("Please enter a valid range");
                                continue;
                            }
                            
                            //-, -
                            firstNum = teams.size() + firstNum - 1;
                            secondNum = teams.size() + secondNum - 1;
                            if (firstNum > secondNum)
                            {
                                System.out.println("Please enter a valid range");
                                continue;
                            }
                            
                            for (int i = firstNum; i < secondNum; i++)
                            {
                                printSingleTeamStats(teams.get(i));
                            }
                        }
                    }
                    catch (NumberFormatException error)
                    {
                        System.out.println("Please enter a valid range (min of 0, max of " + numWithCommas(teams.size() - 1));
                    }
                }
            }
        }
    }
    
    /**
     * Sorts the teams. This method does not change the original HashMap
     *
     * @param highToLow  True if the returned value should be sorted highest to lowest, false otherwise
     * @param sortOption The values the program should sort the teams by. ("wins" for wins "losses" for losses and "ratio" for win/loss ratio)
     * @return A sorted ArrayList of Teams
     */
    private static ArrayList<Team> sortTeams(ArrayList<Team> teams, boolean highToLow, String sortOption)
    {
        for (int i = 1; i < teams.size(); i++)
        {
            Team key = teams.get(i);
            int j = i - 1;
            while (insertionSortBoolean(highToLow, sortOption, j, key, teams))
            {
                teams.set(j + 1, teams.get(j));
                j--;
            }
            teams.set(j + 1, key);
        }
        
        return teams;
    }
    
    /**
     * Gives the boolean required for the sorting algorithm
     *
     * @param highToLow  True if the returned value should be sorted highest to lowest, false otherwise
     * @param sortOption This is the values the program will use to sort the teams
     * @param j          The current index
     * @param key        The current key
     * @param teams      The current list
     * @return The result of the boolean
     */
    private static boolean insertionSortBoolean(boolean highToLow, String sortOption, int j, Team key, ArrayList<Team> teams)
    {
        if (j < 0)
        {
            return false;
        }
        
        double keyRatio = 1.0 * key.getWins() / key.getWins();
        double otherRatio = 1.0 * teams.get(j).getWins() / teams.get(j).getLosses();
        if (highToLow)
        {
            if (sortOption.equals("wins"))
            {
                return key.getWins() > teams.get(j).getWins();
            }
            else if (sortOption.equals("losses"))
            {
                return key.getLosses() > teams.get(j).getLosses();
            }
            return keyRatio > otherRatio;
        }
        
        //Low to high
        if (sortOption.equals("wins"))
        {
            return key.getWins() < teams.get(j).getWins();
        }
        
        else if (sortOption.equals("losses"))
        {
            return key.getLosses() < teams.get(j).getLosses();
        }
        return keyRatio < otherRatio;
    }
    
    /**
     * Prints a single team with its stats
     *
     * @param team The Team to print
     */
    public static void printSingleTeamStats(Team team)
    {
        for (Monster mon : team.getMonsters())
        {
            System.out.printf("%s\t\t", mon.getName(true, false));
        }
        System.out.printf("Number of wins: %s\tNumber of losses: %s", numWithCommas(team.getWins()), numWithCommas(team.getLosses()));
        
        //Print ratio
        double ratio = 1.0 * team.getWins() / (team.getLosses() + team.getWins());
        ratio *= 100;
        if (team.getLosses() == 0)
        {
            if (team.getWins() == 0)
            {
                ratio = 0.0;
            }
        }
        
        System.out.print("\tWin/Loss Ratio: " + numWithCommas(ratio));
        System.out.println();
    }
    
    /**
     * Exports current results to a csv file
     *
     * @return The name of the newly created file
     */
    public static String exportResults()
    {
        //Initialize list of teams
        ArrayList<String> lines = new ArrayList<>();
        
        //Add library to top of file
        final String[] temp = {""};
        monsterKeys.forEach((name) -> temp[0] += name + ":" + monsterKeys.indexOf(name) + ",");
        lines.add(temp[0]);
        
        //Add each team to list
        for (Team team : teamStats)
        {
            lines.add(compressTeam(team));
        }
        
        Date today = new Date();
        //Try to crate a file using the current date and time as a name and write to it
        String fileName;
        try
        {
            fileName = "src/Game/Results/" + today.toString().replaceAll(":", "-") + ".csv";
            FileWriter writer = new FileWriter(fileName);
            //Add each line to file
            for (String line : lines)
            {
                writer.write(line + "\n");
            }
            writer.close();
            return fileName;
        }
        //Create a random seed name in case date file name is already taken
        catch (IOException e)
        {
            try
            {
                fileName = "src/Game/Results/" + new Random().nextDouble(0, 10) + ".csv";
                FileWriter writer = new FileWriter(fileName);
                
                //Add each line to file
                for (String line : lines)
                {
                    writer.write(line + "\n");
                }
                writer.close();
                return fileName;
            }
            catch (IOException ignored)
            {
            }
            return null;
        }
    }
    
    /**
     * Allows the user to whitelist or blacklist Monsters
     *
     * @param mons           The original set of Monsters
     * @param limitSelection True if method should check if user selected a valid number of Monsters, false otherwise
     * @return The new list of Monsters
     */
    public static ArrayList<Monster> filterMonsters(ArrayList<Monster> mons, boolean limitSelection)
    {
        while (true)
        {
            System.out.println("Would you like to whitelist or blacklist any monsters? (\"w\" for whitelist, \"b\" for blacklist, \"n\" for no)");
            String response = scan.nextLine();
            if (response.equals("w"))
            {
                whitelisted = true;
                String monName = "";
                ArrayList<Monster> newMons = new ArrayList<>();
                while (!monName.equals("exit"))
                {
                    while (!stringIsMonsterName(monName, mons) && !monName.equals("exit"))
                    {
                        System.out.println("Type the next monster to whitelist or \"exit\" to exit");
                        System.out.print("Current Monsters: ");
                        newMons.forEach(m -> System.out.print(m.getName(true, false) + "\t\t"));
                        System.out.println();
                        monName = scan.nextLine();
                    }
                    if (monName.equals("exit"))
                    {
                        if (newMons.size() < 5 && limitSelection)
                        {
                            monName = "";
                            System.out.println("Error, must include at least 5 monsters");
                            continue;
                        }
                        return newMons;
                    }
                    newMons.add(createNewMonFromName(monName));
                    monName = "";
                }
            }
            if (response.equals("b"))
            {
                whitelisted = false;
                String monName = "";
                ArrayList<Monster> blacklistedMonsters = new ArrayList<>();
                while (!monName.equals("exit"))
                {
                    while (!stringIsMonsterName(monName, mons) && !monName.equals("exit"))
                    {
                        System.out.println("Type the next monster to blacklist or \"exit\" to exit");
                        System.out.print("Current blacklisted Monsters: ");
                        blacklistedMonsters.forEach(m -> System.out.print(m.getName(true, false) + "\t\t"));
                        System.out.println();
                        monName = scan.nextLine();
                    }
                    if (monName.equals("exit"))
                    {
                        return mons;
                    }
                    if (mons.size() == 5 && limitSelection)
                    {
                        monName = "";
                        System.out.println("Error, must include at least 5 monsters");
                        continue;
                    }
                    Monster mon = createNewMonFromName(monName);
                    for (int i = 0; i < mons.size(); i++)
                    {
                        if (mons.get(i).getName(false, false).equals(mon.getName(false, false)))
                        {
                            blacklistedMonsters.add(mons.remove(i));
                            break;
                        }
                    }
                    monName = "";
                }
            }
            if (response.equals("n"))
            {
                return mons;
            }
        }
    }
    
    /**
     * Filters the teams by Monsters
     *
     * @param teams The teams to filter
     * @return The filtered teams
     */
    public static ArrayList<Team> filterTeams(ArrayList<Team> teams)
    {
        ArrayList<Monster> filteredMonsters = filterMonsters(Monster.getMonstersFromDatabase(), false);
        ArrayList<Team> temp = new ArrayList<>(teams);
        
        for (int i = temp.size() - 1; i >= 0; i--)
        {
            Team t = temp.get(i);
            if (whitelisted)
            {
                for (Monster mon : filteredMonsters)
                {
                    if (!t.hasInstanceOf(mon))
                    {
                        temp.remove(t);
                        break;
                    }
                }
            }
            else
            {
                for (Monster monster : t.getMonsters())
                {
                    boolean contains = false;
                    for (Monster filteredMonster : filteredMonsters)
                    {
                        if (filteredMonster.getName(false, false).equals(monster.getName(false, false)))
                        {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains)
                    {
                        temp.remove(t);
                        break;
                    }
                }
            }
        }
        return temp;
    }
    
    private static String compressTeam(Team team)
    {
        String line = "";
        for (Monster mon : team.getMonsters())
        {
            line += monsterKeys.indexOf(mon.getName(false, false)) + ",";
        }
        line += team.getWins() + "," + team.getLosses() + ",";
        return line;
    }
}