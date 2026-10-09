package meowtils.jade;

import wtf.tatp.meowtils.extension.Extension;

public class Main {
    public static void init() {
        Extension.registerModule(new JadeLoaderExtension());
    }
}
