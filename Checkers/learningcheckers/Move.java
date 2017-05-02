package learningcheckers;

public class Move {
    
    private int fromX, fromY, toX, toY;
    
    
    

    public Move(int fromX, int fromY, int toX, int toY) {
        super();
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public int getFromX() {
        return fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }
    
    
    public String toString() {
        return "(" + fromX + ", " + fromY + ") --> (" + toX + ", " + toY + ")";
    }
    
    
    public long getMoveHash() {
        return Board.getPositionHash(fromX, fromY) * Board.getPositionHash(toX, toY);
    }
    
    

}
