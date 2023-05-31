package shop.entities.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum KategorieEreignisTyp {
    SUCH_EREIGNIS,
    ARTIKEL_EREIGNIS,
    PERSONEN_EREIGNIS;

    private EreignisTyp ereignisTyps;

    KategorieEreignisTyp(){
    }
    KategorieEreignisTyp(EreignisTyp ereignisTyps) {
        this.ereignisTyps = ereignisTyps;
    }


    public EreignisTyp getEreignisTyps() {
        return ereignisTyps;
    }

}
