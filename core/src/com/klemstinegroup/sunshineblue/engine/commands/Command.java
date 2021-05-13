package com.klemstinegroup.sunshineblue.engine.commands;

import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;

public abstract class Command {
    public String actionOnUUID;
    public int framePos=0;
    public int arrayPos=0;

    public Command(){}

    public abstract void execute();

    public abstract void undo();

    public static BaseObject getBaseObject(String uuid) {
        for (BaseObject bo : SunshineBlue.instance.userObjects) {
            if (bo.uuid.equals(uuid)) {
                return bo;
            }
        }
        return null;
    }

    public static void insert(int frameAt, Command command, BaseObject bo) {
        if (bo!=null){
            command.actionOnUUID = bo.uuid;
        }
        if (!SunshineBlue.instance.commands.containsKey(frameAt)) {
            SunshineBlue.instance.commands.put(frameAt, new Array<Command>());
        }
        Array<Command> array = SunshineBlue.instance.commands.get(frameAt);
        array.add(command);
    }

    public static void remove(int frameAt, String uuid) {
        if (!SunshineBlue.instance.commands.containsKey(frameAt)) {
            return;
        }
        Array<Command> array = SunshineBlue.instance.commands.get(frameAt);
        Array<Command> remove = new Array<>();
        BaseObject bo=getBaseObject(uuid);
        for (Command com : array) {
            if (com.actionOnUUID.equals(uuid)) {
                remove.add(com);
            }
        }
        array.removeAll(remove, false);
    }
}
