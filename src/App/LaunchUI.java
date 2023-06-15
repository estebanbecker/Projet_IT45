package App;

import Graph.Graph;

import javax.swing.*;

import static UI.GraphEditor.createAndShowGUI;
import static java.lang.Thread.sleep;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class LaunchUI {

    public static Color[] colors;

    public static Color[] getColors() {
        return colors;
    }

    public static void main() throws InterruptedException {
        sleep(1000);
        UI.main();
        Graph graph1 = new Graph();

        //use the getter for coordinates
        double[][] coordinates = UI.getCoordinates();
        ArrayList<Integer>[][] solution = Problem.App.getSolution();


        //use graph.createANode(null, null); for each node in coordinates
        //for the array of graphs
        for (int i = 0; i < coordinates.length; i++) {
            graph1.createANode((float) coordinates[i][0], (float) coordinates[i][1]);
        }


        //create an array of random colors for each employee
        //make sure those colors are earthy colors
        colors = new Color[solution.length];
        for (int i = 0; i < solution.length; i++) {
            Random rand = new Random();
            float r = rand.nextFloat() / 2f + 0.5f;
            float g = rand.nextFloat() / 2f + 0.5f;
            float b = rand.nextFloat() / 2f + 0.5f;
            colors[i] = new Color(r, g, b);
        }

        for(int i = 0; i < solution.length; i++) {
            for(int j = 0; j < solution[i].length; j++) {
                for(int k = 0; k < solution[i][j].size()-1; k++) {
                    graph1.connectUnidirectionalNodes(solution[i][j].get(k), solution[i][j].get(k + 1), "Day " + (j + 1) + "", colors[i]);
                }
            }
            //System.out.println();
        }




        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(graph1);
        });

    }

    public static void newProgram() {
        // Update the current graph with the newGraph
        // kill the current createAndShowGUI
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(new Graph());
        });
    }

    public static void restartProgram(Graph newGraph) {
        // Update the current graph with the newGraph
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(newGraph);
        });
    }
}