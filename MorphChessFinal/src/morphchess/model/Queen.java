package morphchess.model;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece{
	
	public Queen (pieceColor color, int row, int col) {super (pieceType.QUEEN, color, row, col);}
	
	@Override
	public List<int[]> getLegalMoves(Piece[][] board) 
	{
		List<int[]> moves = new ArrayList<>();
		moves = drawMoves(board,8,true,true,true,0);	
		return moves;
	}
}