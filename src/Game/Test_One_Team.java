package Game;

import Monsters.*;
import java.util.*;

import static Game.Auto_Play.generateCombinations;
import static Game.Main.printMonsToPick;
import static Game.Main.scan;

/**
 * This class is used to test one Team against all others
 *
 * @author Anthony (Tony) Youssef
 */
public class Test_One_Team
{
    private static int mainTeamWins = 0, numOfBattles = 0;
    
    /**
     * Runs this program
     */
    public void main()
    {
        //Prevent battles from printing anything
        Monster.setPrint(false);
        Team mainTeam = setTeam();
        System.out.println("Setting up...");
        //Get Monsters from the global database
        ArrayList<Monster> allMons = Monster.getMonstersFromDatabase();
        //Generate combinations
        ArrayList<ArrayList<Monster>> allTeams = generateCombinations(allMons, 4, false);
        System.out.println("Battling...");
        //Simulate battles
        for (ArrayList<Monster> enemyMons : allTeams)
        {
            Team enemyTeam = new Team("Team 2", enemyMons);
            //Reset teams
            Auto_Play.resetTeamForMemory(mainTeam.getMonsters());
            Auto_Play.resetTeamForMemory(enemyTeam.getMonsters());
            Main.setRuneEffectsAndNames(mainTeam, enemyTeam);
            Game g = Auto_Play.battle(mainTeam, enemyTeam);
            numOfBattles++;
            //Increase wins if the testing team won
            if (g.getWinningTeam().getName().equals("Team 1"))
            {
                mainTeamWins++;
            }
        }
        
        //Print information
        System.out.printf("Number of wins: %d%n", mainTeamWins);
        System.out.printf("Number of battles: %d%n", numOfBattles);
    }
    
    /**
     * Sets the Team to test.
     *
     * @return The main Team to test
     */
    private static Team setTeam()
    {
        ArrayList<Monster> monstersPicked = new ArrayList<>();
        String inputMon;
        while (monstersPicked.size() < 4)
        {
            do
            {
                //Print potential Monsters
                printMonsToPick(monstersPicked);
                
                //Print current team
                System.out.print("\nCurrent team: ");
                for (Monster mon : monstersPicked)
                {
                    System.out.printf("%s, ", mon.getName(true, false));
                }
                
                //Get next Monster's name
                System.out.println("\nChoose your monster");
                inputMon = scan.nextLine();
            }
            while (!Monster.stringIsMonsterName(inputMon) || Team.teamHasMon(inputMon, monstersPicked));
            
            //Add Monster to team
            //Attempt to add the Monster to the team
            if (!Main.addMonToTeam(monstersPicked, inputMon))
            {
                scan.nextLine();
            }
        }
        return new Team("Team 1", monstersPicked);
    }
}
