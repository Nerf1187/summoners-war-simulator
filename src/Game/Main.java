package Game;

import Monsters.Dark.*;
import Monsters.Fire.*;
import Monsters.*;
import Monsters.Light.*;
import Monsters.Water.*;
import Monsters.Wind.*;
import Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * This class is a manual battle simulation. You can either play with preset Teams or pick Teams in the program (pick 5, ban 1)
 */
public class Main
{
    /**
     * Scanner
     */
    public static final Scanner scan = new Scanner(System.in);
    
    //Boolean to make sure the acting Monster isn't changed when changing abilities
    private static boolean monChosen = false;
    
    private static Monster next = null;
    
    /**
     * Runs this program
     */
    public void main()
    {
        //Initialize Monster database
        Monster.setDatabase();
        //Create team ArrayLists
        ArrayList<Monster> mons1;
        ArrayList<Monster> mons2;
        
        //Select teams
        ArrayList<ArrayList<Monster>> teams = selectTeams();
        mons1 = teams.get(0);
        mons2 = teams.get(1);
        
        //Set teams
        Team team1 = new Team("Team 1", mons1);
        Team team2 = new Team("Team 2", mons2);
        
        //Activate rune set effects
        setRuneEffectsAndNames(team1, team2);
        
        //Pick leaders for each team
        pickLeaders(team1, team2);
        
        //Create game
        Game game = new Game(team1, team2);
        Monster.setGame(game);
        
        //Run the game
        game = battle(game);
        
        //Print which team won
        System.out.printf("%s wins!", ConsoleColors.BLUE_BOLD_BRIGHT + game.getWinningTeam().getName());
    }
    
    protected static Game battle(Game game)
    {
        //Play game
        while (!game.endGame())
        {
            //Increment attack bars until at least one is full
            if (!game.hasFullAtkBar())
            {
                game.increaseAtkBar();
                continue;
            }
            
            //Print game and find who is next
            System.out.printf("%n%n%s%n%n%n", game);
            
            //Delay output
            pause(1000);
            
            //Get acting Monster
            if (!monChosen)
            {
                Team teamWithHighestAtkBar = game.getTeamWithHighestAtkBar();
                next = teamWithHighestAtkBar.MonsterWithHighestFullAtkBar();
            }
            //Print acting Monster
            System.out.println(next);
            //Delay output
            pause(1000);
            
            //Activate before turn passives
            game.activateBeforeTurnPassives(next);
            
            //Apply buffs and debuffs before the Monster's turn starts
            if (!monChosen)
            {
                game.applyStats(next);
            }
            
            //If the monster's Hp falls below zero before turn
            if (next.getCurrentHp() <= 0)
            {
                next.kill();
            }
            
            //Print next mon
            System.out.println(next.shortToString(true));
            
            //Check if the Monster is stunned
            if (next.isStunned())
            {
                next.decreaseStatCooldowns();
                next.setAtkBar(0);
                continue;
            }
            
            //Go to next monster if current is dead
            if (next.getCurrentHp() <= 0)
            {
                next.kill();
                monChosen = false;
                if (next.isDead())
                {
                    continue;
                }
            }
            
            //Ends game if a team is dead
            if (game.endGame())
            {
                break;
            }
            //Delay output
            pause(300);
            
            //Check for Provoke
            Provoke p = next.getProvoke();
            if (p != null)
            {
                System.out.printf("%sProvoked!%s%n", ConsoleColors.YELLOW, ConsoleColors.RESET);
                Monster caster = p.getCaster();
                next.nextTurn(caster, 1);
                continue;
            }
            
            //Checks if any Monster on the other team has Threat
            boolean threat = game.getOtherTeam().monHasThreat();
            
            //Get ability number/print buff and debuff descriptions
            monChosen = true;
            int abilityNum = Game.getAbilityNum(next);
            
            //Get target num
            Monster targetMon = game.getTarget(next, abilityNum, threat);
            //Re-choose ability if no target selected
            if (targetMon == null)
            {
                continue;
            }
            
            //Start the Monster's turn
            Game.applyNextTurn(next, targetMon, abilityNum);
            monChosen = false;
            //Delay output
            pause(800);
        }
        return game;
    }
    
    protected static Game battle(Team team1, Team team2)
    {
        return battle(new Game(team1, team2));
    }
    
    /**
     * Prints the stun effect to the console
     */
    protected static void printStunEffect()
    {
        if (Monster.isPrint())
        {
            System.out.println("Stunned!");
            pause(200);
        }
    }
    
    /**
     * Prints the sleep effect to the console
     */
    protected static void printSleepEffect()
    {
        if (Monster.isPrint())
        {
            System.out.println("Zzzzz... Slept!");
            pause(200);
        }
    }
    
