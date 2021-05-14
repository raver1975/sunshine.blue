package com.klemstinegroup.sunshineblue.engine.commands;

import java.util.Objects;

public class UndoCommand extends Command{

    Command command;

    public UndoCommand(){}
    public UndoCommand(Command command){
        this.command=command;
    }

    @Override
    public void execute() {
        this.command.undo();
    }

    @Override
    public void undo() {
        this.command.execute();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UndoCommand)) return false;
        UndoCommand undo = (UndoCommand) o;
        return command.equals(undo.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command);
    }
}
