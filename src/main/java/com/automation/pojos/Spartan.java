package com.automation.pojos;
import com.google.gson.annotations.SerializedName;
//          {
//           "id": 112,
//           "name": "Vasya",
//           "gender": "Male",
//           "phone": 7654321876
//           },
public class Spartan {
    @SerializedName("id")// "id"
    private int spartanId;
    private String name;// "name"
    private String gender;//"gender"
    private long phone;//"phone"
    public Spartan() {
    }
    public Spartan(String name, String gender, long phone) {
        this.name = name;
        this.gender = gender;
        this.phone = phone;
    }
    public Spartan(int spartanId, String name, String gender, long phone) {
        this.spartanId = spartanId;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
    }
    public int getSpartanId() {
        return spartanId;
    }
    public void setSpartanId(int spartanId) {
        this.spartanId = spartanId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public long getPhone() {
        return phone;
    }
    public void setPhone(long phone) {
        this.phone = phone;
    }
    @Override
    public String toString() {
        return "Spartan{" +
                "spartanId=" + spartanId +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", phone=" + phone +
                '}';
    }
    public Spartan withPhone(long phone) {
        this.phone = phone;
        return this;
    }
    public Spartan withName(String name) {
        this.name = name;
        return this;
    }
    public Spartan withGender(String gender) {
        this.gender = gender;
        return this;
    }
}