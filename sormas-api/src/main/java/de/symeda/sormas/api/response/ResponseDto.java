package de.symeda.sormas.api.response;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

import java.util.Date;

@DependingOnFeatureType(featureType = {FeatureType.CASE_SURVEILANCE })
public class ResponseDto extends PseudonymizableDto {

    public static final String I18N_PREFIX = "Response";
    private static final long serialVersionUID = -5883189159873348012L;

    public static final String PROTECTIVE_DOSE_TT = "protectiveDoseTt";
    public static final String RESPONSE_DATE = "responseDate";
    public static final String SUPPLEMENTAL_IMMUNIZATION = "supplementalImmunization";
    public static final String RESPONSE_DETAILS = "responseDetails";

    private YesNoUnknown protectiveDoseTt;
    private Date responseDate;
    private YesNoUnknown supplementalImmunization;
    private String responseDetails;

    public static ResponseDto build() {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setUuid(DataHelper.createUuid());
        return responseDto;
    }

    public YesNoUnknown getProtectiveDoseTt() {
        return protectiveDoseTt;
    }

    public void setProtectiveDoseTt(YesNoUnknown protectiveDoseTt) {
        this.protectiveDoseTt = protectiveDoseTt;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public YesNoUnknown getSupplementalImmunization() {
        return supplementalImmunization;
    }

    public void setSupplementalImmunization(YesNoUnknown supplementalImmunization) {
        this.supplementalImmunization = supplementalImmunization;
    }

    public String getResponseDetails() {
        return responseDetails;
    }

    public void setResponseDetails(String responseDetails) {
        this.responseDetails = responseDetails;
    }
}
