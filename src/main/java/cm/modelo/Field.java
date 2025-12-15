package cm.modelo;

import java.util.ArrayList;
import java.util.List;

public class Field {

    private final int line;
    private final int column;

    private boolean mined = false;
    private boolean open = false;
    private boolean marked = false;

    private List<Field> neighbors = new ArrayList<>();
    private List<FieldObserver> observers = new ArrayList<>();

    Field(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public void RegisterObserver(FieldObserver observer) {
        observers.add(observer);
    }

    private void notifyObserver(FieldEvent event) {
        observers.stream()
                .forEach(o -> o.eventOccurred(this, event));
    }

    boolean addNeighbor(Field neighbor) {
        boolean differentLine = line != neighbor.line;
        boolean differentColumn = column != neighbor.column;
        boolean diagonal = differentLine && differentColumn;

        int deltaLine = Math.abs(line - neighbor.line);
        int deltaColumn = Math.abs(column - neighbor.column);
        int generalDelta = deltaLine + deltaColumn;

        if(generalDelta == 1 && !diagonal) {
            neighbors.add(neighbor);
            return true;
        } else if(generalDelta == 2 && diagonal) {
            neighbors.add(neighbor);
            return true;
        } else {
            return false;
        }
    }
    void toggleMarking() {
        if(!open) {
            marked = !marked;

            if(marked) {
                notifyObserver(FieldEvent.MARK);
            } else {
                notifyObserver(FieldEvent.UNMARK);
            }
        }
    }
    boolean open() {
        if(!open && !marked) {
            if(mined) {
                notifyObserver(FieldEvent.EXPLOSION);
                return true;
            }

            setOpen(true);

            if(safeNeighborhood()) {
                neighbors.forEach(v -> v.open());
            }
            return true;
        } else {
            return false;
        }
    }

    boolean safeNeighborhood() {
        return neighbors.stream().noneMatch(v -> v.mined);
    }

    void undermine() {
        mined = true;
    }

    public boolean isMarked() {
        return marked;
    }

    void setOpen(boolean open) {
        this.open = open;

        if(open) {
            notifyObserver(FieldEvent.OPEN);
        }
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isClosed() {
        return !open;
    }
    public boolean isMined() {
        return mined;
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    boolean objectiveAchieved() {
        boolean unveiled = !mined && open;
        boolean secure = mined && marked;
        return unveiled || secure;
    }

    long minesNeighborhood() {
        return neighbors.stream().filter(v -> v.mined).count();
    }

    void restart() {
        open = false;
        mined = false;
        marked = false;
    }
}
