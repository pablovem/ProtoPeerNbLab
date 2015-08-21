package experiments.simcommander;

import protopeer.network.*;

public class CommandMessage extends Message {
  public String command;
  public int ttl;
}
