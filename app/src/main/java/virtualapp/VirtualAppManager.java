package virtualapp;

import com.lody.virtual.remote.InstalledAppInfo;
import com.lody.virtual.remote.VAppInstallerResult;

public class VirtualAppManager {
    public static VirtualAppManager sVirtualAppManager = new VirtualAppManager();

    /**
     * 安装APK到虚拟机内部
     *
     * @param apk
     * @return
     */
    public VAppInstallerResult installApk(String apk) {
        return null;
    }

    /**
     * 卸载虚拟机内部的APP
     *
     * @param pkgName
     * @return
     */
    public boolean uninstallPackage(String pkgName) {
        return true;
    }

    /**
     * app是否已经安装到虚拟机内部
     *
     * @param pkg
     * @return
     */
    public boolean isAppInstalled(String pkg) {
        return false;
    }

    /**
     * 启动虚拟机内部安装的APP的信息
     *
     * @param pkg
     * @param flags
     * @return
     */
    public InstalledAppInfo getInstalledAppInfo(String pkg, int flags) {
        return null;
    }

    /**
     * 启动虚拟机内部的APP
     *
     * @param packageName
     * @return
     */
    public boolean launchApp(String packageName) {
        return false;
    }
}
