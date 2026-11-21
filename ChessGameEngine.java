import java.util.*;

public class ChessGameEngine {

    public enum GameStatus {
        IN_PROGRESS,
        CHECK,
        CHECKMATE,
        STALEMATE,
        DRAW_FIFTY_MOVE,
        DRAW_INSUFFICIENT_MATERIAL
    }

    private char[][] board;
    private boolean isWhiteTurn;
    private boolean[] castlingRights; // [whiteKingSide, whiteQueenSide, blackKingSide, blackQueenSide]
    private int[] enPassantSquare; // [row, col] of square where en passant capture is possible
    private int halfMoveClock; // Half-moves since last pawn move or capture (for 50-move rule)

    public ChessGameEngine() {
        initializeBoard();
        isWhiteTurn = true;
        castlingRights = new boolean[] { true, true, true, true };
        enPassantSquare = null;
        halfMoveClock = 0;
    }

    private void initializeBoard() {
        board = new char[8][8];
        // Black pieces
        board[0] = new char[] { 'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r' };
        Arrays.fill(board[1], 'p');
        // Empty squares
        for (int i = 2; i < 6; i++) {
            Arrays.fill(board[i], ' ');
        }
        // White pieces
        Arrays.fill(board[6], 'P');
        board[7] = new char[] { 'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R' };
    }

    public boolean makeMove(
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        return makeMove(startRow, startCol, endRow, endCol, 'q');
    }

    public boolean makeMove(
        int startRow,
        int startCol,
        int endRow,
        int endCol,
        char promotionChoice
    ) {
        if (isGameOver()) {
            return false;
        }
        if (!isPseudoLegalMove(startRow, startCol, endRow, endCol)) {
            return false;
        }

        char piece = board[startRow][startCol];
        char capturedPiece = board[endRow][endCol];
        boolean isPawn = Character.toLowerCase(piece) == 'p';
        boolean isKing = Character.toLowerCase(piece) == 'k';

        boolean isEnPassant = isPawn && startCol != endCol && capturedPiece == ' ';
        boolean isCastling = isKing && Math.abs(endCol - startCol) == 2;
        boolean isPromotion = isPawn && (endRow == 0 || endRow == 7);

        // Apply move
        board[endRow][endCol] = piece;
        board[startRow][startCol] = ' ';

        char enPassantCaptured = ' ';
        if (isEnPassant) {
            enPassantCaptured = board[startRow][endCol];
            SpecialMoves.handleEnPassant(board, startRow, endRow, endCol);
        } else if (isCastling) {
            SpecialMoves.handleCastling(board, startRow, startCol, endRow, endCol);
        } else if (isPromotion) {
            SpecialMoves.handlePromotion(board, endRow, endCol, promotionChoice);
        }

        // Check if the move leaves the current player's king in check
        if (isInCheck(isWhiteTurn)) {
            // Undo everything cleanly
            board[startRow][startCol] = piece;
            board[endRow][endCol] = capturedPiece;
            if (isEnPassant) {
                board[startRow][endCol] = enPassantCaptured;
            } else if (isCastling) {
                SpecialMoves.undoCastling(board, startRow, startCol, endCol);
            }
            return false;
        }

        // Update castling rights
        updateCastlingRights(piece, startRow, startCol, endRow, endCol);

        // Update en passant target square (valid for the next turn only)
        if (isPawn && Math.abs(endRow - startRow) == 2) {
            enPassantSquare = new int[] { (startRow + endRow) / 2, startCol };
        } else {
            enPassantSquare = null;
        }

        // Update 50-move rule clock
        if (isPawn || capturedPiece != ' ' || isEnPassant) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }

