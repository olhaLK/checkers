package com.checkers.cw_checkers.model;

public class Player {
    private final String name;
    private final Piece.Color color;
    private int score;

    public Player(String name, Piece.Color color) {
        this.name = name;
        this.color = color;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public Piece.Color getColor() {
        return color;
    }

    private static int greenCount = 12;
    private static int pinkCount = 12;

    public static void decreaseScore(Piece.Color color) {
        if (color == Piece.Color.GREEN) greenCount--;
        else if (color == Piece.Color.PINK) pinkCount--;
    }

    public int getScore() {
        return (color == Piece.Color.GREEN) ? greenCount : pinkCount;
    }

    private int kills = 0;

    public void incrementKills() {
        kills++;
    }

    public int getKills() {
        return kills;
    }

    public void addPoint() {
        score++;
    }
}
