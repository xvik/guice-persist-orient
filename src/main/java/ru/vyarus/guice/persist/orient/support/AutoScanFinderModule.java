package ru.vyarus.guice.persist.orient.support;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.FinderModule;
import ru.vyarus.guice.persist.orient.finder.scanner.FinderScanner;

import java.util.List;

/**
 * Finder module which able to scan classpath to dynamically find and register finder interfaces.
 * Anyway, ability to manually add interfaces still remain: part of classpath may be scanned and finders from other
 * parts may be added manually. The reason for this could be some optional classes in classpath, which will cause class
 * loading exception (e.g. some jars are absent).
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
public class AutoScanFinderModule extends FinderModule {
    private final Logger logger = LoggerFactory.getLogger(AutoScanFinderModule.class);
    private final String[] packages;

    public AutoScanFinderModule(final String... packages) {
        super();
        Preconditions.checkArgument(packages != null && packages.length > 0,
                "At least one package is required to reduce classpath scanning scope.");
        this.packages = packages;
        registerFinders();
    }

    private void registerFinders() {
        final List<Class<?>> finderClasses = FinderScanner.scan(packages);
        for (Class<?> cls : finderClasses) {
            logger.info("Registering finder interface: {}", cls);
            addFinder(cls);
        }
    }
}
