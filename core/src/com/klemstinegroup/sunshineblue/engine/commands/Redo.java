package com.klemstinegroup.sunshineblue.engine.commands;

import java.util.Objects;

public class Redo extends Command{

    Command command;

    public Redo(Command command){
        this.command=command;
    }

    @Override
    public void execute() {
        this.command.execute();
    }

    @Override
    public void undo() {
        this.command.undo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Redo)) return false;
        Redo redo = (Redo) o;
        return command.equals(redo.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command);
    }
}