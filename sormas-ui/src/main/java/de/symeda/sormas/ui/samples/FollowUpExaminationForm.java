package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.ui.utils.CssStyles.*;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.utils.InjectionSite;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.FeatureTypeFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.UserRightFieldVisibilityChecker;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.*;

import java.util.Arrays;
import java.util.List;

public class FollowUpExaminationForm extends AbstractEditForm<PathogenTestDto> {

    private static final long serialVersionUID = 1L;

    protected static final String FOLLOW_UP_EXAMINATION_HEADLINE_LOC = "followUpExaminationLoc";

    private static final String HTML_LAYOUT =
            loc(FOLLOW_UP_EXAMINATION_HEADLINE_LOC)
                    + fluidRowLocs(PathogenTestDto.DATE_FOLLOWUP_EXAM, "")
                    + fluidRowLocs(PathogenTestDto.RESIDUAL_ANALYSIS, PathogenTestDto.RESULT_EXAM);

    private final String pathogenTestUuid;
    private final Disease disease;

    public FollowUpExaminationForm(
            String pathogenTestUuid,
            Disease disease,
            ViewMode viewMode,
            boolean isPseudonymized,
            boolean inJurisdiction) {

        super(
                PathogenTestDto.class,
                PathogenTestDto.I18N_PREFIX,
                false,
                FieldVisibilityCheckers.withDisease(disease)
                        .add(new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale()))
                        .add(new UserRightFieldVisibilityChecker(UiUtil::permitted))
                        .add(new FeatureTypeFieldVisibilityChecker(FacadeProvider.getFeatureConfigurationFacade().getActiveServerFeatureConfigurations())),
                FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized));

        this.pathogenTestUuid = pathogenTestUuid;
        this.disease = disease;

        addFields();
    }

    @Override
    protected void addFields() {

        Label followUpExamination = new Label(I18nProperties.getString(Strings.headingFollowUpExamination));
        CssStyles.style(followUpExamination, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, VSPACE_4);
        getContent().addComponent(followUpExamination, FOLLOW_UP_EXAMINATION_HEADLINE_LOC);

        addField(PathogenTestDto.DATE_FOLLOWUP_EXAM, DateField.class);

        NullableOptionGroup residualAnalysis = addField(PathogenTestDto.RESIDUAL_ANALYSIS, NullableOptionGroup.class);
        List<InjectionSite> paralysisSite = Arrays.asList(InjectionSite.LEFT_ARM, InjectionSite.LEFT_LEG, InjectionSite.RIGHT_ARM, InjectionSite.RIGHT_LEG);
        FieldHelper.updateEnumData(residualAnalysis, paralysisSite);

        ComboBox resultExamField = addField(PathogenTestDto.RESULT_EXAM, ComboBox.class);
        resultExamField.setNullSelectionAllowed(true);
        resultExamField.setItemCaptionMode(ComboBox.ItemCaptionMode.ID_TOSTRING);
    }

    @Override
    protected String createHtmlLayout() {
        return HTML_LAYOUT;
    }

    public String getPathogenTestUuid() {
        return pathogenTestUuid;
    }

    public Disease getDisease() {
        return disease;
    }
}