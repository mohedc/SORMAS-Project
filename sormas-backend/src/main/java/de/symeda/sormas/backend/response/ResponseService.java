package de.symeda.sormas.backend.response;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.BaseAdoService;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class ResponseService extends BaseAdoService<Response> {

    public ResponseService() {
        super(Response.class);
    }

    public Response createResponse() {

        Response response = new Response();
        response.setUuid(DataHelper.createUuid());
        return response;
    }
}

