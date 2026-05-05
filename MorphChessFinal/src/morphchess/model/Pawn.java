package morphchess.model;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece{
	
	public Pawn (pieceColor color, int row, int col) {super (pieceType.PAWN, color, row, col);}
	
	@Override
	public List<int[]> getLegalMoves(Piece[][] board)
	{
	    if (isStolen && stolenType != null) {
	        return getStolenMoves(board);
	    }
	    return drawMoves(board, 1, true, false, true, 1);
	}
	private List<int[]> getStolenMoves(Piece[][] board)
	{
	    List<int[]> moves = new ArrayList<>();

	    switch (stolenType)
	    {
	        case ROOK:
	            moves = drawMoves(board, 2, false, true, true, 0);
	            break;
	        case BISHOP:
	            moves = drawMoves(board, 2, true, false, false, 0);
	            break;
	        case QUEEN:
	            moves = drawMoves(board, 2, true, true, true, 0);
	            break;
	        case KNIGHT:
	            moves = drawMoves(board, 1, false, false, false, 2);
	            break;
	        case KING:
	            moves = drawMoves(board, 1, true, true, true, 0);
	            break;
	        default:
	            moves = drawMoves(board, 1, true, false, true, 1);
	    }
	    return moves;
	}
}