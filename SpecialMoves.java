public class SpecialMoves {

    public static void handleCastling(
        char[][] board,
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        boolean isKingSide = endCol > startCol;
        int rookStartCol = isKingSide ? 7 : 0;
        int rookEndCol = isKingSide ? endCol - 1 : endCol + 1;
        char rook = board[startRow][rookStartCol];

        board[startRow][rookStartCol] = ' ';
        board[startRow][rookEndCol] = rook;
    }

    public static void undoCastling(
        char[][] board,
        int startRow,
        int startCol,
        int endCol
    ) {
        boolean isKingSide = endCol > startCol;
        int rookStartCol = isKingSide ? 7 : 0;
        int rookEndCol = isKingSide ? endCol - 1 : endCol + 1;
        char rook = board[startRow][rookEndCol];

        board[startRow][rookStartCol] = rook;
        board[startRow][rookEndCol] = ' ';
    }

    public static void handlePromotion(
        char[][] board,
        int endRow,
        int endCol,
        char promotionChoice
    ) {
        boolean isWhite = board[endRow][endCol] == 'P';
        char promo = Character.toLowerCase(promotionChoice);
        if (promo != 'q' && promo != 'r' && promo != 'b' && promo != 'n') {
            promo = 'q';
        }
        board[endRow][endCol] = isWhite ? Character.toUpperCase(promo) : promo;
    }

    public static void handleEnPassant(
        char[][] board,
        int startRow,
        int endRow,
        int endCol
    ) {
        board[startRow][endCol] = ' ';
    }
}
