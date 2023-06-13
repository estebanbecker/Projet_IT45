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
        System.out.println("Number of mission : " + nb_mission);

        //Print the distance
        float distance = 0;
        for(int i = 0; i < solution.length; i++) {
            for(int j = 0; j < solution[i].length; j++) {
                for(int k = 0; k < solution[i][j].size()-1; k++) {
                    distance += sessad.distance[j][solution[i][j].get(k)][solution[i][j].get(k+1)];
                }
            }
        }

        System.out.println("Distance : " + distance);

        //Print the number of mission where the speciality are the same

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

        System.out.println("Number of mission where the speciality are the same : " + nb_mission_same_speciality);

    }

    public void updatePheromone() {
        for(int i = 0; i < sessad.employee.length; i++) {
            for(int j = 0; j < nb_jour; j++) {
                for(int k = 0; k < pheromone[i][j].length; k++) {
                    for(int l = 0; l < pheromone[i][j][k].length; l++) {
                        pheromone[i][j][k][l] = (1 - rho) * pheromone[i][j][k][l];
                    }
                }
            }
        }

        float distance[][][][] = new float[nb_jour][sessad.employee.length][][];
        for(int i = 0; i < nb_jour; i++) {
            for(int j = 0; j < sessad.employee.length; j++) {
                int nb_possibilite = nb_mission_par_jour[i] + sessad.center_name.length;
                distance[i][j] = new float[nb_possibilite][];
                for(int k = 0; k < nb_possibilite; k++) {
                    distance[i][j][k] = new float[nb_possibilite];
                    for(int l = 0; l < nb_possibilite; l++) {
                        distance[i][j][k][l] = 0;
                    }
                }
            }
        }

        float nb_mission[][][][] = new float[nb_jour][sessad.employee.length][][];
        for(int i = 0; i < nb_jour; i++) {
            for(int j = 0; j < sessad.employee.length; j++) {
                int nb_possibilite = nb_mission_par_jour[i] + sessad.center_name.length;
                nb_mission[i][j] = new float[nb_possibilite][];
                for(int k = 0; k < nb_possibilite; k++) {
                    nb_mission[i][j][k] = new float[nb_possibilite];
                    for(int l = 0; l < nb_possibilite; l++) {
                        nb_mission[i][j][k][l] = 0;
                    }
                }
            }
        }

        float nb_mission_same_speciality[][][][] = new float[nb_jour][sessad.employee.length][][];
        for(int i = 0; i < nb_jour; i++) {
            for(int j = 0; j < sessad.employee.length; j++) {
                int nb_possibilite = nb_mission_par_jour[i] + sessad.center_name.length;
                nb_mission_same_speciality[i][j] = new float[nb_possibilite][];
                for(int k = 0; k < nb_possibilite; k++) {
                    nb_mission_same_speciality[i][j][k] = new float[nb_possibilite];
                    for(int l = 0; l < nb_possibilite; l++) {
                        nb_mission_same_speciality[i][j][k][l] = 0;
                    }
                }
            }
        }

        for(AntGroup ant_group : ants) {
            int k = 0;
            for(Ant ant : ant_group.ants) {
                k++;
            }
        }

        float multiplier_distance = sessad.mission.length;
        float multiplier_nb_mission = sessad.mission.length + multiplier_distance;
        for(int i = 0; i < nb_jour; i++) {
            for(int j = 0; j < sessad.employee.length; j++) {
                int nb_possibilite = nb_mission_par_jour[i] + sessad.center_name.length;
                for(int k = 0; k < nb_possibilite; k++) {
                    for(int l = 0; l < nb_possibilite; l++) {
                        if(nb_mission[i][j][k][l] != 0) {
                            pheromone[i][j][k][l] += (nb_mission[i][j][k][l]) *multiplier_nb_mission + (1 / distance[i][j][k][l] + 1) * multiplier_distance + (nb_mission_same_speciality[i][j][k][l]);
                        }
                    }
                }
            }
        }
    }
}



