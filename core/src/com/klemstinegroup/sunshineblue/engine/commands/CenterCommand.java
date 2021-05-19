package com.klemstinegroup.sunshineblue.engine.commands;

import com.badlogic.gdx.math.Vector2;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class CenterCommand extends Command {
    Vector2 center = new Vector2();
    Vector2 oldcenter = new Vector2();


    public CenterCommand() {
    }

    public CenterCommand(Vector2 center, String uuid) {
        this.center.set(center);
        this.oldcenter.set(((ScreenObject)getBaseObject(uuid)).sd.center);
        this.actionOnUUID = uuid;
    }

    @Override
    public void execute() {
        System.out.println(center+"\t"+oldcenter);
        BaseObject bo = Command.getBaseObject(actionOnUUID);
        if (bo != null) {
            ((ScreenObject) bo).recenter(this.center);
        }
    }

    @Override
    public void undo() {
        BaseObject bo = Command.getBaseObject(actionOnUUID);
        if (bo != null) {
            ((ScreenObject) bo).recenter(this.oldcenter);
        }
    }

    @Override
    public boolean compress(Command command) {
        this.center.set(((CenterCommand)command).center);
        this.oldcenter.set(((CenterCommand)command).oldcenter);
        return true;
    }
}
