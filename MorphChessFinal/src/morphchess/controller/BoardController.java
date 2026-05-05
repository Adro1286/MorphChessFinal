package morphchess.controller;

import javafx.fxml.FXML;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import morphchess.model.Piece;
import morphchess.model.Piece.pieceType;
import morphchess.model.Pawn;
import morphchess.model.Bishop;
import morphchess.model.King;
import morphchess.model.Knight;
import morphchess.model.Queen;
import morphchess.model.Rook;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class BoardController 
{
	@FXML
	private ListView<String> moveList;

	private ObservableList<String> moveHistory = FXCollections.observableArrayList();

	@FXML
	private GridPane boardGrid;
	
	@FXML
	private VBox topPane;
	
	@FXML
	private VBox bottomPane;
	
	private final int tileSize = 80;
	private Piece[][] board = new Piece[8][8];
	private Piece selectedPiece = null;
	private List<int[]> currentLegalMoves = null;
	private Piece.pieceColor currentTurn = Piece.pieceColor.WHITE;
	
	@FXML
	public void initialize() 
	{
		setupBoard();
		setupPieces();
		renderPieces();
		moveList.setItems(moveHistory);
	}
	
	private void setupBoard() 
	{
		for (int row = 0; row < 8; row++) 
		{
			for (int col = 0; col < 8; col++)
			{
				final int r = row;
				final int c = col;
				
				StackPane cell = new StackPane();
				cell.setPrefSize(tileSize, tileSize);
				Rectangle tile = new Rectangle(tileSize, tileSize);
				tile.setFill((row + col) % 2 == 0 ? Color.BEIGE : Color.BROWN);
				cell.getChildren().add(tile);
				
				cell.setOnMouseClicked(e -> handleClick(r, c));
				boardGrid.add(cell,  col,  row);
			}
		}
	}
	
	private void setupPieces() 
	{
		//-------------Black Row------------------------------
		board[0][0] = new Rook  (Piece.pieceColor.BLACK, 0, 0);
		board[0][1] = new Knight(Piece.pieceColor.BLACK, 0, 1);
		board[0][2] = new Bishop(Piece.pieceColor.BLACK, 0, 2);
		board[0][3] = new Queen (Piece.pieceColor.BLACK, 0, 3);
		board[0][4] = new King  (Piece.pieceColor.BLACK, 0, 4);
		board[0][5] = new Bishop(Piece.pieceColor.BLACK, 0, 5);
		board[0][6] = new Knight(Piece.pieceColor.BLACK, 0, 6);
		board[0][7] = new Rook  (Piece.pieceColor.BLACK, 0, 7);
		//-------------Pawns-----------------------------------
		for (int col = 0; col < 8; col++) 
		{
			board[1][col] = new Pawn(Piece.pieceColor.BLACK, 1, col);
		}
		for (int col = 0; col < 8; col++) 
		{
			board[6][col] = new Pawn(Piece.pieceColor.WHITE, 6, col);
		}
		//-------------White Row------------------------------
		board[7][0] = new Rook  (Piece.pieceColor.WHITE, 7, 0);
		board[7][1] = new Knight(Piece.pieceColor.WHITE, 7, 1);
		board[7][2] = new Bishop(Piece.pieceColor.WHITE, 7, 2);
		board[7][3] = new Queen (Piece.pieceColor.WHITE, 7, 3);
		board[7][4] = new King  (Piece.pieceColor.WHITE, 7, 4);
		board[7][5] = new Bishop(Piece.pieceColor.WHITE, 7, 5);
		board[7][6] = new Knight(Piece.pieceColor.WHITE, 7, 6);
		board[7][7] = new Rook  (Piece.pieceColor.WHITE, 7, 7);
	}
	
	private void renderPieces() 
	{
		for (int row = 0; row < 8; row++) 
		{
			for(int col = 0; col < 8; col++) 
			{
				Piece piece = board[row][col];
				if (piece != null) 
				{
					ImageView iv = new ImageView(loadImage(piece));
					iv.setFitWidth(tileSize);
					iv.setFitHeight(tileSize);
					iv.setPreserveRatio(true);
					iv.setMouseTransparent(true);
					piece.setImageView(iv);
					getCell(row, col).getChildren().add(iv);
				}
			}
		}
	}
	private void handleClick(int row, int col) 
	{
		Piece clicked = board[row][col];
		//-------------Move piece if valid------------------------------
		if (selectedPiece != null && isLegalMove(row, col)) 
		{
            movePiece(selectedPiece, row, col);
            clearHighlights();
            selectedPiece = null;
            currentLegalMoves = null;
            return;
		}
		
		clearHighlights();
		selectedPiece = null;
		currentLegalMoves = null;
		
		if (clicked != null && clicked.getColor() == currentTurn) 
		{
            selectedPiece = clicked;
            List<int[]> rawMoves = clicked.getLegalMoves(board);
            currentLegalMoves = new java.util.ArrayList<>();

            for (int[] move : rawMoves) {
                if (isSafeMove(clicked, move[0], move[1])) {
                    currentLegalMoves.add(move);
                }
            }
            highlightMoves(currentLegalMoves);
        }
	}
	
	private void movePiece(Piece piece, int newRow, int newCol) 
	{
		Piece captured = board[newRow][newCol];

        int oldRow = piece.getRow();
        int oldCol = piece.getCol();
		String notation = generateNotation(piece, oldRow, oldCol, newRow, newCol);
		//-------------remove old piece from board------------------------------
        getCell(oldRow, oldCol).getChildren().remove(piece.getImageView());
        board[oldRow][oldCol] = null;

        if(board[newRow][newCol] != null)
        {
        	getCell(newRow, newCol).getChildren().remove(board[newRow][newCol].getImageView());
			sendToGraveyard(captured);
        	board[newRow][newCol] = null;
        }
        //-------------place piece in new cell------------------------------
        board[newRow][newCol] = piece;
        piece.setRow(newRow);
        piece.setCol(newCol);
        getCell(newRow, newCol).getChildren().add(piece.getImageView());
		//morph logic
		if (piece instanceof Pawn && captured != null && !piece.isStolen())
        {
            Pawn pawn = (Pawn) piece;
            pawn.setStolen(true);
            pawn.setStolenType(captured.getType());
            updateStolenVisual(pawn);
        }
        //-------------Switch turns------------------------------
        currentTurn = (currentTurn == Piece.pieceColor.WHITE)
                ? Piece.pieceColor.BLACK
                : Piece.pieceColor.WHITE;
		moveHistory.add(notation);
		Piece.pieceColor opponent = currentTurn;
		//-------------check system------------------------------
        if (isInCheck(opponent)) {
            if (!hasAnyLegalMove(opponent)) {
                moveHistory.add("CHECKMATE - " + currentTurn + " wins");
                boardGrid.setDisable(true);
                return;
            } else {
                moveHistory.add("CHECK!");
            }
        }
	}
	
	private void highlightMoves(List<int[]> moves) 
	{
        for (int[] move : moves) 
        {
            StackPane cell = getCell(move[0], move[1]);
            Rectangle highlight = new Rectangle(tileSize, tileSize);
            highlight.setFill(Color.color(0.4, 0.8, 1.0, 0.5));
            highlight.setMouseTransparent(true);
            cell.getChildren().add(highlight);
        }
    }
	
	private void clearHighlights() 
	{
        for (int row = 0; row < 8; row++) 
        {
            for (int col = 0; col < 8; col++) 
            {
                StackPane cell = getCell(row, col);
                cell.getChildren().removeIf(node -> node instanceof Rectangle && ((Rectangle) node).getFill().equals(Color.color(0.4, 0.8, 1.0, 0.5)));
            }
        }
    }
	//-------------move validation------------------------------
	private boolean isLegalMove(int row, int col) 
	{
        if (currentLegalMoves == null) return false;
        for (int[] move : currentLegalMoves) 
        {
            if (move[0] == row && move[1] == col) return true;
        }
        return false;
    }

	private StackPane getCell(int row, int col) 
	{
	    for (var node : boardGrid.getChildren()) 
	    {
	        Integer r = GridPane.getRowIndex(node);
	        Integer c = GridPane.getColumnIndex(node);
	        int nodeRow = (r == null) ? 0 : r;
	        int nodeCol = (c == null) ? 0 : c;
	        if (nodeRow == row && nodeCol == col && node instanceof StackPane) 
	        {
	            return (StackPane) node;
	        }
	    }
	    return null;
	}
	
	private Image loadImage(Piece piece) 
	{
		String color = piece.getColor() == Piece.pieceColor.WHITE ? "white" : "black";
		String type = piece.getType().name().toLowerCase();
		String path = "/images/" + color + "_" + type + ".png";
		return new Image(getClass().getResourceAsStream(path));
	}

	private void updateStolenVisual(Pawn pawn) 
	{
		String color = pawn.getColor() == Piece.pieceColor.WHITE ? "white" : "black";
	    String type = pawn.getStolenType().name().toLowerCase();
	    String path = "/images/" + color + "_" + type + ".png";
	    
	    Image newImage = new Image(getClass().getResourceAsStream(path));
	    pawn.getImageView().setImage(newImage);
	    
	 
	    if(pawn.getStolenType() != pieceType.PAWN)
	    {
	    	applyTint(pawn);                                                
	    } else
	    {
	    	pawn.getImageView().setEffect(null);
	    }
	 
	}

	private void applyTint(Pawn pawn)
	{
	    ColorAdjust tint = new ColorAdjust();

	    if (pawn.getColor() == Piece.pieceColor.BLACK) {
	        tint.setHue(0.0);        
	        tint.setSaturation(0.8); //red
	    } else {
	    	tint.setSaturation(0.3);
	        tint.setHue(-0.5);       // blue
	    }

	    pawn.getImageView().setEffect(tint);
	}

	private void sendToGraveyard(Piece piece) 
	{
		ImageView size = piece.getImageView();
		size.setFitWidth(60);
		size.setFitHeight(60);
		if (piece.getColor() == Piece.pieceColor.WHITE) 
		{
			if (topPane.getChildren().isEmpty() ||
		            ((HBox) topPane.getChildren()
		                .get(topPane.getChildren().size() - 1))
		                .getChildren().size() >= 2) 
			{
		            HBox newRow = new HBox(5);
		            topPane.getChildren().add(newRow);
		    }
		        HBox currentRow = (HBox) topPane.getChildren()
		                .get(topPane.getChildren().size() - 1);
		        currentRow.getChildren().add(size);
		} else 
		{
			if (bottomPane.getChildren().isEmpty() ||
		            ((HBox) bottomPane.getChildren()
		                .get(bottomPane.getChildren().size() - 1))
		                .getChildren().size() >= 2) 
			{
		            HBox newRow = new HBox(5);
		            bottomPane.getChildren().add(newRow);
		    }
		        HBox currentRow = (HBox) bottomPane.getChildren()
		                .get(bottomPane.getChildren().size() - 1);
		        currentRow.getChildren().add(size);
		}

	}

	@FXML
	private void handleReset() 
	{
		boardGrid.getChildren().clear();
		boardGrid.requestLayout();
		topPane.getChildren().clear();
		bottomPane.getChildren().clear();
		board = new Piece[8][8];
		currentTurn = Piece.pieceColor.WHITE;
		setupBoard();
		setupPieces();
		renderPieces();
		moveHistory.clear();
		boardGrid.setDisable(false);
	}

	private String generateNotation(Piece piece, int fromRow, int fromCol, int toRow, int toCol) 
	{

	    char file = (char) ('a' + toCol);
	    int rank = 8 - toRow;
	    if (piece.getType() == Piece.pieceType.PAWN)
		{
	        return "" + file + rank;
	    }
	    String pieceLetter = "";
	    switch (piece.getType()) 
		{
	        case KNIGHT: pieceLetter = "N"; break;
	        case BISHOP: pieceLetter = "B"; break;
	        case ROOK:   pieceLetter = "R"; break;
	        case QUEEN:  pieceLetter = "Q"; break;
	        case KING:   pieceLetter = "K"; break;
	        default:     pieceLetter = "";
	    }
	    return pieceLetter + file + rank;
	}

	private Piece findKing(Piece.pieceColor color) 
	{
	    for (int r = 0; r < 8; r++) 
		{
	        for (int c = 0; c < 8; c++) 
			{
	            Piece p = board[r][c];
	            if (p != null && p.getType() == Piece.pieceType.KING && p.getColor() == color) 
				{
	                return p;
	            }
	        }
	    }
	    return null;
	}

	private boolean isSquareUnderAttack(int row, int col, Piece.pieceColor attackerColor) 
	{
	    for (int r = 0; r < 8; r++) 
		{
	        for (int c = 0; c < 8; c++) 
			{
	            Piece p = board[r][c];
	            if (p != null && p.getColor() == attackerColor) 
				{
	                List<int[]> moves = p.getLegalMoves(board);
	                for (int[] move : moves) 
					{
	                    if (move[0] == row && move[1] == col) 
						{
	                        return true;
	                    }
	                }
	            }
	        }
	    }
	    return false;
	}

	private boolean isInCheck(Piece.pieceColor color) 
	{
	    Piece king = findKing(color);
	    if (king == null) return false;
	    Piece.pieceColor enemy = (color == Piece.pieceColor.WHITE)
	            ? Piece.pieceColor.BLACK
	            : Piece.pieceColor.WHITE;
	    return isSquareUnderAttack(king.getRow(), king.getCol(), enemy);
	}

	private boolean hasAnyLegalMove(Piece.pieceColor color) 
	{
	    for (int r = 0; r < 8; r++) 
		{
	        for (int c = 0; c < 8; c++) 
			{
	            Piece p = board[r][c];

	            if (p != null && p.getColor() == color) 
				{
	                List<int[]> moves = p.getLegalMoves(board);

	                for (int[] move : moves) 
					{
	                    if (isSafeMove(p, move[0], move[1])) 
						{
	                        return true;
	                    }
	                }
	            }
	        }
	    }
	    return false;
	}

	private boolean isSafeMove(Piece piece, int newRow, int newCol) {
	    int oldRow = piece.getRow();
	    int oldCol = piece.getCol();
	    Piece captured = board[newRow][newCol];
		
	    board[oldRow][oldCol] = null;
	    board[newRow][newCol] = piece;
	    piece.setRow(newRow);
	    piece.setCol(newCol);
		
	    boolean inCheck = isInCheck(piece.getColor());
		
	    board[oldRow][oldCol] = piece;
	    board[newRow][newCol] = captured;
	    piece.setRow(oldRow);
	    piece.setCol(oldCol);

	    return !inCheck;
	}
}