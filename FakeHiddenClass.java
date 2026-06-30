package jzbds.stellar_finale.Utils;

import sun.misc.Unsafe;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public final class FakeHiddenClass {

    private static final Unsafe UNSAFE;
    private static final long KLAZZ_OFFSET = 16L;
    private static final long ACCESS_FLAGS_OFFSET = 0xA4L;
    private static final int JVM_ACC_HIDDEN = 0x4000000;
    private static final AtomicInteger hiddenCount = new AtomicInteger(0);

    static {
        UNSAFE = getUnsafe();
    }

    private FakeHiddenClass() {}

    private static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to obtain Unsafe instance", e);
        }
    }

    private static long klassAddrOf(Class<?> clazz) {
        try {
            return UNSAFE.getLong(clazz, KLAZZ_OFFSET);
        } catch (Throwable ignored) {
            return 0L;
        }
    }

    public static boolean makeHidden(Class<?> clazz) {
        long addr = klassAddrOf(clazz);
        if (addr == 0L) return false;
        try {
            int cur = UNSAFE.getInt(addr + ACCESS_FLAGS_OFFSET);
            int mod = cur | JVM_ACC_HIDDEN;
            if (mod == cur) return false;
            UNSAFE.putInt(addr + ACCESS_FLAGS_OFFSET, mod);
            int count = hiddenCount.incrementAndGet();
            if (count % 50 == 1) {
                System.out.println("[FakeHiddenClass] made hidden " + count
                        + " classes (latest: " + clazz.getName() + ")");
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean makeVisible(Class<?> clazz) {
        long addr = klassAddrOf(clazz);
        if (addr == 0L) return false;
        try {
            int cur = UNSAFE.getInt(addr + ACCESS_FLAGS_OFFSET);
            int mod = cur & ~JVM_ACC_HIDDEN;
            if (mod == cur) return false;
            UNSAFE.putInt(addr + ACCESS_FLAGS_OFFSET, mod);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static void makeHidden(Class<?>... classes) {
        for (Class<?> c : classes) makeHidden(c);
    }

    public static void makeVisible(Class<?>... classes) {
        for (Class<?> c : classes) makeVisible(c);
    }
}