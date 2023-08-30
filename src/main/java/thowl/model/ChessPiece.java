package thowl.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ChessPiece {

  private Color currentTurnColor = Color.WHITE; // Game starts for white

  // Add a getter method for currentTurnColor
  public Color getCurrentTurnColor() {
    return currentTurnColor;
  }

  // TODO:
  /*  Could be a void function which prints either : no / check / checkmate.
  Inserted inside movePiece for example but needs to check for every enemy piece not only the last move */
  public boolean isOwnKingInCheck(Cell[][] cell, int kingRow, int kingCol) {

    // Your king is not in check
    return false;
  }

  /**
   * is called by the eventhandler when clicking a piece to move to another cell and gets the
   * pieceName given from which it then calls which Movement logic method needs to be called. If the
   * move is possible it returns true (and the method in eventhandler will call movePiece method and
   * reposition the piece).
   *
   * @param cell
   * @param fromRow
   * @param fromCol
   * @param toRow
   * @param toCol
   * @return
   */
  public boolean isMoveAllowed(Cell[][] cell, int fromRow, int fromCol, int toRow, int toCol) {
    String pieceName = cell[fromRow][fromCol].getPieceName();
    switch (pieceName) {
      case "pawn":
        return isPawnMoveAllowed(cell, fromRow, fromCol, toRow, toCol);
      case "rook":
        return isRookMoveAllowed(cell, fromRow, fromCol, toRow, toCol);
      case "bishop":
        return isBishopMoveAllowed(cell, fromRow, fromCol, toRow, toCol);
      case "knight":
        return isKnightMoveAllowed(cell, fromRow, fromCol, toRow, toCol);
      case "king":
        return isKingMoveAllowed(cell, fromRow, fromCol, toRow, toCol);
      case "queen":
        return isQueenMoveAllowed(cell, fromRow, fromCol, toRow, toCol);
      default:
        return false; // Invalid piece name
    }
  }

  /**
   * Move a piece and print its changes.
   *
   * @param fromRow
   * @param fromCol
   * @param toRow
   * @param toCol
   */
  public void movePiece(Cell[][] cell, int fromRow, int fromCol, int toRow, int toCol) {
    String pieceKilled = cell[toRow][toCol].getPieceName();

    // Get the piece values from the source cell
    Color pieceColor = cell[fromRow][fromCol].getPieceColor();
    String pieceName = cell[fromRow][fromCol].getPieceName();
    Image pieceImage = cell[fromRow][fromCol].getPieceImage();

    // Clear the piece values in the source cell
    cell[fromRow][fromCol].clearPiece();

    // Set the piece values in the destination cell
    cell[toRow][toCol].setPieceValues(pieceColor, pieceName, pieceImage);

    // Update the current turn's color
    currentTurnColor = (currentTurnColor == Color.WHITE) ? Color.BLACK : Color.WHITE;

    // printing action to the terminal (Could be implemented on the screen).
    char firstChar = pieceName.charAt(0);
    char fromChar = (char) (fromCol + 'A');
    char toChar = (char) (toCol + 'A');
    if (pieceKilled == null) {
      System.out.print(
          "\n"
              + firstChar
              + ""
              + (fromRow + 1)
              + fromChar
              + " to "
              + (toRow + 1)
              + toChar
              + "     "); // nothing in here. Auto format is better like this
    } else {
      System.out.print(
          "\n"
              + firstChar
              + ""
              + (fromRow + 1)
              + fromChar
              + " to "
              + (toRow + 1)
              + toChar
              + " takes "
              + pieceKilled);
    }
    // Check for pawn promotion
    if (cell[toRow][toCol].getPieceName() == "pawn" && (toRow == 0 || toRow == 7)) {
      System.out.println(" Change to another piece ");
      openPromotionDialog(cell, pieceColor, toRow, toCol);
    }
  }

  // Creates the window for choosing a new Piece and calls the PromotoButton class as eventhandler
  public void openPromotionDialog(Cell[][] cell, Color pieceColor, int toRow, int toCol) {
    ImageView queen = new ImageView();
    ImageView rook = new ImageView();
    ImageView bishop = new ImageView();
    ImageView knight = new ImageView();

    // white pieces
    if (pieceColor == Color.WHITE) {
      Image queenImage = new Image(getClass().getResourceAsStream("/images/whiteQueen.png"));
      queen.setImage(queenImage);

      Image rookImage = new Image(getClass().getResourceAsStream("/images/whiteRook.png"));
      rook.setImage(rookImage);

      Image bishopImage = new Image(getClass().getResourceAsStream("/images/whiteBishop.png"));
      bishop.setImage(bishopImage);

      Image knightImage = new Image(getClass().getResourceAsStream("/images/whiteknight.png"));
      knight.setImage(knightImage);
    } else {
      // black pieces
      Image queenImage = new Image(getClass().getResourceAsStream("/images/blackQueen.png"));
      queen.setImage(queenImage);

      Image rookImage = new Image(getClass().getResourceAsStream("/images/blackRook.png"));
      rook.setImage(rookImage);

      Image bishopImage = new Image(getClass().getResourceAsStream("/images/blackBishop.png"));
      bishop.setImage(bishopImage);

      Image knightImage = new Image(getClass().getResourceAsStream("/images/blackknight.png"));
      knight.setImage(knightImage);
    }

    // box for choosing to which piece the pawn is promoted
    HBox buttonsBox =
        new HBox(
            new PromoteButton(queen, cell, toRow, toCol, pieceColor, "queen"),
            new PromoteButton(rook, cell, toRow, toCol, pieceColor, "rook"),
            new PromoteButton(bishop, cell, toRow, toCol, pieceColor, "bishop"),
            new PromoteButton(knight, cell, toRow, toCol, pieceColor, "knight"));

    // Window for choosing the new piece
    Alert promotionAlert = new Alert(AlertType.NONE);
    promotionAlert.setTitle("Pawn Promotion");
    promotionAlert.setHeaderText("Choose a piece to promote your pawn:");

    // Set content and buttons
    promotionAlert.getDialogPane().setContent(buttonsBox);
    ButtonType applyButtonType = new ButtonType("Apply");
    promotionAlert.getButtonTypes().add(applyButtonType);

    // Disables close button (X button)
    Stage stage = (Stage) promotionAlert.getDialogPane().getScene().getWindow();
    stage.setOnCloseRequest(e -> e.consume());
    promotionAlert.showAndWait();
  }

  // Called by the eventhandler in PromoteButton and sets the chosen piece on the board
  public void promotePawn(Cell[][] cell, int toRow, int toCol, Color pieceColor, String pieceName) {
    String capitalizedPieceName = pieceName.substring(0, 1).toUpperCase() + pieceName.substring(1);

    String imagePath;
    if (pieceColor == Color.WHITE) {
      imagePath = "/images/white" + capitalizedPieceName + ".png";

    } else {
      imagePath = "/images/black" + capitalizedPieceName + ".png";
    }
    Image image = new Image(getClass().getResourceAsStream(imagePath));
    cell[toRow][toCol].setPieceValues(pieceColor, pieceName, image);
    System.out.println("Pawn promoted to:  " + pieceName);
  }

  public Boolean isPawnMoveAllowed(Cell[][] cell, int fromRow, int fromCol, int toRow, int toCol) {
    Cell oldCell = cell[fromRow][fromCol];
    Cell newCell = cell[toRow][toCol];

    if (!"pawn".equals(oldCell.getPieceName())) {
      return false;
    }

    if (oldCell.getPieceColor() == Color.WHITE) {

      // true: trying to move forward. false: trying to hit.
      if (fromCol == toCol) {

        // one cell forward
        if ((fromRow - toRow == -1) && newCell.isEmpty()) {
          return true;
        }
        // two cells forward
        if ((fromRow == 1 && toRow == 3) && cell[2][toCol].isEmpty() && newCell.isEmpty()) {
          return true;
        }

        // hit (left or right)
      } else if (((fromCol - toCol == -1) || (fromCol - toCol == 1))
          && (fromRow - toRow == -1)
          && newCell.getPieceColor() == Color.BLACK) {
        return true;
      }

      // black moves
    } else {
      // true: trying to move forward. false: trying to hit.
      if (fromCol == toCol) {

        // one cell forward
        if ((fromRow - toRow == 1) && newCell.isEmpty()) {
          return true;
        }
        // two cells forward
        if ((fromRow == 6 && toRow == 4) && cell[5][toCol].isEmpty() && newCell.isEmpty()) {
          return true;
        }

        // hit (left or right)
      } else if (((fromCol - toCol == 1) || (fromCol - toCol == -1))
          && (fromRow - toRow == 1)
          && newCell.getPieceColor() == Color.WHITE) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if rook moves are possible.
   *
   * @param fromRow
   * @param fromCol
   * @param toRow
   * @param toCol
   * @return true if possible, false if not
   */
  public Boolean isRookMoveAllowed(Cell[][] cell, int fromRow, int fromCol, int toRow, int toCol) {
    // Rook can move horizontally or vertically
    if (fromRow == toRow || fromCol == toCol) {
      // Check if there are any pieces in the path between from and to positions

      // If moving horizontally
      if (fromRow == toRow) {
        int startCol = Math.min(fromCol, toCol) + 1;
        int endCol = Math.max(fromCol, toCol);

        for (int col = startCol; col < endCol; col++) {
          if (!cell[fromRow][col].isEmpty()) {
            return false; // Path is blocked
          }
        }
      }

      // If moving vertically
      if (fromCol == toCol) {
        int startRow = Math.min(fromRow, toRow) + 1;
        int endRow = Math.max(fromRow, toRow);

        for (int row = startRow; row < endRow; row++) {
          if (!cell[row][fromCol].isEmpty()) {
            return false; // Path is blocked
          }
        }
      }

      // Destination cell should be either empty or occupied by opponent's piece
      if (cell[toRow][toCol].isEmpty()
          || (cell[toRow][toCol].getPieceColor() != cell[fromRow][fromCol].getPieceColor())) {
        return true; // Move is allowed
      }
    }

    return false; // Invalid move
  }

  public Boolean isKnightMoveAllowed(
      Cell[][] cell, int fromRow, int fromCol, int toRow, int toCol) {
    int rowDistance = Math.abs(toRow - fromRow);
    int colDistance = Math.abs(toCol - fromCol);

    // Knight can move in an L-shape (2 squares in one direction and 1 square in the other)
    if ((rowDistance == 2 && colDistance == 1) || (rowDistance == 1 && colDistance == 2)) {

      // Destination cell should be empty or occupied by opponent's piece
      if (cell[toRow][toCol].isEmpty()
          || (cell[toRow][toCol].getPieceColor() != cell[fromRow][fromCol].getPieceColor())) {
        return true; // Move is allowed
      }
    }

    return false; // Invalid move
  }

  // hard to unterstand
  public Boolean isBishopMoveAllowed(
      Cell[][] cell, int fromRow, int fromCol, int toRow, int toCol) {
    // Bishop can move diagonally
    if (Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol)) {
      int rowDirection = (toRow > fromRow) ? 1 : -1;
      int colDirection = (toCol > fromCol) ? 1 : -1;

      int currentRow = fromRow + rowDirection;
      int currentCol = fromCol + colDirection;

      while (currentRow != toRow && currentCol != toCol) {
        if (!cell[currentRow][currentCol].isEmpty()) {
          return false; // Path is blocked
        }

        currentRow += rowDirection;
        currentCol += colDirection;
      }

      // Destination cell should be either empty or occupied by opponent's piece
      if (cell[toRow][toCol].isEmpty()
          || (cell[toRow][toCol].getPieceColor() != cell[fromRow][fromCol].getPieceColor())) {
        return true; // Move is allowed
      }
    }

    return false; // Invalid move
  }

  public Boolean isKingMoveAllowed(Cell[][] cell, int fromRow, int fromCol, int toRow, int toCol) {
    int rowDistance = Math.abs(toRow - fromRow);
    int colDistance = Math.abs(toCol - fromCol);

    // King can move one square in any direction
    if (rowDistance <= 1 && colDistance <= 1) {
      // Check if the destination cell is empty or occupied by an opponent's piece
      if (cell[toRow][toCol].isEmpty()
          || cell[toRow][toCol].getPieceColor() != cell[fromRow][fromCol].getPieceColor()) {
        return true;
      }
    }

    return false;
  }

  public Boolean isQueenMoveAllowed(Cell[][] cell, int fromRow, int fromCol, int toRow, int toCol) {
    int rowDistance = Math.abs(toRow - fromRow);
    int colDistance = Math.abs(toCol - fromCol);

    // Queen can move horizontally, vertically, or diagonally
    if ((fromRow == toRow || fromCol == toCol) || (rowDistance == colDistance)) {
      // Check if the path is clear for the move
      int rowStep = (toRow > fromRow) ? 1 : (toRow < fromRow) ? -1 : 0;
      int colStep = (toCol > fromCol) ? 1 : (toCol < fromCol) ? -1 : 0;

      for (int row = fromRow + rowStep, col = fromCol + colStep;
          row != toRow || col != toCol;
          row += rowStep, col += colStep) {
        if (!cell[row][col].isEmpty()) {
          return false; // Path is blocked
        }
      }

      // Check if the destination cell is empty or occupied by an opponent's piece
      if (cell[toRow][toCol].isEmpty()
          || cell[toRow][toCol].getPieceColor() != cell[fromRow][fromCol].getPieceColor()) {
        return true;
      }
    }

    return false;
  }
  // hardcoded numbers bad should be altered
  public void showAllpossibleMoves(Cell[][] cell, int fromRow, int fromCol){
    for(int j =0;j<8;j++) {
      for (int i = 0; i < 8; i++) {
        if( isMoveAllowed(cell, fromRow, fromCol, i, j)){
          cell[i][j].setRectangleFill(Color.LIGHTYELLOW);
        }
      }
    }
  }

}
