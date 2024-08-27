package customer.bean.sys;

import java.time.LocalDate;
import java.util.ArrayList;
import lombok.*;

public class Sys001User {
    private String userId;
    private String userType;
    private String bpNumber;
    private String userStatus;
    private String userName;
    private LocalDate validDateTo;
    private LocalDate validDateFrom;

    private ArrayList<String> roles;
    private ArrayList<String> plants;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getBpNumber() {
        return bpNumber;
    }

    public void setBpNumber(String bpNumber) {
        this.bpNumber = bpNumber;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getValidDateTo() {
        return validDateTo;
    }

    public void setValidDateTo(LocalDate validDateTo) {
        this.validDateTo = validDateTo;
    }

    public LocalDate getValidDateFrom() {
        return validDateFrom;
    }

    public void setValidDateFrom(LocalDate validDateFrom) {
        this.validDateFrom = validDateFrom;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public ArrayList<String> getPlants() {
        return plants;
    }

    public void setPlants(ArrayList<String> plants) {
        this.plants = plants;
    }

}
