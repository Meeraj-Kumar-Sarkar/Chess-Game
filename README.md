# ♟️ Chess Game

A fully functional two-player Chess game built in Java using **Swing** for the graphical user interface. The game implements standard chess rules including all piece movements, special moves, check/checkmate/stalemate detection, draw rules, legal move highlighting, and traditional chess piece icons.

---

## 📋 Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Class Overview](#class-overview)
- [How to Run](#how-to-run)
- [How to Play](#how-to-play)
- [Chess Pieces & Internal Notation](#chess-pieces--internal-notation)
- [Special Moves & Draw Rules](#special-moves--draw-rules)
- [Known Limitations](#known-limitations)
- [Future Improvements](#future-improvements)

---

## ✨ Features

- ✅ Two-player local gameplay (White vs Black)
- ✅ Traditional rendered chess piece icons (`♔ ♕ ♖ ♗ ♘ ♙` / `♚ ♛ ♜ ♝ ♞ ♟`) on a classic wood-toned board
- ✅ Full piece movement validation for all 6 piece types
- ✅ Accurate check detection (`isSquareAttacked` / `isInCheck`) — illegal moves that leave your king in check are blocked and cleanly reverted
- ✅ **Castling** (kingside and queenside) with full rights tracking, path checks, and attack-square validation
- ✅ **En passant** capture with single-turn target square tracking
- ✅ **Pawn promotion** with interactive piece-selection dialog (Queen, Rook, Bishop, or Knight)
- ✅ **Checkmate, Stalemate, and Draw detection** (50-move rule & insufficient material) with live status bar and end-of-game popups
- ✅ Interactive Swing GUI with click-to-move, piece re-selection, and legal-move highlighting

---

## 🗂️ Project Structure

```
Chess Game/
├── ChessGameUI.java        # Swing GUI, board rendering, promotion & game-over dialogs
├── ChessPieceIcons.java    # Anti-aliased traditional chess piece icon renderer & cache
├── ChessGameEngine.java    # Core game logic, state management, attack & game-status checks
├── PieceValidation.java    # Pseudo-legal movement rules for each piece type
├── SpecialMoves.java       # Board mutation helpers for castling, en passant & promotion
├── *.class                 # Compiled bytecode files
└── README.md               # Project documentation
```

---

## 🏗️ Class Overview

### `ChessGameEngine`
The heart of the application. Manages board state, turn order, special-move state, attack detection, and game-over conditions.

| Responsibility | Details |
|---|---|
| Board initialization | Sets up the standard 8×8 chess starting position |
| Move execution & undo | Validates moves, applies special moves, and fully reverts board state on self-check |
| Attack & check detection | `isSquareAttacked` and `isInCheck` evaluate threats without turn-order conflicts |
| Legal move validation | `isLegalMove` and `hasAnyLegalMove` power UI highlighting and game-over checks |
| Castling & en passant state | Tracks `castlingRights` (king/rook moves & rook captures) and `enPassantSquare` |
| Game status & draws | Exposes `GameStatus` (`IN_PROGRESS`, `CHECK`, `CHECKMATE`, `STALEMATE`, `DRAW_FIFTY_MOVE`, `DRAW_INSUFFICIENT_MATERIAL`) |

**Key fields:**
- `board` — 8×8 `char` array (uppercase = White, lowercase = Black, `' '` = empty)
- `isWhiteTurn` — tracks whose turn it is
- `castlingRights` — tracks `[whiteKingSide, whiteQueenSide, blackKingSide, blackQueenSide]`
- `enPassantSquare` — `[row, col]` eligible for en passant capture on the immediately following turn
- `halfMoveClock` — counts half-moves since the last pawn move or capture (50-move draw rule)

---

### `ChessGameUI`
Builds and manages the Swing GUI and handles all user interaction.

| Responsibility | Details |
|---|---|
| Board rendering | 8×8 grid of `JButton` squares styled with classic wood colors |
| Piece display | Renders cached traditional piece icons via `ChessPieceIcons` |
| Click handling | Select a piece, switch to another own piece, deselect on re-click, or click a destination to move |
| Visual feedback | Highlights the selected piece in yellow and all legal destination squares in green |
| Promotion & status | Displays a promotion choice dialog on back-rank pawn arrival, updates the live status bar, and announces checkmate/stalemate/draws |

**Entry point:** `main()` — launches the application via `SwingUtilities.invokeLater`.

---

### `ChessPieceIcons`
Generates and caches traditional chess piece `ImageIcon`s using Java2D (`Graphics2D` + `GlyphVector`).

| Responsibility | Details |
|---|---|
| Font fallback | Automatically selects a system font with chess glyphs (`Segoe UI Symbol`, `DejaVu Sans`, `Noto Sans Symbols 2`, `Apple Symbols`, etc.) |
| High-contrast styling | Ivory fill with dark outline for White pieces; dark fill with subtle light rim for Black pieces; soft drop shadow on both |
| Caching | Renders each piece once per icon size and reuses it from an in-memory map |

---

### `PieceValidation`
A utility class containing **static** methods for pseudo-legal move validation for each piece type.

| Method | Piece | Rules Enforced |
|---|---|---|
| `validatePawnMove` | Pawn (`P`/`p`) | Forward 1-square, initial 2-square advance, diagonal capture, and en passant capture |
| `validateRookMove` | Rook (`R`/`r`) | Horizontal/vertical movement, clear path required |
| `validateKnightMove` | Knight (`N`/`n`) | L-shaped moves (2+1 squares), can jump over pieces |
| `validateBishopMove` | Bishop (`B`/`b`) | Diagonal movement, clear path required |
| `validateQueenMove` | Queen (`Q`/`q`) | Combined rook + bishop movement |
| `validateKingMove` | King (`K`/`k`) | One square in any direction (castling is validated in `ChessGameEngine`) |
| `isPathClear` | (shared) | Verifies no pieces block sliding-piece paths and prevents capturing own pieces |

---

### `SpecialMoves`
A focused utility class for applying (and reverting) special move board mutations.

| Method | Description |
|---|---|
| `handleCastling` | Moves the rook to the correct side of the king during castling |
| `undoCastling` | Restores the rook if a castling move is rolled back |
| `handlePromotion` | Replaces the promoted pawn with the player's chosen piece (`Q`, `R`, `B`, or `N`) |
| `handleEnPassant` | Removes the captured pawn from the board during en passant |

---

## 🚀 How to Run

### Prerequisites
- Java Development Kit (JDK) **8 or higher**
- A terminal or command prompt

### Compile

Navigate to the project directory and compile all `.java` files:

```bash
javac *.java
```

Or list them explicitly:

```bash
javac ChessGameUI.java ChessPieceIcons.java ChessGameEngine.java PieceValidation.java SpecialMoves.java
```

### Run

```bash
java ChessGameUI
```

The game window will open automatically.

---

## 🎮 How to Play

1. **Start the game** — White always moves first.
2. **Select a piece** — Click on one of your pieces. It is highlighted in **yellow**, and all legal destination squares are highlighted in **green**.
3. **Move the piece** — Click any highlighted destination square.
   - To deselect, click the selected piece again.
   - To switch pieces, click another one of your own pieces directly.
4. **Promote pawns** — When a pawn reaches the opposite back rank, choose Queen, Rook, Bishop, or Knight from the popup dialog.
5. **End of game** — The status bar announces checks, and a dialog announces checkmate, stalemate, or draw when the game ends.

---

## ♟️ Chess Pieces & Internal Notation

The UI displays traditional chess piece icons, while the engine stores the board internally using single-character notation:

| Icon | Symbol | Piece | Color |
|------|--------|-------|-------|
| ♔ | `K` | King | White |
| ♕ | `Q` | Queen | White |
| ♖ | `R` | Rook | White |
| ♗ | `B` | Bishop | White |
| ♘ | `N` | Knight | White |
| ♙ | `P` | Pawn | White |
| ♚ | `k` | King | Black |
| ♛ | `q` | Queen | Black |
| ♜ | `r` | Rook | Black |
| ♝ | `b` | Bishop | Black |
| ♞ | `n` | Knight | Black |
| ♟ | `p` | Pawn | Black |

---

## 🎯 Special Moves & Draw Rules

### Castling
- Move the king **2 squares** toward either rook on its home rank.
- Allowed only if neither the king nor that rook has moved (or been captured), the path between them is clear, and the king is not in check, does not pass through an attacked square, and does not land in check.

### En Passant
- When a pawn advances 2 squares from its starting rank, an adjacent enemy pawn can capture it diagonally on the **immediately following turn** as if it had moved 1 square.

### Pawn Promotion
- When a pawn reaches the opposite back rank, a dialog lets you promote it to a **Queen**, **Rook**, **Bishop**, or **Knight**.

### Draw Conditions
- **Stalemate** — The player to move is not in check and has no legal moves.
- **50-Move Rule** — 50 full moves (100 half-moves) occur without any pawn move or capture.
- **Insufficient Material** — `K vs K`, `K+B vs K`, `K+N vs K`, or `K+B vs K+B` on same-colored squares.

---

## ⚠️ Known Limitations

- **No threefold repetition detection** — Repeating the same position three times is not yet tracked as a draw.
- **No move history / undo** — Moves cannot be undone or reviewed in a move list during a game.
- **No AI opponent** — Local two-player mode only (same machine).
- **No restart button** — Restarting requires relaunching the application.

---

## 🔮 Future Improvements

- [x] Checkmate and stalemate detection with end-game screen
- [x] Player-choice pawn promotion dialog
- [x] Proper castling rights tracking and attack-square checks
- [x] Traditional chess piece icons and legal-move highlighting
- [ ] Threefold repetition draw detection
- [ ] Move history panel, undo functionality, and New Game button
- [ ] AI opponent (minimax with alpha-beta pruning)
- [ ] Game timer / clock
- [ ] Save and load games (PGN / FEN support)
- [ ] Sound effects for moves, captures, and checks
- [ ] Piece drag-and-drop in addition to click-to-move

---

## 🛠️ Built With

- **Java** — Core programming language
- **Java Swing & Java2D** — GUI framework and vector piece icon rendering (`javax.swing`, `java.awt`)

---

*Built as a Java Swing desktop project.*
