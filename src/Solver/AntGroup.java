package Solver;
import java.util.ArrayList;

import Problem.SESSAD;

public class AntGroup implements Runnable{
    
    public Ant[] ants;
    public float pheromone[][][][];

    public ArrayList<Integer>[][] solution;
    
    public AntGroup(SESSAD sessad, int nb_ants, float pheromone[][][][], float alpha, float beta, int nb_jour) {
        nb_ants = sessad.employee.length;

        for (int i = 0; i < nb_ants; i++) {
            ants[i] = new Ant(sessad, i, pheromone[i], sessad.employee[i].competence, sessad.employee[i].specialite, 1, 1,nb_jour);
        }

    }

    public void setPheromone(float pheromone[][][][]) {
        this.pheromone = pheromone;
    }

    public void run() {
        for (Ant ant : ants) {
            ant.run();
        }

        for (Ant ant : ants) {
            solution[ant.id] = ant.mission_done;
        }

    }

}
