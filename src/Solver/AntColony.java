package Solver;

import java.util.ArrayList;

import Problem.SESSAD;

public class AntColony {
    
    private SESSAD sessad;
    private int nb_ants;
    private float alpha;
    private float beta;
    private float rho;


    private AntGroup[] ants;

    private float pheromone[][][][];

    private ArrayList<Integer>[][] best_solution;

    private int nb_jour;

    private int[] nb_mission_par_jour;

    public AntColony(SESSAD sessad, int nb_ants, float alpha, float beta, float rho) {
        this.sessad = sessad;
        this.nb_ants = nb_ants;
        this.alpha = alpha;
        this.beta = beta;
        this.rho = rho;

        //Get the maximum number of days in the problem

        nb_jour = 0;
        for (int i = 0; i < sessad.mission.length; i++) {
            if (sessad.mission[i].day > nb_jour) {
                nb_jour = sessad.mission[i].day;
            }
        }

        nb_mission_par_jour = new int[nb_jour];

        //Get the number of missions per day in the problem
        for(int i = 0; i < sessad.mission.length; i++) {
            nb_mission_par_jour[sessad.mission[i].day-1]++;
        }


        
    }

    public ArrayList<Integer>[][] solve(int teta, float maximum_time){
        initPheromone();
        best_solution = new ArrayList[sessad.employee.length][nb_jour];
        for(int i = 0; i < sessad.employee.length; i++) {
            for(int j = 0; j < nb_jour; j++) {
                best_solution[i][j] = new ArrayList<Integer>();
            }
        }
        long current_mission_time = System.currentTimeMillis();
        int nb_iteration_without_improvement = 0;
        Thread[] threads = new Thread[nb_ants];

        ants = new AntGroup[nb_ants];

        AntGroup best_ant = new AntGroup(sessad, nb_iteration_without_improvement, pheromone, current_mission_time, teta, nb_iteration_without_improvement);
        int nb_iteration = 0;
        while((System.currentTimeMillis() - current_mission_time) /1000f < maximum_time && nb_iteration_without_improvement < teta) {
            nb_iteration++;
            for(int i = 0; i < nb_ants; i++) {
                ants[i] = new AntGroup(sessad, i, pheromone, alpha, beta,nb_jour);
                
                threads[i] = new Thread(ants[i]);
                threads[i].start();
            }   
            for(int i = 0; i < nb_ants; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            boolean improvement = false;
            for(int i = 0; i < nb_ants; i++) {
                if(ants[i].solution!= null){
                    if(sessad.is_it_better(ants[i].solution, best_solution)) {
                        best_solution = ants[i].solution;
                        best_ant = ants[i];
                        improvement = true;
                    }
                }
            }
            if(improvement) {
                nb_iteration_without_improvement = 0;
                System.out.println("New best solution at iteration " + nb_iteration);
                printSolution(best_solution);
                System.out.println("");
            } else {
                nb_iteration_without_improvement++;
            }
            updatePheromone();
        }
        printSolution(best_solution);

        System.out.println("Working time");

        for(int i = 0; i<best_ant.ants.length; i++) {
            System.out.println("Ant " + i + " : " + best_ant.ants[i].total_working_time);
        }

        System.out.println("Number of iteration : " + nb_iteration);

        return best_solution;
    }

    public void initPheromone() {
        pheromone = new float[sessad.employee.length][nb_jour][][];

        for(int i = 0; i < sessad.employee.length; i++) {
            for(int j = 0; j < nb_jour ; j++) {
                int nb_possible_mission = nb_mission_par_jour[j] + sessad.center_name.length;
                pheromone[i][j] = new float[nb_possible_mission][];
                for(int k = 0; k < nb_possible_mission; k++) {
                    pheromone[i][j][k] = new float[nb_possible_mission];
                    for(int l = 0; l < nb_possible_mission; l++) {
                        pheromone[i][j][k][l] = 1;
                    }
                }
            }
        }
    }

    public void printSolution(ArrayList<Integer>[][] solution) {
        // for(int i = 0; i < solution.length; i++) {
        //     for(int j = 0; j < solution[i].length; j++) {

        //         for(int k = 0; k < solution[i][j].size(); k++) {
        //             if(solution[i][j].get(k) != sessad.employee[i].center_id) {
        //                 int current_mission = sessad.ConvertADayAndMissionNumberToMissionId(j, solution[i][j].get(k) - sessad.center_name.length);
        //                 System.out.println("Employee : " + i + " Day : " + j + " Mission : " + current_mission);                        
        //                 System.out.println(sessad.employee[i].specialite + " et " + sessad.mission[current_mission].specialite);

        //             }
        //         }
        //     }
        // }

        //Print the number of mission

        System.out.println("Number of mission : " + nb_mission(solution));

        //Print the distance

        System.out.println("Distance : " + distance(solution));

        //Print the number of mission where the speciality are the same


        System.out.println("Number of mission where the speciality are the same : " + nb_mission_same_speciality(solution));

    }

    public int nb_mission(ArrayList<Integer>[][] solution) {
        int nb_mission = 0;
        for(int i = 0; i < solution.length; i++) {
            for(int j = 0; j < solution[i].length; j++) {
                for(int k = 0; k < solution[i][j].size(); k++) {
                    if(solution[i][j].get(k) != sessad.employee[i].center_id) {
                        nb_mission++;
                    }
                }
            }
        }
        return nb_mission;
    }

    public int nb_mission_same_speciality(ArrayList<Integer>[][] solution) {
        int nb_mission_same_speciality = 0;

        for(int i = 0; i < solution.length; i++) {
            for(int j = 0; j < solution[i].length; j++) {
                for(int k = 0; k < solution[i][j].size(); k++) {
                    if(solution[i][j].get(k) != sessad.employee[i].center_id){
                        int mission_id = sessad.ConvertADayAndMissionNumberToMissionId(j, solution[i][j].get(k) - sessad.center_name.length);
                        if(sessad.employee[i].specialite.equals(sessad.mission[mission_id].specialite)) {
                            nb_mission_same_speciality++;
                        }
                    }
                }
            }
        }

        return nb_mission_same_speciality;
    }

    public float distance(ArrayList<Integer>[][] solution) {
        float distance = 0;
        for(int i = 0; i < solution.length; i++) {
            for(int j = 0; j < solution[i].length; j++) {
                for(int k = 0; k < solution[i][j].size()-1; k++) {
                    distance += sessad.distance[j][solution[i][j].get(k)][solution[i][j].get(k+1)];
                }
            }
        }
        return distance;
    }

    public void updatePheromone() {
        for (int i = 0; i < sessad.employee.length; i++) {
            for (int j = 0; j < nb_jour; j++) {
                int nb_possible_mission = nb_mission_par_jour[j] + sessad.center_name.length;
                for (int k = 0; k < nb_possible_mission; k++) {
                    for (int l = 0; l < nb_possible_mission; l++) {
                        pheromone[i][j][k][l] = (1 - rho) * pheromone[i][j][k][l];
                    }
                }
            }
        }
        
        float multiplier_nb_mission_same_speciality = 1;
        float multiplier_distance = sessad.mission.length;
        float multiplier_nb_mission = sessad.mission.length + multiplier_distance;

        for(AntGroup ant_group : ants) {
            float distance = distance(ant_group.solution);
            int nb_mission = nb_mission(ant_group.solution);
            int nb_mission_same_speciality = nb_mission_same_speciality(ant_group.solution);

            for(int i = 0; i < ant_group.solution.length; i++) {
                for(int j = 0; j < ant_group.solution[i].length; j++) {
                    for(int k = 0; k < ant_group.solution[i][j].size() - 1; k++) {
                        int current_mission = ant_group.solution[i][j].get(k);
                        int next_mission = ant_group.solution[i][j].get(k+1);

                        pheromone[i][j][current_mission][next_mission] += ((1 / distance)+1) * multiplier_distance + (nb_mission) * multiplier_nb_mission + (nb_mission_same_speciality) * multiplier_nb_mission_same_speciality;
                                                
                    }
                }
            }

        }

        //Normalize the pheromone
        float max = 0;
        float min = Float.MAX_VALUE;

        for(int i = 0; i < sessad.employee.length; i++) {
            for(int j = 0; j < nb_jour; j++) {
                int nb_possible_mission = nb_mission_par_jour[j] + sessad.center_name.length;
                for(int k = 0; k < nb_possible_mission; k++) {
                    for(int l = 0; l < nb_possible_mission; l++) {
                        if(pheromone[i][j][k][l] > max) {
                            max = pheromone[i][j][k][l];
                        }
                        if(pheromone[i][j][k][l] < min) {
                            min = pheromone[i][j][k][l];
                        }
                    }
                }
            }
        }

        min *= 0.9;

        for(int i = 0; i < sessad.employee.length; i++) {
            for(int j = 0; j < nb_jour; j++) {
                int nb_possible_mission = nb_mission_par_jour[j] + sessad.center_name.length;
                for(int k = 0; k < nb_possible_mission; k++) {
                    for(int l = 0; l < nb_possible_mission; l++) {
                        pheromone[i][j][k][l] = (pheromone[i][j][k][l] - min) / (max - min);
                    }
                }
            }
        }
    }
}



