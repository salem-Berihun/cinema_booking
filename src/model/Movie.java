package model;

public class Movie {
    private int id;
    private String title;
    private String description; // Added
    private int durationMinutes;
    private String genre;       // Added

    // Constructor for creating new movies (without ID)
    public Movie(String title, String description, int durationMinutes, String genre) {
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.genre = genre;
    }

    // Constructor for retrieving movies from the database (with ID)
    public Movie(int id, String title, String description, int durationMinutes, String genre) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.genre = genre;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getGenre() {
        return genre;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Movie ID: " + id +
                ", Title: '" + title + '\'' +
                ", Description: '" + description + '\'' +
                ", Duration: " + durationMinutes + " mins" +
                ", Genre: '" + genre + '\'';
    }
}