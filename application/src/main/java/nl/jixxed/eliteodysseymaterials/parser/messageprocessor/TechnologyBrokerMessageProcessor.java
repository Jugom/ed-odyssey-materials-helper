package nl.jixxed.eliteodysseymaterials.parser.messageprocessor;

import lombok.extern.slf4j.Slf4j;
import nl.jixxed.eliteodysseymaterials.domain.BrokerTraderJournalEvent;
import nl.jixxed.eliteodysseymaterials.enums.Commodity;
import nl.jixxed.eliteodysseymaterials.enums.HorizonsMaterial;
import nl.jixxed.eliteodysseymaterials.enums.StoragePool;
import nl.jixxed.eliteodysseymaterials.schemas.journal.TechnologyBroker.TechnologyBroker;
import nl.jixxed.eliteodysseymaterials.service.LocationService;
import nl.jixxed.eliteodysseymaterials.service.StorageService;
import nl.jixxed.eliteodysseymaterials.service.TraderBrokerService;
import nl.jixxed.eliteodysseymaterials.service.event.EventService;
import nl.jixxed.eliteodysseymaterials.service.event.StorageEvent;

import java.math.BigInteger;

@Slf4j
public class TechnologyBrokerMessageProcessor implements MessageProcessor<TechnologyBroker> {

    private static BigInteger marketId = BigInteger.ZERO;

    @Override
    public void process(final TechnologyBroker event) {
        //we only send events once per station visit
        if(!marketId.equals(LocationService.getMarketID())) {
            marketId = LocationService.getMarketID();
            TraderBrokerService.sendBrokerTraderEvent(new BrokerTraderJournalEvent(
                    event.getTimestamp(),
                    event.getEvent(),
                    event.getMarketID(),
                    event.getBrokerType()
            ));
        }
        event.getMaterials().forEach(material -> {
            try {
                final HorizonsMaterial horizonsMaterial = HorizonsMaterial.subtypeForName(material.getName());
                if (horizonsMaterial instanceof Commodity commodity && !horizonsMaterial.isUnknown()) {
                    StorageService.removeCommodity(commodity, StoragePool.SHIP, material.getCount().intValue());
                }
                if (!horizonsMaterial.isUnknown()) {
                    StorageService.removeMaterial(horizonsMaterial, material.getCount().intValue());
                }
            } catch (final IllegalArgumentException e) {
                log.error(e.getMessage());
                //NotificationService.showWarning(NotificationType.ERROR, "Unknown Material Detected", material.getName() + "\nPlease report!");
            }
        });
        EventService.publish(new StorageEvent(StoragePool.SHIP));

    }

    @Override
    public Class<TechnologyBroker> getMessageClass() {
        return TechnologyBroker.class;
    }
}