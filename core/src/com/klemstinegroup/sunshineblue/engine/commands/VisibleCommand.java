package com.klemstinegroup.sunshineblue.engine.commands;

import com.badlogic.gdx.math.Vector2;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class VisibleCommand extends Command {
    boolean visible = true;

    public VisibleCommand() {
    }

    public VisibleCommand(boolean visible, String uuid) {
        this.visible = visible;
        this.actionOnUUID = uuid;
    }

    @Override
    public void execute() {
        BaseObject bo = Command.getBaseObject(actionOnUUID);
        if (bo != null) {
            ((ScreenObject) bo).sd.visible = visible;
        }
    }

    @Override
    public void undo() {
        BaseObject bo = Command.getBaseObject(actionOnUUID);
        if (bo != null) {
            ((ScreenObject) bo).sd.visible = !visible;
        }
    }

    @Override
    public boolean compress(Command command) {
        this.visible = ((VisibleCommand) command).visible;
        return true;
    }
}
