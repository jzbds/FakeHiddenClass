# FakeHiddenClass
A utility class for disguising normal classes as hidden classes
 This utility has **only been tested on Java 17 (HotSpot)**. No compatibility guarantees are made for other JDK versions (e.g., 8, 11, 21) or other JVM implementations (e.g., OpenJ9, GraalVM).
It relies on `sun.misc.Unsafe` and specific memory offsets (`KLAZZ_OFFSET=16`, `ACCESS_FLAGS_OFFSET=0xA4`). These offsets are internal implementation details and may differ across JVM versions or platforms. Direct use can result in **JVM crashes, memory corruption, or undefined behavior**.
This tool is intended for research, framework development, and testing purposes only. The author **assumes no liability** for any data loss, system downtime, or business interruption caused by its use.
