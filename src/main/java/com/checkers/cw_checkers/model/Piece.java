package com.checkers.cw_checkers.model;

public class Piece {
    public enum Color { GREEN, PINK }
    public enum Type { NORMAL, QUEEN }

    private final Color color;
    private Type type;

    public Piece(Color color) {
        this.color = color;
        this.type = Type.NORMAL;
    }

    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }

    public boolean isQueen() {
        return type == Type.QUEEN;
    }

    public void promote() {
        this.type = Type.QUEEN;
    }
}

