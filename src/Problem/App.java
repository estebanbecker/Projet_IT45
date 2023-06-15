package Problem;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

import App.LaunchUI;

import Benchmark.Benchmark;
import Solver.AntColony;

public class App {

    public static String folder;
    public static int[] parameter2Variations;
    public static float parameter3Variations;
    public static float[] parameter4Variations;
    public static float parameter5Variations;
    public static float elapsedTimeSec;

    public static ArrayList<Integer>[][] solution;

    //create getter and setter for antColony
    public static ArrayList<Integer>[][] getSolution() {
        return solution;
    }

    public static void main(String[] args) {

        String folderPath = "instances/";
        File instanceFolder = new File(folderPath);
        File[] listOfFiles = instanceFolder.listFiles();
        Arrays.sort(listOfFiles, Comparator.comparingInt(o -> Integer.parseInt(o.getName().split("Missions")[0])));
        int o = 0;
        //check if valid, folder contains centers.csv, distances.csv, missions.csv and employees.csv
        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                File[] filesInFolder = file.listFiles();
                boolean containsCenters = false;
                boolean containsDistances = false;
                boolean containsMissions = false;
                boolean containsEmployees = false;

                for (File subFile : filesInFolder) {
                    String fileName = subFile.getName();
                    if (fileName.equals("centers.csv") || fileName.equals("Centers.csv") || fileName.equals("centres.csv") || fileName.equals("Centres.csv")) {
                        containsCenters = true;
                    } else if (fileName.equals("distances.csv") || fileName.equals("Distances.csv")){
                        containsDistances = true;
                    } else if (fileName.equals("missions.csv") || fileName.equals("Missions.csv")){
                        containsMissions = true;
                    } else if (fileName.equals("employees.csv") || fileName.equals("Employees.csv")){
                        containsEmployees = true;
                    }
                }

                if (containsCenters && containsDistances && containsMissions && containsEmployees) {
                    System.out.println("[" + o + "] " + file.getName());
                    o++;
                }
            }
        }
        // Get user input for the instance to run
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of the instance you want to run: ");
        int instance = scanner.nextInt();

        if (instance < 0 || instance >= listOfFiles.length) {
            System.out.println("Invalid instance number. Exiting the program.");
            return;
        }

        // Get the folder name of the instance to run
        String instanceFolderName = listOfFiles[instance].getName();
        // Get the folder path of the instance to run
        folder = folderPath + instanceFolderName + "/";


        //folder = "instances/200Missions-2centres/";
        String csvFile = folder + "distances.csv";
        String line;
        String csvSplitBy = ",";
        SESSAD sessad = new SESSAD();

        String csvFile2 = folder + "Missions.csv";
        List<Mission> Missions = new ArrayList<>();
        Map<Integer, String> dayMapping = new HashMap<>();
        dayMapping.put(1, "Monday");
        dayMapping.put(2, "Tuesday");
        dayMapping.put(3, "Wednesday");
        dayMapping.put(4, "Thursday");
        dayMapping.put(5, "Friday");
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile2))) {

            // Read the remaining lines of the file
            while ((line = br.readLine()) != null) {
                String[] missionData = line.split(csvSplitBy);
                Mission mission = new Mission();
                mission.setId(Integer.parseInt(missionData[0]));
                mission.setDay(Integer.parseInt(missionData[1]));
                mission.setStart_time(Integer.parseInt(missionData[2]));
                mission.setEnd_time(Integer.parseInt(missionData[3]));
                mission.setCompetence(missionData[4]);
                mission.setSpecialite(missionData[5]);
                Missions.add(mission);
            }

            System.out.println("Missions for monday: " + Mission.getMissionIdsForDay(1, Missions));
            System.out.println("Missions for tuesday: " + Mission.getMissionIdsForDay(2, Missions));
            System.out.println("Missions for wednesday: " + Mission.getMissionIdsForDay(3, Missions));
            System.out.println("Missions for thursday: " + Mission.getMissionIdsForDay(4, Missions));
            System.out.println("Missions for friday: " + Mission.getMissionIdsForDay(5, Missions));

            Integer[][] missionarray = Mission.createMissionIndexArrayByDay(Missions);

            System.out.println("\n");
            for (int i = 0; i < 5; i++) {
                System.out.print(i + 1 + "\n");
                for (int j = 0; j < missionarray[i].length; j++) {
                    System.out.print(missionarray[i][j] + "\t");
                    System.out.println();
                }
                System.out.println();
            }

            sessad.missionPerDay = missionarray;

            sessad.mission = Missions.toArray(new Mission[Missions.size()]);

        } catch (IOException e) {
            e.printStackTrace();
        }

        String csvFile3 = folder + "Employees.csv";
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile3))) {
            while ((line = br.readLine()) != null) {
                String[] employeeData = line.split(csvSplitBy);
                Employee employee = new Employee();
                employee.setId(Integer.parseInt(employeeData[0]));
                employee.setCenter_id(Integer.parseInt(employeeData[1]) - 1);
                employee.setCompetence(employeeData[2]);
                employee.setSpecialite(employeeData[3]);
                employees.add(employee);
            }

            // Print the employees
            sessad.employee = employees.toArray(new Employee[employees.size()]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String csvFile4 = folder + "centres.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile4))) {

            ArrayList<String> centerNames = new ArrayList<String>();

            while ((line = br.readLine()) != null) {
                String[] centerData = line.split(csvSplitBy);
                centerNames.add(centerData[1]);
            }

            sessad.center_name = centerNames.toArray(new String[centerNames.size()]);

        } catch (IOException e) {
            e.printStackTrace();
        }

        int citiesCount = sessad.mission.length + sessad.center_name.length; // Assuming you have the number of cities
        // specified
        Float[][] dm = new Float[citiesCount][citiesCount];

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] distanceData = line.split(csvSplitBy);
                for (int j = 0; j < citiesCount; j++) {
                    dm[i][j] = Float.parseFloat(distanceData[j]);
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Now but the distance metrix into the right structure with 5 matrix for each
        // day

        sessad.distance = new Float[5][][];

        for (int i = 0; i < 5; i++) {
            sessad.distance[i] = new Float[sessad.missionPerDay[i].length
                    + sessad.center_name.length][sessad.missionPerDay[i].length + sessad.center_name.length];

            Integer[] missionIds = new Integer[sessad.missionPerDay[i].length + sessad.center_name.length];

            for (int j = 0; j < sessad.center_name.length; j++) {
                missionIds[j] = j;
            }

            for (int j = 0; j < sessad.missionPerDay[i].length; j++) {
                missionIds[j + sessad.center_name.length] = sessad.missionPerDay[i][j] + 1;
            }

            for (int j = 0; j < missionIds.length; j++) {
                for (int k = 0; k < missionIds.length; k++) {
                    sessad.distance[i][j][k] = dm[missionIds[j]][missionIds[k]];
                }
            }

        }

        System.out.println("Finished loading data");


        //Variation values for parameters 2-5
        parameter2Variations = new int[]{10/*, 100, 1000, 10000*/};
        parameter3Variations = 0.2f;
        parameter4Variations = new float[]{0.2f/*, 0.4f, 0.6f, 0.8f, 1f*/};
        parameter5Variations = 0.0f;

        String outcsvFile = folder + "benchmark.csv";
        File file = new File(outcsvFile);
        try {
            if (!file.exists()) {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write("nb_ant,alpha,beta,rho,distance,time,nb_mission,nb_specialite\n");
                writer.close();
            }

            for (int parameter2 : parameter2Variations) {
                for (float parmeter3 = parameter3Variations; parmeter3 <= 1f; parmeter3 += /*0.2f*/ 5) {
                    for (float parameter4 : parameter4Variations) {
                        for (float paramter5 = parameter5Variations; paramter5 <= 1f; paramter5 += /*0.2f*/5) {
                            float sum_dist = 0;
                            float sum_time = 0;
                            float sum_nb_mission = 0;
                            float sum_nb_specialite = 0;
                            for (int g = 0; g < /*5*/1; g++) {

                                AntColony antColony = new AntColony(sessad, parameter2, parmeter3, parameter4, paramter5);

                                System.out.println("With praameters: nb_ant=" + parameter2 + ", alpha=" + parmeter3 + ", beta=" + parameter4 + ", rho=" + paramter5);

                                // starting a timer
                                long startTime = System.currentTimeMillis();

                                solution = antColony.solve(100, 120);
                                // convert timer to seconds
                                long elapsedTime = System.currentTimeMillis() - startTime;
                                elapsedTimeSec = elapsedTime / 1000F;

                                sum_dist += antColony.distance(solution);
                                sum_time += elapsedTimeSec;
                                sum_nb_mission += antColony.nb_mission(solution);
                                sum_nb_specialite += antColony.nb_mission_same_speciality(solution);

                                System.out.println("Finished solving in " + elapsedTimeSec + " seconds");

                                System.out.println("");

                                for (int i = 0; i < solution.length; i++) {
                                    System.out.print("Employee " + (i + 1) + ": \n");
                                    for (int j = 0; j < solution[i].length; j++) {
                                        System.out.print("Day " + (j + 1) + ": \n");
                                        for (int k = 0; k < solution[i][j].size(); k++) {
                                            System.out.print(solution[i][j].get(k) + " \n");
                                        }
                                    }
                                    System.out.println();
                                }
                                //Benchmark.main();
                                //}
                                int maxDays = solution[0].length;
                                int maxShifts = 0;
                                for (int i = 0; i < solution.length; i++) {
                                    if (solution[i][0].size() > maxShifts) {
                                        maxShifts = solution[i][0].size();
                                    }
                                }
                                System.out.print("         ");
                                for (int i = 0; i < solution.length; i++) {
                                    System.out.format("%-10s", " " + (i + 1));
                                }
                                System.out.println();

                                for (int j = 0; j < maxDays; j++) {
                                    System.out.println("Day " + (j + 1) + ":");

                                    for (int k = 0; k < maxShifts; k++) {
                                        System.out.format("%-10s", "Shift " + (k + 1));
                                        for (int i = 0; i < solution.length; i++) {
                                            if (j < solution[i].length && k < solution[i][j].size()) {
                                                System.out.format("%-10s", solution[i][j].get(k) + 1);
                                            } else {
                                                System.out.format("%-10s", "");
                                            }
                                        }
                                        System.out.println();
                                    }
                                    System.out.println();
                                }
                            }

                            //Print the avarange and the parameters in a CSV file
                            FileWriter writer = new FileWriter(file, true);
                            writer.write(parameter2 + "," + parmeter3 + "," + parameter4 + "," + paramter5 + "," + sum_dist / 5 + "," + sum_time / 5 + "," + sum_nb_mission / 5 + "," + sum_nb_specialite / 5 + "\n");
                            writer.close();
                        }
                    }
                }
            }
            LaunchUI.main();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}