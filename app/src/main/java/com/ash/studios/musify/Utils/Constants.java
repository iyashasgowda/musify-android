package com.ash.studios.musify.Utils;

public class Constants {
    public interface ACTION {
        String PLAY = "com.ash.studios.musify.Utils.action.play";
        String PAUSE = "com.ash.studios.musify.Utils.action.pause";
        String NEXT = "com.ash.studios.musify.Utils.action.next";
        String PREV = "com.ash.studios.musify.Utils.action.prev";
        String CREATE = "com.ash.studios.musify.Utils.action.create";
        String STOP_SERVICE = "com.ash.studios.musify.Utils.action.stopservice";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
}
