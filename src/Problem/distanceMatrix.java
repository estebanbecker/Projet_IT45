package Problem;

public class distanceMatrix{
    private Float[][] distances;

    public distanceMatrix(int citiesCount) {
        this.distances = new Float[citiesCount][citiesCount];
    }

    public double getDistances(int missionId, int centerId) {
        //round the float to 3 deicimal plces
        return Math.round(this.distances[missionId][centerId] * 10000.0) / 10000.0;
        //return this.distances[missionId][centerId];
    }

    public void setDistances(int missionId, int centerId, Float distance) {
        this.distances[missionId][centerId] = distance;
    }
}
