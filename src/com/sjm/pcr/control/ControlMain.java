package com.sjm.pcr.control;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.CommandLineRunner;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.mini.springboot.api.Resource;
import com.sjm.core.util.Misc;
import com.sjm.pcr.client_control.cv.CvConstants;
import com.sjm.pcr.client_control.cv.CvFactory;
import com.sjm.pcr.client_control.cv.CvObjectManager;
import com.sjm.pcr.client_control.cv.CvUtil;
import com.sjm.pcr.client_control.cv.Mat;
import com.sjm.pcr.client_control.cv.opencv_highgui;
import com.sjm.pcr.client_control.cv.opencv_imgcodecs;
import com.sjm.pcr.client_control.cv.opencv_imgproc;
import com.sjm.pcr.common.model.Rect;
import com.sjm.pcr.common.model.RectSize;
import com.sjm.pcr.common_component.rpc.RemoteContext;
import com.sjm.pcr.common_component.service.ClientService;
import com.sjm.pcr.common_component.service.CommonService;

@Component
public class ControlMain implements CommandLineRunner {
    static final Logger logger = LoggerFactory.getLogger(ControlMain.class);

    @Resource(name = "CommonServiceRemote")
    private CommonService commonService;

    @Autowired
    private ClientService clientManager;

    @Autowired
    private CvUtil cvUtil;

    @Autowired
    private CvFactory cvFactory;

    @Resource(name = "CvObjectManagerToClient")
    private CvObjectManager cvObjectManager;

    @Autowired
    private opencv_imgcodecs opencv_imgcodecs;

    @Autowired
    private opencv_imgproc opencv_imgproc;

    @Autowired
    private opencv_highgui opencv_highgui;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println(clientManager.listClient());
            RemoteContext.get().setRemoteName("xxx");
            // ResInfo res = commonService.runCmd(null, null, "true");
            // System.out.println(res);

            RectSize size = cvUtil.getWindowSize();
            // System.out.println(monitorService.getWindowSize());
            // monitorService.input("123456");


            System.out.println(cvObjectManager.listHandles());
            // cvObjectManager.clearHandles();
            // System.out.println(cvObjectManager.listHandles());

            // Mat mat = cvFactory.newMat();
            // System.out.println(mat);
            // mat.close();

            // Mat mat = opencv_imgcodecs.imread("/root/a.png");
            for (int i = 0; i < 10; i++) {
                Misc.startRecordTime();
                try (Mat mat = cvUtil.snapshot(new Rect(0, 0, size.width, size.height));) {
                    System.out.println(mat);
                    opencv_imgproc.cvtColor(mat, mat, CvConstants.COLOR_BGRA2GRAY);
                    opencv_imgcodecs.imwrite("/root/aaa.png", mat);
                }
                Misc.showRecordTime();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        System.exit(0);
    }
}
