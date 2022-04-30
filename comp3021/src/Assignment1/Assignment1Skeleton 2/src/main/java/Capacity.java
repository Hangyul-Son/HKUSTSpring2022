public class Capacity {
    public int CriticalCapacity;
    public int ModerateCapacity;
    public int MildCapacity;

    public Capacity(int p_CriticalCapacity, int p_ModerateCapacity, int p_MildCapacity) {
        this.CriticalCapacity = p_CriticalCapacity;
        this.ModerateCapacity = p_ModerateCapacity;
        this.MildCapacity = p_MildCapacity;
    }

    public int getSingleCapacity(SymptomLevel symptomLevel) {
        switch (symptomLevel) {
            case Critical:
                return this.CriticalCapacity;
            case Moderate:
                return this.ModerateCapacity;
            default:
                return this.MildCapacity;
        }
    }

    public boolean decreaseCapacity(SymptomLevel symptomLevel) {
        switch (symptomLevel) {
            case Critical:
                if (this.CriticalCapacity > 0) {
                    this.CriticalCapacity -= 1;
                    return false;
                }
                break;
            case Moderate:
                if (this.ModerateCapacity > 0) {
                    this.ModerateCapacity -= 1;
                    return false;
                }
                break;
            case Mild:
                if (this.MildCapacity > 0) {
                    this.MildCapacity -= 1;
                    return false;
                }
                break;
            default:
                break;
        }
        return false;
    }

    public boolean increaseCapacity(SymptomLevel symptomLevel) {
        switch (symptomLevel) {
            case Critical:
                this.CriticalCapacity += 1;
                return true;
            case Moderate:
                this.ModerateCapacity += 1;
                return true;
            case Mild:
                this.MildCapacity += 1;
                return true;
            default:
                break;
        }
        return false;
    }
}
