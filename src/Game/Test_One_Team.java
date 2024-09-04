package Game;

import Monsters.*;
import java.util.*;

import static Game.Auto_Play.generateCombinations;
import static Game.Main.printMonsToPick;

/**
 * This class is used to test one Team against all others
 *
 * @author Anthony (Tony) Youssef
 */
public class Test_One_Team
{
    private static final Scanner scan = new Scanner(System.in);
    private static int mainTeamWins = 0, numOfBattles = 0;
    
    /**
     * Runs this program
     */
    
    void main()
    {
        Monster.setPrint(false);
        Team mainTeam = setTeam();
        System.out.println("Battling...");
        ArrayList<Monster> allMons = Monster.getMonstersFromDatabase();
        ArrayList<ArrayList<Monster>> allTeams = generateCombinations(allMons, 4);
        for (ArrayList<Monster> enemyMons : allTeams)
        {
            Team enemyTeam = new Team("Team 2", enemyMons);
            Auto_Play.resetTeam(mainTeam.getMonsters());
            Auto_Play.resetTeam(enemyTeam.getMonsters());
            Main.setRuneEffectsAndNames(mainTeam, enemyTeam);
            Game g = Auto_Play.battle(mainTeam, enemyTeam);
            numOfBattles++;
            if (g.getWinningTeam().getName().equals("Team 1"))
            {
                mainTeamWins++;
            }
        }
        
        System.out.println("Number of wins: " + mainTeamWins);
        System.out.println("Number of battles: " + numOfBattles);
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
                printMonsToPick(monstersPicked);
                System.out.print("\nCurrent team: ");
                for (Monster mon : monstersPicked)
                {
                    System.out.print(mon.getName(true, false) + ", ");
                }
                System.out.println("\nChoose your monster");
                inputMon = scan.nextLine();
            }
            while (!Monster.stringIsMonsterName(inputMon) || Team.teamHasMon(inputMon, monstersPicked));
            
            //Add Monster to team
            Main.addMonToTeam(monstersPicked, inputMon);
        }
        return new Team("Team 1", monstersPicked);
    }
}
