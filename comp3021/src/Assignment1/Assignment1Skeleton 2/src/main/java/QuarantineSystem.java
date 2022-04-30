import javafx.scene.shape.PathElement;
import org.w3c.dom.html.HTMLOptGroupElement;

import java.io.*;
import java.util.*;


public class QuarantineSystem {
    public static class DashBoard {
        List<Person> People;
        List<Integer> patientNums;
        List<Integer> infectNums;
        List<Double> infectAvgNums;
        List<Integer> vacNums;
        List<Integer> vacInfectNums;

        public DashBoard(List<Person> p_People) {
            this.People = p_People;
            this.patientNums = new ArrayList<>(8);
            this.infectNums = new ArrayList<>(8);
            this.infectAvgNums = new ArrayList<>(8);
            this.vacNums = new ArrayList<>(8);
            this.vacInfectNums = new ArrayList<>(8);
        }

        public void runDashBoard() {
            for (int i = 0; i < 8; i++) {
                this.patientNums.add(0);
                this.infectNums.add(0);
                this.vacNums.add(0);
                this.vacInfectNums.add(0);
                this.infectAvgNums.add(0.0);
            }
            /*
             * TODO: Collect the statistics based on People
             *  Add the data in the lists, such as patientNums, infectNums, etc.
             */
//            int a = 0, b = 0, c = 0, d = 0, e = 0, f = 0, g = 0, h = 0;
            int arr[] = new int[8];
            Arrays.fill(arr, 0);
            for (Person person : People) {
                int age = person.getAge();
                if (0 < age && age < 10) {
                    arr[0]++;
                    personUpdate(person, 0);
                } else if (10 <= age && age < 20) {
                    arr[1]++;
                    personUpdate(person, 1);
                } else if (20 <= age && age < 30) {
                    arr[2]++;
                    personUpdate(person, 2);
                } else if (30 <= age && age < 40) {
                    arr[3]++;
                    personUpdate(person, 3);
                } else if (40 <= age && age < 50) {
                    arr[4]++;
                    personUpdate(person, 4);
                } else if (50 <= age && age < 60) {
                    arr[5]++;
                    personUpdate(person, 5);
                } else if (60 <= age && age < 70) {
                    arr[6]++;
                    personUpdate(person, 6);
                } else {
                    arr[7]++;
                    personUpdate(person, 7);
                }
            }
            for(int i=0; i<8; i++){
                System.out.println(arr[i]);
                if(arr[i] != 0){
//                    this.infectAvgNums.set(i, ((double)this.infectNums.get(i))/arr[i]);
                    this.infectAvgNums.set(i, ((double)this.infectNums.get(i))/this.patientNums.get(i));
                }
            }
        }
        public void personUpdate(Person person, int i){
            if (person.getInfectCnt()>0){
                this.patientNums.set(i, this.patientNums.get(i)+1);
                this.infectNums.set(i, this.infectNums.get(i)+person.getInfectCnt());
            }
            if(person.getIsVac() && person.getInfectCnt()>0){
                this.vacInfectNums.set(i, this.vacInfectNums.get(i)+1);
                this.vacNums.set(i, this.vacNums.get(i)+1);
            }
            else if(person.getIsVac()){
                this.vacNums.set(i, this.vacNums.get(i)+1);
            }
        }
    }

//    private Map<String,Person> People;/**/
    private List<Person> People;
    private List<Patient> Patients;

    private List<Record> Records;
    private List<Hospital> Hospitals;
    private int newHospitalNum;

    private DashBoard dashBoard;

    public QuarantineSystem() throws IOException {
        importPeople();
        importHospital();
        importRecords();
        dashBoard = null;
        Patients = new ArrayList<>();
    }

