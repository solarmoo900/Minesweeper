package com.joshafeinberg.minesweeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Random;

public class Minesweeper {

    private MainActivity context;
    private int rows;
    private int cols;
    private int mines;
    private int blocksLeft;
    public Block[][] board;
    public Drawable defaultBackground;
    public Drawable mymine;
    public Drawable myflag;
    public Drawable mywrongflag;
    public int gameMode;
    public Constants gameType;
    public static int FLAGMODE = 0;
    public static int MINEMODE = 1;
    public static int ENDMODE = 2;
    public static enum Result {BLOCKHIT, MINEHIT, FLAGGED, CLEARED, EMPTY, NONE, INCORRECTFLAG}

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMines() {
        return mines;
    }

    public Minesweeper(MainActivity context, int rows, int cols, int mines) {
        this.context = context;
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.gameMode = MINEMODE;
        if (Constants.BEGINNER.isEqual(rows,cols,mines)) {
            this.gameType = Constants.BEGINNER;
        } else if (Constants.MEDIUM.isEqual(rows,cols,mines)) {
            this.gameType = Constants.MEDIUM;
        } else if (Constants.HARD.isEqual(rows,cols,mines)) {
            this.gameType = Constants.HARD;
        }
        TextView minesRemaining = (TextView) context.findViewById(R.id.minesRemaining);
        minesRemaining.setText(Integer.toString(mines));
        ImageButton flagButton = (ImageButton) context.findViewById(R.id.flagButton);
        flagButton.setImageDrawable(context.getResources().getDrawable(R.drawable.mine));
        this.blocksLeft = (rows * cols) - mines;
        createBoard();
    }

