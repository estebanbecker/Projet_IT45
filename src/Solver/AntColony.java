package Solver;

import Problem.SESSAD;

public class AntColony {
    
    private SESSAD sessad;
    private int nb_ants;

    private AntGroup[] ants;

    private float pheromone[][];

    public AntColony(SESSAD sessad, int nb_ants) {
        this.sessad = sessad;

        this.nb_ants = nb_ants;

        
    }
}



