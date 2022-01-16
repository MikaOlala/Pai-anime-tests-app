package com.example.pai;

public class Character {
    private int id;
    private String name;
    private String anime;

    public Character() {
    }

    public Character(int id, String name, String anime) {
        this.id = id;
        this.name = name;
        this.anime = anime;
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

    public String getAnime() {
        return anime;
    }

    public void setAnime(String anime) {
        this.anime = anime;
    }
}
