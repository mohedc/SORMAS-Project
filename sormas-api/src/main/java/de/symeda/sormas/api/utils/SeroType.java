package de.symeda.sormas.api.utils;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum SeroType {
    SERO_TYPE_1,
    SERO_TYPE_2,
    SERO_TYPE_3;

    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
