package com.github.dudiao.picocli.plus;

import cn.hutool.extra.spring.EnableSpringUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.github.dudiao.picocli.plus.cli.AutoCompleteCliServiceImpl;
import com.github.dudiao.picocli.plus.cli.PicocliPlusCli;
import com.github.dudiao.picocli.plus.cli.PicocliPlusCliService;
import com.github.dudiao.picocli.plus.cli.UpdateInfo;
import com.github.dudiao.picocli.plus.cli.DefaultUpgradeCliService;
import com.github.dudiao.picocli.plus.nativex.HutoolRuntimeHints;
import com.github.dudiao.picocli.plus.nativex.PicocliPlusRuntimeHits;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import picocli.CommandLine;

import java.util.List;

@Slf4j
@EnableSpringUtil
@AutoConfiguration
@EnableConfigurationProperties(PicocliPlusProperties.class)
@ImportRuntimeHints(value = {HutoolRuntimeHints.class, PicocliPlusRuntimeHits.class})
public class PicocliPlusAutoConfiguration implements CommandLineRunner, ExitCodeGenerator {

  private int exitCode;

  private CommandLineHolder commandLineHolder;

  @Bean
  public PicocliPlusCli nbootCli() {
    return new PicocliPlusCli();
  }

  @Bean
  public AutoCompleteCliServiceImpl autoCompleteCli() {
    return new AutoCompleteCliServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public DefaultUpgradeCliService upgradeCli(PicocliPlusProperties properties) {
    return new DefaultUpgradeCliService(properties);
  }

  @Bean
  public CommandLineHolder commandLineHolder(PicocliPlusCli picocliPlusCli, List<PicocliPlusCliService> picocliPlusCliServiceList) {
    commandLineHolder = new CommandLineHolder(picocliPlusCli, picocliPlusCliServiceList);
    return commandLineHolder;
  }

  @Override
  public void run(String... args) throws Exception {
    CommandLine commandLine = commandLineHolder.getCommandLine();
    DefaultUpgradeCliService upgradeCliService = SpringUtil.getBean(DefaultUpgradeCliService.class);
    UpdateInfo updateInfo = upgradeCliService.checkForUpdates();
    if (updateInfo.isNeedUpdate()) {
      String msg = """
          当前版本：{}, 最新版本：{}. 可以使用如下命令进行升级：
          java -jar xxx.jar upgrade
          或者：
          xxx upgrade
          """;
      log.info(msg, updateInfo.getAppVersion(), updateInfo.getLatestAppVersion());
    }
    exitCode = commandLine.execute(args);
  }

  @Override
  public int getExitCode() {
    return exitCode;
  }
}
