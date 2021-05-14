package com.klemstinegroup.sunshineblue.engine.commands;

import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class ScaleCommand extends Command {
    float delta = 0;


    public ScaleCommand() {
    }

    public ScaleCommand(float delta, String uuid) {
        this.delta=delta;
        this.actionOnUUID = uuid;
    }

    @Override
    public void execute() {
        BaseObject bo = Command.getBaseObject(actionOnUUID);
//        this.oldPosition.set(((ScreenObject) Command.getBaseObject(actionOnUUID)).sd.position);
        if (bo != null) {
            ((ScreenObject) bo).sd.scale+=this.delta;
        }
    }

    @Override
    public void undo() {
        BaseObject bo = Command.getBaseObject(actionOnUUID);
        if (bo != null) {
            ((ScreenObject) bo).sd.scale-=this.delta;
        }
    }

    @Override
    public boolean compress(Command command) {
        this.delta+=((ScaleCommand) command).delta;
        return true;
    }
}
