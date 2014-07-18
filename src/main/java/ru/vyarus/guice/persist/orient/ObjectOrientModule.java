package ru.vyarus.guice.persist.orient;

import com.google.common.base.Strings;
import com.google.inject.name.Names;
import com.google.inject.persist.PersistModule;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.aopalliance.intercept.MethodInterceptor;
import ru.vyarus.guice.persist.orient.internal.OrientPersistService;
import ru.vyarus.guice.persist.orient.internal.TransactionInterceptor;

public class ObjectOrientModule extends PersistModule {

    private String uri;
    private String user;
    private String password;
    private String pkg;

    private MethodInterceptor interceptor;

    public ObjectOrientModule(String uri, String user, String password) {
        this(uri, user, password, null);
    }

    public ObjectOrientModule(String uri, String user, String password, String basePackage) {
        this.uri = uri;
        this.user = user;
        this.password = password;
        this.pkg = basePackage;
    }

    @Override
    protected void configurePersistence() {
        bindConstant().annotatedWith(Names.named("orient.uri")).to(uri);
        bindConstant().annotatedWith(Names.named("orient.user")).to(user);
        bindConstant().annotatedWith(Names.named("orient.password")).to(password);
        // if package not provided empty string will mean root package (search all classpath)
        bindConstant().annotatedWith(Names.named("orient.model.package")).to(Strings.nullToEmpty(pkg));

        bind(OrientPersistService.class);
        bind(PersistService.class).to(OrientPersistService.class);
        bind(UnitOfWork.class).to(OrientPersistService.class);
        bind(OObjectDatabaseTx.class).toProvider(OrientPersistService.class);
        interceptor = new TransactionInterceptor();
        requestInjection(interceptor);
    }

    @Override
    protected MethodInterceptor getTransactionInterceptor() {
        return interceptor;
    }
}
