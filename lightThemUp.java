import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;



import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

class LightEmAll extends World {

  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;
  // the width and height of the board
  int width;
  int height;

  int powerRow;// the current location of the power station,
  // as well as its effective radius
  int powerCol;
  int radius;

  int size = 100;

  Random rand;
  int tickCount; // extra credit 
  boolean lost; 
  int steps; // extra credit 
  int score; 

  // intializeign all the feilds 
  LightEmAll(ArrayList<ArrayList<GamePiece>> board, ArrayList<GamePiece> nodes, ArrayList<Edge> mst,
      int width, int height, int powerRow, int powerCol, int radius) {
    this.board = board;
    this.nodes = nodes;
    this.mst = mst;
    this.width = width;
    this.height = height;
    this.powerCol = powerCol; // WHER THE POWER 5HING IS
    this.powerRow = powerRow; // WHERE THE POWER THING IS
    this.radius = radius; // how the power is tranmitting

  }

  // used for part1 to manual gnerate the board 
  LightEmAll(ArrayList<ArrayList<GamePiece>> board, ArrayList<GamePiece> nodes, int width,
      int height) {

    this.width = width;
    this.height = height;
    this.nodes = nodes; 
    this.rand = new Random();



    this.board = new ArrayList<ArrayList<GamePiece>>(); 


    addPieces();
    this.board = generateBoard();
    randomize(nodes);
    this.powerRow = Math.round(height / 2);

    this.powerCol = Math.round(width / 2);

    this.board.get(this.powerCol).get(this.powerRow).powered = true;

    this.board.get(this.powerCol).get(this.powerRow).powerStation = true;
    bfs();
  }

  // used for part2 implementing the algorithim to make the game
  LightEmAll(int width, int height, Random rand) {
    Utils u = new Utils(); 
    this.width = width;
    this.height = height;
    this.rand = rand;


    this.blankBoard();

    this.powerCol = 0;
    this.powerRow = 0;
    this.nodes = u.flatten(this.board);

    bfs();

    findMST();
    this.randomize(nodes);
    this.score = 0; 
    this.lost = false; 



  }


  // a method to restart the game by 
  public void resetGame() {
    Utils u = new Utils();

    this.blankBoard();

    this.powerCol = 0;
    this.powerRow = 0;
    this.nodes = u.flatten(this.board);

    bfs();

    findMST();
    this.randomize(nodes);
    this.lost = false; 

    this.tickCount = 0;
    this.steps = 0;
    this.score = 0;
  }






  // EFFECT: initializes the board to to have no connections
  void blankBoard() {
    this.board = new ArrayList<>();

    for (int i = 0; i < this.width; i++) {
      ArrayList<GamePiece> col = new ArrayList<>();

      for (int j = 0; j < this.height; j++) {
        col.add(new GamePiece(j, i, false, false, false, false, false, false));
      }

      this.board.add(col);
    }
    this.board.get(0).get(0).powerStation = true;
  }



  // EFFECT: adds pieces on to the board to then be generated
  public void addPieces() {
    for (int i = 0; i < this.width; i++) {
      ArrayList<GamePiece> row = new ArrayList<>(); 
      for (int j = 0; j < this.height; j++) {
        row.add(new GamePiece(j, i)); 
      }
      board.add(row);
    }
  }


  //  Manually generating a board 
  public ArrayList<ArrayList<GamePiece>> generateBoard() {
    // Iterate over each row
    for (int col = 0; col < this.width; col++) {
      ArrayList<GamePiece> column = new ArrayList<>(); // Create a new row for each iteration
      // Iterate over each column
      for (int row = 0; row < this.height; row++) {
        // Create a new GamePiece and add it to the row
        GamePiece piece = this.board.get(col).get(row); 

        piece.top = (row > 0); 

        piece.bottom = (row < (height - 1));


        piece.left = (row == (height / 2) && col != 0); 

        piece.right = (row == (height / 2) && col != (width - 1));


        if (col == 3 && row == width - 3) {
          this.powerCol = col; 
          this.powerRow = row; 
          piece.powerStation = true; 
        }

        column.add(piece);
        nodes.add(piece);
      }

      this.board.add(column);
    }

    return board; 
  }



  // to dictate the color of the wire 
  public Color makeColor(GamePiece peice,int y, int x) {
    Color color = Color.lightGray;
    if (peice.powered) {
      int gradient = 255 - 50 * (int) Math.sqrt(Math.pow(this.powerCol - y, 2) 
          + Math.pow(this.powerRow - x, 2));
      color = new Color(255, gradient, 0);
    }
    return color; 
  }


  //EXTRA CREDIT 
  // EFFECT: Allows the player to see how long it would take 
  // them solve the puzzel updates the tickCount feild 
  public void onTick() {
    // Increment the tick count
    this.tickCount++;

  }


  /// draws the current world scene 
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(this.width * size, this.height * size);
    // this.board = hard code

    // col = x , width 
    // row = y . height 

