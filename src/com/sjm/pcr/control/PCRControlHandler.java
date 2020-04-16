package com.sjm.pcr.control;

import java.io.IOException;

import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.nio.core.ChannelContext;
import com.sjm.pcr.client_control.BaseClientHandler;

@Component
public class PCRControlHandler extends BaseClientHandler {
    static final Logger logger = LoggerFactory.getLogger(PCRControlHandler.class);

    @Override
    public void onConnect(ChannelContext ctx) throws IOException {
        super.onConnect(ctx);
    }

    @Override
    public void onClose(ChannelContext ctx) throws IOException {
        super.onClose(ctx);
        System.exit(1);
    }
}
