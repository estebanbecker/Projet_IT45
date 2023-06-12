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
                    distance1 += this.distance[j][solution1[i][j].get(k)][solution1[i][j].get(k + 1)];
                }
                for (int k = 0; k < solution2[i][j].size() - 1; k++) {
                    distance2 += this.distance[j][solution2[i][j].get(k)][solution2[i][j].get(k + 1)];
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
                    
                    if(solution1[i][j].get(k) >= this.center_name.length){
                        int mission_id1 = ConvertADayAndMissionNumberToMissionId(j, solution1[i][j].get(k) - this.center_name.length);
                        if (this.employee[i].specialite == this.mission[mission_id1].specialite) {
                            nb_mission_same_speciality1++;
                        }
                    }

                }
            }
        }

        for(int i = 0; i < solution2.length; i++){
            for(int j = 0; j < solution2[i].length; j++){
                for(int k = 0; k < solution2[i][j].size(); k++){
                    
                    if(solution2[i][j].get(k) >= this.center_name.length){
                        int mission_id2 = ConvertADayAndMissionNumberToMissionId(j, solution2[i][j].get(k) - this.center_name.length);
                        if(this.employee[i].specialite == this.mission[mission_id2].specialite){
                            nb_mission_same_speciality2++;
                        }
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
            for (int j = 0; j < solution.length; j++) {
                for (int k = 0; k < solution[i].length; k++) {
                    for(int l = 0; l < solution[i][k].size(); l++){
                        for(int m = 0; m < solution[j][k].size(); m++){
                            while(solution[i][k].get(l) == solution[j][k].get(m) && solution[i][k].get(l) > this.center_name.length && (i != j || l != m)){
                                solution = repair(solution, i, j, k, l, m);
                            }
                        }
                        
                    }
                }
            }
        }
        return solution;
    }

    public ArrayList<Integer>[][] repair(ArrayList<Integer>[][] solution, int i1, int i2, int j, int k1, int k2) {

        // make two deep copies of the solution
        ArrayList<Integer>[][] solution1 = deepcopy(solution);
        ArrayList<Integer>[][] solution2 = deepcopy(solution);

        // remove the mission from the first solution
        solution1[i1][j].remove(k1);

        // remove the mission from the second solution
        solution2[i2][j].remove(k2);

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
