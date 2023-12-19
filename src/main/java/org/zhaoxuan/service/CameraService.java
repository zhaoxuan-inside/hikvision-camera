package org.zhaoxuan.service;

import MvCameraControlWrapper.CameraControlException;
import MvCameraControlWrapper.MvCameraControlDefines;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public interface CameraService {


    ArrayList<MvCameraControlDefines.MV_CC_DEVICE_INFO> scanCamera()
            throws CameraControlException;

    List<MvCameraControlDefines.Handle> openCamera(List<MvCameraControlDefines.MV_CC_DEVICE_INFO> cameras)
            throws CameraControlException;

    void setCaptureMode(MvCameraControlDefines.Handle handle);

    void startGrabPicture(MvCameraControlDefines.Handle handle);

    void stopGrabPicture(MvCameraControlDefines.Handle handle);

    void destroyHandle(MvCameraControlDefines.Handle handle);

    void saveImage(MvCameraControlDefines.Handle handle);

    byte[] getPictureFromCamera(MvCameraControlDefines.Handle handle,
                                MvCameraControlDefines.MV_FRAME_OUT_INFO stImageInfo);
}
