package ru.vyarus.guice.persist.orient.util.transactional

import com.google.inject.Injector
import com.orientechnologies.orient.core.tx.OTransaction
import org.spockframework.guice.GuiceInterceptor
import org.spockframework.runtime.IRunListener
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.IterationInfo
import org.spockframework.runtime.model.SpecInfo
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate

/**
 * @author Vyacheslav Rusakov 
 * @since 10.06.2015
 */
class TransactionalExtension extends AbstractAnnotationDrivenExtension<TransactionalTest> {

    Injector injector
    TransactionManager manager
    OTransaction.TXTYPE type;
    String name;

    @Override
    void visitFeatureAnnotation(TransactionalTest annotation, FeatureInfo feature) {
        type = annotation.value()
        name = feature.getName()
        feature.addInterceptor({
            if (manager == null) {
                GuiceInterceptor guiceInterceptor = feature.getSpec().sharedInitializerInterceptors.find {
                    if (it instanceof GuiceInterceptor) {
                        return it
                    }
                }
                assert guiceInterceptor != null
                injector = (Injector) guiceInterceptor["injector"]
                assert injector != null

                manager = injector.getInstance(TransactionManager.class)
            }
            IMethodInterceptor interceptor = new IMethodInterceptor() {
                @Override
                void intercept(IMethodInvocation invocation) throws Throwable {
                    injector.getInstance(TxTemplate).doInTransaction(new TxConfig(type), new TxAction<Void>() {
                        @Override
                        Void execute() throws Throwable {
                            return invocation.proceed()
                        }
                    })
                }
            }
            if (feature.isParameterized()) {
                // transaction for each iteration
                feature.addIterationInterceptor(interceptor)
            } else {
                feature.getFeatureMethod().addInterceptor(interceptor)
            }
        })
    }
}
