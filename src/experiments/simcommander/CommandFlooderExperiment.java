package experiments.simcommander;

import protopeer.*;
import protopeer.measurement.*;
import protopeer.network.*;
import protopeer.network.delayloss.*;
import protopeer.servers.bootstrap.*;
import protopeer.util.quantities.*;

import protopeer.PeerFactory;

// SimulatedExperiment uses SimulatedClock and one of the available simulated network models
// LiveExperiment uses the RealClock and a networking implementation using TCP and UDP
public class CommandFlooderExperiment extends SimulatedExperiment {

    public static void main(String[] args) {
        // ProtoPeer environment initialization
        Experiment.initEnvironment();
        
        // Experiment instance (single instance of Experiment per JVM)
        CommandFlooderExperiment experiment = new CommandFlooderExperiment();
        
        // Experiment instance initialization
        experiment.init();
        
        PeerFactory peerFactory = new PeerFactory() {
            // (Peer, peerlets) creation, peers config 
            public Peer createPeer(int peerIndex, Experiment experiment) {
                Peer newPeer = new Peer(peerIndex);
                
                if (peerIndex == 0) {
                    newPeer.addPeerlet(new BootstrapServer());
                }
                
                newPeer.addPeerlet(new NeighborManager());
                newPeer.addPeerlet(new SimpleConnector());
                newPeer.addPeerlet(new BootstrapClient(Experiment.getSingleton().getAddressToBindTo(0), new SimplePeerIdentifierGenerator()));
                newPeer.addPeerlet(new CommandFlooder(peerIndex == 0));
                
                System.out.println("Peer " + peerIndex + " created.");
                
                return newPeer;
            }
        };
        
        int numPeersInSim = MainConfiguration.getSingleton().numPeersInSim;
        System.out.println("Num Peers in Simulation: " + numPeersInSim);
        // initPeer(int startIndex, int numPeers, PeerFactory perrFactory)
        experiment.initPeers(0,numPeersInSim,peerFactory);
        // Start Peers
        experiment.startPeers(0,numPeersInSim);  
        
        // Run the Simulation
        experiment.runSimulation(Time.inSeconds(20));
        // dump the measurements
        MeasurementLog mlog = experiment.getRootMeasurementLog();
        
        // Log header``
        System.out.print("\nEpochDur");
        System.out.print("\t");
        // Each epoch started and 'ttl0_command' measurement
        System.out.print("'ttl0_command'");
        System.out.print("\t");
        System.out.print("'message_count'");
        System.out.print("\t");
        System.out.print("Min Epoch");
        System.out.print("\t");
        System.out.print("Max Epoch");
        
        System.out.print("\n");
        
        for (int epochNumber=0; epochNumber<=20; epochNumber++){
            System.out.print(epochNumber*MainConfiguration.getSingleton().measurementEpochDuration/1000);
            System.out.print("\t\t");
            System.out.print(mlog.getAggregateByEpochNumber(epochNumber, "ttl0_command").getSum());
            System.out.print("\t\t");
            System.out.print(mlog.getAggregateByEpochNumber(epochNumber, "message_count").getSum());
            System.out.print("\t\t");
            System.out.print(mlog.getMinEpochNumber());
            System.out.print("\t\t");
            System.out.print(mlog.getMaxEpochNumber());
            System.out.print("\n");  
        }
    }
    
    @Override
    public NetworkInterfaceFactory createNetworkInterfaceFactory() {
        // Lossless network model: all messages have a uniformely randomly delay chosen [500,2000] ms 
        return new DelayLossNetworkInterfaceFactory(getEventScheduler(),new UniformDelayModel(500,2000));
    }

}
