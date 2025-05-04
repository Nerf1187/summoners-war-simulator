# How to run

Go to the [Executables](Executables) Directory, then open the folder of the class you want to run and double-click the .bat file (titled "RUN ME").
You must have Java 23 or higher installed to run the program

# Main

The [Main](src/Game/Main.java) class will allow you to run with preset teams or choose your own teams and rune sets.
Teams cannot have repeat Monsters (ex. Team 1 cannot have two Lushens, even if there are two different rune sets)

# Auto Play

The [Auto_Play](src/Game/Auto_Play.java) class is currently set up to run every possible team composition against each other and print the four best
teams when it is finished.
Press enter at any time to pause the program and view the best teams at the time.
Ending the program will also display the top four teams at the time.
The program will only run with default rune sets and NO leader skills to limit the number of simulations

# Test One Team

The [Test_One_Team](src/Game/Test_One_Team.java) allows you to create a Team and simulate it in a battle against every other possible Team

# Runes

The [Runes](src/GUI/Runes.java) class is an easier way to work with rune sets.
You will first be asked to enter a Monster name and rune set number then choose what action to complete.
The program will handle everything to do with the files themselves. Just supply the information requested

# Read Results

The [Read_Results](src/Game/Read_Results.java) class allows you to view results of past Auto_Play runs.
File names are determined by the date and time of their creation if possible, otherwise a randomly generated number is used as the name.

# Rune parser

The [Rune_Parser](src/Runes/Rune_Parser.java) class allows you to import runes from a JSON file and either create new rune sets or replace and update the files already in the project.
Visit <a href="https://tool.swop.one">Summoner's War Optimizer</a> to learn how to get a JSON file for your Summoners War account.

# Developer Website

The [Developer website](src/Developer_Website/index.html) is a locally run website to help with creating Monster classes.
(I'm not great with frontend design, so give me a break with the visuals).
Instructions for how to use it are in [Monster Creation Instructions.md](Monster%20Creation%20Instructions.md).