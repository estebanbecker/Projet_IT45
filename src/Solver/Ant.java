package Solver;

import Problem.SESSAD;

public class Ant {

    public SESSAD sessad;

    public int id;

    public float pheromone[][];

    public String competence;
    public String specialite;

    private int nb_centres;

    public float alpha;
    public float beta;

    public int center_id;

    private int day;

    public int[][] mission_done;


    public Ant(SESSAD sessad, int id, float pheromone[][], String competence, String specialite, float alpha, float beta) {
        this.sessad = sessad;
        this.id = id;
        this.pheromone = pheromone;
        this.competence = competence;
        this.specialite = specialite;
        this.alpha = alpha;
        this.beta = beta;
        this.center_id = sessad.employee[id].center_id;

        nb_centres = sessad.center_name.length;
    }

    public void run(float pheromone[][]) {
        this.pheromone = pheromone;
        
        mission_done = new int[5][];

        float total_working_time = 0;

        for(int i = 0; i < 5; i++) {
            float today_working_time = 0;
            float starting_time = -1;
            int current_mission = center_id;
            boolean first_mission = true;

            mission_done[i][0] = center_id;
            do{
                
                current_mission = chooseMission(current_mission, today_working_time, total_working_time, starting_time);
                if(first_mission){
                    starting_time = (float) (sessad.mission[current_mission].start_time-sessad.distance[day][center_id][current_mission]/(50*3.6));
                }
                today_working_time += sessad.distance[day][current_mission][current_mission]/(50*3.6)+sessad.mission[current_mission].end_time-sessad.mission[current_mission].start_time;
                total_working_time += sessad.distance[day][current_mission][current_mission]/(50*3.6)+sessad.mission[current_mission].end_time-sessad.mission[current_mission].start_time;
                mission_done[i][mission_done[i].length] = current_mission;

            }while(current_mission != center_id);
            

        }



    }

    /**
     * Choose the next mission to do
     * @param current_mission   The mission that the employee is currently doing
     * @param today_working_time    The time that the employee has already worked today
     * @param total_working_time    The time that the employee has already worked this week
     * @param start_time    The time that the employee started working today
     * @return  The id of the mission that the employee will do next
     */
    private int chooseMission(int current_mission, float today_working_time, float total_working_time, float starting_time) {

        float sum = 0;
        float[] proba = new float[sessad.mission.length];
        
        for (int i = 0; i < sessad.mission.length + nb_centres; i++) {
            if(isMissionPossible(current_mission,i , today_working_time, total_working_time , starting_time)) {
                proba[i] = (float) Math.pow(pheromone[center_id][i], alpha) * (float) Math.pow(1 / sessad.distance[day][current_mission][i], beta);
                sum += proba[i];
            }
        }

        float rand = (float) Math.random() * sum;

        for (int i = 0; i < sessad.mission.length; i++) {
            if(isMissionPossible(current_mission,i , today_working_time, total_working_time, starting_time)) {
                rand -= proba[i];
                if(rand <= 0) {
                    return i;
                }
            }
        }
        throw new RuntimeException("No mission found");
    }

    /**
     * Is the mission possible to do following the constraints
     * @param current_mission   The mission that the employee is currently doing
     * @param mission_id    The mission that we want to check if it's possible to do
     * @param today_working_time    The time that the employee has already worked today
     * @param total_working_time    The time that the employee has already worked this week
     * @param starting_time     The time that the employee started working today
     * @return  True if the mission is possible to do, false otherwise
     */
    private boolean isMissionPossible(int current_mission, int mission_id, float today_working_time, float total_working_time, float starting_time) {
        if(sessad.mission[mission_id].competence.equals(competence) && TimeToDoAMission(current_mission, mission_id) + today_working_time <= (8*60) && TimeToDoAMission(current_mission, mission_id) + total_working_time <= (35*60) && (endingTime(current_mission, mission_id) <= starting_time + (13*60) || starting_time == -1) && sessad.mission[current_mission].start_time + sessad.distance[day][current_mission][mission_id]/(50*3.6) <= sessad.mission[mission_id].start_time && current_mission != mission_id && mission_id >= nb_centres) {
            return true;
        }else if(mission_id == center_id) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * Calculate the time to do a mission and return to the center
     * @param current_mission   The mission that the employee is currently doing
     * @param mission_id    The mission that the employee is going to do
     * @return  The time to do the mission and return to the center
     */
    private float TimeToDoAMission( int current_mission,int mission_id) {
        return (float) (sessad.mission[mission_id].end_time - sessad.mission[mission_id].start_time + sessad.distance[day][current_mission][mission_id]/(50*3.6)+sessad.distance[day][mission_id][center_id]/(50*3.6));
    }

    /**
     * Calculate the ending time of a mission and return to the center
     * @param current_mission   The mission that the employee is currently doing
     * @param mission_id    The mission that the employee is going to do
     * @return  The ending time of the mission and return to the center
     */
    private float endingTime(int current_mission, int mission_id) {
        return (float) (sessad.mission[mission_id].end_time + sessad.distance[day][mission_id][center_id]/(50*3.6));
    }
}
