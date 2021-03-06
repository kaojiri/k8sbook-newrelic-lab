package k8sbook.backend.entity;

import java.util.function.Consumer;

public interface SampleAppEntity<T extends SampleAppEntity<T>> {

    default T apply(Consumer<T> consumer) {
        consumer.accept((T) this);
        return (T) this;
    }

}