    WorldImage gridImage = new EmptyImage();
    for (int row = 0; row < this.height; row++) {
      //      if(i < board.size()) {
      WorldImage rowImage = new EmptyImage();

      for (int col = 0; col < this.width; col++) {

        //        if (j < row.size()) {

        GamePiece gp = this.board.get(col).get(row);


        /// EXTRA CREDIT 
        WorldImage gpImage = gp.tileImage(makeColor(gp, row, col));



        rowImage = new BesideImage(rowImage, gpImage); 

      }


      gridImage = new AboveImage(gridImage, rowImage);

      // EXTRA CREDIT
      // Calculate elapsed time in seconds
      int elapsedTimeInSeconds = this.tickCount;

      // EXTRA CREDIT
      // Calculate minutes and seconds
      int minutes = elapsedTimeInSeconds / 60;
      int seconds = elapsedTimeInSeconds % 60;

      // EXTRA CREDIT
      WorldImage timeImage = new TextImage("Time: " + minutes + " minutes " 
          + seconds + " seconds", 20, Color.BLACK);
      
      WorldImage stepImage = new TextImage("Steps:" + steps, 20, Color.BLACK);
      
      WorldImage scoreImage = new TextImage("Score:" + score, 20, Color.BLACK);
      
      WorldImage textImage = new TextImage("How To Play Game:", 20, Color.MAGENTA); 
      
      WorldImage textImage2 = new TextImage("2. Get the lowest amount of both steps "
          + "and score", 15, Color.MAGENTA); 
      
      WorldImage textImage3 = new TextImage("1. Connect all tiles together. "
          + "Can move the powerstaion", 15, Color.MAGENTA); 
      
      WorldImage textImage4 = new TextImage("3. Ohh too hard :(   "
          + "just press 'f' to restart game", 15, Color.MAGENTA);


      // EXTRA CREIDT
      scene.placeImageXY(gridImage, this.width * size / 2, this.height * size / 2);
      
      scene.placeImageXY(timeImage, this.width * size - height + size / 2, 
          this.width * size - height + size / 2 + size); 
      
      scene.placeImageXY(stepImage, this.width * size - height + size / 3 , 
          this.width * size - height + size / 3); 
      
      scene.placeImageXY(scoreImage,this.width * size - height + size - 60, 
          this.width * size - height + size / size + size); 
      
      scene.placeImageXY(textImage, 900, 100); 
      scene.placeImageXY(textImage2, 900, 300); 
      scene.placeImageXY(textImage3, 900, 200); 
      scene.placeImageXY(textImage4, 900, 400); 

    }
    return scene;

  }

  // EFFECT: updates the board as the user click on the mouse pad 
  // to rotate the peice while checking you the user has won the game
  public void onMouseClicked(Posn pos, String buttonName) {
    // Convert mouse click position to grid coordinates
    int col = Math.floorDiv(pos.x , size);
    int row = Math.floorDiv(pos.y , size) ;


    // Check if the click is within the bounds of the board
    if (col >= 0 && col < width && col >= 0 && row < height) {
      // Get the game piece at the clicked position
      GamePiece clickedPiece = board.get(col).get(row);

      // Rotate the clicked piece
      rotatePiece(clickedPiece);

      bfs();
    }

    if (userWon()) {
      this.endOfWorld("YOU WON");
    }


  }




  // Rotate the given game piece
  public void rotatePiece(GamePiece piece) {
    // Toggle the rotation status of the piece
    piece.rotate();

    steps++; 
    int newScore = score + 20; 
    score = newScore++; 
  }



  // randomly places the game piece on the board 
  void randomize(ArrayList<GamePiece> nodes) {
    Random rand = new Random(4);  // is seed for the sake of testing the TA said I was allowed 
    for (GamePiece gp : nodes) {
      // Randomly rotate each GamePiece object
      for (int i = 0; i < rand.nextInt(4); i++) {
        gp.rotate();
      }
    }
  }


  // if the user has won
  public boolean userWon() {

    for (GamePiece currPiece : nodes) {
      if (!currPiece.powered) {
        return false; // If any piece is not powered, return false immediately
      }
    }

    // If all pieces are powered, return true
    return true;
  }



  // draws the image of the last scene when the user wins
  public WorldScene lastScene(String msg) {
    WorldScene finalScene = makeScene(); 

    TextImage gameOverText = new TextImage(msg, 40, FontStyle.BOLD, Color.RED);
    finalScene.placeImageXY(gameOverText, width * size / 2, height * size / 2);

    return finalScene;
  }







  //EFFECT: to move the power station based off the keys pressed
  public void onKeyEvent(String key) {
    if (key.equals("left")) {
      // Move the power station left (decrease powerCol)


      movePS(-1, 0); // moves it 
      //updateTileStatus(); // Update power distribution
    }

    else if (key.equals("right")) {
      // Move the power station right (increase powerCol)
      movePS(1, 0);
      //updateTileStatus(); // Update power distribution

    }
    else if (key.equals("up")) {
      // Move the power station up (decrease powerRow)
      movePS(0, -1);


    }
    else if (key.equals("down")) {
      // Move the power station down (increase powerRow)
      movePS(0, 1);

    }

    if (key.equals("f")) {
      this.resetGame();
    }

  }

  // EFFECT: move the power station therefore, update the corrdinates of PS
  public void movePS(int changex, int changey) {
    int newY = this.powerRow + changey; 
    int newX = this.powerCol + changex; 

    if (newX >= 0 && newX < this.height && newY >= 0 && newY < this.width) {

      GamePiece currPiece = this.board.get(this.powerCol).get(this.powerRow);

      GamePiece newPiece = this.board.get(newX).get(newY);

      if ((changex == -1 && currPiece.left && newPiece.right)

          || (changex == 1 && currPiece.right && newPiece.left)

          || (changey == -1 && currPiece.top && newPiece.bottom)

          || (changey == 1 && currPiece.bottom && newPiece.top)) {

        this.board.get(this.powerCol).get(powerRow).powerStation = false; 
        this.powerCol = newX; 
        this.powerRow = newY;
        this.board.get(this.powerCol).get(powerRow).powerStation = true; 

      }
    }


  }




  // EFFECT: updates the connect tiles to the power station
  public void bfs() {
    ArrayList<GamePiece> visited = new ArrayList<GamePiece>(); // stores visited nodes
    ArrayList<GamePiece> workList = new ArrayList<GamePiece>(); // stores nodes to visit

    GamePiece start = board.get(powerCol).get(powerRow);
    start.distanceFrom = 0; 



    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {

        GamePiece piece = board.get(i).get(j);
        piece.powered = false; 

      }
    }

    workList.add(start);

    while (!workList.isEmpty()) {
      GamePiece currentPiece = workList.remove(0); // remove the first piece from workList
      currentPiece.powered = true;
      //currentPiece.distanceFrom = 

      // Check if the current piece has not been visited yet
      if (!visited.contains(currentPiece)) {
        visited.add(currentPiece); // mark current piece as visited

        // Add neighboring pieces to workList if they are valid and not visited yet
        if (currentPiece.top && currentPiece.row > 0) {
          GamePiece topPiece = board.get(currentPiece.col).get(currentPiece.row - 1);

          if (topPiece.bottom) {
            topPiece.powered = true; 
            workList.add(topPiece); 
            topPiece.distanceFrom++; // changes the the distance 

          }

        }

        if (currentPiece.bottom && currentPiece.row < height - 1) {
          GamePiece bottomPiece = board.get(currentPiece.col).get(currentPiece.row + 1);


          if (bottomPiece.top) {

            bottomPiece.powered = true; 
            workList.add(bottomPiece); 

          } 

        }
        if (currentPiece.right && currentPiece.col < width - 1) {
          GamePiece rightPiece = board.get(currentPiece.col + 1).get(currentPiece.row);

          if (rightPiece.left) {
            rightPiece.powered = true; 
            workList.add(rightPiece); 

          }

        }

        if (currentPiece.left && currentPiece.col > 0) {
          GamePiece leftPiece = board.get(currentPiece.col - 1).get(currentPiece.row);

          if (leftPiece.right) {
            leftPiece.powered = true; 
            workList.add(leftPiece);

          }

        }
      }
    }


  }


  /// EFFECT: using Kruskal's algorithm to finding the MST of a graph
  void findMST() {
    // stores in the hash map 
    HashMap<GamePiece, GamePiece> reps = new HashMap<>();

    // puts the nodes into the hash map 
    for (GamePiece gp : this.nodes) { 
      reps.put(gp, gp);
    }
    //decides if it is a tree edge 
    ArrayList<Edge> treeEdges = new ArrayList<>();

    // the list for the current edge were working on
    ArrayList<Edge> worklist = this.genEdges();

    while (treeEdges.size() < this.nodes.size() - 1) {
      Edge current = worklist.remove(0);
      GamePiece from = current.fromNode;
      GamePiece to = current.toNode;

      // to prevent for creating cycles 
      if (!this.topRep(reps.get(to), reps).sameGamePiece(this.topRep(reps.get(from), reps))) {
        treeEdges.add(current);// can add the current gamePiece
        this.union(to, from, reps); // puts them togther
        to.connectTo(from); // then connects them
      }
    }
  }


  // EFFECT: Sets the top representatives for each node
  void union(GamePiece to, GamePiece from, HashMap<GamePiece,GamePiece> reps) {
    reps.put(topRep(to, reps), topRep(from, reps));

  }


  // Finds the top level representative for the given GamePiece in the MST
  GamePiece topRep(GamePiece gp, HashMap<GamePiece, GamePiece> reps) {

    if (!reps.containsKey(gp) || gp.sameGamePiece(reps.get(gp))) {

      return gp;
    }

    return this.topRep(reps.get(gp), reps);
  }


  // Finds all the possible edges on this board
  ArrayList<Edge> genEdges() {
    ArrayList<Edge> edges = new ArrayList<>();
    ArrayList<Posn> vectors = new ArrayList<>(Arrays.asList(
        new Posn(-1, 0), new Posn(0, -1), new Posn(0, 1), new Posn(1, 0)));

    for (int i = 0; i < this.width; i++) {
      for (int j = 0; j < this.height; j++) {
        for (int k = 0; k < vectors.size(); k++) {
          Posn p = vectors.get(k);
          int nx = p.x + i;
          int ny = p.y + j;
          if (this.validCoords(nx, ny)) {
            // randomly assign the wieghts to the edges
            edges.add(new Edge(this.board.get(i).get(j), 
                this.board.get(nx).get(ny), this.rand.nextInt(40)));
          }
        }
      }
    }

    Collections.sort(edges, new CompEdgeWeight());
    this.mst = edges; 
    return mst;
  }



  // determines if the coordinatez are within the boundary
  boolean validCoords(int x, int y) {
    return x >= 0 && x < this.width && y >= 0 && y < this.height;
  }

}









