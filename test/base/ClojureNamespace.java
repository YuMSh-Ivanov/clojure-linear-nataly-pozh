package base;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

import java.io.File;

public class ClojureNamespace {
    private final String ns;

    private static final IFn loadFile = Clojure.var("clojure.core", "load-file");
    private static final IFn require = Clojure.var("clojure.core", "require");

    private ClojureNamespace(final String ns) {
        this.ns = ns;
    }

    public static ClojureNamespace load(final String ns) {
        System.err.println("Loading namespace \"" + ns + "\"");
        loadFile.invoke("src" + File.separator + ns + ".clj");
        System.err.println("Namespace \"" + ns + "\" loaded");
        return require(ns);
    }

    public static ClojureNamespace require(final String ns) {
        require.invoke(Clojure.read(ns));
        return new ClojureNamespace(ns);
    }

    public IFn var(final String name) {
        return Clojure.var(ns, name);
    }
}
