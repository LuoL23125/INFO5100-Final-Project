package wellnesschainsupplysystem.model;

public class ClinicBranch {

    private int id;
    private String name;
    private String address;
    private String city;
    private String openingTime;
    private String closingTime;

    public ClinicBranch() {
    }

    public ClinicBranch(int id, String name, String address, String city,
                        String openingTime, String closingTime) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public ClinicBranch(String name, String address, String city,
                        String openingTime, String closingTime) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    @Override
public String toString() {
    return name + " (" + city + ")";
}

}
