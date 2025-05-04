package Game;

import Monsters.*;
import Util.Util.*;
import java.util.*;

import static Util.Util.CONSOLE_INTERFACE.OUTPUT.printfWithColor;

/**
 * This class is a manual battle simulation. You can either play with preset Teams or pick Teams in the program (pick 5, ban 1)
 */
public class Main
{
    /**
     * Scanner
     */
    public static final Scanner scan = new Scanner(System.in);
    
    /**
     * Runs this program
     */
    public void main()
    {
        //Create team ArrayLists
        ArrayList<Monster> mons1;
        ArrayList<Monster> mons2;
        
        //Select teams
        ArrayList<ArrayList<Monster>> teams = CONSOLE_INTERFACE.INPUT.selectTeams();
        mons1 = teams.get(0);
        mons2 = teams.get(1);
        
        //Set teams
        Team team1 = new Team("Team 1", mons1);
        Team team2 = new Team("Team 2", mons2);
        
        //Activate rune set effects
        MONSTERS.setNamesAndRuneEffects(team1, team2);
        
        //Pick leaders for each team
        CONSOLE_INTERFACE.INPUT.pickLeaders(team1, team2);
        
        //Create game
        Game game = new Game(team1, team2);
        Monster.setGame(game);
        
        //Run the game
        game = battle(game);
        
        //Print which team won
        printfWithColor("%s wins!", ConsoleColor.BLUE_BOLD_BRIGHT, game.getWinningTeam().getName());
    }
    
    /**
     * Executes the battle logic for the given game.
     * The battle continues until the game reaches an end state.
     *
     * @param game The game instance containing the setup and state for the battle.
     * @return The modified game instance after the battle concludes.
     */
    protected Game battle(Game game)
    {
        Monster.setGame(game);
        //Play game
        battleLoop:
        while (!game.endGame())
        {
            switch (CHOOSERS.MANUAL.nextTurn(game))
            {
                case CONTINUE:
                    continue;
                case BREAK:
                    break battleLoop;
            }
        }
        return game;
    }
    
    /**
     * Initiates a battle between two teams by creating a new game instance with the given teams
     * and invoking the battle logic on it.
     *
     * @param team1 The first team participating in the battle.
     * @param team2 The second team participating in the battle.
     * @return The Game instance representing the outcome of the battle.
     */
    protected Game battle(Team team1, Team team2)
    {
        return battle(new Game(team1, team2));
    }
    
    /**
     * Pauses the program for a given time
     *
     * @param time The amount of time (in milliseconds) to pause
     */
    public static void pause(int time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch (InterruptedException ignored)
        {
        }
    }
    
    /**
     * Attempts to create a new Monster from the given name and add it to a team
     *
     * @param teamToAdd The team to add the new Monster to
     * @param inputMon  The name of the Monster
     * @return True if the Monster was successfully added, false otherwise
     */
    public static boolean addMonToTeam(ArrayList<Monster> teamToAdd, String inputMon)
    {
        //Get the rune set number to use
        int runeSetNum = CONSOLE_INTERFACE.INPUT.getRuneSetNum();
        
        //Try to create a Monster with the given rune set number
        Monster monToAdd = null;
        try
        {
            monToAdd = MONSTERS.createNewMonFromName(inputMon, Math.abs(runeSetNum), true);
        }
        catch (Exception ignored)
        {
        }
        
        //Return false if the Monster could not be added
        if (monToAdd == null)
        {
            return false;
        }
        
        //Add the Monster to the team
        teamToAdd.add(monToAdd);
        
        //Scan the next line cause Java is weird
        if (runeSetNum != -1)
        {
            scan.nextLine();
        }
        System.out.println();
        return true;
    }
}