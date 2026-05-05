package morphchess.model;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece{
	
	public Bishop (pieceColor color, int row, int col) {super (pieceType.BISHOP, color, row, col);}
	
	@Override
	public List<int[]> getLegalMoves(Piece[][] board) 
	{
		List<int[]> moves = new ArrayList<>();
		moves = drawMoves(board,8,true,false,false,0);	
		return moves;
	}
	
}