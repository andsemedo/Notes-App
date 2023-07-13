package com.example.notesapp.classes;

public class User {
    String nome, photo;

    public User() {

    }

    public User(String nome, String photo) {
        this.nome = nome;
        this.photo = photo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
