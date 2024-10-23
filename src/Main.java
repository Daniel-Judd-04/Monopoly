import com.danieljudd.monopoly.*;

import java.util.Scanner;

public class Main {
    public static Game game;

    public static void main(String[] args) {
        startGame();
        String again = "";
        while (!again.equals("NO")) {
            System.out.print("\n".repeat(20) + "Do you want to play AGAIN? [Yes or No] - ");
            again = new Scanner(System.in).nextLine().toUpperCase();
            if (again.equals("YES")) startGame();
        }
    }

    public static void startGame() {
        game = new Game();
        game.start();
    }
}