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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.fields.FormFieldIndexDto;
import de.symeda.sormas.api.infrastructure.fields.FormFieldReferenceDto;
import de.symeda.sormas.api.infrastructure.fields.FormFieldsCriteria;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class FormBuilderEditForm extends AbstractEditForm<FormBuilderDto> {

	private static final long serialVersionUID = 1L;

	private static final String FIELDS_SELECTION_LOCATION = "searchFieldsLocation";
	private static final String HTML_LAYOUT = fluidRowLocs(FormBuilderDto.DISEASE, FormBuilderDto.FORM_TYPE)
		+ loc(FIELDS_SELECTION_LOCATION);

	private static final String PROPERTY_FIELD = "field";
	private static final String PROPERTY_ORDER = "order";
	private static final String PROPERTY_NAME = "name";

	private boolean create;
	private ComboBox disease;
	private ComboBox formType;
	private ListSelect availableFields;
	private Table selectedFieldsTable;
	private IndexedContainer selectedFieldsContainer;
	private FormFieldsCriteria criteria;
	private int currentOrder = 0;
	private boolean firstPageLoad = true;
	private Set<String> lastSelectedIds;
	
	// Search and counter components
	private TextField availableFieldsSearch;
	private TextField selectedFieldsSearch;
	private Label availableFieldsCountLabel;
	private Label selectedFieldsCountLabel;
	private IndexedContainer availableFieldsContainer; // Store all available fields for filtering

	public FormBuilderEditForm(boolean create) {
		super(FormBuilderDto.class, FormBuilderDto.I18N_PREFIX, false);
		this.create = create;

		setWidth(900, Unit.PIXELS);
		setHeight(700, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}

		criteria = new FormFieldsCriteria();
		// Set default to show only active fields
		criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		addFields();
	}

	@Override
	protected void addFields() {
		disease = addField(FormBuilderDto.DISEASE, ComboBox.class);
		disease.addItems(Disease.values());
		disease.setNullSelectionAllowed(false);

		formType = addField(FormBuilderDto.FORM_TYPE, ComboBox.class);
		formType.addItems(FormType.values());
		formType.setNullSelectionAllowed(false);

		// Available Fields Search and Counter
		VerticalLayout availableFieldsLayout = new VerticalLayout();
		availableFieldsLayout.setSpacing(true);
		availableFieldsLayout.setMargin(false);
		
		availableFieldsSearch = new TextField();
		availableFieldsSearch.setWidth("100%");
		availableFieldsSearch.setInputPrompt("Search available fields...");
		availableFieldsSearch.setNullRepresentation("");
		availableFieldsSearch.setImmediate(true);
		availableFieldsSearch.addTextChangeListener(e -> filterAvailableFields(e.getText()));
		
		availableFieldsCountLabel = new Label();
		availableFieldsCountLabel.setValue("Available: 0");
		availableFieldsCountLabel.addStyleName("v-label-small");
		
		// Available Fields (ListSelect)
		availableFields = new ListSelect();
		availableFields.setWidth("100%");
		availableFields.setRows(20);
		availableFields.setNullSelectionAllowed(false);
		availableFields.setMultiSelect(true);
		availableFields.setCaption(I18nProperties.getCaption(Captions.formFieldsAvailable));
		
		availableFieldsLayout.addComponent(availableFieldsCountLabel);
		availableFieldsLayout.addComponent(availableFieldsSearch);
		availableFieldsLayout.addComponent(availableFields);
		availableFieldsLayout.setExpandRatio(availableFields, 1);

		// Selected Fields Search and Counter
		VerticalLayout selectedFieldsLayout = new VerticalLayout();
		selectedFieldsLayout.setSpacing(true);
		selectedFieldsLayout.setMargin(false);
		
		selectedFieldsSearch = new TextField();
		selectedFieldsSearch.setWidth("100%");
		selectedFieldsSearch.setInputPrompt("Search selected fields...");
		selectedFieldsSearch.setNullRepresentation("");
		selectedFieldsSearch.setImmediate(true);
		selectedFieldsSearch.addTextChangeListener(e -> filterSelectedFields(e.getText()));
		
		selectedFieldsCountLabel = new Label();
		selectedFieldsCountLabel.setValue("Selected: 0");
		selectedFieldsCountLabel.addStyleName("v-label-small");

		// Selected Fields (Table)
		selectedFieldsTable = new Table();
		selectedFieldsTable.setWidth("100%");
		selectedFieldsTable.setHeight("400px");
		selectedFieldsTable.setCaption(I18nProperties.getCaption(Captions.formFieldsSelected));
		selectedFieldsTable.setSelectable(true);
		selectedFieldsTable.setMultiSelect(true);

		selectedFieldsContainer = new IndexedContainer();
		selectedFieldsContainer.addContainerProperty(PROPERTY_FIELD, FormFieldIndexDto.class, null);
		selectedFieldsContainer.addContainerProperty(PROPERTY_ORDER, Integer.class, null);
		selectedFieldsContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
		selectedFieldsTable.setContainerDataSource(selectedFieldsContainer);

		selectedFieldsTable.setColumnHeader(PROPERTY_ORDER, "Order");
		selectedFieldsTable.setColumnHeader(PROPERTY_NAME, "Description");
		selectedFieldsTable.setVisibleColumns(PROPERTY_ORDER, PROPERTY_NAME);
		selectedFieldsTable.setColumnAlignment(PROPERTY_ORDER, Table.Align.CENTER);
		
		selectedFieldsLayout.addComponent(selectedFieldsCountLabel);
		selectedFieldsLayout.addComponent(selectedFieldsSearch);
		selectedFieldsLayout.addComponent(selectedFieldsTable);
		selectedFieldsLayout.setExpandRatio(selectedFieldsTable, 1);

		// Action Buttons
		Button addButton = new Button("→");
		Button removeButton = new Button("←");
		Button moveUpButton = new Button(" ↑ ");
		Button moveDownButton = new Button(" ↓ ");
		Button moveToTopButton = new Button("↑↑");

		VerticalLayout buttonLayout = new VerticalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.addComponents(addButton, removeButton, moveUpButton, moveDownButton, moveToTopButton);

		// Add button action
		addButton.addClickListener(event -> {
			@SuppressWarnings("unchecked")
			Set<FormFieldIndexDto> selected = (Set<FormFieldIndexDto>) availableFields.getValue();
			if (selected != null) {
				for (FormFieldIndexDto field : selected) {
					// Check for duplicates by UUID
					if (selectedFieldsContainer.getItem(field.getUuid()) == null) {
						Item item = selectedFieldsContainer.addItem(field.getUuid());
						if (item != null) {
							int order = selectedFieldsContainer.size();
							item.getItemProperty(PROPERTY_FIELD).setValue(field);
							item.getItemProperty(PROPERTY_ORDER).setValue(order);
							// Show description instead of fieldName
							String description = field.getDescription() != null && !field.getDescription().isEmpty() 
								? field.getDescription() 
								: field.getFieldName();
							item.getItemProperty(PROPERTY_NAME).setValue(description);
						}
						availableFields.removeItem(field);
					}
				}
				availableFields.setValue(null);
				reorderItems();
				updateFormFieldsList();
				updateCounters();
			}
		});

		// Remove button action
		removeButton.addClickListener(event -> {
			@SuppressWarnings("unchecked")
			Set<String> selectedIds = (Set<String>) selectedFieldsTable.getValue();
			if (selectedIds != null) {
				for (String id : selectedIds) {
					Item item = selectedFieldsContainer.getItem(id);
					if (item != null) {
						FormFieldIndexDto field = (FormFieldIndexDto) item.getItemProperty(PROPERTY_FIELD).getValue();
						if (field != null) {
							selectedFieldsContainer.removeItem(id);
							// Add back to availableFieldsContainer if it doesn't exist
							if (availableFieldsContainer != null && availableFieldsContainer.getItem(field) == null) {
								Item containerItem = availableFieldsContainer.addItem(field);
								if (containerItem != null) {
									containerItem.getItemProperty("field").setValue(field);
								}
							}
							availableFields.addItem(field);
							// Show description instead of fieldName
							String caption = field.getDescription() != null && !field.getDescription().isEmpty() 
								? field.getDescription() 
								: field.getFieldName();
							availableFields.setItemCaption(field, caption);
						}
					}
				}
				reorderItems();
				updateFormFieldsList();
				updateCounters();
				// Re-apply search filter after adding items back
				filterAvailableFields(availableFieldsSearch.getValue());
			}
		});

		// Move up button action
		moveUpButton.addClickListener(event -> {
			@SuppressWarnings("unchecked")
			Set<String> selectedIds = (Set<String>) selectedFieldsTable.getValue();
			if (selectedIds != null && !selectedIds.isEmpty()) {
				lastSelectedIds = new HashSet<>(selectedIds);
				for (String id : selectedIds) {
					moveItem(id, true);
				}
				updateFormFieldsList();
				selectedFieldsTable.setValue(lastSelectedIds);
				// Re-apply selected fields filter
				if (selectedFieldsSearch != null) {
					filterSelectedFields(selectedFieldsSearch.getValue());
				}
			}
		});

		// Move down button action
		moveDownButton.addClickListener(event -> {
			@SuppressWarnings("unchecked")
			Set<String> selectedIds = (Set<String>) selectedFieldsTable.getValue();
			if (selectedIds != null && !selectedIds.isEmpty()) {
				lastSelectedIds = new HashSet<>(selectedIds);
				Object[] selectedIdsArray = selectedIds.toArray();
				// Reverse order for move down to maintain visual order
				for (int i = selectedIdsArray.length - 1; i >= 0; i--) {
					moveItem((String) selectedIdsArray[i], false);
				}
				updateFormFieldsList();
				selectedFieldsTable.setValue(lastSelectedIds);
				// Re-apply selected fields filter
				if (selectedFieldsSearch != null) {
					filterSelectedFields(selectedFieldsSearch.getValue());
				}
			}
		});

		// Move to top button action
		moveToTopButton.addClickListener(event -> {
			@SuppressWarnings("unchecked")
			Set<String> selectedIds = (Set<String>) selectedFieldsTable.getValue();
			if (selectedIds != null && !selectedIds.isEmpty()) {
				lastSelectedIds = new HashSet<>(selectedIds);
				List<String> selectedIdsList = new ArrayList<>(selectedIds);
				Collections.reverse(selectedIdsList);
				for (String id : selectedIdsList) {
					moveItemToTop(id);
				}
				updateFormFieldsList();
				selectedFieldsTable.setValue(lastSelectedIds);
				// Re-apply selected fields filter
				if (selectedFieldsSearch != null) {
					filterSelectedFields(selectedFieldsSearch.getValue());
				}
			}
		});

		// Layout assembly
		HorizontalLayout fieldSelectionLayout = new HorizontalLayout();
		fieldSelectionLayout.setWidth("100%");
		fieldSelectionLayout.setSpacing(true);
		fieldSelectionLayout.addComponents(availableFieldsLayout, buttonLayout, selectedFieldsLayout);
		getContent().addComponent(fieldSelectionLayout, FIELDS_SELECTION_LOCATION);

		// FormType change handler
		formType.addValueChangeListener(event -> {
			FormType selectedFormType = (FormType) event.getProperty().getValue();
			if (selectedFormType != null) {
				FormType previousFormType = criteria.getFormType();
				criteria.setFormType(selectedFormType);
				// Ensure relevanceStatus is set to ACTIVE
				if (criteria.getRelevanceStatus() == null) {
					criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
				}
				// Only clear fields if formType actually changed (not on initial load)
				if (previousFormType != null && !previousFormType.equals(selectedFormType)) {
					// User changed formType - clear selected fields
					if (getValue() != null) {
						getValue().setFormFields(null);
					}
					selectedFieldsContainer.removeAllItems();
					firstPageLoad = false; // Prevent reloading on formType change
				}
				updateDataProvider();
			}
		});

		setRequired(true, FormBuilderDto.DISEASE, FormBuilderDto.FORM_TYPE);
	}

	private void moveItem(String itemId, boolean up) {
		int currentIndex = getCurrentItemIndex(itemId);
		if (up && currentIndex > 0) {
			swapItems(currentIndex, currentIndex - 1);
		} else if (!up && currentIndex < selectedFieldsContainer.size() - 1) {
			swapItems(currentIndex, currentIndex + 1);
		}
		reorderItems();
	}

	private void moveItemToTop(String itemId) {
		int currentIndex = getCurrentItemIndex(itemId);
		if (currentIndex > 0) {
			List<Object> itemIds = new ArrayList<>(selectedFieldsContainer.getItemIds());
			Object movedItem = itemIds.remove(currentIndex);
			itemIds.add(0, movedItem);

			IndexedContainer newContainer = new IndexedContainer();
			newContainer.addContainerProperty(PROPERTY_FIELD, FormFieldIndexDto.class, null);
			newContainer.addContainerProperty(PROPERTY_ORDER, Integer.class, null);
			newContainer.addContainerProperty(PROPERTY_NAME, String.class, null);

			for (Object id : itemIds) {
				Item oldItem = selectedFieldsContainer.getItem(id);
				Item newItem = newContainer.addItem(id);
				if (newItem != null && oldItem != null) {
					newItem.getItemProperty(PROPERTY_FIELD).setValue(oldItem.getItemProperty(PROPERTY_FIELD).getValue());
					newItem.getItemProperty(PROPERTY_ORDER).setValue(oldItem.getItemProperty(PROPERTY_ORDER).getValue());
					newItem.getItemProperty(PROPERTY_NAME).setValue(oldItem.getItemProperty(PROPERTY_NAME).getValue());
				}
			}

			selectedFieldsContainer = newContainer;
			selectedFieldsTable.setContainerDataSource(selectedFieldsContainer);
			selectedFieldsTable.setColumnHeader(PROPERTY_ORDER, "Order");
			selectedFieldsTable.setColumnHeader(PROPERTY_NAME, "Description");
			selectedFieldsTable.setVisibleColumns(PROPERTY_ORDER, PROPERTY_NAME);
			selectedFieldsTable.setColumnAlignment(PROPERTY_ORDER, Table.Align.CENTER);
			reorderItems();
			// Re-apply selected fields filter
			if (selectedFieldsSearch != null) {
				filterSelectedFields(selectedFieldsSearch.getValue());
			}
		}
	}

	private void swapItems(int index1, int index2) {
		List<Object> itemIds = new ArrayList<>(selectedFieldsContainer.getItemIds());
		Collections.swap(itemIds, index1, index2);

		IndexedContainer newContainer = new IndexedContainer();
		newContainer.addContainerProperty(PROPERTY_FIELD, FormFieldIndexDto.class, null);
		newContainer.addContainerProperty(PROPERTY_ORDER, Integer.class, null);
		newContainer.addContainerProperty(PROPERTY_NAME, String.class, null);

		for (Object id : itemIds) {
			Item oldItem = selectedFieldsContainer.getItem(id);
			Item newItem = newContainer.addItem(id);
			if (newItem != null && oldItem != null) {
				newItem.getItemProperty(PROPERTY_FIELD).setValue(oldItem.getItemProperty(PROPERTY_FIELD).getValue());
				newItem.getItemProperty(PROPERTY_ORDER).setValue(oldItem.getItemProperty(PROPERTY_ORDER).getValue());
				newItem.getItemProperty(PROPERTY_NAME).setValue(oldItem.getItemProperty(PROPERTY_NAME).getValue());
			}
		}

		selectedFieldsContainer = newContainer;
		selectedFieldsTable.setContainerDataSource(selectedFieldsContainer);
		selectedFieldsTable.setColumnHeader(PROPERTY_ORDER, "Order");
		selectedFieldsTable.setColumnHeader(PROPERTY_NAME, "Description");
		selectedFieldsTable.setVisibleColumns(PROPERTY_ORDER, PROPERTY_NAME);
		selectedFieldsTable.setColumnAlignment(PROPERTY_ORDER, Table.Align.CENTER);
		updateCounters();
		// Re-apply selected fields filter
		if (selectedFieldsSearch != null) {
			filterSelectedFields(selectedFieldsSearch.getValue());
		}
	}

	private int getCurrentItemIndex(String itemId) {
		int index = 0;
		for (Object id : selectedFieldsContainer.getItemIds()) {
			if (id.equals(itemId)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	private void reorderItems() {
		currentOrder = 0;
		getValue().setFormFields(new ArrayList<>());
		for (Object itemId : selectedFieldsContainer.getItemIds()) {
			Item item = selectedFieldsContainer.getItem(itemId);
			if (item != null) {
				item.getItemProperty(PROPERTY_ORDER).setValue(++currentOrder);
				FormFieldIndexDto field = (FormFieldIndexDto) item.getItemProperty(PROPERTY_FIELD).getValue();
				if (field != null) {
					FormFieldReferenceDto dto = new FormFieldReferenceDto();
					dto.setUuid(field.getUuid());
					dto.setCaption(field.getDescription());
					dto.setFieldName(field.getFieldName());
					dto.setDisplayOrder(currentOrder);
					getValue().getFormFields().add(dto);
				}
			}
		}
		updateCounters();
		// Re-apply selected fields filter after reordering
		if (selectedFieldsSearch != null) {
			filterSelectedFields(selectedFieldsSearch.getValue());
		}
	}

	private void updateFormFieldsList() {
		List<FormFieldReferenceDto> selectedDtos = new ArrayList<>();

		for (Object itemId : selectedFieldsContainer.getItemIds()) {
			Item item = selectedFieldsContainer.getItem(itemId);
			if (item != null) {
				FormFieldIndexDto field = (FormFieldIndexDto) item.getItemProperty(PROPERTY_FIELD).getValue();
				if (field != null) {
					FormFieldReferenceDto dto = new FormFieldReferenceDto();
					dto.setUuid(field.getUuid());
					dto.setCaption(field.getDescription());
					dto.setFieldName(field.getFieldName());
					dto.setDisplayOrder(getCurrentItemIndex(itemId.toString()) + 1);
					selectedDtos.add(dto);
				}
			}
		}

		getValue().setFormFields(selectedDtos);
		updateCounters();
	}

	public void updateDataProvider() {
		if (criteria.getFormType() == null) {
			return;
		}

		List<FormFieldIndexDto> formFieldIndexDtos = FacadeProvider.getFormFieldFacade()
			.getIndexList(criteria, null, null, List.of(new SortProperty("fieldName", true)));

		availableFields.removeAllItems();

		Set<String> selectedFieldUuids = getValue().getFormFields() != null
			? getValue().getFormFields().stream()
				.map(FormFieldReferenceDto::getUuid)
				.collect(Collectors.toSet())
			: Collections.emptySet();

		// Load existing selected fields BEFORE populating available fields
		// This ensures we have the field data to populate the selected table
		if (firstPageLoad && getValue() != null && getValue().getFormFields() != null && !getValue().getFormFields().isEmpty()) {
			selectedFieldsContainer.removeAllItems();
			// Sort formFields by displayOrder to maintain order
			List<FormFieldReferenceDto> sortedFields = new ArrayList<>(getValue().getFormFields());
			sortedFields.sort((a, b) -> {
				int orderA = a.getDisplayOrder() != null ? a.getDisplayOrder() : 0;
				int orderB = b.getDisplayOrder() != null ? b.getDisplayOrder() : 0;
				return Integer.compare(orderA, orderB);
			});
			
			for (FormFieldReferenceDto fieldRef : sortedFields) {
				// Find the field in the full list (before filtering)
				FormFieldIndexDto field = null;
				for (FormFieldIndexDto dto : formFieldIndexDtos) {
					if (dto.getUuid().equals(fieldRef.getUuid())) {
						field = dto;
						break;
					}
				}
				// If not found in the list, fetch from backend
				if (field == null) {
					de.symeda.sormas.api.infrastructure.fields.FormFieldsDto dto = FacadeProvider.getFormFieldFacade().getByUuid(fieldRef.getUuid());
					if (dto != null) {
						field = new FormFieldIndexDto(dto.getUuid(), dto.getFormType(), dto.getFieldName(), dto.getDescription(), dto.getActive());
					}
				}
				if (field != null) {
					Item item = selectedFieldsContainer.addItem(field.getUuid());
					if (item != null) {
						item.getItemProperty(PROPERTY_FIELD).setValue(field);
						// Backend uses 0-based index, convert to 1-based for display
						int displayOrder = fieldRef.getDisplayOrder() != null ? fieldRef.getDisplayOrder() + 1 : 1;
						item.getItemProperty(PROPERTY_ORDER).setValue(displayOrder);
						// Show description instead of fieldName
						String description = field.getDescription() != null && !field.getDescription().isEmpty() 
							? field.getDescription() 
							: field.getFieldName();
						item.getItemProperty(PROPERTY_NAME).setValue(description);
					}
				}
			}
			// Update the DTO to match what's loaded in the container
			updateFormFieldsList();
			firstPageLoad = false;
		}

		// Now populate available fields (excluding already selected ones)
		availableFieldsContainer = new IndexedContainer();
		availableFieldsContainer.addContainerProperty("field", FormFieldIndexDto.class, null);
		
		for (FormFieldIndexDto dto : formFieldIndexDtos) {
			if (!selectedFieldUuids.contains(dto.getUuid())) {
				Item containerItem = availableFieldsContainer.addItem(dto);
				if (containerItem != null) {
					containerItem.getItemProperty("field").setValue(dto);
				}
				availableFields.addItem(dto);
				// Show description instead of fieldName
				String caption = dto.getDescription() != null && !dto.getDescription().isEmpty() 
					? dto.getDescription() 
					: dto.getFieldName();
				availableFields.setItemCaption(dto, caption);
			}
		}
		
		updateCounters();
		// Apply any existing search filters
		if (availableFieldsSearch != null) {
			filterAvailableFields(availableFieldsSearch.getValue());
		}
		if (selectedFieldsSearch != null) {
			filterSelectedFields(selectedFieldsSearch.getValue());
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
	private void filterAvailableFields(String searchText) {
		if (availableFieldsContainer == null) {
			return;
		}
		
		availableFields.removeAllItems();
		
		String filterText = searchText != null ? searchText.toLowerCase().trim() : "";
		
		for (Object itemId : availableFieldsContainer.getItemIds()) {
			Item item = availableFieldsContainer.getItem(itemId);
			if (item != null) {
				FormFieldIndexDto field = (FormFieldIndexDto) item.getItemProperty("field").getValue();
				if (field != null) {
					String fieldName = field.getFieldName() != null ? field.getFieldName().toLowerCase() : "";
					String description = field.getDescription() != null ? field.getDescription().toLowerCase() : "";
					
					if (filterText.isEmpty() || fieldName.contains(filterText) || description.contains(filterText)) {
						availableFields.addItem(field);
						// Show description instead of fieldName
						String caption = field.getDescription() != null && !field.getDescription().isEmpty() 
							? field.getDescription() 
							: field.getFieldName();
						availableFields.setItemCaption(field, caption);
					}
				}
			}
		}
		
		updateCounters();
	}
	
	private void filterSelectedFields(String searchText) {
		if (selectedFieldsContainer == null) {
			return;
		}
		
		String filterText = searchText != null ? searchText.toLowerCase().trim() : "";
		
		// Create filtered container
		IndexedContainer filteredContainer = new IndexedContainer();
		filteredContainer.addContainerProperty(PROPERTY_FIELD, FormFieldIndexDto.class, null);
		filteredContainer.addContainerProperty(PROPERTY_ORDER, Integer.class, null);
		filteredContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
		
		for (Object itemId : selectedFieldsContainer.getItemIds()) {
			Item item = selectedFieldsContainer.getItem(itemId);
			if (item != null) {
				String description = (String) item.getItemProperty(PROPERTY_NAME).getValue();
				FormFieldIndexDto field = (FormFieldIndexDto) item.getItemProperty(PROPERTY_FIELD).getValue();
				
				if (description != null || field != null) {
					String descriptionLower = description != null ? description.toLowerCase() : "";
					String fieldName = field != null && field.getFieldName() != null ? field.getFieldName().toLowerCase() : "";
					
					if (filterText.isEmpty() || descriptionLower.contains(filterText) || fieldName.contains(filterText)) {
						Item newItem = filteredContainer.addItem(itemId);
						if (newItem != null) {
							newItem.getItemProperty(PROPERTY_FIELD).setValue(field);
							newItem.getItemProperty(PROPERTY_ORDER).setValue(item.getItemProperty(PROPERTY_ORDER).getValue());
							newItem.getItemProperty(PROPERTY_NAME).setValue(description);
						}
					}
				}
			}
		}
		
		selectedFieldsTable.setContainerDataSource(filteredContainer);
		selectedFieldsTable.setColumnHeader(PROPERTY_ORDER, "Order");
		selectedFieldsTable.setColumnHeader(PROPERTY_NAME, "Description");
		selectedFieldsTable.setVisibleColumns(PROPERTY_ORDER, PROPERTY_NAME);
		selectedFieldsTable.setColumnAlignment(PROPERTY_ORDER, Table.Align.CENTER);
		
		updateCounters();
	}
	
	private void updateCounters() {
		// Update available fields count
		int availableCount = availableFields.getItemIds() != null ? availableFields.getItemIds().size() : 0;
		if (availableFieldsContainer != null) {
			int totalAvailable = availableFieldsContainer.getItemIds() != null ? availableFieldsContainer.getItemIds().size() : 0;
			availableFieldsCountLabel.setValue("Available: " + availableCount + (availableCount != totalAvailable ? " (of " + totalAvailable + ")" : ""));
		} else {
			availableFieldsCountLabel.setValue("Available: " + availableCount);
		}
		
		// Update selected fields count
		int selectedCount = selectedFieldsContainer != null && selectedFieldsContainer.getItemIds() != null 
			? selectedFieldsContainer.getItemIds().size() : 0;
		selectedFieldsCountLabel.setValue("Selected: " + selectedCount);
	}

	@Override
	public void setValue(FormBuilderDto newFieldValue) {
		super.setValue(newFieldValue);
		if (newFieldValue != null) {
			// Reset firstPageLoad flag when loading an existing entity with formFields
			if (newFieldValue.getUuid() != null && newFieldValue.getFormFields() != null && !newFieldValue.getFormFields().isEmpty()) {
				firstPageLoad = true;
			} else if (newFieldValue.getUuid() == null) {
				// New entity - reset firstPageLoad
				firstPageLoad = true;
			}
			if (newFieldValue.getFormType() != null) {
				criteria.formType(newFieldValue.getFormType());
				// Ensure relevanceStatus is set to ACTIVE
				if (criteria.getRelevanceStatus() == null) {
					criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
				}
				// Delay updateDataProvider to ensure formFields are loaded
				updateDataProvider();
			}
		}
	}
}

