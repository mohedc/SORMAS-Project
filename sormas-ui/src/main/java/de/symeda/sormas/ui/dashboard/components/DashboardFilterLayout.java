/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.dashboard.components;

import static de.symeda.sormas.ui.utils.AbstractFilterForm.FILTER_ITEM_STYLE;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.dashboard.AbstractDashboardDataProvider;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;

@SuppressWarnings("serial")
public abstract class DashboardFilterLayout<P extends AbstractDashboardDataProvider> extends HorizontalLayout {

	public static final String DATE_FILTER = "dateFilter";
	public static final String REGION_FILTER = "regionFilter";
	public static final String DISTRICT_FILTER = "districtFilter";
	private static final String RESET_AND_APPLY_BUTTONS = "resetAndApplyButtons";

	protected AbstractDashboardView dashboardView;
	protected P dashboardDataProvider;
	private CustomLayout customLayout;

	// Filters
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private PopupButton btnCurrentPeriod;
	private PopupButton btnComparisonPeriod;
	private Set<Button> dateFilterButtons;
	private Set<Button> dateComparisonButtons;

	// Current period buttons
	private Button btnShowCustomPeriod;
	private Button btnToday;
	private Button btnYesterday;
	private Button btnThisWeek;
	private Button btnLastWeek;
	private Button btnThisYear;

	// Comparison period buttons
	private Button btnComparisonShowCustomPeriod;
	private Button btnComparisonToday;
	private Button btnComparisonYesterday;
	private Button btnComparisonThisWeek;
	private Button btnComparisonLastWeek;
	private Button btnComparisonThisYear;

	private Button resetButton;
	private Button applyButton;

	private DateFilterType currentDateFilterType;
	private DateFilterType comparisonDateFilterType;

	private HorizontalLayout customDateFilterLayout;
	private HorizontalLayout customComparisonDateFilterLayout;

	private Runnable dateFilterChangeCallback;

	public DashboardFilterLayout(AbstractDashboardView dashboardView, P dashboardDataProvider, String[] templateContent) {
		this.dashboardView = dashboardView;
		this.dashboardDataProvider = dashboardDataProvider;
		this.regionFilter = ComboBoxHelper.createComboBoxV7();
		this.districtFilter = ComboBoxHelper.createComboBoxV7();
		dateFilterButtons = new HashSet<>();
		dateComparisonButtons = new HashSet<>();

		setSpacing(true);
		setSizeUndefined();
		setMargin(new MarginInfo(true, true, false, true));

		String[] templateLocs = new String[] {
			DATE_FILTER,
			RESET_AND_APPLY_BUTTONS };
		templateLocs = ArrayUtils.insert(1, templateLocs, templateContent);

		customLayout = new CustomLayout();
		customLayout.setTemplateContents(filterLocs(templateLocs));

		addComponent(customLayout);
		populateLayout();
	}

	public void populateLayout() {
		createDateFilters();
		createResetAndApplyButtons();
	};

	protected void createRegionAndDistrictFilter() {
		createRegionFilter(null);
		createDistrictFilter(null);
	}

	protected void createRegionFilter(String description) {
		if (UiUtil.getUser().getRegion() == null && UiUtil.disabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			regionFilter.setWidth(200, Unit.PIXELS);
			regionFilter.setInputPrompt(I18nProperties.getString(Strings.promptRegion));
			regionFilter.setDescription(description);
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
			regionFilter.addValueChangeListener(e -> {
				dashboardDataProvider.setRegion((RegionReferenceDto) regionFilter.getValue());
			});
			// save height
			// regionFilter.setCaption(I18nProperties.getString(Strings.entityRegion));
			addCustomComponent(regionFilter, REGION_FILTER);
			dashboardDataProvider.setRegion((RegionReferenceDto) regionFilter.getValue());
		}
	}

