package com.example.library.console;

import com.example.library.mode.ModeFactory;
import com.example.library.mode.ModePolicy;

public class LibraryConsole {

    public void selectMode(ModeFactory mode, String path) {
        ModePolicy selectedMode = ModeFactory.of(mode, path);

        selectedMode.apply();
    }
}
