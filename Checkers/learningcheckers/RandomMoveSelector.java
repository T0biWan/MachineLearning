package learningcheckers;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class RandomMoveSelector extends MoveSelector {
    
    private static Random rand = new Random(Calendar.getInstance().getTimeInMillis());
    

    @Override
    public Move select(List<Move> legalMoves, Board b, int player) {
        int k = rand.nextInt(legalMoves.size());
        return legalMoves.get(k);            
    }
    
    
    

}
