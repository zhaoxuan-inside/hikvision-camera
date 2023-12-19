package org.zhaoxuan.biz.impl;

import MvCameraControlWrapper.CameraControlException;
import MvCameraControlWrapper.MvCameraControlDefines;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zhaoxuan.biz.CameraBiz;
import org.zhaoxuan.service.CameraService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

import static MvCameraControlWrapper.MvCameraControl.MV_CC_SetCommandValue;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class CameraBizImpl implements CameraBiz {

    private List<MvCameraControlDefines.Handle> handles = new ArrayList<>();
    private final CameraService cameraService;

    @Override
    public void saveImage() {

        for (MvCameraControlDefines.Handle handle : handles) {
            MV_CC_SetCommandValue(handle, "TriggerSoftware");
            cameraService.saveImage(handle);
        }

    }

    @PostConstruct
    private void camera() throws CameraControlException {
        ArrayList<MvCameraControlDefines.MV_CC_DEVICE_INFO> cameras = cameraService.scanCamera();
        handles = cameraService.openCamera(cameras);
        for (MvCameraControlDefines.Handle handle : handles) {
            cameraService.startGrabPicture(handle);
        }
    }

    @PreDestroy
    private void cameraDestroy() {
        for (MvCameraControlDefines.Handle handle : handles) {
            cameraService.destroyHandle(handle);
        }
    }

}
