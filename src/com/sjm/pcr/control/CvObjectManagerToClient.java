package com.sjm.pcr.control;

import com.sjm.pcr.client_control.cv.CvObjectManager;
import com.sjm.pcr.common_component.rpc.Remote;

@Remote(value = "CvObjectManagerToClient", remote = RemoteCallClient.class,
        clazz = CvObjectManager.class)
public interface CvObjectManagerToClient extends CvObjectManager {

}
