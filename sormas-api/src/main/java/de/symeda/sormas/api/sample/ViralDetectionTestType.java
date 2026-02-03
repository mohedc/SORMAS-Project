package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ViralDetectionTestType {

    YELLOW_FEVER_IGM,
    MEASLES_IGM,
    RUBELLA_IGM,
    RVF_IGM,
    EBOLA_IGM;

    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
