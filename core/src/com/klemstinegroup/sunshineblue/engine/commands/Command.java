package com.klemstinegroup.sunshineblue.engine.commands;

import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;

public interface Command  {
    public void execute();

    public void undo();

    public static BaseObject getBaseObject(String uuid){
        for (BaseObject bo: SunshineBlue.instance.userObjects){
            if (bo.uuid.equals(uuid)){
                return bo;
            }
        }
        return null;
    }

    static void insert(int frameAt,Command command, BaseObject bo){
        if (!SunshineBlue.instance.commands.containsKey(frameAt)){
            SunshineBlue.instance.commands.put(frameAt,new Array<Command>());
        }
        Array<Command> array = SunshineBlue.instance.commands.get(frameAt);
        array.add(command);
        bo.commands.add(command);
    }
    static void remove(int frameAt,BaseObject bo){
        if (!SunshineBlue.instance.commands.containsKey(frameAt)){
            return;
        }
        Array<Command> array = SunshineBlue.instance.commands.get(frameAt);
        Array<Command> remove=new Array<>();
        for (Command com:array){
            if (bo.commands.contains(com,false)){
            remove.add(com);
            }
        }
        bo.commands.removeAll(remove,false);
        array.removeAll(remove,false);
    }
}
