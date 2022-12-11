package com.github.dudiao.picocli.plus;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.github.dudiao.picocli.plus.cli.AutoCompleteCliServiceImpl;
import com.github.dudiao.picocli.plus.cli.PicocliPlusCli;
import com.github.dudiao.picocli.plus.cli.PicocliPlusCliService;
import com.github.dudiao.picocli.plus.nativex.HutoolRuntimeHints;
import com.github.dudiao.picocli.plus.nativex.PicocliPlusRuntimeHits;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import picocli.CommandLine;

import java.util.List;

@EnableSpringUtil
@AutoConfiguration
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
  public CommandLineHolder commandLineHolder(PicocliPlusCli picocliPlusCli, List<PicocliPlusCliService> picocliPlusCliServiceList) {
    commandLineHolder = new CommandLineHolder(picocliPlusCli, picocliPlusCliServiceList);
    return commandLineHolder;
  }

  @Override
  public void run(String... args) throws Exception {
    CommandLine commandLine = commandLineHolder.getCommandLine();
    exitCode = commandLine.execute(args);
  }

  @Override
  public int getExitCode() {
    return exitCode;
  }
}
