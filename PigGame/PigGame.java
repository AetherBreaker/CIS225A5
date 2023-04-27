package PigGame;

import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class PigGame {
    // Instance variables
    private Scanner scanner;
    private Random random;
    private int num_players;
    private int[] scores = {0, 0};
    private String input = "";
    private int roll = 0;
    private int turn_score = 0;
    private boolean turn_over = false;
    private int current_player = 0;


    /**
     * Static method for creating a new PigGame object Needed to ensure every game instance is
     * created with a shutdown hook to save game data when the program exits
     * 
     * @param num_players The number of players in the game
     * @return A new PigGame object
     */
    public static PigGame createGame(int num_players) {
        PigGame game = new PigGame(num_players);
        // Add a shutdown hook to save game data when the program exits
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                game.saveGameData();
            }
        });
        return game;
    }

    /**
     * Private constructor for the PigGame class
     * 
     * @param num_players The number of players in the game
     */
    private PigGame(int num_players) {


        // Initialize the scanner and random objects
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.num_players = num_players;

        // Attempt to load past game data
        this.loadGameData();

        // Prompt the user to enter the number of players
        System.out.println("Enter the number of players");
        this.num_players = this.scanner.nextInt();
        this.scanner.nextLine();
    }

    /**
     * Destructor for the PigGame class Closes the scanner and saves game data
     */
    public void finalize() {
        // Close the scanner
        this.scanner.close();

        // Save game data
        this.saveGameData();
    }

    /**
     * Checks whether the current player has reached the win condition If so, any existing save data
     * for this game is deleted to prevent rerunning an already completed game
     * 
     * @return True if the current player has won, false otherwise
     */
    private boolean checkWin() {
        if (this.scores[this.current_player] >= 10) {
            this.deleteGameData();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Loads game data from persistent storage
     * 
     * @return True if game data was loaded successfully, false otherwise
     */
    private Boolean loadGameData() {
        // Create a file object
        File file = new File("game_data.txt");

        // Check if the file exists
        if (file.exists()) {
            // Create a scanner object
            Scanner file_scanner = null;
            try {
                file_scanner = new Scanner(file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Read the number of players from the file
            this.num_players = file_scanner.nextInt();

            // Read the current player from the file
            this.current_player = file_scanner.nextInt();

            // Read the scores from the file
            for (int i = 0; i < this.num_players; i++) {
                this.scores[i] = file_scanner.nextInt();
            }

            // Read the turn score from the file
            this.turn_score = file_scanner.nextInt();

            // Read the roll from the file
            this.roll = file_scanner.nextInt();

            // Read the turn over flag from the file
            this.turn_over = file_scanner.nextBoolean();

            // Close the file scanner
            file_scanner.close();

            return true;
        } else {
            return false;
        }
    }


    /**
     * Saves game data to persistent storage If the file already exists, it is overwritten
     */
    private void saveGameData() {
        // Create a file object
        File file = new File("game_data.txt");

        // Check if the file exists
        if (file.exists()) {
            // Delete the file
            file.delete();
        }

        // Create a new file
        try {
            file.createNewFile();


            // Create a file writer object
            FileWriter writer = new FileWriter(file);

            // Write the number of players to the file
            writer.write(this.num_players + "\n");

            // Write the current player to the file
            writer.write(this.current_player + "\n");

            // Write the scores to the file
            for (int i = 0; i < this.num_players; i++) {
                writer.write(this.scores[i] + "\n");
            }

            // Write the turn score to the file
            writer.write(this.turn_score + "\n");

            // Write the roll to the file
            writer.write(this.roll + "\n");

            // Write the turn over flag to the file
            writer.write(this.turn_over + "\n");

            // Close the file writer
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Deletes game data from persistent storage
     */
    private void deleteGameData() {
        // Create a file object
        File file = new File("game_data.txt");

        // Check if the file exists
        if (file.exists()) {
            // Delete the file
            file.delete();
        }
    }



    /**
     * Calling this method will start the game loop
     */
    public void startGame() {
        System.out.println("\nIt is now player " + (this.current_player + 1) + "'s turn");
        while (!this.checkWin()) {
            // Only prompt the user to roll if they roll is not 0
            // roll will only ever be zero if the player has not yet rolled
            // on the current turn
            if (roll != 0) {
                System.out.println("Enter 'r' to roll or 'h' to hold");
                Boolean valid_input = false;
                while (!valid_input) {
                    input = this.scanner.nextLine();

                    switch (input) {
                        case "r":
                            valid_input = true;
                            break;
                        case "h":
                            valid_input = true;
                            this.turn_over = true;
                            break;
                        default:
                            System.out.println("Invalid input");
                            break;
                    }
                }
            }

            if (this.turn_over) {
                // Add the turn score to the player's score
                this.scores[this.current_player] += this.turn_score;
                System.out.println("Player " + (current_player + 1) + "'s current score is "
                        + this.scores[this.current_player]);

                // Reset the turn score
                this.turn_score = 0;
                // Reset the roll
                this.roll = 0;
                // Reset the turn over flag
                this.turn_over = false;

                // Switch to the next player
                this.current_player = (this.current_player + 1) % this.num_players;
                System.out.println("\nIt is now player " + (this.current_player + 1) + "'s turn");

                // Print current players score
                System.out.println("Player " + (current_player + 1) + "'s score is "
                        + this.scores[this.current_player]);
            } else {
                // Roll the die
                this.roll = this.random.nextInt(6) + 1;
                System.out.println("You rolled a " + this.roll);

                // Check if the player rolled a 1
                if (this.roll == 1) {
                    System.out.println("You rolled a 1. Your turn is over");
                    this.turn_score = 0;
                    this.turn_over = true;
                    this.roll = 0;
                } else {
                    this.turn_score += roll;
                    System.out.println("Your turn score is " + this.turn_score);
                }
            }

            // Save the game at the end of every loop just incase the shutdown hook fails
            // or the program is terminated unexpectedly
            this.saveGameData();
        }
    }


    public static void main(String[] args) {


        PigGame game = PigGame.createGame(0);
        game.startGame();
    }
}