    public void startQuarantine() throws IOException {
        /*
         * Task 1: Saving Patients
         */
        System.out.println("Task 1: Saving Patients");
        /*
         * TODO: Process each record
         */
        for(Record record: Records){
            if(record.getStatus() == Status.Confirmed){
                saveSinglePatient(record);
            }
            else{
                releaseSinglePatient(record);
            }
        }
        exportRecordTreatment();
        /*
         * Task 2: Displaying Statistics
         */
        System.out.println("Task 2: Displaying Statistics");
        dashBoard = new DashBoard(this.People);
        dashBoard.runDashBoard();
        exportDashBoard();
    }

    /*
     * Save a single patient when the status of the record is Confirmed
     */
    public void saveSinglePatient(Record record) {
        //TODO
        ArrayList<Hospital> availableHospitals = new ArrayList<>();
        for(Hospital hospital: Hospitals){
            if(hospital.getCapacity().getSingleCapacity(record.getSymptomLevel())>=1){
                availableHospitals.add(hospital);
            }
        }

        for(Person person: People) {
            if (person.IDCardNo.equals(record.getIDCardNo())) {
                person.setInfectCnt(person.getInfectCnt()+1);
                Patient patient = new Patient(person, record.getSymptomLevel());
                if(availableHospitals.isEmpty()){
                    newHospitalNum += 1;
                    Hospital hospital = new Hospital("H-New-"+newHospitalNum, person.getLoc(),
                            new Capacity(5,10,20));
                    patient.setHospitalID(hospital.HospitalID);
                    hospital.addPatient(patient);
                    record.setHospitalID(hospital.HospitalID);
                    Hospitals.add(hospital);
                }
                else {
                    Integer dist = Integer.MAX_VALUE;
                    Hospital selectedHospital = availableHospitals.get(0);
                    for(Hospital hospital: availableHospitals){
                        if(hospital.getLoc().getDisSquare(person.getLoc()) < dist){
                            dist = hospital.getLoc().getDisSquare(person.getLoc());
                            selectedHospital = hospital;
                        }
                    }
                    patient.setHospitalID(selectedHospital.HospitalID);
                    record.setHospitalID(selectedHospital.HospitalID);
                    selectedHospital.addPatient(patient);
                }
                Patients.add(patient);
            }
        }
    }

    /*
     * Release a single patient when the status of the record is Recovered
     */
    public void releaseSinglePatient(Record record) {
        //TODO
        Patient selected = null;
        for(Patient patient: Patients){
            if(patient.IDCardNo.equals(record.getIDCardNo())){
                for(Hospital hospital: Hospitals){
                    if(hospital.HospitalID.equals(patient.getHospitalID())){
                        record.setHospitalID(hospital.HospitalID);
                        hospital.releasePatient(patient);
                    }
                }
                selected = patient;
            }
        }
        Patients.remove(selected);
    }

