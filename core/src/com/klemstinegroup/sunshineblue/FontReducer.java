package com.klemstinegroup.sunshineblue;

import java.io.File;
import java.util.*;

public class FontReducer {
    public static void main(String[] args){
        File f=new File("android/assets/fonts/fonts");
        File[] files=f.listFiles();
//        for (File ff:files){
//            System.out.println(ff);
//        }
        List<File> filelist=new ArrayList<File>();
        for (File ff:files){
            filelist.add(ff);
        }
        System.out.println(files.length);
        Collections.shuffle(filelist);
        while(filelist.size()>1000){
            System.out.println("deleting:"+filelist.get(0).getName());
            filelist.get(0).delete();
            filelist.remove(0);
        }
    }

}
