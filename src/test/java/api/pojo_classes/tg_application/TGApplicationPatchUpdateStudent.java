package api.pojo_classes.tg_application;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TGApplicationPatchUpdateStudent {
    private String email;
    private String dob;
}
