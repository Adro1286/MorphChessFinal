package morphchess.model;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece{
	
	public King (pieceColor color, int row, int col) {super (pieceType.KING, color, row, col);}
	
	@Override
	public List<int[]> getLegalMoves(Piece[][] board) 
	{
		List<int[]> moves = new ArrayList<>();
		moves = drawMoves(board,1,true,true,true,0);	
		return moves;
	}
	
}