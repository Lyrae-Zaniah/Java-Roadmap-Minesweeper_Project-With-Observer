package cm.visao;

import cm.modelo.Board;
import javax.swing.*;
import java.awt.*;

public class PainelBoard extends JPanel {

    public PainelBoard(Board board) {

        setLayout(new GridLayout(
                board.getLines(), board.getColumns()));

        board.forEachField(c -> add(new ButtonField(c)));
        board.registerObserver(e -> {

            SwingUtilities.invokeLater(() -> {
            if(e.isWon()) {
                JOptionPane.showMessageDialog(this, "O JOGADOR GANHOU !!!");
            } else {
                JOptionPane.showMessageDialog(this, "O JOGADOR PERDEU :(");
            }

            board.restart();
            });
        });
    }
}
