package cm.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Board implements FieldObserver {

    private final int lines;
    private final int columns;
    private final int mines;

    private final List<Field> fields = new ArrayList<>();
    private final List<Consumer<ResultEvent>> observers =
            new ArrayList<>();

    public Board(int lines, int columns, int mines) {
        this.lines = lines;
        this.columns = columns;
        this.mines = mines;

        generateFields();
        associateTheNeighbors();
        sortTheMines();
    }

    public void forEachField(Consumer<Field> function) {
        fields.forEach(function);
    }

    public void registerObserver(Consumer<ResultEvent> observer) {
        observers.add(observer);
    }

    private void notifyObserver(Boolean result) {
        observers.stream()
                .forEach(o -> o.accept(new ResultEvent(result)));
    }

    public void open(int line, int column) {
            fields.parallelStream()
                    .filter(f -> f.getLine() == line  && f.getColumn() == column)
                    .findFirst()
                    .ifPresent(f -> f.open());
    }

    public void toggleMarking(int line, int column) {
        fields.parallelStream()
                .filter(f -> f.getLine() == line  && f.getColumn() == column)
                .findFirst()
                .ifPresent(f -> f.toggleMarking());
    }


    private void generateFields() {
        for(int line = 0; line < lines; line++) {
            for(int column = 0; column < columns; column++) {
                Field field = new Field(line, column);
                field.RegisterObserver(this);
                fields.add(field);
            }
        }
    }
    private void associateTheNeighbors() {
        for(Field c1: fields) {
            for(Field c2: fields) {
                c1.addNeighbor(c2);
            }
        }
    }
    public void sortTheMines() {
        long armedMines = 0;
        Predicate<Field> mined = field -> field.isMined();

        do {
            int random = (int) (Math.random() * fields.size());
            fields.get(random).undermine();
            armedMines = fields.stream().filter(mined).count();
        } while(armedMines < mines);
    }
    public boolean objectiveAchieved() {
        return fields.stream().allMatch(f -> f.objectiveAchieved());
    }

    public void restart() {
        fields.stream().forEach(f -> f.restart());
        sortTheMines();
    }

    public int getLines() {
        return lines;
    }

    public int getColumns() {
        return columns;
    }

    @Override
    public void eventOccurred(Field field, FieldEvent event) {
        if(event == FieldEvent.EXPLOSION) {
            showMines();
            notifyObserver(false);
        } else if(objectiveAchieved()) {
            System.out.println("Ganhou");
            notifyObserver(true);
        }
    }

    private void showMines() {
        fields.stream()
                .filter(c -> c.isMined())
                .filter(c -> !c.isMarked())
                .forEach(f -> f.setOpen(true));
    }
}