	protected void createDistrictFilter(String description) {
		UserDto user = UiUtil.getUser();
		if (user.getRegion() != null && user.getDistrict() == null && UiUtil.disabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			districtFilter.setWidth(200, Unit.PIXELS);
			districtFilter.setInputPrompt(I18nProperties.getString(Strings.promptDistrict));
			districtFilter.setDescription(description);
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(UiUtil.getUser().getRegion().getUuid()));
			districtFilter.addValueChangeListener(e -> dashboardDataProvider.setDistrict((DistrictReferenceDto) districtFilter.getValue()));
			// save height
			//districtFilter.setCaption(I18nProperties.getString(Strings.entityDistrict));
			addCustomComponent(districtFilter, DISTRICT_FILTER);
			dashboardDataProvider.setDistrict((DistrictReferenceDto) districtFilter.getValue());
		}
	}

	public void createResetAndApplyButtons() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		Button.ClickListener resetListener = e -> dashboardView.navigateTo(null);
		resetButton = ButtonHelper.createButton(Captions.actionResetFilters, resetListener, CssStyles.BUTTON_FILTER_LIGHT);
		buttonLayout.addComponent(resetButton);
		Button.ClickListener applyListener = e -> dashboardView.refreshDashboard();
		applyButton = ButtonHelper.createButton(Captions.actionApplyFilters, applyListener, CssStyles.BUTTON_FILTER_LIGHT);
		applyButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		applyButton.addClickListener(e -> {
			if (getDateFilterChangeCallback() != null) {
				getDateFilterChangeCallback().run();
			}
		});
		buttonLayout.addComponent(applyButton);
		addCustomComponent(buttonLayout, RESET_AND_APPLY_BUTTONS);
	}

	public void createDateFilters() {
		HorizontalLayout dateFilterLayout = new HorizontalLayout();
		dateFilterLayout.setSpacing(true);
		CssStyles.style(dateFilterLayout, CssStyles.VSPACE_3);
		addCustomComponent(dateFilterLayout, DATE_FILTER);

		createCustomDateFilterLayout();
		createCustomComparisonDateFilterLayout();

		btnCurrentPeriod = ButtonHelper.createIconPopupButton(
			"currentPeriod",
			null,
			new VerticalLayout(createDateFilterButtonsLayout(), customDateFilterLayout),
			CssStyles.BUTTON_FILTER,
			CssStyles.BUTTON_FILTER_LIGHT);

		Label lblComparedTo = new Label(I18nProperties.getCaption(Captions.dashboardComparedTo));
		CssStyles.style(lblComparedTo, CssStyles.VSPACE_TOP_4, CssStyles.LABEL_BOLD);

		btnComparisonPeriod = ButtonHelper.createIconPopupButton(
			"comparisonPeriod",
			null,
			new VerticalLayout(createComparisonDateFilterButtonsLayout(), customComparisonDateFilterLayout),
			ValoTheme.BUTTON_BORDERLESS,
			CssStyles.BUTTON_FILTER,
			CssStyles.BUTTON_FILTER_LIGHT);

		dateFilterLayout.addComponents(btnCurrentPeriod, lblComparedTo, btnComparisonPeriod);

		// Set initial date filters
		CssStyles.style(btnThisWeek, CssStyles.BUTTON_FILTER_DARK);
		CssStyles.removeStyles(btnThisWeek, CssStyles.BUTTON_FILTER_LIGHT);
		currentDateFilterType = DateFilterType.THIS_WEEK;
		setDateFilter(DateHelper.getStartOfWeek(new Date()), new Date());
		btnCurrentPeriod.setCaption(btnThisWeek.getCaption());

		CssStyles.style(btnComparisonLastWeek, CssStyles.BUTTON_FILTER_DARK);
		CssStyles.removeStyles(btnComparisonLastWeek, CssStyles.BUTTON_FILTER_LIGHT);
		comparisonDateFilterType = DateFilterType.LAST_WEEK;
		Date now = new Date();
		setComparisonDateFilter(
			DateHelper.getStartOfWeek(DateHelper.subtractWeeks(now, 1)),
			DateHelper.getEndOfWeek(DateHelper.subtractWeeks(now, 1)));
		btnComparisonPeriod.setCaption(btnComparisonLastWeek.getCaption());
	}

	private HorizontalLayout createDateFilterButtonsLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		addPresetDateButtons(
			layout,
			null,
			"",
			dateFilterButtons,
			customDateFilterLayout,
			(type, from, to, caption) -> {
				currentDateFilterType = type;
				setDateFilter(from, to);
				btnCurrentPeriod.setCaption(caption);
			});
		return layout;
	}

	private VerticalLayout createComparisonDateFilterButtonsLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(false);
		HorizontalLayout row1 = new HorizontalLayout();
		HorizontalLayout row2 = new HorizontalLayout();
		row1.setSpacing(true);
		row2.setSpacing(true);
		addPresetDateButtons(
			row1,
			row2,
			"comparison",
			dateComparisonButtons,
			customComparisonDateFilterLayout,
			(type, from, to, caption) -> {
				comparisonDateFilterType = type;
				setComparisonDateFilter(from, to);
				btnComparisonPeriod.setCaption(caption);
			});
		layout.addComponents(row1, row2);
		return layout;
	}

	private void addPresetDateButtons(
		HorizontalLayout primaryRow,
		HorizontalLayout secondaryRow,
		String idPrefix,
		Set<Button> buttonSet,
		HorizontalLayout customLayout,
		PeriodFilterHandler handler) {

		String prefix = idPrefix.isEmpty() ? "" : idPrefix;

		final Button[] showCustomPeriodRef = new Button[1];
		Button showCustomPeriod = ButtonHelper.createButton(
			prefix + Captions.dashboardCustom,
			I18nProperties.getCaption(Captions.dashboardCustom),
			e -> changeCustomDateFilterPanelStyle(e.getButton(), buttonSet, showCustomPeriodRef[0], customLayout),
			ValoTheme.BUTTON_BORDERLESS,
			CssStyles.BUTTON_FILTER,
			CssStyles.BUTTON_FILTER_LIGHT);
		showCustomPeriodRef[0] = showCustomPeriod;
		buttonSet.add(showCustomPeriod);
		if (idPrefix.isEmpty()) {
			btnShowCustomPeriod = showCustomPeriod;
		} else {
			btnComparisonShowCustomPeriod = showCustomPeriod;
		}

		Date now = new Date();

		Button today = createAndAddDateFilterButton(
			prefix + Captions.dashboardToday,
			String.format(I18nProperties.getCaption(Captions.dashboardToday), DateFormatHelper.formatDate(now)),
			buttonSet,
			showCustomPeriod,
			customLayout);
		today.addClickListener(e -> applyPresetDateFilter(DateFilterType.TODAY, today.getCaption(), handler));

		Button yesterday = createAndAddDateFilterButton(
			prefix + Captions.dashboardYesterday,
			String.format(
				I18nProperties.getCaption(Captions.dashboardYesterday),
				DateFormatHelper.formatDate(DateHelper.subtractDays(now, 1))),
			buttonSet,
			showCustomPeriod,
			customLayout);
		yesterday.addClickListener(e -> applyPresetDateFilter(DateFilterType.YESTERDAY, yesterday.getCaption(), handler));

		Button thisWeek = createAndAddDateFilterButton(
			prefix + Captions.dashboardThisWeek,
			String.format(
				I18nProperties.getCaption(Captions.dashboardThisWeek),
				DateHelper.getEpiWeek(now).toString(now, I18nProperties.getUserLanguage())),
			buttonSet,
			showCustomPeriod,
			customLayout);
		thisWeek.addClickListener(e -> applyPresetDateFilter(DateFilterType.THIS_WEEK, thisWeek.getCaption(), handler));

		Button lastWeek = createAndAddDateFilterButton(
			prefix + Captions.dashboardLastWeek,
			String.format(
				I18nProperties.getCaption(Captions.dashboardLastWeek),
				DateHelper.getPreviousEpiWeek(now).toString(I18nProperties.getUserLanguage())),
			buttonSet,
			showCustomPeriod,
			customLayout);
		lastWeek.addClickListener(e -> applyPresetDateFilter(DateFilterType.LAST_WEEK, lastWeek.getCaption(), handler));

		Button thisYear = createAndAddDateFilterButton(
			prefix + Captions.dashboardThisYear,
			String.format(
				I18nProperties.getCaption(Captions.dashboardThisYear),
				DateFormatHelper.buildPeriodString(DateHelper.getStartOfYear(now), now)),
			buttonSet,
			showCustomPeriod,
			customLayout);
		thisYear.addClickListener(e -> applyPresetDateFilter(DateFilterType.THIS_YEAR, thisYear.getCaption(), handler));

		if (idPrefix.isEmpty()) {
			btnToday = today;
			btnYesterday = yesterday;
			btnThisWeek = thisWeek;
			btnLastWeek = lastWeek;
			btnThisYear = thisYear;
		} else {
			btnComparisonToday = today;
			btnComparisonYesterday = yesterday;
			btnComparisonThisWeek = thisWeek;
			btnComparisonLastWeek = lastWeek;
			btnComparisonThisYear = thisYear;
		}

		if (secondaryRow == null) {
			primaryRow.addComponents(showCustomPeriod, today, yesterday, thisWeek, lastWeek, thisYear);
		} else {
			primaryRow.addComponents(showCustomPeriod, today, yesterday);
			secondaryRow.addComponents(thisWeek, lastWeek, thisYear);
		}
	}

	private void applyPresetDateFilter(DateFilterType type, String caption, PeriodFilterHandler handler) {
		Date[] range = getPresetDateRange(type);
		handler.onPresetSelected(type, range[0], range[1], caption);
	}

	private Date[] getPresetDateRange(DateFilterType type) {
		Date now = new Date();
		switch (type) {
		case TODAY:
			return new Date[] {
				DateHelper.getStartOfDay(now),
				now };
		case YESTERDAY:
			return new Date[] {
				DateHelper.getStartOfDay(DateHelper.subtractDays(now, 1)),
				DateHelper.getEndOfDay(DateHelper.subtractDays(now, 1)) };
		case THIS_WEEK:
			return new Date[] {
				DateHelper.getStartOfWeek(now),
				now };
		case LAST_WEEK:
			return new Date[] {
				DateHelper.getStartOfWeek(DateHelper.subtractWeeks(now, 1)),
				DateHelper.getEndOfWeek(DateHelper.subtractWeeks(now, 1)) };
		case THIS_YEAR:
			return new Date[] {
				DateHelper.getStartOfYear(now),
				now };
		default:
			throw new IllegalArgumentException("Unsupported preset date filter type: " + type);
		}
	}

	private void createCustomDateFilterLayout() {
		customDateFilterLayout = buildCustomDateFilterLayout(
			(dateFilterOption, fromDate, toDate, fromWeek, toWeek) -> {
				currentDateFilterType = DateFilterType.CUSTOM;
				if (dateFilterOption == DateFilterOption.DATE) {
					setDateFilter(DateHelper.getStartOfDay(fromDate), DateHelper.getEndOfDay(toDate));
					btnCurrentPeriod.setCaption(DateFormatHelper.buildPeriodString(fromDate, toDate));
				} else {
					setDateFilter(DateHelper.getEpiWeekStart(fromWeek), DateHelper.getEpiWeekEnd(toWeek));
					btnCurrentPeriod.setCaption(fromWeek.toShortString() + " - " + toWeek.toShortString());
				}
			});
	}

	private void createCustomComparisonDateFilterLayout() {
		customComparisonDateFilterLayout = buildCustomDateFilterLayout(
			(dateFilterOption, fromDate, toDate, fromWeek, toWeek) -> {
				comparisonDateFilterType = DateFilterType.CUSTOM;
				if (dateFilterOption == DateFilterOption.DATE) {
					setComparisonDateFilter(DateHelper.getStartOfDay(fromDate), DateHelper.getEndOfDay(toDate));
					btnComparisonPeriod.setCaption(DateFormatHelper.buildPeriodString(fromDate, toDate));
				} else {
					setComparisonDateFilter(DateHelper.getEpiWeekStart(fromWeek), DateHelper.getEpiWeekEnd(toWeek));
					btnComparisonPeriod.setCaption(fromWeek.toShortString() + " - " + toWeek.toShortString());
				}
			});
	}

	private HorizontalLayout buildCustomDateFilterLayout(CustomDateFilterHandler handler) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setVisible(false);

		Button customApplyButton =
			ButtonHelper.createButton(Captions.dashboardApplyCustomFilter, null, CssStyles.FORCE_CAPTION, CssStyles.BUTTON_FILTER_LIGHT);

		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter =
			new EpiWeekAndDateFilterComponent<>(true, true, I18nProperties.getString(Strings.infoCaseDate), null);
		layout.addComponents(weekAndDateFilter, customApplyButton);

		customApplyButton.addClickListener(e -> {
			DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
			Date fromDate = null;
			Date toDate = null;
			EpiWeek fromWeek = null;
			EpiWeek toWeek = null;
			if (dateFilterOption == DateFilterOption.DATE) {
				fromDate = weekAndDateFilter.getDateFromFilter().getValue();
				toDate = weekAndDateFilter.getDateToFilter().getValue();
			} else {
				fromWeek = (EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue();
				toWeek = (EpiWeek) weekAndDateFilter.getWeekToFilter().getValue();
			}

			if ((fromDate != null && toDate != null) || (fromWeek != null && toWeek != null)) {
				handler.onApply(dateFilterOption, fromDate, toDate, fromWeek, toWeek);
			} else {
				if (dateFilterOption == DateFilterOption.DATE) {
					new Notification(
						I18nProperties.getString(Strings.headingMissingDateFilter),
						I18nProperties.getString(Strings.messageMissingDateFilter),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				} else {
					new Notification(
						I18nProperties.getString(Strings.headingMissingEpiWeekFilter),
						I18nProperties.getString(Strings.messageMissingEpiWeekFilter),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				}
			}
		});

		return layout;
	}

	private Button createAndAddDateFilterButton(
		String id,
		String caption,
		Set<Button> buttonSet,
		Button showCustomBtn,
		HorizontalLayout customLayout) {
		Button button = ButtonHelper.createButton(id, caption, e -> {
			changeCustomDateFilterPanelStyle(e.getButton(), buttonSet, showCustomBtn, customLayout);
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);

		buttonSet.add(button);

		return button;
	}

	private void changeCustomDateFilterPanelStyle(
		Button activeFilterButton,
		Set<Button> buttonSet,
		Button showCustomBtn,
		HorizontalLayout customLayout) {
		if (activeFilterButton != null) {
			CssStyles.style(activeFilterButton, CssStyles.BUTTON_FILTER_DARK);
			CssStyles.removeStyles(activeFilterButton, CssStyles.BUTTON_FILTER_LIGHT);
		}

		buttonSet.forEach(b -> {
			if (activeFilterButton == null || b != activeFilterButton) {
				CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
				CssStyles.removeStyles(b, CssStyles.BUTTON_FILTER_DARK);
			}
		});

		if (customLayout != null) {
			customLayout.setVisible(activeFilterButton == showCustomBtn);
		}
	}

	private void setDateFilter(Date from, Date to) {
		dashboardDataProvider.setFromDate(DateHelper.getStartOfDay(from));
		dashboardDataProvider.setToDate(DateHelper.getEndOfDay(to));
		if (dateFilterChangeCallback != null) {
			dateFilterChangeCallback.run();
		}
	}

	private void setComparisonDateFilter(Date from, Date to) {
		dashboardDataProvider.setPreviousFromDate(DateHelper.getStartOfDay(from));
		dashboardDataProvider.setPreviousToDate(DateHelper.getEndOfDay(to));
		if (dateFilterChangeCallback != null) {
			dateFilterChangeCallback.run();
		}
	}

	private enum DateFilterType {
		TODAY,
		YESTERDAY,
		THIS_WEEK,
		LAST_WEEK,
		THIS_YEAR,
		CUSTOM;
	}

	@FunctionalInterface
	private interface PeriodFilterHandler {

		void onPresetSelected(DateFilterType type, Date from, Date to, String caption);
	}

	@FunctionalInterface
	private interface CustomDateFilterHandler {

		void onApply(DateFilterOption dateFilterOption, Date fromDate, Date toDate, EpiWeek fromWeek, EpiWeek toWeek);
	}

	public Runnable getDateFilterChangeCallback() {
		return dateFilterChangeCallback;
	}

	public void setDateFilterChangeCallback(Runnable dateFilterChangeCallback) {
		this.dateFilterChangeCallback = dateFilterChangeCallback;
	}

	protected void addCustomComponent(Component component, String locator) {
		customLayout.addComponent(component, locator);
		component.addStyleName(FILTER_ITEM_STYLE);
	}
}
