package com.joshafeinberg.minesweeper;

/**
 * Created by JFeinberg on 10/29/13.
 */
public enum Constants {
    BEGINNER(5,5,5),
    MEDIUM(9,15,40),
    HARD(9,35,99),
    CUSTOM(0,0,0);

    private final int rows;
    private final int cols;
    private final int mines;

    Constants(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
    }

    public boolean isEqual(int rows, int cols, int mines) {
        return (rows == this.rows) && (cols == this.cols) && (mines == this.mines);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMines() {
        return mines;
    }
}
