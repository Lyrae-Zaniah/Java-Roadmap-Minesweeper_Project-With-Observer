package cm.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Board implements FieldObserver {

    private int lines;
    private int columns;
    private int mines;

    private final List<Field> fields = new ArrayList<>();

    public Board(int lines, int columns, int mines) {
        this.lines = lines;
        this.columns = columns;
        this.mines = mines;

        generateFields();
        associateTheNeighbors();
        sortTheMines();
    }

    public void open(int line, int column) {
        try {
            fields.parallelStream()
                    .filter(f -> f.getLine() == line  && f.getColumn() == column)
                    .findFirst()
                    .ifPresent(f -> f.open());
        } catch (Exception e) {
            // FIXME Ajustar a implementação do método abrir
            fields.forEach(f -> f.setOpen(true));
            throw e;
        }
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

    @Override
    public void eventOccurred(Field field, FieldEvent event) {
        if(event == FieldEvent.EXPLOSION) {
            System.out.println("Perdeu...");
        } else if(objectiveAchieved()) {
            System.out.println("Ganhou");
        }
    }
}
