package com.github.dudiao.picocli.plus.nativex;

import cn.hutool.system.UserInfo;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import java.lang.reflect.Constructor;

/**
 * @author songyinyin
 * @since 2022/12/9 10:55
 */
public class HutoolRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    Constructor<?>[] constructors = UserInfo.class.getConstructors();
    for (Constructor<?> constructor : constructors) {
      hints.reflection().registerConstructor(constructor, ExecutableMode.INVOKE);
    }
  }
}
