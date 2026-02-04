/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderCriteria;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class FormBuilderGrid extends FilteredGrid<FormBuilderDto, FormBuilderCriteria> {

	private static final long serialVersionUID = 1L;

	public FormBuilderGrid(FormBuilderCriteria criteria) {

		super(FormBuilderDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(FormBuildersView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		if (UiUtil.permitted(UserRight.INFRASTRUCTURE_EDIT)) {
			setColumns(
				FormBuilderDto.FORM_TYPE,
				FormBuilderDto.DISEASE);
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editFormBuilder(e.getUuid()));
			addActiveColumn();
		} else {
			// Show read-only active column if user doesn't have edit permission
			setColumns(
				FormBuilderDto.FORM_TYPE,
				FormBuilderDto.DISEASE,
				FormBuilderDto.ACTIVE);
		}

		if (UiUtil.permitted(UserRight.INFRASTRUCTURE_CREATE)) {
			addDuplicateColumn();
		}

		if (UiUtil.permitted(UserRight.INFRASTRUCTURE_EDIT)) {
			addDeleteColumn();
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(FormBuilderDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		if (ViewModelProviders.of(FormBuildersView.class).get(ViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getFormBuilderFacade()::getIndexList, FacadeProvider.getFormBuilderFacade()::count);
	}

	public void setEagerDataProvider() {
		setEagerDataProvider(FacadeProvider.getFormBuilderFacade()::getIndexList);
	}

	private void addDuplicateColumn() {
		addComponentColumn(this::createDuplicateButton).setId("duplicate").setSortable(false);
	}

	private Button createDuplicateButton(FormBuilderDto formBuilder) {
		Button duplicateButton = ButtonHelper.createIconButton(VaadinIcons.COPY);
		duplicateButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		duplicateButton.addClickListener(clickEvent -> {
			ControllerProvider.getInfrastructureController().duplicateFormBuilder(formBuilder.getUuid());
		});
		return duplicateButton;
	}

	private void addActiveColumn() {
		addComponentColumn(this::createActiveCheckBox).setId(FormBuilderDto.ACTIVE).setSortable(false);
	}

	private CheckBox createActiveCheckBox(FormBuilderDto formBuilder) {
		CheckBox activeCheckBox = new CheckBox();
		activeCheckBox.setValue(formBuilder.getActive() != null ? formBuilder.getActive() : false);
		activeCheckBox.addValueChangeListener(e -> {
			Boolean newValue = e.getValue();
			try {
				// Get the full form to update
				FormBuilderDto form = FacadeProvider.getFormBuilderFacade().getByUuid(formBuilder.getUuid());
				if (form != null) {
					form.setActive(newValue);
					FacadeProvider.getFormBuilderFacade().save(form);
					Notification.show(
						I18nProperties.getString(Strings.messageEntryUpdated),
						Type.ASSISTIVE_NOTIFICATION);
					reload();
				}
			} catch (Exception ex) {
				// Revert checkbox on error
				activeCheckBox.setValue(!newValue);
				Notification.show(
					I18nProperties.getString(Strings.errorUpdatingForm),
					Type.ERROR_MESSAGE);
			}
		});
		return activeCheckBox;
	}

	private void addDeleteColumn() {
		addComponentColumn(this::createDeleteButton).setId("delete").setSortable(false).setCaption("");
	}

	private Button createDeleteButton(FormBuilderDto formBuilder) {
		Button deleteButton = ButtonHelper.createIconButton(VaadinIcons.TRASH);
		deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		deleteButton.addClickListener(clickEvent -> {
			ControllerProvider.getInfrastructureController().deleteFormBuilder(formBuilder.getUuid(), this::reload);
		});
		return deleteButton;
	}
}

