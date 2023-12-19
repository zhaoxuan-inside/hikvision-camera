package org.zhaoxuan.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zhaoxuan.biz.CameraBiz;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class Schedule {

    private final CameraBiz cameraBiz;

    @Scheduled(fixedDelayString = "${picture.gap}")
    public void saveImage() {
        cameraBiz.saveImage();
    }

}
