package de.symeda.sormas.ui.response;

import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

public class ResponseView extends AbstractCaseView {

    private static final long serialVersionUID = -1L;

    public static final String VIEW_NAME = ROOT_VIEW_NAME + "/response";

    public ResponseView() {
        super(VIEW_NAME, true);
    }

    @Override
    protected void initView(String params) {
        CommitDiscardWrapperComponent<ResponseForm> caseResponseComponent =
                ControllerProvider.getCaseController().getResponseEditComponent(getCaseRef().getUuid(), getViewMode());
        setSubComponent(caseResponseComponent);
        setEditPermission(caseResponseComponent);
    }
}
