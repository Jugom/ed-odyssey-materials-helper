package nl.jixxed.eliteodysseymaterials.service.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Consumer;

@Getter
@Slf4j
class NonStaticEventListener<T extends Event> extends EventListener<T> {
    private final WeakReference<Object> ownerRef;

    NonStaticEventListener(final boolean fxThread, final Object owner, final Integer priority, final Class<T> eventClass, final Consumer<T> consumer) {
        super(fxThread, priority, eventClass, consumer);
        this.ownerRef = new WeakReference<>(owner);
    }

    @Override
    void handleEvent(final T event) {
        if (this.ownerRef == null || this.ownerRef.get() == null) {
            dispose();
        } else {
            this.consumer.accept(event);
        }
    }

    boolean hasOwner(final Object owner) {
        return Objects.equals(this.ownerRef.get(), owner);
    }

    private void dispose() {
        EventService.removeListener(this);
    }
}
