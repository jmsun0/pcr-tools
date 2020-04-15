package com.sjm.main;

// import org.bytedeco.javacpp.BytePointer;
// import org.bytedeco.opencv.global.opencv_imgcodecs;
// import org.bytedeco.opencv.opencv_core.Mat;
// import org.bytedeco.tesseract.TessBaseAPI;
// import org.mozilla.javascript.Context;
// import org.mozilla.javascript.tools.shell.Global;

public class Test0 {
    // public static void main(String[] args) throws Exception {
    // Context ctx = Context.enter();
    // ctx.setOptimizationLevel(-1);
    // Global global = new Global(ctx);
    // String jsStr = "for(var i=0;i<100;i++)print(1234) sss";
    // Object result = ctx.evaluateString(global, jsStr, null, 0, null);
    // System.out.println("result=" + result);
    // }
    //
    // public static void main1(String[] args) {
    // // System.out.println(JSONObject.toJSONString("1"));
    // // Arrays.asList(1, 2, 3).forEach(System.out::println);
    // System.setProperty("org.bytedeco.javacpp.cachedir.nosubdir", "true");
    // System.setProperty("org.bytedeco.javacpp.logger.debug", "true");
    // opencv_imgcodecs.imread(args[0]);
    // Mat mat = opencv_imgcodecs.imread(args[0]);
    // String whitelist = "";
    //
    // TessBaseAPI api = new TessBaseAPI();
    // if (api.Init("tessdata", "eng") != 0) {
    // System.err.println("Could not initialize tesseract.");
    // System.exit(1);
    // }
    // api.SetVariable("user_defined_dpi", "100");
    // api.SetImage(mat.data(), mat.cols(), mat.rows(), mat.channels(), (int) mat.step1());
    //
    // if (whitelist != null)
    // api.SetVariable("tessedit_char_whitelist", whitelist);
    // BytePointer outText = api.GetUTF8Text();
    // String result = outText.getString().replace(" ", "").replace("\n", "");
    // api.End();
    // outText.deallocate();
    // api.close();
    //
    // System.out.println(result);
    // }
}
