package Solver;

import java.util.ArrayList;
import java.util.Random;

import Problem.SESSAD;

public class Ant {

    final int MAX_WORKING_TIME_PER_DAY = 7 * 60;
    final int MAX_WORKING_TIME_PER_WEEK = 35 * 60;
    final int MAX_RANGE_WORKING_TIME_PER_DAY = 13 * 60;
    final int DAY_DURATION = 24 * 60;
    final float SPEED = 50.0f/60.0f;

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

    public float total_working_time;

    public boolean[][] done;


    public float max_distance;

    private boolean first_mission;

    /**
     * Initialize an ant
     * @param sessad    The problem
     * @param id    The id of the ant
     * @param pheromone The pheromone matrix
     * @param competence    The competence of the ant
     * @param specialite    The speciality of the ant
     * @param alpha The alpha parameter
     * @param beta  The beta parameter
     * @param nb_jour   The number of days
     */
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

    /**
     * Run the ant
     * @param done  The matrix of the missions done by the other ants
     */
    public void run(boolean[][] done) { 

        this.done = done;


        // Initialize the mission_done matrix
        mission_done = new ArrayList[nb_jour];
        for(int i = 0; i < nb_jour; i++) {
            mission_done[i] = new ArrayList<Integer>();
        }

        // Initialize the total_working_time
        total_working_time = 0;

        //Run the ant for each day
        for(day = 0; day < nb_jour; day++) {
            float today_working_time = 0;
            float starting_time = -1;
            int current_mission = center_id;
            first_mission = true;

            // Set the max distance
            set_max_distance();

            mission_done[day].add(center_id);
            do{
                // Choose the next mission
                current_mission = chooseMission(current_mission, today_working_time, total_working_time, starting_time, pheromone[day]);
                if(current_mission == -1) {
                    break;
                }else if(first_mission){
                    if(current_mission != center_id) {
                        starting_time = (float) (startofmission(current_mission)-sessad.distance[day][center_id][current_mission]/(SPEED));

                        first_mission = false;
                    }
                }

                // Update the working time
                if(current_mission == center_id) {
                    today_working_time += sessad.distance[day][mission_done[day].get(mission_done[day].size() - 1)][current_mission]/(SPEED);
                    total_working_time += sessad.distance[day][mission_done[day].get(mission_done[day].size() - 1)][current_mission]/(SPEED);
                }else{
                    int total_current_mission = sessad.ConvertADayAndMissionNumberToMissionId(day, current_mission - sessad.center_name.length);
                    today_working_time += sessad.distance[day][mission_done[day].get(mission_done[day].size() - 1)][current_mission]/(SPEED)+sessad.mission[total_current_mission].end_time-sessad.mission[total_current_mission].start_time;
                    total_working_time += sessad.distance[day][mission_done[day].get(mission_done[day].size() - 1)][current_mission]/(SPEED)+sessad.mission[total_current_mission].end_time-sessad.mission[total_current_mission].start_time;
                }
                
                // Add the mission to the mission_done matrix
                mission_done[day].add(current_mission);
                // Add the mission to the done matrix
                if(current_mission >= sessad.center_name.length){
                    done[day][current_mission-sessad.center_name.length] = true;    
                }            
                
            //While the employee hasn't returned to the center
            }while(current_mission != center_id);
            

        }



    }

    /**
     * Set the max distance
     */
    private void set_max_distance() {
        float max = 0;
        for(int i = 0; i < sessad.distance[day].length; i++) {
            for(int j = 0; j < sessad.distance[day][i].length; j++) {
                if(sessad.distance[day][i][j] > max) {
                    max = sessad.distance[day][i][j];
                }
            }
        }
        max_distance = max;
    }

