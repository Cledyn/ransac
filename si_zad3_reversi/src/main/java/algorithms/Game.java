package algorithms;

/**
 * Created by Sandra on 2016-05-18.
 */
import board.Board;
import heuristic.MovesLeft;
import heuristic.RiskRegions;
import heuristic.Teritory;

import java.util.ArrayList;

public class Game {

    public static int min = -10;
    public static int max = 10;


    public static Integer minimax(int col, int row, boolean maxP, int[][] board, int depth, int heuristicNo) // mostPoints, mostMovesLeft, mostCorners, mostEdges, mostWaves
    {
        ArrayList<Move> moves = getMoves(col, row, board);

        if(moves.size() == 0 || depth == 0)
        {
            return evaluateBasedOnHeuristic(col, row, maxP, board, depth, heuristicNo);
        }

        if(maxP)
        {
            int best = min;
            for(Move m : moves)
            {
                int[][] copyOfBoard = new int[board.length][board[0].length];

                for(int i = 0; i < board.length; i++)
                    for(int j = 0; j < board[0].length; j++)
                        copyOfBoard[i][j] = new Integer(board[i][j]);

                int val = minimax(m.col, m.row, false, copyOfBoard, depth - 1, heuristicNo);
                best = Math.max(best, val);
            }
            return best;
        }
        else
        {
            int best = max;
            for(Move m : moves)
            {
                int[][] copyOfBoard = new int[board.length][board[0].length];

                for(int i = 0; i < board.length; i++)
                    for(int j = 0; j < board[0].length; j++)
                        copyOfBoard[i][j] = new Integer(board[i][j]);
                int val = minimax(m.col, m.row, true, copyOfBoard, depth - 1, heuristicNo);
                best = Math.min(best, val);
            }
            return best;
        }
    }

    public static Integer alphabeta(int col, int row, int alpha, int beta, boolean maxP, int[][] board, int depth, int heuristicNo)
    {
        ArrayList<Move> moves = getMoves(col, row, board);

        if(moves.size() == 0 || depth == 0)
        {
            evaluateBasedOnHeuristic(col, row, maxP, board, depth, heuristicNo);
        }

        if(maxP)
        {
            int val = min;
            for(Move m : moves)
            {
                int[][] copyOfBoard = new int[board.length][board[0].length];

                for(int i = 0; i < board.length; i++)
                    for(int j = 0; j < board[0].length; j++)
                        copyOfBoard[i][j] = new Integer(board[i][j]);
                val = Math.max(val, alphabeta(m.col, m.row, alpha, beta, false, copyOfBoard, depth - 1, heuristicNo));
                alpha = Math.max(alpha, val);
                if(beta <= alpha)
                    break;
            }
            return val;
        }
        else
        {
            int val = max;
            for(Move m : moves)
            {
                int[][] copyOfBoard = new int[board.length][board[0].length];

                for(int i = 0; i < board.length; i++)
                    for(int j = 0; j < board[0].length; j++)
                        copyOfBoard[i][j] = new Integer(board[i][j]);
                val = Math.min(val, alphabeta(m.col, m.row, alpha, beta, true, copyOfBoard, depth - 1, heuristicNo));
                beta = Math.min(beta, val);
                if(beta <= alpha)
                    break;
            }
            return val;
        }
    }

    public static ArrayList<Move> getMoves(int col, int row, int[][] board)
    {
        Board.updateBoard(col, row, true, board);
        ArrayList<Move> moves = new ArrayList<Move>();
        for(int i = 0; i < Board.board.length; i++)
            for(int j = 0; j < Board.board[0].length; j++)
            {
                if(Board.updateBoard(i, j, false, board))
                    moves.add(new Move(i, j));
            }

        return moves;
    }


    private static int evaluateBasedOnHeuristic(int col, int row, boolean maxP, int[][] board, int depth, int heuristicNo){
        int value=0;
        switch(heuristicNo){
            case 0 :
                value = Teritory.calculateFuncValue(depth, board);
                break;
            case 1:
                value = RiskRegions.riskRegions[col][row];
                break;
            case 2:
                value =  Board.getActualScore(board)[Board.noOfPlayers - 1];
                break;
            case 3:
                value = MovesLeft.calculateFuncValue(board);
                break;
        }
        return value;
    }

}


