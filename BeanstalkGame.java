import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BeanstalkGame extends JFrame implements KeyListener {
    // Singleton instance
    private static BeanstalkGame instance;

    private final char[][] maze;
    private int gridSize = 40;
    private final JLabel[][] gridLabels;
    private int playerRow;
    private int playerCol;
    private boolean gameFinished;

    private BeanstalkGame() {
        super("Maze Game");

        String level = "xxxxxxxxxxxxxxxxxxxx!xxxxxx####xxxxxxxxxx!xxxxx##--#xxxxxxxxxx!xxxxx#---#xxxxxxxxxx!xxx###--5##xxxxxxxxx!xxx#--3-4-#xxxxxxxxx!xxx#-#-##-#xxx#####x!xxx#-#-##-#####---#x!xxx#--2---------7-#x!xxx###-###-#0##---#x!xxxxx#-----########x!xxxxx#######xxxxxxxx!xxxxxxxxxxxxxxxxxxxx!xxxxxxxxxxxxxxxxxxxx";

        String[] rows = level.split("!");

        maze = new char[rows.length][rows[0].length()];
        for (int i = 0; i < rows.length; i++) {
            maze[i] = rows[i].toCharArray();
        }

        gridLabels = new JLabel[maze.length][maze[0].length];

        JPanel gridPanel = new JPanel(new GridLayout(maze.length, maze[0].length));
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                JLabel label = new JLabel();
                char cell = maze[i][j];
                switch (cell) {
                    case '#' -> label.setIcon(new ImageIcon("resources/wall.png"));
                    case '-' -> label.setIcon(new ImageIcon("resources/grass.png"));
                    case '0' -> {
                        label.setIcon(new ImageIcon("resources/jackfront.png"));
                        playerRow = i;
                        playerCol = j;
                    }
                    case '2' -> label.setIcon(new ImageIcon("resources/shovel.png"));
                    case '3' -> label.setIcon(new ImageIcon("resources/bean.png"));
                    case '4' -> label.setIcon(new ImageIcon("resources/fert.png"));
                    case '5' -> label.setIcon(new ImageIcon("resources/water.png"));
                    case '7' -> label.setIcon(new ImageIcon("resources/x.png"));
                }
                gridLabels[i][j] = label;
                gridPanel.add(label);
            }
        }

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton moveLeftButton = new JButton("Move Left");
        moveLeftButton.addActionListener(e -> movePlayer("left"));
        JButton moveRightButton = new JButton("Move Right");
        moveRightButton.addActionListener(e -> movePlayer("right"));
        JButton moveUpButton = new JButton("Move Up");
        moveUpButton.addActionListener(e -> movePlayer("up"));
        JButton moveDownButton = new JButton("Move Down");
        moveDownButton.addActionListener(e -> movePlayer("down"));
        controlPanel.add(moveLeftButton);
        controlPanel.add(moveRightButton);
        controlPanel.add(moveUpButton);
        controlPanel.add(moveDownButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(maze[0].length * gridSize, maze.length * gridSize + 50); // Added extra 50 pixels for the control panel
        addKeyListener(this);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // Singleton getInstance method
    public static BeanstalkGame getInstance() {
        if (instance == null) {
            instance = new BeanstalkGame();
        }
        return instance;
    }

    private void movePlayer(String direction) {
        if (gameFinished) {
            return;
        }

        int newRow = playerRow;
        int newCol = playerCol;
        int pushRow = -1;
        int pushCol = -1;
        switch (direction) {
            case "up" -> {
                newRow--;
                pushRow = newRow - 1;
                pushCol = newCol;
            }
            case "down" -> {
                newRow++;
                pushRow = newRow + 1;
                pushCol = newCol;
            }
            case "left" -> {
                newCol--;
                pushRow = newRow;
                pushCol = newCol - 1;
            }
            case "right" -> {
                newCol++;
                pushRow = newRow;
                pushCol = newCol + 1;
            }
        }

        if (newRow >= 0 && newRow < maze.length && newCol >= 0 && newCol < maze[0].length) {
            char newCell = maze[newRow][newCol];
            if (newCell == '-' || newCell == '0' || newCell == '7') {
                // Move player to new position
                maze[playerRow][playerCol] = '-';
                maze[newRow][newCol] = '0';
                playerRow = newRow;
                playerCol = newCol;
                updateGrid();

                // Check if the game is finished
                if (checkGameFinished()) {
                    gameFinished = true;
                    JOptionPane.showMessageDialog(this, "Congratulations! You completed the game!");
                }
            } else if (newCell == '2' || newCell == '3' || newCell == '4' || newCell == '5') {
                // Move player and pushed object to new positions
                if (pushRow >= 0 && pushRow < maze.length && pushCol >= 0 && pushCol < maze[0].length) {
                    char pushCell = maze[pushRow][pushCol];
                    if (pushCell == '-' || pushCell == 'x') {
                        maze[pushRow][pushCol] = newCell;
                        maze[playerRow][playerCol] = '-';
                        maze[newRow][newCol] = '0';
                        playerRow = newRow;
                        playerCol = newCol;
                        updateGrid();

                        // Check if the game is finished
                        if (checkGameFinished()) {
                            gameFinished = true;
                            JOptionPane.showMessageDialog(this, "Congratulations! You completed the game!");
                        }
                    }
                }
            }
        }
    }

    private boolean checkGameFinished() {
        int shovelRow = -1;
        int shovelCol = -1;
        int beanRow = -1;
        int beanCol = -1;
        int fertRow = -1;
        int fertCol = -1;
        int waterRow = -1;
        int waterCol = -1;

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                char cell = maze[i][j];
                if (cell == '2') {
                    shovelRow = i;
                    shovelCol = j;
                } else if (cell == '3') {
                    beanRow = i;
                    beanCol = j;
                } else if (cell == '4') {
                    fertRow = i;
                    fertCol = j;
                } else if (cell == '5') {
                    waterRow = i;
                    waterCol = j;
                }
            }
        }

        return shovelRow != -1 && shovelCol != -1 &&
                beanRow != -1 && beanCol != -1 &&
                fertRow != -1 && fertCol != -1 &&
                waterRow != -1 && waterCol != -1 &&
                isNextToEachOther(shovelRow, shovelCol, beanRow, beanCol) &&
                isNextToEachOther(beanRow, beanCol, fertRow, fertCol) &&
                isNextToEachOther(fertRow, fertCol, waterRow, waterCol);
    }

    private boolean isNextToEachOther(int row1, int col1, int row2, int col2) {
        return Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1;
    }

    private void updateGrid() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                char cell = maze[i][j];
                switch (cell) {
                    case '#' -> gridLabels[i][j].setIcon(new ImageIcon("resources/wall.png"));
                    case '-' -> gridLabels[i][j].setIcon(new ImageIcon("resources/grass.png"));
                    case '0' -> gridLabels[i][j].setIcon(new ImageIcon("resources/jackfront.png"));
                    case '2' -> gridLabels[i][j].setIcon(new ImageIcon("resources/shovel.png"));
                    case '3' -> gridLabels[i][j].setIcon(new ImageIcon("resources/bean.png"));
                    case '4' -> gridLabels[i][j].setIcon(new ImageIcon("resources/fert.png"));
                    case '5' -> gridLabels[i][j].setIcon(new ImageIcon("resources/water.png"));
                    case '7' -> gridLabels[i][j].setIcon(new ImageIcon("resources/x.png"));
                }
            }
        }
    }

    public static void main(String[] args) {
        BeanstalkGame game = BeanstalkGame.getInstance();
        game.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {
            movePlayer("up");
        } else if (keyCode == KeyEvent.VK_DOWN) {
            movePlayer("down");
        } else if (keyCode == KeyEvent.VK_LEFT) {
            movePlayer("left");
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            movePlayer("right");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}