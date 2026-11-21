import java.awt.*;
import javax.swing.*;

public class ChessGameUI extends JFrame {

    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_SIZE = 80;
    private static final int ICON_SIZE = 68;
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color SELECTED_COLOR = new Color(246, 246, 105);
    private static final Color LEGAL_MOVE_COLOR = new Color(130, 186, 120);

    private JPanel boardPanel;
    private JButton[][] squares;
    private JLabel statusLabel;
    private ChessGameEngine gameEngine;
    private int selectedRow = -1;
    private int selectedCol = -1;

    public ChessGameUI() {
        gameEngine = new ChessGameEngine();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the board panel
        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        squares = new JButton[BOARD_SIZE][BOARD_SIZE];

        // Initialize the squares
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                squares[row][col] = new JButton();
                squares[row][col].setPreferredSize(
                    new Dimension(SQUARE_SIZE, SQUARE_SIZE)
                );
                squares[row][col].setFocusPainted(false);
                squares[row][col].setBorderPainted(false);
                squares[row][col].setOpaque(true);
                squares[row][col].setBackground(getBaseSquareColor(row, col));
                final int finalRow = row;
                final int finalCol = col;
                squares[row][col].addActionListener(e ->
                    handleSquareClick(finalRow, finalCol)
                );
                boardPanel.add(squares[row][col]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        // Add status panel
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("White's turn");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        // Update the board display
        updateBoardDisplay();

        pack();
        setLocationRelativeTo(null);
    }

    private Color getBaseSquareColor(int row, int col) {
        return (row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE;
    }

    private void resetSquareColors() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                squares[row][col].setBackground(getBaseSquareColor(row, col));
            }
        }
    }

    private void selectSquare(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        resetSquareColors();
        squares[row][col].setBackground(SELECTED_COLOR);

        // Highlight legal destination squares
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (gameEngine.isLegalMove(row, col, r, c)) {
                    squares[r][c].setBackground(LEGAL_MOVE_COLOR);
                }
            }
        }
    }

    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        resetSquareColors();
    }

    private void handleSquareClick(int row, int col) {
        if (gameEngine.isGameOver()) {
            return;
        }

        char clickedPiece = gameEngine.getBoard()[row][col];
        boolean clickedOwnPiece =
            clickedPiece != ' ' &&
            Character.isUpperCase(clickedPiece) == gameEngine.isWhiteTurn();

        if (selectedRow == -1) {
            if (clickedOwnPiece) {
                selectSquare(row, col);
            }
        } else {
            // Clicking the same square deselects
            if (selectedRow == row && selectedCol == col) {
                clearSelection();
                return;
            }

            // Clicking another of your own pieces switches selection
            if (clickedOwnPiece) {
                selectSquare(row, col);
                return;
            }

            // Attempt move
            char promotionChoice = 'q';
            if (
                gameEngine.isLegalMove(selectedRow, selectedCol, row, col) &&
                gameEngine.isPromotionMove(selectedRow, selectedCol, row, col)
            ) {
                promotionChoice = askPromotionPiece();
            }

            boolean moveSuccess = gameEngine.makeMove(
                selectedRow,
                selectedCol,
                row,
                col,
                promotionChoice
            );

            clearSelection();

            if (moveSuccess) {
                updateBoardDisplay();
                updateStatus();
            }
        }
    }

    private char askPromotionPiece() {
        String[] options = { "Queen", "Rook", "Bishop", "Knight" };
        int choice = JOptionPane.showOptionDialog(
            this,
            "Choose promotion piece:",
            "Pawn Promotion",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
        switch (choice) {
            case 1:
                return 'r';
            case 2:
                return 'b';
            case 3:
                return 'n';
            default:
                return 'q';
        }
    }

    private void updateStatus() {
        String player = gameEngine.isWhiteTurn() ? "White" : "Black";
        ChessGameEngine.GameStatus status = gameEngine.getGameStatus();
        switch (status) {
            case CHECK:
                statusLabel.setText(player + "'s turn — CHECK!");
                break;
            case CHECKMATE:
                String winner = gameEngine.isWhiteTurn() ? "Black" : "White";
                statusLabel.setText("Checkmate! " + winner + " wins.");
                JOptionPane.showMessageDialog(
                    this,
                    "Checkmate! " + winner + " wins."
                );
                break;
            case STALEMATE:
                statusLabel.setText("Draw by Stalemate.");
                JOptionPane.showMessageDialog(this, "Draw by Stalemate!");
                break;
            case DRAW_FIFTY_MOVE:
                statusLabel.setText("Draw by 50-move rule.");
                JOptionPane.showMessageDialog(this, "Draw by 50-move rule!");
                break;
            case DRAW_INSUFFICIENT_MATERIAL:
                statusLabel.setText("Draw by insufficient material.");
                JOptionPane.showMessageDialog(
                    this,
                    "Draw by insufficient material!"
                );
                break;
            default:
                statusLabel.setText(player + "'s turn");
                break;
        }
    }

    private void updateBoardDisplay() {
        char[][] board = gameEngine.getBoard();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                squares[row][col].setText("");
                squares[row][col].setIcon(
                    ChessPieceIcons.getIcon(board[row][col], ICON_SIZE)
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGameUI ui = new ChessGameUI();
            ui.setVisible(true);
        });
    }
}
