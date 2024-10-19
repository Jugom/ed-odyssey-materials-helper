package nl.jixxed.eliteodysseymaterials.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import nl.jixxed.eliteodysseymaterials.enums.Good;
import nl.jixxed.eliteodysseymaterials.enums.StoragePool;
import nl.jixxed.eliteodysseymaterials.service.StorageService;

import java.util.Iterator;
@Slf4j
public class FleetCarrierGoodParser  {
    public void parse(final Iterator<JsonNode> items, final StoragePool storagePool) {
        items.forEachRemaining(itemNode ->
        {
            final String name = itemNode.get(getNameField()).asText();
            final Good good = Good.forName(name);
            final int amount = itemNode.get(getAmountField()).asInt();
            if (good.isUnknown()) {
                log.warn("Unknown Good detected: " + itemNode.toPrettyString());
                //NotificationService.showWarning(NotificationType.ERROR, "Unknown Material Detected", name + "\nPlease report!");
            } else {
                StorageService.addMaterial(good, storagePool, amount);
            }
        });
    }
    private String getAmountField() {
        return "quantity";
    }

    private String getNameField() {
        return "name";
    }
}
