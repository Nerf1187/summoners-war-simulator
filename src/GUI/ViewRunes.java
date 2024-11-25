package GUI;

import javax.swing.*;
import javax.swing.table.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Runes.*;
import java.util.*;

/**
 * This class is used to view a rune file without editing it
 *
 * @author Anthony (Tony) Youssef
 */
public class ViewRunes extends JFrame
{
    private JPanel panel;
    private JTextPane textArea;
    private JButton exitButton;
    private JTable table;
    private JButton toEditButton;
    
    private static ArrayList<String[]> tableValues;
    private int selectedRow = -1;
    
    /**
     * Creates the GUI.
     */
    private ViewRunes(String fileName)
    {
        //General GUI stuff
        add(panel);
        setTitle("View %s".formatted(fileName));
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        //Exit viewing
        exitButton.addActionListener(_ -> System.exit(0));
        
        //Go to edit runes
        toEditButton.addActionListener(_ -> {
            dispose();
            EditRuneFile.run(fileName, table.getSelectedRow());
        });
    }
    
    /**
     * Runs the GUI
     *
     * @param fileName The name of the file to view
     */
    public static void run(String fileName)
    {
        //Get the Monster's runes
        ArrayList<Rune> runes = MonsterRunes.getRunesFromFile(fileName, new Monster());
        ArrayList<String[]> rowValues = new ArrayList<>();
        int i = 0;
        for (Rune rune : runes)
        {
            rowValues.add(new String[4]);
            //Rune number
            rowValues.get(i)[0] = "%d".formatted(i + 1);
            //Rune type
            rowValues.get(i)[1] = (Rune.numToType(rune.getType()));
            //Main attribute
            rowValues.get(i)[2] = (rune.getMainAttribute().toString());
            //Sub attributes
            String tempText = "";
            String bufferText = ",            ";
            for (SubAttribute subAttribute : rune.getSubAttributes())
            {
                tempText += subAttribute.toString() + bufferText;
            }
            //Make sure there is a sub attribute
            if (tempText.length() > 2)
            {
                tempText = tempText.substring(0, tempText.length() - bufferText.length());
            }
            else
            {
                tempText = "None";
            }
            rowValues.get(i)[3] = (tempText);
            i++;
        }
        tableValues = rowValues;
        new ViewRunes(fileName);
    }
    
    /**
     * Creates the table to display the runes.
     */
    private void createUIComponents()
    {
        //Set the table column names
        String[] columnNames = {"Place", "Type", "Main Attribute", "Sub Attributes"};
        
        //Convert table values to array
        String[][] temp = new String[tableValues.size()][4];
        for (int i = 0; i < tableValues.size(); i++)
        {
            temp[i] = tableValues.get(i);
        }
        
        //Set table size
        table = new JTable(temp, columnNames);
        TableColumnModel tc = table.getColumnModel();
        tc.getColumn(0).setPreferredWidth(75);
        tc.getColumn(0).setMaxWidth(75);
        tc.getColumn(1).setPreferredWidth(125);
        tc.getColumn(1).setMaxWidth(125);
        tc.getColumn(2).setPreferredWidth(100);
        tc.getColumn(2).setMaxWidth(100);
        tc.getColumn(3).setPreferredWidth(500);
        table.setRowHeight(30);
    }
}
