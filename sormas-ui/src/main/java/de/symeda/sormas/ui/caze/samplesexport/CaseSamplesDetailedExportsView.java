/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.ui.caze.samplesexport;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.caze.CasesView;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class CaseSamplesDetailedExportsView extends AbstractView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = CasesView.VIEW_NAME + "/sampleExports";

	private final CaseSamplesDetailedExportsGrid grid;

	public CaseSamplesDetailedExportsView() {
		super(VIEW_NAME);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();

		grid = new CaseSamplesDetailedExportsGrid();

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		Button refresh = ButtonHelper.createIconButton(Captions.actionRefresh, VaadinIcons.REFRESH, e -> grid.reload(), ValoTheme.BUTTON_PRIMARY);
		Button back = ButtonHelper.createButton(Captions.caseSamplesDetailedExportBackToCases, e -> {
			getUI().getNavigator().navigateTo(CasesView.VIEW_NAME);
		});
		buttons.addComponents(refresh, back);
		layout.addComponent(buttons);
		CssStyles.style(grid, CssStyles.VSPACE_TOP_3);
		layout.addComponent(grid);
		layout.setExpandRatio(grid, 1);

		addComponent(layout);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		grid.reload();
	}
}
