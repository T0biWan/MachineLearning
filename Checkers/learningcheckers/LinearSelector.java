package learningcheckers;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.IntFunction;

public class LinearSelector extends MoveSelector {

    private double factBasis;
    private double factNrPiecesSelf;
    private double factNrPiecesOther;
    private double factNrKingsSelf;
    private double factNrKingsOther;
    private double factNrThreatenedPiecesSelf;
    private double factNrThreatenedPiecesOther;
    private double factNrStuckSelfPieces;
    private double factNrStuckOtherPieces;
    // these factors are for the own pieces of a player
    // where the factor at index 0 is for the row that is closest to 
    // the own player side (so the indices of the rows concur with the ones of the 
    // board for the start player but NOT for the other player)
    private double factNrOfNormalPiecesInRow[];   
    private static Random rand = new Random(Calendar.getInstance().getTimeInMillis());

    
    
    
    
    
    public LinearSelector(int boardSize) {
        factBasis = 0;
        factNrPiecesSelf = 0;
        factNrPiecesOther = 0;
        factNrKingsSelf = 0;
        factNrKingsOther = 0;
        factNrThreatenedPiecesSelf = 0;
        factNrThreatenedPiecesOther = 0;
        factNrStuckSelfPieces = 0;
        factNrStuckOtherPieces = 0;
        factNrOfNormalPiecesInRow = new double[boardSize-1];
    }
    
    
    
    
    public LinearSelector(int boardSize, double factBasis, double factNrPiecesSelf,
            double factNrPiecesOther, double factNrKingsSelf, double factNrKingsOther, 
            double factNrThreatenedPiecesSelf, double factNrThreatenedPiecesOther, 
            double factNrStuckSelfPieces, double factNrStuckOtherPieces,
            double ... rowFact) {
        super();
        this.factBasis = factBasis;
        this.factNrPiecesSelf = factNrPiecesSelf;
        this.factNrPiecesOther = factNrPiecesOther;
        this.factNrKingsSelf = factNrKingsSelf;
        this.factNrKingsOther = factNrKingsOther;
        this.factNrThreatenedPiecesSelf = factNrThreatenedPiecesSelf;
        this.factNrThreatenedPiecesOther = factNrThreatenedPiecesOther;
        this.factNrStuckSelfPieces = factNrStuckSelfPieces;
        this.factNrStuckOtherPieces = factNrStuckOtherPieces;
        factNrOfNormalPiecesInRow = new double[boardSize-1];
        for (int i = 0; i < rowFact.length; i++) 
               factNrOfNormalPiecesInRow[i] = rowFact[i];
    }




    @Override
    public Move select(List<Move> legalMoves, Board b, int player) {
        Move best = null;
        TreeMap<Double, Move> evalMap = new TreeMap<>();
        for (Move candidate: legalMoves) {
            Board b1 = b.apply(candidate);
            double score = evaluate(b1, player);
            while (evalMap.containsKey(score)) {
                int before = b.getNumberOfPiecesFor(b.getOtherPlayer(player));
                int after = b1.getNumberOfPiecesFor(b.getOtherPlayer(player));
                if (after < before) {
                    score = score + 0.000001 + score/100;
                }
                else score = score + score/100 * (rand.nextDouble() - 0.5) + (rand.nextDouble() - 0.5) * 0.0000000000001;
            }
            evalMap.put(score, candidate);
        }
        for (Double score : evalMap.descendingKeySet()) {
            Move candidate = evalMap.get(score);
            if (!moveMemoryContains(b, candidate)) {
                best = candidate;
                break;
            }
        }
        if (best != null) registerMoveOnBoard(b, best);
        return best;
    }
    
    
    
    
    
    public double evaluate(Board b, int player) {
        if (b.boardHasFinalState()) {
            if (b.getNumberOfPiecesFor(player) > 0) return 100;
            else return -100;
        } else {
            double rowFact = 0;
            if (player == Board.getStartPlayer()) 
                for (int i = 0; i < factNrOfNormalPiecesInRow.length; i++)                 
                    rowFact = rowFact + b.getNumberOfNormalPiecesInRow(i, player) * factNrOfNormalPiecesInRow[i];
            else 
                for (int i = 0; i < factNrOfNormalPiecesInRow.length; i++)                 
                    rowFact = rowFact + b.getNumberOfNormalPiecesInRow(Board.getSize() - i - 1, player) * factNrOfNormalPiecesInRow[i];
            return rowFact + factBasis + 
                factNrPiecesSelf * getSelfPieces(b, player) + 
                factNrPiecesOther * getOtherPieces(b, player) +
                factNrKingsSelf * getSelfKings(b, player) +
                factNrKingsOther * getOtherKings(b, player) + 
                factNrThreatenedPiecesSelf * piecesThreatened(b, player) +
                factNrThreatenedPiecesOther * piecesThreatened(b, b.getOtherPlayer(player)) + 
                factNrStuckSelfPieces * piecesStuck(b, player) +
                factNrStuckOtherPieces * piecesStuck(b, b.getOtherPlayer(player));
        }
    }
    
    
    
