package Solver;

import java.util.ArrayList;

import Problem.SESSAD;
import Problem.distanceMatrix;






public class Ant {

    final int MAX_WORKING_TIME_PER_DAY = 7 * 60;
    final int MAX_WORKING_TIME_PER_WEEK = 35 * 60;
    final int MAX_RANGE_WORKING_TIME_PER_DAY = 13 * 60;

    public SESSAD sessad;

    public int id;

    public float pheromone[][][];

    public String competence;
    public String specialite;

    private int nb_centres;

    public float alpha;
    public float beta;

    public int center_id;

    private int day;

    public ArrayList<Integer>[] mission_done;
    private int nb_jour;


    public Ant(SESSAD sessad, int id, float pheromone[][][], String competence, String specialite, float alpha, float beta, int nb_jour) {
        this.sessad = sessad;
        this.id = id;
        this.pheromone = pheromone;
        this.competence = competence;
        this.specialite = specialite;
        this.alpha = alpha;
        this.beta = beta;
        this.center_id = sessad.employee[id].center_id;
        this.nb_jour = nb_jour;

        nb_centres = sessad.center_name.length;
    }

    public void run() {
        
        mission_done = new ArrayList[nb_jour];
        for(int i = 0; i < nb_jour; i++) {
            mission_done[i] = new ArrayList<Integer>();
        }

        float total_working_time = 0;

        for(day = 0; day < nb_jour; day++) {
            float today_working_time = 0;
            float starting_time = -1;
            int current_mission = center_id;
            boolean first_mission = true;

            mission_done[day].add(center_id);
            do{
                
                current_mission = chooseMission(current_mission, today_working_time, total_working_time, starting_time, pheromone[id]);
                if(current_mission == -1) {
                    break;
                }else if(first_mission){
                    starting_time = (float) (sessad.mission[current_mission].start_time-sessad.distance[day][center_id][current_mission]/(50*3.6));
                }
                today_working_time += sessad.distance[day][current_mission][current_mission]/(50*3.6)+sessad.mission[current_mission].end_time-sessad.mission[current_mission].start_time;
                total_working_time += sessad.distance[day][current_mission][current_mission]/(50*3.6)+sessad.mission[current_mission].end_time-sessad.mission[current_mission].start_time;
                mission_done[day].add(current_mission);
                
                

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
    private int chooseMission(int current_mission, float today_working_time, float total_working_time, float starting_time, float today_pheromone[][]) {

        float sum = 0;
        float[] proba = new float[sessad.distance[day].length];
        
        for (int i = 0; i < sessad.distance[day].length; i++) {
            if(isMissionPossible(current_mission,i , today_working_time, total_working_time , starting_time)) {
                proba[i] = (float) Math.pow(today_pheromone[center_id][i], alpha) * (float) Math.pow(1 / sessad.distance[day][current_mission][i], beta);
                if(proba[i] == Double.POSITIVE_INFINITY) {
                    proba[i] = Float.MAX_VALUE;
                }
                sum += proba[i];
            }
        }

        float rand = (float) Math.random() * sum;

        if(sum == 0) {
            return -1;
        }
        for (int i = 0; i < sessad.distance[day].length; i++) {
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

        if(mission_id == current_mission){
            return false;
        }else if(mission_id == center_id) {
            return true;
        }else if(mission_id <= nb_centres) {
            return false;
        }

        
        int mission_id_day = sessad.ConvertADayAndMissionNumberToMissionId(day, mission_id-sessad.center_name.length);

        //Check that the employee has the right competence
        if(!sessad.mission[mission_id_day].competence.equals(competence)) {
            return false;
        }//check that the employee doesn't work to much today
        else if(TimeToDoAMission(current_mission, mission_id)+today_working_time > MAX_WORKING_TIME_PER_DAY) {
            return false;
        }//check that the employee doesn't work to much this week
        else if(TimeToDoAMission(current_mission, mission_id)+total_working_time > MAX_WORKING_TIME_PER_WEEK) {
            return false;
        }//check the the employee will not end to late
        else if(starting_time + TimeToDoAMission(current_mission, mission_id) > starting_time + MAX_RANGE_WORKING_TIME_PER_DAY) {
            return false;
        }//check that the employee can be at the mission on time
        else if(current_mission == center_id) {
            return true;
        }
        int current_mission_day = sessad.ConvertADayAndMissionNumberToMissionId(day, current_mission-sessad.center_name.length);
        if(endingTime(current_mission, mission_id) > sessad.mission[current_mission_day].end_time){
            return false;
        }
        return true;
        
    }

    /**
     * Calculate the time to do a mission and return to the center
     * @param current_mission   The mission that the employee is currently doing
     * @param mission_id    The mission that the employee is going to do
     * @return  The time to do the mission and return to the center
     */
    private float TimeToDoAMission( int current_mission,int mission_id) {
        int mission_id_day = sessad.ConvertADayAndMissionNumberToMissionId(day, mission_id-sessad.center_name.length);
        return (float) (sessad.mission[mission_id_day].end_time - sessad.mission[mission_id_day].start_time + sessad.distance[day][current_mission][mission_id]/(50*3.6)+sessad.distance[day][mission_id][center_id]/(50*3.6));
    }

    /**
     * Calculate the ending time of a mission and return to the center
     * @param current_mission   The mission that the employee is currently doing
     * @param mission_id    The mission that the employee is going to do
     * @return  The ending time of the mission and return to the center
     */
    private float endingTime(int current_mission, int mission_id) {
        int mission_id_day = sessad.ConvertADayAndMissionNumberToMissionId(day, mission_id-sessad.center_name.length);
        return (float) (sessad.mission[mission_id_day].end_time + sessad.distance[day][mission_id][center_id]/(50*3.6));
    }
}

