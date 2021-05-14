package com.klemstinegroup.sunshineblue.engine.commands;

import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import sun.security.provider.Sun;

public abstract class Command {
    public String actionOnUUID;
    public int framePos = 0;
    public int arrayPos = 0;

    public Command() {
    }

    public static void insert(Command command, BaseObject bo) {
        insert(SunshineBlue.instance.frameCount, command, bo);
    }

    public static void setToFrame(int frame) {
        if (frame == SunshineBlue.instance.frameCount) {
            return;
        } else if (frame < SunshineBlue.instance.frameCount) {
            for (int i = SunshineBlue.instance.frameCount; i >= frame; i--) {
                Array<Command> subcom = SunshineBlue.instance.commands.get(i);
                if (subcom != null) {
                    for (int j = subcom.size - 1; j >= 0; j--) {
                        subcom.get(j).undo();
                    }
                }
            }
        } else {
            for (int i = frame; i < SunshineBlue.instance.frameCount; i++) {
                Array<Command> subcom = SunshineBlue.instance.commands.get(i);
                if (subcom != null) {
                    for (Command c : subcom) {
                        c.execute();
                    }
                }
            }
        }
    }

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
        if (bo != null) {
            command.actionOnUUID = bo.uuid;
        }
        if (!SunshineBlue.instance.commands.containsKey(frameAt)) {
            SunshineBlue.instance.commands.put(frameAt, new Array<Command>());
        }
        Array<Command> array = SunshineBlue.instance.commands.get(frameAt);
        array.add(command);
//        SunshineBlue.instance.commands.put(frameAt,array);
    }

    public static void remove(int frameAt, String uuid) {
        if (!SunshineBlue.instance.commands.containsKey(frameAt)) {
            return;
        }
        Array<Command> array = SunshineBlue.instance.commands.get(frameAt);
        Array<Command> remove = new Array<>();
        BaseObject bo = getBaseObject(uuid);
        for (Command com : array) {
            if (com.actionOnUUID.equals(uuid)) {
                remove.add(com);
            }
        }
        array.removeAll(remove, false);
    }
}
