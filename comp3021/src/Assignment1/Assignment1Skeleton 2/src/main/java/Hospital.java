import java.util.List;
import java.util.ArrayList;

public class Hospital {
    public String HospitalID;
    private Location loc;
    private Capacity cap;
    private List<Patient> CriticalPatients;
    private List<Patient> ModeratePatients;
    private List<Patient> MildPatients;

    /* Create the hospital according to the input file */
    public Hospital(String p_HospitalID, Location p_loc, Capacity p_cap) {
        this.HospitalID = p_HospitalID;
        this.loc = p_loc;
        this.cap = p_cap;
        this.CriticalPatients = new ArrayList<>();
        this.ModeratePatients = new ArrayList<>();
        this.MildPatients = new ArrayList<>();
    }

    /* Create new hospital */
    public Hospital(String p_HospitalID, Location p_loc) {
        this.HospitalID = p_HospitalID;
        this.loc = p_loc;
        this.cap = new Capacity(5, 10, 20);
        this.CriticalPatients = new ArrayList<>();
        this.ModeratePatients = new ArrayList<>();
        this.MildPatients = new ArrayList<>();
    }

    /* Get the location of the hospital */
    public Location getLoc() {
        return this.loc;
    }

    /* Get the capacity of the hospital */
    public Capacity getCapacity() {
        return this.cap;
    }

    /* Get the patient list according to the symptom level */
    public List<Patient> getPatients(SymptomLevel symptomLevel) {
        switch (symptomLevel) {
            case Critical:
                return this.CriticalPatients;
            case Moderate:
                return this.ModeratePatients;
            case Mild:
                return this.MildPatients;
        }
        //unreachable code
        return null;
    }

    /* Add a patient to the corresponding patient list */
    public boolean addPatient(Patient patient) {
        switch (patient.getSymptomLevel()) {
            //TODO: handle three kinds of the symptom levels
            case Critical:
                this.CriticalPatients.add(patient);
                this.cap.decreaseCapacity(SymptomLevel.Critical);
                return true;
            case Moderate:
                this.ModeratePatients.add(patient);
                this.cap.decreaseCapacity(SymptomLevel.Moderate);
                return true;
            case Mild:
                this.MildPatients.add(patient);
                this.cap.decreaseCapacity(SymptomLevel.Mild);
                return true;
            default:
                break;
        }
        return false;
    }

    /* Remove a patient from the corresponding patient list */
    public boolean releasePatient(Patient patient) {
        switch (patient.getSymptomLevel()) {
            //TODO: handle three kinds of the symptom levels
            case Critical:
                this.CriticalPatients.remove(patient);
                this.cap.increaseCapacity(SymptomLevel.Critical);
                return true;
            case Moderate:
                this.ModeratePatients.remove(patient);
                this.cap.increaseCapacity(SymptomLevel.Moderate);
                return true;
            case Mild:
                this.MildPatients.remove(patient);
                this.cap.increaseCapacity(SymptomLevel.Mild);
                return true;
            default:
                break;
        }
        return false;
    }

    /* Dump the hospital info as a string */
    public String toString() {
        String str = "";
        str += HospitalID + "        ";
        str += loc.xloc + "        ";
        str += loc.yloc + "        ";
        str += cap.CriticalCapacity + "        ";
        str += cap.ModerateCapacity + "        ";
        str += cap.MildCapacity;
        return str;
    }
}
