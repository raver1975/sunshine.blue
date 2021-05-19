package com.klemstinegroup.sunshineblue.engine.commands;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ParticleObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class TransformCommand extends Command {
    Vector2 posDelta = new Vector2();
    float rotDelta=0;
    float scaleDelta=0;


    public TransformCommand() {
    }

    public TransformCommand(Vector2 posDelta, float rotDelta, float scaleDelta, String uuid) {
        this.posDelta.set(posDelta);
        this.rotDelta=rotDelta;
        this.scaleDelta=scaleDelta;
        this.actionOnUUID = uuid;
    }

    @Override
    public void execute() {
        BaseObject bo = Command.getBaseObject(actionOnUUID);
        if (bo != null) {
            ((ScreenObject) bo).transform(this.posDelta,rotDelta,scaleDelta);
        }
    }

    @Override
    public void undo() {
        BaseObject bo = Command.getBaseObject(actionOnUUID);
        if (bo != null) {
            ((ScreenObject) bo).invtransform(this.posDelta,rotDelta,scaleDelta);
        }
    }

    @Override
    public boolean compress(Command command) {
        this.posDelta.add(((TransformCommand) command).posDelta);
        this.rotDelta+=((TransformCommand) command).rotDelta;
        this.scaleDelta+=((TransformCommand) command).scaleDelta;
        return true;
    }
}
