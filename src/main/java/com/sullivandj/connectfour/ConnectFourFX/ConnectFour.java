package com.sullivandj.connectfour.ConnectFourFX;

import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ConnectFour extends Application {
	
	private static final int ROW_COUNT = 6;
	private static final int COL_COUNT = 7;
	
	private static final double INDEX_HEIGHT = 100.0;
	private static final double INDEX_WIDTH = INDEX_HEIGHT;
	
	private static final double VGAP = 10.0;
	private static final double HGAP = VGAP;
	private static final double PADDING = 10.0;
	
	private static final double PIECE_RADIUS = INDEX_WIDTH / 2.0;
	
	private static final String NEW_GAME_TEXT = "New Game";
	private static final String QUIT_TEXT = "Quit";
	
	private Stage stage;
	private Scene root;
	private GridPane board;
	private Dialog<ButtonType> endDialog;
	
	private Piece [][] pieces;
	
	private Color currentColor;
	
	private class Piece extends Circle {
		
		private final int colIndex;
		private final int rowIndex;
		private Color color;
		
		private Piece (double radius, Color color, int colIndex, int rowIndex) {
			super(radius, color);
			this.colIndex = colIndex;
			this.rowIndex = rowIndex;
			this.color = color;
			setOnMousePressed(e -> drop(this.colIndex));
		}
		
		private void setColor (Color color) {
			this.color = color;
			setFill(color);
		}
		
		private boolean isEmpty () {
			return getFill().equals(Color.WHITE);
		}
	}
	
    public static void main (String[] args) {
    	launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		stage.setTitle("ConnectFour");
		initBoard();
		initEndDialog();
		root = new Scene(board);
		primaryStage.setScene(root);
		primaryStage.show();
	}
	
	public void initBoard () {
		board = new GridPane();
		board.setAlignment(Pos.BOTTOM_LEFT);
		board.setVgap(VGAP);
		board.setHgap(HGAP);
		board.setPadding(new Insets(PADDING));
		board.setStyle("-fx-background-color: #8FBC8F");
		currentColor = Color.RED;
		pieces = new Piece [COL_COUNT][ROW_COUNT];
		for (int i = 0; i < COL_COUNT; i++) {
			for (int j = 0; j < ROW_COUNT; j++) {
				pieces[i][j] = new Piece(PIECE_RADIUS, Color.WHITE, i, j);
				board.add(pieces[i][j], COL_COUNT - 1 - i, ROW_COUNT - 1 - j);
			}
		}
	}
	
	public void resetBoard () {
		currentColor = Color.RED;
		for (int i = 0; i < COL_COUNT; i++) {
			for (int j = 0; j < ROW_COUNT; j++) {
				pieces[i][j].setColor(Color.WHITE);;
			}
		}
	}
	
	public void initEndDialog () {
		endDialog = new Dialog<>();
		endDialog.setTitle("Game Over");
		endDialog.setContentText("YOU SHOULD NOT SEE THIS.");
		ButtonType newGameButtonType = new ButtonType(NEW_GAME_TEXT);
		ButtonType quitButtonType = new ButtonType(QUIT_TEXT);
		endDialog.getDialogPane().getButtonTypes().addAll(newGameButtonType, quitButtonType);
	}
	
	public void showEndDialog () {
		Optional<ButtonType> result = endDialog.showAndWait();
		if (result.isPresent() && result.get().getText().equals(NEW_GAME_TEXT)) {
			resetBoard();
		}
		else {
			stage.close();
		}
	}
	
	private void drop (int colIndex) {
		Piece lastPiece = pieces[colIndex][ROW_COUNT-1];
		if (lastPiece.isEmpty()) {
			for (int i = ROW_COUNT-2; i >= 0; i--) {
				if (pieces[colIndex][i].isEmpty()) {
					lastPiece = pieces[colIndex][i];
				}
				else {
					break;
				}
			}
			lastPiece.setColor(currentColor);
			swapColor();
			check(lastPiece);
			return;
		}
	}
	
	private void swapColor () {
		if (currentColor.equals(Color.RED)) {
			currentColor = Color.BLACK;
		}
		else {
			currentColor = Color.RED;
		}
	}
	
	private void check (Piece piece) {
		if (checkS(piece) >= 3 ||
				checkW(piece) + checkE(piece) >= 3 ||
				checkNW(piece) + checkSE(piece) >= 3 ||
				checkNE(piece) + checkSW(piece) >= 3) {
			win(piece.color);
		}
	}
	
	private void win (Color color) {
		if (color.equals(Color.RED)) {
			endDialog.setContentText("Red wins.  Start a new game or quit.");
		} 
		else {
			endDialog.setContentText("Black wins.  Start a new game or quit.");
		}
		showEndDialog();
	}
	
	private int checkS (Piece piece) {
		Color color = piece.color;
		int colIndex = piece.colIndex;
		int rowIndex = piece.rowIndex - 1;
		int connectingPieces = 0;
		while (rowIndex >= 0 && rowIndex < ROW_COUNT && colIndex >= 0 && colIndex < COL_COUNT) {
			if (pieces[colIndex][rowIndex].color.equals(color)) {
				connectingPieces++;
				rowIndex--;
			}
			else {
				break;
			}
		}
		return connectingPieces;
	}
	
	private int checkE (Piece piece) {
		int connectingPieces = 0;
		Color color = piece.color;
		int colIndex = piece.colIndex + 1;
		int rowIndex = piece.rowIndex;
		while (rowIndex >= 0 && rowIndex < ROW_COUNT && colIndex >= 0 && colIndex < COL_COUNT) {
			if (pieces[colIndex][rowIndex].color.equals(color)) {
				connectingPieces++;
				colIndex++;
			}
			else {
				break;
			}
		}
		return connectingPieces;
	}
	
	private int checkW (Piece piece) {
		int connectingPieces = 0;
		Color color = piece.color;
		int colIndex = piece.colIndex - 1;
		int rowIndex = piece.rowIndex;
		while (rowIndex >= 0 && rowIndex < ROW_COUNT && colIndex >= 0 && colIndex < COL_COUNT) {
			if (pieces[colIndex][rowIndex].color.equals(color)) {
				connectingPieces++;
				colIndex--;
			}
			else {
				break;
			}
		}
		return connectingPieces;
	}
	
	private int checkNE (Piece piece) {
		int connectingPieces = 0;
		Color color = piece.color;
		int colIndex = piece.colIndex + 1;
		int rowIndex = piece.rowIndex + 1;
		while (rowIndex >= 0 && rowIndex < ROW_COUNT && colIndex >= 0 && colIndex < COL_COUNT) {
			if (pieces[colIndex][rowIndex].color.equals(color)) {
				connectingPieces++;
				colIndex++;
				rowIndex++;
			}
			else {
				break;
			}
		}
		return connectingPieces;
	}
	
	private int checkNW (Piece piece) {
		int connectingPieces = 0;
		Color color = piece.color;
		int colIndex = piece.colIndex - 1;
		int rowIndex = piece.rowIndex + 1;
		while (rowIndex >= 0 && rowIndex < ROW_COUNT && colIndex >= 0 && colIndex < COL_COUNT) {
			if (pieces[colIndex][rowIndex].color.equals(color)) {
				connectingPieces++;
				colIndex--;
				rowIndex++;
			}
			else {
				break;
			}
		}
		return connectingPieces;
	}
	
	private int checkSE (Piece piece) {
		int connectingPieces = 0;
		Color color = piece.color;
		int colIndex = piece.colIndex + 1;
		int rowIndex = piece.rowIndex - 1;
		while (rowIndex >= 0 && rowIndex < ROW_COUNT && colIndex >= 0 && colIndex < COL_COUNT) {
			if (pieces[colIndex][rowIndex].color.equals(color)) {
				connectingPieces++;
				colIndex++;
				rowIndex--;
			}
			else {
				break;
			}
		}
		return connectingPieces;
	}
	
	private int checkSW (Piece piece) {
		int connectingPieces = 0;
		Color color = piece.color;
		int colIndex = piece.colIndex - 1;
		int rowIndex = piece.rowIndex - 1;
		while (rowIndex >= 0 && rowIndex < ROW_COUNT && colIndex >= 0 && colIndex < COL_COUNT) {
			if (pieces[colIndex][rowIndex].color.equals(color)) {
				connectingPieces++;
				colIndex--;
				rowIndex--;
			}
			else {
				break;
			}
		}
		return connectingPieces;
	}
}