        isWhiteTurn = !isWhiteTurn;
        return true;
    }

    public boolean isPromotionMove(
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        if (!isValidPosition(startRow, startCol) || !isValidPosition(endRow, endCol)) {
            return false;
        }
        char piece = board[startRow][startCol];
        return Character.toLowerCase(piece) == 'p' && (endRow == 0 || endRow == 7);
    }

    public boolean isLegalMove(
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        if (!isPseudoLegalMove(startRow, startCol, endRow, endCol)) {
            return false;
        }

        char piece = board[startRow][startCol];
        char captured = board[endRow][endCol];
        boolean isEnPassant =
            Character.toLowerCase(piece) == 'p' &&
            startCol != endCol &&
            captured == ' ';
        char epCaptured = isEnPassant ? board[startRow][endCol] : ' ';

        board[endRow][endCol] = piece;
        board[startRow][startCol] = ' ';
        if (isEnPassant) {
            board[startRow][endCol] = ' ';
        }

        boolean safe = !isInCheck(isWhiteTurn);

        board[startRow][startCol] = piece;
        board[endRow][endCol] = captured;
        if (isEnPassant) {
            board[startRow][endCol] = epCaptured;
        }

        return safe;
    }

    private boolean isPseudoLegalMove(
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        if (
            !isValidPosition(startRow, startCol) ||
            !isValidPosition(endRow, endCol)
        ) {
            return false;
        }
        if (startRow == endRow && startCol == endCol) {
            return false;
        }

        char piece = board[startRow][startCol];
        if (piece == ' ') {
            return false;
        }

        boolean isWhitePiece = Character.isUpperCase(piece);
        if (isWhiteTurn != isWhitePiece) {
            return false;
        }

        return validatePieceMove(piece, startRow, startCol, endRow, endCol);
    }

    private boolean validatePieceMove(
        char piece,
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        switch (Character.toLowerCase(piece)) {
            case 'p':
                return PieceValidation.validatePawnMove(
                    board,
                    piece,
                    startRow,
                    startCol,
                    endRow,
                    endCol,
                    enPassantSquare
                );
            case 'r':
                return PieceValidation.validateRookMove(
                    board,
                    piece,
                    startRow,
                    startCol,
                    endRow,
                    endCol
                );
            case 'n':
                return PieceValidation.validateKnightMove(
                    board,
                    piece,
                    startRow,
                    startCol,
                    endRow,
                    endCol
                );
            case 'b':
                return PieceValidation.validateBishopMove(
                    board,
                    piece,
                    startRow,
                    startCol,
                    endRow,
                    endCol
                );
            case 'q':
                return PieceValidation.validateQueenMove(
                    board,
                    piece,
                    startRow,
                    startCol,
                    endRow,
                    endCol
                );
            case 'k':
                return validateKingMove(
                    piece,
                    startRow,
                    startCol,
                    endRow,
                    endCol
                );
            default:
                return false;
        }
    }

    private boolean validateKingMove(
        char piece,
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        if (
            PieceValidation.validateKingMove(
                board,
                piece,
                startRow,
                startCol,
                endRow,
                endCol
            )
        ) {
            return true;
        }

        // Castling: king moves two squares horizontally on its home rank
        boolean isWhite = Character.isUpperCase(piece);
        int homeRow = isWhite ? 7 : 0;
        if (
            startRow == homeRow &&
            startCol == 4 &&
            endRow == homeRow &&
            Math.abs(endCol - startCol) == 2
        ) {
            return canCastle(isWhite, endCol > startCol);
        }

        return false;
    }

    private boolean canCastle(boolean isWhite, boolean kingSide) {
        int row = isWhite ? 7 : 0;
        int rightIndex = (isWhite ? 0 : 2) + (kingSide ? 0 : 1);
        if (!castlingRights[rightIndex]) {
            return false;
        }

        char expectedRook = isWhite ? 'R' : 'r';
        int rookCol = kingSide ? 7 : 0;
        if (board[row][rookCol] != expectedRook) {
            return false;
        }

        // Path between king and rook must be empty
        int step = kingSide ? 1 : -1;
        for (int c = 4 + step; c != rookCol; c += step) {
            if (board[row][c] != ' ') {
                return false;
            }
        }

        // King cannot be in check, pass through check, or land in check
        boolean opponentIsWhite = !isWhite;
        if (isSquareAttacked(row, 4, opponentIsWhite)) return false;
        if (isSquareAttacked(row, 4 + step, opponentIsWhite)) return false;
        if (isSquareAttacked(row, 4 + 2 * step, opponentIsWhite)) return false;

        return true;
    }

    private void updateCastlingRights(
        char piece,
        int startRow,
        int startCol,
        int endRow,
        int endCol
    ) {
        if (piece == 'K') {
            castlingRights[0] = false;
            castlingRights[1] = false;
        }
        if (piece == 'k') {
            castlingRights[2] = false;
            castlingRights[3] = false;
        }

        // Rook moved from starting square
        if (startRow == 7 && startCol == 7) castlingRights[0] = false;
        if (startRow == 7 && startCol == 0) castlingRights[1] = false;
        if (startRow == 0 && startCol == 7) castlingRights[2] = false;
        if (startRow == 0 && startCol == 0) castlingRights[3] = false;

        // Rook captured on starting square
        if (endRow == 7 && endCol == 7) castlingRights[0] = false;
        if (endRow == 7 && endCol == 0) castlingRights[1] = false;
        if (endRow == 0 && endCol == 7) castlingRights[2] = false;
        if (endRow == 0 && endCol == 0) castlingRights[3] = false;
    }

    public boolean isSquareAttacked(int targetRow, int targetCol, boolean byWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                char piece = board[r][c];
                if (piece == ' ' || Character.isUpperCase(piece) != byWhite) {
                    continue;
                }
                char type = Character.toLowerCase(piece);
                if (type == 'p') {
                    int dir = byWhite ? -1 : 1;
                    if (r + dir == targetRow && Math.abs(c - targetCol) == 1) {
                        return true;
                    }
                } else if (type == 'k') {
                    if (
                        Math.abs(r - targetRow) <= 1 &&
                        Math.abs(c - targetCol) <= 1 &&
                        !(r == targetRow && c == targetCol)
                    ) {
                        return true;
                    }
                } else if (validatePieceMove(piece, r, c, targetRow, targetCol)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInCheck(boolean isWhiteKing) {
        int[] kingPos = findKing(isWhiteKing);
        if (kingPos == null) return false;
        return isSquareAttacked(kingPos[0], kingPos[1], !isWhiteKing);
    }

    public boolean hasAnyLegalMove() {
        for (int sr = 0; sr < 8; sr++) {
            for (int sc = 0; sc < 8; sc++) {
                char piece = board[sr][sc];
                if (piece == ' ' || Character.isUpperCase(piece) != isWhiteTurn) {
                    continue;
                }
                for (int er = 0; er < 8; er++) {
                    for (int ec = 0; ec < 8; ec++) {
                        if (isLegalMove(sr, sc, er, ec)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isInsufficientMaterial() {
        List<Character> minors = new ArrayList<>();
        List<Integer> bishopColors = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                char p = Character.toLowerCase(board[r][c]);
                if (p == ' ' || p == 'k') continue;
                if (p == 'p' || p == 'r' || p == 'q') return false;
                minors.add(p);
                if (p == 'b') {
                    bishopColors.add((r + c) % 2);
                }
            }
        }
        if (minors.size() <= 1) {
            return true; // K vs K, K+B vs K, K+N vs K
        }
        // K+B vs K+B on same-colored squares
        if (
            minors.size() == 2 &&
            bishopColors.size() == 2 &&
            bishopColors.get(0).equals(bishopColors.get(1))
        ) {
            return true;
        }
        return false;
    }

    public GameStatus getGameStatus() {
        boolean inCheck = isInCheck(isWhiteTurn);
        if (!hasAnyLegalMove()) {
            return inCheck ? GameStatus.CHECKMATE : GameStatus.STALEMATE;
        }
        if (halfMoveClock >= 100) {
            return GameStatus.DRAW_FIFTY_MOVE;
        }
        if (isInsufficientMaterial()) {
            return GameStatus.DRAW_INSUFFICIENT_MATERIAL;
        }
        return inCheck ? GameStatus.CHECK : GameStatus.IN_PROGRESS;
    }

    public boolean isGameOver() {
        GameStatus status = getGameStatus();
        return (
            status == GameStatus.CHECKMATE ||
            status == GameStatus.STALEMATE ||
            status == GameStatus.DRAW_FIFTY_MOVE ||
            status == GameStatus.DRAW_INSUFFICIENT_MATERIAL
        );
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private int[] findKing(boolean isWhiteKing) {
        char kingChar = isWhiteKing ? 'K' : 'k';
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == kingChar) {
                    return new int[] { i, j };
                }
            }
        }
        return null;
    }

    public char[][] getBoard() {
        return board;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }
}
