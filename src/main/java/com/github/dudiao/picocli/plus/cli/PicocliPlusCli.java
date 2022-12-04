package com.github.dudiao.picocli.plus.cli;

import com.github.dudiao.picocli.plus.PicocliPlusVersionProvider;
import picocli.CommandLine;

/**
 * @author songyinyin
 * @since 2022/11/26 20:34
 */
@CommandLine.Command(name = "PicocliPlus", mixinStandardHelpOptions = true, versionProvider = PicocliPlusVersionProvider.class, description = "常用工具集合")
public class PicocliPlusCli implements Runnable {

  @CommandLine.Spec
  CommandLine.Model.CommandSpec spec;

  @Override
  public void run() {
    // if the command was invoked without subcommand, show the usage help
    spec.commandLine().usage(System.err);
  }
}
