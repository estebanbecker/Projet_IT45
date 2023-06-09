package Problem;

public class Employee {
    public int id;
    public int center_id;
    public String competence;
    public String specialite;

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", centerId=" + center_id +
                ", skill='" + competence + '\'' +
                ", specialty='" + specialite + '\'' +
                '}';
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCenter_id() {
        return center_id;
    }

    public void setCenter_id(int center_id) {
        this.center_id = center_id;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
}
