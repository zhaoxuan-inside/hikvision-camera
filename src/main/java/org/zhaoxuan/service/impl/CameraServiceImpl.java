package org.zhaoxuan.service.impl;

import MvCameraControlWrapper.CameraControlException;
import MvCameraControlWrapper.MvCameraControl;
import MvCameraControlWrapper.MvCameraControlDefines;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zhaoxuan.common.FileUtil;
import org.zhaoxuan.service.CameraService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static MvCameraControlWrapper.MvCameraControl.MV_CC_EnumDevices;
import static MvCameraControlWrapper.MvCameraControlDefines.MV_GIGE_DEVICE;

@Slf4j
@Service
public class CameraServiceImpl implements CameraService {

    @Override
    public ArrayList<MvCameraControlDefines.MV_CC_DEVICE_INFO> scanCamera()
            throws CameraControlException {
        return MV_CC_EnumDevices(MV_GIGE_DEVICE);
    }

    @Override
    public List<MvCameraControlDefines.Handle> openCamera(List<MvCameraControlDefines.MV_CC_DEVICE_INFO> cameras)
            throws CameraControlException {

        List<MvCameraControlDefines.Handle> handles = new ArrayList<>();
        for (MvCameraControlDefines.MV_CC_DEVICE_INFO camera : cameras) {
            MvCameraControlDefines.Handle handle = MvCameraControl.MV_CC_CreateHandle(camera);
            int result = MvCameraControl.MV_CC_OpenDevice(handle);
            if (result != 0) {
                log.warn("OpenCameraFailure.CameraName:[{}],ErrorCode:[{}].",
                        camera.gigEInfo.userDefinedName,
                        Integer.toHexString(result));
                continue;
            }
            setCaptureMode(handle);
            handles.add(handle);
        }
        return handles;
    }

    @Override
    public void setCaptureMode(MvCameraControlDefines.Handle handle) {
        MvCameraControl.MV_CC_SetEnumValueByString(handle, "AcquisitionMode", "Continuous");
        MvCameraControl.MV_CC_SetEnumValueByString(handle, "TriggerMode", "On");
        MvCameraControl.MV_CC_SetEnumValueByString(handle, "TriggerSource", "Software");
    }

    @Override
    public void startGrabPicture(MvCameraControlDefines.Handle handle) {
        MvCameraControl.MV_CC_StartGrabbing(handle);
    }

    @Override
    public void stopGrabPicture(MvCameraControlDefines.Handle handle) {
        MvCameraControl.MV_CC_StopGrabbing(handle);
    }

    @Override
    public void destroyHandle(MvCameraControlDefines.Handle handle) {
        MvCameraControl.MV_CC_DestroyHandle(handle);
    }

    @Override
    public void saveImage(MvCameraControlDefines.Handle handle) {
        MvCameraControlDefines.MV_FRAME_OUT_INFO stImageInfo = new MvCameraControlDefines.MV_FRAME_OUT_INFO();
        byte[] imageBuffer = getPictureFromCamera(handle, stImageInfo);
        if (imageBuffer.length == 0) {
            log.info("PictureIsEmpty.");
            return;
        }
        FileUtil.saveToLocalDisk(imageBuffer, "D:\\",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss.SSS")) + ".jpg");
    }

    public byte[] getPictureFromCamera(MvCameraControlDefines.Handle handle,
                                       MvCameraControlDefines.MV_FRAME_OUT_INFO stImageInfo) {
        MvCameraControlDefines.MV_SAVE_IMAGE_PARAM stSaveParam = new MvCameraControlDefines.MV_SAVE_IMAGE_PARAM();
        MvCameraControlDefines.MVCC_INTVALUE stParam = new MvCameraControlDefines.MVCC_INTVALUE();
        MvCameraControl.MV_CC_GetIntValue(handle, "PayloadSize", stParam);
        byte[] pData = new byte[(int) stParam.curValue];
        MvCameraControl.MV_CC_GetOneFrameTimeout(handle, pData, stImageInfo, 1000);
        byte[] imageBuffer = new byte[stImageInfo.width * stImageInfo.height * 3];
        setImageParams(stSaveParam, stImageInfo, pData, imageBuffer);
        MvCameraControl.MV_CC_SaveImage(handle, stSaveParam);
        return imageBuffer;
    }

    private static void setImageParams(MvCameraControlDefines.MV_SAVE_IMAGE_PARAM stSaveParam,
                                       MvCameraControlDefines.MV_FRAME_OUT_INFO stImageInfo,
                                       byte[] pData,
                                       byte[] imageBuffer) {

        stSaveParam.width = stImageInfo.width;
        stSaveParam.height = stImageInfo.height;
        stSaveParam.data = pData;
        stSaveParam.dataLen = stImageInfo.frameLen;
        stSaveParam.pixelType = stImageInfo.pixelType;
        stSaveParam.imageBuffer = imageBuffer;
        stSaveParam.imageType = MvCameraControlDefines.MV_SAVE_IAMGE_TYPE.MV_Image_Jpeg;
        stSaveParam.methodValue = 0;
        stSaveParam.jpgQuality = 60;

    }

}
