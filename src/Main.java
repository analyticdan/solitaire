import backend.Board;
import frontend.GUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        Runnable ui = new GUI(board);
        ui.run();
    }
}
