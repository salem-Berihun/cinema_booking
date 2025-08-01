package model;

public class Movie {
    private String title;
    private String description;
    private int duration; // in minutes
    private String genre;

    public Movie(String title, String description, int duration, String genre) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.genre = genre;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDuration() { return duration; }
    public String getGenre() { return genre; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setGenre(String genre) { this.genre = genre; }

    @Override
    public String toString() {
        return title + " (" + genre + ") - " + duration + " mins";
    }
}
