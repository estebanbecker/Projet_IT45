package Solver;

import java.util.ArrayList;

import Problem.SESSAD;

import java.io.File;
import java.io.FileWriter;

public class AntColony {
    
    private SESSAD sessad;
    private int nb_ants;
    private float alpha;
    private float beta;
    private float rho;


    private AntGroup[] ants;

    private float pheromone[][][];

    private ArrayList<Integer>[][] best_solution;

    private int nb_jour;

    private int[] nb_mission_par_jour;

    /**
     * Initilise the solver
     * @param sessad    The problem to solve
     * @param nb_ants   The number of ants groups
     * @param alpha     The alpha parameter that control the importance of the heuristic
     * @param beta      The beta parameter that control the importance of the pheromone
     * @param rho       The rho parameter that control the evaporation of the pheromone
     */
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

    /**
     * Solve the problem and return the best solution
     * @param teta  The number of iteration without improvement
     * @param maximum_time The maximum time to solve the problem
     * @return  The best solution
     */
    public ArrayList<Integer>[][] solve(int teta, float maximum_time){
        initPheromone();

        //Initialise the ArryList to Integer


        //Initialise the counter for the limit condition
        long current_mission_time = System.currentTimeMillis();
        int nb_iteration_without_improvement = 0;

        //Initialise the ants
        Thread[] threads = new Thread[nb_ants];
        ants = new AntGroup[nb_ants];

        //Initialise the best solution
        //dimension 0: employee
        //dimension 1: day
        //dimension 2: mission
        best_solution = new ArrayList[sessad.employee.length][nb_jour];
        for(int i = 0; i < sessad.employee.length; i++) {
            for(int j = 0; j < nb_jour; j++) {
                best_solution[i][j] = new ArrayList<Integer>();
            }
        }

        //Initialise the best ant
        AntGroup best_ant = new AntGroup(sessad, nb_iteration_without_improvement, pheromone, current_mission_time, teta, nb_iteration_without_improvement);

        //Initialise the number of iteration
        int nb_iteration = 0;

        //While the limit conditions are not reached
        while((System.currentTimeMillis() - current_mission_time) /1000f < maximum_time && nb_iteration_without_improvement < teta) {
            nb_iteration++;

            //Make all the ants do an iteration
            for(int i = 0; i < nb_ants; i++) {
                ants[i] = new AntGroup(sessad, i, pheromone, alpha, beta,nb_jour);
                
                threads[i] = new Thread(ants[i]);
                threads[i].start();
            }   

            //Wait for all the ants to finish
            for(int i = 0; i < nb_ants; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            boolean improvement = false;

            //Control if the solution of the ants are better than the best solution
            for(int i = 0; i < nb_ants; i++) {
                if(ants[i].solution!= null){
                    if(sessad.is_it_better(ants[i].solution, best_solution)) {
                        best_solution = ants[i].solution;
                        best_ant = ants[i];
                        improvement = true;
                    }
                }
            }

            //If there is an improvement, reset the counter
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

        // System.out.println("Working time");

        // for(int i = 0; i<best_ant.ants.length; i++) {
        //     System.out.println("Ant " + i + " : " + best_ant.ants[i].total_working_time);
        // }

        System.out.println("Number of iteration : " + nb_iteration);

        return best_solution;
    }

    /**
     * Initialise the pheromone 
     */
    public void initPheromone() {
        pheromone = new float[nb_jour][][];

        for(int j = 0; j < nb_jour ; j++) {
            int nb_possible_mission = nb_mission_par_jour[j] + sessad.center_name.length;
            pheromone[j] = new float[nb_possible_mission][];
            for(int k = 0; k < nb_possible_mission; k++) {
                pheromone[j][k] = new float[nb_possible_mission];
                for(int l = 0; l < nb_possible_mission; l++) {
                    pheromone[j][k][l] = 1;
                }
            }
        }
    }

    /**
     * Print the solution
     * @param solution  The solution to print
     */
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

    /**
     * Count the number of mission done in the solution
     * @param solution  The solution
     * @return        The number of mission
     */
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

    /**
     * Count the number of mission where the speciality are the same
     * @param solution  The solution
     * @return      The number of mission where the speciality are the same
     */
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

    /**
     * Calculate the distance of the solution
     * @param solution  The solution
     * @return    The distance of the solution
     */
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

    /**
     * Update the pheromone according to the solutions found
     */
    public void updatePheromone() {
        //Evaporation
        for (int j = 0; j < nb_jour; j++) {
            int nb_possible_mission = nb_mission_par_jour[j] + sessad.center_name.length;
            for (int k = 0; k < nb_possible_mission; k++) {
                for (int l = 0; l < nb_possible_mission; l++) {
                    pheromone[j][k][l] = (1 - rho) * pheromone[j][k][l];
                }
            }
        }
        
        float nb_total_mission = sessad.mission.length;

        float max_distance = 0;
        float min_distance = Float.MAX_VALUE;

        //Find the max and min distance
        for(AntGroup ant_group : ants) {
            float distance = distance(ant_group.solution);
            if(distance > max_distance) {
                max_distance = distance;
            }
            if(distance < min_distance) {
                min_distance = distance;
            }
        }

        //Update the pheromone between 0 and 1
        for(AntGroup ant_group : ants) {
            float distance = distance(ant_group.solution);



            int nb_mission = nb_mission(ant_group.solution);
            int nb_mission_same_speciality = nb_mission_same_speciality(ant_group.solution);

            for(int i = 0; i < ant_group.solution.length; i++) {
                for(int j = 0; j < ant_group.solution[i].length; j++) {
                    for(int k = 0; k < ant_group.solution[i][j].size() - 1; k++) {
                        int current_mission = ant_group.solution[i][j].get(k);
                        int next_mission = ant_group.solution[i][j].get(k+1);

                        pheromone[j][current_mission][next_mission] += (1 - (distance - min_distance)/(max_distance - min_distance)) * 100 + nb_mission_same_speciality/nb_total_mission  + nb_mission/nb_total_mission*10000;
                                                
                    }
                }
            }
        }

        //Normalize the pheromone
        float max = 0;
        float min = Float.MAX_VALUE;

        for(int j = 0; j < nb_jour; j++) {
            int nb_possible_mission = nb_mission_par_jour[j] + sessad.center_name.length;
            for(int k = 0; k < nb_possible_mission; k++) {
                for(int l = 0; l < nb_possible_mission; l++) {
                    if(pheromone[j][k][l] > max) {
                        max = pheromone[j][k][l];
                    }
                    if(pheromone[j][k][l] < min) {
                        min = pheromone[j][k][l];
                    }
                }
            }
        }

        min *= 0.9;

        for(int j = 0; j < nb_jour; j++) {
            int nb_possible_mission = nb_mission_par_jour[j] + sessad.center_name.length;
            for(int k = 0; k < nb_possible_mission; k++) {
                for(int l = 0; l < nb_possible_mission; l++) {
                    pheromone[j][k][l] = (pheromone[j][k][l] - min) / (max - min);
                }
            }
        }
    }
}



