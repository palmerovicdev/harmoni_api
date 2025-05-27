package com.palmerodev.harmoni_api.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Scheduler {

    @EventListener(ApplicationReadyEvent.class)
    public void handleEvent() {
        log.info("""
                     ___                ___            __  _                     __             __           __
                    /   |  ____  ____  / (_)________ _/ /_(_)___  ____     _____/ /_____ ______/ /____  ____/ /
                   / /| | / __ \\/ __ \\/ / / ___/ __ `/ __/ / __ \\/ __ \\   / ___/ __/ __ `/ ___/ __/ _ \\/ __  /\s
                  / ___ |/ /_/ / /_/ / / / /__/ /_/ / /_/ / /_/ / / / /  (__  ) /_/ /_/ / /  / /_/  __/ /_/ / \s
                 /_/  |_/ .___/ .___/_/_/\\___/\\__,_/\\__/_/\\____/_/ /_/  /____/\\__/\\__,_/_/   \\__/\\___/\\__,_/  \s
                       /_/   /_/                                                                              \s""");
    }

}
