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
        
        experiment.initPeers(0,10,peerFactory);
        // This line has changed
        experiment.startPeers(0,10);  
        
        // Run the Simulation
        experiment.runSimulation(Time.inSeconds(20));
        // dump the measurements
        MeasurementLog mlog = experiment.getRootMeasurementLog();
        
        // Log header
        System.out.print("\nEpochDur");
        System.out.print("\t");
        System.out.print("Each epoch started and 'ttl0_command' measurement");
        System.out.print("\n");
        
        for (int epochNumber=0; epochNumber<=20; epochNumber++){
            System.out.print(epochNumber*MainConfiguration.getSingleton().measurementEpochDuration/1000);
            System.out.print("\t\t");
            System.out.print(mlog.getAggregateByEpochNumber(epochNumber, "ttl0_command").getSum());
            System.out.print("\n");
        }
    }

}
