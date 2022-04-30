enum Status {
    Confirmed, Recovered
}

public class Record {
    private String IDCardNo;
    private SymptomLevel symptomLevel;
    private Status status;
    private String HospitalID;

    public Record(String p_IDCardNo, String p_symptomLevelStr, String p_statusStr) {
        this.IDCardNo = p_IDCardNo;

        assert(p_symptomLevelStr.equals("Critical") || p_symptomLevelStr.equals("Moderate") || p_symptomLevelStr.equals("Mild"));
        switch (p_symptomLevelStr) {
            case "Critical":
                this.symptomLevel = SymptomLevel.Critical;
                break;
            case "Moderate":
                this.symptomLevel = SymptomLevel.Moderate;
                break;
            case "Mild":
                this.symptomLevel = SymptomLevel.Mild;
                break;
            default:
                break;
        }

        assert(p_statusStr.equals("Confirmed") || p_statusStr.equals("Recovered"));
        switch(p_statusStr) {
            case "Confirmed":
                this.status = Status.Confirmed;
                break;
            case "Recovered":
                this.status = Status.Recovered;
                break;
            default:
                break;
        }

        this.HospitalID = null;
    }

    public String getIDCardNo() {
        return this.IDCardNo;
    }

    public SymptomLevel getSymptomLevel() {
        return this.symptomLevel;
    }

    public Status getStatus() {
        return this.status;
    }

    public String getHospitalID() {
        return this.HospitalID;
    }

    public void setHospitalID(String pHospitalID) {
        this.HospitalID = pHospitalID;
    }

    /*
     * Dump the record info as a string
     * DO NOT change the functionality of the method
     * Otherwise, you may generate wrong results in Task 1
     */
    public String toString() {
        String str = this.IDCardNo;

        switch (this.symptomLevel) {
            case Critical:
                str += "        " + "Critical";
                break;
            case Moderate:
                str += "        " + "Moderate";
                break;
            case Mild:
                str += "        " + "Mild";
                break;
            default:
                break;
        }

        switch (this.status) {
            case Confirmed:
                str += "        " + "Confirmed";
                break;
            case Recovered:
                str += "        " + "Recovered";
                break;
            default:
                break;
        }

        if (HospitalID != null) {
            str += "        " + HospitalID;
        }

        return str;
    }
}
