package com.joshafeinberg.minesweeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableRow;

public class Block extends Button {

    private Minesweeper minesweeperGame;
    private boolean isMine;
    private enum State {DEFAULT, HIT, FLAGGED, EMPTY}
    private State status;
    private int surrounding;

    public int getSurrounding() {
        return this.surrounding;
    }

    public String getSurroundingString() {
        return Integer.toString(this.surrounding);
    }

    /**
     * Creates a button with additional attributes
     *
     * @param  context   context of the game for reference to screen and timer
     */
    public Block(Context context, Minesweeper minesweeperGame) {
        super(context);
        this.setBackgroundResource(R.drawable.baseblock);

        this.minesweeperGame = minesweeperGame;
        this.isMine = false;
        this.status = State.DEFAULT;
        this.surrounding = 0;
        this.setText(" ");
        Resources a = getResources();
        if (a != null) {
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 48.0, a.getDisplayMetrics());
            TableRow.LayoutParams params = new TableRow.LayoutParams(width, width);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.setMargins(-2, -2, -2, -2);
            this.setLayoutParams(params);
            float font = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    (float) 15.0, a.getDisplayMetrics());
            this.setTextSize(font);
        }
    }

    public Minesweeper.Result hitBlock(int mode) {
        if (mode == Minesweeper.MINEMODE) {
            if (isMine) {
                mineHit();
                return Minesweeper.Result.MINEHIT;
            }

            if (this.status == State.DEFAULT) {
                if (this.surrounding > 0) {
                    blockHit();
                    return Minesweeper.Result.BLOCKHIT;
                } else {
                    emptyHit();
                    return Minesweeper.Result.EMPTY;
                }
            }
        } else if (mode == Minesweeper.ENDMODE) {
            if (isMine && this.status == State.DEFAULT) {
                mineHit();
                return Minesweeper.Result.MINEHIT;
            }
            if (this.status == State.FLAGGED) {
                incorrectFlagHit();
                return Minesweeper.Result.INCORRECTFLAG;
            }
        } else if (mode == Minesweeper.FLAGMODE) {
            flagHit();
            if (this.status == State.FLAGGED) {
                return Minesweeper.Result.CLEARED;
            } else {
                return Minesweeper.Result.FLAGGED;
            }
        }
        return Minesweeper.Result.NONE;
    }

    private void mineHit() {
        this.status = State.HIT;
        Drawable[] layers = {minesweeperGame.defaultBackground, minesweeperGame.mymine};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        this.setBackground(layerDrawable);
    }
    private void blockHit() {
        this.status = State.HIT;
        this.setText(this.getSurroundingString());
        minesweeperGame.fixBlocksLeft(false);
    }

    private void emptyHit() {
        this.status = State.HIT;
        this.setBackgroundResource(R.drawable.emptyblock);
        this.setText(" ");
        minesweeperGame.fixBlocksLeft(false);
    }

    private void flagHit() {
        if (this.status == State.FLAGGED) {
            this.setBackground(minesweeperGame.defaultBackground);
            this.status = State.DEFAULT;
            if (this.isMine) {
                minesweeperGame.fixBlocksLeft(false);
            }
        } else {
            Drawable[] layers = {minesweeperGame.defaultBackground, minesweeperGame.myflag};
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            this.setBackground(layerDrawable);
            this.status = State.FLAGGED;
            if (this.isMine) {
                minesweeperGame.fixBlocksLeft(true);
            }
        }
    }

    private void incorrectFlagHit() {
        Drawable[] layers = {minesweeperGame.defaultBackground, minesweeperGame.mywrongflag};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        this.setBackground(layerDrawable);
    }

    @Override
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void setBackground(Drawable newBackground) {
        int width = this.getWidth();
        int height = this.getHeight();
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            super.setBackground(newBackground);
        } else {
            super.setBackgroundDrawable(newBackground);
        }
        if (height > 0 && width > 0) {
            this.setWidth(width);
            this.setHeight(height);
        }
    }

    /**
     * If a non-hit Box with 0 surrounding it will be hit automatically if a
     * surrounding non-hit Box with 0 surrounding is hit
     *
     * @return      the surrounding mines
     */
    public int clearMe() {
        if (this.status == State.HIT || this.status == State.FLAGGED) {
            return 1;
        }
        if (!this.isMine && this.status == State.DEFAULT) {
            this.hitBlock(Minesweeper.MINEMODE);
            /*if (this.surrounding == 0) {
                this.setText("0");
            } else {
                this.setText(this.getSurroundingString());
            }*/
        }
        return this.surrounding;
    }

    public int addSurrounding() {
        return ++this.surrounding;
    }

    public int makeMine() {
        if (this.isMine) {
            return 0;
        } else {
            this.isMine = true;
            return 1;
        }
    }

}
