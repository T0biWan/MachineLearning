package learningcheckers;

import java.util.HashSet;
import java.util.List;

public abstract class MoveSelector {

    
    
    public abstract Move select(List<Move> legalMoves, Board b, int player);

    
    
    
    
    // A selector is not allowed to apply a move more than once in a game
    // therefore every selector has to keep track on the moves done so far
    // also, the memory of a selector should be reset using resetBoardMoveMemory
    // each time a new game is started
    private HashSet<Long> boardMoveMemory;

    
    public MoveSelector() {
        resetBoardMoveMemory();
    }

    
    public void resetBoardMoveMemory() {
        boardMoveMemory = new HashSet<>();
    }
    
    
    private long boardMoveHash(Board b, Move m) {
        return b.getBoardHash() * m.getMoveHash();
    }

    
    public boolean moveMemoryContains(Board b, Move m) {
        return boardMoveMemory.contains(boardMoveHash(b, m));
    }
    
    
    public void registerMoveOnBoard(Board b, Move m) {
        boardMoveMemory.add(boardMoveHash(b, m));
    }
    
    
    

}