// demonstrates a gamepiece on the board
class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;
  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;
  // whether the power station is on this piece
  boolean powerStation;
  boolean powered;
  int distanceFrom; 

  /// include what color to draw it as

  int size = 100;

  Color wireColor = Color.LIGHT_GRAY;
  int wireWidth = 6;
  int wireLength = 50;

  // intializes all the fields
  GamePiece(int row, int col, boolean left, boolean right, boolean top, boolean bottom,
      boolean powerStation, boolean powered) {
    this.row = row;
    this.col = col;

    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;

    this.powerStation = powerStation;
    this.powered = powered;
  }

  // used to change the corridnates only 
  GamePiece(int row, int col) {
    this.row = row; 
    this.col = col; 
  }




  //DIDINT TEST GOT FROM USE IT 
  WorldImage tileImage(Color color) {
    // Start tile image off as a blue square with a wire-width square in the middle,
    // to make image "cleaner" (will look strange if tile has no wire, but that
    // can't be)
    WorldImage image = new OverlayImage(
        new RectangleImage(size, size, OutlineMode.OUTLINE, Color.BLACK),
        new OverlayImage(
            new RectangleImage(wireWidth, wireWidth, OutlineMode.SOLID, wireColor),
            new RectangleImage(size, size, OutlineMode.SOLID, Color.DARK_GRAY)
            )
        );

    WorldImage vWire = new RectangleImage(wireWidth, (size + 1) / 2, OutlineMode.SOLID, color);
    WorldImage hWire = new RectangleImage((size + 1) / 2, wireWidth, OutlineMode.SOLID, color);

    if (this.top) {
      image = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, vWire, 0, 0, image);
    }

    if (this.right) {
      image = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, hWire, 0, 0, image);
    }

    if (this.bottom) {
      image = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, vWire, 0, 0, image);
    }

    if (this.left) {
      image = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.MIDDLE, hWire, 0, 0, image);
    }


    if (this.powerStation) {
      image = new OverlayImage(
          new OverlayImage(new StarImage(size / 3, 7, OutlineMode.OUTLINE, new Color(255, 128, 0)),
              new StarImage(size / 3, 7, OutlineMode.SOLID, new Color(0, 255, 255))),
          image);
    }
    return image;
  }



  //Add a rotate method to your GamePiece class
  public void rotate() {
    // Toggle the connection status of the piece's edges
    boolean temp = this.top;
    this.top = this.left;
    this.left = this.bottom;
    this.bottom = this.right;
    this.right = temp;
  }


  boolean sameGamePiece(GamePiece that) {
    return that.row == this.row 
        && that.col == this.col 
        && that.left == this.left
        && that.right == this.right 
        && that.top == this.top 
        && that.bottom ==  this.bottom
        && that.powerStation == this.powerStation;
  }


  // 
  void connectTo(GamePiece to) {
    int rowDiff = this.row - to.row;
    int colDiff = this.col - to.col;

    if (rowDiff <= 1 && rowDiff >= -1 && colDiff <= 1 && colDiff >= -1) {
      if (rowDiff == 1) {
        this.top = true;
        to.bottom = true;
      } else if (rowDiff == -1) {
        this.bottom = true;
        to.top = true;
      } else if (colDiff == 1) {
        this.left = true;
        to.right = true;
      } else if (colDiff == -1) {
        this.right = true;
        to.left = true;
      }
    }
  }




}


