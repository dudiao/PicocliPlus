package com.github.dudiao.picocli.plus;

import com.github.dudiao.picocli.plus.cli.PicocliPlusCli;
import com.github.dudiao.picocli.plus.cli.PicocliPlusCliService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine;

import java.util.List;

/**
 * @author songyinyin
 * @since 2022/11/27 11:44
 */
public class CommandLineHolder {
  private final PicocliPlusCli picocliPlusCli;
  @Autowired
  private List<PicocliPlusCliService> picocliPlusCliServiceList;

  public CommandLineHolder(PicocliPlusCli picocliPlusCli, List<PicocliPlusCliService> picocliPlusCliServiceList) {
    this.picocliPlusCli = picocliPlusCli;
    this.picocliPlusCliServiceList = picocliPlusCliServiceList;
  }

  private CommandLine commandLine;

  public CommandLine getCommandLine() {
    return commandLine;
  }

  @PostConstruct
  public void initCommandLine() {
    CommandLine commandLine = new CommandLine(picocliPlusCli);
    for (PicocliPlusCliService picocliPlusCliService : picocliPlusCliServiceList) {
      commandLine.addSubcommand(picocliPlusCliService);
    }
    this.commandLine = commandLine;
  }
}
