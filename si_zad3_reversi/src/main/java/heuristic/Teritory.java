package heuristic;

/**
 * Created by Sandra on 2016-05-23.
 */
public class Teritory implements Heuristic{



    public static int calculateFuncValue(int depth, int [][] board){

        int sum=0;
        for(int i=0; i<board.length;i++){
            for(int j=0; j<board[0].length;j++){
                if(board[i][j]==2){
                    sum+=(-1*depth);
                }
                else
                    sum+=(depth*board[i][j]);
            }
        }
        return sum;
    }

    @Override
    public String getName() {
        return "Teritory";
    }
}
