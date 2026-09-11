package com.grim3212.assorted.lib.dist;

import com.grim3212.assorted.lib.platform.Services;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class DistExecutor {

    private static final Logger LOGGER = LogManager.getLogger();

    private DistExecutor() {
        throw new IllegalStateException("Can not instantiate an instance of: DistExecutor. This is a utility class");
    }

    /**
     * Calls the callable only on {@code dist}. Not side-safe: the callable must not trigger loading
     * of classes missing on the other side.
     *
     * @return the callable's result, or null on the other side
     * @deprecated use {@link #safeCallWhenOn(Dist, Supplier)}; this is for advanced use only
     */
    @Deprecated
    public static <T> T callWhenOn(Dist dist, Supplier<Callable<T>> toRun) {
        return unsafeCallWhenOn(dist, toRun);
    }

    public static <T> T unsafeCallWhenOn(Dist dist, Supplier<Callable<T>> toRun) {
        if (dist == Dist.current()) {
            try {
                return toRun.get().call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Calls the SafeCallable only on {@code dist}. {@code toRun} must supply a method reference to
     * a method in another class, or it fails the {@link SafeReferent} check.
     *
     * @return the result, or null on the other side
     */
    public static <T> T safeCallWhenOn(Dist dist, Supplier<SafeCallable<T>> toRun) {
        validateSafeReferent(toRun);
        return callWhenOn(dist, toRun::get);
    }

    /**
     * Runs the runnable only on {@code dist}; not side-safe, like
     * {@link #callWhenOn(Dist, Supplier)}.
     *
     * @deprecated use {@link #safeRunWhenOn(Dist, Supplier)}; this is for advanced use only
     */
    @Deprecated
    public static void runWhenOn(Dist dist, Supplier<Runnable> toRun) {
        unsafeRunWhenOn(dist, toRun);
    }

    /**
     * Runs the runnable only on {@code dist}. Not side-safe: the class verifier can load classes
     * the other side lacks. Prefer {@link #safeRunWhenOn(Dist, Supplier)}.
     */
    public static void unsafeRunWhenOn(Dist dist, Supplier<Runnable> toRun) {
        if (dist == Dist.current()) {
            toRun.get().run();
        }
    }

    /** Runs the SafeRunnable only on {@code dist}; see {@link SafeReferent}. */
    public static void safeRunWhenOn(Dist dist, Supplier<SafeRunnable> toRun) {
        validateSafeReferent(toRun);
        if (dist == Dist.current()) {
            toRun.get().run();
        }
    }

    /**
     * Runs whichever supplier matches the active side, e.g.
     * {@code DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new)}. The double
     * supplier keeps the other side's target from being class-loaded.
     *
     * @deprecated use {@link #safeRunForDist(Supplier, Supplier)}
     */
    @Deprecated
    public static <T> T runForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return unsafeRunForDist(clientTarget, serverTarget);
    }

    /**
     * Unsafe version of {@link #safeRunForDist(Supplier, Supplier)}: the verifier can still throw
     * ClassNotFoundException for code that looks unsided, so test both sides.
     */
    public static <T> T unsafeRunForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        switch (Dist.current()) {
            case CLIENT:
                return clientTarget.get().get();
            case DEDICATED_SERVER:
                return serverTarget.get().get();
            default:
                throw new IllegalArgumentException("UNSIDED?");
        }
    }

    /**
     * Runs whichever supplier matches the active side, e.g.
     * {@code DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new)}. The
     * double supplier keeps the other side's target from being class-loaded.
     */
    public static <T> T safeRunForDist(Supplier<SafeSupplier<T>> clientTarget, Supplier<SafeSupplier<T>> serverTarget) {
        validateSafeReferent(clientTarget);
        validateSafeReferent(serverTarget);
        switch (Dist.current()) {
            case CLIENT:
                return clientTarget.get().get();
            case DEDICATED_SERVER:
                return serverTarget.get().get();
            default:
                throw new IllegalArgumentException("UNSIDED?");
        }
    }

    /**
     * Marks a lambda that must be a method reference to a non-private method in another class,
     * checked at the call, so the caller never class-loads side-only code. Valid:
     * {@code safeCallWhenOn(Dist.CLIENT, () -> AnotherClass::clientOnlyMethod)}; invalid:
     * {@code safeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().level)}.
     */
    public interface SafeReferent {
    }

    /** The {@link SafeReferent} form of a {@link Callable}. */
    public interface SafeCallable<T> extends SafeReferent, Callable<T>, Serializable {
    }

    /**
     * SafeSupplier version of {@link SafeReferent}
     *
     * @param <T> The return type of the Supplier
     */
    public interface SafeSupplier<T> extends SafeReferent, Supplier<T>, Serializable {
    }

    /**
     * SafeRunnable version of {@link SafeReferent}
     */
    public interface SafeRunnable extends SafeReferent, Runnable, Serializable {
    }

    private static void validateSafeReferent(Supplier<? extends SafeReferent> safeReferentSupplier) {
        if (Services.PLATFORM.isProduction()) return;

        final SafeReferent setter;
        try {
            setter = safeReferentSupplier.get();
        } catch (Exception e) {
            // Typically a class cast exception, just return out, expected.
            return;
        }

        for (Class<?> cl = setter.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                Method m = cl.getDeclaredMethod("writeReplace");
                m.setAccessible(true);
                Object replacement = m.invoke(setter);
                if (!(replacement instanceof SerializedLambda))
                    break;// custom interface implementation
                SerializedLambda l = (SerializedLambda) replacement;
                if (Objects.equals(l.getCapturingClass(), l.getImplClass())) {
                    LOGGER.fatal("Detected unsafe referent usage, please view the code at {}", Thread.currentThread().getStackTrace()[3]);
                    throw new RuntimeException("Unsafe Referent usage found in safe referent method");
                }
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException | InvocationTargetException e) {
                break;
            }
        }
    }
}
