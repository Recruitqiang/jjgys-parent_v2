package glgc.jjgys.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JgCommonEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String proname;
    private String sjz;
}
