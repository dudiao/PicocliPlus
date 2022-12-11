package com.github.dudiao.picocli.plus;

import cn.hutool.system.UserInfo;
import com.github.dudiao.picocli.plus.nativex.HutoolRuntimeHints;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import java.lang.reflect.Constructor;


/**
 * @author songyinyin
 * @since 2022/12/9 11:05
 */
public class RuntimeHintsTest {

  @Test
  void shouldRegisterHints() {
    RuntimeHints hints = new RuntimeHints();
    new HutoolRuntimeHints().registerHints(hints, getClass().getClassLoader());

    Constructor<?>[] constructors = UserInfo.class.getConstructors();

    Assertions.assertThat(RuntimeHintsPredicates.reflection().onConstructor(constructors[0])).accepts(hints);
  }
}
