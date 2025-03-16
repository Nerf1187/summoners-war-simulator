package Game;

import Abilities.*;
import Monsters.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.io.*;
import java.util.*;

import static Game.Main.scan;

/**
 * This class runs multiple simulations such that every possible team combination fights against every other team.
 *
 * @author Anthony (Tony) Youssef
 */
public class Auto_Play extends Thread
{
    private static Game game;
    private static boolean pause = false, endThread = false, completed = false;
    private static Team highestAtkBar = new Team("", new ArrayList<>()), other = new Team("", new ArrayList<>());
    private static ArrayList<Team> teamStats = new ArrayList<>();
    private static final ArrayList<Team> bestTeams = new ArrayList<>();
    private static final ArrayList<String> monsterKeys = new ArrayList<>();
    private static long numOfCompletedSimulations = 0, totalSims = 0;
    private static int i = 0, j = 1;
    private static boolean simsCalculationError = false;
    private static StopWatch totalRunningTime = new StopWatch(false, 0), battleTime = new StopWatch(false, 0);
    
    /**
     * Runs the Auto_Play class
     */
    public void main()
    {
        //Initialize global game and Monster database
        Monster.setGame(game);
        Monster.setDatabase();
        //Show results when the program unnaturally ends
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //Total simulations run
            System.out.printf("\n\nTotal number of simulations ran: %,d%n", numOfCompletedSimulations);
            totalRunningTime.pause();
            updateBestTeams();
            //Total time elapsed while running (not including while paused)
            System.out.printf("Total time elapsed: \t%s%n", toReadableTime(totalRunningTime.getElapsedTime()));
            //Time spent simulating battles
            System.out.printf("Time elapsed during battle: %s%n", toReadableTime(battleTime.getElapsedTime()));
            System.out.println("Final standings:");
            //Print the top 4 teams
            for (Team team : bestTeams)
            {
                if (team == null)
                {
                    continue;
                }
                for (Monster mon : team.getMonsters())
                {
                    System.out.printf("%s\t\t", mon.getName(true, false));
                }
                System.out.printf("Number of wins: %,d Number of losses: %,d\n", team.getWins(), team.getLosses());
            }
            System.out.println("\n\n");
            
            //Export results to a csv file if there are more than 500,000 simulations run, the number of completed simulations is more than half the total number, or more than an hour of battle time has passed.
            if (numOfCompletedSimulations >= 500_000 || numOfCompletedSimulations > Math.abs(totalSims) * 0.5 || battleTime.getElapsedTime() >= 3.6e12)
            {
                //Attempt to export and get file name
                String fileName = exportResults(i, j);
                
                //Print file name if possible
                if (fileName != null)
                {
                    System.out.printf("Results exported to \"%s\"\n", fileName);
                }
                else
                {
                    System.out.println("Unable to export to file");
                }
            }
        }));
        
        //Initialize the best teams array
        for (int i = 0; i < 4; i++)
        {
            bestTeams.add(null);
        }
        
        //Prevent the battles from printing
        Monster.setPrint(false);
        
        //Run simulations
        allPossibleTeams();
        
        completed = true;
        //Allow the user to view results
        System.out.println("Simulations complete");
        endThread = true;
        System.out.println("Press enter to continue");
        while (endThread)
        {
            Main.pause(5);
        }
        pauseMenu(teamStats);
    }
    
    /**
     * Continues simulations from a previous instance of Auto_Play
     *
     * @param teams       The teams to use
     * @param library     The names of every Monster used
     * @param totalTime   The total time when the previous Auto Play stopped
     * @param _battleTime The battle time when the previous Auto Play stopped
     * @param _i          The last i value from the previous instance
     * @param _j          The last j value from the previous instance
     */
    public void main(ArrayList<Team> teams, ArrayList<String> library, long totalTime, long _battleTime, int _i, int _j)
    {
        teamStats = teams;
        monsterKeys.addAll(library);
        totalRunningTime = new StopWatch(false, totalTime);
        battleTime = new StopWatch(false, _battleTime);
        i = _i;
        j = _j;
        numOfCompletedSimulations = calculateNumOfPreCompletedSims(_i, _j, teams.size());
        main();
    }
    
    /**
     * Calculates the number of completed simulations from a previous instance of Auto_Play
     *
     * @param i    The last i value from the previous instance
     * @param j    The last j value from the previous instance
     * @param size The size of the library from the previous instance
     * @return The number of simulations from the previous Auto_Play instance
     */
    private int calculateNumOfPreCompletedSims(int i, int j, int size)
    {
        int total = 0;
        for (int k = 0; k < i; k++)
        {
            total += size - (k + 1);
        }
        total += j - (i + 1);
        return total;
    }
    
    /**
     * Runs and pauses the program according to user input
     */
    public void run()
    {
        while (!endThread)
        {
            scan.nextLine();
            if (!endThread)
            {
                pause = true;
            }
            while (pause) //Pause program
            {
                Main.pause(5);
            }
        }
        endThread = false;
    }
    
    /**
     * Runs a simulated battle with the provided teams
     *
     * @param team1 The first team in the battle
     * @param team2 The second team in the battle
     * @return The Game object that was used in the battle
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
            
            //Apply before turn buffs and debuffs
            game.applyStats(next);
            
            //If the monster's Hp falls below zero before turn (like from DoT)
            if (next.getCurrentHp() <= 0)
            {
                next.kill();
            }
            
            //Check if stunned
            if (next.isStunned())
            {
                next.decreaseStatCooldowns();
                next.setAtkBar(0);
                continue;
            }
            
            //Check if dead again
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
            
            //Get ability number
            int abilityNum = next.chooseAbilityNum(next, game.getTeamWithHighestAtkBar(), next.getAbilities(), true);
            
            //Get target and apply nextTurn
            if (threat && !next.getAbility(abilityNum).targetsEnemy())
            {
                Monster target = other.getMonWithThreat();
                next.nextTurn(target, abilityNum);
            }
            chooseTargetAndApplyNextTurn(next, abilityNum, (next.getAbility(abilityNum).targetsEnemy() ? other : highestAtkBar), true);
            
            //Check if the target is dead after turn
            for (Monster mon : other.getMonsters())
            {
                if (mon.isDead() && !deadMons.containsKey(mon))
                {
                    deadMons.put(mon, turnNumber);
                }
            }
            
            //End battle if more than 150 games have passed
            turnNumber++;
            if (turnNumber >= 150)
            {
                break;
            }
        }
        //Return game object
        return game;
    }
    
    /**
     * A recursive algorithm to choose the best target to attack/heal and apply the {@link Monster#nextTurn(Monster, int)} function.
     * <pre>
     *     The significance when choosing the target is as follows:
     *     1. low health targets
     *     2. Advantageous attributes
     *     3. Neutral attributes
     *     4. Disadvantageous attributes
     *     5. Monsters with a low number of buffs that decrease damage taken
     *     6. Monsters with a high number of debuffs that increase damage
     * </pre>
     *
     * @param next             The acting Monster
     * @param abilityNum       The ability number to use
     * @param potentialTargets A temporary team of Monsters that contain the potential targets
     * @param firstCall        True if this call is the first time it is called (From outside the method)
     */
    public static void chooseTargetAndApplyNextTurn(Monster next, int abilityNum, Team potentialTargets, boolean firstCall)
    {
        //Remove all dead monsters on first call
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
        //Do nothing if no potential targets found
        if (potentialTargets.size() == 0)
        {
            return;
        }
        Team allyTeam = highestAtkBar;
        Ability chosenAbility = next.getAbility(abilityNum);
        
        //Ability is a support ability
        if (chosenAbility instanceof Heal_Ability)
        {
            //Target the Monster on the allied team with the lowest HP ratio
            Monster target = allyTeam.getLowestHpMon();
            verifyTargetAndApplyTurn(next, chosenAbility, target, abilityNum, potentialTargets);
            return;
        }
        
        //Ability targets self
        if (chosenAbility.targetsSelf())
        {
            next.nextTurn(next, abilityNum);
            return;
        }
        
        //Ability is an attack ability
        //Get best attack choice and try to attack
        verifyTargetAndApplyTurn(next, chosenAbility, getBestMonToAttack(next, potentialTargets.getMonsters(), chosenAbility.getNumOfBeneficialEffectsToRemove()), abilityNum, potentialTargets);
    }
    
    /**
     * Verifies the target and attempts to complete its turn
     *
     * @param next             The acting Monster
     * @param chosenAbility    The chosen ability
     * @param target           The target Monster
     * @param abilityNum       The ability number
     * @param potentialTargets The potential targets
     */
    private static void verifyTargetAndApplyTurn(Monster next, Ability chosenAbility, Monster target, int abilityNum, Team potentialTargets)
    {
        //Check if the target is valid
        if (!next.targetIsValid(target, chosenAbility.targetsEnemy()))
        {
            removeInvalidTarget(next, target, abilityNum, potentialTargets);
            return;
        }
        //Try to complete turn
        if (!next.nextTurn(target, abilityNum))
        {
            removeInvalidTarget(next, target, abilityNum, potentialTargets);
        }
    }
    
    /**
     * Removes an invalid target and attempts to find a new one
     *
     * @param next             The acting Monster
     * @param target           The target to remove
     * @param abilityNum       The chosen ability number
     * @param potentialTargets The potential targets
     */
    private static void removeInvalidTarget(Monster next, Monster target, int abilityNum, Team potentialTargets)
    {
        ArrayList<Monster> modifiedTeam = new ArrayList<>();
        //Remove invalid target
        for (Monster potentialTarget : potentialTargets.getMonsters())
        {
            if (!potentialTarget.equals(target))
            {
                modifiedTeam.add(potentialTarget);
            }
        }
        //Attempt to find new target
        chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
    }
    
    /**
     * A method to find the best Monster to attack
     *
     * @param next                       The attacking Monster
     * @param monsters                   The Monsters to search through
     * @param numOfBuffsCanAbilityRemove Whether the chosen ability can remove buffs
     * @return The best Monster to attack
     */
    public static Monster getBestMonToAttack(Monster next, ArrayList<Monster> monsters, int numOfBuffsCanAbilityRemove)
    {
        Monster target = null;
        //Make sure there is at least one Monster alive
        for (Monster mon : monsters)
        {
            if (!mon.isDead())
            {
                target = mon;
                break;
            }
        }
        //Do nothing if no alive targets
        if (target == null)
        {
            return null;
        }
        
        //Calculate each Monster's "score" and find the highest
        double highestPoints = -1;
        double currentPoints;
        for (Monster mon : monsters)
        {
            //Set to a predetermined non zero value
            currentPoints = 2;
            if (mon.isDead())
            {
                continue;
            }
            
            ArrayList<Buff> targetBuffs = mon.getAppliedBuffs();
            //Decrease score for certain buffs
            for (Buff buff : targetBuffs)
            {
                currentPoints *= switch (buff.getBuffNum())
                {
                    case Buff.REFLECT -> 0.9;
                    case Buff.SHIELD -> 0.946;
                    case Buff.IMMUNITY -> 0.82;
                    case Buff.COUNTER -> 0.916;
                    case Buff.CRIT_RESIST_UP -> 0.83;
                    case Buff.DEF_UP -> 0.947;
                    case Buff.ENDURE -> 0.983;
                    case Buff.SOUL_PROTECTION -> 0.74;
                    case Buff.DEFEND -> 0.69;
                    case Buff.INVINCIBILITY -> 0.65;
                    default -> 1;
                };
            }
            //Increase the score depending on how many buffs the ability can remove
            currentPoints *= (0.19 * numOfBuffsCanAbilityRemove + 1);
            ArrayList<Debuff> targetDebuffs = mon.getAppliedDebuffs();
            //Increase score for certain debuffs
            for (Debuff debuff : targetDebuffs)
            {
                switch (debuff.getDebuffNum())
                {
                    case Debuff.DEC_DEF -> currentPoints *= 1.2;
                    case Debuff.BRAND -> currentPoints *= 1.15;
                }
            }
            
            //Alter score for elemental relationship
            currentPoints *= switch (Team.elementalRelationship(next.getElement(), mon.getElement()))
            {
                case ConsoleColors.GREEN_BACKGROUND -> 1.3;
                case ConsoleColors.RED_BACKGROUND -> 0.7;
                default -> 1.16; //Neutral
            };
            
            //Alter score for HP ratio
            double weight = target.getHpRatio() / 100 * 1.31;
            currentPoints /= weight;
            
            //Set a new target if it has a higher score
            if (currentPoints > highestPoints)
            {
                target = mon;
                highestPoints = currentPoints;
            }
            //If the scores are the same, choose the one with a lower HP ratio
            else if (currentPoints == highestPoints)
            {
                target = (mon.getHpRatio() < target.getHpRatio()) ? mon : target;
            }
        }
        return target;
    }
    
    /**
     * Gets the Team without the acting Monster
     *
     * @return The Team without the acting Monster
     */
    public static Team getOther()
    {
        return other;
    }
    
    /**
     * Gets the Game object currently in use
     *
     * @return The Game object currently in use
     */
    public static Game getGame()
    {
        return game;
    }
    
    /**
     * Creates every possible Team combination (ignores leader skills to decrease the number of combinations) and simulates each of their battles
     */
    public static void allPossibleTeams()
    {
        boolean saveTime;
        ArrayList<Team> allPossibleTeams = new ArrayList<>();
        if (teamStats.isEmpty())
        {
            //Filter Monsters
            ArrayList<Monster> allMons = filterMonsters(Monster.getMonstersFromDatabase(), true);
            //Add Monster names to the keys list
            for (Monster mon : allMons)
            {
                monsterKeys.add(mon.getName(false, false));
            }
            
            //Ask if the user wants to save time or memory
            String response;
            do
            {
                System.out.println("Do you want to prioritize speed or memory? (\"s\" for speed, \"m\" for memory, \"info\" to learn more)");
                response = scan.nextLine();
                if (response.equalsIgnoreCase("info"))
                {
                    System.out.println("Choosing speed is much faster but takes up more RAM and takes longer to create the teams.");
                    System.out.println("Choosing memory takes up less RAM and creates the teams faster but takes much longer to finish.");
                }
            }
            while (!response.equalsIgnoreCase("s") && !response.equalsIgnoreCase("m"));
            saveTime = response.equalsIgnoreCase("s");
            
            //Create teams
            System.out.println("Creating teams...");
            ArrayList<ArrayList<Monster>> allPossibleTeamMonsters = generateCombinations(allMons, 4, saveTime);
            //Add teams to list
            for (ArrayList<Monster> list : allPossibleTeamMonsters)
            {
                allPossibleTeams.add(new Team("temp", list));
            }
            //Randomize order
            Collections.shuffle(allPossibleTeams);
            //Calculate the total number of simulations to be run
            totalSims = totalNumOfSims(allPossibleTeams.size());
            teamStats = allPossibleTeams;
        }
        else
        {
            allPossibleTeams = teamStats;
            saveTime = true;
            totalSims = -1;
        }
        
        System.out.println("Simulations started");
        
        //Check for pausing
        Auto_Play thread = new Auto_Play();
        thread.start();
        
        boolean firstRun = true;
        
        //Start overall timer
        totalRunningTime.play();
        
        //Run all simulations
        for (; i < allPossibleTeams.size(); i++)
        {
            //Current team for testing
            final Team current = allPossibleTeams.get(i);
            
            final int tempI = i;
            if (firstRun)
            {
                i = j - 1;
            }
            for (j = i + 1; j < allPossibleTeams.size(); j++)
            {
                if (firstRun)
                {
                    i = tempI;
                    firstRun = false;
                }
                
                //Pause program
                if (pause && numOfCompletedSimulations != 0)
                {
                    //Pause program and update best teams
                    totalRunningTime.pause();
                    updateBestTeams();
                    
                    //Try to recalculate total simulations if there was an error previously
                    if (totalSims < 0 || simsCalculationError)
                    {
                        totalSims = totalNumOfSims(allPossibleTeams.size());
                    }
                    if (simsCalculationError)
                    {
                        totalSims *= -1;
                    }
                    simsCalculationError = false;
                    //Calculate the number of simulations remaining
                    long numOfSimsLeft = Math.abs(totalSims) - numOfCompletedSimulations;
                    //Show there may be more simulations than shown if there was an error in calculations
                    String end = (totalSims < 0) ? "+" : "";
                    //Print information
                    System.out.printf("Number of simulations run: %,d%n", numOfCompletedSimulations);
                    System.out.printf("Number of simulations left: %,d%s%n", numOfSimsLeft, end);
                    long nanosecondsPerSim = totalRunningTime.getElapsedTime() / numOfCompletedSimulations;
                    long timeRemaining = nanosecondsPerSim * numOfSimsLeft;
                    System.out.printf("Total elapsed time: %s%n", toReadableTime(totalRunningTime.getElapsedTime()));
                    System.out.printf("Time elapsed during battle: %s%n", toReadableTime(battleTime.getElapsedTime()));
                    System.out.printf("Estimated time remaining: %s%s%n", toReadableTime(timeRemaining), end);
                    System.out.println();
                    
                    //Allow the user to view current standings
                    pauseMenu(teamStats);
                    
                    //Exit pause
                    pause = false;
                    System.out.println("Running");
                    totalRunningTime.play();
                }
                final Team contender = allPossibleTeams.get(j);
                
                //Reset teams
                if (saveTime)
                {
                    resetTeamForTime(current);
                    resetTeamForTime(contender);
                }
                else //Save memory
                {
                    resetTeamForMemory(current.getMonsters());
                    resetTeamForMemory(contender.getMonsters());
                    Main.setRuneEffectsAndNames(current, contender);
                }
                //Start battle timer
                battleTime.play();
                //Simulate the battle
                battle(current, contender);
                //Pause the battle timer
                battleTime.pause();
                numOfCompletedSimulations++;
                
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
     * @param saveTime True if the program should prioritize time over space, false otherwise
     * @return A list containing lists of Monsters to set up the Teams
     * @author ChatGPT
     */
    public static ArrayList<ArrayList<Monster>> generateCombinations(ArrayList<Monster> monsters, int r, boolean saveTime)
    {
        ArrayList<ArrayList<Monster>> result = new ArrayList<>();
        ArrayList<Monster> currentCombination = new ArrayList<>();
        //Initialize list
        for (int i = 0; i < r; i++)
        {
            currentCombination.add(null);
        }
        System.out.print("Progress: 0%\r");
        generateCombinationsUtil(monsters, r, 0, 0, currentCombination, result, saveTime);
        System.out.println("\n");
        return result;
    }
    
    /**
     * A helper method to generate each combination. This method should only be called from {@link Auto_Play#generateCombinations(ArrayList, int, boolean)}
     *
     * @param monsters           A list of Monsters to generate the combinations from
     * @param r                  The length of each combination
     * @param index              The place in the list Monsters to start. This should be zero on the first call
     * @param depth              The current place in the current combination to add the next Monster. This should be zero on the first call
     * @param currentCombination The current combination being built. This should be a new ArrayList with a size of r on the first call
     * @param result             All combinations built already. This should be a new ArrayList on the first call.
     * @param saveTime           True if the program should prioritize time over space, false otherwise
     * @author ChatGPT
     */
    private static void generateCombinationsUtil(ArrayList<Monster> monsters, int r, int index, int depth, ArrayList<Monster> currentCombination, ArrayList<ArrayList<Monster>> result, boolean saveTime)
    {
        //Add the current combination to the result if it is complete
        if (depth == r)
        {
            if (saveTime)
            {
                ArrayList<Monster> a = new ArrayList<>();
                //Create new instances of each Monster to save time later
                currentCombination.forEach(m -> {
                    try
                    {
                        a.add(m.getClass().getDeclaredConstructor().newInstance());
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                });
                
                result.add(a);
            }
            else
            {
                //Add the same instance of each Monster to save memory
                result.add(new ArrayList<>(currentCombination));
            }
            System.out.printf("Progress: %.1f%%\r", 100.0 * result.size() / nCr(monsters.size(), r));
            return;
        }
        
        for (int i = index; i < monsters.size(); i++)
        {
            //Add the Monster to the current combination and go to the place
            currentCombination.set(depth, monsters.get(i));
            generateCombinationsUtil(monsters, r, i + 1, depth + 1, currentCombination, result, saveTime);
        }
    }
    
    
    /**
     * Calculates the number of combinations (nCr) for choosing {@code r} items from a set of {@code n} items.
     *
     * @param n The total number of items in the set.
     * @param r The number of items to choose from the set.
     * @return The number of valid combinations (nCr), or 0 if {@code r > n}.
     * @author JetBrains AI Assistant
     */
    private static long nCr(int n, int r)
    {
        //Make sure there are valid combinations
        if (r > n)
        {
            return 0;
        }
        
        //Take advantage of symmetry: C(n, r) = C(n, n-r)
        r = Math.min(r, n - r);
        
        //Calculate combination count
        long result = 1;
        for (int i = 0; i < r; i++)
        {
            result *= (n - i);
            result /= (i + 1);
        }
        return result;
    }
    
    /**
     * @param numOfCombos The number of combinations being tested
     * @return The total number of different simulations that can be run (equivalent to numOfCombos!)
     */
    public static long totalNumOfSims(int numOfCombos)
    {
        long result = 0;
        try //to add recursively
        {
            for (int i = numOfCombos; i > 0; i--)
            {
                result += numOfCombos;
            }
            return result;
        }
        catch (Exception e) //Too many recursive calls
        {
            //Flag an error
            simsCalculationError = true;
            return result;
        }
    }
    
    /**
     * Asks for four Monsters from the user and finds the Team that has all four.
     *
     * @param pickedMons The Monsters already picked. The first call should pass an empty ArrayList.
     * @param teams      The teams in use
     * @return The position (index) of the Team in the array
     */
    public static int findTeamFromMonsters(ArrayList<Monster> pickedMons, ArrayList<Team> teams)
    {
        //Get the name of 1 one the Monster in the team
        String inputMon;
        do
        {
            System.out.printf("Enter Monster %d's name%n", pickedMons.size() + 1);
            inputMon = scan.nextLine();
        }
        while (!Monster.stringIsMonsterName(inputMon) || Team.teamHasMon(inputMon, pickedMons));
        
        //Try to create the Monster and add it to the array
        try
        {
            pickedMons.add(Monster.createNewMonFromName(inputMon, true));
        }
        catch (Exception e)
        {
            System.out.println("Unable to find Monster");
            return findTeamFromMonsters(pickedMons, teams);
        }
        
        //Do the above until there are 4 selected Monsters
        if (pickedMons.size() < 4)
        {
            return findTeamFromMonsters(pickedMons, teams);
        }
        
        int index = teams.size() - 1;
        
        //Linear search to find the team
        outer:
        for (int i = teams.size() - 1; i >= 0; i--)
        {
            Team team = teams.get(i);
            for (int j = 0; j < 4; j++)
            {
                //Go to the next team if the current one does not have one of the Monsters
                if (!team.hasInstanceOf(pickedMons.get(j)))
                {
                    index--;
                    continue outer;
                }
            }
            break;
        }
        
        if (index < 0)
        {
            System.out.println("Oops! Team not found!");
        }
        return index;
    }
    
    /**
     * Converts a time in nanoseconds to readable time
     *
     * @param nanoseconds Number to convert
     * @return A String conveying the time in a readable format
     */
    private static String toReadableTime(long nanoseconds)
    {
        //Initialize times
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        
        while (nanoseconds >= 1e9) //1 second
        {
            //Increment seconds
            seconds++;
            nanoseconds -= (long) 1e9;
            
            //Increment times if they are overflowed
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
        //Format final String
        String returnString = "";
        if (days > 0)
        {
            returnString += "%d day%s, ".formatted(days, days == 1 ? "" : "s");
        }
        if (hours > 0)
        {
            returnString += "%d hour%s, ".formatted(hours, hours == 1 ? "" : "s");
        }
        if (minutes > 0)
        {
            returnString += "%d minute%s, ".formatted(minutes, minutes == 1 ? "" : "s");
        }
        return "%s%d.%d seconds".formatted(returnString, seconds, nanoseconds);
    }
    
    /**
     * Replaces each Monster on a given team with a new instance of the same Monster
     *
     * @param team The team to replace Monsters on
     */
    public static void resetTeamForMemory(ArrayList<Monster> team)
    {
        for (int i = 0; i < team.size(); i++)
        {
            try
            {
                team.set(i, Monster.createNewMonFromMon(team.get(i)));
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    /**
     * Resets each Monster on the team
     *
     * @param team The team to reset
     */
    public static void resetTeamForTime(Team team)
    {
        team.resetTeam();
    }
    
    /**
     * Updates the current best teams (sorts by number of wins)
     */
    private static void updateBestTeams()
    {
        final int[] highest = {0};
        final int[] second = {0};
        final int[] third = {0};
        final int[] fourth = {0};
        
        //Compare each team and find the top 4
        for (Team team : teamStats)
        {
            switch (team.getWins())
            {
                //Best
                case int wins when wins > highest[0] ->
                {
                    highest[0] = team.getWins();
                    bestTeams.set(0, team);
                }
                //2nd best
                case int wins when wins > second[0] ->
                {
                    second[0] = team.getWins();
                    bestTeams.set(1, team);
                }
                //3rd best
                case int wins when wins > third[0] ->
                {
                    third[0] = team.getWins();
                    bestTeams.set(2, team);
                }
                //4th best
                case int wins when wins > fourth[0] ->
                {
                    fourth[0] = team.getWins();
                    bestTeams.set(3, team);
                }
                default ->
                {
                }
            }
        }
    }
    
    /**
     * Allows the user to look at the teams in more detail
     *
     * @param teams A list of the teams to use.
     */
    public static void pauseMenu(ArrayList<Team> teams)
    {
        final ArrayList<Team> finalTeams = new ArrayList<>(teams);
        ArrayList<Team> tempTeams = new ArrayList<>(finalTeams);
        sortTeams(tempTeams, true, "wins");
        outer:
        while (true)
        {
            if (teams.isEmpty())
            {
                teams = new ArrayList<>(finalTeams);
            }
            System.out.println("Enter a command or type \"help\" for a list of commands.");
            String input = scan.nextLine();
            try //Single teams at an index
            {
                boolean neg = input.contains("-");
                int index = Math.abs(Integer.parseInt(input));
                
                //Start from the end of the list if the user entered with a negative number
                if (neg)
                {
                    Collections.reverse(tempTeams);
                }
                
                //Get team at requested index
                Team team = tempTeams.get(index);
                
                //Flip the list again if the user entered a negative number
                if (neg)
                {
                    Collections.reverse(tempTeams);
                }
                
                printSingleTeamStats(team);
            }
            catch (IndexOutOfBoundsException e) //Invalid index
            {
                System.out.printf("Index out of bounds, please enter a number between 0 and %,d inclusive%n", tempTeams.size() - 1);
            }
            catch (NumberFormatException e)
            {
                switch (switch (input.toLowerCase())
                {
                    //Find a specific team
                    case "inspect" ->
                    {
                        //Get the index of the requested team
                        int index = findTeamFromMonsters(new ArrayList<>(), tempTeams);
                        
                        //Team not found
                        if (index == -1)
                        {
                            yield 1;
                        }
                        
                        //Print team
                        Team inspectTeam = tempTeams.get(index);
                        for (Monster mon : inspectTeam.getMonsters())
                        {
                            System.out.printf("%s\t\t", mon.getName(true, false));
                        }
                        //Print team info
                        System.out.printf("Number of wins: %,d\tNumber of losses: %,d\tPlace: %,d%n", inspectTeam.getWins(), inspectTeam.getLosses(), index);
                        yield 1;
                    }
                    //Order the teams
                    case "order" ->
                    {
                        int reversed = -1;
                        String sortOption = "";
                        
                        //Get the value to sort by
                        while (sortOption.isEmpty())
                        {
                            System.out.println("Type \"wins\" to sort by wins, \"losses\" to sort by losses, \"ratio\" to sort by win/loss ratio, or \"back\" to go back");
                            String sortString = scan.nextLine();
                            
                            sortOption = switch (sortString.toLowerCase())
                            {
                                case "wins", "losses", "ratio", "back" -> sortString;
                                default ->
                                {
                                    System.out.println("Please enter a valid response");
                                    yield "";
                                }
                            };
                            if (sortOption.equals("back"))
                            {
                                yield 1;
                            }
                        }
                        
                        //Get the sort order
                        while (reversed == -1)
                        {
                            System.out.println("Type \"normal\" to sort highest to lowest, \"reversed\" to sort lowest to highest, or \"back\" to go back");
                            String reverseInput = scan.nextLine();
                            
                            switch (reverseInput.toLowerCase())
                            {
                                case "reversed" -> reversed = 0;
                                case "normal" -> reversed = 1;
                                case "back" ->
                                {
                                }
                                default -> System.out.println("Please enter a valid input");
                            }
                            if (reverseInput.equalsIgnoreCase("back")) //Cancel operation
                            {
                                yield 1;
                            }
                        }
                        
                        //Sort the teams as requested
                        sortTeams(tempTeams, reversed == 1, sortOption);
                        yield 1;
                    }
                    //Filter the results
                    case "filter" ->
                    {
                        tempTeams = new ArrayList<>(finalTeams);
                        tempTeams = filterTeams(tempTeams);
                        yield 1;
                    }
                    //Order Monsters by their average placing
                    case "monsters" ->
                    {
                        ArrayList<Monster> sortMonsByPlace = sortMonsByPlace(tempTeams);
                        if (sortMonsByPlace == null)
                        {
                            yield 1;
                        }
                        
                        //Print each monster
                        for (int i = 0; i < sortMonsByPlace.size(); i++)
                        {
                            Monster m = sortMonsByPlace.get(i);
                            System.out.printf("%d. %s%n", i + 1, m.getName(true, false));
                        }
                        yield 1;
                    }
                    case "help" ->
                    {
                        printPauseCommands();
                        yield 1;
                    }
                    //Exit
                    case "exit" -> -1;
                    case "quit" ->
                    {
                        System.exit(0);
                        yield 1;
                    }
                    default -> 0;
                })
                {
                    case -1 ->
                    {
                        break outer;
                    }
                    case 1 ->
                    {
                        continue;
                    }
                    default ->
                    {
                    }
                }
                
                //Range of teams
                if (input.contains("-"))
                {
                    //Remove whitespace
                    input = input.replaceAll(" ", "");
                    try
                    {
                        //Check if the first number is negative and remove the first dash if it is to properly parse number
                        boolean firstNeg = input.startsWith("-");
                        if (firstNeg)
                        {
                            input = input.substring(1);
                        }
                        //Get range
                        int firstNum = Integer.parseInt(input.substring(0, input.indexOf("-")));
                        int secondNum = Integer.parseInt(input.substring(input.indexOf("-") + 1));
                        
                        //Make the first number negative if needed
                        if (firstNeg)
                        {
                            firstNum *= -1;
                        }
                        if (secondNum > tempTeams.size())
                        {
                            System.out.println("Please enter a valid range");
                            continue;
                        }
                        
                        //Start is positive
                        if (firstNum >= 0)
                        {
                            //Get end value if input is negative
                            if (secondNum < 0)
                            {
                                secondNum = tempTeams.size() + secondNum - 1;
                            }
                            
                            //End value is less than starting value (Invalid range)
                            if (firstNum > secondNum)
                            {
                                System.out.println("Please enter a valid range");
                                continue;
                            }
                            
                            //Print each team in the range
                            for (int i = firstNum; i < secondNum; i++)
                            {
                                printSingleTeamStats(tempTeams.get(i));
                            }
                        }
                        //Start is negative
                        else
                        {
                            //End is positive (Invalid range)
                            if (secondNum > 0)
                            {
                                System.out.println("Please enter a valid range");
                                continue;
                            }
                            
                            //End is negative
                            firstNum = tempTeams.size() + firstNum - 1;
                            secondNum = tempTeams.size() + secondNum - 1;
                            //End value is less than the starting value (Invalid range)
                            if (firstNum > secondNum)
                            {
                                System.out.println("Please enter a valid range");
                                continue;
                            }
                            
                            //Print each team in the range
                            for (int i = firstNum; i < secondNum; i++)
                            {
                                printSingleTeamStats(tempTeams.get(i));
                            }
                        }
                    }
                    catch (NumberFormatException error) //Invalid range
                    {
                        System.out.printf("Please enter a valid range (min of 0, max of %,d)%n", tempTeams.size() - 1);
                    }
                }
            }
        }
    }
    
    /**
     * Prints a list of available commands in the pause menu along with their descriptions.
     */
    private static void printPauseCommands()
    {
        //Add each command and description to a HashMap
        HashMap<String, String> commandsMap = new HashMap<>();
        commandsMap.put("inspect", "Inspect a specific team using its Monsters");
        commandsMap.put("order", "Change how the teams are ordered");
        commandsMap.put("monsters", "View every Monster sorted by their average place");
        commandsMap.put("#", "Get the team at a specific index (starting at 0). You can use a negative number to start counting from the end of the list");
        commandsMap.put("# - #", "Use this format to get a range of teams, replacing \"#\" with a number");
        commandsMap.put("help", "Show this commands list");
        commandsMap.put("filter", "Filter the teams by whitelisting or blacklisting specific Monsters");
        commandsMap.put("exit", "Exit this menu");
        commandsMap.put("quit", "Quit the program");
        
        //Print each command as a list
        int i = 1;
        for (Map.Entry<String, String> entry : commandsMap.entrySet())
        {
            System.out.printf("%d. \"%s\": %s%n", i++, entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * Calculates each Monster's average place and sorts them from lowest to highest
     *
     * @param teams The teams to look through
     * @return An ordered list of Monsters
     */
    private static ArrayList<Monster> sortMonsByPlace(final ArrayList<Team> teams)
    {
        HashMap<String, int[]> map = new HashMap<>(); //Monster name, sum, number of appearances
        //For each team, add the index to each Monster's respective sum
        for (int i = 0; i < teams.size(); i++)
        {
            Team team = teams.get(i);
            for (Monster monster : team.getMonsters())
            {
                //Increase the Monster's sum and # of appearances if it is already in the HashMap
                if (map.containsKey(monster.getName(false, false)))
                {
                    int[] temp = map.get(monster.getName(false, false));
                    temp[0] += i;
                    temp[1]++;
                    map.put(monster.getName(false, false), temp);
                }
                else //Put the Monster into the HashMap with new values
                {
                    map.put(monster.getName(false, false), new int[]{i, 1});
                }
            }
        }
        
        //Get each Monster's average place
        ArrayList<Monster> monsters = new ArrayList<>();
        HashMap<Monster, Integer> averages = new HashMap<>();
        try
        {
            map.forEach((name, vals) -> {
                Monster m = Monster.createNewMonFromName(name, true);
                averages.put(m, (vals[0] / vals[1]));
                monsters.add(m);
            });
        }
        catch (Exception e)
        {
            System.out.println("Unable to sort Monsters");
            return null;
        }
        
        //Sort the final list according to each Monster's average place
        for (int i = 0; i < monsters.size(); i++)
        {
            Monster key = monsters.get(i);
            int j = i - 1;
            while (j >= 0 && averages.get(key) < averages.get(monsters.get(j)))
            {
                monsters.set(j + 1, monsters.get(j));
                j--;
            }
            monsters.set(j + 1, key);
        }
        return monsters;
    }
    
    /**
     * Sorts the teams using merge sort.
     *
     * @param teams      The teams to sort
     * @param highToLow  True if the returned value should be sorted highest to lowest, false otherwise
     * @param sortOption The values the program should sort the teams by. ("wins" for wins "losses" for losses and "ratio" for win/loss ratio)
     */
    private static void sortTeams(ArrayList<Team> teams, boolean highToLow, String sortOption)
    {
        System.out.println("Sorting teams...");
        ArrayList<Team> temp = sortHelper(teams, highToLow, sortOption, teams.size());
        teams.clear();
        teams.addAll(temp);
        System.out.println("Done");
    }
    
    /**
     * Helper method for sorting a list of Team objects based on the specified options.
     *
     * @param teams      The list of Team objects to be sorted.
     * @param highToLow  A boolean indicating whether the sorting should be in descending (true) or ascending (false) order.
     * @param sortOption The sorting criteria to be applied.
     * @param totalSize  The total size of the original list, used for calculating and displaying progress.
     * @return A sorted ArrayList of Team objects based on the specified options.
     */
    private static ArrayList<Team> sortHelper(ArrayList<Team> teams, boolean highToLow, String sortOption, int totalSize)
    {
        if (teams.isEmpty() || teams.size() == 1)
        {
            return teams;
        }
        
        int middle = teams.size() / 2;
        ArrayList<Team> left = new ArrayList<>(teams.subList(0, middle));
        ArrayList<Team> right = new ArrayList<>(teams.subList(middle, teams.size()));
        
        return merge(sortHelper(left, highToLow, sortOption, totalSize), sortHelper(right, highToLow, sortOption, totalSize), highToLow, sortOption, totalSize);
    }
    
    /**
     * Merges two sorted lists of teams into a single sorted list based on the specified sorting criteria.
     *
     * @param left       The left ArrayList of teams to merge, pre-sorted based on the sorting criteria
     * @param right      The right ArrayList of teams to merge, pre-sorted based on the sorting criteria
     * @param highToLow  A boolean flag indicating whether sorting should be in descending order (true) or ascending order (false)
     * @param sortOption The sorting criteria to be applied.
     * @param totalSize  The total size of all teams being merged from both lists, used for progress calculation
     * @return An ArrayList of teams containing all elements from the input lists merged according to the specified order
     */
    private static ArrayList<Team> merge(ArrayList<Team> left, ArrayList<Team> right, boolean highToLow, String sortOption, int totalSize)
    {
        int leftIndex = 0, rightIndex = 0;
        ArrayList<Team> mergedTeams = new ArrayList<>();
        
        while (leftIndex < left.size() && rightIndex < right.size())
        {
            mergedTeams.add(mergeBool(highToLow, sortOption, left.get(leftIndex), right.get(rightIndex)) ? left.get(leftIndex++) : right.get(rightIndex++));
        }
        
        while (leftIndex < left.size())
        {
            mergedTeams.add(left.get(leftIndex++));
        }
        
        while (rightIndex < right.size())
        {
            mergedTeams.add(right.get(rightIndex++));
        }
        System.out.printf("Progress: %.4f%%\r", (mergedTeams.size() * 1.0 / totalSize) * 100);
        return mergedTeams;
    }
    
    /**
     * Gives the boolean required for the sorting algorithm.
     *
     * @param highToLow  True if the returned value should be sorted highest to lowest, false otherwise
     * @param sortOption This is the value the program will use to sort the teams.
     * @param t1         The first team for order checking
     * @param t2         The second team for order checking
     * @return The result of the boolean (true for the first team, false for the second team)
     */
    private static boolean mergeBool(boolean highToLow, String sortOption, Team t1, Team t2)
    {
        if (t1 == null || t2 == null)
        {
            return false;
        }
        
        double t1Ratio = 1.0 * t1.getWins() / t1.getLosses();
        double t2Ratio = 1.0 * t2.getWins() / t2.getLosses();
        
        return (highToLow) ? switch (sortOption)
        {
            case "wins" -> t1.getWins() > t2.getWins(); //Wins high to low
            case "losses" -> t1.getLosses() > t2.getLosses(); //Losses high to low
            default -> t1Ratio > t2Ratio; //Ratio high to low
        } : switch (sortOption)
        {
            case "wins" -> t1.getWins() < t2.getWins(); //Wins low to high
            case "losses" -> t1.getLosses() < t2.getLosses(); //Losses low to high
            default -> t1Ratio < t2Ratio; //Ratio low to high
        };
    }
    
    /**
     * Prints a single team with its stats
     *
     * @param team The Team to print
     */
    public static void printSingleTeamStats(Team team)
    {
        //Print each Monster in the team
        for (Monster mon : team.getMonsters())
        {
            System.out.printf("%s\t\t", mon.getName(true, false));
        }
        System.out.printf("Number of wins: %,d\tNumber of losses: %,d", team.getWins(), team.getLosses());
        
        //Calculate the win-loss ratio
        double ratio = 1.0 * team.getWins() / (team.getLosses() + team.getWins());
        ratio *= 100;
        if (team.getLosses() == 0)
        {
            if (team.getWins() == 0)
            {
                ratio = 0.0;
            }
        }
        
        //Print win-loss ratio
        System.out.printf("\tWin/Loss Ratio: %f", ratio);
        System.out.println();
    }
    
    /**
     * Exports current results to a csv file
     *
     * @param i The last i value used in the loop
     * @param j The last j value used in the loop
     * @return The name of the newly created file or null if the file could not be created
     */
    public static String exportResults(int i, int j)
    {
        //Initialize the list of teams
        ArrayList<String> lines = new ArrayList<>();
        
        //Add the library to the top of the file
        final String[] temp = {""};
        monsterKeys.forEach((name) -> temp[0] += "%s:%d,".formatted(name, monsterKeys.indexOf(name)));
        lines.add(temp[0]);
        if (!completed)
        {
            lines.add("%d,%d".formatted(i, j));
            lines.add("%d,%d".formatted(totalRunningTime.getElapsedTime(), battleTime.getElapsedTime()));
        }
        
        //Add each team to the list
        for (Team team : teamStats)
        {
            lines.add(compressTeam(team));
        }
        
        Date today = new Date();
        //Try to crate a file using the current date and time as a name and write to it
        String fileName;
        try
        {
            //Create the file
            fileName = "%s/src/Game/Results/%s.csv".formatted(Auto_Play.class.getResource("Auto_Play.class").getPath().substring(0, Auto_Play.class.getResource("Auto_Play.class").getPath().indexOf("Summoners%20War%20Battle%20Simulator") + 36), today.toString().replaceAll(":", "-")).replaceAll("%20", " ").replaceAll("file:", "");
            File f = new File(fileName);
            FileWriter writer = new FileWriter(f);
            //Add each line to file
            for (String line : lines)
            {
                writer.write("%s\n".formatted(line));
            }
            writer.close();
            return fileName;
        }
        //Create a random seed name in case date file name is already taken
        catch (IOException e)
        {
            try
            {
                //Create the file
                fileName = "%ssrc/Game/Results/%s.csv".formatted(Auto_Play.class.getResource("Auto_Play.class").getPath().substring(0, Auto_Play.class.getResource("Auto_Play.class").getPath().indexOf("Summoners%20War%20Battle%20Simulator") + 36), new Random().nextDouble(0, 10));
                FileWriter writer = new FileWriter(fileName);
                
                //Add each line to file
                for (String line : lines)
                {
                    writer.write("%s\n".formatted(line));
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
            //Ask the user if they want to whitelist, blacklist, or do neither
            System.out.println("Would you like to whitelist or blacklist any monsters? (\"w\" for whitelist, \"b\" for blacklist, \"n\" for no)");
            String response = scan.nextLine();
            switch (response)
            {
                //Whitelist
                case "w" ->
                {
                    String monName = "";
                    ArrayList<Monster> whitelistedMonsters = new ArrayList<>();
                    while (true)
                    {
                        //Get Monster name
                        while (!Monster.stringIsMonsterName(monName, mons) && !monName.equals("exit"))
                        {
                            //Print current whitelisted Monsters
                            System.out.println("Type the next monster to whitelist or \"exit\" to exit");
                            System.out.print("Current Monsters: ");
                            whitelistedMonsters.forEach(m -> System.out.printf("%s\t\t", m.getName(true, false)));
                            System.out.println();
                            //Get the next Monster
                            monName = scan.nextLine();
                        }
                        //Exit filtering
                        if (monName.equals("exit"))
                        {
                            //Check there are enough Monsters
                            if (whitelistedMonsters.size() < 5 && limitSelection)
                            {
                                monName = "";
                                System.out.println("Error, must include at least 5 monsters");
                                continue;
                            }
                            return whitelistedMonsters;
                        }
                        //Move Monster to whitelist
                        moveToFilteredList(mons, monName, whitelistedMonsters);
                        monName = "";
                    }
                }
                //Blacklist
                case "b" ->
                {
                    String monName = "";
                    ArrayList<Monster> blacklistedMonsters = new ArrayList<>();
                    while (true)
                    {
                        //Get Monster name
                        while (!Monster.stringIsMonsterName(monName, mons) && !monName.equals("exit"))
                        {
                            //Print current blacklisted Monsters
                            System.out.println("Type the next monster to blacklist or \"exit\" to exit");
                            System.out.print("Current Monsters: ");
                            blacklistedMonsters.forEach(m -> System.out.printf("%s\t\t", m.getName(true, false)));
                            System.out.println();
                            //Get the next Monster
                            monName = scan.nextLine();
                        }
                        //Exit filtering
                        if (monName.equals("exit"))
                        {
                            return mons;
                        }
                        //Check there are enough Monsters left to remove another one
                        if (mons.size() == 5 && limitSelection)
                        {
                            monName = "";
                            System.out.println("Error, must include at least 5 monsters");
                            continue;
                        }
                        //Move Monster to blacklist
                        moveToFilteredList(mons, monName, blacklistedMonsters);
                        monName = "";
                    }
                }
                //No filter
                case "n" ->
                {
                    return mons;
                }
            }
        }
    }
    
    /**
     * Creates a new Monster from the given name, adds it to the filtered list, and removes it from the unfiltered list
     *
     * @param unfilteredMonsters The unfiltered monsters
     * @param monName            The name of the Monster
     * @param filteredMonsters   The filtered monsters
     */
    private static void moveToFilteredList(ArrayList<Monster> unfilteredMonsters, String monName, ArrayList<Monster> filteredMonsters)
    {
        //Create Monster from name
        Monster mon;
        try
        {
            mon = Monster.createNewMonFromName(monName, true);
        }
        catch (Exception e)
        {
            System.out.println("Unable to find Monster");
            return;
        }
        
        //Move Monster from the unfiltered list to the filtered one
        for (int i = 0; i < unfilteredMonsters.size(); i++)
        {
            if (unfilteredMonsters.get(i).getName(false, false).equals(mon.getName(false, false)))
            {
                filteredMonsters.add(unfilteredMonsters.remove(i));
                break;
            }
        }
    }
    
    /**
     * Filters the teams by Monsters (original list is unchanged)
     *
     * @param teams The teams to filter
     * @return The filtered teams
     */
    public static ArrayList<Team> filterTeams(final ArrayList<Team> teams)
    {
        //Get filtered Monsters
        ArrayList<Monster> filteredMonsters = filterMonsters(Monster.getMonstersFromDatabase(), false);
        Team tempFilteredTeam = new Team("Filtered", filteredMonsters);
        //Create a temporary list so the original list is unchanged
        ArrayList<Team> temp = new ArrayList<>(teams);
        
        //Filter teams
        outer:
        for (int i = temp.size() - 1; i >= 0; i--)
        {
            Team t = temp.get(i);
            //Remove the team if it has a Monster that is not included in the filter
            for (Monster teamMonster : t.getMonsters())
            {
                if (!tempFilteredTeam.hasInstanceOf(teamMonster))
                {
                    temp.remove(t);
                    continue outer;
                }
            }
        }
        return temp;
    }
    
    /**
     * Compresses a team into a line to export to a file
     *
     * @param team The team to compress
     * @return A String containing a single line containing the team
     */
    private static String compressTeam(Team team)
    {
        String line = "";
        //Add each Monsters key denoted by the library
        for (Monster mon : team.getMonsters())
        {
            line += "%d,".formatted(monsterKeys.indexOf(mon.getName(false, false)));
        }
        //Add wins and losses
        line += "%d,%d,".formatted(team.getWins(), team.getLosses());
        return line;
    }
}