package aurora.carevisionapiserver.global.fcm.dto;

public record AlarmPreviewResponse(long count) {
    public static AlarmPreviewResponse of(long count) {
        return new AlarmPreviewResponse(count);
    }
}
