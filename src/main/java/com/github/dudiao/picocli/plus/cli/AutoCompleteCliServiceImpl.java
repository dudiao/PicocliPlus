package com.github.dudiao.picocli.plus.cli;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.system.SystemUtil;
import com.github.dudiao.picocli.plus.CommandLineHolder;
import lombok.extern.slf4j.Slf4j;
import picocli.AutoComplete;
import picocli.CommandLine;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author songyinyin
 * @since 2022/11/27 11:24
 */
@Slf4j
@CommandLine.Command(name = "autoComplete", description = "生成自动补全脚本")
public class AutoCompleteCliServiceImpl implements PicocliPlusCliService {

  /**
   * /Users/songyinyin/study/nboot-cli/
   */
  public static final String CURRENT_DIR = SystemUtil.getUserInfo().getCurrentDir();

  /**
   * 用户目录下 ~/.bash_profile
   */
  public static final String bash_profile_path = SystemUtil.getUserInfo().getHomeDir() + ".bash_profile";

  public static final Pattern pattern = Pattern.compile("# add by nboot-cli([\\s\\S]*)# end nboot-cli.*\n");

  @CommandLine.Option(names = {"-n", "--name"}, defaultValue = "nboot", description = "脚本名称，默认为：nboot")
  private String scriptName;

  @CommandLine.Option(names = {"-j", "--jdk17home"},
      defaultValue = "/Users/songyinyin/.sdkman/candidates/java/22.3.r17-grl",
      description = "jdk17的路径")
  private String jdk17home;

  @Override
  public Integer call() throws Exception {

    if (SystemUtil.getOsInfo().isWindows()) {
      log.error("不支持Windows系统");
      return 1;
    }

    String appName = SpringUtil.getApplicationName();
    String appVersion = SpringUtil.getProperty("application.version");
    // userDir + scriptName + appName + appVersion有任何一个变动，都会进行备份
    String md5 = SecureUtil.md5(CURRENT_DIR + scriptName + appName + appVersion);

    File bashProfile = new File(bash_profile_path);
    String bashStr = FileUtil.readString(bashProfile, Charset.defaultCharset());
    // 如果没变化，不备份也不追加
    if (StrUtil.isNotBlank(bashStr) && bashStr.contains(md5)) {
      log.info("命令补全脚本已存在");
      return 0;
    }

    // 生成 xxx_complete 脚本
    File file = generateCompleteShell();

    // 备份 .bash_profile 文件
    backUp(bashProfile);

    // 添加命令到第一行
    replaceAndAdd(appName, appVersion, md5, bashProfile, bashStr);

    log.info("请执行以下命令，使{}生效: source {}", file.getName(), bashProfile.getAbsolutePath());
    return 0;
  }

  /**
   * 生成 xxx_complete 脚本
   */
  private File generateCompleteShell() {
    CommandLineHolder commandLineHolder = SpringUtil.getBean(CommandLineHolder.class);
    String bash = AutoComplete.bash(scriptName, commandLineHolder.getCommandLine());
    File file = new File(String.format("%s%s_completion", CURRENT_DIR, scriptName));
    log.info("生成的 命令补全脚本 路径为：{}", file.getAbsolutePath());
    FileUtil.writeBytes(bash.getBytes(), file);
    return file;
  }

  /**
   * 将原来的命令替换掉，并追加新命令到文件第一行
   *
   * @param bashStr .bash_profile文件中的内容
   */
  private void replaceAndAdd(String appName, String appVersion, String md5, File bashProfile, String bashStr) {
    Map<String, String> map = new HashMap<>();
    map.put("userDir", CURRENT_DIR);
    map.put("scriptName", scriptName);
    map.put("appName", appName);
    map.put("appVersion", appVersion);
    map.put("md5", md5);
    map.put("jdk17home", jdk17home);
    String writeStr = StrUtil.format("""
        # add by nboot-cli, don't change it!
        source {userDir}{scriptName}_completion
        alias {scriptName}="{jdk17home}/bin/java -jar {userDir}{appName}-{appVersion}.jar"
        # end nboot-cli, md5={md5}
        """, map);

    log.info("start writing file [{}] at first line", bashProfile.getAbsoluteFile());
    String replaceBashStr = StrUtil.replace(bashStr, pattern, parameters -> "");
    FileUtil.writeString(writeStr + replaceBashStr, bashProfile, Charset.defaultCharset());
  }

  /**
   * 备份文件
   *
   * @param bashProfile 待备份的文件
   */
  private static void backUp(File bashProfile) {
    String backUpPath = bashProfile.getAbsolutePath() + "_bak";
    File bakFile = new File(backUpPath);
    log.info("start backup {} to {}", bash_profile_path, backUpPath);
    FileUtil.copy(bashProfile, bakFile, true);
  }
}