// demonstrates the connection between gamepeices on the gird
class Edge {
  GamePiece fromNode;
  GamePiece toNode;
  int weight;

  // intializes all the fields
  Edge(GamePiece fromNode, GamePiece toNode, int weight) {
    this.fromNode = fromNode;
    this.toNode = toNode;
    this.weight = weight;
  }
}

// used to compare the weights of each edge
class CompEdgeWeight implements Comparator<Edge> {
  // Compares the edges by weight
  public int compare(Edge edge1, Edge edge2) {
    return edge1.weight - edge2.weight;
  }
}

// to create a well used helper
class Utils {

  // to make all the coordinates into one list in order to properly search for 
  // the edges 
  <T> ArrayList<T> flatten(ArrayList<ArrayList<T>> toFlatten) {
    ArrayList<T> flat = new ArrayList<>();
    for (ArrayList<T> al : toFlatten) {
      flat.addAll(al);
    }

    return flat;
  }
}


// examples 
class LightEmAllExamples {

  GamePiece upDown;
  GamePiece downUp;

  // left bigining side
  GamePiece upDownRightAt10;
  GamePiece leftRightUpDown11;
  GamePiece rightUpDown12;

  // row 2
  GamePiece leftUpAt2;
  GamePiece rightLeftAt1;
  GamePiece rightDownAt0;

  // row 3
  GamePiece upDownAt20;
  GamePiece upDownAt21;
  GamePiece upDownAt22;

  GamePiece leftDown;
  GamePiece leftUp;

  // right being side
  GamePiece rightDown;
  GamePiece rightLeft;

  // coner peices
  GamePiece upDownAt00;
  GamePiece upDownAt01;
  GamePiece upDownAt02;

  ArrayList<GamePiece> row1;
  ArrayList<GamePiece> row2;
  ArrayList<GamePiece> row3;

  ArrayList<GamePiece> all123;

  ArrayList<ArrayList<GamePiece>> board1;
  ArrayList<ArrayList<GamePiece>> emptyBoard; 
  ArrayList<GamePiece> emptyNode; 
  //  ArrayList<Edge> edgeTest; 

  LightEmAll game1;
  LightEmAll game2;

  LightEmAll gameTest;
  LightEmAll newGame; 

  void initaData() {
    this.upDown = new GamePiece(0, 1, false, false, true, true, false, false);
    this.downUp = new GamePiece(0, 1, true, true, false, false, false, false);

    this.leftDown = new GamePiece(1, 0, false, false, false, true, false, false);
    this.leftUp = new GamePiece(1, 0, false, false, true, false, false, false);

    this.rightDown = new GamePiece(1, 0, false, false, false, false, false, false);

    this.upDownAt00 = new GamePiece(0, 0, false, false, true, true, false, false);
    this.upDownAt01 = new GamePiece(0, 1, false, false, true, true, false, false);
    this.upDownAt02 = new GamePiece(0, 2, false, false, true, true, false, false);

    this.upDownRightAt10 = new GamePiece(1, 0, false, true, true, true, false, false);
    this.leftRightUpDown11 = new GamePiece(1, 1, true, true, true, true, false, false);
    this.rightUpDown12 = new GamePiece(1, 2, true, true, true, true, false, false);

    this.upDownAt20 = new GamePiece(2, 0, false, false, true, true, false, false);
    this.upDownAt21 = new GamePiece(2, 1, false, false, true, true, false, false);
    this.upDownAt22 = new GamePiece(2, 2, false, false, true, true, true, false);



    // row1
    this.row1 = new ArrayList<GamePiece>();
    this.row1.add(upDownAt00);
    this.row1.add(upDownAt01);
    this.row1.add(upDownAt02);

    // row 2
    this.row2 = new ArrayList<GamePiece>();
    this.row2.add(upDownRightAt10);
    this.row2.add(leftRightUpDown11);
    this.row2.add(rightUpDown12);

    // row 3
    this.row3 = new ArrayList<GamePiece>();
    this.row3.add(upDownAt20);
    this.row3.add(upDownAt21);
    this.row3.add(upDownAt22);

    // board game
    this.board1 = new ArrayList<ArrayList<GamePiece>>();
    this.board1.add(row1);
    this.board1.add(row2);
    this.board1.add(row3);

    this.all123 = new ArrayList<GamePiece>(Arrays.asList(upDownAt00, upDownAt01, upDownAt02,
        upDownRightAt10, leftRightUpDown11, rightUpDown12, upDownAt20, upDownAt21, upDownAt22));

    // a 3x3 game with the power station


    this.emptyBoard = new ArrayList<ArrayList<GamePiece>>(); 
    this.emptyNode = new ArrayList<GamePiece>();

    this.gameTest = new LightEmAll(board1, all123, 3, 3);

    this.game1 = new LightEmAll(emptyBoard, emptyNode, 3, 3);

    this.game2 = new LightEmAll(emptyBoard, emptyNode, 1, 1);

    this.newGame = new LightEmAll(3, 3, new Random(1));


  }


