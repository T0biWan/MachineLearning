package learningcheckers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import simplevisuals.ViewPort;

public class Board {
    
    
    private static final int SIZE = 8;
    private static final int PLAYER1 = 1; // do not change this constant!
    private static final int PLAYER2 = 2; // do not change this constant!
    private static final int EMPTY_FIELD = 0; 
    private static final int PLAYER1_NORMAL_PIECE = PLAYER1;
    private static final int PLAYER2_NORMAL_PIECE = PLAYER2;
    private static final int PLAYER1_KING_PIECE = PLAYER1 + 2;
    private static final int PLAYER2_KING_PIECE = PLAYER2 + 2;
    private static final Color WHITE = Color.WHITE;  
    private static final Color BLACK = Color.BLACK;  
    private static final Color TEXTCOLOR = Color.GRAY;  
    private static final Color PLAYER1_COLOR = Color.CYAN;  
    private static final Color PLAYER2_COLOR = Color.ORANGE;  
    private static final Color PLAYER1_KING_COLOR = Color.BLUE;  
    private static final Color PLAYER2_KING_COLOR = Color.RED;  
    private static final Color[] cols = {BLACK, WHITE};
    private static final Color[] playerCols = {PLAYER1_COLOR, PLAYER2_COLOR, PLAYER1_KING_COLOR, PLAYER2_KING_COLOR};
    private static final boolean SHOW_BOARDINFO = true;
    private static final int VPSize = 1000;
    public static final boolean reuseViewPortForDifferentGames = true;
    private static final ViewPort staticWindow = (reuseViewPortForDifferentGames ? new ViewPort("Dame", VPSize, VPSize, false) : null);
    // each piece at every position on the board can be mapped to a unique identifier (which is a number) 
    // by multiplying respective line-/column-/ and pieceUID.
    // all of the following constants are used to compute a field hash rapidly such that 
    // it can be updated for every move in a fast way
    private static final int[] columnUIDs = getPrimes(1, SIZE); 
    private static final int[] lineUIDs = getPrimes(SIZE + 1, 2 * SIZE);
    private static final int[] pUID = getPrimes(2 * SIZE + 1, 2* SIZE + 4);
    private static final int[] pieceUIDs = {0, pUID[0], pUID[1], pUID[2], pUID[3]};
    private static final long fieldModule = 2147483647L;
    

    // Fields
    private int[][] content;
    private int nextPlayer;
    private long boardHash;
    private int numberOfPlayer1Pieces;
    private int numberOfPlayer2Pieces;
    private int numberOfPlayer1Kings;
    private int numberOfPlayer2Kings;
    private ViewPort window;
    
    
    
    
    
