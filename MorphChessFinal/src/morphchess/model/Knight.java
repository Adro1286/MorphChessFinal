package morphchess.model;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece{
	
	public Knight (pieceColor color, int row, int col) {super (pieceType.KNIGHT, color, row, col);}
	
	@Override
	public List<int[]> getLegalMoves(Piece[][] board) {
		List<int[]> moves = new ArrayList<>();

		moves = drawMoves(board,1,false,false,false,2);	
		return moves;
	}
	
}