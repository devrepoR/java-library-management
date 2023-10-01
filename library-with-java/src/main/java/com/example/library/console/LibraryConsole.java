package com.example.library.console;

import com.example.library.console.mode.ModeFactory;
import com.example.library.console.mode.ModePolicy;

import java.util.logging.Logger;

public class LibraryConsole {

    private static final Logger log = Logger.getLogger(LibraryConsole.class.getName());

    public void selectMode(ModeFactory mode, String path) {
        ModePolicy selectedMode = ModeFactory.of(mode, path);
        log.info("Selected mode: " + selectedMode.getClass().getSimpleName());
    }
}
