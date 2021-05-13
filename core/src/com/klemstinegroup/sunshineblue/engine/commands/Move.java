package com.klemstinegroup.sunshineblue.engine.commands;

import com.badlogic.gdx.math.Vector2;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

import java.util.Objects;

public class Move implements Command {
    Vector2 oldPosition = new Vector2();
    Vector2 newPosition = new Vector2();
    String uuid;
//    boolean relative;

    public Move(Vector2 newPosition, String uuid) {
        this.oldPosition.set(((ScreenObject) Command.getBaseObject(uuid)).sd.position);
        this.newPosition = newPosition;
        this.uuid = uuid;
    }

    @Override
    public void execute() {
        ((ScreenObject) Command.getBaseObject(uuid)).sd.position.set(this.newPosition);
    }

    @Override
    public void undo() {
        ((ScreenObject) Command.getBaseObject(uuid)).sd.position.set(this.oldPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return newPosition.equals(move.newPosition) && uuid.equals(move.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newPosition, uuid);
    }
}
