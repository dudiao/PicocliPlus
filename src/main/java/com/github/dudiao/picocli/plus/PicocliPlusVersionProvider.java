package com.github.dudiao.picocli.plus;

import org.springframework.boot.SpringBootVersion;
import picocli.CommandLine;

/**
 * 项目版本
 *
 * @author songyinyin
 * @since 2022/8/9 18:07
 */

public class PicocliPlusVersionProvider implements CommandLine.IVersionProvider {

  @Override
  public String[] getVersion() throws Exception {
    String springbootVersion = String.format(":: SpringBoot  :: v(%s)", SpringBootVersion.getVersion());
    String nbootCliVersion = String.format(":: PicocliPlus :: v(%s)", PicocliPlusVersion.getVersion());
    return new String[]{springbootVersion, nbootCliVersion};
  }
}
