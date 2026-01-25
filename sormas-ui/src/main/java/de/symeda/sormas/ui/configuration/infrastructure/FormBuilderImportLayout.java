package de.symeda.sormas.ui.configuration.infrastructure;

import java.io.IOException;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportReceiver;

@SuppressWarnings("serial")
public class FormBuilderImportLayout extends AbstractImportLayout {

	public FormBuilderImportLayout() {

		super();

		String templateFilePath;
		String templateFileName;
		String fileNameAddition = "_form_builder_import_";
		ImportFacade importFacade = FacadeProvider.getImportFacade();

		templateFilePath = importFacade.getFormBuilderImportTemplateFilePath();
		templateFileName = importFacade.getFormBuilderImportTemplateFileName();

		// Add download template component
		addDownloadImportTemplateComponent(1, templateFilePath, templateFileName);

		addImportCsvComponentWithOverwrite(2, allowOverwrite -> new ImportReceiver(fileNameAddition, file -> {
			resetDownloadErrorReportButton();

			try {
				DataImporter importer = new de.symeda.sormas.ui.importer.FormBuilderImporter(
					file,
					currentUser,
					allowOverwrite,
					(ValueSeparator) separator.getValue());

				importer.startImport(this::extendDownloadErrorReportButton, currentUI, true);
			} catch (IOException | CsvValidationException e) {
				new Notification(
					I18nProperties.getString(Strings.headingImportFailed),
					I18nProperties.getString(Strings.messageImportFailed),
					Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}
		}));

		addDownloadErrorReportComponent(3);
	}
}
