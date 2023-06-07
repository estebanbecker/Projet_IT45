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
        nb_jour++;

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
        while(System.currentTimeMillis() - current_mission_time < maximum_time && nb_iteration_without_improvement < teta) {
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
            for(int i = 0; i < nb_ants; i++) {
                if(sessad.is_it_better(ants[i].solution, best_solution)) {
                    best_solution = ants[i].solution;
                    nb_iteration_without_improvement = 0;
                }
            }
        }
        printSolution(best_solution);
        return best_solution;
    }

    public void initPheromone() {
        pheromone = new float[nb_jour][sessad.employee.length][][];

        for(int i = 0; i < nb_jour; i++) {
            for(int j = 0; j < sessad.employee.length; j++) {
                int nb_possible_mission = nb_mission_par_jour[i] + sessad.center_name.length;
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
        for(int i = 0; i < solution.length; i++) {
            int offset = 0;
            for(int j = 0; j < solution[i].length; j++) {

                for(int k = 0; k < solution[i][j].size(); k++) {
                    int current_mission = solution[i][j].get(k)-2 + offset;
                    System.out.println("Employee : " + i + " Day : " + j + " Mission : " + current_mission);
                }
                offset += nb_mission_par_jour[j];
            }
        }

        //Print the number of mission
        int nb_mission = 0;
        for(int i = 0; i < solution.length; i++) {
            for(int j = 0; j < solution[i].length; j++) {
                nb_mission += solution[i][j].size();
            }
        }
        System.out.println("Number of mission : " + nb_mission);

        //Print the distance
        float distance = 0;
        for(int i = 0; i < solution.length; i++) {
            for(int j = 0; j < solution[i].length; j++) {
                for(int k = 0; k < solution[i][j].size()-1; k++) {
                    distance += sessad.distance[i][solution[i][j].get(k)][solution[i][j].get(k)+1];
                }
            }
        }

        System.out.println("Distance : " + distance);

        //Print the number of mission where the speciality are the same

        int nb_mission_same_speciality = 0;

        for(int i = 0; i < solution.length; i++) {
            for(int j = 0; j < solution[i].length; j++) {
                for(int k = 0; k < solution[i][j].size(); k++) {
                    if(sessad.employee[i].specialite == sessad.mission[solution[i][j].get(k)].specialite) {
                        nb_mission_same_speciality++;
                    }
                }
            }
        }

        System.out.println("Number of mission where the speciality are the same : " + nb_mission_same_speciality);

    }

    public void ubpdatePheromone() {
        for(int i = 0; i < nb_jour; i++) {
            for(int j = 0; j < sessad.employee.length; j++) {
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
            for(Ant ant : ant_group.ants) {
                for(int i = 0; i < ant.mission_done.length; i++) {
                    for(int j = 0; j < ant.mission_done[i].size(); j++) {
                        if(ant.mission_done[i].get(j) != -1) {
                            int current_mission = ant.mission_done[i].get(j);
                            int next_mission = -1;
                            if(j != ant.mission_done[i].size()-1) {
                                next_mission = ant.mission_done[i].get(j+1);
                            }
                            if(next_mission != -1) {
                                distance[i][j][current_mission][next_mission] += sessad.distance[i][current_mission][next_mission];
                                nb_mission[i][j][current_mission][next_mission] += 1;
                                if(sessad.employee[i].specialite == sessad.mission[current_mission].specialite) {
                                    nb_mission_same_speciality[i][j][current_mission][next_mission] += 1;
                                }
                            }
                        }
                    }
                }
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



