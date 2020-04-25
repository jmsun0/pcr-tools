package com.sjm.pcr.common_component.rpc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sjm.core.json.JSONObject;
import com.sjm.core.logger.Logger;
import com.sjm.core.logger.LoggerFactory;
import com.sjm.core.mini.springboot.api.Autowired;
import com.sjm.core.mini.springboot.api.Component;
import com.sjm.core.nio.core.ChannelContext;
import com.sjm.core.nio.ext.ByteArrayWithFiles;
import com.sjm.core.util.ArrayController;
import com.sjm.core.util.Converters;
import com.sjm.core.util.Lists;
import com.sjm.core.util.Misc;
import com.sjm.pcr.common.constants.MsgSign;
import com.sjm.pcr.common.misc.ChannelContextHolder;
import com.sjm.pcr.common.misc.Promise;

@Component
public class RemoteCallSocketProcessor {
    static final Logger logger = LoggerFactory.getLogger(RemoteCallSocketProcessor.class);

    @Autowired
    private RemoteCallHandler remoteCallHandler;

    public void processRead(ChannelContext ctx, ByteArrayWithFiles data) throws Exception {
        JSONObject json = JSONObject.parseObject(new String(data.data));
        int flag = json.getInteger("flag", 0);
        String uuid = json.getString("uuid");
        switch (flag) {
            case MsgSign.FLAG_REMOTE_REQUEST: {
                RemoteCallRequest req = json.getObject("data", RemoteCallRequest.class);
                Class<?>[] types = req.getTypes();
                if (types != null && types.length != 0) {
                    for (int i = 0, j = 0; i < types.length; i++) {
                        Class<?> type = types[i];
                        if (type == File.class) {
                            req.getArgs()[i] = data.files.get(j++);
                        } else if (type == File[].class) {
                            Object arr = req.getArgs()[i];
                            ArrayController<Object, Object> ctr = ArrayController.valueOf(arr);
                            for (int k = 0, len = ctr.getLength(arr); k < len; k++) {
                                ctr.set(arr, k, data.files.get(j++));
                            }
                        }
                    }
                }
                ChannelContextHolder.set(ctx);
                try {
                    RemoteCallResponse res = remoteCallHandler.hanleRemoteCall(req);
                    SerializableRemoteCallResponse sres = serializeResponse(res);
                    sendMessage(ctx, MsgSign.FLAG_REMOTE_RESPONSE, uuid, sres, Lists.emptyList());
                } finally {
                    ChannelContextHolder.remove();
                }
                break;
            }
            case MsgSign.FLAG_REMOTE_RESPONSE: {
                Promise<RemoteCallResponse> pro = remoteResultMap.remove(uuid);
                SerializableRemoteCallResponse sres =
                        json.getObject("data", SerializableRemoteCallResponse.class);
                RemoteCallResponse res = deserializeResponse(sres);
                if (pro == null) {
                    logger.warn("Promise is null,uuid={}", uuid);
                } else {
                    pro.set(res, null);
                }
                break;
            }
            default:
                logger.warn("Invalid flag={},ingored", flag);
                break;
        }
    }

    private Map<String, Promise<RemoteCallResponse>> remoteResultMap = new HashMap<>();

    public RemoteCallResponse remoteCallSync(ChannelContext ctx, RemoteCallRequest req,
            List<File> files, long timeout) throws Exception {
        String uuid = UUID.randomUUID().toString();
        Promise<RemoteCallResponse> pro = new Promise<>();
        remoteResultMap.put(uuid, pro);
        sendMessage(ctx, MsgSign.FLAG_REMOTE_REQUEST, uuid, req, files);
        RemoteCallResponse result = pro.get(timeout);
        if (result == null)
            return new RemoteCallResponse(null, pro.getError());
        return result;
    }

    public static void sendMessage(ChannelContext ctx, int flag, String uuid, Object data,
            List<File> files) throws Exception {
        JSONObject resp = new JSONObject();
        resp.put("flag", flag);
        resp.put("uuid", uuid);
        resp.put("data", data);
        ctx.write(new ByteArrayWithFiles(resp.toString().getBytes(), files));
    }

    public List<File> getFiles(RemoteCallRequest req) {
        List<File> files = new ArrayList<>();
        Class<?>[] types = req.getTypes();
        if (types != null && types.length != 0) {
            for (int i = 0; i < types.length; i++) {
                Class<?> type = types[i];
                if (type == File.class) {
                    files.add((File) req.getArgs()[i]);
                } else if (type == File[].class) {
                    for (File file : (File[]) req.getArgs()[i])
                        files.add(file);
                }
            }
        }
        return files;
    }

    public SerializableRemoteCallResponse serializeResponse(RemoteCallResponse res)
            throws Exception {
        SerializableRemoteCallResponse sres = new SerializableRemoteCallResponse();
        sres.returnValue = res.getReturnValue();
        if (sres.returnValue != null)
            sres.returnType = sres.returnValue.getClass();
        if (res.getError() != null)
            sres.error = Misc.toBytes(res.getError());
        return sres;
    }

    public RemoteCallResponse deserializeResponse(SerializableRemoteCallResponse sres)
            throws Exception {
        RemoteCallResponse res = new RemoteCallResponse();
        if (sres.returnValue != null)
            res.setReturnValue(Converters.convert(sres.returnValue, sres.returnType));
        if (sres.error != null)
            res.setError((Throwable) Misc.toObject(sres.error));
        return res;
    }
}
