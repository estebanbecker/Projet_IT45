package Solver;
import java.util.ArrayList;

import Problem.SESSAD;

public class AntGroup implements Runnable{
    
    public Ant[] ants;
    public float pheromone[][][];

    public ArrayList<Integer>[][] solution;

    private int nb_jour;

    SESSAD sessad;
    
    /**
     * Creant an Ant group, wich is a groupe of ants where each ant is an employee with his own mission
     * @param sessad    The problem
     * @param nb_ants   The number of ants
     * @param pheromone The pheromone matrix
     * @param alpha    The alpha parameter
     * @param beta   The beta parameter
     * @param nb_jour   The number of days
     */
    public AntGroup(SESSAD sessad, int nb_ants, float pheromone[][][], float alpha, float beta, int nb_jour) {
        
        this.nb_jour = nb_jour;
        nb_ants = sessad.employee.length;
        this.sessad = sessad;

        ants = new Ant[nb_ants];

        for (int i = 0; i < nb_ants; i++) {
            ants[i] = new Ant(sessad, i, pheromone, sessad.employee[i].competence, sessad.employee[i].specialite, alpha, beta,nb_jour);
        }

    }

    /**
     * Set the pheromone matrix
     * @param pheromone The pheromone matrix
     */
    public void setPheromone(float pheromone[][][]) {
        this.pheromone = pheromone;
    }

    /**
     * Run the ants
     */
    public void run() {

        boolean[][] done = new boolean[nb_jour][];

        for(int i = 0; i < nb_jour; i++) {
            done[i] = new boolean[sessad.missionPerDay[i].length];
            for(int j = 0; j < sessad.missionPerDay[i].length; j++) {
                done[i][j] = false;
            }
        }

        for (Ant ant : ants) {
            ant.run(done);
        }

        solution = new ArrayList[ants.length][];

        for (Ant ant : ants) {
            solution[ant.id] = ant.mission_done;
        }

        //solution = sessad.make_it_valid(solution);

    }


}
