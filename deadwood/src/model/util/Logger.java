/**
 *  Written by Yulo Leake.
 *  Winter 2015, CSCI 345 (Object Oriented Design)
 *
 *  Was supposed to be an abstract logger class that is extended by dummy and actual logger, but never came to be.
 */

package model.util;

public class Logger {

    public static void p(Integer intg){
        System.out.println(intg);
    }

    public static void p(Object obj){
        System.out.println(obj);
    }

    public static void p(String str){
        System.out.println(str);
    }

    public static void e(Integer intg){
        System.err.println(intg);
    }

    public static void e(Object obj){
        System.err.println(obj);
    }

    public static void e(String err){
        System.err.println(err);
    }
}
