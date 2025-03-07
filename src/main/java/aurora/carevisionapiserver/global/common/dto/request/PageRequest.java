package aurora.carevisionapiserver.global.common.dto.request;

public record PageRequest(Long lastIdx, int size) {
    public PageRequest(Long lastIdx, int size) {
        this.lastIdx = lastIdx;
        this.size = (size > 0) ? size : 8;
    }
}
