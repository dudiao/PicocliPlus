package com.github.dudiao.picocli.plus.cli;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrMatcher;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.github.dudiao.picocli.plus.PicocliPlusException;
import com.github.dudiao.picocli.plus.PicocliPlusProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author songyinyin
 * @since 2022/12/28 17:08
 */
@Slf4j
@CommandLine.Command(name = "upgrade", description = "自动升级")
public class DefaultUpgradeCliService implements PicocliPlusCliService {

  @CommandLine.Option(names = {"-f", "--full"}, description = "全量升级，不指定时，仅升级依赖包")
  boolean fullUpgrade;

  private final PicocliPlusProperties properties;

  public DefaultUpgradeCliService(PicocliPlusProperties properties) {
    this.properties = properties;
  }

  private static final ApplicationHome APPLICATION_HOME = new ApplicationHome();
  private static final String TMP_DIR = FileUtil.getUserHomePath() + "/.picocli/cache/upgrade";

  private static final String latestReleaseTemplate = "https://api.github.com/repos/{owner}/{repo}/releases/latest";
  private static final String GITHUB_REPO_URL_TEMPLATE = "https://github.com/{owner}/{repo}";

  public static final StrMatcher GITHUB_LATEST_RELEASE_MATCHER = new StrMatcher(latestReleaseTemplate);
  public static final StrMatcher GITHUB_REPO_MATCHER = new StrMatcher(GITHUB_REPO_URL_TEMPLATE);

  @Override
  public Integer call() throws Exception {
    StopWatch stopWatch = new StopWatch("update");
    stopWatch.start("check for updates");
    UpdateInfo updateInfo = checkForUpdates();
    stopWatch.stop();
    if (!updateInfo.isNeedUpdate()) {
      log.info("当前已是最新版本，无需更新");
      return 0;
    }

    // 清除缓存
    FileUtil.del(TMP_DIR);
    List<String> downloadUrls = choseDownloadUrls(updateInfo.getUrls());
    if (CollUtil.isEmpty(downloadUrls)) {
      log.warn("未找到下载资源：{}", updateInfo.getUrls());
      return 1;
    }

    String applicationName = SpringUtil.getApplicationName();
    if (StrUtil.isBlank(applicationName)) {
      applicationName = "picocli-plus";
    }

    String downloadFile = TMP_DIR + "/" + applicationName;

    for (String downloadUrl : downloadUrls) {
      HttpUtil.downloadFile(downloadUrl, downloadFile);
    }

    FileUtil.copy(new File(downloadFile), APPLICATION_HOME.getDir(), true);
    return 0;
  }

  protected List<String> choseDownloadUrls(List<String> urls) {
    return urls.stream().filter(url -> {
      OsInfo osInfo = SystemUtil.getOsInfo();
      if (osInfo.isWindows() && url.contains("-win")) {
        return true;
      }
      if (osInfo.isMac() && url.contains("-mac")) {
        return true;
      }
      if (osInfo.isLinux() && url.contains("-linux")) {
        return true;
      }
      return false;
    }).collect(Collectors.toList());
  }

  /**
   * 检查是否需要更新
   */
  public UpdateInfo checkForUpdates() throws PicocliPlusException {

    String latestRelease;
    if (StrUtil.isNotBlank(properties.getGithubLatestRelease())) {
      latestRelease = properties.getGithubLatestRelease();
    } else {
      String githubRepoUrl = properties.getGithubRepoUrl();
      if (StrUtil.isBlank(githubRepoUrl)) {
        throw new PicocliPlusException("github repo url is not null, please config: picocli-plus.github-repo-url");
      }
      Map<String, String> match = GITHUB_REPO_MATCHER.match(githubRepoUrl);
      if (CollUtil.isEmpty(match) || match.size() != 2) {
        throw new PicocliPlusException("github repo url format is incorrect: https://github.com/{owner}/{repo}");
      }
      String repo = StrUtil.removeSuffix(match.get("repo"), ".git");
      latestRelease = StrUtil.format(latestReleaseTemplate, MapUtil.of(Pair.of("owner", match.get("owner")), Pair.of("repo", repo)));
    }
    // 3秒超时
    String reposeBody = HttpUtil.get(latestRelease, 3000);
    JSON parse = JSONUtil.parse(reposeBody);
    String latestVersion = parse.getByPath("tag_name", String.class);

    UpdateInfo updateInfo = new UpdateInfo();
    if (StrUtil.isBlank(latestVersion)) {
      return updateInfo;
    }

    String appVersion = properties.getAppVersion();
    log.debug("当前版本：{}，最新版本：{}", appVersion, latestVersion);
    updateInfo.setAppVersion(appVersion);
    updateInfo.setLatestAppVersion(latestVersion);

    int result = CharSequenceUtil.compareVersion(latestVersion, appVersion);
    if (result > 0) {
      JSONArray assets = parse.getByPath("assets", JSONArray.class);
      List<String> browserDownloadUrls = assets.stream()
          .map(e -> ((JSONObject) e).getStr("browser_download_url"))
          .toList();
      updateInfo.setNeedUpdate(true);
      updateInfo.setUrls(browserDownloadUrls);
      return updateInfo;
    }
    return updateInfo;
  }

  public String getOwnerAndRepo() {
    if (StrUtil.isNotBlank(properties.getGithubLatestRelease())) {
      Map<String, String> match = GITHUB_LATEST_RELEASE_MATCHER.match(properties.getGithubLatestRelease());
      return StrUtil.format(GITHUB_REPO_URL_TEMPLATE, match);
    }

    return properties.getGithubRepoUrl();
  }
}