  void testMakeScene(Tester t) {
    initaData();

    WorldScene game2Scene = new WorldScene(100, 100); // grid Image
    WorldImage empty = new EmptyImage(); // rowimage

    GamePiece at00 = game2.board.get(0).get(0); 
    WorldImage imageAt00 = at00.tileImage(Color.LIGHT_GRAY); // tile mage 


    WorldImage expectedTime = (new TextImage("Time: 0 minutes 0 seconds", 20.0, 
        FontStyle.REGULAR, Color.BLACK));
    
    WorldImage expectedSteps = (new TextImage("Steps:0", 20.0, 
        FontStyle.REGULAR, Color.BLACK));



    WorldImage expectedImage = new AboveImage(empty, new BesideImage(empty,imageAt00 )); 



    game2Scene.placeImageXY(expectedImage, 50, 50);
    game2Scene.placeImageXY(expectedTime, 149, 149);
    game2Scene.placeImageXY(expectedSteps, 132, 132);



    WorldScene actualScene = game2.makeScene();



    t.checkExpect(actualScene, game2Scene);

  }


  void testGenerateBoard(Tester t) {
    initaData();

    t.checkExpect(game1.board.size(), 6); // before 

    // Generate the board
    game1.generateBoard();

    // Check the dimensions of the generated board
    t.checkExpect(game1.board.size(), 9); // Check the number of rows
    //t.checkExpect(game1.board.get(0).size(), 9); // Check the number of columns
  }




  void testRotate(Tester t) {
    // Create a new GamePiece with initial orientation (left, right, top, bottom)
    GamePiece piece = new GamePiece(0, 0, false, false, true, false, false, false); 
    // for a piece that is up


    t.checkExpect(piece.left, false); 
    t.checkExpect(piece.right, false); 
    t.checkExpect(piece.top, true); 
    t.checkExpect(piece.bottom, false);

    piece.rotate(); 

    t.checkExpect(piece.left, false); 
    t.checkExpect(piece.right, true); 
    t.checkExpect(piece.top, false); 
    t.checkExpect(piece.bottom, false); 

    GamePiece rightPiece = new GamePiece(0, 0, true, false, false, false, false, false);
    // opened right

    t.checkExpect(rightPiece.left, true); 
    t.checkExpect(rightPiece.right, false); 
    t.checkExpect(rightPiece.top, false); 
    t.checkExpect(rightPiece.bottom, false);

    rightPiece.rotate(); 

    t.checkExpect(rightPiece.left, false); 
    t.checkExpect(rightPiece.right, false); 
    t.checkExpect(rightPiece.top, true); 
    t.checkExpect(rightPiece.bottom, false); 



  }

  void testRandomize(Tester t) {
    // Create a list of GamePieces
    ArrayList<GamePiece> nodes = new ArrayList<>();

    // Create GamePieces with known initial orientations
    GamePiece piece1 = new GamePiece(0, 0, false, false, true, 
        false, false, false); // Initial: top
    GamePiece piece2 = new GamePiece(0, 1, false, false, false, 
        true, false, false); // Initial: bottom
    GamePiece piece3 = new GamePiece(1, 0, true, false, false, 
        false, false, false); // Initial: left
    GamePiece piece4 = new GamePiece(1, 1, false, true, false, 
        false, false, false); // Initial: right

    nodes.addAll(Arrays.asList(piece1, piece2, piece3, piece4));

    // Randomize the orientations
    LightEmAll game = new LightEmAll(new ArrayList<>(), nodes, 2, 2);
    game.randomize(nodes);

    // Check if the orientations have changed
    t.checkExpect(piece1.top, false);
    t.checkExpect(piece2.bottom, false);
    t.checkExpect(piece3.left, false);
    t.checkExpect(piece4.right, true);
  }



  void testUserWon(Tester t) {
    initaData();

    // Set up initial conditions
    gameTest.powerRow = 2;
    gameTest.powerCol = 2;
    gameTest.board.get(2).get(2).powerStation = true;

    // Check if the game is won before powering any pieces
    t.checkExpect(gameTest.userWon(), false);

    // Power all pieces
    for (GamePiece piece : gameTest.nodes) {
      piece.powered = true;
    }


    // Check if the game is won after powering all pieces
    t.checkExpect(gameTest.userWon(), true);
  }




