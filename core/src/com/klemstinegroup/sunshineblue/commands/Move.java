package com.klemstinegroup.sunshineblue.commands;

import com.badlogic.gdx.math.Vector2;

public class Move implements Command{
    Vector2 oldPosition = new Vector2();
    Vector2 newPosition = new Vector2();
    String uuid;
//    boolean relative;

    public Move(Vector2 oldPosition, Vector2 newPosition, String uuid) {
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.uuid = uuid;
    }

    @Override
    public void execute() {

    }

    @Override
    public void deexecute() {

    }
}
