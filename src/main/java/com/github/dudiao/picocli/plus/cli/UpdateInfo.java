package com.github.dudiao.picocli.plus.cli;

import lombok.Data;

import java.util.List;

/**
 * @author songyinyin
 * @since 2023/4/1 21:37
 */
@Data
public class UpdateInfo {

  /**
   * 是否需要升级
   */
  private boolean isNeedUpdate = false;

  /**
   * 当前版本
   */
  private String appVersion;

  /**
   * 最新版本
   */
  private String latestAppVersion;

  /**
   * 最新版本的资源集合
   */
  private List<String> urls;
}