  void testBFS(Tester t) {
    initaData();

    // Generate the board
    gameTest.generateBoard();

    // Set up initial conditions
    gameTest.powerRow = 2;
    gameTest.powerCol = 2;

    gameTest.board.get(2).get(2).powerStation = true;

    // Run BFS
    gameTest.bfs();

    // Check if pieces are correctly powered
    t.checkExpect(gameTest.board.get(2).get(2).powered, true); // Check if power station is powered

    // Check a few neighboring pieces
    t.checkExpect(gameTest.board.get(2).get(1).powered, true); 
    t.checkExpect(gameTest.board.get(1).get(2).powered, true); 
    t.checkExpect(gameTest.board.get(3).get(2).powered, true); 
  }

  void testMakeColor(Tester t) {
    initaData();
    GamePiece piece = new GamePiece(0, 0, true, false, false, true, false, true);


    t.checkExpect(piece.wireColor, Color.lightGray);
    t.checkExpect(upDown.wireColor, Color.lightGray); 
    t.checkExpect(downUp.wireColor, Color.lightGray);




  }


  void testOnMouseClicked(Tester t) {
    // Create a new game instance
    LightEmAll game = new LightEmAll(new ArrayList<>(), new ArrayList<>(), 3, 3);
    GamePiece piece = new GamePiece(0, 0, true, false, false, true, false, false);
    game.generateBoard();

    game.onMouseClicked(new Posn(50, 50), "left");
    // Verify that the piece was rotated
    t.checkExpect(piece.top, false);
    t.checkExpect(piece.bottom, true);
    t.checkExpect(piece.left, true);
    t.checkExpect(piece.right, false);
  }


  void testMovePS(Tester t) {
    initaData();

    game1.generateBoard(); 

    t.checkExpect(game1.board.get(0).get(0).powerStation, false); 
    t.checkExpect(game1.board.get(0).get(1).powerStation, false); 
    t.checkExpect(game1.board.get(0).get(2).powerStation, false); 

    t.checkExpect(game1.board.get(1).get(0).powerStation, false); 
    t.checkExpect(game1.board.get(1).get(1).powerStation, true); 
    t.checkExpect(game1.board.get(1).get(2).powerStation, false); 

    t.checkExpect(game1.board.get(2).get(0).powerStation, false); 
    t.checkExpect(game1.board.get(2).get(1).powerStation, false); 
    t.checkExpect(game1.board.get(2).get(2).powerStation, false); 

    game1.movePS(1, 0);
    // move down one 
    t.checkExpect(game1.board.get(2).get(1).powerStation, true);
    game1.movePS(-1, 0);
    // comes back up 
    t.checkExpect(game1.board.get(1).get(1).powerStation, true);

    game1.movePS(0, 1);
    t.checkExpect(game1.board.get(1).get(2).powerStation, true);

    game1.movePS(0, -1);
    t.checkExpect(game1.board.get(1).get(1).powerStation, true);




  }




  void testAddPieces(Tester t) {
    initaData();

    ArrayList<GamePiece> newList = new ArrayList<GamePiece>();


    t.checkExpect(newList.size(), 0); 
    t.checkExpect(gameTest.board.size(), 6);



    gameTest.addPieces();
    t.checkExpect(gameTest.board.size(), 9);



  }

  // EXTRA CREDIT TESTING 
  void testRotePiece(Tester t) {
    LightEmAll  world = new LightEmAll(2, 2, new Random(1));


    // checks the amount of steps current in the world 
    t.checkExpect(world.steps, 0); 
    t.checkExpect(world.score, 0); 

    // Rresents how the peice will become onee roated 
    GamePiece newGp = new GamePiece(0, 0, false, true, false, true, true, true);

    // the powerstation 
    world.rotatePiece(world.board.get(0).get(0)); 

    // checks for the changes
    t.checkExpect(world.board.get(0).get(0), newGp); 
    t.checkExpect(world.steps, 1); 
    t.checkExpect(world.score, 20); 


    // moves another different piece that is not a powerStation 
    GamePiece newGp2 = new GamePiece(0, 1, false, true, true, false, false, false);

    // the powerstation 
    world.rotatePiece(world.board.get(1).get(0)); 


    // checks for the changes
    t.checkExpect(world.board.get(1).get(0), newGp2); 
    t.checkExpect(world.steps, 2); 
    t.checkExpect(world.score, 40); 






  }

  void testBlankBoard(Tester t) {
    initaData();

    // location of powerstation
    gameTest.board.get(2).get(2).powerStation = true;

    // before was all true 
    t.checkExpect(gameTest.board.get(1).get(1).right, true );
    t.checkExpect(gameTest.board.get(1).get(1).left, true ); 
    t.checkExpect(gameTest.board.get(1).get(1).top, true ); 
    t.checkExpect(gameTest.board.get(1).get(1).bottom, true ); 

    // a game that has connections 
    gameTest.blankBoard();

    // the power station has moved 
    t.checkExpect(gameTest.board.get(0).get(0).powerStation, true); 

    // and the piece has become black
    t.checkExpect(gameTest.board.get(1).get(1).right, false );
    t.checkExpect(gameTest.board.get(1).get(1).left, false ); 
    t.checkExpect(gameTest.board.get(1).get(1).top, false ); 
    t.checkExpect(gameTest.board.get(1).get(1).bottom, false ); 


  }



  void testOnTick(Tester t) {
    initaData();


    game1.tickCount = 0; 


    game1.onTick();

    t.checkExpect(game1.tickCount, 1); 

    game1.onTick();

    t.checkExpect(game1.tickCount, 2);


  }

