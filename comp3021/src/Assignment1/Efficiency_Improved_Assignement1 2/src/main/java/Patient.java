enum SymptomLevel {
    Critical, Moderate, Mild
}

public class Patient extends Person{
    private SymptomLevel symptomLevel;
    private String HospitalID;

    /*
    * Class Constructors
    * Remember to initialize the attributes in the Person
    */
    public Patient(Person p, SymptomLevel p_symptomLevel) {
        super(p.IDCardNo, p.getLoc(), p.getGender(), p.getAge(), p.getIsVac());
        symptomLevel = p_symptomLevel;
        //TODO
    }

    public void setSymptomLevel(SymptomLevel p_symptomLevel) {
        this.symptomLevel = p_symptomLevel;
    }

    public void setHospitalID(String p_HospitalID) {
        this.HospitalID = p_HospitalID;
    }

    public SymptomLevel getSymptomLevel() {
        return this.symptomLevel;
    }

    public String getHospitalID() {
        return this.HospitalID;
    }
}
