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

    public static SESSAD sessad;

    //create getter and setter for antColony
    public static ArrayList<Integer>[][] getSolution() {
        return solution;
    }

    public static void main(String[] args) {

        String folderPath = "instances/";
        File instanceFolder = new File(folderPath);
        File[] listOfFiles = instanceFolder.listFiles();
        //if the folder is empty, exit the program
        if (listOfFiles.length == 0) {
            System.out.println("No instances found. Exiting the program.");
            return;
        }
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
        sessad = new SESSAD();

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


        int nb_ants = 200;
        float alpha = 0.95f;
        float beta = 0.01f;
        float rho = 0.8f;
        int teta = 1000;
        int max_time = 120;
        
        try{
            AntColony antColony = new AntColony(sessad, nb_ants, alpha, beta, rho);

            // starting a timer
            long startTime = System.currentTimeMillis();

            solution = antColony.solve(teta, max_time);
            // convert timer to seconds
            long elapsedTime = System.currentTimeMillis() - startTime;
            elapsedTimeSec = elapsedTime / 1000F;

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
                for (int j = 0; j < solution[i].length; j++){
                    if (solution[i][j].size() > maxShifts) {
                        maxShifts = solution[i][j].size();
                    }
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
                            if(solution[i][j].get(k) >= sessad.center_name.length){
                                System.out.format("%-10s", sessad.ConvertADayAndMissionNumberToMissionId(j, solution[i][j].get(k)-sessad.center_name.length)+1);
                            }else{
                                System.out.format("%-10s", sessad.center_name[solution[i][j].get(k)]);
                            }
                        } else {
                            System.out.format("%-10s", "");
                        }
                    }
                    System.out.println();
                }
                System.out.println();
                            
            }
            LaunchUI.main();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}