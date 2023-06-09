package Problem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        String csvFile = "instances/30Missions-2centres/distances.csv";
        String line;
        String csvSplitBy = ",";
        int citiesCount = 30; // Assuming you have the number of cities specified
        distanceMatrix dm = new distanceMatrix(citiesCount);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            Float[][] distancesArray = new Float[citiesCount+2][citiesCount+2];
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] distances = line.split(csvSplitBy);
                for (int col = 2; col < citiesCount+2; col++) {
                    //give the method in float to convert the string to float
                    //limit the float to 2 decimal places
                    distancesArray[row][col] = Float.parseFloat(distances[col]);
                    //put those inside dm as well
                    dm.setDistances(row,col,Float.parseFloat(distances[col]));
                }
                row++;
            }

            // Print the distancesArray

            System.out.println("\n");
            for (int i = 2; i < citiesCount+2; i++) {
                System.out.print(i-1 + "\t");
                for (int j = 2; j < citiesCount+2; j++) {
                    //System.out.print(distancesArray[i][j] + "\t");
                    //print out the dm as well

                    System.out.print(dm.getDistances(i,j) + "\t");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String csvFile2 = "instances/30Missions-2centres/Missions.csv";
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



            // Print the missions with day information
            for (Mission mission : Missions) {
                int dayIndex = mission.getDay();
                System.out.println("Mission: " + mission.getId() + ", Day: " + dayIndex + ", Day Name: " + dayMapping.get(dayIndex) + ", Distance= " + dm.getDistances(mission.getId(),2));
            }

            System.out.println("Missions for monday: " + Mission.getMissionIdsForDay(1, Missions));
            System.out.println("Missions for tuesday: " + Mission.getMissionIdsForDay(2, Missions));
            System.out.println("Missions for wednesday: " + Mission.getMissionIdsForDay(3, Missions));
            System.out.println("Missions for thursday: " + Mission.getMissionIdsForDay(4, Missions));
            System.out.println("Missions for friday: " + Mission.getMissionIdsForDay(5, Missions));

            Float[][][] missionarray = Mission.createMissionIndexArrayByDay(Missions);

            System.out.println("\n");
            for (int i = 0; i < 5; i++) {
                System.out.print(i+1 + "\n");
                for (int j = 0; j < missionarray[i].length; j++) {
                    for (int k = 0; k < missionarray[i][j].length; k++) {
                        System.out.print(missionarray[i][j][k] + "\t");
                    }
                    System.out.println();
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        String csvFile3 = "instances/30Missions-2centres/Employees.csv";
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
            for (Employee employee : employees) {
                System.out.println(employee);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String csvFile4 = "instances/30Missions-2centres/centers.csv";
        List<SESSAD> SESSADS = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(csvFile4))) {

            while ((line = br.readLine()) != null) {
                String[] centerData = line.split(csvSplitBy);
                SESSAD SESSAD = new SESSAD();
                //REMOVE BOM IN INTELLIJ
                //System.out.println(Integer.parseInt(centerData[0].replaceFirst("\uFEFF", "")));
                SESSAD.setId(Integer.parseInt(centerData[0].replaceFirst("\uFEFF", "")));
                //center.setId(Integer.parseInt(centerData[0]));
                SESSAD.setName(centerData[1]);
                SESSADS.add(SESSAD);
            }

            // Print the employees
            for (SESSAD SESSAD : SESSADS) {
                System.out.println(SESSAD);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}