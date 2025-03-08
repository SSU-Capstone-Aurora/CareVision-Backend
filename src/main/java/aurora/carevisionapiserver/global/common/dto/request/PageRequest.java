package aurora.carevisionapiserver.global.common.dto.request;

public final class PageRequest extends BasePageRequest<Long> {
    public PageRequest(Long lastIdx) {
        super(lastIdx);
    }

    public PageRequest(Long lastIdx, int size) {
        super(lastIdx, size);
    }

    public Long getLastIdx() {
        return super.identifier;
    }
}
