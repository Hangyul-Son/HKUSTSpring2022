public class Person {
    String IDCardNo;
    private Location loc;
    private String gender;
    private int age;
    private boolean isVac;
    private int infectCnt;

    /* Class Constructor */
    public Person(String p_IDCardNo, Location p_loc, String p_gender, int p_age, boolean p_isVac) {
        //TODO
        IDCardNo = p_IDCardNo;
        loc = p_loc;
        gender = p_gender;
        age = p_age;
        isVac = p_isVac;
    }

    public void setIDCardNo(String p_IDCardNo) {
        this.IDCardNo = p_IDCardNo;
    }

    public void setLoc(Location p_loc) {
        this.loc = p_loc;
    }

    public void setGender(String p_gender) {
        this.gender = p_gender;
    }

    public void setAge(int p_age) {
        this.age = p_age;
    }

    public void setIsVac(Boolean p_isVac) {
        this.isVac = p_isVac;
    }

    public void setInfectCnt(int p_infectCnt) {
        this.infectCnt = p_infectCnt;
    }

    public String getIDCardNo() {
        return this.IDCardNo;
    }

    public Location getLoc() {
        return this.loc;
    }

    public String getGender() {
        return this.gender;
    }

    public int getAge() {
        return this.age;
    }

    public boolean getIsVac() {
        return this.isVac;
    }

    public int getInfectCnt() {
        return this.infectCnt;
    }

    /* Dump the person info as a string */
    public String toString() {
        String str = "";
        str += IDCardNo + "        ";
        str += loc.xloc + "        " + loc.yloc + "        ";
        str += gender + "        ";
        str += age + "        ";
        str += (isVac ? "Yes" : "No");
        return str;
    }
}
