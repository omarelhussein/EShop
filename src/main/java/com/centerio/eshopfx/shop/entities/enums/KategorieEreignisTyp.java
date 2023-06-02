package com.centerio.eshopfx.shop.entities.enums;

public enum KategorieEreignisTyp {
    SUCH_EREIGNIS,
    ARTIKEL_EREIGNIS,
    PERSONEN_EREIGNIS,

    WARENKORB_EREIGNIS;

    private EreignisTyp ereignisTyps;

    KategorieEreignisTyp(EreignisTyp ereignisTyps) {
        this.ereignisTyps = ereignisTyps;
    }
    KategorieEreignisTyp(){

    }

    public void setEreignisTyps(EreignisTyp ereignisTyps){
        this.ereignisTyps = ereignisTyps;
    }
    public EreignisTyp getEreignisTyps() {
        return ereignisTyps;
    }

}
