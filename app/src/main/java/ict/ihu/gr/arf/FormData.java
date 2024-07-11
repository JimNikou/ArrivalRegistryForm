package ict.ihu.gr.arf;

public class FormData {
    String fullName;
    String streetAddress;
    String zipCode;
    String town;
    String email;
    String phoneNumber;
    String idNo;
    String nationality;
    String dateTime;
    String paymentType;
    String documentType;
    public String getFullName() {
        return fullName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getTown() {
        return town;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getIdNo() {
        return idNo;
    }

    public String getNationality() {
        return nationality;
    }

    public String getDateTime() {
        return dateTime;
    }
    public String getPaymentType(){
        return paymentType;
    }
    public String getDocumentType(){
        return documentType;
    }
    public FormData(String fullName, String streetAddress, String zipCode, String town, String email, String phoneNumber, String idNo, String nationality, String dateTime, String paymentType, String documentType) {
        this.fullName = fullName;
        this.streetAddress = streetAddress;
        this.zipCode = zipCode;
        this.town = town;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.idNo = idNo;
        this.nationality = nationality;
        this.dateTime = dateTime;
        this.paymentType = paymentType;
        this.documentType = documentType;
    }

    //add getter methods
}