    /**
     * Creates the actual minesweeper board as well as all Boxes. Sets up certain
     * boxes as mines.
     *
     */
    private void createBoard() {
        ViewGroup frame = (ViewGroup) this.context.findViewById(R.id.minesweeper);
        board = new Block[this.rows][this.cols];

        for (int i = 0; i < this.rows; ++i) {
            TableRow row = new TableRow(context);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 40.0, context.getResources().getDisplayMetrics());
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
            params.gravity = Gravity.CENTER;
            params.setMargins(0, 0, 0, 0);
            row.setLayoutParams(params);
            row.setOrientation(LinearLayout.HORIZONTAL);
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) -2, context.getResources().getDisplayMetrics());
            row.setPadding(padding, padding, padding, padding);


            for (int j = 0; j < this.cols; ++j) {
                this.board[i][j] = new Block(this.context, this);
                final int localI = i;
                final int localJ = j;
                this.board[i][j].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Result result = board[localI][localJ].hitBlock(gameMode);
                        if (result == Result.MINEHIT) {
                            showResults();
                        } else if (result == Result.EMPTY) {
                            fixSurrounding(localI, localJ);
                        } else if (result == Result.FLAGGED) {
                            updateMinesRemaing(true);
                        } else if(result == Result.CLEARED) {
                            updateMinesRemaing(false);
                        }

                        Log.i(context.sharedprefs, "Blocks Left: " + blocksLeft);
                        if (blocksLeft == 0) {
                            endGame(true);
                        }
                    }

                });
                row.addView(this.board[i][j]);
            }
            frame.addView(row);
        }

        // use a while loop for more randomness
        int minesCreated = 0;
        while (minesCreated < this.mines) {
            Random r = new Random();
            int i = r.nextInt(this.rows);
            int j = r.nextInt(this.cols);
            double rando =  (((this.mines-minesCreated) / ((this.rows*this.cols)-((i * this.cols) + j)*1.0)) * 10);
            int surroundingMines = this.board[i][j].getSurrounding();
            int temp = r.nextInt(10-(surroundingMines/2));
            if (rando >= temp ) {
                int result = this.board[i][j].makeMine();
                if (result == 1) {
                    Log.i("jfminesweeper", "Block " + i + "," + j);
                    this.addSurrounding(i, j);
                    minesCreated++;
                }
            }
        }

        ViewTreeObserver vto = frame.getViewTreeObserver();
        if (vto != null) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mymine == null) {
                        createNewImages();
                        defaultBackground = board[0][0].getBackground();
                        Drawable myempty = context.getResources().getDrawable(R.drawable.empty);
                        Drawable[] layers = {defaultBackground, myempty};
                        LayerDrawable layerDrawable = new LayerDrawable(layers);
                        for (int i = 0; i < rows; ++i) {
                            for (int j = 0; j < cols; ++j) {
                                board[i][j].setBackground(layerDrawable);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * When a Box is marked as a mine, the surrounding mines surrounding
     * value is increased
     *
     * @param  x   x coordinate of Box hit
     * @param  y   y coordinate of Box hit
     * @see    com.joshafeinberg.minesweeper.Block
     */
    public void addSurrounding(int x, int y) {
        if (x != 0) {
            this.board[x-1][y].addSurrounding();
            if (y != 0) {
                this.board[x-1][y-1].addSurrounding();
            }
            if (y != this.cols-1) {
                this.board[x-1][y+1].addSurrounding();
            }
        }

        if (x != this.rows-1) {
            this.board[x+1][y].addSurrounding();
            if (y != 0) {
                this.board[x+1][y-1].addSurrounding();
            }
            if (y != this.cols-1) {
                this.board[x+1][y+1].addSurrounding();
            }
        }

        if (y != 0) {
            this.board[x][y-1].addSurrounding();
        }
        if (y != this.cols-1) {
            this.board[x][y+1].addSurrounding();
        }
    }

    /**
     * If Box is hit an has no surrounding mines, the surrounding
     * boxes will be marked as hit
     *
     * @param  x   x coordinate of Box hit
     * @param  y   y coordinate of Box hit
     * @see    com.joshafeinberg.minesweeper.Block
     */
    public void fixSurrounding(int x, int y) {
        if (x != 0) {
            if (this.board[x-1][y].clearMe() == 0) {
                this.fixSurrounding(x-1, y);
            }
            if (y != 0) {
                if (this.board[x-1][y-1].clearMe() == 0) {
                    this.fixSurrounding(x-1, y-1);
                }
            }
            if (y != this.cols-1) {
                if (this.board[x-1][y+1].clearMe() == 0) {
                    this.fixSurrounding(x-1, y+1);
                }
            }
        }

        if (x != this.rows-1) {
            if (this.board[x+1][y].clearMe() == 0) {
                this.fixSurrounding(x+1, y);
            }
            if (y != 0) {
                if (this.board[x+1][y-1].clearMe() == 0) {
                    this.fixSurrounding(x+1, y-1);
                }
            }
            if (y != this.cols-1) {
                if (this.board[x+1][y+1].clearMe() == 0) {
                    this.fixSurrounding(x+1, y+1);
                }
            }
        }

        if (y != 0) {
            if (this.board[x][y-1].clearMe() == 0) {
                this.fixSurrounding(x, y-1);
            }
        }
        if (y != this.cols-1) {
            if (this.board[x][y+1].clearMe() == 0) {
                this.fixSurrounding(x, y+1);
            }
        }
    }

    /**
     * Show final results when a game is finished
     */
    public void showResults() {
        for (int i = 0; i < this.rows; ++i) {
            for (int j = 0; j < this.cols; ++j) {
                this.board[i][j].hitBlock(ENDMODE);
                this.board[i][j].setClickable(false);
            }
        }

        endGame(false);
    }

    public void endGame(boolean victory) {
        String message;
        if (victory) {
            context.gameFinished(true);
            message = this.context.getResources().getString(R.string.victory);
        } else {
            context.gameFinished(false);
            message = this.context.getResources().getString(R.string.defeat);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.newgame, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                context.newGameMenu(false);
            }
        });
        final ImageButton flagButton = (ImageButton) this.context.findViewById(R.id.flagButton);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gameMode = Minesweeper.ENDMODE;
                flagButton.setImageDrawable(context.getResources().getDrawable(R.drawable.smile));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                gameMode = Minesweeper.ENDMODE;
                flagButton.setImageDrawable(context.getResources().getDrawable(R.drawable.smile));
            }
        });

        dialog.show();

    }

    public void updateMinesRemaing(boolean add) {
        TextView minesRemaining = (TextView) this.context.findViewById(R.id.minesRemaining);
        CharSequence newValueCharSequence = minesRemaining.getText();
        String newValueString = "";
        if (newValueCharSequence != null) {
           newValueString = newValueCharSequence.toString();
        }
        int newValue = Integer.valueOf(newValueString);
        if (add) {
            newValue++;
        } else {
            newValue--;
        }
        minesRemaining.setText(Integer.toString(newValue));
    }

    public void fixBlocksLeft() {
        this.blocksLeft--;
    }

    public void clearOldGame() {
        TableLayout frame = (TableLayout) this.context.findViewById(R.id.minesweeper);
        frame.removeAllViews();
    }

    public void createNewImages() {
        CreateImages ci = new CreateImages();
        ci.doInBackground();
    }

    private class CreateImages extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Bitmap mine = BitmapFactory.decodeResource(context.getResources(), R.drawable.mine);
            Bitmap mineScaled = Bitmap.createScaledBitmap(mine, board[0][0].getWidth(),
                    board[0][0].getHeight(), true);
            mymine = new BitmapDrawable(context.getResources(),mineScaled);
            Bitmap flag = BitmapFactory.decodeResource(context.getResources(), R.drawable.flag);
            Bitmap flagScaled = Bitmap.createScaledBitmap(flag, board[0][0].getWidth(),
                    board[0][0].getHeight(), true);
            myflag = new BitmapDrawable(context.getResources(),flagScaled);
            Bitmap wrongflag = BitmapFactory.decodeResource(context.getResources(), R.drawable.wrongflag);
            Bitmap wrongFlagScaled = Bitmap.createScaledBitmap(wrongflag, board[0][0].getWidth(),
                    board[0][0].getHeight(), true);
            mywrongflag = new BitmapDrawable(context.getResources(),wrongFlagScaled);
            return null;
        }
    }



}