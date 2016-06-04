package board;

/**
 * Created by Sandra on 2016-05-16.
 */

import algorithms.Game;
import algorithms.Move;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;

public class Board extends Applet {

    private static final String SEPARATOR = " ";
    public static int noOfPlayers = 2;
    private static boolean AIvsAI = true;
    private static ImageIcon bPlayer = new ImageIcon("D:\\Studenckie\\sem6\\projects\\si_zad3_reversi\\src\\main\\resources\\bball.png");
    private static ImageIcon wPlayer = new ImageIcon("D:\\Studenckie\\sem6\\projects\\si_zad3_reversi\\src\\main\\resources\\rsz_bball.png");
    private static final String [] heuristicKeys = {"teritory", "risk regions", "actual score", "moves left"};
    private static int defaultBDepth = 2;
    private static int defaultWDepth = 2;
    private static boolean stopWhite = false;
    private static boolean stopBlack = false;
    private static boolean minmaxBEnabled = true;
    private static boolean minmaxWEnabled = true;
    //GUI
    private static int defaultBoardSize = 8;
    private static JLabel[][] gui;
    private static JFrame boardFrame;
    private static int width = 35;

    //results
    public static int wScore = 0;
    public static int bScore = 0;
    public static int timeWhite = 0;
    public static int timeBlack = 0;
    public static int wMovesCount = 0;
    public static int bMovesCount = 0;

    private static final long serialVersionUID = 1L;
    public static int[][] board;
    public static boolean stop = false;
    public static boolean bStillCanMove = true;
    public static boolean wStillCanMove = true;




    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    startGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private static void startGame() throws InterruptedException {
        Board window = new Board();
        window.boardFrame.setVisible(true);
    }


    public Board() throws InterruptedException {
        initialize();
    }

