package one.nem.disablephotoproshutterforce;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Entry implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (!loadPackageParam.packageName.equals("com.sonymobile.photopro")) return;

        Class<?> clazz = null;
        Class<?> cameraIdClazz = null;

        try {
            clazz = loadPackageParam.classLoader.loadClass("com.sonymobile.photopro.util.capability.PlatformCapability");
            cameraIdClazz = loadPackageParam.classLoader.loadClass("com.sonymobile.photopro.device.CameraInfo$CameraId");
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                XposedBridge.log("Class not found: " + e.getMessage());
            } else {
                XposedBridge.log("Error loading classes: " + e.getMessage());
            }
        }

        if (clazz != null) {
            try {
                XposedHelpers.findAndHookMethod(clazz, "isForceSound", cameraIdClazz, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        // Force the sound to be off
                        param.setResult(false);
                    }
                });
            } catch (Exception e) {
                XposedBridge.log("Error hooking method isForceSound: " + e.getMessage());
            }
        }
    }
}
