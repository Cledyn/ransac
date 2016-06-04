package heuristic;

/**
 * Created by Sandra on 2016-05-23.
 */
public class MovesLeft implements Heuristic {

    public static int calculateFuncValue( int [][] board){

        int sum=0;
        for(int i=0; i<board.length;i++){
            for(int j=0; j<board[0].length;j++){
                if(board[i][j]==0){
                   sum++;
                }
            }
        }
        return sum;
    }

    @Override
    public String getName() {
        return "Moves left";
    }


}
