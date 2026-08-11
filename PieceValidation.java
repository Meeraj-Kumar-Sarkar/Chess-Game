public class PieceValidation {

    // Helper method to check if a piece's path is clear (for pieces that move in straight lines)
    public static boolean isPathClear(
        char[][] board,
        boolean isWhite,
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        int rowStep = Integer.compare(endRow, startRow);
        int colStep = Integer.compare(endCol, startCol);

        int currentRow = startRow + rowStep;
        int currentCol = startCol + colStep;

        char targetPiece = board[endRow][endCol];

        while (currentRow != endRow || currentCol != endCol) {
            if (board[currentRow][currentCol] != ' ') {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        if (
            targetPiece != ' ' && Character.isUpperCase(targetPiece) != isWhite
        ) return true;
        else if (
            targetPiece != ' ' && Character.isUpperCase(targetPiece) == isWhite
        ) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean validatePawnMove(
        char[][] board,
        char piece,
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        boolean isWhite = Character.isUpperCase(piece);
        int direction = isWhite ? -1 : 1; // White pawns move up (-1), black pawns move down (+1)
        int startRank = isWhite ? 6 : 1; // Starting rank for pawns

        // Normal one-square move
        if (
            startCol == endCol &&
            endRow == startRow + direction &&
            board[endRow][endCol] == ' '
        ) {
            return true;
        }

        // Initial two-square move
        if (
            startRow == startRank &&
            startCol == endCol &&
            endRow == startRow + 2 * direction &&
            board[endRow][endCol] == ' ' &&
            board[startRow + direction][startCol] == ' '
        ) {
            return true;
        }

        // Capture move (diagonal)
        if (
            Math.abs(endCol - startCol) == 1 && endRow == startRow + direction
        ) {
            char targetPiece = board[endRow][endCol];
            if (
                targetPiece != ' ' &&
                Character.isUpperCase(targetPiece) != isWhite
            ) {
                return true;
            }
        }

        return false;
    }

    public static boolean validateRookMove(
        char[][] board,
        char piece,
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        boolean isWhite = Character.isUpperCase(piece);
        // Rook moves horizontally or vertically
        if (startRow != endRow && startCol != endCol) {
            return false;
        }

        return isPathClear(board, isWhite, startRow, startCol, endRow, endCol);
    }

    public static boolean validateKnightMove(
        char[][] board,
        char piece,
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        boolean isWhite = Character.isUpperCase(piece);
        // Knight moves in L-shape: 2 squares in one direction and 1 square perpendicular
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            char targetPiece = board[endRow][endCol];
            if (
                targetPiece != ' ' &&
                Character.isUpperCase(targetPiece) != isWhite
            ) {
                return true;
            } else if (
                targetPiece != ' ' &&
                Character.isUpperCase(targetPiece) == isWhite
            ) {
                return false;
            } else {
                return true;
            }
        } else return false;
    }

    public static boolean validateBishopMove(
        char[][] board,
        char piece,
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        boolean isWhite = Character.isUpperCase(piece);
        // Bishop moves diagonally
        if (Math.abs(endRow - startRow) != Math.abs(endCol - startCol)) {
            return false;
        }

        return isPathClear(board, isWhite, startRow, startCol, endRow, endCol);
    }

    public static boolean validateQueenMove(
        char[][] board,
        char piece,
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        // Queen combines rook and bishop movements
        if (startRow == endRow || startCol == endCol) {
            return validateRookMove(
                board,
                piece,
                startRow,
                startCol,
                endRow,
                endCol
            );
        } else if (Math.abs(endRow - startRow) == Math.abs(endCol - startCol)) {
            return validateBishopMove(
                board,
                piece,
                startRow,
                startCol,
                endRow,
                endCol
            );
        }
        return false;
    }

    public static boolean validateKingMove(
        char[][] board,
        char piece,
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        boolean isWhite = Character.isUpperCase(piece);
        // King moves one square in any direction
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        char targetPiece = board[endRow][endCol];

        if (rowDiff <= 1 && colDiff <= 1) {
            if (
                targetPiece != ' ' &&
                Character.isUpperCase(targetPiece) != isWhite
            ) return true;
            else if (
                targetPiece != ' ' &&
                Character.isUpperCase(targetPiece) == isWhite
            ) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