    /**
     * Choose the next mission to do
     * @param current_mission   The mission that the employee is currently doing
     * @param today_working_time    The time that the employee has already worked today
     * @param total_working_time    The time that the employee has already worked this week
     * @param start_time    The time that the employee started working today
     * @param today_pheromone   The pheromone matrix of the current day
     * @return  The id of the mission that the employee will do next
     */
    private int chooseMission(int current_mission, float today_working_time, float total_working_time, float starting_time, float today_pheromone[][]) {

        float sum = 0;
        float[] proba = new float[sessad.distance[day].length];
        
        // Calculate the probability of each mission
        for (int i = 0; i < sessad.distance[day].length; i++) {
            if(isMissionPossible(current_mission,i , today_working_time, total_working_time , starting_time)) {
                if(center_id == i) {
                    proba[i] = 1f/sessad.distance[day].length * (float) Math.pow(today_pheromone[current_mission][i], alpha) ;
                }else {
                    proba[i] = (float) Math.pow(today_pheromone[current_mission][i], alpha) * (float) Math.pow(heuristic(current_mission,i), beta);
                    if(proba[i] == Double.POSITIVE_INFINITY) {
                        proba[i] = 100;
                    }
                }
                sum += proba[i];
            }
        }

        //initialize random
        Random random = new Random();

        float rand = (float) (random.nextFloat()) * sum;

        // Choose the mission according to the probability and the random
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
        return center_id;
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

        if(mission_id == center_id){
            return true;
        }else if(mission_id == current_mission) {
            return false;
        }else if(mission_id < nb_centres) {
            return false;
        }

        
        int mission_id_day = sessad.ConvertADayAndMissionNumberToMissionId(day, mission_id-sessad.center_name.length);

        //Check that the employee hasn't already done the mission
        if(done[day][mission_id-sessad.center_name.length]) {
            return false;
        }
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
        else if(endingTime(mission_id) - starting_time > MAX_RANGE_WORKING_TIME_PER_DAY) {
            if(!first_mission){
                return false;
            }
        }
        if(current_mission == center_id) {
            return true;
        }//check that the employee will not be late for the mission
        if(endofmission(current_mission) + sessad.distance[day][current_mission][mission_id]/SPEED> startofmission(mission_id)){
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
        return (float) (sessad.mission[mission_id_day].end_time - sessad.mission[mission_id_day].start_time + sessad.distance[day][current_mission][mission_id]/(SPEED)+sessad.distance[day][mission_id][center_id]/(SPEED));
    }

    /**
     * Calculate the ending time of a mission and return to the center
     * @param current_mission   The mission that the employee is currently doing
     * @param mission_id    The mission that the employee is going to do
     * @return  The ending time of the mission and return to the center
     */
    private float endingTime(int mission_id) {
        int mission_id_day = sessad.ConvertADayAndMissionNumberToMissionId(day, mission_id-sessad.center_name.length);
        return (float) (sessad.mission[mission_id_day].end_time + sessad.distance[day][mission_id][center_id]/(SPEED));
    }

    /**
     * Calculate the ending time of a mission
     * @param mission_id    The mission that we want to know the ending time    
     * @return  The ending time of the mission
     */
    private float endofmission(int mission_id) {
        int mission_id_day = sessad.ConvertADayAndMissionNumberToMissionId(day, mission_id-sessad.center_name.length);
        return (float) (sessad.mission[mission_id_day].end_time);
    }

    /**
     * Calculate the starting time of a mission
     * @param mission_id    The mission that we want to know the starting time
     * @return  The starting time of the mission
     */
    private float startofmission(int mission_id) {
        int mission_id_day = sessad.ConvertADayAndMissionNumberToMissionId(day, mission_id-sessad.center_name.length);
        return (float) (sessad.mission[mission_id_day].start_time);
    }

    /**
     * Calculate the heuristic of a mission
     * @param current_mission   The mission that the employee is currently doing
     * @param mission_id    The mission that we want to know the heuristic
     * @return  The heuristic of the mission
     */
    private float heuristic(int current_mission, int mission_id) {
        float distance = sessad.distance[day][current_mission][mission_id];
        float ending_time = endofmission(mission_id);
        int specialite = 0;
        if(sessad.mission[mission_id].competence.equals(this.competence)) {
            specialite = 1;
        }

        ending_time = ending_time/DAY_DURATION;
        distance = 1-distance/max_distance;

        return (float) (distance*100 + ending_time*10000 + specialite )/10101;

    }
}

