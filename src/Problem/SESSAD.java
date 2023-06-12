package Problem;

import java.util.ArrayList;

public class SESSAD {

    // Distance is composed of 5 matrixes, each one for a different day
    public Float[][][] distance;

    public Mission[] mission;

    public String[] center_name;

    public Employee[] employee;

    public String name;

    public Integer [][] missionPerDay;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Mission{" +
                ", name=" + name +
                '}';
    }

    public boolean is_it_better(ArrayList<Integer>[][] solution1, ArrayList<Integer>[][] solution2) {

        // Check the number of missions in each solution

        int nb_mission1 = 0;
        int nb_mission2 = 0;

        for (int i = 0; i < solution1.length; i++) {
            for (int j = 0; j < solution1[i].length; j++) {
                nb_mission1 += solution1[i][j].size();
                nb_mission2 += solution2[i][j].size();
            }
        }

        if (nb_mission1 > nb_mission2) {
            return true;
        } else if (nb_mission1 < nb_mission2) {
            return false;
        }

        // Check the distance of each solution

        float distance1 = 0;
        float distance2 = 0;

        for (int i = 0; i < solution1.length; i++) {
            for (int j = 0; j < solution1[i].length; j++) {
                for (int k = 0; k < solution1[i][j].size() - 1; k++) {
                    distance1 += this.distance[i][solution1[i][j].get(k)][solution1[i][j].get(k + 1)];
                    distance2 += this.distance[i][solution2[i][j].get(k)][solution2[i][j].get(k + 1)];
                }
            }
        }

        if (distance1 < distance2) {
            return true;
        } else if (distance1 > distance2) {
            return false;
        }

        // Check the number of mission where the speciality are the same

        int nb_mission_same_speciality1 = 0;
        int nb_mission_same_speciality2 = 0;

        for (int i = 0; i < solution1.length; i++) {
            for (int j = 0; j < solution1[i].length; j++) {
                for (int k = 0; k < solution1[i][j].size(); k++) {
                    if (this.employee[i].specialite == this.mission[solution1[i][j].get(k)].specialite) {
                        nb_mission_same_speciality1++;
                    }
                    if (this.employee[i].specialite == this.mission[solution2[i][j].get(k)].specialite) {
                        nb_mission_same_speciality2++;
                    }
                }
            }
        }

        if (nb_mission_same_speciality1 > nb_mission_same_speciality2) {
            return true;
        } else {
            return false;
        }

    }

    public ArrayList<Integer>[][] make_it_valid(ArrayList<Integer>[][] solution) {
        // Check that each mission is done maximum one time
        for (int i = 0; i < solution.length; i++) {
            for (int j = 0; j < solution[i].length; j++) {
                for (int k = 0; k < solution[i][j].size(); k++) {
                    for (int l = j + 1; l < solution[i].length; l++) {
                        for (int m = k + 1; m < solution[i][l].size(); m++) {
                            if(solution[i][j].get(k) > center_name.length && solution[i][l].get(m) > center_name.length)
                            {
                                if (solution[i][j].get(k) == solution[i][l].get(m) && solution[i][j].get(k) != -1
                                        && solution[i][l].get(k) >= this.center_name.length) {
                                    solution = repair(solution, i, j, k);
                                    m -= 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        return solution;
    }

    public ArrayList<Integer>[][] repair(ArrayList<Integer>[][] solution, int i, int j, int k) {

        // make two deep copies of the solution
        ArrayList<Integer>[][] solution1 = deepcopy(solution);
        ArrayList<Integer>[][] solution2 = deepcopy(solution);

        // remove the mission from the first solution
        solution1[i][j].remove(k);

        // remove the mission from the second solution
        solution2[i][j].remove(k);

        // compare the two solutions
        if (is_it_better(solution1, solution2)) {
            return solution1;
        } else {
            return solution2;
        }
    }

    public ArrayList<Integer>[][] deepcopy(ArrayList<Integer>[][] originalArray) {

        // create a new array with the same dimensions as the original array
        ArrayList<Integer>[][] newArray = new ArrayList[originalArray.length][originalArray[0].length];

        // iterate over the elements of the original array and create a new ArrayList
        // for each element
        for (int i = 0; i < originalArray.length; i++) {
            for (int j = 0; j < originalArray[i].length; j++) {
                newArray[i][j] = new ArrayList<Integer>(originalArray[i][j]);
            }
        }

        return newArray;
    }

    public Integer ConvertADayAndMissionNumberToMissionId(int day, int mission_number) {
        return this.missionPerDay[day][mission_number]-1;
    }

}