    public Board() {
        this((reuseViewPortForDifferentGames ? staticWindow : new ViewPort("Dame", VPSize, VPSize, false)));
    }
    
    
    public Board(ViewPort old) {
        content = new int[SIZE][SIZE];
        boardHash = 0;
        nextPlayer = getStartPlayer();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                setContent(i, j, EMPTY_FIELD);
            }
        }
        for (int i = 0; i < SIZE/2; i++) {
            for (int j = 0; j < SIZE/2 - 1; j++) {
                setContent(i*2 + j%2, j, PLAYER1_NORMAL_PIECE);
                setContent(i*2 + (j + 1)%2, SIZE - j - 1, PLAYER2_NORMAL_PIECE);
            }
        }
        numberOfPlayer1Pieces = SIZE/2 * (SIZE/2 - 1);
        numberOfPlayer2Pieces = SIZE/2 * (SIZE/2 - 1);
        numberOfPlayer1Kings = 0;
        numberOfPlayer2Kings = 0;        
        window = old;
    }
    
    
    
    
    public static int getSize() {
        return SIZE;
    }
    
    
    
    private Board(boolean cloningMarker) {
        content = new int[SIZE][SIZE];        
    }
    
    
    
    public Color getColor(int x, int y) {
        return getColor((y + x) % 2);
    }
    
    
    
    public Color getColor(int c) {
        return cols[c];
    }
    
    
    
    public int getContent(int x, int y) {
        return content[x][y];
    }
    
    
    public static long getPositionHash(int x, int y) {
        return lineUIDs[y] * columnUIDs[x];
    }
    
    public void setContent(int x, int y, int col) {
        long coordUID = getPositionHash(x, y);
        int oldCol = content[x][y]; 
        long oldPieceID = pieceUIDs[oldCol] * coordUID;
        long pieceID = pieceUIDs[col] * coordUID;
        boardHash = boardHash - oldPieceID;
        if (boardHash < 0) boardHash = boardHash + fieldModule;
        boardHash = boardHash + pieceID;
        if (boardHash >= fieldModule) boardHash = boardHash - fieldModule;
        
        content[x][y] = col;
    }
    
    
    public long getBoardHash() {
        return boardHash;
    }
    
    
    public void drawBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                drawField(i, j);
                drawPiece(i, j);                
            }
        }        
    }
    
    
    
    public void show() {        
        window.copyBackgroundBuffer();
    }
    
    
    public void closeWindow() {
        window.close();
    }
    
    
    private void drawField(int x, int y) {
        Rectangle r = getFieldCoords(x, y);
        window.drawBlock(r, getColor(x, y));
        if (SHOW_BOARDINFO)
            window.drawString("(" + x + ", " + y + ")", r.x + getFieldSize()/4, r.y + getFieldSize()*7/8, getFieldSize()/2, getFieldSize()/8, TEXTCOLOR);
    }
    
    
    
    private void drawPiece(int x, int y) {
        int col = content[x][y];
        if (col != 0) {
            Rectangle r = getFieldCoords(x, y);
            window.drawDisk(r.x+getFieldSize()/2, r.y+ getFieldSize()/2, 4*getFieldSize()/11, playerCols[col-1]);
            if (SHOW_BOARDINFO)
                window.drawString(""  + getPlayerFor(col) + "", r.x + getFieldSize()*7/16, r.y + getFieldSize()*7/16, getFieldSize()/8, getFieldSize()/8, TEXTCOLOR);
            
        }
    }

    
    
    private int getFieldSize() {
        return VPSize/SIZE;
    }
    
    

    private Rectangle getFieldCoords(int x, int y) {
        final int fieldSize = getFieldSize();
        return new Rectangle(new Point(x*fieldSize, VPSize - (y+1)*fieldSize), new Dimension(fieldSize, fieldSize));
    }
    
    
    
    public int getPlayerNormalColor(int player) {
        if (player == PLAYER1) return PLAYER1_NORMAL_PIECE;
        if (player == PLAYER2) return PLAYER2_NORMAL_PIECE;
        throw new RuntimeException("Unknown Player: " + player);        
    }
    
    
    
    public int getPlayerKingColor(int player) {
        if (player == PLAYER1) return PLAYER1_KING_PIECE;
        if (player == PLAYER2) return PLAYER2_KING_PIECE;
        throw new RuntimeException("Unknown Player: " + player);        
    }
    
    
    
    public int getPlayerFor(int color) {
        if (color == PLAYER1_NORMAL_PIECE) return PLAYER1;
        if (color == PLAYER1_KING_PIECE) return PLAYER1;
        if (color == PLAYER2_NORMAL_PIECE) return PLAYER2;
        if (color == PLAYER2_KING_PIECE) return PLAYER2;
        throw new RuntimeException("Color " + color + " is not a valid player color.");
    }
    

    
    public int getNumberOfPiecesFor(int player) {
        if (player == PLAYER1) return numberOfPlayer1Pieces;
        else return numberOfPlayer2Pieces;
    }
    
    
    
    public int getNumberOfKingsFor(int player) {
        if (player == PLAYER1) return numberOfPlayer1Kings;
        else return numberOfPlayer2Kings;
    }
    
    
    
    public int getNumberOfPlayer1Pieces() {
        return numberOfPlayer1Pieces;
    }

    

    public int getNumberOfPlayer2Pieces() {
        return numberOfPlayer2Pieces;
    }
    
    
    public int getNumberOfNormalPiecesInRow(int row, int player) {
        int offset = row % 2;
        int count = 0;
        int col = getPlayerNormalColor(player);
        if (content[offset][row] == col) count++; 
        if (content[offset + 2][row] == col) count++; 
        if (content[offset + 4][row] == col) count++; 
        if (content[offset + 6][row] == col) count++;
        return count;
    }


    
    public boolean isPieceOf(int pieceColor, int player) {
        return pieceColor == getPlayerNormalColor(player) || pieceColor == getPlayerKingColor(player);
    }
    
    
    
    public boolean isKingOf(int pieceColor, int player) {
        return pieceColor == getPlayerKingColor(player);
    }
    

    public boolean isKing(int pieceColor) {
        return pieceColor == getPlayerKingColor(PLAYER1) || pieceColor == getPlayerKingColor(PLAYER2);
    }
    
    
    private boolean coordWithinBoard(int x, int y) {
        return x >= 0 && x < SIZE &&  y >= 0 && y < SIZE;
    }
    
    
    
    private boolean coordIsBlackField(int x, int y) {
        return coordWithinBoard(x, y) && getColor(x, y) == BLACK;
    }
    
    
    
    public int getOtherPlayer(int player) {
        if (player == PLAYER1) return PLAYER2;
        else return PLAYER1;
    }
    
    
    
    public boolean isThreatened(int player, int x, int y) {
        //System.out.println("x: " + x + ", y: " + y + ", threat for player " + player + ": " + numberOfEnemiesThreatening(player, x, y));
        return numberOfEnemiesThreatening(player, x, y) > 0;
    }
    
    
    
    public int numberOfEnemiesThreatening(int player, int x, int y) {  
        int col = content[x][y];
        if (col == EMPTY_FIELD) return 0;
        int p = getPlayerFor(col);
        if (p != player) return 0;
        return numberOfEnemiesThreateningFromDirection(player, x, y, 1, 1) +
                numberOfEnemiesThreateningFromDirection(player, x, y, 1, -1) +
                numberOfEnemiesThreateningFromDirection(player, x, y, -1, 1) +
                numberOfEnemiesThreateningFromDirection(player, x, y, -1, -1);
    }
    
    
    
    private boolean inIntervall(int value, int left, int right) {
        return value >= left && value <= right;
    }
    
    
    
    private boolean allValuesInIntervall(int left, int right, int ... values) {
        for (int v : values) if (!inIntervall(v, left, right)) return false;
        return true;
    }
    
    
    
    private int numberOfEnemiesThreateningFromDirection(int player, int x, int y, int xDir, int yDir) {
        if (!allValuesInIntervall(0, SIZE-1, x + xDir, y + yDir)) return 0;
        int count = 0;
        int other = getOtherPlayer(player);
        
        if (allValuesInIntervall(0, SIZE - 1, x + (-1 * xDir), y + (-1 * yDir))  && 
                content[x + xDir * -1][y + yDir * -1] == EMPTY_FIELD) {
            int field = 0;
            for (int i = xDir, j = yDir;  
                    allValuesInIntervall(0, SIZE - 1, x + i, y + j);  
                    i = i + xDir, j = j + yDir) {
                field = content[x+i][y+j];
                if (isPieceOf(field, other)) {
                    if (isKingOf(field, other)) count++;
                    else {
                        if (j == 1 && other == PLAYER2) count++;
                        if (j == -1 && other == PLAYER1) count++;
                    }
                }
                if (field != EMPTY_FIELD) break;
            }
        }
        return count;
    }
    
    
    public boolean isStuck(int player, int x, int y) {
        int field = content[x][y];
        if (field == EMPTY_FIELD || player != getPlayerFor(field)) return false;
        int other = getOtherPlayer(player);
        if (isKing(field)) {
            int[] dirs = {1, -1};
            for (int j : dirs) {
                for (int k : dirs) {
                    boolean enemyInDirFound = false;
                    for (int xk = x+j, yk = y+k; xk >= 0 && yk >= 0 && xk < SIZE && yk < SIZE; xk = xk + j, yk = yk + k) {
                        int searchField = content[xk][yk]; 
                        if (searchField == EMPTY_FIELD) {
                            return false;
                        } else if (isPieceOf(searchField, other)) {
                            if (enemyInDirFound) break;
                            else enemyInDirFound = true;
                        } else break;
                    }
                }
            }
        } else {
            int dir = 1;
            if (isPieceOf(field, PLAYER2)) dir = -1;
            if (x < SIZE - 1 && content[x + 1][y + dir] == EMPTY_FIELD) return false;
            if (x > 0 && content[x - 1][y + dir] == EMPTY_FIELD) return false;
            
            if (y + 2*dir < 0 || y + 2*dir >= SIZE) return true;
            
            if (x < SIZE - 2 && content[x + 2][y + 2*dir] == EMPTY_FIELD) {
                if (isPieceOf(content[x + 1][y + dir], other)) return false; 
            }
            
            if (x > 2 && content[x - 2][y + 2*dir] == EMPTY_FIELD) {
                if (isPieceOf(content[x - 1][y + dir], other)) return false; 
            }
        }
        return true;
    }
    
    
    public ViewPort getViewPort() {
        return window;
    }
    
    
    
    public boolean isLegalMoveFor(Move m, int player) {
        // move stays on the black fields of the board
        if (!coordIsBlackField(m.getFromX(), m.getFromY())) return false;
        if (!coordIsBlackField(m.getToX(), m.getToY())) return false;
        // target field of move is empty
        if (getContent(m.getToX(), m.getToY()) != EMPTY_FIELD) return false;
        // source field contains a piece of the right player
        if (!isPieceOf(getContent(m.getFromX(), m.getFromY()), player)) return false;
        
        int distX = m.getToX() - m.getFromX();
        int distY = m.getToY() - m.getFromY();
        // move is on a diagonal
        if (distX * distX != distY * distY) return false;
        
        if (getContent(m.getFromX(), m.getFromY()) == getPlayerNormalColor(player)) { // normal piece
            if (distX * distX == 1) {
                if (player == PLAYER1 && distY > 0) return true;
                if (player == PLAYER2 && distY < 0) return true;
            }
            if (distX * distX == 4) {
                int middleContent = getContent(m.getFromX() + distX/2, m.getFromY() + distY/2);
                if (player == PLAYER1 && distY > 0 && isPieceOf(middleContent, PLAYER2)) return true;
                if (player == PLAYER2 && distY < 0 && isPieceOf(middleContent, PLAYER1)) return true;
            }
            
        } else if (getContent(m.getFromX(), m.getFromY()) == getPlayerKingColor(player)) { // king piece
            int count = 0;
            int xDirection = 1;
            int yDirection = 1;
            if (m.getFromX() > m.getToX()) xDirection = -1;
            if (m.getFromY() > m.getToY()) yDirection = -1;
            for (int i = m.getFromX() + xDirection, j = m.getFromY()+ yDirection; i != m.getToX(); i = i + xDirection, j = j + yDirection) {
                if (isPieceOf(getContent(i, j), player)) return false;
                if (isPieceOf(getContent(i, j), getOtherPlayer(player))) count++;
            }
            return count <= 1;
        } 
        return false;
    }
    
    
    
    private void addToListIfLegal(int fromX, int fromY, int toX, int toY, int player, List<Move> li) {
        Move m = new Move(fromX,fromY, toX, toY);
        if (isLegalMoveFor(m, player)) li.add(m);        
    }
    
    
    
    public List<Move> getAllLegalMovesForPosition(int x, int y, int player) {
        LinkedList<Move> li = new LinkedList<Move>();
        int cont = getContent(x, y);
        if (isPieceOf(cont, player)) {
            if (cont == getPlayerNormalColor(player)) {
                int yDir = 1;
                if (player == PLAYER2) yDir = -1;
                for (int i = 1; i <= 2; i++) { 
                    addToListIfLegal(x, y, x+i, y+yDir*i,  player, li);
                    addToListIfLegal(x, y, x+i, y-yDir*i,  player, li);
                    addToListIfLegal(x, y, x-i, y+yDir*i,  player, li);
                    addToListIfLegal(x, y, x-i, y-yDir*i,  player, li);
                }
            } else {
                for (int i = x-1, j = y-1; i>=0 && j >= 0; i--, j--) addToListIfLegal(x, y, i, j, player, li);
                for (int i = x-1, j = y+1; i>=0 && j < SIZE; i--, j++) addToListIfLegal(x, y, i, j, player, li);
                for (int i = x+1, j = y-1; i < SIZE && j >= 0; i++, j--) addToListIfLegal(x, y, i, j, player, li);
                for (int i = x+1, j = y+1; i < SIZE && j < SIZE; i++, j++) addToListIfLegal(x, y, i, j, player, li);
            }
        }
        return li;
    }
    
    
    
    public List<Move> getAllLegalMoves(int player) {
        LinkedList<Move> li = new LinkedList<Move>();
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                li.addAll(getAllLegalMovesForPosition(x, y, player));
            }            
        }
        return li;
    }
    
    
    
    public static int getStartPlayer() {
        return PLAYER1;
    }
    
    
    
    private int getOppositeSideNumberFor(int player) {
        if (player == PLAYER1) return SIZE - 1;
        else return 0;
    }
    
    
    
    /**
     * Applies the given move. Expects a legal move.
     * Returns a new board where the given move is applied.
     * The input-board remains unchanged.
     */
    public Board apply(Move m) {
        Board b = (Board)clone();
        int piece = getContent(m.getFromX(), m.getFromY());        
        int player = getPlayerFor(piece);
        if (player != nextPlayer) throw new RuntimeException("Move " + m + " is not applicable to board \n" + this + "since it's the turn of player " + nextPlayer + ".");
        b.setContent(m.getFromX(), m.getFromY(), EMPTY_FIELD);
        if (m.getToY() == getOppositeSideNumberFor(player) && !isKingOf(piece, player)) {
            piece = getPlayerKingColor(player);
            if (player == PLAYER1) b.numberOfPlayer1Kings++;
            else b.numberOfPlayer2Kings++;
        }
        b.setContent(m.getToX(), m.getToY(), piece);
        int xDirection = 1;
        int yDirection = 1;
        if (m.getFromX() > m.getToX()) xDirection = -1;
        if (m.getFromY() > m.getToY()) yDirection = -1;
        for (int i = m.getFromX() + xDirection, j = m.getFromY()+ yDirection; i != m.getToX(); i = i + xDirection, j = j + yDirection) {
            int pieceBetween = content[i][j];  
            if (isPieceOf(pieceBetween ,getOtherPlayer(player))) {                
                if (player == PLAYER1) {
                    b.numberOfPlayer2Pieces--;
                    if (isKingOf(pieceBetween, getOtherPlayer(player))) b.numberOfPlayer2Kings--;
                } else {
                    b.numberOfPlayer1Pieces--;
                    if (isKingOf(pieceBetween, player)) b.numberOfPlayer1Kings--;
                }
                b.setContent(i, j, EMPTY_FIELD);
                break;
            }
        }
        b.nextPlayer = getOtherPlayer(player);
        return b;
    }
    
    
    public int getNextPlayer() {
        return nextPlayer;
    }
    
    
    
    public boolean boardHasFinalState() {
        return numberOfPlayer1Pieces == 0 || numberOfPlayer2Pieces == 0;
    }
    
    
    
    public Object clone() {
        Board b = new Board(true);
        for (int x = 0; x < SIZE; x++) {
            System.arraycopy(content[x], 0, b.content[x], 0, content[x].length);
        }        
        b.boardHash = boardHash;
        b.numberOfPlayer1Pieces = numberOfPlayer1Pieces;
        b.numberOfPlayer2Pieces = numberOfPlayer2Pieces;
        b.numberOfPlayer1Kings = numberOfPlayer1Kings;
        b.numberOfPlayer2Kings = numberOfPlayer2Kings;
        b.nextPlayer = nextPlayer;
        b.window = window; 
        return b;
    }
    
    
    
    /**
     *  returns all primes between from and to, where 2 is the first prime, 3 is the second etc.
     *  example: getPrimes(3, 6) returns {5, 7, 11, 13}
     */
    private static int[] getPrimes(int from, int to) {
        LinkedList<Integer> li = new LinkedList<>();
        li.add(2);
        int[] a = new int[to - from + 1];
        int i = 0;
        int x = 3;
        int count = 1;
        if (from == 1) {
            a[0] = 2;
            i = 1;
        }
        while (count < to) {
            boolean foundDivisor = false;
            for (int j : li) {
                if (x % j == 0) {
                    foundDivisor = true;
                    break;
                }
                if (j*j >= x) break;
            }
            if (!foundDivisor) {
               li.add(x);
               count++;
               if (count >= from) {
                   a[i] = x;
                   i++;
               }
            }
            x = x + 2;
        }
        return a;
    }
    
    
    private String repeat(int times, String s) {
        String t = "";
        for (int i = 0; i < times; i++) t = t + s;
        return t;
    }
    
    public String toString() {
        String s = "";
        s = s + " |-----" + repeat(SIZE-2, "-----") + "----|\n";
        for (int y =0; y < SIZE; y++) {
            s = s + (SIZE - y - 1) + "| ";
            for (int x = 0; x < SIZE; x++) {
                int field = content[x][SIZE - y - 1];
                if (field != EMPTY_FIELD) {
                    if (isKingOf(field, PLAYER1) || isKingOf(field, PLAYER2))
                        s = s + "K";
                    else 
                        s = s + "N";
                    s = s + getPlayerFor(field) + " | ";
                } else s = s + "   | ";
            } 
            s = s.substring(0, s.length() - 1) + "\n";
            s = s + " |" + repeat(SIZE * 5 - 2, "-") + "-|\n";
        }
        s = s + "   ";
        for (int x = 0; x < SIZE; x++) {
            s = s + " " + x + "   "; 
        } 
        
        s = s + "\nNext player: " + nextPlayer + "\n";
        return s;
    }
    
    
    public static void main(String[] args) {
        Board b = new Board();
        b.setContent(4, 4, PLAYER1_KING_PIECE);
        b.setContent(2, 2, PLAYER2_KING_PIECE);
        b.setContent(6, 6, PLAYER2_KING_PIECE);
        b.setContent(2, 6, PLAYER2_KING_PIECE);
        b.setContent(6, 2, PLAYER2_KING_PIECE);        
        b.drawBoard();
        b.show();
        System.out.println(b.isStuck(PLAYER1, 0, 0));
        
    }
    
    
}
