package com.klemstinegroup.sunshineblue.engine.commands;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.util.UUID;

import java.util.Objects;

public abstract class Command {
    public String actionOnUUID;
    transient String uuid = UUID.randomUUID().toString();
    public int framePos = 0;
    public int arrayPos = 0;
    transient static boolean[] doneundun=new boolean[Statics.RECMAXFRAMES+1];
    public Command() {
    }

    public abstract void execute();

    public abstract void undo();

    public abstract boolean compress(Command command);

    public static void compress(int frame) {
        Array<Command> subcom = SunshineBlue.instance.commands.get(frame);
        if (subcom != null) {
            int s = subcom.size;
            int kk = 0;

            while (kk < subcom.size) {
                Command c1 = subcom.get(kk);
                Array<Command> remove = new Array<>();
                for (int i = kk + 1; i < subcom.size; i++) {
                    Command c2 = subcom.get(i);
                    if (!c1.actionOnUUID.equals(c2.actionOnUUID)) {
                        continue;
                    }
                    if (c1.getClass().toString().equals(c2.getClass().toString())) {
                        if (c1.compress(c2)) {
                            remove.add(c2);
                        }
                    }
                }
//                kk-=remove.size;
                subcom.removeAll(remove, false);
                kk++;
            }
        }
    }

    public static void insert(Command command, BaseObject bo) {
        insert(SunshineBlue.instance.frameCount, command, bo);
    }


    public static float getStateTime(){
        return SunshineBlue.instance.frameCount/SunshineBlue.fps;
    }

    public static void setToFrame(int frame) {
//        System.out.println("frame:"+frame+"\tframeCount"+SunshineBlue.instance.frameCount);
       /* if (frame == SunshineBlue.instance.frameCount) {
            if (!doneundun[frame]){

            }
            return;
        } else*/
            if (frame < SunshineBlue.instance.frameCount) {
            for (int i = SunshineBlue.instance.frameCount; i > frame; i--) {
                if (!doneundun[i]){
                    continue;
                }
                doneundun[i]=false;
                Array<Command> subcom = SunshineBlue.instance.commands.get(i);
                if (subcom != null) {
                    for (int j = subcom.size - 1; j > -1; j--) {
                        subcom.get(j).undo();
                    }
                }
            }
        } else {
            for (int i = SunshineBlue.instance.frameCount; i < frame; i++) {
                if (doneundun[i]){
                    continue;
                }
                doneundun[i]=true;
                Array<Command> subcom = SunshineBlue.instance.commands.get(i);
                if (subcom != null) {
                    for (Command c : subcom) {
                        c.execute();
                    }
                }
            }
        }
        SunshineBlue.instance.startTime = TimeUtils.millis() - (long) ((frame * 1000f) / SunshineBlue.fps);
        SunshineBlue.instance.frameCount = frame;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Command)) return false;
        Command command = (Command) o;
        return uuid.equals(command.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
