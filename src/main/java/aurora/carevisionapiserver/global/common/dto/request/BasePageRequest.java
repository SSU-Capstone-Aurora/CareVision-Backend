package aurora.carevisionapiserver.global.common.dto.request;

public abstract class BasePageRequest<T> {
    private static final int DEFAULT_SIZE = 8;

    protected final T identifier;
    protected final int size;

    public BasePageRequest(T identifier, int size) {
        this.identifier = identifier;
        this.size = getValidSize(size);
    }

    public BasePageRequest(T identifier) {
        this(identifier, DEFAULT_SIZE);
    }

    private int getValidSize(int size) {
        return (size > 0) ? size : DEFAULT_SIZE;
    }

    public int getSize() {
        return this.size;
    }
}
