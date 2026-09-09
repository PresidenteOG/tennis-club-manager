package Tennis_ERP.TennisErp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PistaOcupacionDTO {
    private String nombrePista;
    private List<String> horasOcupadas;
}