    /**
     * Prints the freeze effect to the console
     */
    protected static void printFreezeEffect()
    {
        if (Monster.isPrint())
        {
            System.out.printf("%s%sBrrr... Frozen!%s%n", ConsoleColors.CYAN_BACKGROUND_BRIGHT, ConsoleColors.BLACK_BOLD, ConsoleColors.RESET);
            pause(200);
        }
    }
    
    /**
     * Prints the bomb explosion effect
     *
     * @param target The Monster with the bomb
     */
    protected static void printBombExplodeEffect(Monster target)
    {
        
        if (Monster.isPrint())
        {
            System.out.printf("%s%sBOOM!%s Bomb exploded! You took %,f damage! %s has %,d health left!%n", ConsoleColors.RED_BACKGROUND_BRIGHT, ConsoleColors.BLACK_BOLD, ConsoleColors.RESET, target.getMaxHp() * 0.4, target.getName(true, true),
                    Math.max(target.getCurrentHp(), 0));
        }
        
        printStunEffect();
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
     * Converts the given String to a Monster
     *
     * @param name The name of the Monster
     * @param mons The List of Monsters to search in
     * @return The Monster whose name equals the given String
     */
    private static Monster getMonFromName(String name, ArrayList<Monster> mons)
    {
        //Search the list for the Monster
        for (Monster mon : mons)
        {
            if (mon.getName(false, false).equalsIgnoreCase(name))
            {
                return mon;
            }
        }
        //Return a blank Monster if the name was not found
        return new Monster();
    }
    
    /**
     * Adds the elemental color to the given String
     *
     * @param name    The name of the Monster
     * @param element The element of the Monster
     * @return The name of the Monster with the appropriate elemental color
     */
    private static String nameWithElement(String name, String element)
    {
        return switch (element.toLowerCase())
               {
                   case "fire" -> ConsoleColors.RED_BOLD_BRIGHT;
                   case "water" -> ConsoleColors.BLUE_BOLD_BRIGHT;
                   case "wind" -> ConsoleColors.YELLOW_BOLD_BRIGHT;
                   case "light" -> ConsoleColors.WHITE_BOLD_BRIGHT;
                   case "dark" -> ConsoleColors.PURPLE_BOLD_BRIGHT;
                   default -> "";
               } + name + ConsoleColors.RESET;
    }
    
    /**
     * Prints the Monsters that can still be picked for the Team
     *
     * @param monsPicked The Monsters that cannot be picked
     */
    protected static void printMonsToPick(ArrayList<Monster> monsPicked)
    {
        ArrayList<String> toPrint = new ArrayList<>();
        ArrayList<String> fire = new ArrayList<>();
        ArrayList<String> water = new ArrayList<>();
        ArrayList<String> wind = new ArrayList<>();
        ArrayList<String> light = new ArrayList<>();
        ArrayList<String> dark = new ArrayList<>();
        try
        {
            //Read from the Monster database
            Scanner read = new Scanner(Objects.requireNonNull(Monster.class.getResourceAsStream("Monster database.csv")));
            while (read.hasNextLine())
            {
                String line = read.nextLine();
                if (line.isEmpty())
                {
                    continue;
                }
                String[] monAndElement = line.split(",");
                String name = nameWithElement(monAndElement[0], monAndElement[1]);
                //Monster has not been picked yet
                if (getMonFromName(monAndElement[0], monsPicked).equals(new Monster()))
                {
                    //Add Monster to the proper array
                    switch (monAndElement[1].toLowerCase())
                    {
                        case "fire" -> fire.add(name);
                        case "water" -> water.add(name);
                        case "wind" -> wind.add(name);
                        case "light" -> light.add(name);
                        case "dark" -> dark.add(name);
                    }
                }
            }
            //Sort the arrays alphabetically
            Collections.sort(water);
            Collections.sort(fire);
            Collections.sort(wind);
            Collections.sort(light);
            Collections.sort(dark);
            
            //Combine the arrays
            toPrint.addAll(water);
            toPrint.addAll(fire);
            toPrint.addAll(wind);
            toPrint.addAll(light);
            toPrint.addAll(dark);
            
            //Print each Monster
            for (String s : toPrint)
            {
                System.out.printf("%s      ", s);
            }
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Allows the user to either play with preset Teams or pick the Monsters for each Team
     *
     * @return The two Teams chosen by the user
     */
    public static ArrayList<ArrayList<Monster>> selectTeams()
    {
        ArrayList<ArrayList<Monster>> teams = new ArrayList<>();
        
        //Create team ArrayLists
        ArrayList<Monster> mons1 = new ArrayList<>();
        ArrayList<Monster> mons2 = new ArrayList<>();
        
        //Skips monster selection and starts game with pre-built Teams if user answers "y"
        String test;
        if (!Monster.monsterNamesDatabase.isEmpty())
        {
            System.out.println("Preset teams? (Type \"y\" for yes)");
            test = scan.nextLine();
        }
        else
        {
            test = "y";
        }
        //Preset teams (Mainly for testing/debugging)
        if (test.equalsIgnoreCase("y"))
        {
            //Team 1
            mons1.add(new Dominic());
            mons1.add(new Loren());
            mons1.add(new Alice());
            mons1.add(new Feng_Yan());
            
            //Team 2
            mons2.add(new Ariel());
            mons2.add(new Dominic());
            mons2.add(new Riley());
            mons2.add(new Evan());
        }
        
        //4v4
        //Choose monsters 5 monsters for each team then ban 1
        if (!test.equals("y"))
        {
            //Pick mons
            while (mons1.size() < 5 || mons2.size() < 5)
            {
                String inputMon;
                //Team 1 pick
                if (mons1.size() < 5 && mons1.size() == mons2.size())
                {
                    do
                    {
                        //Print potential Monsters
                        printMonsToPick(mons1);
                        
                        //Print current team
                        System.out.print("\nTeam 1 current team: ");
                        for (Monster mon : mons1)
                        {
                            System.out.printf("%s, ", mon.getName(true, false));
                        }
                        
                        //Get the next Monster's name
                        System.out.println("\nTeam 1 choose your monster (type \"inspect\" to inspect a monster)");
                        inputMon = scan.nextLine();
                        
                        //Inspect monster
                        if (inputMon.equals("inspect"))
                        {
                            Monster.inspect();
                        }
                    }
                    while (!Monster.stringIsMonsterName(inputMon) || Team.teamHasMon(inputMon, mons1));
                    
                    //Attempt to add the Monster to the team
                    if (!addMonToTeam(mons1, inputMon))
                    {
                        scan.nextLine();
                        continue;
                    }
                }
                
                
                //Team 2 pick
                if (mons2.size() < 5 && mons1.size() == mons2.size() + 1)
                {
                    do
                    {
                        //Print potential Monsters
                        printMonsToPick(mons2);
                        
                        //Print current team
                        System.out.print("\nTeam 2 current team: ");
                        for (Monster mon : mons2)
                        {
                            System.out.printf("%s, ", mon.getName(true, false));
                        }
                        
                        //Get the next Monster's name
                        System.out.println("\nTeam 2 choose your monster (type \"inspect\" to inspect a monster)");
                        inputMon = scan.nextLine();
                        
                        //Inspect monster
                        if (inputMon.equals("inspect"))
                        {
                            Monster.inspect();
                        }
                    }
                    while (!Monster.stringIsMonsterName(inputMon) || Team.teamHasMon(inputMon, mons2));
                    
                    //Attempt to add the Monster to the team
                    if (!addMonToTeam(mons2, inputMon))
                    {
                        scan.nextLine();
                    }
                }
            }
            
            //Ban one Monster from the other team
            String banName;
            //Team 2 choosing
            do
            {
                System.out.println("Team 2 choose one monster from Team 1 to ban.");
                
                //Print team 1 Monsters
                for (Monster mon : mons1)
                {
                    System.out.printf("%s, ", mon.getName(true, false));
                }
                System.out.println();
                
                //Get input
                banName = scan.nextLine();
                
                //Inspect
                if (banName.equals("inspect"))
                {
                    Monster.inspect();
                }
            }
            while (!Monster.stringIsMonsterName(banName, mons1));
            
            //Remove Monster from team 1
            mons1.remove(getMonFromName(banName, mons1));
            
            //Team 1 choosing
            do
            {
                System.out.println("Team 1 choose one monster from Team 2 to ban.");
                
                //Print team 2 Monsters
                for (Monster mon : mons2)
                {
                    System.out.printf("%s, ", mon.getName(true, false));
                }
                System.out.println();
                
                //Get input
                banName = scan.nextLine();
                
                //Inspect
                if (banName.equals("inspect"))
                {
                    Monster.inspect();
                }
            }
            while (!Monster.stringIsMonsterName(banName, mons2));
            
            //Remove Monster from team 2
            mons2.remove(getMonFromName(banName, mons2));
        }
        
        //Return final teams
        teams.add(mons1);
        teams.add(mons2);
        return teams;
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
        int runeSetNum = getRuneSetNum();
        
        //Try to create a Monster with the given rune set number
        Monster monToAdd = null;
        try
        {
            monToAdd = Monster.createNewMonFromName(inputMon, Math.abs(runeSetNum), true);
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
    
    /**
     * Activates the team-based rune effects (e.g., fight, shield) and sets the names for each Monster
     *
     * @param team1 The first Team to apply
     * @param team2 The second Team to apply
     */
    public static void setRuneEffectsAndNames(Team team1, Team team2)
    {
        //Implement ally rune effects
        setRuneEffects(team1);
        setRuneEffects(team2);
        
        //Set names for each team
        for (Monster mon : team1.getMonsters())
        {
            mon.setName("%s(1)".formatted(mon.getName(false, false)));
        }
        for (Monster mon : team2.getMonsters())
        {
            mon.setName("%s(2)".formatted(mon.getName(false, false)));
        }
    }
    
    /**
     * Sets ally rune effects for a single team
     *
     * @param team The team to apply rune effects for
     */
    private static void setRuneEffects(Team team)
    {
        for (Monster mon : team.getMonsters())
        {
            for (Monster mon1 : team.getMonsters())
            {
                //Apply rune effects for each Monster
                mon1.addAppliedBuff(new Rune_shield((int) Math.ceil(mon.getMaxHp() * (0.15 * mon.numOfSets(Rune.SHIELD))), 3), mon);
                mon1.setAtk((int) Math.ceil(mon1.getAtk() + mon1.getBaseAtk() * (0.08 * mon.numOfSets(Rune.FIGHT))));
                mon1.setDef((int) Math.ceil(mon1.getDef() + mon1.getBaseDef() * (0.08 * mon.numOfSets(Rune.DETERMINATION))));
                mon1.setMaxHp((int) Math.ceil(mon1.getMaxHp() + mon1.getBaseMaxHp() * (0.08 * mon.numOfSets(Rune.ENHANCE))));
                mon1.setAccuracy((mon1.getAccuracy() + (10 * mon.numOfSets(Rune.ACCURACY))));
                mon1.setResistance(mon1.getResistance() + (10 * mon.numOfSets(Rune.TOLERANCE)));
                mon1.setCurrentHp(mon1.getMaxHp());
            }
        }
    }
    
    /**
     * Gets the rune set number to apply to the Monster
     *
     * @return The rune set number to apply
     */
    public static int getRuneSetNum()
    {
        int runeSetNum = 0;
        do
        {
            System.out.println("Type rune set number or \"d\" for default");
            try
            {
                runeSetNum = Math.max((scan.nextInt()), 0);
            }
            catch (InputMismatchException e)
            {
                if (scan.nextLine().equals("d"))
                {
                    runeSetNum = -1;
                }
            }
        }
        while (runeSetNum == 0);
        return runeSetNum;
    }
    
    /**
     * Allows the user to pick leaders for each Team if a leader is available
     *
     * @param t1 The first Team
     * @param t2 The second Team
     */
    public static void pickLeaders(Team t1, Team t2)
    {
        ArrayList<Monster> mons1 = new ArrayList<>();
        ArrayList<Monster> mons2 = new ArrayList<>();
        for (Monster mon : t1.getMonsters())
        {
            if (mon.hasLeaderSkill())
            {
                mons1.add(mon);
            }
        }
        for (Monster mon : t2.getMonsters())
        {
            if (mon.hasLeaderSkill())
            {
                mons2.add(mon);
            }
        }
        Team pickLeader1 = new Team("1", mons1);
        Team pickLeader2 = new Team("2", mons2);
        
        getLeader(pickLeader1, t1);
        getLeader(pickLeader2, t2);
    }
    
    /**
     * Prompt the user to select a leader Monster for the Team if possible, prints a message otherwise.
     *
     * @param potentialLeaders A list of potential leaders.
     * @param team             The Team to apply the leader skill to.
     */
    private static void getLeader(Team potentialLeaders, Team team)
    {
        //Do nothing if there are no available leaders
        if (!potentialLeaders.getMonsters().isEmpty())
        {
            String inputMon;
            //Get monster name
            do
            {
                System.out.printf("Team %s pick a leader by typing their name (Type \"inspect\" to inspect a Monster or \"none\" for no leader)%n", potentialLeaders.getName());
                System.out.println(potentialLeaders);
                inputMon = scan.nextLine();
                if (inputMon.equals("inspect"))
                {
                    Monster.inspect();
                }
                else if (inputMon.equals("none"))
                {
                    return;
                }
            }
            while (!Monster.stringIsMonsterName(inputMon, potentialLeaders.getMonsters()));
            //Get leader
            Monster leader = getMonFromName(inputMon, potentialLeaders.getMonsters());
            //Apply leader skill to the team
            leader.applyLeaderSkill(team);
        }
        else
        {
            System.out.println("No leader skill available.");
        }
    }
}