  void testLastScene(Tester t) {
    // Create an instance of MinesweeperWorld with some parameters
    LightEmAll  world = new LightEmAll(6, 6, new Random(1));

    // Create a message to display in the final scene
    String message = "YOU WON!";

    // Call the lastScene method to generate the final scene
    WorldScene scene = world.lastScene(message);

    // Check if the final scene has been generated
    t.checkExpect(scene, scene);



    // Create an instance of MinesweeperWorld with some parameters
    LightEmAll  world2 = new LightEmAll(4, 4, new Random(1));

    // Create a message to display in the final scene
    String message2 = "CONGRATES WINNER!";

    // Call the lastScene method to generate the final scene
    WorldScene scene2 = world2.lastScene(message2);

    // Check if the final scene has been generated
    t.checkExpect(scene2, scene2);

  }

  public void testFindMST(Tester t) {
    // Create a sample configuration of nodes and edges


    // Create a LightEmAll instance with the sample configuration
    LightEmAll game = new LightEmAll(2, 2, new Random(1));

    // makes sure these are the pieces on the graph 
    t.checkExpect(game.board.get(0).get(0), 
        new GamePiece(0, 0, false, true, true, false, true, true));
    
    t.checkExpect(game.board.get(0).get(1), 
        new GamePiece(1, 0, false, true, false, false, false, false));
    
    t.checkExpect(game.board.get(1).get(0),
        new GamePiece(0, 1, true, false, true, false, false, false));
    
    t.checkExpect(game.board.get(1).get(1), 
        new GamePiece(1, 1, false, false, true, false, false, false));

    // obtains the corridnates
    GamePiece at00 = game.board.get(0).get(0); 

    GamePiece at10 = game.board.get(0).get(1);

    GamePiece at01 = game.board.get(1).get(0);

    GamePiece at11 = game.board.get(1).get(1);


    
    // creats the edges in the graoh 
    Edge eg1 = new Edge(at00, at01, 28); 
    Edge eg2 = new Edge(at10, at11, 33); 
    Edge eg3 = new Edge(at11, at01, 34); 
    Edge eg4 = new Edge(at01, at00, 37); 




    // Call the findMST method
    game.findMST();

    // makes the lise with the shorest 
    ArrayList<Edge> expectedMST = new ArrayList<Edge>(Arrays.asList(eg1, eg2, eg3, eg4)); 

    // makes sure it is equal to the expected 
    t.checkExpect(game.mst, expectedMST); 



  }



  void testUnion(Tester t) {
    LightEmAll game = new LightEmAll(3, 3, new Random(1));

    GamePiece gp1 = game.board.get(0).get(0); // right nexrt to eaxh ote
    GamePiece gp2 = game.board.get(0).get(1); 
    GamePiece gp3 = game.board.get(0).get(2); 

    GamePiece gp4 = game.board.get(1).get(0); 
    GamePiece gp5 = game.board.get(1).get(1); 
    GamePiece gp6 = game.board.get(1).get(2); 


    HashMap<GamePiece, GamePiece> current = new HashMap<GamePiece, GamePiece>();

    t.checkExpect(current.isEmpty(), true); 

    current.put(gp1, gp2);
    current.put(gp3, gp4);
    current.put(gp5, gp6);

    t.checkExpect(current.isEmpty(), false); 

    game.union(gp2, gp1, current); 

    t.checkExpect(current.containsKey(gp2), true);
    t.checkExpect(current.containsValue(gp1), false); // has combined them 
    t.checkExpect(current.containsValue(gp2), true);





  }

  void testTopRes(Tester t) {
    LightEmAll game = new LightEmAll(3, 3, new Random(1));

    GamePiece gp1 = game.board.get(0).get(0); // right nexrt to eaxh ote
    GamePiece gp2 = game.board.get(0).get(1); 
    GamePiece gp3 = game.board.get(0).get(2); 

    GamePiece gp4 = game.board.get(1).get(0); 
    GamePiece gp5 = game.board.get(1).get(1); 
    GamePiece gp6 = game.board.get(1).get(2); 

    GamePiece gp7 = game.board.get(2).get(0); 
    GamePiece gp8 = game.board.get(2).get(1); 

    GamePiece gp9 = game.board.get(2).get(2); 
    GamePiece same9 =  new GamePiece(2, 2, false, false, false, true,false, false); 

    HashMap<GamePiece, GamePiece> current = new HashMap<GamePiece, GamePiece>();

    current.put(gp1, gp2);
    current.put(gp3, gp4);
    current.put(gp5, gp6);
    current.put(gp9, same9); 



    // test for the same gamePieces it returns the gamePiece 
    t.checkExpect(game.topRep(gp9, current), same9); 

    // if the key and value are not the same
    t.checkExpect(game.topRep(gp4, current), gp4); 



  }



  void testGenEdges(Tester t) {

    LightEmAll world = new LightEmAll(4, 4, new Random(1));
    t.checkExpect(world.mst.size(), 22);

    world.genEdges(); // makes all the conects

    t.checkExpect(world.mst.size(), 48); 
  }


  void testValidCordinates(Tester t) {
    initaData();

    // a 3 by 3 grid 
    t.checkExpect(newGame.validCoords(1, 0), true); // on the grid
    t.checkExpect(newGame.validCoords(5, 0), false); // not on the grid

    t.checkExpect(newGame.validCoords(-1, 0), false);
    t.checkExpect(newGame.validCoords(2, 2), true);
  }

  void testConnectTo(Tester t) {
    GamePiece piece1 = new GamePiece(0, 0, false, false, false, false, false, false); 
    GamePiece piece2 = new GamePiece(0, 1, false, false, false, false, false, false);

    // Connect the pieces
    piece1.connectTo(piece2);



    t.checkExpect(piece1.right, true); 
    // piece1's right side should connect to piece2's left side
    t.checkExpect(piece2.left, true);

    t.checkExpect(piece2.top, false);
    t.checkExpect(piece1.bottom, false);
    
    
  }



