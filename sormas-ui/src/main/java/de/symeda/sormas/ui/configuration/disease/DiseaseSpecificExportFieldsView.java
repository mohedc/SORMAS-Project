/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.ui.configuration.disease;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificExportFieldConfiguration;
import de.symeda.sormas.api.caze.samplesexport.DiseaseSpecificExportFieldDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DiseaseSpecificExportFieldsView extends AbstractConfigurationView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/diseaseSpecificExportFields";

	private final ComboBox<Disease> diseaseFilter;
	private final VerticalLayout fieldsLayout;
	private final Map<String, CheckBox> checkBoxes = new LinkedHashMap<>();
	private List<DiseaseSpecificExportFieldDto> currentFields = java.util.Collections.emptyList();

	public DiseaseSpecificExportFieldsView() {
		super(VIEW_NAME);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();

		layout.addComponent(new Label(I18nProperties.getString(Strings.infoDiseaseSpecificExportFields), ContentMode.HTML));

		diseaseFilter = new ComboBox<>();
		diseaseFilter.setCaption(I18nProperties.getCaption(Captions.disease));
		diseaseFilter.setItems(DiseaseSpecificExportFieldConfiguration.SUPPORTED_DISEASES);
		diseaseFilter.setEmptySelectionAllowed(false);
		diseaseFilter.addValueChangeListener(e -> reloadFields());
		layout.addComponent(diseaseFilter);

		HorizontalLayout actions = new HorizontalLayout();
		actions.setSpacing(true);
		actions.addComponent(ButtonHelper.createButton(Captions.actionSelectAll, e -> setAll(true), ValoTheme.BUTTON_PRIMARY));
		actions.addComponent(ButtonHelper.createButton(Captions.actionDeselectAll, e -> setAll(false)));
		actions.addComponent(ButtonHelper.createButton(Captions.actionSave, e -> save(), ValoTheme.BUTTON_PRIMARY));
		actions.addComponent(ButtonHelper.createButton(Captions.actionDiscard, e -> reloadFields()));

		Button exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_PRIMARY);
		new FileDownloader(new StreamResource(() -> {
			Disease disease = diseaseFilter.getValue();
			byte[] csv = disease == null ? new byte[0] : FacadeProvider.getDiseaseSpecificExportFieldFacade().exportCsv(disease);
			return new java.io.ByteArrayInputStream(csv);
		}, "disease-specific-export-fields.csv")).extend(exportButton);
		actions.addComponent(exportButton);

		CsvReceiver receiver = new CsvReceiver();
		Upload upload = new Upload(I18nProperties.getCaption(Captions.actionImport), receiver);
		upload.addSucceededListener(receiver);
		actions.addComponent(upload);
		layout.addComponent(actions);

		fieldsLayout = new VerticalLayout();
		fieldsLayout.setMargin(false);
		fieldsLayout.setSpacing(true);
		Panel panel = new Panel(fieldsLayout);
		panel.setSizeFull();
		layout.addComponent(panel);
		layout.setExpandRatio(panel, 1);

		addComponent(layout);

		diseaseFilter.setValue(DiseaseSpecificExportFieldConfiguration.SUPPORTED_DISEASES.iterator().next());
	}

	private void reloadFields() {
		fieldsLayout.removeAllComponents();
		checkBoxes.clear();
		Disease disease = diseaseFilter.getValue();
		if (disease == null) {
			return;
		}
		currentFields = FacadeProvider.getDiseaseSpecificExportFieldFacade().getByDisease(disease);
		Map<String, List<DiseaseSpecificExportFieldDto>> byBlock =
			currentFields.stream().collect(Collectors.groupingBy(f -> f.getSchemaBlock() == null ? "case" : f.getSchemaBlock(), LinkedHashMap::new, Collectors.toList()));
		for (Map.Entry<String, List<DiseaseSpecificExportFieldDto>> entry : byBlock.entrySet()) {
			Label heading = new Label(entry.getKey());
			CssStyles.style(heading, CssStyles.H3);
			fieldsLayout.addComponent(heading);
			for (DiseaseSpecificExportFieldDto field : entry.getValue()) {
				CheckBox box = new CheckBox(field.getCaption() + " (" + field.getPropertyPath() + ")");
				box.setValue(field.isActive());
				checkBoxes.put(field.getPropertyPath(), box);
				fieldsLayout.addComponent(box);
			}
		}
	}

	private void setAll(boolean active) {
		checkBoxes.values().forEach(box -> box.setValue(active));
	}

	private void save() {
		Disease disease = diseaseFilter.getValue();
		if (disease == null) {
			return;
		}
		Set<String> active = new HashSet<>();
		checkBoxes.forEach((path, box) -> {
			if (Boolean.TRUE.equals(box.getValue())) {
				active.add(path);
			}
		});
		FacadeProvider.getDiseaseSpecificExportFieldFacade().saveActivePaths(disease, active);
		Notification.show(I18nProperties.getString(Strings.messageExportConfigurationSaved), Notification.Type.TRAY_NOTIFICATION);
		reloadFields();
	}

	private class CsvReceiver implements Upload.Receiver, Upload.SucceededListener {

		private static final long serialVersionUID = 1L;
		private final ByteArrayOutputStream output = new ByteArrayOutputStream();

		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			output.reset();
			return output;
		}

		@Override
		public void uploadSucceeded(Upload.SucceededEvent event) {
			try {
				FacadeProvider.getDiseaseSpecificExportFieldFacade().importCsv(output.toByteArray());
				Notification.show(I18nProperties.getString(Strings.messageImportSuccessful), Notification.Type.TRAY_NOTIFICATION);
				reloadFields();
			} catch (ValidationRuntimeException ex) {
				VaadinUiUtil.showWarningPopup(ex.getMessage());
			}
		}
	}
}
