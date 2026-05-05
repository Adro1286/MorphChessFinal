package morphchess.model;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece
{	
	public Rook (pieceColor color, int row, int col) {super (pieceType.ROOK, color, row, col);}
	
	@Override
	public List<int[]> getLegalMoves(Piece[][] board) 
	{
		List<int[]> moves = new ArrayList<>();
		moves = drawMoves(board,8,false,true,true,0);	
		return moves;
	}	
}
