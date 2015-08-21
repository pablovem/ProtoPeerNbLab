package experiments.simcommander;

import protopeer.*;
import protopeer.measurement.*;
import protopeer.network.*;
import protopeer.network.delayloss.*;
import protopeer.servers.bootstrap.*;
import protopeer.util.quantities.*;

import protopeer.PeerFactory;

public class CommandFlooderExperiment extends SimulatedExperiment {

    public static void main(String[] args) {
        Experiment.initEnvironment();
        CommandFlooderExperiment experiment = new CommandFlooderExperiment();
        experiment.init();
        
        PeerFactory peerFactory = new PeerFactory() {
            // This line has been changes as well
            public Peer createPeer(int peerIndex, Experiment experiment) {
                Peer newPeer = new Peer(peerIndex);
                if (peerIndex == 0) {
                    newPeer.addPeerlet(new BootstrapServer());
                }
                newPeer.addPeerlet(new NeighborManager());
                newPeer.addPeerlet(new SimpleConnector());
                newPeer.addPeerlet(new BootstrapClient(Experiment.getSingleton().getAddressToBindTo(0), new SimplePeerIdentifierGenerator()));
                newPeer.addPeerlet(new CommandFlooder(peerIndex == 0));
                return newPeer;
            }
        };
        
        experiment.initPeers(0,300,peerFactory);
        // This line has changed
        experiment.startPeers(0,1);  
        
        experiment.runSimulation(Time.inSeconds(20));
    }

}
