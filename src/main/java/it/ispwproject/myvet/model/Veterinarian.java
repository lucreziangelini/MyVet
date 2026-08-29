package it.ispwproject.myvet.model;

import it.ispwproject.myvet.enumerator.Role;

public class Veterinarian extends User {

    private String clinicAddress;

    public Veterinarian() {
        super();
    }

    public Veterinarian(int id,
                        String name,
                        String surname,
                        String email,
                        String password,
                        String clinicAddress) {
        super(
                id,
                name,
                surname,
                email,
                password,
                Role.VETERINARIAN
        );

        this.clinicAddress = clinicAddress;
    }

    public String getClinicAddress() {return clinicAddress;}
    public void setClinicAddress(String clinicAddress) {this.clinicAddress = clinicAddress;}

}
