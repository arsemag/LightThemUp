# LightEmAll Game

## Overview

This project implements the **LightEmAll** game, a puzzle where players need to rotate tiles to connect power stations across the board. The objective is to power all pieces by rotating the tiles to form a continuous path from the power station.

The game logic is implemented in the `LightEmAll` class, and the `GamePiece` class models each tile. The project also includes unit tests to verify game functionality and logic.

---

## Running the Game

To run the game, follow these steps:

1. Clone the repository.
2. Open the project in a Java IDE (e.g., IntelliJ IDEA or Eclipse).
3. Run the `LightEmAll` class to start the game.

## Class: `LightEmAll`

### Overview

The `LightEmAll` class controls the main logic of the game, including generating the game board, handling user input, and checking for win conditions. It interacts with the `GamePiece` class to manipulate the tiles and ensures that the game is played correctly.

### Methods

- **`void generateBoard()`**  
  Generates a random board with tiles in various orientations.

- **`void powerTile(int row, int col)`**  
  Powers the tile at the specified position (row, col) and updates its status.

- **`boolean userWon()`**  
  Checks if the player has won by ensuring all tiles are powered.

- **`WorldScene makeScene()`**  
  Creates and returns the game scene, including the game board and status messages such as time and steps.

- **`void rotateTile(int row, int col)`**  
  Rotates the tile at the specified position (row, col) to connect the power stations.

---

## Class: `GamePiece`

### Overview

The `GamePiece` class represents each individual tile on the game board. Each tile can be rotated to change its orientation and form connections to other tiles. 

### Fields

- **`boolean powered`**  
  Represents whether the tile is powered.

- **`boolean top, right, bottom, left`**  
  Represents whether the tile has power connections in the respective directions.

- **`boolean powerStation`**  
  Indicates whether the tile is a power station.

### Methods

- **`void rotate()`**  
  Rotates the tile by 90 degrees to change the connections.

- **`WorldImage tileImage(Color color)`**  
  Returns an image representing the tile with the specified color.

---

## Class: `Tester`

### Overview

The `Tester` class includes unit tests to verify that the key features of the game work as expected. These tests ensure correct functionality of the board generation, tile rotation, win conditions, and game logic.

### Example Tests

- **`testMakeScene()`**  
  Tests if the game scene is correctly generated with all game elements (board, time, steps).

- **`testGenerateBoard()`**  
  Verifies that the board is generated with the correct number of rows and columns.

- **`testUserWon()`**  
  Checks if the user wins when all tiles are powered.

- **`testRotate()`**  
  Verifies the functionality of rotating the tiles.

- **`testBFS()`**  
  Ensures that the BFS algorithm correctly propagates power through the tiles.

---



