package meowtils.jade;

import wtf.tatp.meowtils.extension.Extension;
import wtf.tatp.meowtils.Meowtils;

public class JadeLoaderExtension extends Extension {

    public boolean enabled = true;
    public int key = 0;

    public JadeLoaderExtension() {
        super("Jade Loader", "Mega");
        this.info("Installs Jade on Lunar. Needs a restart.");
        this.button("Load Jade", 5.0f, new Runnable() {
            @Override
            public void run() {
                report(JadeInstaller.install());
            }
        });
        this.button("Unload Jade", 5.0f, new Runnable() {
            @Override
            public void run() {
                report(JadeInstaller.uninstall());
            }
        });
        this.button("Check Status", 5.0f, new Runnable() {
            @Override
            public void run() {
                report(JadeInstaller.status());
            }
        });
    }

    private static void report(String message) {
        if (message != null) {
            Meowtils.addMessage(message);
        }
    }
}
