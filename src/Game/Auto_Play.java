package Game;

import Monsters.*;
import Util.Util.*;
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
    private static boolean pause = false, endThread = false, completed = false, prioritizeSpd;
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
        //Show results when the program unnaturally ends
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //Total simulations run
            System.out.printf("\n\nTotal number of simulations ran: %,d%n", numOfCompletedSimulations);
            totalRunningTime.pause();
            updateBestTeams();
            //Total time elapsed while running (not including while paused)
            System.out.printf("Total time elapsed: \t%s%n", STRINGS.toReadableTime(totalRunningTime.getElapsedTime()));
            //Time spent simulating battles
            System.out.printf("Time elapsed during battle: %s%n", STRINGS.toReadableTime(battleTime.getElapsedTime()));
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
                String fileName = TEAMS.exportResults(i, j, completed, teamStats, totalRunningTime, battleTime);
                
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
        CONSOLE_INTERFACE.pauseMenu(teamStats);
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
     * @param prioritizeSpd Whether the user wants to save time or memory
     */
    public void main(ArrayList<Team> teams, ArrayList<String> library, long totalTime, long _battleTime, int _i, int _j, boolean prioritizeSpd)
    {
        teamStats = teams;
        monsterKeys.addAll(library);
        totalRunningTime = new StopWatch(false, totalTime);
        battleTime = new StopWatch(false, _battleTime);
        i = _i;
        j = _j;
        numOfCompletedSimulations = STATISTICS.calculateNumOfPreCompletedSims(_i, _j, teams.size());
        Auto_Play.prioritizeSpd = prioritizeSpd;
        main();
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
     * Sets the status of the simulation calculation error.
     *
     * @param simsCalculationError A boolean value indicating whether there is a simulation
     *                              calculation error (true for error, false otherwise).
     */
    public static void setSimsCalculationError(boolean simsCalculationError)
    {
        Auto_Play.simsCalculationError = simsCalculationError;
    }
    
    /**
     * Retrieves the list of monster keys.
     *
     * @return An ArrayList of strings representing the monster keys.
     */
    public static ArrayList<String> getMonsterKeys()
    {
        return monsterKeys;
    }
    
    /**
     * Creates every possible Team combination (ignores leader skills to decrease the number of combinations) and simulates each of their battles
     */
    public static void allPossibleTeams()
    {
        boolean prioritizeSpeed;
        ArrayList<Team> allPossibleTeams = new ArrayList<>();
        if (teamStats.isEmpty())
        {
            //Filter Monsters
            ArrayList<Monster> allMons = FILTER_AND_SORT.filterMonsters(Monster.getMonstersFromDatabase(), true);
            //Add Monster names to the keys list
            for (Monster mon : allMons)
            {
                monsterKeys.add(mon.getName(false, false));
            }
            
            //Ask if the user wants to save time or memory
            String response = CONSOLE_INTERFACE.INPUT.getSpecificString("Do you want to prioritize speed or memory? (\"s\" for speed, \"m\" for memory, \"info\" to learn more)",
                    "s", "m", "info");
            
            while (response.equalsIgnoreCase("info"))
            {
                System.out.println("Choosing speed is much faster but takes up more RAM and takes longer to create the teams.");
                System.out.println("Choosing memory takes up less RAM and creates the teams faster but takes much longer to finish.");
                response = CONSOLE_INTERFACE.INPUT.getSpecificString("Do you want to prioritize speed or memory? (\"s\" for speed, \"m\" for memory, \"info\" to learn more)",
                    "s", "m", "info");
            }
            prioritizeSpeed = response.equalsIgnoreCase("s");
            
            //Create teams
            System.out.println("Creating teams...");
            ArrayList<ArrayList<Monster>> allPossibleTeamMonsters = STATISTICS.generateCombinations(allMons, 4, prioritizeSpeed);
            //Add teams to list
            for (ArrayList<Monster> list : allPossibleTeamMonsters)
            {
                allPossibleTeams.add(new Team("temp", list));
            }
            //Randomize order
            Collections.shuffle(allPossibleTeams);
            //Calculate the total number of simulations to be run
            totalSims = STATISTICS.totalNumOfSims(allPossibleTeams.size());
            teamStats = allPossibleTeams;
        }
        else
        {
            allPossibleTeams = teamStats;
            prioritizeSpeed = prioritizeSpd;
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
                        totalSims = STATISTICS.totalNumOfSims(allPossibleTeams.size());
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
                    System.out.printf("Total elapsed time: %s%n", STRINGS.toReadableTime(totalRunningTime.getElapsedTime()));
                    System.out.printf("Time elapsed during battle: %s%n", STRINGS.toReadableTime(battleTime.getElapsedTime()));
                    System.out.printf("Estimated time remaining: %s%s%n", STRINGS.toReadableTime(timeRemaining), end);
                    System.out.println();
                    
                    //Allow the user to view current standings
                    CONSOLE_INTERFACE.pauseMenu(teamStats);
                    
                    //Exit pause
                    pause = false;
                    System.out.println("Running");
                    totalRunningTime.play();
                }
                final Team contender = allPossibleTeams.get(j);
                
                //Reset teams
                if (prioritizeSpeed)
                {
                    TEAMS.resetTeamForTime(current);
                    TEAMS.resetTeamForTime(contender);
                }
                else //Save memory
                {
                    TEAMS.resetTeamForMemory(current.getMonsters());
                    TEAMS.resetTeamForMemory(contender.getMonsters());
                    MONSTERS.setNamesAndRuneEffects(current, contender);
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
        
        battleLoop:
        while (!game.endGame())
        {
            switch (CHOOSERS.AUTO.nextTurn(game))
            {
                case CONTINUE:
                    continue;
                case BREAK:
                    break battleLoop;
            }
            
            //Check for dead Monsters
            for (Monster mon : team1)
            {
                if (mon.isDead())
                {
                    mon.kill();
                    
                    if (!deadMons.containsKey(mon))
                    {
                        deadMons.put(mon, turnNumber);
                    }
                }
            }
            for (Monster mon : team2)
            {
                if (mon.isDead())
                {
                    mon.kill();
                    
                    if (!deadMons.containsKey(mon))
                    {
                        deadMons.put(mon, turnNumber);
                    }
                }
            }
            
            //End battle if more than 150 turns have passed
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
     * Gets the Game object currently in use
     *
     * @return The Game object currently in use
     */
    public static Game getGame()
    {
        return game;
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
     * Retrieves the current progress information of the simulations.
     *
     * @return An array of objects containing the progress information:
     *         i - the current value of the outer loop index,
     *         j - the current value of the inner loop index,
     *         completed - the number of completed simulations,
     *         totalRunningTime - the total running time of the simulations,
     *         battleTime - the time spent on battles.
     */
    public static Object[] getProgressInfo()
    {
        return new Object[] {i, j, completed, totalRunningTime, battleTime};
    }
}