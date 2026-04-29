package de.symeda.sormas.api.utils;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum SabinType {
    TYPE_1,
    TYPE_2,
    TYPE_3;
    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
    }
