package cm.visao;

import cm.modelo.Board;

import javax.swing.*;

public class mainScreen extends JFrame {

    public mainScreen() {
        Board board = new Board(16, 30, 50);
        add(new PainelBoard(board));

        setTitle("Campo Minado");
        setSize(690, 438);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


    static void main(String[] args) {
        new mainScreen();
    }
}
