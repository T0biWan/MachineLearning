package learningcheckers;

import java.util.LinkedList;
import java.util.List;

public class Game {
        
    private static final int MAX_NUMBER_OF_ROUNDS = 200;
    
    private LinkedList<Board> trace;
    private MoveSelector[] mSelect = new MoveSelector[2];
    
    private boolean showVisuals;
    private boolean waitForKeyStroke;
    
    
    public Game(MoveSelector ms1, MoveSelector ms2, boolean visuals) {        
        trace = new LinkedList<Board>();
        Board b = new Board();
        trace.add(b);
        mSelect[0] = ms1;
        mSelect[1] = ms2;
        showVisuals = visuals;
        waitForKeyStroke = true;
        if (visuals) {
            b.drawBoard();
            b.show();            
        }
    }
    
    
    
    public void setVisuals(boolean on) {
        showVisuals = on;
    }
    
    
    public void setKeyStrokeWaiting(boolean on) {
        waitForKeyStroke = on;
    }
    
    
    public void closeGameWindow() {
        if (trace.isEmpty()) return;
        Board b = trace.get(0);
        b.closeWindow();
    }
    
    
    /**
     * returns the number of the winning player or -1 if a stalemate (= patt) was detected.
     */
    public int run(boolean visualsOn) {
        mSelect[0].resetBoardMoveMemory();
        mSelect[1].resetBoardMoveMemory();
        setVisuals(visualsOn);
        Board b = trace.getLast();               
        int actPlayer = Board.getStartPlayer();
        int playerTurn = 0;
        int roundNr = 1;
        while (!b.boardHasFinalState()) {

            List<Move> li = b.getAllLegalMoves(actPlayer);
            if (li.isEmpty()) {
                List<Move> li1 = b.getAllLegalMoves(b.getOtherPlayer(actPlayer));
                if (li1.isEmpty()) return -1;
                return b.getOtherPlayer(actPlayer);
            }
            
            Move m = mSelect[playerTurn].select(li, b, actPlayer);
            
            if (m == null) return -1;            
            Board b1 = b.apply(m);
            
            roundNr++;
            if (roundNr >= MAX_NUMBER_OF_ROUNDS) return -1;
            if (showVisuals) {
                b1.drawBoard();
                b1.show();
                /*
                System.out.println(b1.getNumberOfPlayer1Pieces() + " : " + b1.getNumberOfPlayer2Pieces());
                System.out.println(roundNr);
                */
                if (waitForKeyStroke) {
                    while (!b.getViewPort().hasNextTypedChar())  /* do nothing */;
                    b.getViewPort().getNextTypedChar();
                }
            }
            
            playerTurn = (playerTurn + 1) % 2;
            actPlayer = b1.getOtherPlayer(actPlayer);
            trace.addLast(b1);
            b = b1;
        }
        return b.getOtherPlayer(b.getNextPlayer());
    }
    
    
    public List<Board> getHistory() {
        return trace;
    }
    
    
    public static void main(String[] args) {
        int count = 0;
        for (int round = 0; round < 1000; round++) {
            Game g = new Game(new LinearSelector(8, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0,0,0,0,0,0,0), 
                                  new LinearSelector(8, 2.2792686377271774, 6.24143670691181, 2.0119805302918037, 4.932494642868991, 5.998905859081998, 0.2238866893958605, 1.3105324737855193, 0.09206237098973476, 0.08762426611315538, 0, 0, 0, 0, 0, 0), false);
            if (g.run(false) == 1) count++;
            g.closeGameWindow();
            System.out.println(round);
        }
        System.out.println("Winner: " + count);
    }
    

}
