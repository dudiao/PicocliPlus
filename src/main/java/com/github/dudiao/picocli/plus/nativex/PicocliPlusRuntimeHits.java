package com.github.dudiao.picocli.plus.nativex;

import com.github.dudiao.picocli.plus.PicocliPlusVersionProvider;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import java.lang.reflect.Constructor;

/**
 * @author songyinyin
 * @since 2022/12/9 11:33
 */
public class PicocliPlusRuntimeHits implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    Constructor<?>[] constructors = PicocliPlusVersionProvider.class.getConstructors();
    for (Constructor<?> constructor : constructors) {
      hints.reflection().registerConstructor(constructor, ExecutableMode.INVOKE);
    }

  }
}
