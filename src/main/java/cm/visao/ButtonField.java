package cm.visao;

import cm.modelo.Field;
import cm.modelo.FieldEvent;
import cm.modelo.FieldObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ButtonField extends JButton
        implements FieldObserver, MouseListener {

    private final Color BG_STANDARD = new Color(184, 184, 184);
    private final Color BG_MARKED = new Color(8, 179, 247);
    private final Color BG_EXPLOSION = new Color(189, 66, 68);
    private final Color TEXT_GREEN = new Color(0, 100, 0);

    private Field field;

    public  ButtonField(Field field) {
        this.field = field;
        setBackground(BG_STANDARD);
        setOpaque(true);
        setBorder(BorderFactory.createBevelBorder(0));

        addMouseListener(this);
        field.RegisterObserver(this);
    }

    @Override
    public void eventOccurred(Field field, FieldEvent event) {
        switch(event) {
            case OPEN:
                applyStylesheetOpen();
                break;
            case MARK:
                applyStylesheetMark();
                break;
            case EXPLOSION:
                applyStylesheetExplosion();
                break;
            default:
                applyStylesheetStandard();
        }

        SwingUtilities.invokeLater(() -> {
           repaint();
           validate();
        });

    }

    private void applyStylesheetStandard() {
        setBackground(BG_STANDARD);
        setBorder(BorderFactory.createBevelBorder(0));
        setText("");
    }

    private void applyStylesheetExplosion() {
        setBackground(BG_EXPLOSION);
        setForeground(Color.WHITE);
        setText("X");
    }

    private void applyStylesheetMark() {
        setBackground(BG_MARKED);
        setForeground(Color.black);
        setText("X");
    }

    private void applyStylesheetOpen() {

        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        if(field.isMined()) {
            setBackground(BG_EXPLOSION);
            setForeground(Color.WHITE);
            setText("X");
            return;
        }

        setBackground(BG_STANDARD);

        switch (field.minesNeighborhood()) {
            case 1:
                setForeground(TEXT_GREEN);
                break;
            case 2:
                setForeground(Color.blue);
                break;
            case 3:
                setForeground(Color.yellow);
                break;
            case 4:
            case 5:
            case 6:
                setForeground(Color.red);
                break;
            default:
                setForeground(Color.pink);
        }

        String value = !field.safeNeighborhood() ?
                field.minesNeighborhood() + "" : "";
        setText(value);
    }

    // Mouse interface events

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
            field.open();
        } else {
            field.toggleMarking();
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
}
