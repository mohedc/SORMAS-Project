package de.symeda.sormas.backend.response;

import de.symeda.sormas.api.response.ResponseDto;
import de.symeda.sormas.api.response.ResponseFacade;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.util.DtoHelper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

@Stateless(name = "ResponseFacade")
public class ResponseFacadeEjb implements ResponseFacade {

    @EJB
    private ResponseService responseService;
    @EJB
    private CaseService caseService;

    public Response fillOrBuildEntity(ResponseDto source, Response target, boolean checkChangeDate) {

        if (source == null) {
            return null;
        }

        target = DtoHelper.fillOrBuildEntity(source, target, Response::new, checkChangeDate);

        target.setProtectiveDoseTt(source.getProtectiveDoseTt());
        target.setResponseDate(source.getResponseDate());
        target.setSupplementalImmunization(source.getSupplementalImmunization());
        target.setResponseDetails(source.getResponseDetails());

        return target;
    }

    public static ResponseDto toDto(Response source) {
        if (source == null) {
            return null;
        }
        ResponseDto target = new ResponseDto();

        DtoHelper.fillDto(target, source);
        target.setProtectiveDoseTt(source.getProtectiveDoseTt());
        target.setResponseDate(source.getResponseDate());
        target.setSupplementalImmunization(source.getSupplementalImmunization());
        target.setResponseDetails(source.getResponseDetails());
        return target;
    }



    @LocalBean
    @Stateless
    public static class ResponseFacadeEjbLocal extends ResponseFacadeEjb {
    }
}