  void testSameGamePiece(Tester t) {
    LightEmAll game = new LightEmAll(3, 3, new Random(1));


    GamePiece gp8 = game.board.get(1).get(2); 
    GamePiece gp9 = game.board.get(2).get(2); 
    GamePiece same9 =  new GamePiece(2, 2, false, false, false, true, false, false); 
    
    GamePiece gp1 = new GamePiece(0, 0, true, true, true, true, true, true);
    GamePiece gp2 = new GamePiece(0, 0, true, true, true, true, true, true);
    GamePiece gp3 = new GamePiece(0, 0, false, true, true, true, false, true);
    GamePiece gp4 = new GamePiece(1, 1, true, true, true, true, true, true);
   
    t.checkExpect(gp1.sameGamePiece(gp2), true);
        
    t.checkExpect(gp2.sameGamePiece(gp3), false); 
       
    t.checkExpect(gp2.sameGamePiece(gp4), false);

    t.checkExpect(gp9, new GamePiece(2, 2, false, false, false, true, false, false)); 
    t.checkExpect(gp8.sameGamePiece(gp9), false); 
    t.checkExpect(gp9.sameGamePiece(same9), true); 

  }
  
  

  void testmakeColor(Tester t) {

    LightEmAll testerGame = new LightEmAll(2, 2, new Random(1));


    GamePiece gp1 = new GamePiece(0, 0, false, false, false, false, false, false);

    t.checkExpect(testerGame.makeColor(gp1, 0, 0), Color.lightGray);

    GamePiece gp2 = new GamePiece(0, 0, true, false, false, true, true, true);

    // chnaging color from 0,0 TO 1, 1
    t.checkExpect(testerGame.makeColor(gp2, 0, 0), new Color(255, 255, 0));

    // chnaging color From 0,0 TO 1, 1
    GamePiece gp3 = new GamePiece(1, 1, true, false, false, true, true, true);
    t.checkExpect(testerGame.makeColor(gp3, 1, 1), new Color(255, 205, 0));
    
    // a yellow piece
    GamePiece gp7 = new GamePiece(0, 0, true, true, true, true, true, true);
    t.checkExpect(testerGame.makeColor(gp7, 0, 0), new Color(255, 255, 0));

    // going from yellow from orange  FROM 0,0 TO 0,1
    GamePiece gp8 = new GamePiece(0, 1, true, true, true, true, true, true);
    t.checkExpect(testerGame.makeColor(gp8, 0, 1), new Color(255, 205, 0));
    

    // regulare piece 
    GamePiece gp4 = new GamePiece(0, 1, false, false, true, false, true, false);
    t.checkExpect(testerGame.makeColor(gp4, 0, 1), Color.lightGray);

    // regulare piece 
    GamePiece gp5 = new GamePiece(1, 0, false, true, false, true, false, false);
    t.checkExpect(testerGame.makeColor(gp5, 1, 0), Color.lightGray);

    // regulare piece 
    GamePiece gp6 = new GamePiece(1, 1, false, true, true, false, false, false);
    t.checkExpect(testerGame.makeColor(gp6, 1, 1), Color.lightGray);
    

    
  }


  void testFlatten(Tester t) {
    LightEmAll game = new LightEmAll(2, 2, new Random(1));
    Utils u = new Utils(); 

    GamePiece at00 = game.board.get(0).get(0); 
    GamePiece at10 = game.board.get(0).get(1);
    GamePiece at01 = game.board.get(1).get(0);
    GamePiece at11 = game.board.get(1).get(1);

    ArrayList<GamePiece> row1 = new ArrayList<>(Arrays.asList(at00, at10, at01, at11)); 

    // Store the result of flatten
    ArrayList<GamePiece> flattenedList = u.flatten(game.board);

    // Check the size of the flattened list
    t.checkExpect(flattenedList.size(), 4);

    // Check if the contents are as expected
    t.checkExpect(flattenedList, row1);
  }


  void testCompareEdgeWeight(Tester t) {
    LightEmAll game = new LightEmAll(2, 2, new Random(1));

    GamePiece at00 = game.board.get(0).get(0); 

    GamePiece at10 = game.board.get(0).get(1);

    GamePiece at01 = game.board.get(1).get(0);

    GamePiece at11 = game.board.get(1).get(1);

    // creats the edges in the graoh 
    Edge eg1 = new Edge(at00, at01, 28); 
    Edge eg2 = new Edge(at10, at11, 33); 
    Edge eg3 = new Edge(at11, at01, 34); 
    Edge eg4 = new Edge(at01, at00, 37); 

    CompEdgeWeight comp = new CompEdgeWeight();

    t.checkExpect(comp.compare(eg1, eg2), -5); 
    t.checkExpect(comp.compare(eg2, eg1), 5); 
    t.checkExpect(comp.compare(eg2, eg2), 0); 
    t.checkExpect(comp.compare(eg3, eg4), -3); 
    t.checkExpect(comp.compare(eg4, eg3), 3); 


  }

  public void testResetBoard(Tester t) {

    LightEmAll game = new LightEmAll(2, 2, new Random(1));


    t.checkExpect(game.board.size(), 2);
    t.checkExpect(game.board.get(0).size(), 2);
    //make sure power station is 0,0
    t.checkExpect(game.board.get(0).get(0).powerStation, true);



    game.resetGame();



    t.checkExpect(game.board.size(), 2);
    t.checkExpect(game.board.get(0).size(), 2);

    //make sure power station is 0,0
    t.checkExpect(game.board.get(0).get(0).powerStation, true);

  }









  void testBigBang(Tester t) {

    initaData();
    LightEmAll world = new LightEmAll(6, 6, new Random(1));
    int worldWidth = 1500;
    int worldHeight = 1500;
    double tickRate = .80;

    world.bigBang(worldWidth, worldHeight, tickRate);
  }


}

