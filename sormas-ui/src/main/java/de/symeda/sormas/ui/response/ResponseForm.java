package de.symeda.sormas.ui.response;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.response.ResponseDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.ViewMode;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

public class ResponseForm extends AbstractEditForm<ResponseDto> {

    private static final long serialVersionUID = -1L;
    private static final String RESPONSE_HEADING_LOC = "responseHeadingLoc";

    private static final String HTML_LAYOUT =
            loc(RESPONSE_HEADING_LOC) +
                    fluidRowLocs(ResponseDto.PROTECTIVE_DOSE_TT, ResponseDto.RESPONSE_DATE) +
                    fluidRowLocs(ResponseDto.SUPPLEMENTAL_IMMUNIZATION, "") +
                    fluidRowLocs(ResponseDto.RESPONSE_DETAILS, "");

    public ResponseForm(ViewMode viewMode, boolean isPseudonymized, boolean inJurisdiction) {
        super(ResponseDto.class, ResponseDto.I18N_PREFIX, false, null, FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized));
        addFields();
    }

    @Override
    protected String createHtmlLayout() {
        return HTML_LAYOUT;
    }

    @Override
    protected void addFields() {
        Label responseHeadingLabel = new Label(I18nProperties.getString(Strings.headingResponse));
        responseHeadingLabel.addStyleName(H3);
        getContent().addComponent(responseHeadingLabel, RESPONSE_HEADING_LOC);

        addField(ResponseDto.PROTECTIVE_DOSE_TT, NullableOptionGroup.class);
        addField(ResponseDto.RESPONSE_DATE, DateField.class);
        addField(ResponseDto.SUPPLEMENTAL_IMMUNIZATION, NullableOptionGroup.class);
        TextArea responseDetails = addField(ResponseDto.RESPONSE_DETAILS, TextArea.class);
        responseDetails.setRows(6);
    }
}
