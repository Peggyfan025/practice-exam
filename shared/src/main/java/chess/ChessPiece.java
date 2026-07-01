package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        PieceMoveCal cal;
        if (piece.getPieceType()==PieceType.BISHOP){
            cal = new BishopMove(board,piece,myPosition);
        }
        else if (piece.getPieceType()==PieceType.KNIGHT){
            cal = new KnightMove(board,piece,myPosition);
        }
        else if (piece.getPieceType()==PieceType.ROOK){
            cal = new RookMove(board,piece,myPosition);
        }
        else if (piece.getPieceType()==PieceType.KING){
            cal = new KingMove(board,piece,myPosition);
        }
        else if (piece.getPieceType()==PieceType.QUEEN){
            cal = new QueenMove(board,piece,myPosition);
        }
        else if (piece.getPieceType()==PieceType.PAWN){
            cal = new PawnMove(board,piece,myPosition);
        }
        else {
            return List.of();
        }
        return cal.pieceMove();
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
abstract class PieceMoveCal{

    protected final ChessBoard board;
    protected final ChessPiece piece;
    protected final ChessPosition position;

    public PieceMoveCal(ChessBoard board, ChessPiece piece, ChessPosition position){
        this.board = board;
        this.piece = piece;
        this.position = position;
    }
    //direction help
    protected final static int[][] STRAIGHT_DIRECTION = {{0,1}, {0,-1}, {-1,0}, {1,0}};
    protected final static int[][] DIAGONAL_DIRECTION = {{1,1}, {-1,-1}, {-1,1}, {1,-1}};

    public abstract Collection<ChessMove> pieceMove();
    protected boolean inRange (int row, int col){
        return row<=8 && row>=1 && col<=8 && col>=1;
    }
    protected void addMove(Collection<ChessMove> moves,int[][]directions){
        for (int[] direction:directions){
            int row = position.getRow()+direction[0];
            int col = position.getColumn()+direction[1];

            while (inRange(row, col)){
                ChessPosition end = new ChessPosition(row,col);
                if (board.getPiece(end) == null){
                    moves.add(new ChessMove(position,end,null));
                }
                else if (board.getPiece(end).getTeamColor()!=piece.getTeamColor()){
                    moves.add(new ChessMove(position, end, null));
                    break;
                }
                else{
                    break;
                }
                row+=direction[0];
                col+=direction[1];
            }
        }
    }
}

class BishopMove extends PieceMoveCal{
    public BishopMove(ChessBoard board, ChessPiece piece, ChessPosition position){
        super(board, piece, position);
    }
    @Override
    public Collection<ChessMove> pieceMove(){
        Collection<ChessMove> moves = new ArrayList<>();
        addMove(moves,DIAGONAL_DIRECTION);
        return moves;
    }
}
class RookMove extends PieceMoveCal{
    public RookMove(ChessBoard board, ChessPiece piece, ChessPosition position){
        super(board, piece, position);
    }
    @Override
    public Collection<ChessMove> pieceMove(){
        Collection<ChessMove> moves = new ArrayList<>();
        addMove(moves,STRAIGHT_DIRECTION);
        return moves;
    }
}
class QueenMove extends PieceMoveCal{
    public QueenMove(ChessBoard board, ChessPiece piece, ChessPosition position){
        super(board, piece, position);
    }
    @Override
    public Collection<ChessMove> pieceMove(){
        Collection<ChessMove> moves = new ArrayList<>();
        addMove(moves,STRAIGHT_DIRECTION);
        addMove(moves,DIAGONAL_DIRECTION);
        return moves;
    }
}
class KnightMove extends PieceMoveCal{
    public KnightMove(ChessBoard board, ChessPiece piece, ChessPosition position){
        super(board, piece, position);
    }
    int[][] knight_direction= {{1,2}, {1,-2}, {-1, 2}, {-1,-2}, {2, 1}, {2, -1}, {-2, 1},{-2, -1}};
    @Override
    public Collection<ChessMove> pieceMove(){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        for (int[] direction: knight_direction){
            int r = row+direction[0];
            int c = col+direction[1];
            if (inRange(r,c)) {
                ChessPosition end = new ChessPosition(r, c);
                if (board.getPiece(end) == null){
                    moves.add(new ChessMove(position, end, null));
                }
                else if (board.getPiece(end).getTeamColor()!=piece.getTeamColor()){
                    moves.add(new ChessMove(position, end, null));
                }

            }
        }
        return moves;
    }
}
//unique from other
class KingMove extends PieceMoveCal{
    public KingMove(ChessBoard board, ChessPiece piece, ChessPosition position){
        super(board, piece, position);
    }
    int [][] king_direction = {{1,1}, {1,0}, {0,1}, {-1,-1}, {-1, 0}, {-1,1}, {0,-1},{1, -1}};
    @Override
    public Collection<ChessMove> pieceMove(){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        for (int[] direction: king_direction){
            int r = row+direction[0];
            int c = col+direction[1];
            if (inRange(r,c)) {
                ChessPosition end = new ChessPosition(r, c);
                if (board.getPiece(end) == null){
                    moves.add(new ChessMove(position, end, null));
                }
                else if (board.getPiece(end).getTeamColor()!=piece.getTeamColor()){
                    moves.add(new ChessMove(position, end, null));
                }

            }
        }
        return moves;
    }
}
class PawnMove extends PieceMoveCal{
    public PawnMove(ChessBoard board, ChessPiece piece, ChessPosition position){
        super(board, piece, position);
    }
    @Override
    public Collection<ChessMove> pieceMove(){
        Collection<ChessMove> moves = new ArrayList<>();
        addMove(moves,DIAGONAL_DIRECTION);
        return moves;
    }
}