package com.joshafeinberg.minesweeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

    public Minesweeper minesweeperGame;
    public final String sharedprefs = "jfminesweeper";
    public SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.minesweeperGame = new Minesweeper(this, 5, 5, 2);
        addResizeButtons();
        addGameControlButton();
        prefs = getSharedPreferences(sharedprefs, MainActivity.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (minesweeperGame == null) {
            newGameMenu(true);
            ImageButton flagButton = (ImageButton) findViewById(R.id.flagButton);
            flagButton.setImageDrawable(getResources().getDrawable(R.drawable.smile));
        }
    }

    public void addGameControlButton() {
        final ImageButton flagButton = (ImageButton) findViewById(R.id.flagButton);
        flagButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (minesweeperGame.gameMode == Minesweeper.FLAGMODE) {
                    minesweeperGame.gameMode = Minesweeper.MINEMODE;
                    flagButton.setImageDrawable(getResources().getDrawable(R.drawable.mine));
                } else if (minesweeperGame.gameMode == Minesweeper.MINEMODE) {
                    minesweeperGame.gameMode = Minesweeper.FLAGMODE;
                    flagButton.setImageDrawable(getResources().getDrawable(R.drawable.flag));
                } else {
                    newGameMenu(false);
                    //flagMode = 0;
                    //flagButton.setImageDrawable(getResources().getDrawable(R.drawable.flag));
                }
            }
        });

    }

    public void addResizeButtons() {
        /*ImageView zoomOutButton = (ImageView) findViewById(R.id.zoomout);
        zoomOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resizeMe(1);
            }
        });

        ImageView zoomInButton = (ImageView) findViewById(R.id.zoomin);
        zoomInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resizeMe(0);
            }
        });

        final LinearLayout zoom = (LinearLayout) findViewById(R.id.zoomme);
        final android.os.CountDownTimer cdt = new android.os.CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
                //zoom.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                zoom.setVisibility(View.GONE);

            }
        }.start();
        ScrollView sview = (ScrollView) findViewById(R.id.verticalLayout);
        HorizontalScrollView hview = (HorizontalScrollView) findViewById(R.id.horizontalLayout);
        ScrollView.OnTouchListener mylistener = new ScrollView.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                cdt.cancel();
                cdt.start();
                return false;
            }
        };
        sview.setOnTouchListener(mylistener);
        hview.setOnTouchListener(mylistener);*/
    }


    // ToDo: This still needs a massive rewrite. Perhaps try creating static button sizes
    /*public void resizeMe(int big) {
        double resizeButton;
        double resizeText;
        int newButtonHeight = 0;
        boolean myCondition;
        boolean isSizeChanged = false;
        if (big == 0) {
            resizeButton = 1.25;
            resizeText = 0.3125;
            //resizeText = 1;
            myCondition = true;
        } else {
            resizeButton = 0.8;
            resizeText = 0.3125;
            myCondition = false;
        }

        for (int x = 0; x < minesweeperGame.getRows(); ++x) {
            for (int y = 0; y < minesweeperGame.getCols(); ++y) {
                Button change = minesweeperGame.board[x][y];
                ViewGroup.LayoutParams params = change.getLayoutParams();
                if (params != null) {
                    if (((change.getHeight() * resizeButton > 50.0) && !myCondition) || ((change.getHeight() * resizeButton < 100.0) && myCondition)) {
                        params.height = (int) (change.getHeight() * resizeButton);
                        newButtonHeight = params.height;
                        params.width = (int) (change.getWidth() * resizeButton);
                        change.setLayoutParams(params);
                        change.setTextSize((float) (params.height * resizeText));
                        isSizeChanged = true;
                    }
                }
            }
        }
        View parent = (View) minesweeperGame.board[0][0].getParent().getParent().getParent();
        // Check for null even though it can never return null
        if (parent == null) { return; }
        parent.setMinimumHeight(((newButtonHeight+parent.getPaddingTop()) * minesweeperGame.getRows()));
        parent.setPadding(parent.getPaddingLeft(),
                parent.getPaddingTop(),
                    //Math.abs( parent.getHeight() - ((newButtonHeight) * minesweeperGame.getRows())),
                    parent.getPaddingRight(), parent.getPaddingBottom());

        Log.i("jfminesweeper", "parent: " + parent.toString());
        Log.i("jfminesweeper", "parent height: " + parent.getHeight());
        Log.i("jfminesweeper", "button height: " + newButtonHeight);
        Log.i("jfminesweeper", "paddingTop: " + parent.getPaddingTop());


        if (isSizeChanged) {
            minesweeperGame.createNewImages();
        }

    }*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_newgame) {
            //this.timer.stop();
            this.newGameMenu(false);
        }

        /*if (item.getItemId() == R.id.menu_stats) {
            this.timer.stop();
            this.statsMenu();
        }*/

        return super.onOptionsItemSelected(item);
    }



    /**
     * Creates the new game menu screen with buttons to increase and decrease
     * the numbers. It starts the new game and it's timer
     *
     * @param  onStart  specifies if being called when game is going (true) or on boot (false)
     */
    @SuppressWarnings("ConstantConditions")
    public void newGameMenu(boolean onStart) {
        LayoutInflater factory = LayoutInflater.from(this);
        View newGameView = factory.inflate(R.layout.newgamemenu, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("New Game");
        alert.setView(newGameView);

        Button begButton = (Button) newGameView.findViewById(R.id.begButton);
        Button medButton = (Button) newGameView.findViewById(R.id.medButton);
        Button hardButton = (Button) newGameView.findViewById(R.id.hardButton);

        Button rowAdd = (Button) newGameView.findViewById(R.id.rowsAdd);
        final TextView numRows = (TextView) newGameView.findViewById(R.id.numRows);
        Button rowSub = (Button) newGameView.findViewById(R.id.rowsSub);

        Button colAdd = (Button) newGameView.findViewById(R.id.colsAdd);
        final TextView numCols = (TextView) newGameView.findViewById(R.id.numCols);
        Button colSub = (Button) newGameView.findViewById(R.id.colsSub);

        Button mineAdd = (Button) newGameView.findViewById(R.id.minesAdd);
        final TextView numMines = (TextView) newGameView.findViewById(R.id.numMines);
        Button mineSub = (Button) newGameView.findViewById(R.id.minesSub);

        int lastRows = this.prefs.getInt("lastRows", Constants.BEGINNER.getRows());
        int lastCols = this.prefs.getInt("lastCols", Constants.BEGINNER.getCols());
        int lastMines = this.prefs.getInt("lastMines", Constants.BEGINNER.getMines());
        numRows.setText(Integer.toString(lastRows));
        numCols.setText(Integer.toString(lastCols));
        numMines.setText(Integer.toString(lastMines));


        begButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                numRows.setText(Integer.toString(Constants.BEGINNER.getRows()));
                numCols.setText(Integer.toString(Constants.BEGINNER.getCols()));
                numMines.setText(Integer.toString(Constants.BEGINNER.getMines()));
            }
        });
        medButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                numRows.setText(Integer.toString(Constants.MEDIUM.getRows()));
                numCols.setText(Integer.toString(Constants.MEDIUM.getCols()));
                numMines.setText(Integer.toString(Constants.MEDIUM.getMines()));
            }
        });
        hardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                numRows.setText(Integer.toString(Constants.HARD.getRows()));
                numCols.setText(Integer.toString(Constants.HARD.getCols()));
                numMines.setText(Integer.toString(Constants.HARD.getMines()));
            }
        });

        rowAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rows = Integer.parseInt(numRows.getText().toString()) + 1;
                numRows.setText(Integer.toString(rows));
            }
        });
        rowSub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rows = Integer.parseInt(numRows.getText().toString()) - 1;
                if (rows < 1) {
                    rows = 1;
                }
                numRows.setText(Integer.toString(rows));
                int cols = Integer.parseInt(numCols.getText().toString());
                int mines = Integer.parseInt(numMines.getText().toString());
                if (mines > ((rows * cols) - 1)) {
                    numMines.setText(Integer.toString(((rows * cols) - 1)));
                }
            }
        });

        colAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int cols = Integer.parseInt(numCols.getText().toString()) + 1;
                numCols.setText(Integer.toString(cols));
            }
        });
        colSub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int cols = Integer.parseInt(numCols.getText().toString()) - 1;
                if (cols < 1) {
                    cols = 1;
                }
                numCols.setText(Integer.toString(cols));
                int rows = Integer.parseInt(numRows.getText().toString());
                int mines = Integer.parseInt(numMines.getText().toString());
                if (mines > ((rows * cols) - 1)) {
                    numMines.setText(Integer.toString(((rows * cols) - 1)));
                }
            }
        });

        mineAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int mines = Integer.parseInt(numMines.getText().toString()) + 1;
                int rows = Integer.parseInt(numRows.getText().toString());
                int cols = Integer.parseInt(numCols.getText().toString());
                if (mines > ((rows*cols) - 1)) {
                    mines = (rows * cols) - 1;
                }
                numMines.setText(Integer.toString(mines));
            }
        });
        mineSub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int mines = Integer.parseInt(numMines.getText().toString()) - 1;
                if (mines < 1) {
                    mines = 1;
                }
                numMines.setText(Integer.toString(mines));
            }
        });

        final MainActivity myContext = this;
        alert.setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (minesweeperGame != null) {
                    if (minesweeperGame.gameMode != Minesweeper.ENDMODE) {
                        gameFinished(false);
                    }
                    minesweeperGame.clearOldGame();
                }
                int mines = Integer.parseInt(numMines.getText().toString());
                int rows = Integer.parseInt(numRows.getText().toString());
                int cols = Integer.parseInt(numCols.getText().toString());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("lastRows", rows);
                editor.putInt("lastCols", cols);
                editor.putInt("lastMines", mines);
                editor.commit();
                minesweeperGame = new Minesweeper(myContext, rows, cols, mines);
            }
        });

        final ImageButton flagButton = (ImageButton) findViewById(R.id.flagButton);

        AlertDialog dialog = null;
        if (!onStart) {
            alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog = alert.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    if (minesweeperGame == null) {
                        //TODO: restart timer

                    } else {
                        minesweeperGame.gameMode = Minesweeper.ENDMODE;
                        flagButton.setImageDrawable(getResources().getDrawable(R.drawable.smile));
                    }
                }
            });
        }

        if (dialog == null) {
            dialog = alert.create();
            dialog.setCancelable(false);
        }
        dialog.show();
    }


    public void gameFinished(boolean victory) {
        int mines = minesweeperGame.getMines();
        int rows = minesweeperGame.getRows();
        int cols = minesweeperGame.getCols();
        if (Constants.BEGINNER.isEqual(rows,cols,mines)) {
            SharedPreferences.Editor editor = prefs.edit();
            if (victory) {
                // TODO: finish time running
                int score = prefs.getInt("begWins", 0);
                editor.putInt("begWins", ++score);
            } else {
                int score = prefs.getInt("begLosses", 0);
                editor.putInt("begLosses", ++score);
            }
            editor.commit();
        } else if (Constants.MEDIUM.isEqual(rows,cols,mines)) {
            SharedPreferences.Editor editor = prefs.edit();
            if (victory) {
                int score = prefs.getInt("medWins", 0);
                editor.putInt("medWins", ++score);
            } else {
                int score = prefs.getInt("medLosses", 0);
                editor.putInt("medLosses", ++score);
            }
            editor.commit();
        } else if (Constants.HARD.isEqual(rows,cols,mines)) {
            SharedPreferences.Editor editor = prefs.edit();
            if (victory) {
                int score = prefs.getInt("hardWins", 0);
                editor.putInt("hardWins", ++score);
            } else {
                int score = prefs.getInt("hardLosses", 0);
                editor.putInt("hardLosses", ++score);
            }
            editor.commit();
        }
    }

}