    private void initialize() throws InterruptedException {

        setupBoard(defaultBoardSize);
        setupFrameAppearance();


        JButton passB = new JButton("|>");
        passB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                noOfPlayers = noOfPlayers == 1 ? 2 : 1;

                if (noOfPlayers == 1)
                    try {
                        moveAI();
                    } catch (InterruptedException e1) {
                    }
                update();
                noOfPlayers = noOfPlayers == 1 ? 2 : 1;
            }
        });
        passB.setBounds(0, 0, 40, 40);
        boardFrame.getContentPane().add(passB);

        gui = new JLabel[defaultBoardSize][defaultBoardSize];

        for (int i = 0; i < gui.length; i++)
            for (int j = 0; j < gui[0].length; j++) {
                setupBoardCell(i,j);

                if (!AIvsAI) {
                    gui[i][j].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                            Object o;
                            if ((o = e.getSource()) instanceof JLabel) {
                                JLabel source = (JLabel) o;
                                String[] indexesS = source.getText().split(SEPARATOR);
                                int col = Integer.parseInt(indexesS[0]);
                                int row = Integer.parseInt(indexesS[1]);
                                if (updateBoard(col, row, true, board)) {
                                    if (noOfPlayers == 2) {
                                        board[col][row] = 2;
                                        source.setIcon(bPlayer);
                                    }

                                    noOfPlayers = noOfPlayers == 1 ? 2 : 1;

                                    if (noOfPlayers == 1)
                                        try {
                                            moveAI();
                                        } catch (InterruptedException e1) {
                                        }
                                    update();
                                    noOfPlayers = noOfPlayers == 1 ? 2 : 1;
                                }
                            }
                        }
                    });
                }

                else {
                    gui[i][j].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                            stop = false;
                            while (!stop) {
                                if (noOfPlayers == 1) {
                                    long before = new Date().getTime();
                                    try {
                                        moveAI();
                                    } catch (InterruptedException e1) {
                                    }
                                    long after = new Date().getTime();
                                    timeWhite += after - before;
                                }

                                if (noOfPlayers == 2) {
                                    long before = new Date().getTime();
                                    try {
                                        moveBlack();
                                    } catch (InterruptedException e1) {
                                    }
                                    long after = new Date().getTime();
                                    timeBlack += after - before;
                                }

                                noOfPlayers = noOfPlayers == 1 ? 2 : 1;
                                update();

                            }

                            int[] counts = getActualScore(board);
                            wScore = counts[0];
                            bScore = counts[1];

                            System.out.println("Game Over. \n Black - moves: " + bMovesCount + ", time: " + timeBlack + ", scores: " + bScore);
                            System.out.println(" Whites - moves: " + wMovesCount + ", time: " + timeWhite + ", scores: " + wScore);

                        }
                    });
                }

                if (board[i][j] == 0) {
                    finalizeMove(i, j, null);
                }
                if (board[i][j] == 1) {
                    finalizeMove(i, j, wPlayer);
                }
                if (board[i][j] == 2) {
                    finalizeMove(i, j, bPlayer);
                }
                boardFrame.getContentPane().add(gui[i][j]);

            }
        boardFrame.getContentPane().add(passB);

    }

    private void setupFrameAppearance() {
        boardFrame = new JFrame();
        boardFrame.setBounds(50, 50, 10 * width, (21 * width) / 2);
        boardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        boardFrame.getContentPane().setLayout(null);
        boardFrame.getContentPane().setBackground(Color.GREEN);
    }


    private void setupBoard(int N){
        board = new int[N][N];

        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = 0;
            }

        board[3][3] = 1;
        board[4][4] = 1;
        board[3][4] = 2;
        board[4][3] = 2;
    }
    private void finalizeMove(int x, int y, ImageIcon playerIcon) {
        gui[x][y].setIcon(playerIcon);
    }

    public static void update() {
        for (int i = 0; i < gui.length; i++)
            for (int j = 0; j < gui[0].length; j++) {
                if (board[i][j] == 0)
                    gui[i][j].setIcon(null);
                if (board[i][j] == 1) {
                    gui[i][j].setIcon(wPlayer);
                }
                if (board[i][j] == 2)
                    gui[i][j].setIcon(bPlayer);
                boardFrame.getContentPane().add(gui[i][j]);
            }
    }

    public static boolean updateBoard(int col, int row, boolean change, int[][] board) {
        if (board[col][row] != 0)
            return false;

        boolean isAllowed = false;
        int opponent = noOfPlayers == 1 ? 2 : 1;

        int[][] copyOfBoard = new int[board.length][board[0].length];

        for (int i = 0; i < defaultBoardSize; i++)
            for (int j = 0; j < defaultBoardSize; j++)
                copyOfBoard[i][j] = board[i][j];

        for (int i = col + 2; i < defaultBoardSize; i++) {
            if (board[i - 1][row] != opponent)
                break;

            if (board[i][row] == noOfPlayers) {
                for (int j = col + 1; j < i; j++) {
                    if (board[j][row] == opponent) {
                        copyOfBoard[j][row] = noOfPlayers;
                        isAllowed = true;
                    }
                }
                break;
            }
        }

        for (int i = col - 2; i >= 0; i--) {
            if (board[i + 1][row] != opponent)
                break;

            if (board[i][row] == noOfPlayers) {
                for (int j = col - 1; j > i; j--) {
                    if (board[j][row] == opponent) {
                        copyOfBoard[j][row] = noOfPlayers;
                        isAllowed = true;
                    }
                }
                break;
            }
        }

        for (int i = row + 2; i < defaultBoardSize; i++) {
            if (board[col][i - 1] != opponent)
                break;

            if (board[col][i] == noOfPlayers) {
                for (int j = row + 1; j < i; j++) {
                    if (board[col][j] == opponent) {
                        copyOfBoard[col][j] = noOfPlayers;
                        isAllowed = true;
                    }
                }
                break;
            }
        }

        for (int i = row - 2; i >= 0; i--) {
            if (board[col][i + 1] != opponent)
                break;

            if (board[col][i] == noOfPlayers) {
                for (int j = row - 1; j > i; j--) {
                    if (board[col][j] == opponent) {
                        copyOfBoard[col][j] = noOfPlayers;
                        isAllowed = true;
                    }
                }
                break;
            }
        }

        // check diagonal

        for (int i = col + 2, j = row + 2; i < defaultBoardSize && j < defaultBoardSize; i++, j++) {
            if (board[i - 1][j - 1] != opponent)
                break;

            if (board[i][j] == noOfPlayers) {
                for (int k = col + 1, l = row + 1; k < i && l < j; k++, l++) {
                    if (board[k][l] == opponent) {
                        copyOfBoard[k][l] = noOfPlayers;
                        isAllowed = true;
                    }
                }
                break;
            }
        }

        for (int i = col + 2, j = row - 2; i < defaultBoardSize && j >= 0; i++, j--) {
            if (board[i - 1][j + 1] != opponent)
                break;

            if (board[i][j] == noOfPlayers) {
                for (int k = col + 1, l = row - 1; k < i && l > j; k++, l--) {
                    if (board[k][l] == opponent) {
                        copyOfBoard[k][l] = noOfPlayers;
                        isAllowed = true;
                    }
                }
                break;
            }
        }

        for (int i = col - 2, j = row + 2; i >= 0 && j < defaultBoardSize; i--, j++) {
            if (board[i + 1][j - 1] != opponent)
                break;

            if (board[i][j] == noOfPlayers) {
                for (int k = col - 1, l = row + 1; k > i && l < j; k--, l++) {
                    if (board[k][l] == opponent) {
                        copyOfBoard[k][l] = noOfPlayers;
                        isAllowed = true;
                    }
                }
                break;
            }
        }

        for (int i = col - 2, j = row - 2; i >= 0 && j >= 0; i--, j--) {
            if (board[i + 1][j + 1] != opponent)
                break;

            if (board[i][j] == noOfPlayers) {
                for (int k = col - 1, l = row - 1; k > i && l > j; k--, l--) {
                    if (board[k][l] == opponent) {
                        copyOfBoard[k][l] = noOfPlayers;
                        isAllowed = true;
                    }
                }
                break;
            }
        }

        if (change)
            for (int i = 0; i < defaultBoardSize; i++)
                for (int j = 0; j < defaultBoardSize; j++)
                    board[i][j] = new Integer(copyOfBoard[i][j]);

        return isAllowed;

    }

    public static void moveAI() throws InterruptedException {
        stopWhite = false;
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int i = 0; i < Board.board.length; i++)
            for (int j = 0; j < Board.board[0].length; j++) {
                if (Board.updateBoard(i, j, false, board))
                    moves.add(new Move(i, j));
            }

        int[] movesValues = new int[moves.size()];
        if (moves.isEmpty()) {
            stopWhite = true;
            if (stopBlack)
                stop = true;
        } else {
            Move bestMove = moves.get(0);
            int bestIndex = 0;


            for (int i = 0; i < movesValues.length; i++) {
                int[][] copyOfBoard = new int[board.length][board[0].length];

                for (int j = 0; j < board.length; j++)
                    for (int k = 0; k < board[0].length; k++)
                        copyOfBoard[j][k] = board[j][k];

                if (wStillCanMove) {
                    if (minmaxWEnabled)
                        movesValues[i] = Game.minimax(moves.get(i).col, moves.get(i).row, true, copyOfBoard, defaultWDepth, 0);
                    else
                        movesValues[i] = Game.alphabeta(moves.get(i).col, moves.get(i).row, Game.min, Game.max, true, copyOfBoard, defaultWDepth, 0);
                } else {
                    movesValues[i] = Game.getMoves(moves.get(i).col, moves.get(i).row, copyOfBoard).size();
                }

                if (movesValues[bestIndex] < movesValues[i]) {
                    bestMove = moves.get(i);
                    bestIndex = i;
                }
            }


            if (updateBoard(bestMove.col, bestMove.row, true, board)) {
                wMovesCount++;
                board[bestMove.col][bestMove.row] = 1;
                gui[bestMove.col][bestMove.row].setIcon(wPlayer);
            }
        }
    }

    public static void moveBlack() throws InterruptedException {
        stopBlack = false;
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int i = 0; i < Board.board.length; i++)
            for (int j = 0; j < Board.board[0].length; j++) {
                if (Board.updateBoard(i, j, false, board))
                    moves.add(new Move(i, j));
            }

        int[] movesValues = new int[moves.size()];
        if (moves.isEmpty()) {
            stopBlack = true;
            if (stopWhite)
                stop = true;
        } else {
            Move bestMove = moves.get(0);
            int bestIndex = 0;
            for (int i = 0; i < movesValues.length; i++) {
                int[][] copyOfBoard = new int[board.length][board[0].length];

                for (int j = 0; j < board.length; j++)
                    for (int k = 0; k < board[0].length; k++)
                        copyOfBoard[j][k] = board[j][k];

                if (bStillCanMove) {
                    if (minmaxBEnabled)
                        movesValues[i] = Game.minimax(moves.get(i).col, moves.get(i).row, true, copyOfBoard, defaultBDepth, 0);
                    else
                        movesValues[i] = Game.alphabeta(moves.get(i).col, moves.get(i).row, Game.min, Game.max, true, copyOfBoard, defaultBDepth, 0);
                } else {
                    movesValues[i] = Game.getMoves(moves.get(i).col, moves.get(i).row, copyOfBoard).size();
                }

                if (movesValues[bestIndex] < movesValues[i]) {
                    bestMove = moves.get(i);
                    bestIndex = i;
                }
            }

            if (updateBoard(bestMove.col, bestMove.row, true, board)) {
                bMovesCount++;
                board[bestMove.col][bestMove.row] = 2;
                gui[bestMove.col][bestMove.row].setIcon(bPlayer);
            }
        }
    }

    public static int[] getActualScore(int[][] board) {
        int whites = 0;
        int blacks = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 1)
                    whites++;
                if (board[i][j] == 2)
                    blacks++;
            }
        }
        return new int[]{whites, blacks};
    }

    private void setupBoardCell(int x, int y){
        gui[x][y] = new JLabel(x + " " + y);
        gui[x][y].setFont(new Font("Verdana", Font.PLAIN, 12));
        gui[x][y].setForeground(Color.GREEN);
        gui[x][y].setBounds(((2 * x + 3) * width) / 2, ((2 * y + 3) * width) / 2, width, width);
        gui[x][y].setHorizontalAlignment(SwingConstants.CENTER);
        gui[x][y].setVerticalAlignment(SwingConstants.CENTER);
    }
}