    /*
     * Import the information of the people in the area from Person.txt
     * The data is finally stored in the attribute People
     * You do not need to change the method.
     */
    public void importPeople() throws IOException {
        this.People = new ArrayList<>();
//        this.People = new HashMap<>();
        File filename = new File("data/Person.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        int lineNum = 0;

        while (line != null) {
            lineNum++;
            if (lineNum > 1) {
                String[] records = line.split("        ");
                assert (records.length == 6);
                String pIDCardNo = records[0];
                int XLoc = Integer.parseInt(records[1]);
                int YLoc = Integer.parseInt(records[2]);
                Location pLoc = new Location(XLoc, YLoc);
                assert (records[3].equals("Male") || records[3].equals("Female"));
                String pGender = records[3];
                int pAge = Integer.parseInt(records[4]);
                assert (records[5].equals("Yes") || records[5].equals("No"));
                boolean pIsVac = (records[5].equals("Yes"));
                Person p = new Person(pIDCardNo, pLoc, pGender, pAge, pIsVac);
                this.People.add(p);
            }
            line = br.readLine();
        }
    }

    /*
     * Import the information of the records
     * The data is finally stored in the attribute Records
     * You do not need to change the method.
     */
    public void importRecords() throws IOException {
        this.Records = new ArrayList<>();

        File filename = new File("data/Record.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        int lineNum = 0;

        while (line != null) {
            lineNum++;
            if (lineNum > 1) {
                String[] records = line.split("        ");
                assert(records.length == 3);
                String pIDCardNo = records[0];
                assert(records[1].equals("Critical") || records[1].equals("Moderate") || records[1].equals("Mild"));
                assert(records[2].equals("Confirmed") || records[2].equals("Recovered"));
                Record r = new Record(pIDCardNo, records[1], records[2]);
                Records.add(r);
            }
            line = br.readLine();
        }
    }

    /*
     * Import the information of the hospitals
     * The data is finally stored in the attribute Hospitals
     * You do not need to change the method.
     */
    public void importHospital() throws IOException {
        this.Hospitals = new ArrayList<>();
        this.newHospitalNum = 0;

        File filename = new File("data/Hospital.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        int lineNum = 0;

        while (line != null) {
            lineNum++;
            if (lineNum > 1) {
                String[] records = line.split("        ");
                assert(records.length == 6);
                String pHospitalID = records[0];
                int XLoc = Integer.parseInt(records[1]);
                int YLoc = Integer.parseInt(records[2]);
                Location pLoc = new Location(XLoc, YLoc);
                int pCritialCapacity = Integer.parseInt(records[3]);
                int pModerateCapacity = Integer.parseInt(records[4]);
                int pMildCapacity = Integer.parseInt(records[5]);
                Capacity cap = new Capacity(pCritialCapacity, pModerateCapacity, pMildCapacity);
                Hospital hospital = new Hospital(pHospitalID, pLoc, cap);
                this.Hospitals.add(hospital);
            }
            line = br.readLine();
        }
    }

    /*
     * Export the information of the records
     * The data is finally dumped into RecordTreatment.txt
     * DO NOT change the functionality of the method
     * Otherwise, you may generate wrong results in Task 1
     */
    public void exportRecordTreatment() throws IOException {
        File filename = new File("output/RecordTreatment.txt");
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename));
        BufferedWriter bw = new BufferedWriter(writer);
        bw.write("IDCardNo        SymptomLevel        Status        HospitalID\n");
        for (Record record : Records) {
            //Invoke the toString method of Record.
            bw.write(record.toString() + "\n");
        }
        bw.close();
    }

    /*
     * Export the information of the dashboard
     * The data is finally dumped into Statistics.txt
     * DO NOT change the functionality of the method
     * Otherwise, you may generate wrong results in Task 2
     */
    public void exportDashBoard() throws IOException {
        File filename = new File("output/Statistics.txt");
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename));
        BufferedWriter bw = new BufferedWriter(writer);

        bw.write("AgeRange        patientNums        infectAvgNums        vacNums        vacInfectNums\n");

        for (int i = 0; i < 8; i++) {
            String ageRageStr = "";
            switch (i) {
                case 0:
                    ageRageStr = "(0, 10)";
                    break;
                case 7:
                    ageRageStr = "[70, infinite)";
                    break;
                default:
                    ageRageStr = "[" + String.valueOf(i) + "0, " + String.valueOf(i + 1) + "0)";
                    break;
            }
            String patientNumStr = String.valueOf(dashBoard.patientNums.get(i));
            String infectAvgNumsStr = String.valueOf(dashBoard.infectAvgNums.get(i));
            String vacNumsStr = String.valueOf(dashBoard.vacNums.get(i));
            String vacInfectNumsStr = String.valueOf(dashBoard.vacInfectNums.get(i));

            bw.write(ageRageStr + "        " + patientNumStr + "        " + infectAvgNumsStr + "        " + vacNumsStr + "        " + vacInfectNumsStr + "\n");
        }

        bw.close();
    }

    /* The entry of the project */
    public static void main(String[] args) throws IOException {
        QuarantineSystem system = new QuarantineSystem();
        System.out.println("Start Quarantine System");
        system.startQuarantine();
        System.out.println("Quarantine Finished");
    }
}
