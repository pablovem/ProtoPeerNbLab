package experiments.simcommander;

import protopeer.*;
import protopeer.network.*;
import protopeer.time.*;

public class CommandFlooder extends BasePeerlet {
    
    private final boolean captain;

    private Timer initialDelayTimer;

    public CommandFlooder(boolean captain) {
        this.captain = captain;
    }

    private NeighborManager getNeighborManager() {
        return (NeighborManager) getPeer().getPeerletOfType(NeighborManager.class);
    }

    @Override
    public void init(Peer peer) {
        super.init(peer);
        if (captain) {
            initialDelayTimer = getPeer().getClock().createNewTimer();
            initialDelayTimer.addTimerListener(new TimerListener() {
                @Override
                public void timerExpired(Timer timer) {
                    CommandMessage commandMessage = new CommandMessage();
                    commandMessage.command = "fire at will";
                    commandMessage.ttl = 4;
                    
                    System.out.println("\nCaptain gives the first command to " + getNeighborManager().getNumNeighbors() + " neighbors.");
                    
                    for (Finger neighbor : getNeighborManager().getNeighbors()) {
                        
                        System.out.println("peer " + getPeer().getNetworkAddress() + " (State= " + getPeer().getState() + ") " + " sent " + commandMessage + " to NetworkAddress: " + neighbor.getNetworkAddress());
                        
                        getPeer().sendMessage(neighbor.getNetworkAddress(), commandMessage);
                        getPeer().getMeasurementLogger().log("command_sent", 1);
                    }
                    
                }
            });
            initialDelayTimer.schedule(5e3);
        }
        
        System.out.println("Peer " + peer.getIndexNumber() + " (Peer State= " + getPeer().getState() + ", Peer Clock= " + getPeer().getClock().getCurrentTime() + ") ");
    }
    
    
    @Override
    public void handleIncomingMessage(Message message) {
              
        if (message instanceof CommandMessage) {
            
            System.out.println("peer " + getPeer().getNetworkAddress() + " (Peer State= " + getPeer().getState() + ", Peer Clock= " + getPeer().getClock().getCurrentTime() + ") " + " RECEIVED " + message);
            
            CommandMessage commandMessage = (CommandMessage) message;
            if (--commandMessage.ttl > 0) {
                for (Finger neighbor : getNeighborManager().getNeighbors()) {
                    getPeer().sendMessage(neighbor.getNetworkAddress(), commandMessage);
                    
                    System.out.println("peer " + getPeer().getNetworkAddress() + " (Peer State= " + getPeer().getState() + ", Peer Clock= " + getPeer().getClock().getCurrentTime() + ")" + " SENT " + message + " to " + neighbor.getNetworkAddress());
                }
            } else {
                getPeer().getMeasurementLogger().log("ttl0_command", 1);
            }
        
        getPeer().getMeasurementLogger().log("message_count", 1);
        System.out.println("message_count= " + getPeer().getMeasurementLogger().getMeasurementLog().getAggregate("message_count"));
        }
    }

}