    private int getSelfPieces(Board b, int player) {
        return b.getNumberOfPiecesFor(player);
    }
    
    
    public int piecesStuck(Board b, int player) {
        int s = Board.getSize() - 1;
        return sum(0, s, x -> sum(0, s, y ->  (b.isStuck(player, x, y) ? 1 : 0)));
    }
    
    
    public int piecesThreatened(Board b, int player) {
        int s = Board.getSize() - 1;
        return sum(0, s, x -> sum(0, s, y ->  (b.isThreatened(player, x, y) ? 1 : 0)));
    }
    
    
    private int sum(int from, int to, IntFunction<Integer> f) {
        int k = 0;
        for (int i = from; i <= to; i++) k = k + f.apply(i);
        return k;
    }
    
    
    private int getOtherPieces(Board b, int player) {
        return b.getNumberOfPiecesFor(b.getOtherPlayer(player));
    }
    
    private int getSelfKings(Board b, int player) {
        return b.getNumberOfKingsFor(player);
    }

    private int getOtherKings(Board b, int player) {
        return b.getNumberOfKingsFor(b.getOtherPlayer(player));
    }


    public double getFactBasis() {
        return factBasis;
    }


    public void setFactBasis(double factBasis) {
        this.factBasis = factBasis;
    }


    public double getFactNrPiecesSelf() {
        return factNrPiecesSelf;
    }


    public void setFactNrPiecesSelf(double factNrPiecesSelf) {
        this.factNrPiecesSelf = factNrPiecesSelf;
    }


    public double getFactNrPiecesOther() {
        return factNrPiecesOther;
    }


    public void setFactNrPiecesOther(double factNrPiecesOther) {
        this.factNrPiecesOther = factNrPiecesOther;
    }


    public double getFactNrKingsSelf() {
        return factNrKingsSelf;
    }


    public void setFactNrKingsSelf(double factNrKingsSelf) {
        this.factNrKingsSelf = factNrKingsSelf;
    }


    public double getFactNrKingsOther() {
        return factNrKingsOther;
    }


    public void setFactNrKingsOther(double factNrKingsOther) {
        this.factNrKingsOther = factNrKingsOther;
    }


    public double getFactNrThreatenedPiecesSelf() {
        return factNrThreatenedPiecesSelf;
    }


    public void setFactNrThreatenedPiecesSelf(double factNrThreatenedPiecesSelf) {
        this.factNrThreatenedPiecesSelf = factNrThreatenedPiecesSelf;
    }


    public double getFactNrThreatenedPiecesOther() {
        return factNrThreatenedPiecesOther;
    }


    public void setFactNrThreatenedPiecesOther(double factNrThreatenedPiecesOther) {
        this.factNrThreatenedPiecesOther = factNrThreatenedPiecesOther;
    }

    
    
    
    public double getFactNrStuckSelfPieces() {
        return factNrStuckSelfPieces;
    }




    public void setFactNrStuckSelfPieces(double factNrStuckSelfPieces) {
        this.factNrStuckSelfPieces = factNrStuckSelfPieces;
    }




    public double getFactNrStuckOtherPieces() {
        return factNrStuckOtherPieces;
    }




    public void setFactNrStuckOtherPieces(double factNrStuckOtherPieces) {
        this.factNrStuckOtherPieces = factNrStuckOtherPieces;
    }




    public double getFactNrOfNormalPiecesInRow(int row) {
        return factNrOfNormalPiecesInRow[row];
    }
    
    public void setFactNrOfNormalPiecesInRow(int row, double v) {
        factNrOfNormalPiecesInRow[row] = v;
    }
    
    
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < factNrOfNormalPiecesInRow.length; i++) s = s + factNrOfNormalPiecesInRow[i] + ", ";
        s = s.substring(0, s.length() - 2);
        return  factBasis + 
                ", " + factNrPiecesSelf + ", " + factNrPiecesOther + 
                ", " + factNrKingsSelf + ", " + factNrKingsOther + 
                ", " + factNrThreatenedPiecesSelf + ", " +  factNrThreatenedPiecesOther + ", " + s + "\n" +
                ", " + factNrStuckSelfPieces + ", " +  factNrStuckOtherPieces + ", " + s + "\n" +
                "Basis: " + factBasis + 
                ", self pieces: " + factNrPiecesSelf + ", other pieces: " + factNrPiecesOther + 
                ", self kings: " + factNrKingsSelf + ", other kings: " + factNrKingsOther + 
                ", self threatened: " + factNrThreatenedPiecesSelf + ", other threatened: " +  factNrThreatenedPiecesOther + 
                ", self stuck: " + factNrStuckSelfPieces + ", other stuck: " +  factNrStuckOtherPieces + 
                ", rowfacts: " + s;
    }
    
    
    @Override
    public Object clone() {
        LinearSelector copy = new LinearSelector(factNrOfNormalPiecesInRow.length + 1);
        copy.factBasis = factBasis;
        copy.factNrKingsOther = factNrKingsOther;
        copy.factNrKingsSelf = factNrKingsSelf;
        copy.factNrPiecesOther = factNrPiecesOther;
        copy.factNrPiecesSelf = factNrPiecesSelf;
        copy.factNrStuckOtherPieces = factNrStuckOtherPieces;
        copy.factNrStuckSelfPieces = factNrStuckSelfPieces;
        copy.factNrThreatenedPiecesOther = factNrThreatenedPiecesOther;
        copy.factNrThreatenedPiecesSelf = factNrThreatenedPiecesSelf;
        copy.factNrOfNormalPiecesInRow = new double[factNrOfNormalPiecesInRow.length];
        System.arraycopy(factNrOfNormalPiecesInRow, 0, copy.factNrOfNormalPiecesInRow, 0, factNrOfNormalPiecesInRow.length);
        return copy;
    }
    

}
