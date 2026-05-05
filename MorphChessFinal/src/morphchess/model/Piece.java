package morphchess.model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.ImageView;
import morphchess.model.Piece.pieceColor;

public abstract class Piece 
{
	
	public enum pieceType {PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING}
	public enum pieceColor{WHITE, BLACK}
	
	protected pieceType  type;
	protected pieceType  stolenType;
	protected pieceColor color;
	protected boolean    isStolen;
	protected int        row;
	protected int        col;
	protected ImageView  imageView;
	
	public Piece (pieceType type, pieceColor color, int row, int col) 
	{
		this.type = type;
		this.color = color;
		this.row = row;
		this.col = col;
		this.isStolen = false;
	}
	
	public abstract java.util.List<int[]> getLegalMoves(Piece[][] board);
	
	public pieceType  getType()      {return type;}
	public pieceColor getColor()     {return color;}
	public boolean    isStolen()     {return isStolen;}
	public int        getRow()       {return row;}
	public int        getCol()       {return col;}
	public ImageView  getImageView() {return imageView;}
	
	public void setRow(int row)           {this.row = row;}
	public void setCol(int col)           {this.col = col;}
	public void setStolen(boolean stolen) {this.isStolen = stolen;}
	public void setImageView(ImageView imageView) {this.imageView = imageView;}
	
	public boolean isEnemyOf(pieceColor otherColor) {return this.color != otherColor;}
		
	public boolean inBounds(int r, int c)
	{
		return r >= 0 && r < 8 && c >=0 && c < 8;
	}
	
	//Movement Logic Below//
	
	public List<int[]> drawLine(Piece[][] board, int distance,int rowIncrement,int colIncrement)
	{
		List<int[]> moves = new ArrayList<>();
		int level = 1;
		int rowIndex;
		int colIndex;
		boolean sightOpen = true;
		
		while(sightOpen && level <=distance)
		{
			rowIndex = rowIncrement*level;
			colIndex = colIncrement*level;;
			
			if(inBounds(row+rowIndex,col+colIndex) && board[row+rowIndex][col+colIndex] == null)
			{
				moves.add(new int[] {row+rowIndex,col+colIndex});
			}
			else if(inBounds(row+rowIndex,col+colIndex) && board[row+rowIndex][col+colIndex] != null)
			{
				sightOpen = false;
				if(board[row+rowIndex][col+colIndex].isEnemyOf(color))
				{
					moves.add(new int[] {row+rowIndex,col+colIndex});
				}
			}
			level++;
		}
		return moves;
	}
	
	public List<int[]> drawDiagonals(Piece[][] board, int distance)
	{
		List<int[]> moves = new ArrayList<>();
		
		for(int[] move:drawLine(board, distance, 1, 1)) {moves.add(move);}
		for(int[] move:drawLine(board, distance, 1,-1)) {moves.add(move);}
		for(int[] move:drawLine(board, distance,-1, 1)) {moves.add(move);}
		for(int[] move:drawLine(board, distance,-1,-1)) {moves.add(move);}
		
		return moves;
	}
	
	public List<int[]> drawVertical(Piece[][] board, int distance)
	{
		List<int[]> moves = new ArrayList<>();
		
		for(int[] move:drawLine(board, distance, 1, 0)) {moves.add(move);}
		for(int[] move:drawLine(board, distance,-1, 0)) {moves.add(move);}
		
		return moves;
	}
	
	public List<int[]> drawHorizontal(Piece[][] board, int distance)
	{
		List<int[]> moves = new ArrayList<>();
		
		for(int[] move:drawLine(board, distance, 0, 1)) {moves.add(move);}
		for(int[] move:drawLine(board, distance, 0,-1)) {moves.add(move);}
		
		return moves;
	}
	
	public List<int[]> drawMoves(Piece[][] board, int distance, boolean diagonal, boolean vertical, boolean horizontal, int special)
	{
		List<int[]> moves = new ArrayList<>();
		
		int direction = 0;
		if(special == 1) {direction = (color == pieceColor.WHITE) ? -1 : 1;}
		
		if(direction != 0) //Pawn Logic//
		{
			// Movement Logic
			int nextRow = row + direction*distance;
			int startRow = (color == pieceColor.WHITE) ? 6 : 1;
			
			if (inBounds(nextRow, col) && board[nextRow][col] == null) 
			{
				if(row == startRow && distance == 1)
				{
					moves.add(new int[] {nextRow, col});
					moves.add(new int[] {nextRow+direction, col});
				}
				moves.add(new int[] {nextRow, col});
			}
			
			//Capture Logic
			int[] captureCols = {col - 1, col + 1};			
			for (int captureCol : captureCols) 
			{
				if (inBounds(nextRow, captureCol)) 
				{
					Piece target = board[nextRow][captureCol];
					
					//game rule, pawns cant take stolen pieces
					if (target != null && target.isEnemyOf(color)) 
					{
						if (!target.isStolen()) 
						{
							moves.add(new int[] {nextRow, captureCol});
						}
					}
				}
			}
		}
		else //All other pieces
		{
			if(diagonal)
			{
				for(int[] move:drawDiagonals(board, distance)){moves.add(move);}
			}
			if(vertical)
			{
				for(int[] move:drawVertical(board, distance)){moves.add(move);}
			}
			if(horizontal)
			{
				for(int[] move:drawHorizontal(board, distance)){moves.add(move);}
			}
			if(special == 2) // Knight Logic
			{
				for(int[] move:drawLine(board, distance, 2, 1)) {moves.add(move);}
				for(int[] move:drawLine(board, distance, 2,-1)) {moves.add(move);}
				for(int[] move:drawLine(board, distance,-2, 1)) {moves.add(move);}
				for(int[] move:drawLine(board, distance,-2,-1)) {moves.add(move);}
				for(int[] move:drawLine(board, distance, 1, 2)) {moves.add(move);}
				for(int[] move:drawLine(board, distance, 1,-2)) {moves.add(move);}
				for(int[] move:drawLine(board, distance,-1, 2)) {moves.add(move);}
				for(int[] move:drawLine(board, distance,-1,-2)) {moves.add(move);}
			}
		}
		
		return moves;
	}
	public pieceType getStolenType() 
	{ 
		return stolenType; 
	}
	public void setStolenType(pieceType type) 
	{ 
		this.stolenType = type; 
	}
}	