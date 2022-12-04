package com.github.dudiao.picocli.plus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author songyinyin
 * @since 2022/12/4 16:10
 */
@SpringBootApplication
public class BaseTestPicocliPlusApplication {

  public static void main(String[] args) {
    System.exit(SpringApplication.exit(SpringApplication.run(BaseTestPicocliPlusApplication.class, args)));
  }

}
