package com.github.dudiao.picocli.plus;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author songyinyin
 * @since 2023/1/18 10:10
 */
@Data
@ConfigurationProperties(prefix = "picocli-plus")
public class PicocliPlusProperties {

  /**
   * 当前应用的版本，用于自动升级时 版本的比较，比如：0.0.1，需要和 git release 的版本号格式相同
   */
  private String appVersion;

  /**
   * github获取最新的release版本的URL
   * <p>格式为：https://api.github.com/repos/{owner}/{repo}/releases/latest</p>
   * <p>比如：https://api.github.com/repos/dudiao/native-demo/releases/latest</p>
   */
  private String githubLatestRelease;

  /**
   * github仓库地址
   * <p>格式为：https://github.com/{owner}/{repo}</p>
   * <p>比如：https://github.com/dudiao/native-demo</p>
   */
  private String githubRepoUrl;


}
