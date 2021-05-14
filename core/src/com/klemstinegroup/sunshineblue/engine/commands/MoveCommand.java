package com.klemstinegroup.sunshineblue.engine.commands;

import com.badlogic.gdx.math.Vector2;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

import java.util.Objects;

public class MoveCommand extends Command {
    Vector2 delta = new Vector2();

    public MoveCommand(){}

    public MoveCommand(Vector2 delta, String uuid) {
//        this.oldPosition.set(((ScreenObject) Command.getBaseObject(uuid)).sd.position);
        this.delta = delta;
        this.actionOnUUID = uuid;
    }

    @Override
    public void execute() {
        BaseObject bo=Command.getBaseObject(actionOnUUID);
//        this.oldPosition.set(((ScreenObject) Command.getBaseObject(actionOnUUID)).sd.position);
        if (bo!=null) {
            ((ScreenObject) bo).sd.position.add(this.delta);
        }
    }

    @Override
    public void undo() {
        BaseObject bo=Command.getBaseObject(actionOnUUID);
        if (bo!=null) {
            ((ScreenObject) bo).sd.position.sub(this.delta);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoveCommand)) return false;
        MoveCommand move = (MoveCommand) o;
        return delta.equals(move.delta) && actionOnUUID.equals(move.actionOnUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delta, actionOnUUID);
    }
}
