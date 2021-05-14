package com.klemstinegroup.sunshineblue.engine.commands;

import com.badlogic.gdx.math.Vector2;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class RotateCommand extends Command {
    float delta = 0;


    public RotateCommand() {
    }

    public RotateCommand(float delta, String uuid) {
        this.delta=delta;
        this.actionOnUUID = uuid;
    }

    @Override
    public void execute() {
        BaseObject bo = Command.getBaseObject(actionOnUUID);
//        this.oldPosition.set(((ScreenObject) Command.getBaseObject(actionOnUUID)).sd.position);
        if (bo != null) {
            ((ScreenObject) bo).sd.rotation+=this.delta;
        }
    }

    @Override
    public void undo() {
        BaseObject bo = Command.getBaseObject(actionOnUUID);
        if (bo != null) {
            ((ScreenObject) bo).sd.rotation-=this.delta;
        }
    }

    @Override
    public boolean compress(Command command) {
        this.delta+=((RotateCommand) command).delta;
        return true;
    }
}
