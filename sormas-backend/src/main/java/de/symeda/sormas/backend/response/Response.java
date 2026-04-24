package de.symeda.sormas.backend.response;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;

import javax.persistence.*;
import java.util.Date;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;

@Entity
public class Response extends AbstractDomainObject {

    private static final long serialVersionUID = -5883189159873348012L;

    public static final String TABLE_NAME = "response";

    private YesNoUnknown protectiveDoseTt;
    private Date responseDate;
    private YesNoUnknown supplementalImmunization;
    private String responseDetails;

    @Enumerated(EnumType.STRING)
    public YesNoUnknown getProtectiveDoseTt() {
        return protectiveDoseTt;
    }

    public void setProtectiveDoseTt(YesNoUnknown protectiveDoseTt) {
        this.protectiveDoseTt = protectiveDoseTt;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    @Enumerated(EnumType.STRING)
    public YesNoUnknown getSupplementalImmunization() {
        return supplementalImmunization;
    }

    public void setSupplementalImmunization(YesNoUnknown supplementalImmunization) {
        this.supplementalImmunization = supplementalImmunization;
    }

    @Column(length = CHARACTER_LIMIT_BIG)
    public String getResponseDetails() {
        return responseDetails;
    }

    public void setResponseDetails(String responseDetails) {
        this.responseDetails = responseDetails;
    }
}
