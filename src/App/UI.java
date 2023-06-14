package App;
import org.apache.commons.math3.util.FastMath;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;





public class UI {
    public static double[][] getCoordinates() {
        return coordinates;
    }

    public static void setCoordinates(double[][] coordinates) {
        UI.coordinates = coordinates;
    }

    public static double [][] coordinates;

    public static void main() {

        double[][] distances = {
                {0, 15.333, 29.786, 15.805},
                {15.333, 0, 19.934, 2.1135},
                {29.786, 19.934, 0, 17.907},
                {15.805, 2.1135, 17.907, 0}
        };

        String csvFilePath = "instances/150Missions-2centres/distances.csv";

        distances = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (distances == null) {
                    distances = new double[values.length][];
                }

                distances[row] = new double[values.length];
                for (int col = 0; col < values.length; col++) {
                    distances[row][col] = Double.parseDouble(values[col]);
                }

                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int numNodes = distances.length;

        Random random = new Random();
        coordinates = new double[numNodes][2];
        for (int i = 0; i < numNodes; i++) {
            coordinates[i][0] = random.nextDouble();
            coordinates[i][1] = random.nextDouble();
        }

        // Run the Fruchterman-Reingold algorithm
        double area = 1000;  // Initial area of the layout
        double k = FastMath.sqrt(area / numNodes);  // Optimal distance between nodes
        double maxIterations = 1000;  // Maximum number of iterations
        double temperature = area / 10;  // Initial temperature

        for (int iteration = 0; iteration < maxIterations && temperature > 0.01; iteration++) {
            // Calculate repulsive forces between nodes
            double[][] displacement = new double[numNodes][2];
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < numNodes; j++) {
                    if (i != j) {
                        double dx = coordinates[i][0] - coordinates[j][0];
                        double dy = coordinates[i][1] - coordinates[j][1];
                        double distance = FastMath.sqrt(dx * dx + dy * dy);
                        double force = k * k / distance;
                        displacement[i][0] += force * dx / distance;
                        displacement[i][1] += force * dy / distance;
                    }
                }
            }

            // Calculate attractive forces between adjacent nodes
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < numNodes; j++) {
                    if (i != j) {
                        double dx = coordinates[i][0] - coordinates[j][0];
                        double dy = coordinates[i][1] - coordinates[j][1];
                        double distance = FastMath.sqrt(dx * dx + dy * dy);
                        double force = distance * distance / k;
                        displacement[i][0] -= force * dx / distance;
                        displacement[i][1] -= force * dy / distance;
                    }
                }
            }

            // Update node coordinates based on the displacement
            for (int i = 0; i < numNodes; i++) {
                double distance = FastMath.sqrt(displacement[i][0] * displacement[i][0] + displacement[i][1] * displacement[i][1]);
                if (distance > 0) {
                    coordinates[i][0] += displacement[i][0] / distance * FastMath.min(distance, temperature);
                    coordinates[i][1] += displacement[i][1] / distance * FastMath.min(distance, temperature);
                }
            }
            //normalise coordinates to the orioginal area
            double maxX = 100;
            double maxY = 100;
            for (int i = 0; i < numNodes; i++) {
                if (coordinates[i][0] > maxX) {
                    maxX = coordinates[i][0];
                }
                if (coordinates[i][1] > maxY) {
                    maxY = coordinates[i][1];
                }
            }
            for (int i = 0; i < numNodes; i++) {
                coordinates[i][0] = coordinates[i][0] * 1000 / maxX;
                coordinates[i][1] = coordinates[i][1] * 1000 / maxY;
            }
            // Cool temperature
            temperature *= (1.0 - iteration / maxIterations);


        }

        // Print the final coordinates
        for (int i = 0; i < numNodes; i++) {
            System.out.printf("Node %d: (%f, %f)%n", i, coordinates[i][0], coordinates[i][1]);
        }
    }


}
