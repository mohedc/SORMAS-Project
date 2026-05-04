package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class FollowUpExaminationView extends AbstractCaseView {

    private static final long serialVersionUID = -1L;

    public static final String VIEW_NAME = ROOT_VIEW_NAME + "/followUpExamination";

    public FollowUpExaminationView() {
        super(VIEW_NAME, true);
    }

    @Override
    protected void initView(String params) {

        String caseUuid = getCaseRef().getUuid();
        CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);

        if (caze.getDisease() != Disease.AFP) {
            setMessageComponent("Follow-up examination is only available for AFP cases.");
            return;
        }

        String pathogenTestUuid = findFirstPathogenTestUuid(caseUuid);
        if (pathogenTestUuid == null) {
            setMessageComponent("No pathogen test found for this case.");
            return;
        }

        CommitDiscardWrapperComponent<FollowUpExaminationForm> followUpExaminationComponent =
                ControllerProvider.getPathogenTestController()
                        .getFollowUpExaminationEditComponent(pathogenTestUuid, getViewMode());

        setSubComponent(followUpExaminationComponent);
        setEditPermission(followUpExaminationComponent);
    }

    private String findFirstPathogenTestUuid(String caseUuid) {

        List<SampleDto> samples = FacadeProvider.getSampleFacade().getByCaseUuids(List.of(caseUuid));

        for (SampleDto sample : samples) {
            if (sample.isDeleted()) {
                continue;
            }

            List<PathogenTestDto> pathogenTests =
                    FacadeProvider.getPathogenTestFacade().getAllBySample(sample.toReference());

            for (PathogenTestDto pathogenTest : pathogenTests) {
                if (!pathogenTest.isDeleted()) {
                    return pathogenTest.getUuid();
                }
            }
        }

        return null;
    }

    private void setMessageComponent(String message) {
        DetailSubComponentWrapper wrapper = new DetailSubComponentWrapper(() -> null);
        wrapper.addComponent(new Label(message));
        setSubComponent(wrapper);
    }
}