/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.ui.caze.samplesexport;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportDownloadDto;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportDto;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportResult;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CaseSamplesDetailedExportsGrid extends Grid<CaseSamplesDetailedExportDto> {

	private static final long serialVersionUID = 1L;

	public CaseSamplesDetailedExportsGrid() {
		setSizeFull();
		setHeightByRows(12);

		addColumn(dto -> dto.getRequestedBy() == null ? "" : dto.getRequestedBy().getCaption())
			.setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_requestedBy))
			.setId(CaseSamplesDetailedExportDto.REQUESTED_BY);
		addColumn(dto -> DateFormatHelper.formatLocalDateTime(dto.getRequestedAt()))
			.setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_requestedAt))
			.setId(CaseSamplesDetailedExportDto.REQUESTED_AT);
		addColumn(dto -> dto.getResult() == null ? "" : dto.getResult().toString())
			.setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_result))
			.setId(CaseSamplesDetailedExportDto.RESULT);
		addColumn(CaseSamplesDetailedExportDto::getFilterSummary)
			.setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_filterSummary))
			.setId(CaseSamplesDetailedExportDto.FILTER_SUMMARY)
			.setExpandRatio(1);
		addColumn(CaseSamplesDetailedExportDto::getRowCount)
			.setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_rowCount))
			.setId(CaseSamplesDetailedExportDto.ROW_COUNT);
		addColumn(dto -> DateFormatHelper.formatLocalDateTime(dto.getExpiresAt()))
			.setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_expiresAt))
			.setId(CaseSamplesDetailedExportDto.EXPIRES_AT);
		addColumn(CaseSamplesDetailedExportDto::getDownloadCount)
			.setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_downloadCount))
			.setId(CaseSamplesDetailedExportDto.DOWNLOAD_COUNT);
		addComponentColumn(this::buildErrorButton)
			.setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_errorLog))
			.setId("errorLog");
		addColumn(this::progressCaption)
			.setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_progress))
			.setId("progress");
		addComponentColumn(this::buildDownloadsButton)
			.setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_viewDownloads))
			.setId("downloadsAction");
	}

	public void reload() {
		setDataProvider(new ListDataProvider<>(FacadeProvider.getCaseSamplesDetailedExportFacade().getIndexList()));
	}

	private String progressCaption(CaseSamplesDetailedExportDto dto) {
		if (dto.getResult() != CaseSamplesDetailedExportResult.IN_PROGRESS) {
			return "";
		}
		if (dto.getProgressRowCount() == null) {
			return "—";
		}
		if (dto.getProgressPercent() != null) {
			return dto.getProgressRowCount() + " rows (" + dto.getProgressPercent() + "%)";
		}
		return dto.getProgressRowCount() + " rows";
	}

	private Button buildErrorButton(CaseSamplesDetailedExportDto dto) {
		if (StringUtils.isBlank(dto.getErrorMessage())) {
			return null;
		}
		return ButtonHelper.createButton(Captions.CaseSamplesDetailedExport_viewError, e -> {
			VerticalLayout layout = new VerticalLayout();
			layout.setWidth(100, Unit.PERCENTAGE);
			layout.addComponent(new Label(dto.getErrorMessage(), ContentMode.PREFORMATTED));
			if (StringUtils.isNotBlank(dto.getErrorStack())) {
				TextArea stack = new TextArea();
				stack.setValue(dto.getErrorStack());
				stack.setReadOnly(true);
				stack.setWidth(100, Unit.PERCENTAGE);
				stack.setRows(18);
				layout.addComponent(stack);
			}
			VaadinUiUtil.showPopupWindowWithWidth(layout, I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_viewError), 70);
		}, ValoTheme.BUTTON_LINK);
	}

	private Button buildDownloadsButton(CaseSamplesDetailedExportDto dto) {
		return ButtonHelper.createButton(Captions.CaseSamplesDetailedExport_viewDownloads, e -> showDownloads(dto), ValoTheme.BUTTON_LINK);
	}

	private void showDownloads(CaseSamplesDetailedExportDto dto) {
		List<CaseSamplesDetailedExportDownloadDto> downloads = FacadeProvider.getCaseSamplesDetailedExportFacade().getDownloads(dto.getUuid());
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.addComponent(new Label(String.format(I18nProperties.getString(Strings.infoExportDownloadedTimes), dto.getDownloadCount())));

		Grid<CaseSamplesDetailedExportDownloadDto> grid = new Grid<>();
		grid.setWidth(100, Unit.PERCENTAGE);
		grid.setItems(downloads);
		grid.addColumn(d -> DateFormatHelper.formatLocalDateTime(d.getDownloadedAt()))
			.setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_downloadedAt));
		grid.addColumn(this::whoCaption).setCaption(I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_downloadedBy));
		layout.addComponent(grid);

		if (dto.isHasFile()
			&& (dto.getResult() == CaseSamplesDetailedExportResult.SUCCESS || dto.getResult() == CaseSamplesDetailedExportResult.FAILED)) {
			String caption = dto.getResult() == CaseSamplesDetailedExportResult.FAILED
				? Captions.CaseSamplesDetailedExport_downloadPartial
				: Captions.CaseSamplesDetailedExport_downloadFile;
			Button download = ButtonHelper.createIconButton(caption, VaadinIcons.DOWNLOAD, click -> {
				String downloadUrl = com.vaadin.server.VaadinServlet.getCurrent().getServletContext().getContextPath() + "/export-downloads/auth/"
					+ dto.getUuid();
				Page.getCurrent().open(downloadUrl, "_blank");
			}, ValoTheme.BUTTON_PRIMARY);
			layout.addComponent(download);
		}

		VaadinUiUtil.showPopupWindowWithWidth(layout, I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_viewDownloads), 60);
	}

	private String whoCaption(CaseSamplesDetailedExportDownloadDto download) {
		UserReferenceDto user = download.getDownloadingUser();
		if (user == null) {
			return I18nProperties.getCaption(Captions.CaseSamplesDetailedExport_notLoggedIn);
		}
		return StringUtils.defaultIfBlank(user.getCaption(), DataHelper.getShortUuid(user.getUuid()));
	}
}
