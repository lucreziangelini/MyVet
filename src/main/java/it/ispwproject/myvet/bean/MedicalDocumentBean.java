package it.ispwproject.myvet.bean;

import it.ispwproject.myvet.enumerator.DocumentType;

import java.time.LocalDateTime;

public class MedicalDocumentBean {

    private int id;
    private PetBean pet;
    private VeterinarianBean veterinarian;
    private String title;
    private DocumentType type;
    private String storageReference;
    private LocalDateTime uploadedAt;

    public MedicalDocumentBean() {
    }

    public MedicalDocumentBean(int id,
                               PetBean pet,
                               VeterinarianBean veterinarian,
                               String title,
                               DocumentType type,
                               String storageReference,
                               LocalDateTime uploadedAt) {
        this.id = id;
        this.pet = pet;
        this.veterinarian = veterinarian;
        this.title = title;
        this.type = type;
        this.storageReference = storageReference;
        this.uploadedAt = uploadedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PetBean getPet() {
        return pet;
    }

    public void setPet(PetBean pet) {
        this.pet = pet;
    }

    public VeterinarianBean getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(VeterinarianBean veterinarian) {
        this.veterinarian = veterinarian;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getStorageReference() {
        return storageReference;
    }

    public void setStorageReference(String storageReference) {
        this.storageReference = storageReference;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}