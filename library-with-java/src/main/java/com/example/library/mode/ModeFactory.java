package com.example.library.mode;

import com.example.library.storage.FileChannelDataAccess;
import com.example.library.storage.MemoryConcurrentDataAccess;

import java.util.Arrays;
import java.util.function.Function;

public enum ModeFactory {
    TEST_MODE("테스트모드", (path) -> new TestMode(new MemoryConcurrentDataAccess(path))),
    REAL_MODE("실제모드", (path) -> new RealMode(new FileChannelDataAccess(path)));

    private final String title;
    private final Function<String, ModePolicy> modePolicy;

    ModeFactory(String title, Function<String, ModePolicy> modePolicy) {
        this.title = title;
        this.modePolicy = modePolicy;
    }

    public ModePolicy mode(String path) {
        return modePolicy.apply(path);
    }

    public static ModePolicy of(ModeFactory modeName, String path) {
        return Arrays.stream(values())
                .filter(modeFactory -> modeFactory.title.equals(modeName.title))
                .findFirst()
                .map(modeFactory -> modeFactory.mode(path))
                .orElseThrow(() -> new IllegalArgumentException("Invalid mode: " + modeName));
    }
}
