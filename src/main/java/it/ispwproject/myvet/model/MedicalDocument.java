package it.ispwproject.myvet.model;

import it.ispwproject.myvet.enumerator.DocumentType;

import java.time.LocalDateTime;

public class MedicalDocument {

    private int id;
    private Pet pet;
    private Veterinarian veterinarian;
    private String title;
    private DocumentType type;
    private String storageReference;
    private LocalDateTime uploadedAt;

    public MedicalDocument() {
        // Used when reconstructing a document from persistence
    }

    public MedicalDocument(int id,
                           Pet pet,
                           Veterinarian veterinarian,
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

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Veterinarian getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(Veterinarian veterinarian) {
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