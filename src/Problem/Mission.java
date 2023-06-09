package Problem;

import java.util.ArrayList;
import java.util.List;

public class Mission {
    public int id;

    public int day;
    public int start_time;
    public int end_time;
    public int centerId;
    public String competence;

    public String specialite;

    public static List<Integer> getMissionIdsForDay(int day, List<Mission> Missions) {
        List<Integer> missionIds = new ArrayList<>();

        for (Mission mission : Missions) {
            if (mission.getDay() == day) {
                missionIds.add(mission.getId());
            }
        }

        return missionIds;
    }

    public static Float[][][] createMissionIndexArrayByDay(List<Mission> Missions) {
        Float[][][] missionIndexArrayByDay = new Float[5][][]; // Assuming there are 5 days in a week

        for (int day = 1; day <= 5; day++) {
            List<Integer> missionIds = Mission.getMissionIdsForDay(day, Missions);
            Float[][] missionIndexArray = new Float[missionIds.size()][missionIds.size()];

            for (int i = 0; i < missionIds.size(); i++) {
                for (int j = 0; j < missionIds.size(); j++) {
                    missionIndexArray[i][j] = Float.valueOf(missionIds.get(i));
                }
            }

            missionIndexArrayByDay[day - 1] = missionIndexArray;
        }

        return missionIndexArrayByDay;
    }


    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public Mission() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", day=" + day +
                ", startingPeriod=" + start_time +
                ", endingPeriod=" + end_time +
                ", skill='" + competence + '\'' +
                ", specialty='" + specialite + '\'' +
                '}';
    }
}
