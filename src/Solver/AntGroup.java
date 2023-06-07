package Solver;
import Problem.SESSAD;

public class AntGroup implements Runnable{
    
    public Ant[] ants;
    public float pheromone[][];
    
    public AntGroup(SESSAD sessad, int nb_ants) {
        nb_ants = sessad.employee.length;

        for (int i = 0; i < nb_ants; i++) {
            ants[i] = new Ant(sessad, i, pheromone, sessad.employee[i].competence, sessad.employee[i].specialite);
        }

    }

    public void setPheromone(float pheromone[][]) {
        this.pheromone = pheromone;
    }

    public void run() {
        for (Ant ant : ants) {
            ant.run(pheromone);
        }
    }

}
