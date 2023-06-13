package Problem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Solver.AntColony;

public class App {
    public static void main(String[] args) {

        String folder = "instances/30Missions-2centres/";
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
                employee.setCenter_id(Integer.parseInt(employeeData[1]));
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

        // // Variation values for parameters 2-5
        // int[] parameter2Variations = {10, 100, 1000, 10000};
        // float parameter3Variations = 0.8f;
        // float[] parameter4Variations = { 1f, 0.9f, 0.85f, 0.8f };
        // float parameter5 = 0f;

        // for (int parameter2 : parameter2Variations) {
        //     for (float i=parameter3Variations; i<10f; i+=0.5f) {
        //         for (float parameter4 : parameter4Variations) {
        //             for (float j=parameter5; j<1f; j+=0.1f) {
                         try {

                            AntColony antColony = new AntColony(sessad, 1, 0.5f, 0.5f, 0.5f);
                            
                            System.out.println("Starting to solve");
                            
                            // starting a timer
                            long startTime = System.currentTimeMillis();
                            
                            ArrayList<Integer>[][] solution = antColony.solve(10, 120);
                            // convert timer to seconds
                            long elapsedTime = System.currentTimeMillis() - startTime;
                            float elapsedTimeSec = elapsedTime / 1000F;
                            
                            System.out.println("Finished solving in " + elapsedTimeSec + " seconds");
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
        //             }
        //         }
        //     }
        // }

    }
}