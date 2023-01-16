package dependency_injector.instance;

import dependency_injector.DependencyInjector;
import dependency_injector.class_marker.processor.InjectProcessor;
import dependency_injector.class_marker.processor.InjectableMetadata;
import dependency_injector.utils.TreeNode;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

public class InstanceBuilder {

    private static final Logger LOGGER = Logger.getLogger(DependencyInjector.class.getName());

    private final InjectProcessor injectProcessor;

    public InstanceBuilder(InjectProcessor injectProcessor) {
        this.injectProcessor = injectProcessor;
    }

    public List<TreeNode<Object>> build(List<TreeNode<InjectableMetadata>> injectableDependencies) {

        List<TreeNode<Object>> result = new ArrayList<>();

        for (TreeNode<InjectableMetadata> injectable : injectableDependencies) {

            InjectableMetadata data = injectable.getData();

            Class<?> aClass = data.aClass();
            Class<?> injectableClass = isNull(aClass) ? data.field().getDeclaringClass() : aClass;

            Object instance = createInstance(injectableClass);

            TreeNode<Object> parent = new TreeNode<>(instance);
            parent.addChildren(build(injectable.getChildren()));
            result.add(parent);
        }
        return result;
    }

    private Object createInstance(Class<?> aClass) {
        Constructor<?>[] classConstructors = aClass.getDeclaredConstructors();
        Constructor<?> singleConstructor = processConstructors(classConstructors); // without parameters
        try {
            return singleConstructor.newInstance();
        } catch (Exception e) {
            throw new InstanceCreatingErrorException("Cannot create instance for: " + singleConstructor.getName(), e);
        }
    }

    private Object createInstance() {
//        Constructor<?> constructorWithoutParameters = processConstructors(someClass.getConstructors());
//        constructorWithoutParameters.newInstance();
        return null;
    }

    private Object createInstanceByConstructor(final List<Constructor<?>> injectableByConstructors) {
//        Object instance = null;
//        try {
//            Constructor<?> constructor = processConstructors(injectableByConstructors.toArray(new Constructor<?>[0]));
//            instance = constructor.newInstance();
//        } catch (Exception e) {
//            LOGGER.info("Constructors were not found"); // TODO: but what about empty constructor which always exists?
//        }
//        return instance;
        return null;
    }

    private Constructor<?> processConstructors(final Constructor<?>[] constructors) {
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return constructor;
            }
        }
        throw new RuntimeException(); // TODO: get rid of this bad practice
    }

    private static final class NotOnlyConstructorException extends RuntimeException {

        NotOnlyConstructorException(String message) {
            super(message);
        }
    }

    private static final class InstanceCreatingErrorException extends RuntimeException {

        InstanceCreatingErrorException(String message, Exception exception) {
            super(message, exception);
        }
    }
}
