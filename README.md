# ♟️ Chess Game

A fully functional two-player Chess game built in Java using **Swing** for the graphical user interface. The game implements standard chess rules including all piece movements, special moves, and check detection.

---

## 📋 Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Class Overview](#class-overview)
- [How to Run](#how-to-run)
- [How to Play](#how-to-play)
- [Chess Pieces & Notation](#chess-pieces--notation)
- [Special Moves](#special-moves)
- [Known Limitations](#known-limitations)
- [Future Improvements](#future-improvements)

---

## ✨ Features

- ✅ Two-player local gameplay (White vs Black)
- ✅ Full piece movement validation for all 6 piece types
- ✅ Check detection — illegal moves that leave your king in check are blocked
- ✅ **Castling** (kingside and queenside)
- ✅ **En passant** capture
- ✅ **Pawn promotion** (auto-promotes to Queen)
- ✅ Interactive Swing GUI with click-to-move interface
- ✅ Visual piece selection highlighting

---

## 🗂️ Project Structure

```
Chess Game/
├── ChessGameEngine.java    # Core game logic and state management
├── ChessGameUI.java        # Swing-based graphical user interface
├── PieceValidation.java    # Move validation for each piece type
├── SpecialMoves.java       # Handlers for castling, en passant, promotion & checkmate
├── *.class                 # Compiled bytecode files
└── README.md               # Project documentation
```

---

## 🏗️ Class Overview

### `ChessGameEngine`
The heart of the application. Manages the board state, turn order, and coordinates all game logic.

| Responsibility | Details |
|---|---|
| Board initialization | Sets up the standard 8×8 chess starting position |
| Move execution | Validates and applies moves, undoing them if they cause self-check |
| Check detection | Scans opponent pieces to see if the king is under attack |
| Special move dispatch | Delegates castling, en passant, and promotion to `SpecialMoves` |
| Turn management | Alternates between White and Black after each valid move |

**Key fields:**
- `board` — 8×8 `char` array (uppercase = White, lowercase = Black)
- `isWhiteTurn` — tracks whose turn it is
- `castlingRights` — tracks whether each side can still castle
- `enPassantSquare` — tracks the square eligible for en passant capture

---

### `ChessGameUI`
Builds and manages the Swing GUI. Handles all user interaction via mouse clicks.

| Responsibility | Details |
|---|---|
| Board rendering | 8×8 grid of `JButton` components representing squares |
| Click handling | Two-click selection model (select piece → select destination) |
| Visual feedback | Selected piece highlighted in yellow |
| Board refresh | Updates text labels on squares after every successful move |
| Status bar | Displays whose turn it is at the bottom of the window |

**Entry point:** `main()` method — launches the application via `SwingUtilities.invokeLater`.

---

### `PieceValidation`
A utility class containing **static** methods for validating moves for each piece type.

| Method | Piece | Rules Enforced |
|---|---|---|
| `validatePawnMove` | Pawn (P/p) | Forward movement, initial 2-square advance, diagonal capture |
| `validateRookMove` | Rook (R/r) | Horizontal/vertical movement, clear path required |
| `validateKnightMove` | Knight (N/n) | L-shaped moves (2+1 squares), can jump over pieces |
| `validateBishopMove` | Bishop (B/b) | Diagonal movement, clear path required |
| `validateQueenMove` | Queen (Q/q) | Combined rook + bishop movement |
| `validateKingMove` | King (K/k) | One square in any direction |
| `isPathClear` | (shared) | Checks no pieces block the path between two squares |

---

### `SpecialMoves`
Handles the three special chess moves and checkmate detection.

| Method | Description |
|---|---|
| `handleCastling` | Moves the rook to the correct side of the king during a castling move |
| `handlePromotion` | Automatically promotes a pawn reaching the back rank to a Queen |
| `handleEnPassant` | Removes the captured pawn from the board during en passant |
| `isCheckmate` | Determines if the current player has no legal moves out of check |
| `isInCheck` | Checks if the given king is under attack from any opponent piece |

---

## 🚀 How to Run

### Prerequisites
- Java Development Kit (JDK) **8 or higher**
- A terminal or command prompt

### Compile

Navigate to the project directory and run:

```bash
javac ChessGameUI.java ChessGameEngine.java PieceValidation.java SpecialMoves.java
```

Or simply compile all `.java` files at once:

```bash
javac *.java
```

### Run

```bash
java ChessGameUI
```

The game window will open automatically.

---

## 🎮 How to Play

1. **Start the game** — White always moves first.
2. **Select a piece** — Click on one of your pieces. It will be **highlighted in yellow**.
3. **Move the piece** — Click on a valid destination square.
   - If the move is valid, the piece moves and the turn passes to the other player.
   - If the move is invalid (e.g., leaves your king in check), nothing happens.
4. **Continue** — Take turns until the game concludes.

> **Note:** To deselect a piece, simply click on another one of your own pieces.

---

## ♟️ Chess Pieces & Notation

The board uses single-character notation:

| Symbol | Piece | Color |
|--------|-------|-------|
| `K` | King | White |
| `Q` | Queen | White |
| `R` | Rook | White |
| `B` | Bishop | White |
| `N` | Knight | White |
| `P` | Pawn | White |
| `k` | King | Black |
| `q` | Queen | Black |
| `r` | Rook | Black |
| `b` | Bishop | Black |
| `n` | Knight | Black |
| `p` | Pawn | Black |

---

## 🎯 Special Moves

### Castling
- The king moves **2 squares** toward a rook.
- The rook automatically jumps to the other side of the king.
- Both kingside (short) and queenside (long) castling are supported.

### En Passant
- If a pawn advances two squares from its starting position, an adjacent enemy pawn may capture it **as if** it had only moved one square.
- This capture must be made immediately on the next turn.

### Pawn Promotion
- When a pawn reaches the **opposite end of the board**, it is automatically promoted to a **Queen**.

---

## ⚠️ Known Limitations

- **No checkmate announcement** — The game does not display a win screen or notify players when checkmate occurs.
- **No stalemate detection** — Stalemate (a draw condition) is not currently detected.
- **Castling rights not fully tracked** — The engine does not prevent castling after the king or rook has previously moved.
- **Pawn promotion is fixed** — Promotion always results in a Queen; the player cannot choose another piece.
- **No move history / undo** — There is no option to undo a move or view the game history.
- **No AI opponent** — The game is two-player only (local, same machine).

---

## 🔮 Future Improvements

- [ ] Checkmate and stalemate detection with end-game screen
- [ ] Move history panel and undo functionality
- [ ] Player-choice pawn promotion dialog
- [ ] Proper castling rights tracking (disable after king/rook moves)
- [ ] AI opponent (minimax with alpha-beta pruning)
- [ ] Game timer / clock
- [ ] Save and load game (PGN format support)
- [ ] Sound effects for moves and captures
- [ ] Piece drag-and-drop in addition to click-to-move

---

## 🛠️ Built With

- **Java** — Core programming language
- **Java Swing** — GUI framework (`javax.swing`, `java.awt`)
- **Java AWT Events** — Mouse/click event handling

---

*Built as a Java Swing desktop project.*
