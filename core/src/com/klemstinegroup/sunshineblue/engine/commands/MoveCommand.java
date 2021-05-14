package com.klemstinegroup.sunshineblue.engine.commands;

import com.badlogic.gdx.math.Vector2;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;
import com.klemstinegroup.sunshineblue.engine.util.UUID;

import java.util.Objects;

public class MoveCommand extends Command {
    Vector2 delta = new Vector2();


    public MoveCommand() {
    }

    public MoveCommand(Vector2 delta, String uuid) {
        this.delta.set(delta);
        this.actionOnUUID = uuid;
    }

    @Override
    public void execute() {
        BaseObject bo = Command.getBaseObject(actionOnUUID);
        if (bo != null) {
            ((ScreenObject) bo).sd.position.add(this.delta);
        }
        System.out.println("move:" + this.delta.x + "," + this.delta.y);
    }

    @Override
    public void undo() {
        BaseObject bo = Command.getBaseObject(actionOnUUID);
        if (bo != null) {
            ((ScreenObject) bo).sd.position.sub(this.delta);
        }
    }

    @Override
    public boolean compress(Command command) {
        this.delta.add(((MoveCommand) command).delta);
        return true;
    }
}
